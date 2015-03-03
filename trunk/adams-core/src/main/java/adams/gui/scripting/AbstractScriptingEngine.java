/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * AbstractScriptingEngine.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import adams.core.DateUtils;
import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingObject;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.env.Environment;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;
import adams.gui.core.BasePanel;
import adams.gui.event.ScriptingInfoEvent;
import adams.gui.event.ScriptingInfoListener;

/**
 * Processes scripting commands.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see CommandProcessor
 */
public abstract class AbstractScriptingEngine
  extends LoggingObject
  implements DatabaseConnectionHandler, DatabaseConnectionChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = -532845009254256601L;

  /** the name of the props file. */
  public final static String FILENAME = "ScriptingEngine.props";

  /** the property for the directory containing the scripts. */
  public final static String SCRIPT_DIRECTORY = "ScriptDirectory";

  /** the property for the scripting log file. */
  public final static String LOG_FILE = "ScriptingLogFile";

  /** the property for the AbstractCommandProcessor-derived class. */
  public final static String COMMAND_PROCESSOR = "CommandProcessorClass";

  /** the property for the alternative AbstractCommandProcessor-derived class. */
  public final static String ALTERNATIVE_COMMAND_PROCESSOR = "AlternativeCommandProcessorClass";

  /** the start of a comment. */
  public final static String COMMENT = "#";

  /** the database connection to use. */
  protected AbstractDatabaseConnection m_DbConn;

  /** the history of all (successfully run) commands since instantiation. */
  protected List<String> m_History;

  /** whether the commands are currently being recorded. */
  protected boolean m_Recording;

  /** the currently recorded commands. */
  protected List<String> m_Recorded;

  /** the listeners for changes in commands being run, etc. */
  protected HashSet<ScriptingInfoListener> m_ScriptingInfoListeners;

  /** the last last error that was encountered. */
  protected String m_LastError;

  /** whether logging is enabled. */
  protected boolean m_LoggingEnabled;

  /** the command processor for executing the commands. */
  protected AbstractCommandProcessor m_Processor;

  /** the thread to use for executing the commands. */
  protected ScriptingEngineThread m_ProcessingThread;

  /**
   * Initializes the engine.
   */
  protected AbstractScriptingEngine() {
    super();

    m_History                = new ArrayList<String>();
    m_Recording              = false;
    m_Recorded               = new ArrayList<String>();
    m_ScriptingInfoListeners = new HashSet<ScriptingInfoListener>();
    m_LastError              = null;
    m_Processor              = null;
    m_ProcessingThread       = null;
    m_DbConn                 = getDefaultDatabaseConnection();
    m_DbConn.addChangeListener(this);

    getProcessingThread().start();

    updatePrefix();
  }

  /**
   * Updates the prefix of the console object output streams.
   */
  protected void updatePrefix() {
    String	prefix;

    prefix   = getClass().getName() + "(" + getDatabaseConnection().toStringShort() + "/" + getDatabaseConnection().hashCode() + ")";
    m_Logger = LoggingHelper.getLogger(prefix);
    m_Logger.setLevel(m_LoggingEnabled ? Level.INFO : Level.WARNING);
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Returns the command processor to use. Initializes it if necessary.
   *
   * @return		the processor
   */
  public synchronized AbstractCommandProcessor getProcessor() {
    String	classname;
    Class	cls;
    Constructor	constr;

    if (m_Processor == null) {
      try {
	classname = getProperties().getPath(ALTERNATIVE_COMMAND_PROCESSOR, "").trim();
	if (classname.length() == 0)
	  classname = getProperties().getPath(COMMAND_PROCESSOR, "noCommandProcessorClassDefined");
	cls         = Class.forName(classname);
	constr      = cls.getConstructor(new Class[]{AbstractScriptingEngine.class});
	m_Processor = (AbstractCommandProcessor) constr.newInstance(new Object[]{this});
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate processor", e);
	m_Processor = null;
      }
    }

    return m_Processor;
  }

  /**
   * Returns the thread for processing the scripting commands.
   *
   * @return		the thread
   */
  public synchronized ScriptingEngineThread getProcessingThread() {
    if (m_ProcessingThread == null)
      m_ProcessingThread = new ScriptingEngineThread(this);

    return m_ProcessingThread;
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  @Override
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DbConn;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  @Override
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DbConn.removeChangeListener(this);
    if (value != null)
      m_DbConn = value;
    else
      m_DbConn = DatabaseConnection.getSingleton();
    m_DbConn.addChangeListener(this);

    // reset recording!
    if (isRecording())
      startRecording();

    updatePrefix();
  }

  /**
   * Returns whether there are no commands in the queue currently.
   *
   * @return		true if no commands wait to be processed
   */
  public boolean isEmpty() {
    return getProcessingThread().isEmpty();
  }

  /**
   * Returns whether a command is currently being processed.
   *
   * @return		true if a command is currently being processed
   */
  public boolean isProcessing() {
    return getProcessingThread().isProcessing();
  }

  /**
   * Stops the execution of the script (but will still finish the current step).
   */
  public void stop() {
    getProcessingThread().clear();
  }

  /**
   * Stops the scripting engine (incl. the processing thread).
   *
   * @see		#m_ProcessingThread
   */
  public synchronized void stopEngine() {
    if (!isEmpty() || isProcessing())
      stop();

    getProcessingThread().stopExecution();
    DatabaseConnection.getSingleton().removeChangeListener(this);
  }

  /**
   * Adds the command to the history.
   *
   * @param cmd		the command to add
   */
  public void addToHistory(String cmd) {
    m_History.add(cmd);
    if (m_Recording)
      m_Recorded.add(cmd);
  }

  /**
   * Returns the complete history of commands.
   *
   * @return		the history of commands
   */
  public List<String> getCommandHistory() {
    return m_History;
  }

  /**
   * Starts the recording of commands.
   */
  public void startRecording() {
    m_Recording = true;
    m_Recorded.clear();
    m_Recorded.add(COMMENT + " Recording started at " + DateUtils.getTimestampFormatter().format(new Date()));
    m_Recorded.add("");

    getLogger().info("Recoding started");
  }

  /**
   * Stops the recording of commands.
   */
  public void stopRecording() {
    m_Recording = false;
    m_Recorded.add("");
    m_Recorded.add(COMMENT + " Recording stopped at " + DateUtils.getTimestampFormatter().format(new Date()));

    getLogger().info("Recoding stopped");
  }

  /**
   * Whether commands are currently being recorded.
   *
   * @return		true if the commands are currently being recorded
   */
  public boolean isRecording() {
    return m_Recording;
  }

  /**
   * Returns whether there are any commands in the recording buffer.
   *
   * @return		true if commands were recorded
   */
  public boolean hasRecording() {
    return (m_Recorded.size() > 0);
  }

  /**
   * Returns the last recorded commands.
   *
   * @return		the commands
   */
  public List<String> getRecordedCommands() {
    return m_Recorded;
  }

  /**
   * Removes all commands from the queue.
   */
  public void clear() {
    getProcessingThread().clear();

    m_History.add("");
    m_History.add(COMMENT + " command queue emptied");
    m_History.add("");

    getLogger().info("Cleared");
  }

  /**
   * Adds the given command to the queue.
   *
   * @param command	the command to add
   */
  public synchronized void add(ScriptingCommand command) {
    getProcessingThread().add(command);

    getLogger().fine("add: " + command);
  }

  /**
   * Adds the given command to the queue.
   *
   * @param panel	the affected base panel
   * @param cmd		the command to execute
   * @param code	the code to execute after the command finishes
   */
  public synchronized void add(BasePanel panel, String cmd, ScriptingCommandCode code) {
    add(new ScriptingCommand(panel, cmd, code));
  }

  /**
   * Adds the given command to the queue.
   *
   * @param panel	the affected base panel
   * @param command	the command to add
   */
  public synchronized void add(BasePanel panel, String command) {
    if (check(command))
      add(new ScriptingCommand(panel, command));
  }

  /**
   * Adds the given commands to the queue.
   *
   * @param panel	the affected base panel
   * @param commands	the commands to add
   */
  public synchronized void add(BasePanel panel, String[] commands) {
    int		i;

    commands = filter(commands);
    for (i = 0; i < commands.length; i++)
      add(new ScriptingCommand(panel, commands[i]));
  }

  /**
   * Adds the given commands to the queue.
   *
   * @param panel	the affected base panel
   * @param commands	the commands to add
   */
  public synchronized void add(BasePanel panel, List<String> commands) {
    int		i;

    commands = filter(commands);
    for (i = 0; i < commands.size(); i++)
      add(new ScriptingCommand(panel, commands.get(i)));
  }

  /**
   * Adds the commands from the given file to the queue.
   *
   * @param panel	the affected base panel
   * @param commandFile	the file with the commands to add
   */
  public synchronized void add(BasePanel panel, File commandFile) {
    add(panel, load(commandFile));
  }

  /**
   * Adds the scripting info listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addScriptingInfoListener(ScriptingInfoListener l) {
    m_ScriptingInfoListeners.add(l);
  }

  /**
   * Removes the scripting info listener from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeScriptingInfoListener(ScriptingInfoListener l) {
    m_ScriptingInfoListeners.remove(l);
  }

  /**
   * Notifies all scripting info listeners.
   *
   * @param cmd		the command, if any
   */
  public void notifyScriptingInfoListeners(String cmd) {
    Iterator<ScriptingInfoListener>	iter;
    ScriptingInfoEvent			event;

    event = new ScriptingInfoEvent(this, cmd);
    iter  = m_ScriptingInfoListeners.iterator();
    while (iter.hasNext())
      iter.next().scriptingInfo(event);
  }

  /**
   * Returns whether there was an error during the last run.
   *
   * @return		true if an error was encountered during the last run
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Sets the last error.
   *
   * @param value	the error
   */
  public void setLastError(String value) {
    m_LastError = value;

    getLogger().info("Last error: " + value);
  }

  /**
   * Returns the last error that was encountered, or null if no error occurred.
   *
   * @return		the error string or null if no error occurred
   */
  public String getLastError() {
    return m_LastError;
  }

  /**
   * Checks whether the given string represents a command and not a comment
   * or empty line.
   *
   * @param cmd		the command to check
   * @return		true if proper command
   */
  public static boolean check(String cmd) {
    boolean	result;

    result = true;

    if (cmd.trim().startsWith(COMMENT))
      result = false;
    else if (cmd.trim().length() == 0)
      result = false;

    return result;
  }

  /**
   * Filters all comments and empty lines.
   *
   * @param cmds	the commands to process
   * @return		the "cleansed" commands
   */
  public static String[] filter(String[] cmds) {
    List<String>	list;
    int			i;

    list = new ArrayList<String>();
    for (i = 0; i < cmds.length; i++)
      list.add(cmds[i]);

    list = filter(list);

    return list.toArray(new String[list.size()]);
  }

  /**
   * Filters all comments and empty lines.
   *
   * @param cmds	the commands to process
   * @return		the "cleansed" commands
   */
  public static List<String> filter(List<String> cmds) {
    List<String>	result;
    int			i;

    result = new ArrayList<String>();

    for (i = 0; i < cmds.size(); i++) {
      if (check(cmds.get(i)))
	result.add(cmds.get(i));
    }

    return result;
  }

  /**
   * Returns the content of the given file, null in case of an error.
   *
   * @param file	the file to load
   * @return		the content/lines of the file
   */
  public static List<String> load(File file) {
    return FileUtils.loadFromFile(file);
  }

  /**
   * Saves the script content in the given file.
   *
   * @param script	the script to save
   * @param file	the file to save the content to
   * @return		true if successfully saved
   */
  public static boolean save(String[] script, File file) {
    return FileUtils.saveToFile(script, file);
  }

  /**
   * Saves the script content in the given file.
   *
   * @param script	the script to save
   * @param file	the file to save the content to
   * @return		true if successfully saved
   */
  public static boolean save(List<String> script, File file) {
    return FileUtils.saveToFile(script, file);
  }

  /**
   * Returns the properties key to use for retrieving the properties.
   *
   * @return		the key
   */
  protected abstract String getDefinitionKey();

  /**
   * Reads the properties.
   *
   * @return		the properties
   * @see		#getDefinitionKey()
   */
  protected synchronized Properties readProperties() {
    return Environment.getInstance().read(getDefinitionKey());
  }

  /**
   * Provides access to the properties object.
   *
   * @return		the properties
   */
  protected abstract Properties getProperties();

  /**
   * Returns the scripts home directory. $HOME/.gcms/scripts
   *
   * @return		the scripts directory
   */
  public String getScriptsHome() {
    return getProperties().getPath(SCRIPT_DIRECTORY, "%p/scripts");
  }

  /**
   * Returns the available scripts in the scripts home directory.
   *
   * @return		a list of available scripts
   * @see		#getScriptsHome()
   */
  public List<String> getAvailableScripts() {
    return getAvailableScripts(getScriptsHome());
  }

  /**
   * Returns the available scripts in the scripts home directory.
   *
   * @param home	the scripts home
   * @return		a list of available scripts
   * @see		#getScriptsHome()
   */
  protected static List<String> getAvailableScripts(String home) {
    List<String>	result;
    File		file;
    File[]		files;
    int			i;

    result = new ArrayList<String>();

    file = new File(home);
    if (file.exists()) {
      files = file.listFiles();
      for (i = 0; i < files.length; i++) {
	// skip directories (e.g., source control directories like "CSV", ".svn")
	if (files[i].isDirectory())
	  continue;
	// no backup files
	if (files[i].getName().endsWith("~") || files[i].getName().endsWith(".bak"))
	  continue;
	// no hidden files
	if (files[i].getName().startsWith("."))
	  continue;

	result.add(files[i].getAbsolutePath());
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  @Override
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    if (e.getType() == EventType.CONNECT)
      setDatabaseConnection(e.getDatabaseConnection());
  }
}
