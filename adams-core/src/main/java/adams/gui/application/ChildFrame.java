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
 * ChildFrame.java
 * Copyright (C) 2011-2021 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.application;

import adams.core.CleanUpHandler;
import adams.core.logging.Logger;
import adams.db.DatabaseConnectionHandler;
import adams.gui.core.BaseFrame;
import adams.gui.core.ImageManager;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.UISettings;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Specialized JFrame class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChildFrame
  extends BaseFrame
  implements Child {

  /** for serialization. */
  protected static final long serialVersionUID = 8588293938686425618L;

  /** the parent frame. */
  protected AbstractApplicationFrame m_Parent;

  /** the frame itself. */
  protected ChildFrame m_Self;

  /** whether the dispose method has been called already manually. */
  protected boolean m_DisposeCalled;

  /** the initial title. */
  protected String m_InitialTitle;

  /** the name of the icon to use. */
  protected String m_IconName;

  /** whether a window listener to dispose the frame was added. */
  protected boolean m_DisposeWindowListenerAdded;

  /** the UI settings prefix to use. */
  protected String m_UISettingsPrefix;

  /**
   * Constructs a new frame that knows about its parent.
   *
   * @param parent	the parent frame
   * @param title	the title of the frame
   */
  public ChildFrame(AbstractApplicationFrame parent, String title) {
    this(parent, title, null);
  }

  /**
   * Constructs a new frame that knows about its parent.
   *
   * @param parent	the parent frame
   * @param title	the title of the frame
   * @param icon	the name of the icon to use, null to use default
   */
  public ChildFrame(AbstractApplicationFrame parent, String title, String icon) {
    super(title, parent.getGraphicsConfiguration());

    m_Self                       = this;
    m_Parent                     = parent;
    m_InitialTitle               = title;
    m_DisposeWindowListenerAdded = false;
    m_IconName                   = icon;
    m_UISettingsPrefix           = "";

    performInitialization();
  }

  /**
   * Contains all the initialization steps to perform.
   */
  @Override
  protected void performInitialization() {
    if (getParentFrame() != null)
      super.performInitialization();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DisposeCalled = false;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    ImageIcon		icon;

    super.initGUI();

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowActivated(WindowEvent e) {
        // update title of parent
        if (getParentFrame() != null)
          getParentFrame().createTitle(getTitle());
      }

      @Override
      public void windowClosing(WindowEvent e) {
        cleanUp();
        if (!m_DisposeCalled && (getDefaultCloseOperation() == JFrame.DISPOSE_ON_CLOSE))
          dispose();
      }
    });

    // icon
    if (getParentFrame() != null)
      setIconImage(getParentFrame().getIconImage());
    if (m_IconName != null) {
      if (m_IconName.contains("/"))
        icon = ImageManager.getExternalIcon(m_IconName);
      else
        icon = ImageManager.getIcon(m_IconName);
      if (icon != null)
        setIconImage(icon.getImage());
    }

    // add to parent
    if (getParentFrame() != null)
      getParentFrame().addChildFrame(this);
  }

  /**
   * returns the parent frame, can be null.
   *
   * @return		the parent frame
   */
  public AbstractApplicationFrame getParentFrame() {
    return m_Parent;
  }

  /**
   * Sets the new title.
   *
   * @param title	the new title
   */
  @Override
  public void setTitle(String title) {
    super.setTitle(AbstractApplicationFrame.insertHostnamePrefix(title));
    
    if (getParentFrame() != null) {
      if (isActive())
	getParentFrame().createTitle(title);
      getParentFrame().windowListChanged();
    }
  }

  /**
   * Sets the prefix for the UI settings (eg stores width/height).
   *
   * @param value	the prefix, ignored if null or empty
   */
  public void setUISettingsPrefix(String value) {
    if (value == null)
      value = "";
    m_UISettingsPrefix = value;
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
   * Applies any UI settings if present.
   */
  public void applyUISettings() {
    Dimension	size;
    int		x;
    int		y;

    // size
    if (UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".width") && UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".width")) {
      size = getSize();
      setSize(new Dimension(
        UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".width", size.width),
	UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".height", size.height)));
    }

    // position
    if (UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".x") && UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".y")) {
      x = getX();
      y = getY();
      setLocation(
        UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".x", x),
	UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".y", y));
    }
  }

  /**
   * Calls the cleanUp() method if the first component is a CleanUpHandler.
   */
  public void cleanUp() {
    Component		comp;

    if (getContentPane().getComponentCount() > 0) {
      comp = getContentPane().getComponent(0);
      if (comp instanceof CleanUpHandler)
	((CleanUpHandler) comp).cleanUp();
    }
  }

  /**
   * de-registers the child frame with the parent first.
   */
  @Override
  public void dispose() {
    if (!m_DisposeCalled) {
      if (!m_UISettingsPrefix.isEmpty()) {
        UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".width", getWidth());
        UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".height", getHeight());
        UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".x", getX());
        UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".y", getY());
        UISettings.save();
      }
    }

    m_DisposeCalled = true;

    if (getParentFrame() != null) {
      getParentFrame().removeChildFrame(this);
      getParentFrame().createTitle("");
    }

    super.dispose();
  }

  /**
   * Returns whether a new window, containing the same panel, can be created.
   *
   * @return		true if a new window can be created
   */
  public boolean canCreateNewWindow() {
    boolean		result;
    Component		comp;

    comp = getContentPane().getComponent(0);

    // must be a JPanel descendant
    result = ClassLocator.isSubclass(JPanel.class, comp.getClass());

    // default constructor available?
    if (result) {
      try {
        comp.getClass().getConstructor();
      }
      catch (Exception e) {
        // ignored
        result = false;
      }
    }

    return result;
  }

  /**
   * Creates a new window of itself.
   *
   * @return		the new window, or null if not possible
   */
  public ChildFrame getNewWindow() {
    ChildFrame	result;
    JPanel		panel;
    Component		comp;

    if (!canCreateNewWindow())
      return null;

    comp = getContentPane().getComponent(0);
    try {
      panel  = (JPanel) comp.getClass().getDeclaredConstructor().newInstance();
      result = new ChildFrame(getParentFrame(), m_InitialTitle, m_IconName);
      result.setSize(getSize());
      result.setLocation(getX() + 20, getY() + 20);
      result.getContentPane().setLayout(new BorderLayout());
      result.getContentPane().add(panel, BorderLayout.CENTER);
      if (panel instanceof MenuBarProvider)
        result.setJMenuBar(((MenuBarProvider) panel).getMenuBar());
      if (panel instanceof DatabaseConnectionHandler)
        ((DatabaseConnectionHandler) panel).setDatabaseConnection(getParentFrame().getDatabaseConnection());
      if (m_DisposeWindowListenerAdded)
        result.addDisposeWindowListener();
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Adds a window listener to dispose the frame.
   */
  public void addDisposeWindowListener() {
    m_DisposeWindowListenerAdded = true;
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        m_Self.dispose();
      }
    });
  }

  /**
   * Returns the logger.
   *
   * @return		the logger
   */
  @Override
  public Logger getLogger() {
    if (getParentFrame() != null)
      return getParentFrame().getLogger();
    else
      return getLogger();
  }
}