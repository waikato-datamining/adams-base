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
 * RunScheme.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.run;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingObject;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionManager;
import adams.core.option.OptionUtils;
import adams.env.Environment;

/**
 * Abstract class for running non-commandline schemes from commandline.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class RunScheme
  extends LoggingObject
  implements OptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7050643013286875370L;

  /** for managing the available options. */
  protected OptionManager m_OptionManager;

  /**
   * default constructor.
   */
  public RunScheme() {
    super();
    initialize();
    defineOptions();
    getOptionManager().setDefaults();
  }

  /**
   * initializes member variables.
   */
  protected void initialize() {
  }

  /**
   * Returns a new instance of the option manager.
   *
   * @return		the manager to use
   */
  protected OptionManager newOptionManager() {
    return new OptionManager(this);
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  public void defineOptions() {
    m_OptionManager = newOptionManager();
  }

  /**
   * Returns the option manager.
   *
   * @return		the manager
   */
  public OptionManager getOptionManager() {
    if (m_OptionManager == null)
      defineOptions();

    return m_OptionManager;
  }

  /**
   * Cleans up the options.
   */
  public void cleanUpOptions() {
    if (m_OptionManager != null) {
      m_OptionManager.cleanUp();
      m_OptionManager = null;
    }
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <p/>
   * Cleans up the options.
   *
   * @see	#cleanUpOptions()
   */
  public void destroy() {
    cleanUpOptions();
  }

  /**
   * Performs some initializations before the actual run.
   * Default implementation does nothing.
   *
   * @throws Exception 	if something goes wrong
   */
  protected void preRun() throws Exception {
  }

  /**
   * Performs the actual run.
   *
   * @throws Exception 	if something goes wrong
   */
  protected abstract void doRun() throws Exception;

  /**
   * Performs some output/cleanup after the actual run.
   * Default implementation does nothing.
   *
   * @throws Exception 	if something goes wrong
   */
  protected void postRun() throws Exception {
  }

  /**
   * Runs the scheme and prints some information to stdout.
   *
   * @throws Exception	if something goes wrong
   */
  public void run() throws Exception {
    getLogger().info("\n--> Pre-run");
    preRun();

    getLogger().info("\n--> Run");
    doRun();

    getLogger().info("\n--> Post-run");
    postRun();
  }

  /**
   * Instantiates the run scheme with the given options.
   *
   * @param classname	the classname of the run scheme to instantiate
   * @param options	the options for the run scheme
   * @return		the instantiated run scheme or null if an error occurred
   */
  public static RunScheme forName(String classname, String[] options) {
    RunScheme	result;

    try {
      result = (RunScheme) OptionUtils.forName(RunScheme.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the run scheme from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			run scheme to instantiate
   * @return		the instantiated run scheme
   * 			or null if an error occurred
   */
  public static RunScheme forCommandLine(String cmdline) {
    return (RunScheme) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Runs the tool from commandline.
   *
   * @param env		the environment class to use
   * @param scheme	the scheme to execute
   * @param args	the commandline arguments, use -help to display all
   */
  public static void runScheme(Class env, Class scheme, String[] args) {
    RunScheme	schemeInst;

    Environment.setEnvironmentClass(env);
    LoggingHelper.useHandlerFromOptions(args);

    try {
      if (OptionUtils.helpRequested(args)) {
	System.out.println("Help requested...\n");
	schemeInst = forName(scheme.getName(), new String[0]);
	System.out.print("\n" + OptionUtils.list(schemeInst));
	LoggingHelper.outputHandlerOption();
      }
      else {
	schemeInst = forName(scheme.getName(), args);
	schemeInst.run();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
