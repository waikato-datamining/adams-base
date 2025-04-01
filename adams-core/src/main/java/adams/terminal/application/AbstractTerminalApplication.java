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
 * AbstractTerminalApplication.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.terminal.application;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.management.ProcessUtils;
import adams.core.net.InternetHelper;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.core.scriptingengine.BackgroundScriptingEngineRegistry;
import adams.core.shutdownbuiltin.AbstractBuiltInShutdownHook;
import adams.db.AbstractDatabaseConnection;
import adams.db.AbstractIndexedTable;
import adams.db.DatabaseConnectionHandler;
import adams.env.Environment;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.gui.event.RemoteScriptingEngineUpdateListener;
import adams.scripting.RemoteScriptingEngineHandler;
import adams.scripting.engine.MultiScriptingEngine;
import adams.scripting.engine.RemoteScriptingEngine;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;

/**
 * Ancestor for terminal-based applications.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTerminalApplication
  extends AbstractOptionHandler
  implements DatabaseConnectionHandler, DatabaseConnectionChangeListener,
  RemoteScriptingEngineHandler {

  private static final long serialVersionUID = 2187425015130568365L;

  /** the global database connection. */
  protected AbstractDatabaseConnection m_DbConn;

  /** the title of the application. */
  protected String m_ApplicationTitle;

  /** the commandline of the remote scripting engine to use at startup time. */
  protected String m_RemoteScriptingEngineCmdLine;

  /** the remote command scripting engine. */
  protected RemoteScriptingEngine m_RemoteScriptingEngine;

  /** the listeners for changes to the remote scripting engine. */
  protected Set<RemoteScriptingEngineUpdateListener> m_RemoteScriptingEngineUpdateListeners;

  /**
   * Default constructor.
   */
  protected AbstractTerminalApplication() {
    super();
    initTerminal();
    finishTerminal();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_DbConn                = getDefaultDatabaseConnection();
    m_DbConn.addChangeListener(this);

    m_RemoteScriptingEngine                = null;
    m_RemoteScriptingEngineUpdateListeners = new HashSet<>();
  }

  /**
   * Initializes the terminal.
   */
  protected abstract void initTerminal();

  /**
   * Logs the message.
   *
   * @param msg		the message to log
   */
  public abstract void logMessage(String msg);

  /**
   * Logs the error.
   *
   * @param msg		the error message to log
   */
  public abstract void logError(String msg);

  /**
   * Logs the error.
   *
   * @param msg		the error message to log
   * @param t 		the exception
   */
  public abstract void logError(String msg, Throwable t);

  /**
   * Returns the log handler to use.
   *
   * @return		the handler
   */
  protected abstract Handler createLogHandler();

  /**
   * Finishes the initialization.
   */
  protected abstract void finishTerminal();

  /**
   * Starts the application.
   */
  public abstract void start();

  /**
   * Stops the application.
   */
  public abstract void stop();

  /**
   * Returns the default title of the application.
   *
   * @return		the default title
   */
  protected abstract String getDefaultApplicationTitle();

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

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
    m_DbConn = value;
  }

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  @Override
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    if (e.getType() == EventType.CONNECT)
      m_DbConn = e.getDatabaseConnection();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"remote-scripting-engine-cmdline", "remoteScriptingEngineCmdLine",
	"");

    m_OptionManager.add(
	"title", "applicationTitle",
	getDefaultApplicationTitle());
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    getDatabaseConnection().setLoggingLevel(value);
  }

  /**
   * Sets the commandline of the remote scripting engine to execute at startup time.
   *
   * @param value	the commandline, use empty string if not to use one
   */
  public void setRemoteScriptingEngineCmdLine(String value) {
    m_RemoteScriptingEngineCmdLine = value;
    reset();
  }

  /**
   * Returns the commandline of the remote scripting engine to execute at startup time.
   *
   * @return		the commandline, empty string it not to use one
   */
  public String getRemoteScriptingEngineCmdLine() {
    return m_RemoteScriptingEngineCmdLine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteScriptingEngineCmdLineTipText() {
    return
        "The command-line of the remote scripting engine to execute at startup "
	  + "time; use empty string for disable scripting.";
  }

  /**
   * Adds the scripting engine to execute. Doesn't stop any running engines.
   *
   * @param value	the engine to add
   */
  public void addRemoteScriptingEngine(RemoteScriptingEngine value) {
    MultiScriptingEngine multi;

    value.setRemoteScriptingEngineHandler(this);
    if (!value.isRunning())
      new Thread(() -> value.execute()).start();

    if (m_RemoteScriptingEngine == null) {
      m_RemoteScriptingEngine = value;
    }
    else {
      if (m_RemoteScriptingEngine instanceof MultiScriptingEngine) {
	((MultiScriptingEngine) m_RemoteScriptingEngine).addEngine(value);
      }
      else {
	multi = new MultiScriptingEngine();
	new Thread(() -> multi.execute()).start();
	multi.addEngine(m_RemoteScriptingEngine);
	multi.addEngine(value);
	m_RemoteScriptingEngine = multi;
      }
    }
    notifyRemoteScriptingEngineUpdateListeners(new RemoteScriptingEngineUpdateEvent(this));
  }

  /**
   * Removes the scripting engine (and stops it). Doesn't stop any running engines.
   *
   * @param value	the engine to remove
   */
  public void removeRemoteScriptingEngine(RemoteScriptingEngine value) {
    MultiScriptingEngine	multi;

    if (m_RemoteScriptingEngine == null)
      return;

    if (m_RemoteScriptingEngine instanceof MultiScriptingEngine) {
      multi = (MultiScriptingEngine) m_RemoteScriptingEngine;
      multi.removeEngine(value);
    }
    else {
      if (value.toCommandLine().equals(m_RemoteScriptingEngine.toCommandLine()))
	setRemoteScriptingEngine(null);
    }
    notifyRemoteScriptingEngineUpdateListeners(new RemoteScriptingEngineUpdateEvent(this));
  }

  /**
   * Sets the scripting engine to execute. Any running engine is stopped first.
   *
   * @param value	the engine to use, null to turn off scripting
   */
  public void setRemoteScriptingEngine(RemoteScriptingEngine value) {
    if (m_RemoteScriptingEngine != null) {
      getLogger().info("Stop listening for remote commands: " + m_RemoteScriptingEngine.getClass().getName());
      m_RemoteScriptingEngine.stopExecution();
      m_RemoteScriptingEngine.setRemoteScriptingEngineHandler(null);
    }
    m_RemoteScriptingEngine = value;
    if (m_RemoteScriptingEngine != null) {
      m_RemoteScriptingEngine.setRemoteScriptingEngineHandler(this);
      getLogger().info("Start listening for remote commands: " + m_RemoteScriptingEngine.getClass().getName());
      new Thread(() -> m_RemoteScriptingEngine.execute()).start();
    }
    notifyRemoteScriptingEngineUpdateListeners(new RemoteScriptingEngineUpdateEvent(this));
  }

  /**
   * Returns the current scripting engine if any.
   *
   * @return		the engine in use, null if none running
   */
  public RemoteScriptingEngine getRemoteScriptingEngine() {
    return m_RemoteScriptingEngine;
  }

  /**
   * Adds the listener for remote scripting engine changes.
   *
   * @param l		the listener
   */
  public void addRemoteScriptingEngineUpdateListener(RemoteScriptingEngineUpdateListener l) {
    m_RemoteScriptingEngineUpdateListeners.add(l);
  }

  /**
   * Removes the listener for remote scripting engine changes.
   *
   * @param l		the listener
   */
  public void removeRemoteScriptingEngineUpdateListener(RemoteScriptingEngineUpdateListener l) {
    m_RemoteScriptingEngineUpdateListeners.remove(l);
  }

  /**
   * Notifies all listeners of remote scripting engine changes.
   *
   * @param e		the event to send
   */
  public void notifyRemoteScriptingEngineUpdateListeners(RemoteScriptingEngineUpdateEvent e) {
    for (RemoteScriptingEngineUpdateListener l: m_RemoteScriptingEngineUpdateListeners)
      l.remoteScriptingEngineUpdated(e);
  }

  /**
   * Returns the currently set application title.
   *
   * @return		the current title
   */
  public String getApplicationTitle() {
    return m_ApplicationTitle;
  }

  /**
   * Sets the application title to use.
   *
   * @param value	the title to use
   */
  public void setApplicationTitle(String value) {
    m_ApplicationTitle = value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String applicationTitleTipText() {
    return "The title for the application.";
  }

  /**
   * Sets the title to use.
   *
   * @param value	the title
   */
  protected abstract void setTitle(String value);

  /**
   * creates and displays the title.
   *
   * @param title 	the additional part of the title
   */
  public void createTitle(String title) {
    String	newTitle;
    String	name;

    newTitle = getApplicationTitle();
    name     = InternetHelper.getLocalHostName();
    if (name != null)
      newTitle += "@" + name;

    if (!title.isEmpty()) {
      if (title.length() > 50)
	newTitle += " - " + title.substring(0, 50) + "...";
      else
	newTitle += " - " + title;
    }

    setTitle(newTitle);
  }

  /**
   * Instantiates the application with the given options.
   *
   * @param classname	the classname of the application to instantiate
   * @param options	the options for the application
   * @return		the instantiated application or null if an error occurred
   */
  public static AbstractTerminalApplication forName(String classname, String[] options) {
    AbstractTerminalApplication	result;

    try {
      result = (AbstractTerminalApplication) OptionUtils.forName(AbstractTerminalApplication.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the application from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			application to instantiate
   * @return		the instantiated application
   * 			or null if an error occurred
   */
  public static AbstractTerminalApplication forCommandLine(String cmdline) {
    return (AbstractTerminalApplication) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Runs the application from the commandline. Calling code needs to perform
   * a System.exit(0).
   *
   * @param env		the environment class to use
   * @param app		the application frame class
   * @param options	the commandline options
   */
  public static void runApplication(Class env, Class app, String[] options) {
    AbstractTerminalApplication application;

    Environment.setEnvironmentClass(env);
    LoggingHelper.useHandlerFromOptions(options);

    try {
      if (OptionUtils.helpRequested(options)) {
	System.out.println("Help requested...\n");
	application = forName(app.getName(), new String[0]);
	System.out.print("\n" + OptionUtils.list(application));
	LoggingHelper.outputHandlerOption();
	BackgroundScriptingEngineRegistry.getSingleton().stopAllEngines();
      }
      else {
	AbstractBuiltInShutdownHook.installAll();
	application = forName(app.getName(), options);
	Environment.getInstance().setApplicationTerminal(application);
	if (application.getDatabaseConnection().isConnected())
	  AbstractIndexedTable.initTables(application.getDatabaseConnection());
	application.getLogger().info("PID: " + ProcessUtils.getVirtualMachinePID());
	application.start();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
