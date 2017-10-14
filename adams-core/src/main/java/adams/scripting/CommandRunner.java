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

/**
 * CommandRunner.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting;

import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.scripting.command.RemoteCommand;
import adams.scripting.connection.Connection;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.processor.RemoteCommandProcessor;
import adams.scripting.processor.RemoteCommandProcessorHandler;

/**
 <!-- globalinfo-start -->
 * Executes scripting commands.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-start-local-engine &lt;boolean&gt; (property: startLocalEngine)
 * &nbsp;&nbsp;&nbsp;If enabled, starts the local scripting engine.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-engine &lt;adams.scripting.engine.RemoteScriptingEngine&gt; (property: engine)
 * &nbsp;&nbsp;&nbsp;The remote scripting engine to use for processing, e.g., responses.
 * &nbsp;&nbsp;&nbsp;default: adams.scripting.engine.DefaultScriptingEngine -permission-handler adams.scripting.permissionhandler.AllowAll -request-handler adams.scripting.requesthandler.LoggingHandler -response-handler adams.scripting.responsehandler.LoggingHandler
 * </pre>
 * 
 * <pre>-connection &lt;adams.scripting.connection.Connection&gt; (property: connection)
 * &nbsp;&nbsp;&nbsp;The connection to use for sending the commands to the remote host.
 * &nbsp;&nbsp;&nbsp;default: adams.scripting.connection.DefaultConnection
 * </pre>
 * 
 * <pre>-command &lt;adams.scripting.command.RemoteCommand&gt; [-command ...] (property: commands)
 * &nbsp;&nbsp;&nbsp;The remote commands to execute one after the other.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-time-out &lt;int&gt; (property: timeOut)
 * &nbsp;&nbsp;&nbsp;The grace period in msec to wait for commands to finish before shutting 
 * &nbsp;&nbsp;&nbsp;down.
 * &nbsp;&nbsp;&nbsp;default: 10000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CommandRunner
  extends AbstractOptionHandler
  implements RemoteCommandProcessorHandler {

  private static final long serialVersionUID = -347088462601591488L;

  /** whether to start the local scripting engine. */
  protected boolean m_StartLocalEngine;

  /** the scripting engine to use. */
  protected RemoteScriptingEngine m_Engine;

  /** the connection to use. */
  protected Connection m_Connection;

  /** the command processor. */
  protected RemoteCommandProcessor m_CommandProcessor;

  /** the commands to execute. */
  protected RemoteCommand[] m_Commands;

  /** the timeout in msec to wait for commands to finish. */
  protected int m_TimeOut;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes scripting commands.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "start-local-engine", "startLocalEngine",
      false);

    m_OptionManager.add(
      "engine", "engine",
      ScriptingHelper.getSingleton().getDefaultEngine());

    m_OptionManager.add(
      "connection", "connection",
      ScriptingHelper.getSingleton().getDefaultConnection());

    m_OptionManager.add(
      "command-processor", "commandProcessor",
      ScriptingHelper.getSingleton().getDefaultProcessor());

    m_OptionManager.add(
      "command", "commands",
      new RemoteCommand[0]);

    m_OptionManager.add(
      "time-out", "timeOut",
      10000, 1, null);
  }

  /**
   * Sets whether to start a local scripting engine.
   *
   * @param value	true if to start
   */
  public void setStartLocalEngine(boolean value) {
    m_StartLocalEngine = value;
    reset();
  }

  /**
   * Returns whether to start a local scripting engine.
   *
   * @return		true if to start
   */
  public boolean getStartLocalEngine() {
    return m_StartLocalEngine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startLocalEngineTipText() {
    return "If enabled, starts the local scripting engine.";
  }

  /**
   * Sets the scripting engine to use.
   *
   * @param value	the engine
   */
  public void setEngine(RemoteScriptingEngine value) {
    m_Engine = value;
    reset();
  }

  /**
   * Returns the scripting engine in use.
   *
   * @return		the engine
   */
  public RemoteScriptingEngine getEngine() {
    return m_Engine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String engineTipText() {
    return "The remote scripting engine to use for processing, e.g., responses.";
  }

  /**
   * Sets the connection to use.
   *
   * @param value	the connection
   */
  public void setConnection(Connection value) {
    m_Connection = value;
    reset();
  }

  /**
   * Returns the connection in use.
   *
   * @return		the connection
   */
  public Connection getConnection() {
    return m_Connection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String connectionTipText() {
    return "The connection to use for sending the commands to the remote host.";
  }

  /**
   * Sets the command processor to use.
   *
   * @param value	the processor
   */
  public void setCommandProcessor(RemoteCommandProcessor value) {
    m_CommandProcessor = value;
    reset();
  }

  /**
   * Returns the command processor in use.
   *
   * @return		the processor
   */
  public RemoteCommandProcessor getCommandProcessor() {
    return m_CommandProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String commandProcessorTipText() {
    return "The processor for formatting/parsing the commands.";
  }

  /**
   * Sets the commands to execute.
   *
   * @param value	the commands
   */
  public void setCommands(RemoteCommand[] value) {
    m_Commands = value;
    reset();
  }

  /**
   * Returns the commands to execute.
   *
   * @return		the commands
   */
  public RemoteCommand[] getCommands() {
    return m_Commands;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandsTipText() {
    return "The remote commands to execute one after the other.";
  }

  /**
   * Sets the grace period to wait for commands to finish before shutting
   * down.
   *
   * @param value	the timeout in msec
   */
  public void setTimeOut(int value) {
    m_TimeOut = value;
    reset();
  }

  /**
   * Returns the grace period to wait for commands to finish before shutting
   * down.
   *
   * @return		the timeout in msec
   */
  public int getTimeOut() {
    return m_TimeOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timeOutTipText() {
    return "The grace period in msec to wait for commands to finish before shutting down.";
  }

  /**
   * Executes the commands.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String		result;
    int			i;
    long		end;

    result = null;

    // assemble commands
    if (isLoggingEnabled())
      getLogger().info("# commands: " + m_Commands.length);

    // start scripting engine
    if (m_StartLocalEngine) {
      new Thread(() -> m_Engine.execute()).start();
    }

    // send commands
    for (i = 0; i < m_Commands.length; i++) {
      if (isLoggingEnabled())
	getLogger().info("Command #" + (i+1) + ": " + m_Commands[i].toCommandLine());
      result = m_Connection.sendRequest(m_Commands[i], m_CommandProcessor);
      if (result != null) {
	getLogger().severe("Command #" + (i + 1) + " generated following error:\n" + result);
	break;
      }
    }

    if (m_StartLocalEngine) {
      if (result != null) {
	getLogger().severe("Shutting down scripting engine...");
	m_Engine.stopExecution();
      }
      else {
	end = System.currentTimeMillis() + m_TimeOut;
	while (!m_Engine.isStopped() && (System.currentTimeMillis() < end))
	  Utils.wait(this, 100, 100);
	if (!m_Engine.isStopped()) {
	  result = "Commands didn't finish within " + m_TimeOut + " msec? Forcing shutdown now...";
	  m_Engine.stopExecution();
	}
      }
    }

    return result;
  }

  /**
   * Instantiates the flow with the given options.
   *
   * @param classname	the classname of the flow to instantiate
   * @param options	the options for the flow
   * @return		the instantiated flow or null if an error occurred
   */
  public static CommandRunner forName(String classname, String[] options) {
    CommandRunner	result;

    try {
      result = (CommandRunner) OptionUtils.forName(CommandRunner.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the flow from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			flow to instantiate
   * @return		the instantiated flow
   * 			or null if an error occurred
   */
  public static CommandRunner forCommandLine(String cmdline) {
    return (CommandRunner) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Runs the flow from commandline.
   *
   * @param env		the environment class to use
   * @param runner	the flow class to execute
   * @param args	the commandline arguments, use -help to display all
   */
  public static void runCommands(Class env, Class runner, String[] args) {
    CommandRunner 	cmdInst;
    String		result;

    Environment.setEnvironmentClass(env);
    Environment.setHome(OptionUtils.getOption(args, "-home"));
    LoggingHelper.useHandlerFromOptions(args);

    try {
      if (OptionUtils.helpRequested(args)) {
	System.out.println("Help requested...\n");
	cmdInst = forName(runner.getName(), new String[0]);
	System.out.print("\n" + OptionUtils.list(cmdInst));
	LoggingHelper.outputHandlerOption();
      }
      else {
	cmdInst = forName(runner.getName(), args);
	ArrayConsumer.setOptions(cmdInst, args);
	result = cmdInst.execute();
	if (result == null) {
	  System.out.println("\nFinished execution!");
	}
	else {
	  System.err.println("Execution failed:\n" + result);
	  System.exit(1);
	}
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Runs the flow with the given options.
   *
   * @param args	the options to use
   */
  public static void main(String[] args) {
    runCommands(Environment.class, CommandRunner.class, args);
  }
}
