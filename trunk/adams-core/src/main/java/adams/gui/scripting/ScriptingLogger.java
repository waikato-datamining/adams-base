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
 * ScriptingLogger.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;


import java.util.HashSet;
import java.util.Iterator;

import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.env.ScriptingLoggerDefinition;
import adams.gui.event.ScriptingEvent;
import adams.gui.event.ScriptingListener;

/**
 * A class that logs all scripting commands.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingLogger {

  /** the name of the props file. */
  public final static String FILENAME = "ScriptingLogger.props";

  /** the properties. */
  protected Properties m_Properties;

  /** the file to log the commands to. */
  protected PlaceholderFile m_LogFile;

  /** the logging format. */
  protected String m_LogFormat;

  /** the listeners. */
  protected HashSet<ScriptingListener> m_ScriptingListeners;

  /** the singleton. */
  protected static ScriptingLogger m_Singleton;

  /**
   * Initializes the logger.
   */
  private ScriptingLogger() {
    super();

    m_ScriptingListeners = new HashSet<ScriptingListener>();

    // load properties
    m_Properties = Environment.getInstance().read(ScriptingLoggerDefinition.KEY);

    // setup logging
    if (!m_Properties.getBoolean("LogToFile", false)) {
      m_LogFile = null;
    }
    else {
      m_LogFile   = new PlaceholderFile(m_Properties.getPath("LogFile", "%t/gcms_cmds.log"));
      m_LogFormat = m_Properties.getProperty("LogFormat", "SOURCE\tBASEPANEL\tSUCCESS\tCMD\tERROR");
    }
  }

  /**
   * Logs the given command.
   *
   * @param source	the source that logged this command
   * @param cmd		the command that was run
   * @param success	whether the command was successful
   */
  public synchronized void log(Object source, ScriptingCommand cmd, boolean success) {
    log(source, cmd, success, null);
  }

  /**
   * Logs the given command.
   *
   * @param source	the source that logged this command
   * @param cmd		the command that was run
   * @param success	whether the command was successful
   * @param error	an optional error message, null if not available
   */
  public synchronized void log(Object source, ScriptingCommand cmd, boolean success, String error) {
    String	line;

    if (m_LogFile != null) {
      line = m_LogFormat;
      line = line.replaceAll("SOURCE", source.getClass().getName() + "/" + source.hashCode());
      if (cmd.getBasePanel() == null)
	line = line.replaceAll("BASEPANEL", "-no base panel-");
      else
	line = line.replaceAll("BASEPANEL", cmd.getBasePanel().getName());
      line = line.replaceAll("SUCCESS", "" + success);
      line = line.replaceAll("CMD", cmd.getCommand());
      if (error == null)
	line = line.replaceAll("ERROR", "-no error msg-");
      else
	line = line.replaceAll("ERROR", error);

      FileUtils.writeToFile(m_LogFile.getAbsolutePath(), line);
    }

    notifyScriptingListeners(new ScriptingEvent(source, cmd, success, error));
  }

  /**
   * Adds the given listener to its internal list of listeners.
   *
   * @param l		the listener to add
   */
  public void addScriptingListener(ScriptingListener l) {
    m_ScriptingListeners.add(l);
  }

  /**
   * Removes the given listener from its internal list of listeners.
   *
   * @param l		the listener to remove
   */
  public void removeScriptingListener(ScriptingListener l) {
    m_ScriptingListeners.remove(l);
  }

  /**
   * Notifies all the listeners about the specified event.
   *
   * @param e		the event
   */
  protected synchronized void notifyScriptingListeners(ScriptingEvent e) {
    Iterator<ScriptingListener> 	iter;

    iter = m_ScriptingListeners.iterator();
    while (iter.hasNext())
      iter.next().scriptingCommandExecuted(e);
  }

  /**
   * Returns the singleton instance of the logger.
   *
   * @return		the logger instance
   */
  public static synchronized ScriptingLogger getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ScriptingLogger();

    return m_Singleton;
  }
}
