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
 * BaseWindow.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JWindow;

import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.env.Environment;

/**
 * A window that loads the size and location from the props file automatically.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseWindow
  extends JWindow {

  /** for serialization. */
  private static final long serialVersionUID = 6176039693692479862L;

  /** the logger. */
  protected Logger m_Logger;

  /**
   * Initializes the frame with no title.
   */
  public BaseWindow() {
    super();
    performInitialization();
  }

  /**
   * Contains all the initialization steps to perform.
   */
  protected void performInitialization() {
    initialize();
    initGUI();
    finishInit();
  }

  /**
   * For initializing members.
   */
  protected void initialize() {
    initializeLogger();
  }

  /**
   * For initializing the GUI.
   */
  protected void initGUI() {
    if (GUIHelper.getLogoIcon() != null)
      setIconImage(GUIHelper.getLogoIcon().getImage());
  }

  /**
   * finishes the initialization, by setting size/location.
   */
  protected void finishInit() {
    // size and location
    GUIHelper.setSizeAndLocation(this, this);
  }
  
  /**
   * Initializes the logger.
   * <br><br>
   * Default implementation uses the class name.
   */
  protected void initializeLogger() {
    m_Logger = LoggingHelper.getLogger(getClass());
  }
  
  /**
   * Returns the logger in use.
   * 
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      initializeLogger();
    return m_Logger;
  }
  
  /**
   * Returns whether logging is enabled.
   * 
   * @return		true if not {@link Level#OFF}
   */
  public boolean isLoggingEnabled() {
    return (getLogger().getLevel() != Level.OFF);
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
  }

  /**
   * Hook method just after the dialog was made visible.
   */
  protected void afterShow() {
  }

  /**
   * Hook method just before the dialog is hidden.
   */
  protected void beforeHide() {
  }

  /**
   * Hook method just after the dialog was hidden.
   */
  protected void afterHide() {
  }

  /**
   * closes/shows the dialog.
   *
   * @param value	if true then display the dialog, otherwise close it
   */
  @Override
  public void setVisible(boolean value) {
    if (value)
      beforeShow();
    else
      beforeHide();

    super.setVisible(value);

    if (value)
      afterShow();
    else
      afterHide();
  }

  /**
   * Instantiates the frame.
   *
   * @param classname	the classname of the frame to instantiate
   * @return		the instantiated frame or null if an error occurred
   */
  public static BaseWindow forName(String classname) {
    BaseWindow	result;

    try {
      result = (BaseWindow) OptionUtils.forName(AbstractFrameWithOptionHandling.class, classname, new String[0]);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Runs the window from the commandline.
   *
   * @param env		the environment class to use
   * @param app		the window class
   */
  public static void runWindow(Class env, Class app) {
    BaseWindow	frameInst;

    Environment.setEnvironmentClass(env);

    frameInst = forName(app.getName());
    frameInst.setVisible(true);
  }
}
