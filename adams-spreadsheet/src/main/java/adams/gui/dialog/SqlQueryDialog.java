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
 * SqlQueryDialog.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.dialog;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionProvider;
import adams.gui.tools.sqlworkbench.SqlQueryPanel;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

/**
 * Dialog for running a SQL query.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SqlQueryDialog
  extends ApprovalDialog
  implements DatabaseConnectionProvider {

  private static final long serialVersionUID = -2162869597803771001L;

  /** the SQL panel. */
  protected SqlQueryPanel m_PanelSQL;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public SqlQueryDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public SqlQueryDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public SqlQueryDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public SqlQueryDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public SqlQueryDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public SqlQueryDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public SqlQueryDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public SqlQueryDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PanelSQL = new SqlQueryPanel();
    getContentPane().add(m_PanelSQL, BorderLayout.CENTER);

    setSize(600, 300);
  }

  /**
   * Sets the query.
   *
   * @param value	the query to use
   */
  public void setQuery(String value) {
    m_PanelSQL.setQuery(value);
  }

  /**
   * Returns the current query.
   *
   * @return		the current query
   */
  public String getQuery() {
    return m_PanelSQL.getQuery();
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_PanelSQL.getDatabaseConnection();
  }
}
