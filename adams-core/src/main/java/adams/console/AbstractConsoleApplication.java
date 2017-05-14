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
 * AbstractConsoleApplication.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.console;

import adams.core.logging.LoggingHelper;
import adams.core.management.ProcessUtils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.gui.scripting.ScriptingEngine;

/**
 * Ancestor for simple console applications.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractConsoleApplication
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 2214043765392050407L;

  /**
   * Executes the application.
   *
   * @return		null if successful, otherwise error message
   */
  protected abstract String execute();

  /**
   * Instantiates the application with the given options.
   *
   * @param classname	the classname of the application to instantiate
   * @param options	the options for the application
   * @return		the instantiated application or null if an error occurred
   */
  public static AbstractConsoleApplication forName(String classname, String[] options) {
    AbstractConsoleApplication	result;

    try {
      result = (AbstractConsoleApplication) OptionUtils.forName(AbstractConsoleApplication.class, classname, options);
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
  public static AbstractConsoleApplication forCommandLine(String cmdline) {
    return (AbstractConsoleApplication) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
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
    AbstractConsoleApplication application;

    Environment.setEnvironmentClass(env);
    LoggingHelper.useHandlerFromOptions(options);

    try {
      if (OptionUtils.helpRequested(options)) {
	System.out.println("Help requested...\n");
	application = forName(app.getName(), new String[0]);
	System.out.print("\n" + OptionUtils.list(application));
	LoggingHelper.outputHandlerOption();
	ScriptingEngine.stopAllEngines();
      }
      else {
	application = forName(app.getName(), options);
	application.getLogger().info("PID: " + ProcessUtils.getVirtualMachinePID());
	application.execute();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
