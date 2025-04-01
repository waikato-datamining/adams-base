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
 * FlowRunner.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow;

import adams.core.MessageCollection;
import adams.core.Pausable;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.VariablesHandler;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.io.lister.Sorting;
import adams.core.logging.LoggingHelper;
import adams.core.management.ProcessUtils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.core.scriptingengine.BackgroundScriptingEngineRegistry;
import adams.core.shutdown.AbstractShutdownHook;
import adams.core.shutdown.Null;
import adams.core.shutdownbuiltin.AbstractBuiltInShutdownHook;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.processor.ManageInteractiveActors;
import adams.gui.application.AbstractInitialization;
import adams.gui.core.GUIHelper;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.gui.event.RemoteScriptingEngineUpdateListener;
import adams.scripting.RemoteScriptingEngineHandler;
import adams.scripting.engine.MultiScriptingEngine;
import adams.scripting.engine.RemoteScriptingEngine;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Executes flows from command-line.<br>
 * It is also possible to traverse a directory and execute all flows within that match a regular expression.<br>
 * Using the 'no-execute' option, you can suppress the flow execution, but still test whether the flow loads and can be fully set up and wrapped up.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-home &lt;java.lang.String&gt; (property: home)
 * &nbsp;&nbsp;&nbsp;The directory to use as the project's home directory, overriding the automatically
 * &nbsp;&nbsp;&nbsp;determined one.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-headless &lt;boolean&gt; (property: headless)
 * &nbsp;&nbsp;&nbsp;If set to true, the actor is run in headless mode without GUI components.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-non-interactive &lt;boolean&gt; (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If set to true, interactive actors suppress their interaction with the user.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-register &lt;boolean&gt; (property: register)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets register with the 'running flows registry',
 * &nbsp;&nbsp;&nbsp; making it visible to remote commands.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file (or directory containing flows) to load the actor from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-include &lt;adams.core.base.BaseRegExp&gt; (property: include)
 * &nbsp;&nbsp;&nbsp;The regular expression for including flows when traversing a directory rather
 * &nbsp;&nbsp;&nbsp;than just executing a single flow.
 * &nbsp;&nbsp;&nbsp;default: .*\\\\.(flow|flow.gz)
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-clean-up &lt;boolean&gt; (property: cleanUp)
 * &nbsp;&nbsp;&nbsp;If set to true, then a clean up is performed after execution, removing any
 * &nbsp;&nbsp;&nbsp;graphical output as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-no-execute &lt;boolean&gt; (property: noExecute)
 * &nbsp;&nbsp;&nbsp;If set to true, then flow execution is suppressed; flow is only loaded,
 * &nbsp;&nbsp;&nbsp;set up and wrapped up.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-remote-scripting-engine-cmdline &lt;java.lang.String&gt; (property: remoteScriptingEngineCmdLine)
 * &nbsp;&nbsp;&nbsp;The command-line of the remote scripting engine to execute at startup time;
 * &nbsp;&nbsp;&nbsp; use empty string for disable scripting.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-shutdown-hook &lt;adams.core.shutdown.AbstractShutdownHook&gt; (property: shutdownHook)
 * &nbsp;&nbsp;&nbsp;The shutdown hook to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.shutdown.Null
 * </pre>
 *
 * <pre>-force-exit &lt;boolean&gt; (property: forceExit)
 * &nbsp;&nbsp;&nbsp;If set to true, then the runner will trigger a System.exit call to forcefully
 * &nbsp;&nbsp;&nbsp;exit the process.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowRunner
  extends AbstractOptionHandler
  implements Stoppable, Pausable, RemoteScriptingEngineHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5693250462014974198L;

  /** the method for stopping all engines. */
  public final static String METHOD_STOPALLENGINES = "stopAllEngines";

  /** the flow file/dir with flows to execute. */
  protected PlaceholderFile m_Input;

  /** regular expression for including flows when traversing a directory. */
  protected BaseRegExp m_Include;

  /** whether the execution is to be headless, i.e., no GUI components. */
  protected boolean m_Headless;

  /** whether the use non-interactive execution. */
  protected boolean m_NonInteractive;

  /** whether to register the flow. */
  protected boolean m_Register;

  /** the directory to use as the project's home directory. */
  protected String m_Home;

  /** whether to suppress flow execution, simply load/setUp/wrapUp instead. */
  protected boolean m_NoExecute;

  /** whether to clean up after execution, i.e., removing graphical output
   * automatically. */
  protected boolean m_CleanUp;

  /** the commandline of the remote scripting engine to use at startup time. */
  protected String m_RemoteScriptingEngineCmdLine;

  /** the remote command scripting engine. */
  protected RemoteScriptingEngine m_RemoteScriptingEngine;

  /** the shutdown hook. */
  protected AbstractShutdownHook m_ShutdownHook;

  /** whether the force an exit after the flows were stopped. */
  protected boolean m_ForceExit;

  /** the listeners for changes to the remote scripting engine. */
  protected Set<RemoteScriptingEngineUpdateListener> m_RemoteScriptingEngineUpdateListeners;

  /** the actor to execute. */
  protected Actor m_Actor;

  /** the last actor that was executed. */
  protected Actor m_LastActor;

  /** whether the flow was interrupted by the user. */
  protected boolean m_InterruptedByUser;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Executes flows from command-line.\n"
        + "It is also possible to traverse a directory and execute all flows within "
        + "that match a regular expression.\n"
        + "Using the 'no-execute' option, you can suppress the flow execution, but "
        + "still test whether the flow loads and can be fully set up and wrapped up.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "home", "home",
      "");

    m_OptionManager.add(
      "headless", "headless",
      false);

    m_OptionManager.add(
      "non-interactive", "nonInteractive",
      false);

    m_OptionManager.add(
      "register", "register",
      false);

    m_OptionManager.add(
      "input", "input",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "include", "include",
      new BaseRegExp(".*\\.(" + Actor.FILE_EXTENSION + "|" + Actor.FILE_EXTENSION_GZ + ")"));

    m_OptionManager.add(
      "clean-up", "cleanUp",
      false);

    m_OptionManager.add(
      "no-execute", "noExecute",
      false);

    m_OptionManager.add(
      "remote-scripting-engine-cmdline", "remoteScriptingEngineCmdLine",
      "");

    m_OptionManager.add(
      "shutdown-hook", "shutdownHook",
      new Null());

    m_OptionManager.add(
      "force-exit", "forceExit",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actor                                = null;
    m_LastActor                            = null;
    m_RemoteScriptingEngine                = null;
    m_RemoteScriptingEngineUpdateListeners = new HashSet<>();
  }

  /**
   * Overrides the automatic detection of the project's home directory and uses
   * the specified directory instead. No placeholders allowed, should be
   * absolute.
   *
   * @param value	the directory to use
   */
  public void setHome(String value) {
    m_Home = value;
    reset();
  }

  /**
   * Returns the directory to use as home directory instead of the automatically
   * determined one.
   *
   * @return		the directory to use
   */
  public String getHome() {
    return m_Home;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String homeTipText() {
    return "The directory to use as the project's home directory, overriding the automatically determined one.";
  }

  /**
   * Sets whether the actor is to be run in headless mode, i.e., suppressing
   * GUI components.
   *
   * @param value	if true then GUI components will be suppressed
   */
  public void setHeadless(boolean value) {
    m_Headless = value;
    GUIHelper.setHeadless(value);
    reset();
  }

  /**
   * Returns whether the actor is run in headless mode.
   *
   * @return		true if GUI components are suppressed
   */
  public boolean isHeadless() {
    return m_Headless;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headlessTipText() {
    return "If set to true, the actor is run in headless mode without GUI components.";
  }

  /**
   * Sets whether to run the flow without interaction with the user.
   *
   * @param value	if true then interactive actors get suppressed
   */
  public void setNonInteractive(boolean value) {
    m_NonInteractive = value;
    reset();
  }

  /**
   * Returns whether to run the flow without interaction with the user.
   *
   * @return		true if interactive actors get suppressed
   */
  public boolean isNonInteractive() {
    return m_NonInteractive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nonInteractiveTipText() {
    return "If set to true, interactive actors suppress their interaction with the user.";
  }

  /**
   * Sets whether to register the flow with the running flows registry,
   * making it visible to remote commands.
   *
   * @param value	true if to register
   */
  public void setRegister(boolean value) {
    m_Register = value;
    reset();
  }

  /**
   * Returns whether to register the flow with the running flows registry,
   * making it visible to remote commands.
   *
   * @return		true if to register
   */
  public boolean getRegister() {
    return m_Register;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String registerTipText() {
    return "If set to true, the flow gets register with the 'running flows registry', making it visible to remote commands.";
  }

  /**
   * Sets the file (or directory with flows) to load the actor from.
   *
   * @param value 	the file/dir
   */
  public void setInput(PlaceholderFile value) {
    m_Input = value;
    reset();
  }

  /**
   * Returns the file (or directory with flows) to load the actor from.
   *
   * @return 		the file/dir
   */
  public PlaceholderFile getInput() {
    return m_Input;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputTipText() {
    return "The file (or directory containing flows) to load the actor from.";
  }

  /**
   * Sets the regular expression to match flow files against when traversing
   * a directory.
   *
   * @param value 	the regular expression
   */
  public void setInclude(BaseRegExp value) {
    m_Include = value;
    reset();
  }

  /**
   * Returns the regular expression for matching flow files when traversing
   * a directory.
   *
   * @return 		the regular expression
   */
  public BaseRegExp getInclude() {
    return m_Include;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String includeTipText() {
    return
      "The regular expression for including flows when traversing a "
        + "directory rather than just executing a single flow.";
  }

  /**
   * Sets whether to clean up after execution, i.e., removing graphical
   * output.
   *
   * @param value	if true then a clean up is performed after execution
   */
  public void setCleanUp(boolean value) {
    m_CleanUp = value;
    reset();
  }

  /**
   * Returns whether to perform a clean up after the execution and remove
   * graphical output.
   *
   * @return		true if clean up is performed after execution
   */
  public boolean isCleanUp() {
    return m_CleanUp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cleanUpTipText() {
    return
      "If set to true, then a clean up is performed after execution, "
        + "removing any graphical output as well.";
  }

  /**
   * Sets whether to suppress flow execution.
   *
   * @param value	if true then no execution
   */
  public void setNoExecute(boolean value) {
    m_NoExecute = value;
    reset();
  }

  /**
   * Returns whether to suppress flow execution.
   *
   * @return		true if no execution
   */
  public boolean isNoExecute() {
    return m_NoExecute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noExecuteTipText() {
    return
      "If set to true, then flow execution is suppressed; flow is only "
        + "loaded, set up and wrapped up.";
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
   * Sets the shutdown hook to install/use.
   *
   * @param value 	the hook
   */
  public void setShutdownHook(AbstractShutdownHook value) {
    m_ShutdownHook = value;
    reset();
  }

  /**
   * Returns the shutdown hook to install/use.
   *
   * @return 		the hook
   */
  public AbstractShutdownHook getShutdownHook() {
    return m_ShutdownHook;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shutdownHookTipText() {
    return "The shutdown hook to use.";
  }

  /**
   * Sets whether to force the process exit after flows were stopped.
   *
   * @param value	if true then force exit
   */
  public void setForceExit(boolean value) {
    m_ForceExit = value;
    reset();
  }

  /**
   * Returns whether to force the process exit after flows were stopped.
   *
   * @return		true if to force exit
   */
  public boolean getForceExit() {
    return m_ForceExit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forceExitTipText() {
    return
      "If set to true, then the runner will trigger a System.exit call to forcefully exit the process.";
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
   * Returns the instance of the last actor that was executed.
   *
   * @return		the actor or null if no actor has been run yet
   */
  public Actor getLastActor() {
    return m_LastActor;
  }

  /**
   * Executes the actor if possible.
   *
   * @return		the error if one occurred, otherwise null (= everything OK)
   */
  public String execute() {
    String			result;
    MessageCollection 		errors;
    LocalDirectoryLister 	lister;
    String[]			flows;
    ManageInteractiveActors	procInteractive;
    RemoteScriptingEngine 	engine;

    result              = null;
    m_InterruptedByUser = false;

    if (isLoggingEnabled())
      getLogger().info("PID: " + ProcessUtils.getVirtualMachinePID());

    AbstractInitialization.initAll();
    AbstractBuiltInShutdownHook.installAll();

    if (!(m_ShutdownHook instanceof Null)) {
      result = m_ShutdownHook.install();
      if (result != null)
        return result;
    }

    // start scripting engine?
    if (!m_RemoteScriptingEngineCmdLine.isEmpty() && (getRemoteScriptingEngine() == null)) {
      try {
        engine = (RemoteScriptingEngine) OptionUtils.forAnyCommandLine(RemoteScriptingEngine.class, m_RemoteScriptingEngineCmdLine);
      }
      catch (Exception e) {
        engine = null;
        getLogger().log(
          Level.SEVERE,
          "Failed to instantiate remote scripting engine from commandline: '"
            + m_RemoteScriptingEngineCmdLine + "'",
          e);
      }
      if (engine != null)
        setRemoteScriptingEngine(engine);
    }

    // clean up last run
    if (m_LastActor != null)
      m_LastActor.destroy();

    // file has precedence over directory
    if (!m_Input.isDirectory()) {
      errors = new MessageCollection();
      m_Actor = ActorUtils.read(m_Input.getAbsolutePath(), errors);
      if (!errors.isEmpty()) {
        result = "Failed to load actor from '" + m_Input + "'!\n" + errors;
        return result;
      }
      if (!(m_Actor instanceof Flow)) {
        getLogger().warning("Root element is not a " + Flow.class.getName() + ": " + m_Input + ", skipping");
        return null;
      }
    }
    else {
      lister = new LocalDirectoryLister();
      lister.setListFiles(true);
      lister.setListDirs(false);
      lister.setSorting(Sorting.SORT_BY_NAME);
      lister.setWatchDir(m_Input.getAbsolutePath());
      lister.setRegExp(m_Include);
      flows = lister.list();
      if (isLoggingEnabled())
        getLogger().info("Found #" + flows.length + " flows");
      if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
        getLogger().fine("Flows: " + Utils.arrayToString(flows));
      for (String flow: flows) {
        if (isLoggingEnabled())
          getLogger().info("Running: " + flow);
        setInput(new PlaceholderFile(flow));
        result = execute();
        if (result != null)
          break;
      }
      return result;
    }

    // non-interactive?
    if (m_NonInteractive) {
      procInteractive = new ManageInteractiveActors();
      procInteractive.setEnable(false);
      procInteractive.process(m_Actor);
      if (procInteractive.isModified()) {
        m_Actor.destroy();
        m_Actor = procInteractive.getModifiedActor();
        if (isLoggingEnabled())
          getLogger().info("Disabled interactive actors");
      }
    }

    m_LastActor = m_Actor;
    if (isLoggingEnabled())
      getLogger().fine("Actor command-line: " + m_Actor.toCommandLine());

    try {
      // initialize actor
      if (m_Actor instanceof Flow) {
        // headless?
        ((Flow) m_Actor).setHeadless(isHeadless() || GUIHelper.isHeadless());
        if (isLoggingEnabled() && m_Actor.isHeadless())
          getLogger().info("Running in headless mode");
        // register?
        ((Flow) m_Actor).setRegister(m_Register);
        if (isLoggingEnabled() && ((Flow) m_Actor).getRegister())
          getLogger().info("Flow added to running flow registry");
      }

      ActorUtils.updateProgrammaticVariables((VariablesHandler & Actor) m_Actor, m_Input);
      result = m_Actor.setUp();
      ActorUtils.updateProgrammaticVariables((VariablesHandler & Actor) m_Actor, m_Input);
      if (isLoggingEnabled())
        getLogger().info("setUp() result: " + result);

      // execute actor
      if (!m_NoExecute) {
        if (result == null) {
          result = m_Actor.execute();
          if (isLoggingEnabled())
            getLogger().info("execute() result: " + result);
          if (m_Actor.hasStopMessage()) {
            getLogger().info("stop message: " + m_Actor.getStopMessage());
            if (result == null)
              result = m_Actor.getStopMessage();
          }
        }
      }
      else {
        getLogger().info("execute suppressed");
      }

      // finish up
      m_Actor.wrapUp();
      if (isLoggingEnabled())
        getLogger().info("wrapUp() finished");

      // clean up?
      if (m_CleanUp) {
        m_Actor.cleanUp();
        if (isLoggingEnabled())
          getLogger().info("cleanUp() finished");
      }

      // any errors?
      if (result != null) {
        if (!m_Input.isDirectory())
          result = "Error executing flow '" + m_Input + "': " + result;
        else
          result = "Error executing actor: " + result;
      }
    }
    catch (Exception e) {
      result = e.toString();
      LoggingHelper.global().log(Level.SEVERE, "Failed to execute flow!", e);
    }

    m_Actor = null;

    // interrupted by user?
    if (m_InterruptedByUser && (result == null))
      result = "Flow interrupted by user!";

    return result;
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    if ((m_Actor != null) && (m_Actor instanceof Pausable))
      ((Pausable) m_Actor).pauseExecution();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    return ((m_Actor != null) && (m_Actor instanceof Pausable)) && ((Pausable) m_Actor).isPaused();
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    if ((m_Actor != null) && (m_Actor instanceof Pausable))
      ((Pausable) m_Actor).resumeExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_Actor != null) {
      m_InterruptedByUser = true;
      m_Actor.stopExecution();
    }
  }

  /**
   * Instantiates the flow runner with the given options.
   *
   * @param classname	the classname of the flow runner to instantiate
   * @param options	the options for the flow
   * @return		the instantiated flow or null if an error occurred
   */
  public static FlowRunner forName(String classname, String[] options) {
    FlowRunner	result;

    try {
      result = (FlowRunner) OptionUtils.forName(FlowRunner.class, classname, options);
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Failed to instantiate flow runner: " + classname, e);
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
  public static FlowRunner forCommandLine(String cmdline) {
    return (FlowRunner) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Runs the flow runner from commandline.
   *
   * @param env		the environment class to use
   * @param flow	the flow runner class to execute
   * @param args	the commandline arguments, use -help to display all
   */
  public static void runFlow(Class env, Class flow, String[] args) {
    FlowRunner  runner;
    String	result;

    Environment.setEnvironmentClass(env);
    Environment.setHome(OptionUtils.getOption(args, "-home"));
    LoggingHelper.useHandlerFromOptions(args);

    try {
      if (OptionUtils.helpRequested(args)) {
        System.out.println("Help requested...\n");
        runner = forName(flow.getName(), new String[0]);
        System.out.print("\n" + OptionUtils.list(runner));
        LoggingHelper.outputHandlerOption();
      }
      else {
        runner = forName(flow.getName(), args);
        ArrayConsumer.setOptions(runner, args);
        result = runner.execute();
	BackgroundScriptingEngineRegistry.getSingleton().stopAllEngines();
        if (result == null) {
          System.out.println("\nFinished execution!");
        }
        else {
          System.err.println("\n" + result);
          System.exit(1);
        }
        if (runner.getForceExit()) {
          System.out.println("Forcing exit now...");
          System.exit(0);
        }
      }
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Error executing flow!", e);
    }
  }

  /**
   * Runs the flow with the given options.
   * Use "-f &lt;file&gt;" to supply a flow setup file to execute.
   *
   * @param args	the options to use
   */
  public static void main(String[] args) {
    runFlow(Environment.class, FlowRunner.class, args);
  }
}
