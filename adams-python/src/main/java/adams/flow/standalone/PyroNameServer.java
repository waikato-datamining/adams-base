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
 * PyroNameServer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.flow.core.ActorUtils;
import adams.flow.core.RunnableWithLogging;
import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;
import net.razorvine.pyro.Config;
import net.razorvine.pyro.NameServerProxy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Defines the Pyro nameserver to use.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: PyroNameServer
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-server &lt;adams.core.base.BaseHostname&gt; (property: server)
 * &nbsp;&nbsp;&nbsp;The hostname and port of the nameserver to connect to (and&#47;or launch).
 * &nbsp;&nbsp;&nbsp;default: localhost:9090
 * </pre>
 *
 * <pre>-mode &lt;CONNECT|LAUNCH_AND_CONNECT&gt; (property: mode)
 * &nbsp;&nbsp;&nbsp;How to operate, eg simply using or also launching.
 * &nbsp;&nbsp;&nbsp;default: CONNECT
 * </pre>
 *
 * <pre>-launch-mode-options &lt;java.lang.String&gt; (property: launchModeOptions)
 * &nbsp;&nbsp;&nbsp;The additional options to use for the nameserver (aside hostname and port
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-launch-wait &lt;int&gt; (property: launchWait)
 * &nbsp;&nbsp;&nbsp;The number of milliseconds to wait for the nameserver to become operational
 * &nbsp;&nbsp;&nbsp;(and check for potential errors); disabled if 0.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PyroNameServer
  extends AbstractStandalone
  implements StreamingProcessOwner {

  private static final long serialVersionUID = 5148275104228911234L;

  /** the nameserver executable. */
  public final static String EXECUTABLE = "pyro4-ns";

  /**
   * The server mode.
   */
  public enum Mode {
    CONNECT,
    LAUNCH_AND_CONNECT,
  }

  /** the address/port of the nameserver to use. */
  protected BaseHostname m_Server;

  /** whether to launch a nameserver instance or just connect to an existing one. */
  protected Mode m_Mode;

  /** the options for the launch mode. */
  protected String m_LaunchModeOptions;

  /** the number of milliseconds to wait for nameserver to launch (and check for errors). */
  protected int m_LaunchWait;

  /** the nameserver in use. */
  protected transient NameServerProxy m_NameServer;

  /** the nameserver executable. */
  protected transient String m_NameServerExecutable;

  /** the environment in use. */
  protected transient PythonEnvironment m_Environment;

  /** the process monitor. */
  protected transient StreamingProcessOutput m_ProcessOutput;

  /** the runnable executing the command. */
  protected RunnableWithLogging m_Monitor;

  /** in case an exception occurred executing the command (gets rethrown). */
  protected IllegalStateException m_ExecutionFailure;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines the Pyro nameserver to use.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "server", "server",
      new BaseHostname("localhost:9090"));

    m_OptionManager.add(
      "mode", "mode",
      Mode.CONNECT);

    m_OptionManager.add(
      "launch-mode-options", "launchModeOptions",
      "");

    m_OptionManager.add(
      "launch-wait", "launchWait",
      1000, 0, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_NameServer           = null;
    m_NameServerExecutable = null;
  }

  /**
   * Sets the server to connect to.
   *
   * @param value 	the server
   */
  public void setServer(BaseHostname value) {
    m_Server = value;
    reset();
  }

  /**
   * Returns the server to connect to.
   *
   * @return 		the server
   */
  public BaseHostname getServer() {
    return m_Server;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String serverTipText() {
    return "The hostname and port of the nameserver to connect to (and/or launch).";
  }

  /**
   * Sets the mode for operating.
   *
   * @param value 	the mode
   */
  public void setMode(Mode value) {
    m_Mode = value;
    reset();
  }

  /**
   * Returns the mode for operating.
   *
   * @return 		the mode
   */
  public Mode getMode() {
    return m_Mode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modeTipText() {
    return "How to operate, eg simply using or also launching.";
  }

  /**
   * Sets the additional options when launching the nameserver (aside hostname and port).
   *
   * @param value 	the additional options
   */
  public void setLaunchModeOptions(String value) {
    m_LaunchModeOptions = value;
    reset();
  }

  /**
   * Returns the additional options when launching the nameserver (aside hostname and port).
   *
   * @return 		the additional options
   */
  public String getLaunchModeOptions() {
    return m_LaunchModeOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String launchModeOptionsTipText() {
    return "The additional options to use for the nameserver (aside hostname and port).";
  }

  /**
   * Sets the time to wait for the nameserver to become operational (and check for errors).
   *
   * @param value 	the wait time (milliseconds), disabled if 0
   */
  public void setLaunchWait(int value) {
    m_LaunchWait = value;
    reset();
  }

  /**
   * Returns the time to wait for the nameserver to become operational (and check for errors).
   *
   * @return 		the wait time (milliseconds), disabled if 0
   */
  public int getLaunchWait() {
    return m_LaunchWait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String launchWaitTipText() {
    return "The number of milliseconds to wait for the nameserver to become operational (and check for potential errors); disabled if 0.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "server", m_Server, "server: ");
    result += QuickInfoHelper.toString(this, "mode", m_Mode, ", mode: ");
    if (m_Mode == Mode.LAUNCH_AND_CONNECT)
      result += QuickInfoHelper.toString(this, "launchModeOptions", (m_LaunchModeOptions.isEmpty() ? "-none-" : m_LaunchModeOptions), ", launch options: ");
    result += QuickInfoHelper.toString(this, "launchWait", m_LaunchWait, ", wait: ");

    return result;
  }

  /**
   * Returns the nameserver instance.
   *
   * @return		the server instance, null if not available
   */
  public NameServerProxy getNameServer() {
    return m_NameServer;
  }

  /**
   * Returns the nameserver executable.
   *
   * @return		the full path, null if not available
   */
  public String getNameServerExecutable() {
    return m_NameServerExecutable;
  }

  /**
   * Returns what output from the process to forward.
   *
   * @return 		the output type
   */
  public StreamingProcessOutputType getOutputType() {
    return StreamingProcessOutputType.BOTH;
  }

  /**
   * Processes the incoming line.
   *
   * @param line	the line to process
   * @param stdout	whether stdout or stderr
   */
  public void processOutput(String line, boolean stdout) {
    if (stdout)
      getLogger().info("[NS-OUT] " + line);
    else
      getLogger().warning("[NS-ERR] " + line);
  }

  /**
   * Launches the nameserver.
   *
   * @throws Exception	if launching fails
   */
  protected void launch() throws Exception {
    final List<String> 	cmd;

    if (m_Environment == null)
      throw new IllegalStateException("No python environment present!");
    if (m_NameServerExecutable == null)
      m_NameServerExecutable = m_Environment.getActualBinDir() + File.separator + FileUtils.fixExecutable(EXECUTABLE);
    if (!FileUtils.fileExists(m_NameServerExecutable))
      throw new IllegalStateException("Pyro nameserver executable not found: " + m_NameServerExecutable);

    // setup thread
    cmd = new ArrayList<>();
    cmd.add(m_NameServerExecutable);
    cmd.add("-n");
    cmd.add(m_Server.hostnameValue());
    cmd.add("-p");
    cmd.add("" + m_Server.portValue(Config.NS_PORT));
    if (!m_LaunchModeOptions.trim().isEmpty())
      cmd.addAll(Arrays.asList(OptionUtils.splitOptions(m_LaunchModeOptions)));
    if (isLoggingEnabled())
      getLogger().info("Launching: " + OptionUtils.joinOptions(cmd.toArray(new String[0])));
    m_ExecutionFailure = null;
    m_ProcessOutput = new StreamingProcessOutput(this);
    m_Monitor = new RunnableWithLogging() {
      private static final long serialVersionUID = -4475355379511760429L;

      @Override
      protected void doRun() {
	try {
	  ProcessBuilder builder = new ProcessBuilder(cmd);
	  m_Environment.updatePythonPath(builder.environment());
	  m_ProcessOutput.monitor(builder);
	  if (m_ProcessOutput.getExitCode() > 0)
	    m_ExecutionFailure = new IllegalStateException("Failed to execute: " + OptionUtils.joinOptions(cmd.toArray(new String[0])) + "\nExit code: " + m_ProcessOutput.getExitCode());
	}
	catch (Exception e) {
	  m_ExecutionFailure = new IllegalStateException("Failed to execute: " + OptionUtils.joinOptions(cmd.toArray(new String[0])), e);
	}
	m_Monitor = null;
	m_ProcessOutput = null;
      }

      @Override
      public void stopExecution() {
	if (m_ProcessOutput != null)
	  m_ProcessOutput.destroy();
	super.stopExecution();
      }
    };
    new Thread(m_Monitor).start();

    // wait for nameserver to become operational
    if (m_LaunchWait > 0) {
      Utils.wait(this, m_LaunchWait, 100);
      if (m_ExecutionFailure != null)
	throw m_ExecutionFailure;
    }
  }

  /**
   * Connects to the nameserver.
   *
   * @throws IOException	if connection fails
   */
  protected void connect() throws IOException {
    if (isLoggingEnabled())
      getLogger().info("Connecting to: " + m_Server);
    m_NameServer = NameServerProxy.locateNS(
      m_Server.hostnameValue(), m_Server.portValue(Config.NS_PORT), null);
  }

  /**
   * Disconnects from the nameserver.
   */
  protected void disconnect() {
    if (m_NameServer != null) {
      if (isLoggingEnabled())
	getLogger().info("Disconnecting from: " + m_Server);
      m_NameServer.close();
    }
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String 	result;

    result = super.setUp();

    if (result == null) {
      if (m_Mode == Mode.LAUNCH_AND_CONNECT) {
	m_Environment = (PythonEnvironment) ActorUtils.findClosestType(this, PythonEnvironment.class, true);
	if (m_Environment == null)
	  result = "Failed to locate a " + Utils.classToString(PythonEnvironment.class) + " actor!";
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    try {
      switch (m_Mode) {
	case CONNECT:
	  disconnect();
	  connect();
	  break;

	case LAUNCH_AND_CONNECT:
	  disconnect();
	  launch();
	  if (!isStopped() && (m_Monitor != null))
	    connect();
	  break;

	default:
	  result = "Unhandled mode: " + m_Mode;
      }
    }
    catch (Exception e) {
      result = handleException("Failed to connect to nameserver: " + m_Server, e);
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Monitor != null)
      m_Monitor.stopExecution();
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    disconnect();
    super.wrapUp();
  }
}
