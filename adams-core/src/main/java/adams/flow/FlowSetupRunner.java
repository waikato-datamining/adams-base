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
 * FlowSetupRunner.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow;

import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.HomeRelocator;
import adams.flow.setup.FlowSetup;
import adams.flow.setup.FlowSetupManager;
import adams.gui.application.AbstractInitialization;

/**
 <!-- globalinfo-start -->
 * Runs a flow control center setup from commandline.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-home &lt;java.lang.String&gt; (property: home)
 * &nbsp;&nbsp;&nbsp;The directory to use as the project's home directory, overriding the automatically 
 * &nbsp;&nbsp;&nbsp;determined one.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-setup &lt;adams.core.io.PlaceholderFile&gt; (property: setupFile)
 * &nbsp;&nbsp;&nbsp;The setup file to load and execute.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: setupName)
 * &nbsp;&nbsp;&nbsp;The name of the setup to execute.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowSetupRunner
  extends AbstractOptionHandler
  implements HomeRelocator {

  /** for serialization. */
  private static final long serialVersionUID = 8691311691669858254L;

  /** the control center setup. */
  protected PlaceholderFile m_SetupFile;

  /** the name of the setup to execute. */
  protected String m_SetupName;

  /** the directory to use as the project's home directory. */
  protected String m_Home;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Runs a flow control center setup from commandline.";
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
	    "setup", "setupFile",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "name", "setupName",
	    "");
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
   * Sets the setup file to load the actor from.
   *
   * @param value 	the file
   */
  public void setSetupFile(PlaceholderFile value) {
    m_SetupFile = value;
    reset();
  }

  /**
   * Returns the setup file to load the actor from.
   *
   * @return 		the file
   */
  public PlaceholderFile getSetupFile() {
    return m_SetupFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String setupFileTipText() {
    return "The setup file to load and execute.";
  }

  /**
   * Sets the name of the setup to execute.
   *
   * @param value 	the name
   */
  public void setSetupName(String value) {
    m_SetupName = value;
    reset();
  }

  /**
   * Returns the name of the setup to execute.
   *
   * @return 		the name
   */
  public String getSetupName() {
    return m_SetupName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String setupNameTipText() {
    return "The name of the setup to execute.";
  }

  /**
   * Loads the control center setup and executes it, without waiting for it
   * to finish.
   *
   * @return		the error if one occurred, otherwise null (= everything OK)
   */
  public String execute() {
    return execute(false);
  }

  /**
   * Loads the control center setup and executes it.
   *
   * @param wait	whether to wait for the execution to finish
   * @return		the error if one occurred, otherwise null (= everything OK)
   */
  public String execute(boolean wait) {
    String		result;
    FlowSetupManager	manager;
    FlowSetup		setup;

    result = null;
    setup  = null;

    AbstractInitialization.initAll();

    // load
    manager = new FlowSetupManager();
    if (!m_SetupFile.exists() || m_SetupFile.isDirectory())
      result = "Setup file '" + m_SetupFile + "' does not exist or is directory!";
    if (result == null) {
      if (!manager.read(m_SetupFile.getAbsolutePath()))
        result = "Error reading setup file '" + m_SetupFile + "'!";
    }

    // check target
    if (result == null) {
      if (manager.indexOf(m_SetupName) == -1)
	result = "Cannot find setup '" + m_SetupName + "' in setup file '" + m_SetupFile + "'!";
      else
	setup = manager.get(manager.indexOf(m_SetupName));
    }

    // execute in separate thread
    if (setup != null) {
      if (!setup.execute(wait))
	result = setup.retrieveLastError();
    }

    return result;
  }

  /**
   * Instantiates the runner with the given options.
   *
   * @param classname	the classname of the runner to instantiate
   * @param options	the options for the runner
   * @return		the instantiated runner or null if an error occurred
   */
  public static FlowSetupRunner forName(String classname, String[] options) {
    FlowSetupRunner	result;

    try {
      result = (FlowSetupRunner) OptionUtils.forName(FlowSetupRunner.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the runner from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			runner to instantiate
   * @return		the instantiated runner
   * 			or null if an error occurred
   */
  public static FlowSetupRunner forCommandLine(String cmdline) {
    return (FlowSetupRunner) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Runs the setup from commandline.
   *
   * @param env		the environment class to use
   * @param flow	the FlowControlCenterRunner class to execute
   * @param args	the commandline arguments, use -help to display all
   */
  public static void runSetup(Class env, Class flow, String[] args) {
    FlowSetupRunner	setupInst;
    String		result;

    Environment.setEnvironmentClass(env);
    Environment.setHome(OptionUtils.getOption(args, "-home"));
    LoggingHelper.useHandlerFromOptions(args);

    try {
      if (OptionUtils.helpRequested(args)) {
	System.out.println("Help requested...\n");
	setupInst = forName(flow.getName(), new String[0]);
	System.out.print("\n" + OptionUtils.list(setupInst));
	LoggingHelper.outputHandlerOption();
      }
      else {
	setupInst = forName(flow.getName(), args);
	result    = setupInst.execute();
	if (result == null) {
	  System.out.println("\nInitiated execution...");
	}
	else {
	  System.out.println("\n" + result);
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
   * Use "-help" to see all options.
   *
   * @param args	the options to use
   */
  public static void main(String[] args) {
    runSetup(Environment.class, FlowSetupRunner.class, args);
  }
}
