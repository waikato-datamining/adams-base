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
 * RemoteControlCenterPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter;

import adams.core.ClassLister;
import adams.core.logging.LoggingLevel;
import adams.gui.action.AbstractBaseAction;
import adams.gui.action.BaseAction;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.BaseMenu;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.gui.event.RemoteScriptingEngineUpdateListener;
import adams.gui.tools.remotecontrolcenter.panels.AbstractRemoteControlCenterTab;
import adams.gui.tools.remotecontrolcenter.panels.LogTab;
import adams.gui.tools.remotecontrolcenter.panels.SendCommandTab;
import adams.gui.workspace.AbstractWorkspacePanel;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * Remote control center session panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteControlCenterPanel
  extends AbstractWorkspacePanel
  implements RemoteScriptingEngineUpdateListener {

  private static final long serialVersionUID = 6627987454101716937L;

  /** the owner. */
  protected RemoteControlCenterManagerPanel m_Owner;

  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedPane;

  /** the statust bar. */
  protected BaseStatusBar m_StatusBar;

  /** the action for closing the control center. */
  protected BaseAction m_ActionFileClose;

  /** the action for closing a tab. */
  protected BaseAction m_ActionTabCloseTab;

  /** the action for closing all tabs. */
  protected BaseAction m_ActionTabCloseAllTabs;

  /** the submenu for a new tab. */
  protected BaseMenu m_MenuTabNewTab;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Owner = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    AbstractRemoteControlCenterTab	tab;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    m_StatusBar = new BaseStatusBar();
    m_StatusBar.setMouseListenerActive(true);
    add(m_StatusBar, BorderLayout.SOUTH);

    initActions();

    tab = new SendCommandTab();
    tab.setOwner(this);
    m_TabbedPane.addTab(tab.getTitle(), tab);
    tab = new LogTab();
    tab.setOwner(this);
    m_TabbedPane.addTab(tab.getTitle(), tab);
    m_TabbedPane.setSelectedIndex(0);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
  }

  /**
   * Initializes the actions.
   */
  protected void initActions() {
    m_ActionFileClose = new AbstractBaseAction() {
      private static final long serialVersionUID = -1104246458353845500L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	closeParent();
      }
    };
    m_ActionFileClose.setName("Close");
    m_ActionFileClose.setIcon("exit.png");
    m_ActionFileClose.setAccelerator("ctrl pressed Q");

    m_ActionTabCloseTab = new AbstractBaseAction() {
      private static final long serialVersionUID = 1028160012672649573L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	int index = m_TabbedPane.getSelectedIndex();
	if (index > -1)
	  m_TabbedPane.removeTabAt(index);
	updateMenu();
      }
    };
    m_ActionTabCloseTab.setName("Close tab");
    m_ActionTabCloseTab.setIcon(GUIHelper.getIcon("close_tab_focused.gif"));

    m_ActionTabCloseAllTabs = new AbstractBaseAction() {
      private static final long serialVersionUID = 2162739410818834253L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_TabbedPane.removeAll();
	updateMenu();
      }
    };
    m_ActionTabCloseAllTabs.setName("Close all tabs");
    m_ActionTabCloseAllTabs.setIcon(GUIHelper.getEmptyIcon());
  }

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(RemoteControlCenterManagerPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public RemoteControlCenterManagerPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the default title.
   *
   * @return		the default title
   */
  @Override
  protected String getDefaultTitle() {
    return "Remote control center";
  }

  @Override
  public JMenuBar getMenuBar() {
    JMenuBar				result;
    JMenu 				menu;
    JMenuItem 				menuitem;
    Class[]				classes;
    AbstractRemoteControlCenterTab 	tab;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // File/Close
      menu.add(m_ActionFileClose);

      // Tab
      menu = new JMenu("Command tab");
      menu.setMnemonic('T');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // Tab/New tab
      m_MenuTabNewTab = new BaseMenu("New tab");
      m_MenuTabNewTab.setIcon(GUIHelper.getIcon("new.gif"));
      menu.add(m_MenuTabNewTab);
      classes = ClassLister.getSingleton().getClasses(AbstractRemoteControlCenterTab.class);
      for (final Class cls: classes) {
	try {
	  tab      = (AbstractRemoteControlCenterTab) cls.newInstance();
	  menuitem = new JMenuItem(tab.getTitle());
	  if (tab.getTabIcon() == null)
	    menuitem.setIcon(GUIHelper.getEmptyIcon());
	  else
	    menuitem.setIcon(GUIHelper.getIcon(tab.getTabIcon()));
	  menuitem.addActionListener((ActionEvent e) -> {
	    try {
	      AbstractRemoteControlCenterTab tabNew = (AbstractRemoteControlCenterTab) cls.newInstance();
	      tabNew.setOwner(this);
	      m_TabbedPane.addTab(tabNew.getTitle(), tabNew);
	      m_TabbedPane.setSelectedComponent(tabNew);
	    }
	    catch (Exception ex) {
	      ConsolePanel.getSingleton().append("Failed to instantiate tab class: " + cls.getName(), ex);
	    }
	  });
	  m_MenuTabNewTab.add(menuitem);
	}
	catch (Exception e) {
	  ConsolePanel.getSingleton().append("Failed to instantiate tab class: " + cls.getName(), e);
	}
      }
      m_MenuTabNewTab.sort();
      menu.addSeparator();
      menu.add(m_ActionTabCloseTab);
      menu.add(m_ActionTabCloseAllTabs);

      m_MenuBar = result;
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Updates the title of the dialog.
   */
  @Override
  protected void updateTitle() {
    setParentTitle(m_TitleGenerator.generate((String) null));
  }

  /**
   * updates the enabled state of the menu items.
   */
  @Override
  protected void updateMenu() {
    updateActions();
  }

  /**
   * Updates the actions.
   */
  protected void updateActions() {
    m_ActionTabCloseTab.setEnabled(m_TabbedPane.getTabCount() > 0);
    m_ActionTabCloseAllTabs.setEnabled(m_TabbedPane.getTabCount() > 0);
  }

  /**
   * Logs the message.
   *
   * @param msg		the log message
   */
  @Override
  public void logMessage(String msg) {
    ConsolePanel.getSingleton().append(LoggingLevel.INFO, msg);
    // TODO also log to Log panel
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  @Override
  public void logError(String msg, String title) {
    ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, msg);
    // TODO also log to Log panel
    GUIHelper.showErrorMessage(getParent(), msg, title);
  }

  /**
   * Gets called in case the remote scripting engine got updated.
   *
   * @param e		the event
   */
  public void remoteScriptingEngineUpdated(RemoteScriptingEngineUpdateEvent e) {
    int		i;

    for (i = 0; i < m_TabbedPane.getTabCount(); i++)
      ((AbstractRemoteControlCenterTab) m_TabbedPane.getComponentAt(i)).remoteScriptingEngineUpdated(e);
  }

  /**
   * Returns the application frame this panel belongs to.
   *
   * @return		the frame, null if not part of an app frame
   */
  public AbstractApplicationFrame getApplicationFrame() {
    if (getOwner() != null)
      return getOwner().getApplicationFrame();
    return null;
  }
}
