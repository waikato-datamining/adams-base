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
 * AbstractManagementPanelWithDatabase.java
 * Copyright (C) 2012-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import adams.gui.core.ImageManager;
import adams.gui.core.SqlConnectionPanel;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * A panel for managing the objects stored in a database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type
 */
public abstract class AbstractManagementPanelWithSelectableDatabase<T extends Comparable>
  extends AbstractManagementPanelWithDatabase<T> {

  /** for serialization. */
  private static final long serialVersionUID = 3181901882660335578L;

  /** the menu item for selecting a database. */
  protected JMenuItem m_MenuItemFileDatabase;

  /** the panel for connecting. */
  protected SqlConnectionPanel m_PanelDatabaseConnection;

  /** the dialog for selecting a database. */
  protected ApprovalDialog m_DialogDatabase;

  /**
   * Hook method for adding items to the "File" menu.
   *
   * @param menu	the menu to update
   * @return 		true if an item was added
   */
  @Override
  protected boolean addToFileMenu(JMenu menu) {
    JMenuItem		menuitem;

    menuitem = new JMenuItem("Databases...", ImageManager.getIcon("database.gif"));
    menuitem.addActionListener((ActionEvent e) -> selectDatabase());
    menu.add(menuitem);
    m_MenuItemFileDatabase = menuitem;

    return true;
  }

  /**
   * Lets the user select another database.
   */
  protected void selectDatabase() {
    if (m_DialogDatabase == null) {
      if (getParentDialog() != null)
	m_DialogDatabase = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
        m_DialogDatabase =  new ApprovalDialog(getParentFrame(), true);
      m_DialogDatabase.setTitle("Select database");
      m_PanelDatabaseConnection = new SqlConnectionPanel();
      m_DialogDatabase.getContentPane().add(m_PanelDatabaseConnection);
      m_DialogDatabase.pack();
      m_DialogDatabase.setLocationRelativeTo(getParent());
    }

    m_DialogDatabase.setVisible(true);
    if (m_DialogDatabase.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    setDatabaseConnection(m_PanelDatabaseConnection.getDatabaseConnection());
    refresh();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_DialogDatabase != null) {
      m_DialogDatabase.dispose();
      m_DialogDatabase = null;
    }
  }
}
