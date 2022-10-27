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
 * BaseFrame.java
 * Copyright (C) 2008-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import adams.core.management.OS;
import adams.core.management.OS.OperatingSystems;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.gui.application.ChildFrame;

import javax.swing.JFrame;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.util.logging.Level;

/**
 * A frame that loads the size and location from the props file automatically.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseFrame
    extends JFrame
    implements LoggingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -4853427519044621963L;

  /** for logging. */
  protected transient Logger m_Logger;

  /** whether to use the fix. */
  protected static Boolean m_UseMaximizationFix;

  /** the maximization fix listener. */
  protected MaximizationFixWindowListener m_MaximizationFixWindowListener;

  /** the UI settings prefix to use. */
  protected String m_UISettingsPrefix;

  /** whether the UI settings got stored. */
  protected boolean m_UISettingsStored;

  /** whether UI settings were applied. */
  protected boolean m_UISettingsApplied;

  /**
   * Initializes the frame with no title.
   */
  public BaseFrame() {
    this("");
  }

  /**
   * Initializes the frame with the given title.
   *
   * @param title	the title of the frame
   */
  public BaseFrame(String title) {
    super(title);

    performInitialization();
  }

  /**
   * Initializes the frame with no title.
   *
   * @param gc		the graphics configuration to use
   */
  public BaseFrame(GraphicsConfiguration gc) {
    this("", gc);
  }

  /**
   * Initializes the frame with the specified title.
   *
   * @param title	the title of the frame
   * @param gc		the graphics configuration to use
   */
  public BaseFrame(String title, GraphicsConfiguration gc) {
    super(title, gc);

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
    String		useFix;
    OperatingSystems	os;

    initializeLogger();
    if (m_UseMaximizationFix == null) {
      useFix = GUIHelper.getString("UseFrameMaximizationFix", "" + OperatingSystems.LINUX);
      try {
	os = OperatingSystems.valueOf(useFix);
      }
      catch (Exception e) {
	os = null;
      }
      m_UseMaximizationFix = useFix.equals("true") || ((os != null) && (OS.isOS(os)));
      if (m_UseMaximizationFix)
	getLogger().log(Level.INFO, "Using frame maximization fix (GUIHelper.props)");
    }
    m_MaximizationFixWindowListener = new MaximizationFixWindowListener(
	this, m_UseMaximizationFix, GUIHelper.getInteger("FrameMaximizationFixDelay", 200));

    m_UISettingsPrefix  = "";
    m_UISettingsStored  = false;
    m_UISettingsApplied = false;
  }

  /**
   * For initializing the GUI.
   */
  protected void initGUI() {
    if (ImageManager.getLogoIcon() != null)
      setIconImage(ImageManager.getLogoIcon().getImage());

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    addWindowStateListener(m_MaximizationFixWindowListener);
  }

  /**
   * Updates the bounds of the window.
   *
   * @param x		the new x of the frame
   * @param y		the new y of the frame
   * @param width	the new width of the frame
   * @param height	the new height of the frame
   */
  @Override
  public void setBounds(int x, int y, int width, int height) {
    if (m_MaximizationFixWindowListener != null)
      m_MaximizationFixWindowListener.updateBounds(x, y, width, height);
    super.setBounds(x, y, width, height);
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
   * Sets the prefix for the UI settings (eg stores width/height).
   *
   * @param cls		the class to use as prefix, ignored if null or empty
   */
  public void setUISettingsPrefix(Class cls) {
    setUISettingsPrefix((cls == null) ? "" : cls.getName());
  }

  /**
   * Sets the prefix for the UI settings (eg stores width/height).
   *
   * @param value	the prefix, ignored if null or empty
   */
  public void setUISettingsPrefix(String value) {
    if (value == null)
      value = "";
    m_UISettingsPrefix  = value;
    m_UISettingsApplied = false;
    m_UISettingsStored  = false;
  }

  /**
   * Returns the prefix for the UI settings.
   *
   * @return		the prefix, empty if ignored
   */
  public String getUISettingsPrefix() {
    return m_UISettingsPrefix;
  }

  /**
   * Returns whether UI settings were applied. E.g., to determine whether still necessary to set default dimensions/location.
   *
   * @return		true if applied
   */
  public boolean getUISettingsApplied() {
    return m_UISettingsApplied;
  }

  /**
   * Applies any UI settings if present.
   */
  public void applyUISettings() {
    Dimension 	size;
    int		x;
    int		y;

    // size
    if (UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".width") && UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".width")) {
      m_UISettingsApplied = true;
      size = getSize();
      setSize(new Dimension(
	  UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".width", size.width),
	  UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".height", size.height)));
    }

    // position
    if (UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".x") && UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".y")) {
      m_UISettingsApplied = true;
      x = getX();
      y = getY();
      setLocation(
	  UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".x", x),
	  UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".y", y));
    }

    m_UISettingsStored = false;
  }

  /**
   * Stores the UI settings.
   */
  public void storeUISettings() {
    if (!m_UISettingsStored && !m_UISettingsPrefix.isEmpty()) {
      m_UISettingsStored = true;
      UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".width", getWidth());
      UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".height", getHeight());
      UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".x", getX());
      UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".y", getY());
      UISettings.save();
      m_UISettingsApplied = false;
    }
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
    if (!m_UISettingsPrefix.isEmpty())
      applyUISettings();
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
    storeUISettings();
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
   * Sets the location relative to this component, but adjust window size
   * and position if necessary.
   *
   * @param c		the component to position the window relative to
   */
  @Override
  public void setLocationRelativeTo(Component c) {
    super.setLocationRelativeTo(c);
    GUIHelper.adjustSize(this);
    GUIHelper.fixPosition(this);
  }

  /**
   * Instantiates the frame.
   *
   * @param classname	the classname of the frame to instantiate
   * @return		the instantiated frame or null if an error occurred
   */
  public static BaseFrame forName(String classname) {
    BaseFrame	result;

    try {
      result = (BaseFrame) OptionUtils.forName(AbstractFrameWithOptionHandling.class, classname, new String[0]);
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
   * @return		the instantiated frame
   */
  public static BaseFrame runFrame(Class env, Class app) {
    BaseFrame	result;

    Environment.setEnvironmentClass(env);

    result = forName(app.getName());
    result.setVisible(true);

    return result;
  }
}
