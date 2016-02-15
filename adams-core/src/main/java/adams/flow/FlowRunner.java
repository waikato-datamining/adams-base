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
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow;

import adams.core.Pausable;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.VariablesHandler;
import adams.core.base.BaseRegExp;
import adams.core.io.DirectoryLister;
import adams.core.io.DirectoryLister.Sorting;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.management.ProcessUtils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.gui.application.AbstractInitialization;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-home &lt;java.lang.String&gt; (property: home)
 * &nbsp;&nbsp;&nbsp;The directory to use as the project's home directory, overriding the automatically 
 * &nbsp;&nbsp;&nbsp;determined one.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-headless (property: headless)
 * &nbsp;&nbsp;&nbsp;If set to true, the actor is run in headless mode without GUI components.
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
 * </pre>
 * 
 * <pre>-clean-up (property: cleanUp)
 * &nbsp;&nbsp;&nbsp;If set to true, then a clean up is performed after execution, removing any 
 * &nbsp;&nbsp;&nbsp;graphical output as well.
 * </pre>
 * 
 * <pre>-no-execute (property: noExecute)
 * &nbsp;&nbsp;&nbsp;If set to true, then flow execution is suppressed; flow is only loaded, 
 * &nbsp;&nbsp;&nbsp;set up and wrapped up.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowRunner
  extends AbstractOptionHandler
  implements Stoppable, Pausable {

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

  /** the directory to use as the project's home directory. */
  protected String m_Home;

  /** whether to suppress flow execution, simply load/setUp/wrapUp instead. */
  protected boolean m_NoExecute;

  /** whether to clean up after execution, i.e., removing graphical output
   * automatically. */
  protected boolean m_CleanUp;

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
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actor = null;
    m_LastActor   = null;
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
    String		result;
    List<String>	errors;
    DirectoryLister	lister;
    String[]		flows;

    result              = null;
    m_InterruptedByUser = false;

    if (isLoggingEnabled())
      getLogger().info("PID: " + ProcessUtils.getVirtualMachinePID());

    AbstractInitialization.initAll();

    // clean up last run
    if (m_LastActor != null)
      m_LastActor.destroy();

    // file has precedence over directory
    if (!m_Input.isDirectory()) {
      errors = new ArrayList<String>();
      m_Actor = ActorUtils.read(m_Input.getAbsolutePath(), errors);
      if (!errors.isEmpty()) {
	result = "Failed to load actor from '" + m_Input + "'!\n" + Utils.flatten(errors, "\n");
	return result;
      }
      if (!(m_Actor instanceof Flow)) {
	getLogger().warning("Root element is not a " + Flow.class.getName() + ": " + m_Input + ", skipping");
	return null;
      }
    }
    else {
      lister = new DirectoryLister();
      lister.setListFiles(true);
      lister.setListDirs(false);
      lister.setSorting(Sorting.SORT_BY_NAME);
      lister.setWatchDir(new PlaceholderDirectory(m_Input));
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
    
    m_LastActor = m_Actor;
    if (isLoggingEnabled())
      getLogger().fine("Actor command-line: " + m_Actor.toCommandLine());

    try {
      // initialize actor
      if (m_Actor instanceof Flow)
        ((Flow) m_Actor).setHeadless(isHeadless() || GraphicsEnvironment.isHeadless());
      if (isLoggingEnabled() && m_Actor.isHeadless())
	getLogger().info("Running in headless mode");

      result = m_Actor.setUp();
      if (isLoggingEnabled())
	getLogger().info("setUp() result: " + result);

      // execute actor
      if (!m_NoExecute) {
	if (result == null) {
	  if (m_Actor instanceof VariablesHandler)
	    ActorUtils.updateVariablesWithFlowFilename((VariablesHandler) m_Actor, m_Input);
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
      e.printStackTrace();
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
    if ((m_Actor != null) && (m_Actor instanceof Pausable))
      return ((Pausable) m_Actor).isPaused();
    else
      return false;
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
   * Instantiates the flow with the given options.
   *
   * @param classname	the classname of the flow to instantiate
   * @param options	the options for the flow
   * @return		the instantiated flow or null if an error occurred
   */
  public static FlowRunner forName(String classname, String[] options) {
    FlowRunner	result;

    try {
      result = (FlowRunner) OptionUtils.forName(FlowRunner.class, classname, options);
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
  public static FlowRunner forCommandLine(String cmdline) {
    return (FlowRunner) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Stops all engines of the specified ScriptingEngine class.
   *
   * @param engines	the scripting engine to use
   */
  protected static void stopAllEngines(Class[] engines) {
    int		i;
    Method	method;

    for (i = 0; i < engines.length; i++) {
      try {
	method = engines[i].getMethod(METHOD_STOPALLENGINES, new Class[0]);
	method.invoke(null, new Object[0]);
      }
      catch (Exception e) {
	System.err.println("Failed to call " + engines[i].getName() + "." + METHOD_STOPALLENGINES + ":");
	e.printStackTrace();
      }
    }
  }

  /**
   * Runs the flow from commandline.
   *
   * @param env		the environment class to use
   * @param flow	the flow class to execute
   * @param engines	the class array of the scripting engines
   * @param args	the commandline arguments, use -help to display all
   */
  public static void runFlow(Class env, Class flow, Class[] engines, String[] args) {
    FlowRunner	flowInst;
    String	result;

    Environment.setEnvironmentClass(env);
    Environment.setHome(OptionUtils.getOption(args, "-home"));
    LoggingHelper.useHandlerFromOptions(args);

    try {
      if (OptionUtils.helpRequested(args)) {
	System.out.println("Help requested...\n");
	flowInst = forName(flow.getName(), new String[0]);
	System.out.print("\n" + OptionUtils.list(flowInst));
	LoggingHelper.outputHandlerOption();
      }
      else {
	flowInst = forName(flow.getName(), args);
	ArrayConsumer.setOptions(flowInst, args);
	result = flowInst.execute();
	stopAllEngines(engines);
	if (result == null) {
	  System.out.println("\nFinished execution!");
	}
	else {
	  System.err.println("\n" + result);
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
   * Use "-f &lt;file&gt;" to supply a flow setup file to execute.
   *
   * @param args	the options to use
   */
  public static void main(String[] args) {
    runFlow(Environment.class, FlowRunner.class, new Class[]{adams.gui.scripting.ScriptingEngine.class}, args);
  }
}
