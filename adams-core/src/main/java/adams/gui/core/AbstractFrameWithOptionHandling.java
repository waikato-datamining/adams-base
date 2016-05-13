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
 * AbstractFrameWithOptionHandling.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.core;

import adams.core.option.OptionHandler;
import adams.core.option.OptionManager;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.HomeRelocator;

/**
 * Ancestor to all frames that handle command-line options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFrameWithOptionHandling
  extends BaseFrame
  implements OptionHandler, HomeRelocator {

  /** for serialization. */
  private static final long serialVersionUID = -5800519559483605870L;

  /** for managing the available options. */
  protected OptionManager m_OptionManager;

  /** the directory to use as the project's home directory. */
  protected String m_Home;

  /**
   * Constructor for creating frame with no title.
   */
  public AbstractFrameWithOptionHandling() {
    this("");
  }

  /**
   * The constructor for creating frame with title.
   *
   * @param title	the title of the frame
   */
  public AbstractFrameWithOptionHandling(String title) {
    super(title);
  }

  /**
   * Contains all the initialization steps to perform.
   */
  @Override
  protected void performInitialization() {
    initialize();
    defineOptions();
    getOptionManager().setDefaults();
    initGUI();
    finishInit();
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

    m_OptionManager.add(
	"home", "home",
        "");
  }

  /**
   * For resetting the frame.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void reset() {
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
   * <br><br>
   * Cleans up the options.
   *
   * @see	#cleanUpOptions()
   */
  public void destroy() {
    cleanUpOptions();
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
   * Returns the commandline string.
   *
   * @return		 the commandline
   */
  @Override
  public String toCommandLine() {
    return OptionUtils.getCommandLine(this);
  }

  /**
   * Instantiates the frame with the given options.
   *
   * @param classname	the classname of the frame to instantiate
   * @param options	the options for the frame
   * @return		the instantiated frame or null if an error occurred
   */
  public static AbstractFrameWithOptionHandling forName(String classname, String[] options) {
    AbstractFrameWithOptionHandling	result;

    try {
      result = (AbstractFrameWithOptionHandling) OptionUtils.forName(AbstractFrameWithOptionHandling.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Runs the frame from the commandline.
   *
   * @param env		the environment class to use
   * @param app		the frame class
   * @param options	the commandline options
   * @return		the instantiated frame, null in case of an error or
   * 			help being invoked
   */
  public static AbstractFrameWithOptionHandling runFrame(Class env, Class app, String[] options) {
    AbstractFrameWithOptionHandling	result;

    Environment.setEnvironmentClass(env);
    Environment.setHome(OptionUtils.getOption(options, "-home"));

    try {
      if (OptionUtils.helpRequested(options)) {
	System.out.println("Help requested...\n");
	result = forName(app.getName(), new String[0]);
	System.out.println("\n" + OptionUtils.list(result));
	result.dispose();
	result = null;
      }
      else {
	result = forName(app.getName(), options);
	result.setVisible(true);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }
}
