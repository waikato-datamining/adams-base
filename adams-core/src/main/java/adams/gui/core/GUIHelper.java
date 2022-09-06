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
 * GUIHelper.java
 * Copyright (C) 2008-2021 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.Properties;
import adams.core.UniqueIDs;
import adams.core.Utils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.net.HtmlUtils;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.GUIHelperDefinition;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.Child;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextDialog;
import adams.gui.dialog.TextPanel;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

/**
 * A "little" helper class for GUI related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GUIHelper {

  /** the name of the props file. */
  public final static String FILENAME = "GUIHelper.props";

  /** the mnemonic character indicator. */
  public final static char MNEMONIC_INDICATOR = '_';

  /** the allowed mnemonics. */
  public final static String ALLOWED_MNEMONICS = "abdefghijklnpqrstuvwxyz0123456789";

  /** the approve option. */
  public final static int APPROVE_OPTION = ApprovalDialog.APPROVE_OPTION;

  /** the discard option. */
  public final static int DISCARD_OPTION = ApprovalDialog.DISCARD_OPTION;

  /** the cancel option. */
  public final static int CANCEL_OPTION = ApprovalDialog.CANCEL_OPTION;

  /** the properties. */
  protected static Properties m_Properties;

  /** the mappings for replacing keystrokes. */
  protected static HashMap<String,String> m_KeystrokeReplacements;

  /** the debugging level. */
  private final static Logger LOGGER = LoggingHelper.getConsoleLogger(GUIHelper.class);

  /** whether anti-aliasing is enabled. */
  public static boolean AntiAliasingEnabled = true;

  /** whether we are running in headless mode. */
  public static Boolean HeadlessMode = null;

  /**
   * How showInputDialog should display the multiple values to choose from.
   */
  public enum InputDialogMultiValueSelection {
    COMBOBOX,
    BUTTONS_HORIZONTAL,
    BUTTONS_VERTICAL,
  }

  /**
   * Helper class that allows external callers to communicate with input
   * dialogs, enabling them to schedule closing of the dialog.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DialogCommunication
      implements Serializable {

    private static final long serialVersionUID = -7319896279709161892L;

    /** whether the dialog should get closed. */
    protected boolean m_CloseDialogRequested;

    /**
     * Default constructor.
     */
    public DialogCommunication() {
      super();
      m_CloseDialogRequested = false;
    }

    /**
     * Requests to close the dialog.
     */
    public void requestClose() {
      m_CloseDialogRequested = true;
    }

    /**
     * Returns whether closing of the dialog was requested.
     *
     * @return true if to close dialog requested
     */
    public boolean isCloseRequested() {
      return m_CloseDialogRequested;
    }

    /**
     * Returns a short description of object.
     *
     * @return the description
     */
    @Override
    public String toString() {
      return "closeDialog=" + m_CloseDialogRequested;
    }
  }

  /**
   * Initializes the properties if necessary.
   */
  protected static synchronized void initializeProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(GUIHelperDefinition.KEY);
  }

  /**
   * Sets size stored in the props file.
   *
   * @param window	the frame to work on
   * @param c		the component to use for lookup in the props file;
   * 			null can be used to bypass the lookup in the props file
   */
  public static void setSize(Component window, Component c) {
    int		width;
    int		height;

    initializeProperties();

    // custom size?
    if (c != null) {
      if (m_Properties.getBoolean(c.getClass().getName() + ".pack", false)) {
	if (window instanceof Window)
	  ((Window) window).pack();
      }

      width = scale(m_Properties.getInteger(c.getClass().getName() + ".width", window.getWidth()));
      if (width == -1)
	width = window.getGraphicsConfiguration().getBounds().width;

      height = scale(m_Properties.getInteger(c.getClass().getName() + ".height", window.getHeight()));
      if (height == -1)
	height = window.getGraphicsConfiguration().getBounds().height;

      window.setSize(width, height);
      window.validate();
    }
  }

  /**
   * Sets size and location stored in the props file.
   *
   * @param window	the frame to work on
   * @see		#setSizeAndLocation(Component, Component)
   */
  public static void setSizeAndLocation(Component window) {
    setSizeAndLocation(window, null);
  }

  /**
   * Determines the left location for a window.
   *
   * @param window	the window to work on
   * @param left	the left position, -1: centered, -2: right-justified
   */
  public static int calcLeftPosition(Component window, int left) {
    int		result;
    Rectangle	bounds;

    bounds = window.getGraphicsConfiguration().getBounds();
    if (left == -1)
      result = bounds.x + (bounds.width - window.getWidth()) / 2;
    else if (left == -2)
      result = bounds.x + bounds.width;
    else
      result = bounds.x + left;
    if (result < 0)
      result = bounds.x;

    return result;
  }

  /**
   * Determines the top location for a window.
   *
   * @param window	the window to work on
   * @param top		the top position, -1: centered, -2: bottom-justified
   */
  public static int calcTopPosition(Component window, int top) {
    int		result;
    Rectangle	bounds;

    bounds = window.getGraphicsConfiguration().getBounds();
    if (top == -1)
      result = bounds.y + (bounds.height - window.getHeight()) / 2;
    else if (top == -2)
      result = bounds.y + bounds.height;
    else
      result = bounds.y + top;
    if (result < 0)
      result = bounds.y;

    return result;
  }

  /**
   * Sets size and location stored in the props file.
   *
   * @param window	the frame to work on
   * @param c		the component to use for lookup in the props file;
   * 			null can be used to bypass the lookup in the props file
   * 			(in that case, "top" and "left" are initialized with 0)
   * @see		#setSizeAndLocation(Component, int, int, Component)
   */
  public static void setSizeAndLocation(Component window, Component c) {
    int		left;
    int		top;

    initializeProperties();

    // determine size first
    setSize(window, c);

    // custom location
    if (c != null) {
      left = scale(m_Properties.getInteger(c.getClass().getName() + ".left", window.getX()));
      left = calcLeftPosition(window, left);
    }
    else {
      left = calcLeftPosition(window, -1);
    }

    if (c != null) {
      top = scale(m_Properties.getInteger(c.getClass().getName() + ".top", window.getY()));
      top = calcTopPosition(window, top);
    }
    else {
      top = calcTopPosition(window, -1);
    }

    setSizeAndLocation(window, top, left, c);
  }

  /**
   * Sets size and location stored in the props file.
   * <br><br>
   * Takes the following parameters from the props file into account as well:
   * MaxPercentHeight, ScreenBorder.Bottom
   *
   * @param window	the frame to work on
   * @param top		the y position
   * @param left	the x position
   * @see		#setSizeAndLocation(Component, int, int, Component)
   */
  public static void setSizeAndLocation(Component window, int top, int left) {
    setSizeAndLocation(window, top, left, null);
  }

  /**
   * Sets size and location stored in the props file.
   * <br><br>
   * Takes the following parameters from the props file into account as well:
   * MaxPercentHeight, ScreenBorder.Bottom
   *
   * @param window	the frame to work on
   * @param top		the y position
   * @param left	the x position
   * @param c		the component to use for lookup in the props file;
   * 			null can be used to bypass the lookup in the props file
   */
  public static void setSizeAndLocation(Component window, int top, int left, Component c) {
    int		width;
    int		height;
    Rectangle	screen;

    initializeProperties();

    // custom size?
    setSize(window, c);
    adjustSize(window);

    // position
    height = window.getHeight();
    width  = window.getWidth();
    screen = getScreenBounds(window);
    if (left + width > screen.width + screen.x)
      left = screen.width - width + screen.x;
    if (top + height > screen.height + screen.y)
      top = screen.height - height + screen.y;

    window.setLocation(left, top);
  }

  /**
   * Returns the actual screen real estate bounds according to
   * ScreenBorder.Top/Left/Bottom/Right in the props file.
   *
   * <pre>
   * +----------------------------+  physical screen
   * |                            |
   * |   +--------------------+   |
   * |   |                    |   |
   * |   |  available screen  |   |
   * |   |                    |   |
   * |   |                    |   |
   * |   +--------------------+   |
   * |                            |
   * |                            |
   * +----------------------------+
   * </pre>
   *
   * @param window	the window to get the graphics config from
   * @return		the "inner" rectangle where we can display stuff
   */
  public static synchronized Rectangle getScreenBounds(Component window) {
    return getScreenBounds(window.getGraphicsConfiguration());
  }

  /**
   * Returns the actual screen real estate bounds according to
   * ScreenBorder.Top/Left/Bottom/Right in the props file.
   *
   * <pre>
   * +----------------------------+  physical screen
   * |                            |
   * |   +--------------------+   |
   * |   |                    |   |
   * |   |  available screen  |   |
   * |   |                    |   |
   * |   |                    |   |
   * |   +--------------------+   |
   * |                            |
   * |                            |
   * +----------------------------+
   * </pre>
   *
   * @param gc		the graphics config to use
   * @return		the "inner" rectangle where we can display stuff
   */
  public static synchronized Rectangle getScreenBounds(GraphicsConfiguration gc) {
    Rectangle	result;
    int		height;
    int 	width;
    int		top;
    int		left;
    int		bottom;
    int		right;
    Rectangle	bounds;

    initializeProperties();

    bounds  = gc.getBounds();
    top     = scale(m_Properties.getInteger("ScreenBorder.Top", 0));
    left    = scale(m_Properties.getInteger("ScreenBorder.Left", 0));
    bottom  = scale(m_Properties.getInteger("ScreenBorder.Bottom", 0));
    right   = scale(m_Properties.getInteger("ScreenBorder.Right", 0));
    height  = bounds.height - top - bottom;
    width   = bounds.width - left - right;
    top     += bounds.y;
    left    += bounds.x;

    result = new Rectangle(left, top, width, height);

    return result;
  }

  /**
   * Adjusts the size of the window, that it fits onto the screen.
   *
   * @param window	the window to adjust
   * @see		#getScreenBounds(Component)
   */
  public static void adjustSize(Component window) {
    Rectangle	screen;
    double	percHeight;
    double	percWidth;
    int		height;
    int		width;

    screen = getScreenBounds(window);
    height = window.getHeight();
    width  = window.getWidth();

    percHeight = m_Properties.getDouble("MaxWindowHeight", 0.95);
    if ((percHeight <= 0) || (percHeight > 1))
      percHeight = 0.95;
    percWidth = m_Properties.getDouble("MaxWindowWidth", 0.95);
    if ((percWidth <= 0) || (percWidth > 1))
      percWidth = 0.95;

    if (height > (double) screen.height * percHeight)
      height = (int) ((double) screen.height * percHeight);
    if (width > (double) screen.width * percWidth)
      width = (int) ((double) screen.width * percWidth);

    window.setSize(width, height);
    window.validate();
  }

  /**
   * Adjusts the position of the window, that it fits onto the screen.
   *
   * @param window	the window to adjust
   * @see		#getScreenBounds(Component)
   */
  public static void fixPosition(Component window) {
    Point	location;
    Rectangle	bounds;
    double	newLeft;
    double	newTop;

    bounds   = getScreenBounds(window);
    location = window.getLocation();

    // X
    newLeft = (int) location.getX();
    if (location.getX() < bounds.getX())
      newLeft = bounds.getX();
    else if (location.getX() + window.getWidth() > bounds.getX() + bounds.getWidth())
      newLeft = (location.getX() - ((location.getX() + window.getWidth()) - (bounds.getX() + bounds.getWidth())));
    if (newLeft < 0)
      newLeft = 0;

    // Y
    newTop = (int) location.getY();
    if (location.getY() < bounds.getY())
      newTop = bounds.getY();
    else if (location.getY() + window.getHeight() > bounds.getY() + bounds.getHeight())
      newTop = (int) (location.getY() - ((location.getY() + window.getHeight()) - (bounds.getY() + bounds.getHeight())));
    if (newTop < 0)
      newTop = 0;

    window.setLocation((int) newLeft, (int) newTop);
  }

  /**
   * Returns the relative top Y position in its screen.
   *
   * @param window	the window to determine the top Y for
   * @return		the top Y
   */
  public int getRelativeTop(Window window) {
    Rectangle	bounds;

    bounds = window.getGraphicsConfiguration().getBounds();
    return bounds.y - window.getBounds().y;
  }

  /**
   * Returns the relative left X position in its screen.
   *
   * @param window	the window to determine the left X for
   * @return		the left X
   */
  public int getRelativeLeft(Window window) {
    Rectangle	bounds;

    bounds = window.getGraphicsConfiguration().getBounds();
    return bounds.x - window.getBounds().x;
  }

  /**
   * Returns the startup script, if any, for the given component.
   *
   * @param c		the component to look for a startup script for
   * @return		the script file or null if none listed or none-existing
   */
  public static File getStartupScript(Component c) {
    File	result;
    String	key;
    String	script;

    initializeProperties();

    result = null;

    key = c.getClass().getName() + ".script";
    if (m_Properties.hasKey(key)) {
      script = m_Properties.getPath(key);
      result = new File(script);
      if (!result.exists()) {
	LOGGER.severe(
	    "Startup script '" + script
		+ "' listed for component '" + c.getClass().getName()
		+ "' does not exist - ignored!");
	result = null;
      }
    }

    return result;
  }

  /**
   * Returns the string value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static String getString(String key, String defValue) {
    initializeProperties();

    return m_Properties.getProperty(key, defValue);
  }

  /**
   * Returns the string value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static String getString(Class cls, String key, String defValue) {
    return getString(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the integer value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Integer getInteger(String key, Integer defValue) {
    initializeProperties();

    return m_Properties.getInteger(key, defValue);
  }

  /**
   * Returns the integer value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Integer getInteger(Class cls, String key, Integer defValue) {
    return getInteger(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the color value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Color getColor(String key, Color defValue) {
    initializeProperties();

    return m_Properties.getColor(key, defValue);
  }

  /**
   * Returns the color value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Color getColor(Class cls, String key, Color defValue) {
    return getColor(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the double value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Double getDouble(String key, Double defValue) {
    initializeProperties();

    return m_Properties.getDouble(key, defValue);
  }

  /**
   * Returns the double value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Double getDouble(Class cls, String key, Double defValue) {
    return getDouble(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the boolean value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Boolean getBoolean(String key, Boolean defValue) {
    initializeProperties();

    return m_Properties.getBoolean(key, defValue);
  }

  /**
   * Returns the boolean value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Boolean getBoolean(Class cls, String key, Boolean defValue) {
    return getBoolean(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the Font value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Font getFont(String key, Font defValue) {
    initializeProperties();

    return m_Properties.getFont(key, defValue);
  }

  /**
   * Returns the Font value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Font getFont(Class cls, String key, Font defValue) {
    return getFont(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the OptionHandler value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static OptionHandler getOptionHandler(String key, OptionHandler defValue) {
    initializeProperties();

    try {
      return OptionUtils.forCommandLine(OptionHandler.class, m_Properties.getProperty(key, OptionUtils.getCommandLine(defValue)));
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to get option handler for key: " + key, e);
    }

    return null;
  }

  /**
   * Returns the OptionHandler value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static OptionHandler getOptionHandler(Class cls, String key, OptionHandler defValue) {
    return getOptionHandler(cls.getName() + "." + key, defValue);
  }

  /**
   * Tries to determine the parent this panel is part of.
   *
   * @param cont	the container to get the parent for
   * @param parentClass	the class of the parent to obtain
   * @return		the parent if one exists or null if not
   */
  public static Object getParent(Container cont, Class parentClass) {
    Container	result;
    Container	parent;

    result = null;

    parent = cont;
    while (parent != null) {
      if (parentClass.isInstance(parent)) {
	result = parent;
	break;
      }
      else {
	if (parent instanceof KnownParentSupporter) {
	  if (((KnownParentSupporter) parent).getKnownParent() != null)
	    parent = ((KnownParentSupporter) parent).getKnownParent();
	  else
	    parent = parent.getParent();
	}
	else {
	  parent = parent.getParent();
	}
      }
    }

    return result;
  }

  /**
   * Tries to determine the frame the container is part of.
   *
   * @param cont	the container to get the frame for
   * @return		the parent frame if one exists or null if not
   */
  public static Frame getParentFrame(Container cont) {
    return (Frame) getParent(cont, Frame.class);
  }

  /**
   * Tries to determine the frame the component is part of.
   *
   * @param comp	the component to get the frame for
   * @return		the parent frame if one exists or null if not
   */
  public static Frame getParentFrame(Component comp) {
    if (comp instanceof Container)
      return (Frame) getParent((Container) comp, Frame.class);
    else
      return null;
  }

  /**
   * Tries to determine the dialog this container is part of.
   *
   * @param cont	the container to get the dialog for
   * @return		the parent dialog if one exists or null if not
   */
  public static Dialog getParentDialog(Container cont) {
    return (Dialog) getParent(cont, Dialog.class);
  }

  /**
   * Tries to determine the dialog this component is part of.
   *
   * @param comp	the component to get the dialog for
   * @return		the parent dialog if one exists or null if not
   */
  public static Dialog getParentDialog(Component comp) {
    if (comp instanceof Container)
      return (Dialog) getParent((Container) comp, Dialog.class);
    else
      return null;
  }

  /**
   * Tries to determine the internal frame this container is part of.
   *
   * @param cont	the container to start with
   * @return		the parent internal frame if one exists or null if not
   */
  public static JInternalFrame getParentInternalFrame(Container cont) {
    return (JInternalFrame) getParent(cont, JInternalFrame.class);
  }

  /**
   * Tries to determine the internal frame this component is part of.
   *
   * @param comp	the component to start with
   * @return		the parent internal frame if one exists or null if not
   */
  public static JInternalFrame getParentInternalFrame(Component comp) {
    if (comp instanceof Container)
      return (JInternalFrame) getParent((Container) comp, JInternalFrame.class);
    else
      return null;
  }

  /**
   * Tries to determine the child window/frame this container is part of.
   *
   * @param cont	the container to get the child window/frame for
   * @return		the parent child window/frame if one exists or null if not
   */
  public static Child getParentChild(Container cont) {
    return (Child) getParent(cont, Child.class);
  }

  /**
   * Tries to determine the child window/frame this component is part of.
   *
   * @param comp	the component to get the child window/frame for
   * @return		the parent child window/frame if one exists or null if not
   */
  public static Child getParentChild(Component comp) {
    if (comp instanceof Container)
      return (Child) getParent((Container) comp, Child.class);
    else
      return null;
  }

  /**
   * Tries to determine the component this panel is part of in this order:
   * 1. dialog, 2. child, 3. frame.
   *
   * @param comp	the component to get the parent component for, must
   * 			be a container actually
   * @return		the parent component if one exists or null if not
   * @see		#getParentDialog(Container)
   * @see		#getParentChild(Container)
   * @see		#getParentFrame(Container)
   */
  public static Component getParentComponent(Component comp) {
    Component	result;
    Container	cont;

    if (comp == null)
      return null;

    if (comp instanceof Container)
      cont = (Container) comp;
    else
      return null;

    result = getParentDialog(cont);
    if (result == null)
      result = (Component) getParentChild(cont);
    if (result == null)
      result = getParentFrame(cont);

    return result;
  }

  /**
   * Closes the parent dialog/frame of this container.
   *
   * @param cont	the container to close the parent for
   */
  public static void closeParent(Container cont) {
    Dialog		dialog;
    Frame		frame;
    JFrame		jframe;
    JInternalFrame	jintframe;
    int		i;
    WindowListener[] 	listeners;
    WindowEvent	event;

    if (getParentDialog(cont) != null) {
      dialog = getParentDialog(cont);
      dialog.setVisible(false);
    }
    else if (getParentFrame(cont) != null) {
      jintframe = getParentInternalFrame(cont);
      if (jintframe != null) {
	jintframe.doDefaultCloseAction();
      }
      else {
	frame = getParentFrame(cont);
	if (frame instanceof JFrame) {
	  jframe = (JFrame) frame;
	  if (jframe.getDefaultCloseOperation() == JFrame.HIDE_ON_CLOSE)
	    jframe.setVisible(false);
	  else if (jframe.getDefaultCloseOperation() == JFrame.DISPOSE_ON_CLOSE)
	    jframe.dispose();
	  else if (jframe.getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE)
	    System.exit(0);

	  // notify listeners
	  listeners = jframe.getWindowListeners();
	  event     = new WindowEvent(jframe, WindowEvent.WINDOW_CLOSED);
	  for (i = 0; i < listeners.length; i++)
	    listeners[i].windowClosed(event);
	}
	else {
	  frame.dispose();
	}
      }
    }
  }

  /**
   * Attempts to bring the enclosing window to the front. Note: does not
   * work on all platforms.
   *
   * @param cont	the container whose parent window to bring to front
   */
  public static void toFront(final Container cont) {
    Runnable	run;

    run = new Runnable() {
      @Override
      public void run() {
	if (getParentDialog(cont) != null)
	  getParentDialog(cont).toFront();
	else if (getParentFrame(cont) != null)
	  getParentFrame(cont).toFront();
	else if (getParentInternalFrame(cont) != null)
	  getParentInternalFrame(cont).toFront();
      }
    };

    SwingUtilities.invokeLater(run);
  }

  /**
   * Returns the look'n'feel classname from the props file.
   *
   * @return		the classname
   */
  public static String getLookAndFeel() {
    return getString("LookAndFeel", UIManager.getCrossPlatformLookAndFeelClassName());
  }

  /**
   * Installs the specified look'n'feel.
   *
   * @param classname	the classname of the look'n'feel to install
   * @return		true if successfully installed
   */
  public static boolean setLookAndFeel(String classname) {
    boolean	result;

    try {
      UIManager.setLookAndFeel(classname);
      result = true;
    }
    catch (Exception e) {
      result = false;
      LOGGER.severe("Can't set look & feel:" + e);
    }

    return result;
  }

  /**
   * Suggests mnemonics for the given labels.
   *
   * @param labels	the labels to set the mnemonics for
   * @return		the mnemonics
   */
  public static char[] getMnemonics(String[] labels) {
    char[]					result;
    String[]					strLabels;
    Hashtable<Character,HashSet<Integer>>	charIndices;
    int						i;
    int						n;
    char					ch;
    Enumeration<Character>			enm;
    HashSet<Integer>				processedIndices;
    int						numLabels;

    result = new char[labels.length];

    // determine letters and numbers per label
    strLabels = new String[labels.length];
    numLabels = 0;
    for (i = 0; i < labels.length; i++) {
      strLabels[i] = labels[i].toLowerCase().replaceAll("[^a-z0-9]", "");
      if (strLabels[i].length() == 0)
	strLabels[i] = null;
      else
	numLabels++;
    }

    // determine counts of characters across the labels
    charIndices = new Hashtable<>();
    for (i = 0; i < ALLOWED_MNEMONICS.length(); i++) {
      ch = ALLOWED_MNEMONICS.charAt(i);
      for (n = 0; n < strLabels.length; n++) {
	if (strLabels[n] == null)
	  continue;
	if (strLabels[n].indexOf(ch) > -1) {
	  if (!charIndices.containsKey(ch))
	    charIndices.put(ch, new HashSet<>());
	  charIndices.get(ch).add(n);
	}
      }
    }

    // set the mnemonics for the labels which are the most unique
    i                = 0;
    processedIndices = new HashSet<>();
    do {
      i++;
      enm = charIndices.keys();
      while (enm.hasMoreElements()) {
	ch = enm.nextElement();
	if (charIndices.get(ch).size() == i) {
	  for (Integer index: charIndices.get(ch)) {
	    if (index < 0)
	      continue;
	    if (strLabels[index] == null)
	      continue;
	    result[index]    = ch;
	    strLabels[index] = null;
	  }

	  // flag indices as 'processed'
	  processedIndices.addAll(charIndices.get(ch));
	  processedIndices.remove(-1);
	}
      }
    }
    while (processedIndices.size() != numLabels);

    return result;
  }

  /**
   * Checks the caption whether an underscore "_" is present to indicate
   * that the following character is to act as mnemonic.
   *
   * @param caption	the caption to analyze
   * @return		true if an underscore is present
   * @see		#MNEMONIC_INDICATOR
   */
  public static boolean hasMnemonic(String caption) {
    return (caption.indexOf(MNEMONIC_INDICATOR) > -1);
  }

  /**
   * Returns the mnemonic for this caption, preceded by an underscore "_".
   *
   * @param caption	the caption to extract
   * @return		the extracted mnemonic, \0 if none available
   * @see		#MNEMONIC_INDICATOR
   */
  public static char getMnemonic(String caption) {
    int		pos;

    pos = caption.indexOf(MNEMONIC_INDICATOR);
    if ((pos > -1) && (pos < caption.length() - 1))
      return caption.charAt(pos + 1);
    else
      return '\0';
  }

  /**
   * Removes the mnemonic indicator in this caption.
   *
   * @param caption	the caption to process
   * @return		the processed caption
   * @see		#MNEMONIC_INDICATOR
   */
  public static String stripMnemonic(String caption) {
    if (caption == null)
      return null;
    else
      return caption.replace("" + MNEMONIC_INDICATOR, "");
  }

  /**
   * Displays an error message with the default title "Error".
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   * @param t 		the exception to append to the message
   */
  public static void showErrorMessage(Component parent, String msg, Throwable t) {
    showErrorMessage(parent, msg + "\n" + LoggingHelper.throwableToString(t), "Error");
  }

  /**
   * Displays an error message with the default title "Error".
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   */
  public static void showErrorMessage(Component parent, String msg) {
    showErrorMessage(parent, msg, "Error");
  }

  /**
   * Displays an error message.
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   * @param title	the title of the error message
   * @param t 		the exception to append to the message
   */
  public static void showErrorMessage(Component parent, final String msg, Throwable t, String title) {
    showErrorMessage(parent, msg + "\n" + LoggingHelper.throwableToString(t), title);
  }

  /**
   * Displays an error message.
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   * @param title	the title of the error message
   */
  public static void showErrorMessage(Component parent, final String msg, String title) {
    final ApprovalDialog	dlg;
    String[]			lines;
    int				height;
    ErrorMessagePanel		errorPanel;

    parent = GUIHelper.getParentComponent(parent);

    lines  = msg.split("\n");
    height = Math.min(350, (lines.length + 1) * 20);
    if (parent instanceof Frame)
      dlg = ApprovalDialog.getDialog((Frame) parent, true);
    else if (parent instanceof Dialog)
      dlg = ApprovalDialog.getDialog((Dialog) parent, ModalityType.APPLICATION_MODAL);
    else
      dlg = ApprovalDialog.getDialog((Dialog) null, ModalityType.APPLICATION_MODAL);
    dlg.setTitle(title);
    dlg.setApproveCaption("Close");
    dlg.setApproveMnemonic('l');
    dlg.setCancelVisible(false);
    dlg.setDiscardVisible(false);
    dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dlg.setIconImage(ImageManager.getIcon("stop.gif").getImage());

    errorPanel = new ErrorMessagePanel();
    errorPanel.setTitle(title);
    errorPanel.setErrorMessage(msg);
    errorPanel.setLineWrap(false);
    dlg.getButtonsPanel(true).add(errorPanel.getConsoleCheckBox());
    dlg.getContentPane().add(errorPanel, BorderLayout.CENTER);
    dlg.setJMenuBar(errorPanel.getMenuBar());

    dlg.pack();
    dlg.setSize(
	getInteger("DefaultSmallDialog.Width", 600),
	Math.min(dlg.getHeight() + height, (int) (getScreenBounds(dlg).height * 0.5)));
    errorPanel.setErrorMessage(msg);
    dlg.setLocationRelativeTo(parent);
    dlg.getApproveButton().requestFocusInWindow();
    dlg.setVisible(true);
  }

  /**
   * Displays an information message with the default title "Information".
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the information message to display
   */
  public static void showInformationMessage(Component parent, String msg) {
    showInformationMessage(parent, msg, "Information");
  }

  /**
   * Displays an information message.
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the information message to display
   * @param title	the title of the information message
   */
  public static void showInformationMessage(Component parent, String msg, String title) {
    showInformationMessage(parent, msg, title, false, null);
  }

  /**
   * Displays an information message.
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the information message to display
   * @param title	the title of the information message
   * @param html 	whether to display HTML or plain text
   * @param size 	the size of the dialog, can be null
   */
  public static void showInformationMessage(Component parent, String msg, String title, boolean html, Dimension size) {
    final ApprovalDialog	dlg;
    String[]			lines;
    int				height;
    TextPanel 			textPanel;
    BaseHtmlEditorPane 		htmlPanel;

    parent = GUIHelper.getParentComponent(parent);

    lines  = msg.split("\n");
    height = Math.min(350, (lines.length + 1) * 20);
    if (parent instanceof Frame)
      dlg = ApprovalDialog.getDialog((Frame) parent, true);
    else if (parent instanceof Dialog)
      dlg = ApprovalDialog.getDialog((Dialog) parent, ModalityType.DOCUMENT_MODAL);
    else
      dlg = ApprovalDialog.getDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dlg.setTitle(title);
    dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dlg.setIconImage(ImageManager.getIcon("information.png").getImage());
    htmlPanel = null;
    textPanel = null;
    if (html) {
      htmlPanel = new BaseHtmlEditorPane();
      htmlPanel.addDefaultHyperlinkListener();
      dlg.getContentPane().add(new BaseScrollPane(htmlPanel), BorderLayout.CENTER);
    }
    else {
      textPanel = new TextPanel();
      textPanel.setTitle(title);
      textPanel.setEditable(false);
      textPanel.setLineWrap(true);
      dlg.getContentPane().add(textPanel, BorderLayout.CENTER);
    }
    dlg.pack();
    if (size == null)
      dlg.setSize(
	  getInteger("DefaultSmallDialog.Width", 600),
	  Math.min(dlg.getHeight() + height, (int) (getScreenBounds(dlg).height * 0.5)));
    else
      dlg.setSize(size);
    dlg.setLocationRelativeTo(parent);
    if (html)
      htmlPanel.setText(msg);
    else
      textPanel.setContent(msg);
    dlg.getApproveButton().requestFocusInWindow();
    dlg.setVisible(true);
  }

  /**
   * Displays a confirmation dialog (yes/no/cancel) with the default title "Confirm".
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   * @return		the selected option
   * @see		ApprovalDialog#APPROVE_OPTION
   * @see		ApprovalDialog#DISCARD_OPTION
   * @see		ApprovalDialog#CANCEL_OPTION
   */
  public static int showConfirmMessage(Component parent, String msg) {
    return showConfirmMessage(parent, msg, "Confirm");
  }

  /**
   * Displays a confirmation dialog (yes/no/cancel).
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   * @param title	the title of the error message
   * @return		the selected option
   * @see		ApprovalDialog#APPROVE_OPTION
   * @see		ApprovalDialog#DISCARD_OPTION
   * @see		ApprovalDialog#CANCEL_OPTION
   */
  public static int showConfirmMessage(Component parent, String msg, String title) {
    return showConfirmMessage(parent, null, msg, title);
  }

  /**
   * Displays a confirmation dialog (yes/no/cancel).
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param header	the text explaining the message, null to ignore
   * @param msg		the error message to display
   * @param title	the title of the error message
   * @return		the selected option
   * @see		ApprovalDialog#APPROVE_OPTION
   * @see		ApprovalDialog#DISCARD_OPTION
   * @see		ApprovalDialog#CANCEL_OPTION
   */
  public static int showConfirmMessage(Component parent, String header, String msg, String title) {
    return showConfirmMessage(parent, header, msg, title, null, null, null);
  }

  /**
   * Displays a confirmation dialog (yes/no/cancel).
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param header	the text explaining the message, null to ignore
   * @param msg		the error message to display
   * @param title	the title of the error message
   * @param labelYes	the label for the "Yes" button, null to use default
   * @param labelNo	the label for the "No" button, null to use default
   * @param labelCancel	the label for the "Cancel" button, null to use default
   * @return		the selected option
   * @see		ApprovalDialog#APPROVE_OPTION
   * @see		ApprovalDialog#DISCARD_OPTION
   * @see		ApprovalDialog#CANCEL_OPTION
   */
  public static int showConfirmMessage(Component parent, String header, String msg, String title, String labelYes, String labelNo, String labelCancel) {
    return showConfirmMessage(parent, header, msg, title, labelYes, labelNo, labelCancel, null);
  }

  /**
   * Displays a confirmation dialog (yes/no/cancel).
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param header	the text explaining the message, null to ignore
   * @param msg		the error message to display
   * @param title	the title of the error message
   * @param labelYes	the label for the "Yes" button, null to use default
   * @param labelNo	the label for the "No" button, null to use default
   * @param labelCancel	the label for the "Cancel" button, null to use default
   * @param comm        for communicating with the dialog, can be null
   * @return		the selected option
   * @see		ApprovalDialog#APPROVE_OPTION
   * @see		ApprovalDialog#DISCARD_OPTION
   * @see		ApprovalDialog#CANCEL_OPTION
   */
  public static int showConfirmMessage(Component parent, String header, String msg, String title, String labelYes, String labelNo, String labelCancel, DialogCommunication comm) {
    final ApprovalDialog	dlg;
    Component			pparent;
    String[]			lines;
    int				height;
    TextPanel			editor;
    Long			sync;

    pparent = GUIHelper.getParentComponent(parent);

    lines  = msg.split("\n");
    height = Math.min(350, (lines.length + 1) * 20);
    if (comm == null) {
      if (pparent instanceof Dialog)
	dlg = ApprovalDialog.getDialog((Dialog) pparent, ModalityType.DOCUMENT_MODAL);
      else
	dlg = ApprovalDialog.getDialog((Frame) pparent, true);
    }
    else {
      if (pparent instanceof Dialog)
	dlg = ApprovalDialog.getDialog((Dialog) pparent, ModalityType.MODELESS);
      else
	dlg = ApprovalDialog.getDialog((Frame) pparent, false);
    }
    dlg.setTitle(title);
    dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dlg.setIconImage(ImageManager.getIcon("question.png").getImage());
    if (labelYes != null)
      dlg.setApproveCaption(labelYes);
    dlg.setApproveVisible(true);
    if (labelNo != null)
      dlg.setDiscardCaption(labelNo);
    dlg.setDiscardVisible(true);
    if (labelCancel != null)
      dlg.setCancelCaption(labelCancel);
    dlg.setCancelVisible(true);
    editor = new TextPanel();
    editor.setTitle(title);
    editor.setEditable(false);
    editor.setLineWrap(true);
    if (header != null)
      editor.setInfoText(header);
    dlg.getContentPane().add(editor, BorderLayout.CENTER);
    dlg.pack();
    dlg.setSize(
	getInteger("DefaultSmallDialog.Width", 600),
	Math.min(dlg.getHeight() + height, (int) (getScreenBounds(dlg).height * 0.5)));
    dlg.setLocationRelativeTo(pparent);
    editor.setContent(msg);
    dlg.getCancelButton().requestFocusInWindow();
    dlg.setVisible(true);

    if (comm != null) {
      sync = UniqueIDs.nextLong();
      // wait till dialog visible
      while (!dlg.isVisible()) {
	try {
	  synchronized (sync) {
	    sync.wait(10);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
      // wait till dialog closed
      while (dlg.isVisible() && !comm.isCloseRequested()) {
	try {
	  synchronized (sync) {
	    sync.wait(100);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }

      if (comm.isCloseRequested())
	dlg.setVisible(false);
    }

    return dlg.getOption();
  }

  /**
   * A simple dialog for entering a string.
   *
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Enter value" in that case)
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg) {
    return showInputDialog(parent, msg, null);
  }

  /**
   * A simple dialog for entering a string.
   *
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Enter value" in that case)
   * @param initial	the initial selection, can be null
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial) {
    return showInputDialog(parent, msg, initial, (String) null);
  }

  /**
   * A simple dialog for entering a string.
   *
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Enter value" in that case)
   * @param initial	the initial selection, can be null
   * @param title	the title of the input dialog, can be null (uses "Enter value" in that case)
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String title) {
    return showInputDialog(parent, msg, initial, title, null);
  }

  /**
   * A simple dialog for entering a string.
   * If "comm" is null simple modal dialogs are used, otherwise modeless ones
   * with blocking till dialog closed (or closing requested via communication object).
   *
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Enter value" in that case)
   * @param initial	the initial selection, can be null
   * @param title	the title of the input dialog, can be null (uses "Enter value" in that case)
   * @param comm        for communicating with the dialog, can be null
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String title, DialogCommunication comm) {
    return showInputDialog(parent, msg, initial, title, comm, 20, 1);
  }

  /**
   * A simple dialog for entering a string.
   * If "comm" is null simple modal dialogs are used, otherwise modeless ones
   * with blocking till dialog closed (or closing requested via communication object).
   *
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Enter value" in that case)
   * @param initial	the initial selection, can be null
   * @param title	the title of the input dialog, can be null (uses "Enter value" in that case)
   * @param comm        for communicating with the dialog, can be null
   * @param minCols 	the minimum number of columns in the text box
   * @param minRows 	the minimum number of rows in the text box
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String title, DialogCommunication comm, int minCols, int minRows) {
    JPanel			panelAll;
    JPanel			panel;
    JLabel			label;
    final BaseTextArea		textValue;
    final ApprovalDialog	dialog;
    Component			pparent;
    Long                        sync;
    int				rows;
    int				cols;
    String[]			lines;

    if (initial == null)
      initial = "";
    if ((title == null) || (title.isEmpty()))
      title = "Enter value";
    if ((msg == null) || (msg.isEmpty()))
      msg = "Enter value";

    pparent = GUIHelper.getParentComponent(parent);
    if (comm == null) {
      if (pparent instanceof Dialog)
	dialog = ApprovalDialog.getDialog((Dialog) pparent, ModalityType.DOCUMENT_MODAL);
      else
	dialog = ApprovalDialog.getDialog((Frame) pparent, true);
    }
    else {
      if (pparent instanceof Dialog)
	dialog = ApprovalDialog.getDialog((Dialog) pparent, ModalityType.MODELESS);
      else
	dialog = ApprovalDialog.getDialog((Frame) pparent, false);
    }
    dialog.setTitle(title);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);

    rows = 1;
    cols = 20;
    if (!initial.isEmpty()) {
      lines = initial.split("\n");
      // rows
      if (lines.length > 1)
	rows = lines.length;
      if (rows > 5)
	rows = 5;
      // cols
      for (String line: lines) {
	if (line.length() > cols)
	  cols = line.length();
      }
      if (cols > 40)
	cols = 40;
    }
    if (cols < minCols)
      cols = minCols;
    if (rows < minRows)
      rows = minRows;
    textValue = new BaseTextArea(rows, cols);
    textValue.setToolTipText("Use <Ctrl-Enter> for adding a new line");
    textValue.setText(initial);
    if (!initial.isEmpty()) {
      textValue.setSelectionStart(0);
      textValue.setSelectionEnd(initial.length());
    }
    textValue.setLineWrap(true);
    textValue.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  if (KeyUtils.isNoneDown(e.getModifiersEx())) {
	    e.consume();
	    dialog.getApproveButton().doClick();
	  }
	  else if (KeyUtils.isCtrlDown(e.getModifiersEx())) {
	    e.consume();
	    textValue.append("\n");
	  }
	}
	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  e.consume();
	  dialog.getCancelButton().doClick();
	}
	if (!e.isConsumed())
	  super.keyPressed(e);
      }
    });

    panelAll = new JPanel(new BorderLayout(5, 5));
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    label = new JLabel(msg);
    panelAll.add(label, BorderLayout.NORTH);

    panel = new JPanel(new BorderLayout());
    panel.add(new BaseScrollPane(textValue), BorderLayout.CENTER);
    panelAll.add(panel, BorderLayout.CENTER);

    dialog.getContentPane().add(panelAll, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (comm != null) {
      sync = UniqueIDs.nextLong();
      // wait till dialog visible
      while (!dialog.isVisible()) {
	try {
	  synchronized (sync) {
	    sync.wait(10);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
      // wait till dialog closed
      while (dialog.isVisible() && !comm.isCloseRequested()) {
	try {
	  synchronized (sync) {
	    sync.wait(100);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }

      if (comm.isCloseRequested())
	dialog.setVisible(false);
    }

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION)
      return textValue.getText();
    else
      return null;
  }

  /**
   * A simple dialog for selecting a string.
   *
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Select value" in that case)
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String[] options) {
    return showInputDialog(parent, msg, initial, options, null);
  }

  /**
   * A simple dialog for selecting a string.
   *
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Select value" in that case)
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @param title	the title of the input dialog, can be null (uses "Select value" in that case)
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String[] options, String title) {
    return showInputDialog(parent, msg, initial, options, InputDialogMultiValueSelection.COMBOBOX, title);
  }

  /**
   * A simple dialog for selecting a string.
   * If "comm" is null simple modal dialogs are used, otherwise modeless ones
   * with blocking till dialog closed (or closing requested via communication object).
   *
   * @param parent	the parent for this dialog
   * @param title	the title of the input dialog, can be null (uses "Select value" in that case)
   * @param msg		the message to display, can be null (uses "Select value" in that case)
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @param comm        for communicating with the caller, can be null
   * @return		the value entered, null if cancelled
   */
  protected static String showInputDialogComboBox(Component parent, String msg, String initial, String[] options, String title, DialogCommunication comm) {
    JPanel			panelAll;
    JPanel			panelCombo;
    JPanel			panel;
    JLabel			label;
    BaseComboBox<String>	combobox;
    final ApprovalDialog	dialog;
    Component			pparent;
    Long                        sync;

    if (initial == null)
      initial = "";
    if ((title == null) || (title.isEmpty()))
      title = "Select value";
    if ((msg == null) || (msg.isEmpty()))
      msg = "Select value";

    pparent = GUIHelper.getParentComponent(parent);
    if (comm == null) {
      if (pparent instanceof Dialog)
	dialog = ApprovalDialog.getDialog((Dialog) pparent, ModalityType.DOCUMENT_MODAL);
      else
	dialog = ApprovalDialog.getDialog((Frame) pparent, true);
    }
    else {
      if (pparent instanceof Dialog)
	dialog = ApprovalDialog.getDialog((Dialog) pparent, ModalityType.MODELESS);
      else
	dialog = ApprovalDialog.getDialog((Frame) pparent, false);
    }
    dialog.setTitle(title);

    combobox = new BaseComboBox<>(options);
    if (!initial.isEmpty())
      combobox.setSelectedItem(initial);
    else
      combobox.setSelectedIndex(0);
    combobox.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  e.consume();
	  dialog.getApproveButton().doClick();
	}
	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  e.consume();
	  dialog.getCancelButton().doClick();
	}
	else {
	  super.keyPressed(e);
	}
      }
    });
    panelCombo = new JPanel(new BorderLayout());
    panelCombo.add(combobox, BorderLayout.NORTH);

    panelAll = new JPanel(new BorderLayout(5, 0));
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    label = new JLabel(msg);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panelAll.add(panel, BorderLayout.NORTH);

    panel = new JPanel(new BorderLayout());
    panel.add(panelCombo, BorderLayout.CENTER);
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panelAll.add(panel, BorderLayout.CENTER);

    dialog.getContentPane().add(panelAll, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (comm != null) {
      sync = UniqueIDs.nextLong();
      // wait till dialog visible
      while (!dialog.isVisible()) {
	try {
	  synchronized (sync) {
	    sync.wait(10);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
      // wait till dialog closed
      while (dialog.isVisible() && !comm.isCloseRequested()) {
	try {
	  synchronized (sync) {
	    sync.wait(100);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }

      if (comm.isCloseRequested())
	dialog.setVisible(false);
    }

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION)
      return (String) combobox.getSelectedItem();
    else
      return null;
  }

  /**
   * A simple dialog for selecting a string by clicking on a button.
   * If "comm" is null simple modal dialogs are used, otherwise modeless ones
   * with blocking till dialog closed (or closing requested via communication object).
   *
   * @param parent	the parent for this dialog
   * @param title	the title of the input dialog, can be null (uses "Select value" in that case)
   * @param msg		the message to display, can be null (uses "Select value" in that case)
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @param horizontal 	whether the buttons are horizontal or vertical
   * @param comm        for communicating with the caller, can be null
   * @return		the value entered, null if cancelled
   */
  protected static String showInputDialogButtons(Component parent, String msg, String initial, String[] options, String title, boolean horizontal, DialogCommunication comm) {
    Component		pparent;
    final BaseDialog	dialog;
    JPanel		panelButtons;
    JPanel		panel;
    JPanel		panelAll;
    JLabel		label;
    final StringBuilder	result;
    Long                sync;
    BaseButton          initialFocus;

    if (initial == null)
      initial = "";
    if ((title == null) || (title.isEmpty()))
      title = "Select value";
    if ((msg == null) || (msg.isEmpty()))
      msg = "Select alue";

    pparent = GUIHelper.getParentComponent(parent);
    if (comm == null) {
      if (pparent instanceof Dialog)
	dialog = new BaseDialog((Dialog) pparent, ModalityType.DOCUMENT_MODAL);
      else
	dialog = new BaseDialog((Frame) pparent, true);
    }
    else {
      if (pparent instanceof Dialog)
	dialog = new BaseDialog((Dialog) pparent, ModalityType.MODELESS);
      else
	dialog = new BaseDialog((Frame) pparent, false);
    }
    dialog.setTitle(title);

    panelAll = new JPanel(new BorderLayout(5, 5));
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    label = new JLabel(msg);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panelAll.add(panel, BorderLayout.NORTH);

    result = new StringBuilder();

    if (horizontal)
      panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
    else
      panelButtons = new JPanel(new GridLayout(options.length, 1, 0, 5));
    initialFocus = null;
    for (String option: options) {
      final BaseButton button = new BaseButton(option);
      button.addActionListener((ActionEvent e) -> {
	result.append(button.getText());
	dialog.setVisible(false);
      });
      if (option.equals(initial))
	initialFocus = button;
      panelButtons.add(button);
    }
    panelAll.add(panelButtons, BorderLayout.CENTER);

    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panelAll, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    if (initialFocus != null)
      initialFocus.requestFocusInWindow();
    dialog.setVisible(true);

    if (comm != null) {
      sync = UniqueIDs.nextLong();
      // wait till dialog visible
      while (!dialog.isVisible()) {
	try {
	  synchronized (sync) {
	    sync.wait(10);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
      // wait till dialog closed
      while (dialog.isVisible() && !comm.isCloseRequested()) {
	try {
	  synchronized (sync) {
	    sync.wait(100);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }

      if (comm.isCloseRequested())
	dialog.setVisible(false);
    }

    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }

  /**
   * A simple dialog for entering a string.
   *
   * @param parent	the parent for this dialog
   * @param title	the title of the input dialog, can be null
   * @param msg		the message to display
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @param view	how to display the values
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String[] options, InputDialogMultiValueSelection view, String title) {
    return showInputDialog(parent, msg, initial, options, view, title, null);
  }

  /**
   * A simple dialog for entering a string.
   * If "comm" is null simple modal dialogs are used, otherwise modeless ones
   * with blocking till dialog closed (or closing requested via communication object).
   *
   * @param parent	the parent for this dialog
   * @param title	the title of the input dialog, can be null
   * @param msg		the message to display
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @param view	how to display the values
   * @param comm        for communicating with the dialog, can be null
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String[] options, InputDialogMultiValueSelection view, String title, DialogCommunication comm) {
    switch (view) {
      case COMBOBOX:
	return showInputDialogComboBox(parent, msg, initial, options, title, comm);
      case BUTTONS_HORIZONTAL:
	return showInputDialogButtons(parent, msg, initial, options, title, true, comm);
      case BUTTONS_VERTICAL:
	return showInputDialogButtons(parent, msg, initial, options, title, false, comm);
      default:
	throw new IllegalStateException("Unhandled view type: " + view);
    }
  }

  /**
   * Parses the tool tip and breaks it up into multiple lines if longer
   * than width characters. No limit on number of rows.
   *
   * @param text	the tiptext to parse, can be null
   * @param width	the maximum width
   * @return		the processed tiptext
   */
  public static String processTipText(String text, int width) {
    return processTipText(text, width, -1);
  }

  /**
   * Parses the tool tip and breaks it up into multiple lines if longer
   * than width characters.
   *
   * @param text	the tiptext to parse, can be null
   * @param width	the maximum width
   * @param maxLines	the maximum number of lines to allow, -1 for no limit
   * @return		the processed tiptext
   */
  public static String processTipText(String text, int width, int maxLines) {
    String	result;
    String[]	lines;
    String[]	tmp;
    int		i;

    if (text == null)
      return null;

    result = text;
    if (result.length() > width) {
      result = HtmlUtils.toHTML(text);
      lines  = Utils.breakUp(result, width);
      // too many lines?
      if ((maxLines > -1) && (lines.length > maxLines)) {
	tmp = new String[maxLines + 1];
	for (i = 0; i < maxLines; i++)
	  tmp[i] = lines[i];
	tmp[maxLines] = "...";
	lines = tmp;
      }
      result = processTipText(Utils.flatten(lines, "\n"));
    }

    return result;
  }

  /**
   * Parses the tool tip and turns it into HTML if it contains line breaks.
   *
   * @param text	the tiptext to parse, can be null
   * @return		the processed tiptext
   */
  public static String processTipText(String text) {
    String	result;
    String[]	lines;

    if (text == null)
      return null;

    if (!text.contains("\n"))
      return text;

    lines  = text.split("\n");
    result = Utils.flatten(lines, "<br>");
    result = "<html>" + result + "</html>";

    return result;
  }

  /**
   * Returns the replacements for the keystrokes.
   *
   * @return		the replacements
   */
  protected synchronized static HashMap<String,String> getKeystrokeReplacements() {
    HashMap<String,String>	replacements;
    String			prop;
    String[]			list;
    String[]			parts;

    if (m_KeystrokeReplacements == null) {
      replacements = new HashMap<>();
      prop         = getString("ReplaceKeystrokes", "");
      if (prop.trim().length() > 0) {
	if (prop.contains(","))
	  list = prop.split(",");
	else
	  list = new String[]{prop};
	for (String item: list) {
	  if (item.contains("\t")) {
	    parts = item.split("\t");
	    if (parts.length == 1) {
	      try {
		KeyStroke.getKeyStroke(parts[0]);
		replacements.put(parts[0], "");
	      }
	      catch (Exception e) {
		LOGGER.log(Level.SEVERE, "Failed to parse keystroke replacement (format: 'old<tab>new'): " + item, e);
	      }
	    }
	    else if (parts.length == 2) {
	      try {
		KeyStroke.getKeyStroke(parts[0]);
		KeyStroke.getKeyStroke(parts[1]);
		replacements.put(parts[0], parts[1]);
	      }
	      catch (Exception e) {
		LOGGER.log(Level.SEVERE, "Failed to parse keystroke replacement (format: 'old<tab>new'): " + item, e);
	      }
	    }
	    else {
	      LOGGER.severe("Invalid keystroke replacement (format: 'old<tab>new'): " + item);
	    }
	  }
	  else {
	    LOGGER.severe("Invalid keystroke replacement (format: 'old<tab>new'): " + item);
	  }
	}
      }
      m_KeystrokeReplacements = replacements;
    }

    return m_KeystrokeReplacements;
  }

  /**
   * Processes the keystrokes. For Macs, "ctrl/control" is replaced by "meta".
   * Also, if no "ctrl/control/alt" is present, a "meta" is prefix.
   * All other platforms simply return the string.
   *
   * @param keystroke	the keystroke string to process
   * @return		the (potentially) processed keystroke definition
   */
  public static String processKeyStroke(String keystroke) {
    String			result;
    HashMap<String,String>	replacements;

    result = keystroke;
    if (result != null) {
      result = result.trim();
      replacements = getKeystrokeReplacements();
      if (replacements.containsKey(result))
	result = replacements.get(result).trim();
    }

    return result;
  }

  /**
   * Creates a keystroke from the string.
   *
   * @param keystroke	the keystroke string to turn into a
   * @return		the generated keystroke
   * @see		#processKeyStroke(String)
   */
  public static KeyStroke getKeyStroke(String keystroke) {
    keystroke = processKeyStroke(keystroke);
    if ((keystroke == null) || keystroke.isEmpty())
      return null;
    else
      return KeyStroke.getKeyStroke(keystroke);
  }

  /**
   * Retrieves all components of the specified type starting with
   * the given parent container.
   *
   * @param parent	the container to start the search in
   * @param recursive	whether to recurse into other containers
   * @param exact	whether the class must be exactly this type of merely derived
   * @param onlyFirst	whether to stop search once the first component was found
   * @return		the list of located components
   */
  protected static void findComponents(Container parent, Class type, boolean recursive, boolean exact, List<Component> list, boolean onlyFirst) {
    int		i;
    Component	comp;

    for (i = 0; i < parent.getComponentCount(); i++) {
      comp = parent.getComponent(i);

      // match?
      if (exact) {
	if (comp.getClass().equals(type))
	  list.add(comp);
      }
      else {
	if (ClassLocator.isSubclass(type, comp.getClass()))
	  list.add(comp);
	else if (ClassLocator.hasInterface(type, comp.getClass()))
	  list.add(comp);
      }

      // stop at first hit?
      if (onlyFirst && (list.size() > 0))
	return;

      // search deeper?
      if (recursive && (comp instanceof Container))
	findComponents((Container) comp, type, recursive, exact, list, onlyFirst);
    }
  }

  /**
   * Retrieves all components of the specified type starting with
   * the given parent container.
   *
   * @param parent	the container to start the search in
   * @param recursive	whether to recurse into other containers
   * @param exact	whether the class must be exactly this type of merely derived
   * @return		the list of located components
   */
  public static List<Component> findAllComponents(Container parent, Class type, boolean recursive, boolean exact) {
    ArrayList<Component>	result;

    result = new ArrayList<>();
    findComponents(parent, type, recursive, exact, result, false);

    return result;
  }

  /**
   * Retrieves the first component of the specified type starting with
   * the given parent container.
   *
   * @param parent	the container to start the search in
   * @param recursive	whether to recurse into other containers
   * @param exact	whether the class must be exactly this type of merely derived
   * @return		the located component or null if none found
   */
  public static Component findFirstComponent(Container parent, Class type, boolean recursive, boolean exact) {
    ArrayList<Component>	result;

    result = new ArrayList<>();
    findComponents(parent, type, recursive, exact, result, true);

    if (result.size() == 0)
      return null;
    else
      return result.get(0);
  }

  /**
   * Enables/disables anti-aliasing in the Graphics context.
   *
   * @param g		the graphics context to enable/disable anti-aliasing for
   * @param enable	if true anti-aliasing gets enabled, otherwise disabled
   */
  public static void configureAntiAliasing(Graphics g, boolean enable) {
    if (enable && AntiAliasingEnabled)
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    else
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
  }

  /**
   * Determines the top-level menu (the one sitting inside the JMenuBar) that 
   * is associated with a menu item.
   *
   * @param menuitem	the menu item to get the menu bar for
   * @return		the menu, null if not found
   */
  public static JMenu getTopLevelMenu(JMenuItem menuitem) {
    JMenu	result;
    Container	cont;
    Container	prev;

    result = null;

    cont = menuitem;
    prev = null;
    while (cont != null) {
      if (cont instanceof JPopupMenu) {
	result = (JMenu) prev;
	break;
      }
      prev = cont;
      cont = cont.getParent();
    }

    return result;
  }

  /**
   * Attempts to launch the specified menuitem. Requires to find the overall
   * application frame in order to launch the menu item.
   *
   * @param source	where the menuitem launch originated from
   * @param menuitem	the menuitem class to launch
   * @return		true if successfully launched
   */
  public static boolean launchMenuItem(Container source, Class menuitem) {
    Child			child;
    AbstractBasicMenuItemDefinition mitem;

    child = getParentChild(source);
    if (child == null)
      return false;
    if (child.getParentFrame() == null)
      return false;

    try {
      mitem = (AbstractBasicMenuItemDefinition) menuitem.newInstance();
      mitem.setOwner(child.getParentFrame());
      mitem.launch();
    }
    catch (Exception e) {
      // ignored
      return false;
    }

    return true;
  }

  /**
   * Displays a popup menu.
   *
   * @param menu	the menu to show
   * @param invoker	the invoking component
   * @param e		the absolute positions on screen of the event are used
   */
  public static void showPopupMenu(JPopupMenu menu, Component invoker, MouseEvent e) {
    showPopupMenu(menu, invoker, e.getXOnScreen(), e.getYOnScreen());
  }

  /**
   * Displays a popup menu.
   *
   * @param menu	the menu to show
   * @param invoker	the invoking component
   * @param x		the absolute X position on screen
   * @param y		the absolute Y position on screen
   */
  public static void showPopupMenu(JPopupMenu menu, Component invoker, int x, int y) {
    menu.setInvoker(invoker);
    menu.setLocation(x, y);
    menu.setVisible(true);
  }

  /**
   * Tries to determine the {@link GraphicsConfiguration} that the specified component
   * is located on.
   *
   * @param comp	the component to determine the graphics config for
   * @return		the config, null if failed to determine
   */
  public static GraphicsConfiguration getGraphicsConfiguration(Component comp) {
    if (comp == null)
      return null;

    if (getParentDialog(comp) != null)
      return getParentDialog(comp).getGraphicsConfiguration();
    else if (getParentFrame(comp) != null)
      return getParentFrame(comp).getGraphicsConfiguration();
    else if (getParentInternalFrame(comp) != null)
      return getParentInternalFrame(comp).getGraphicsConfiguration();
    else
      return null;
  }

  /**
   * Tries to determine the {@link GraphicsDevice} that the specified component
   * is located on.
   *
   * @param comp	the component to determine the graphics device for
   * @return		the device, null if failed to determine
   */
  public static GraphicsDevice getGraphicsDevice(Component comp) {
    GraphicsConfiguration	gc;

    gc = getGraphicsConfiguration(comp);
    if (gc == null)
      return null;
    else
      return gc.getDevice();
  }

  /**
   * Returns the default dimensions for a dialog.
   *
   * @return		the default
   */
  public static Dimension getDefaultDialogDimension() {
    return scale(new Dimension(
	getInteger("DefaultDialog.Width", 800),
	getInteger("DefaultDialog.Height", 600)));
  }

  /**
   * Returns the default dimensions for a small dialog.
   *
   * @return		the default
   */
  public static Dimension getDefaultSmallDialogDimension() {
    return scale(new Dimension(
	getInteger("DefaultSmallDialog.Width", 600),
	getInteger("DefaultSmallDialog.Height", 400)));
  }

  /**
   * Returns the default dimensions for a tiny dialog.
   *
   * @return		the default
   */
  public static Dimension getDefaultTinyDialogDimension() {
    return scale(new Dimension(
	getInteger("DefaultTinyDialog.Width", 400),
	getInteger("DefaultTinyDialog.Height", 300)));
  }

  /**
   * Returns the default dimensions for a large dialog.
   *
   * @return		the default
   */
  public static Dimension getDefaultLargeDialogDimension() {
    return scale(new Dimension(
	getInteger("DefaultLargeDialog.Width", 1000),
	getInteger("DefaultLargeDialog.Height", 800)));
  }

  /**
   * Widens the rectangle by 20%.
   *
   * @param size  	the current size
   * @return		the widened rectangle
   */
  public static Dimension makeWider(Dimension size) {
    return makeWider(size, 0.2);
  }

  /**
   * Widens the rectangle by the specified percentage.
   *
   * @param size  	the current size
   * @param percent 	the percentage to make it wider (0-1)
   * @return		the widened rectangle
   */
  public static Dimension makeWider(Dimension size, double percent) {
    return new Dimension((int) (size.width * (1 + percent)), size.height);
  }

  /**
   * Narrows the rectangle by 20%.
   *
   * @param size  	the current size
   * @return		the narrowed rectangle
   */
  public static Dimension makeNarrower(Dimension size) {
    return makeNarrower(size, 0.2);
  }

  /**
   * Narrows the rectangle by the specified percentage.
   *
   * @param size  	the current size
   * @param percent 	the percentage to make it narrower (0-1)
   * @return		the narrowed rectangle
   */
  public static Dimension makeNarrower(Dimension size, double percent) {
    return new Dimension((int) (size.width * (1 - percent)), size.height);
  }

  /**
   * Males the rectangle taller by 20%.
   *
   * @param size  	the current size
   * @return		the taller rectangle
   */
  public static Dimension makeTaller(Dimension size) {
    return makeTaller(size, 0.2);
  }

  /**
   * Makes the rectangle taller by the specified percentage.
   *
   * @param size  	the current size
   * @param percent 	the percentage to make it taller (0-1)
   * @return		the taller rectangle
   */
  public static Dimension makeTaller(Dimension size, double percent) {
    return new Dimension(size.width, (int) (size.height * (1 + percent)));
  }

  /**
   * Makes the rectangle smaller by 20%.
   *
   * @param size  	the current size
   * @return		the smaller rectangle
   */
  public static Dimension makeSmaller(Dimension size) {
    return makeSmaller(size, 0.2);
  }

  /**
   * Makes the rectangle smaller by the specified percentage.
   *
   * @param size  	the current size
   * @param percent 	the percentage to make it smaller (0-1)
   * @return		the smaller rectangle
   */
  public static Dimension makeSmaller(Dimension size, double percent) {
    return new Dimension(size.width, (int) (size.height * (1 - percent)));
  }

  /**
   * Makes the dimensions at least the specified size.
   *
   * @param current	the current dimensions to potentially resize
   * @param min		the minimum dimensions
   * @return 		null if no update necessary, otherwise the new dimensions
   */
  public static Dimension makeAtLeast(Dimension current, Dimension min) {
    boolean	update;
    int		width;
    int		height;

    width   = current.width;
    height  = current.height;
    update  = false;

    if (width < min.width) {
      update = true;
      width = min.width;
    }

    if (height < min.height) {
      update = true;
      height = min.height;
    }

    if (update)
      return new Dimension(width, height);
    else
      return null;
  }

  /**
   * Makes the window at least the specified size.
   *
   * @param window	the window to potentially resize
   * @param min		the minimum dimensions
   * @return 		true if updated
   */
  public static boolean makeAtLeast(Component window, Dimension min) {
    Dimension 	current;
    Dimension	updated;

    updated = makeAtLeast(window.getSize(), min);
    if (updated != null) {
      window.setSize(updated);
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Makes the dimensions at most the specified size.
   *
   * @param current	the current dimensions to potentially resize
   * @param max		the maximum dimensions
   * @return 		null if no update necessary, otherwise the new dimensions
   */
  public static Dimension makeAtMost(Dimension current, Dimension max) {
    boolean	update;
    int		width;
    int		height;

    width   = current.width;
    height  = current.height;
    update  = false;

    if (width > max.width) {
      update = true;
      width = max.width;
    }

    if (height > max.height) {
      update = true;
      height = max.height;
    }

    if (update)
      return new Dimension(width, height);
    else
      return null;
  }

  /**
   * Makes the window at most the specified size.
   *
   * @param window	the window to potentially resize
   * @param max		the maximum dimensions
   * @return 		true if updated
   */
  public static boolean makeAtMost(Component window, Dimension max) {
    Dimension 	updated;
    boolean	update;
    int		width;
    int		height;

    updated = makeAtMost(window.getSize(), max);
    if (updated != null) {
      window.setSize(updated);
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Fits the dimensions between min/max.
   *
   * @param current	the dimensions to adjust
   * @param min		the minimum size, ignored if null
   * @param max		the maximum size, ignored if null
   * @return 		null if no need to update, otherwise the recommended dimensions
   */
  public static Dimension makeFit(Dimension current, Dimension min, Dimension max) {
    Dimension 	result;
    boolean 	update;

    result = null;
    update = false;
    if (min != null) {
      result = makeAtLeast(current, min);
      if (result != null) {
	update  = true;
	current = result;
      }
    }
    if (max != null) {
      result = makeAtMost(current, max);
      if ((result == null) && update)
	result = current;
    }

    return result;
  }

  /**
   * Packs the window, but ensures that it fits between min/max.
   *
   * @param window	the window to adjust
   * @param min		the minimum size, ignored if null
   * @param max		the maximum size, ignored if null
   * @return 		true if updated
   */
  public static boolean pack(Window window, Dimension min, Dimension max) {
    Dimension	updated;
    boolean	update;
    Dimension 	current;
    Dimension 	preferred;
    Dimension 	currentUpdated;
    Dimension 	preferredUpdated;
    Point 	position;
    Dimension 	sizePack;
    Point 	positionPack;
    Dimension 	sizeUpdated;
    Point 	positionUpdated;
    int		diffX;
    int		diffY;

    // ensure that min < max
    if ((min != null) && (max != null)) {
      if (min.width > max.width)
	return false;
      if (min.height > max.height)
	return false;
    }

    position  = window.getLocation();
    current   = window.getSize();
    preferred = window.getPreferredSize();

    currentUpdated   = makeFit(current, min, max);
    preferredUpdated = makeFit(preferred, min, max);

    if (preferredUpdated != null)
      updated = preferredUpdated;
    else
      updated = currentUpdated;

    // adjust position
    if (updated != null) {
      diffX = (updated.width - current.width) / 2;
      diffY = (updated.height - current.height) / 2;
      window.setLocation(position.x - diffX, position.y - diffY);
      window.setSize(updated);
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Rotates the rectangle by 90 degrees.
   *
   * @param rect  	the rectangle
   * @return		the rotated rectangle
   */
  public static Dimension rotate(Dimension rect) {
    return new Dimension(rect.height, rect.width);
  }

  /**
   * Returns the preferred button height.
   *
   * @return		the button height
   */
  public static int getPreferredButtonHeight() {
    return (int) (getInteger("ButtonHeight", 25) * getDisplayScaleFactor());
  }

  /**
   * Returns the preferred textfield height.
   *
   * @return		the textfield height
   */
  public static int getPreferredTextFieldHeight() {
    return (int) (getInteger("TextFieldHeight", 25) * getDisplayScaleFactor());
  }

  /**
   * Returns the preferred spinner height.
   *
   * @return		the spinner height
   */
  public static int getPreferredSpinnerHeight() {
    return (int) (getInteger("SpinnerHeight", 25) * getDisplayScaleFactor());
  }

  /**
   * Returns the preferred checkbox height.
   *
   * @return		the checkbox height
   */
  public static int getPreferredCheckBoxHeight() {
    return (int) (getInteger("CheckBoxHeight", 25) * getDisplayScaleFactor());
  }

  /**
   * Returns the preferred combobox height.
   *
   * @return		the combobox height
   */
  public static int getPreferredComboBoxHeight() {
    return (int) (getInteger("ComboBoxHeight", 25) * getDisplayScaleFactor());
  }

  /**
   * Returns the scale factor for the display.
   *
   * @return		the scale factor
   */
  public static double getDisplayScaleFactor() {
    return getDouble("DisplayScaleFactor", 1.0);
  }

  /**
   * Applies the global scale factor to the size.
   *
   * @param size	the size to scale
   * @return		the scaled size
   */
  public static int scale(int size) {
    return (int) (getDisplayScaleFactor() * size);
  }

  /**
   * Applies the global scale factor to the size.
   *
   * @param size	the size to scale
   * @return		the scaled size
   */
  public static double scale(double size) {
    return getDisplayScaleFactor() * size;
  }

  /**
   * Applies the global scale factor to the size.
   *
   * @param size	the size to scale
   * @return		the scaled size
   */
  public static Dimension scale(Dimension size) {
    double	scale;

    scale = getDisplayScaleFactor();
    return new Dimension((int) (size.width * scale), (int) (size.height * scale));
  }

  /**
   * Returns whether we are running in headless mode.
   *
   * @return		true if in headless mode
   */
  public static boolean isHeadless() {
    return GraphicsEnvironment.isHeadless() || ((HeadlessMode != null) && HeadlessMode);
  }

  /**
   * Sets the headless mode.
   *
   * @param value	true if in headless mode
   */
  public static void setHeadless(boolean value) {
    HeadlessMode = value;
  }

  /**
   * Launches a {@link SwingWorker}, executing the provided {@link Runnable}
   * in the {@link SwingWorker#doInBackground()} method.
   *
   * @param runnable	the code to execute
   * @return		the SwingWorker instance
   */
  public static SwingWorker doInBackground(final Runnable runnable) {
    SwingWorker result;

    result = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	runnable.run();
	return null;
      }
    };
    result.execute();

    return result;
  }

  /**
   * Takes a screenshot of the component.
   *
   * @param comp	the component to take a screenshot of
   * @return		the screenshot
   */
  public static BufferedImage screenshot(JComponent comp) {
    BufferedImage 	result;
    Graphics  		g;

    result = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
    g = result.getGraphics();
    g.setPaintMode();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, comp.getWidth(), comp.getHeight());
    comp.printAll(g);

    return result;
  }
}
