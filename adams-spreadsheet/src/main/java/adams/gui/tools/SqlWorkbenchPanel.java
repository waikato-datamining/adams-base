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
 * SqlWorkbenchPanel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.gui.core.BaseMenu;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MultiPagePane;
import adams.gui.tools.sqlworkbench.SqlMetaDataPanel;
import adams.gui.tools.sqlworkbench.SqlQueryPanel;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * SQL Workbench master panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SqlWorkbenchPanel
  extends BasePanel
  implements MenuBarProvider {

  private static final long serialVersionUID = -441129668621939313L;

  /** the menubar. */
  protected JMenuBar m_MenuBar;

  /** the multi-page pane. */
  protected MultiPagePane m_MultiPagePane;

  /** the "new query" menu item. */
  protected JMenuItem m_MenuItemNewQuery;

  /** the "new meta-data" menu item. */
  protected JMenuItem m_MenuItemNewMetaData;

  /** the "close query" menu item. */
  protected JMenuItem m_MenuItemCloseQuery;

  /** the "close all queries" menu item. */
  protected JMenuItem m_MenuItemCloseAllQueries;

  /** the "close" menu item. */
  protected JMenuItem m_MenuItemClose;

  /** the SQL counter. */
  protected int m_SQLCounter;

  /** the MetaData counter. */
  protected int m_MetaDataCounter;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_SQLCounter = 0;
    m_MetaDataCounter = 0;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_MultiPagePane = new MultiPagePane();
    m_MultiPagePane.setMaxPageCloseUndo(10);
    add(m_MultiPagePane, BorderLayout.CENTER);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    newQuery();
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // Queries
      menu = new BaseMenu("Queries");
      result.add(menu);
      menu.setMnemonic('Q');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // Queries/New query
      menuitem = new JMenuItem("New SQL query", ImageManager.getIcon("query.gif"));
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.addActionListener((ActionEvent e) -> newQuery());
      menu.add(menuitem);
      m_MenuItemNewQuery = menuitem;

      // Queries/New meta-data
      menuitem = new JMenuItem("New meta-data query", ImageManager.getIcon("metadata.gif"));
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed N"));
      menuitem.addActionListener((ActionEvent e) -> newMetaData());
      menu.add(menuitem);
      m_MenuItemNewMetaData = menuitem;

      menu.addSeparator();

      // Queries/Close query
      menuitem = new JMenuItem("Close query", ImageManager.getIcon("delete.gif"));
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed W"));
      menuitem.addActionListener((ActionEvent e) -> closeQuery());
      menu.add(menuitem);
      m_MenuItemCloseQuery = menuitem;

      // Queries/Close all queries
      menuitem = new JMenuItem("Close all queries", ImageManager.getEmptyIcon());
      menuitem.addActionListener((ActionEvent e) -> closeAllQueries());
      menu.add(menuitem);
      m_MenuItemCloseAllQueries = menuitem;

      menu.addSeparator();

      // Queries/Close
      menuitem = new JMenuItem("Close", ImageManager.getIcon("exit.png"));
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener((ActionEvent e) -> closeParent());
      menu.add(menuitem);
      m_MenuItemClose = menuitem;

      m_MenuBar = result;
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Updates the menu.
   */
  protected void updateMenu() {
    m_MenuItemCloseQuery.setEnabled(m_MultiPagePane.getSelectedIndex() > -1);
    m_MenuItemCloseAllQueries.setEnabled(m_MultiPagePane.getPageCount() > 0);
  }

  /**
   * Adds a new query page.
   */
  public void newQuery() {
    m_SQLCounter++;
    m_MultiPagePane.addPage("SQL-" + m_SQLCounter, new SqlQueryPanel());
  }

  /**
   * Adds a new meta-data query page.
   */
  public void newMetaData() {
    m_MetaDataCounter++;
    m_MultiPagePane.addPage("MetaData-" + m_MetaDataCounter, new SqlMetaDataPanel());
  }

  /**
   * Closes the current query.
   */
  public void closeQuery() {
    if (m_MultiPagePane.getPageCount() < 1)
      return;
    m_MultiPagePane.removeSelectedPage();
  }

  /**
   * Removes all queries.
   */
  public void closeAllQueries() {
    m_MultiPagePane.removeAllPages();
  }
}
