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
 * InvestigatorPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator;

import adams.core.ClassLister;
import adams.gui.action.AbstractBaseAction;
import adams.gui.action.BaseAction;
import adams.gui.core.BaseMenu;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;
import adams.gui.tools.wekainvestigator.tab.InvestigatorTabbedPane;
import adams.gui.tools.wekainvestigator.tab.LogTab;
import adams.gui.workspace.AbstractWorkspacePanel;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * The main panel for the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorPanel
  extends AbstractWorkspacePanel {

  private static final long serialVersionUID = 7442747356297265526L;

  /** the tabbed pane for the tabs. */
  protected InvestigatorTabbedPane m_TabbedPane;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the submenu for a new tab. */
  protected BaseMenu m_MenuNewTab;

  /** the action for closing a tab. */
  protected BaseAction m_ActionCloseTab;

  /** the action for closing all tabs. */
  protected BaseAction m_ActionCloseAllTabs;

  /** the action for closing the investigator. */
  protected BaseAction m_ActionClose;

  /** the log. */
  protected StringBuilder m_Log;

  /** the data loaded. */
  protected List<DataContainer> m_Data;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Log  = new StringBuilder();
    m_Data = new ArrayList<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    LogTab	log;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new InvestigatorTabbedPane(this);
    add(m_TabbedPane, BorderLayout.CENTER);

    log = new LogTab();
    log.setOwner(this);
    m_TabbedPane.addTab(log.getTitle(), log);

    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);

    initActions();
  }

  /**
   * Initializes the actions.
   */
  protected void initActions() {
    // tabs
    m_ActionCloseTab = new AbstractBaseAction() {
      private static final long serialVersionUID = 1028160012672649573L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	int index = m_TabbedPane.getSelectedIndex();
	if (index > -1)
	  m_TabbedPane.removeTabAt(index);
	updateMenu();
      }
    };
    m_ActionCloseTab.setName("Close tab");
    m_ActionCloseTab.setIcon(GUIHelper.getEmptyIcon());

    m_ActionCloseAllTabs = new AbstractBaseAction() {
      private static final long serialVersionUID = 2162739410818834253L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	m_TabbedPane.removeAll();
	updateMenu();
      }
    };
    m_ActionCloseAllTabs.setName("Close all tabs");
    m_ActionCloseAllTabs.setIcon(GUIHelper.getEmptyIcon());

    m_ActionClose = new AbstractBaseAction() {
      private static final long serialVersionUID = -1104246458353845500L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	closeParent();
      }
    };
    m_ActionClose.setName("Close");
    m_ActionClose.setIcon("exit.png");
    m_ActionClose.setAccelerator("ctrl pressed Q");
  }

  /**
   * Updates the actions.
   */
  protected void updateActions() {
    m_ActionCloseTab.setEnabled(m_TabbedPane.getTabCount() > 0);
    m_ActionCloseAllTabs.setEnabled(m_TabbedPane.getTabCount() > 0);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar			result;
    JMenu			menu;
    JMenuItem			menuitem;
    Class[]			classes;
    AbstractInvestigatorTab	tab;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      result.add(menu);

      // File/New tab
      m_MenuNewTab = new BaseMenu("New tab");
      m_MenuNewTab.setIcon(GUIHelper.getIcon("new.gif"));
      menu.add(m_MenuNewTab);
      classes = ClassLister.getSingleton().getClasses(AbstractInvestigatorTab.class);
      for (final Class cls: classes) {
	try {
	  tab      = (AbstractInvestigatorTab) cls.newInstance();
	  menuitem = new JMenuItem(tab.getTitle());
	  menuitem.addActionListener((ActionEvent e) -> {
	    try {
	      AbstractInvestigatorTab tabNew = (AbstractInvestigatorTab) cls.newInstance();
	      m_TabbedPane.addTab(tabNew.getTitle(), tabNew);
	    }
	    catch (Exception ex) {
	      ConsolePanel.getSingleton().append("Failed to instantiate tab class: " + cls.getName(), ex);
	    }
	  });
	  m_MenuNewTab.add(menuitem);
	}
	catch (Exception e) {
	  ConsolePanel.getSingleton().append("Failed to instantiate tab class: " + cls.getName(), e);
	}
      }
      m_MenuNewTab.sort();

      // File/Open file
      // TODO

      // File/Open database
      // TODO

      // File/Generate
      // TODO

      menu.addSeparator();

      // File/Close
      menu.add(m_ActionClose);


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
    setParentTitle(m_TitleGenerator.generate(getDefaultTitle()));
  }

  /**
   * updates the enabled state of the menu items.
   */
  @Override
  protected void updateMenu() {
    updateActions();
    // TODO
  }

  /**
   * Returns the default title.
   *
   * @return		the default title
   */
  @Override
  protected String getDefaultTitle() {
    return "Investigator";
  }

  /**
   * Logs the message.
   *
   * @param msg		the log message
   */
  @Override
  public void logMessage(String msg) {
    int		i;

    m_Log.append(msg);
    m_Log.append("\n");

    m_StatusBar.showStatus(msg);

    for (i = 0; i < m_TabbedPane.getTabCount(); i++) {
      if (m_TabbedPane.getTabComponentAt(i) instanceof LogTab)
	((LogTab) m_TabbedPane.getTabComponentAt(i)).append(msg);
    }
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  @Override
  public void logError(String msg, String title) {
    logMessage(msg);
    GUIHelper.showErrorMessage(this, msg, title);
  }

  /**
   * Returns the internal log buffer.
   *
   * @return		the buffer
   */
  public StringBuilder getLog() {
    return m_Log;
  }

  /**
   * Empties the log.
   */
  public void clearLog() {
    int		i;

    m_Log.setLength(0);
    for (i = 0; i < m_TabbedPane.getTabCount(); i++) {
      if (m_TabbedPane.getTabComponentAt(i) instanceof LogTab)
	((LogTab) m_TabbedPane.getTabComponentAt(i)).clearLog();
    }
  }

  /**
   * Returns the currently loaded data.
   *
   * @return		the data
   */
  public List<DataContainer> getData() {
    return m_Data;
  }

  /**
   * Notifies all the tabs that the data has changed.
   */
  public void fireDataChange() {
    int		i;

    for (i = 0; i < m_TabbedPane.getTabCount(); i++)
      ((AbstractInvestigatorTab) m_TabbedPane.getTabComponentAt(i)).dataChanged();
  }
}
