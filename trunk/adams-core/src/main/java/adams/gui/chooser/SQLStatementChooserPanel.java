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
 * SQLStatementChooserPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import java.awt.Dialog.ModalityType;

import adams.db.SQLStatement;
import adams.gui.dialog.SQLStatementDialog;

/**
 * A panel that contains a text field with the current SQL statement and a
 * button for bringing up an editor for the SQL statement.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SQLStatementChooserPanel
  extends AbstractChooserPanel<SQLStatement> {

  /** for serialization. */
  private static final long serialVersionUID = -8755020252465094120L;

  /** the dialog for editing the SQL statement. */
  protected SQLStatementDialog m_DialogSQL;
  
  /**
   * Initializes the panel with default statement.
   */
  public SQLStatementChooserPanel() {
    this(new SQLStatement("SELECT *"));
  }

  /**
   * Initializes the panel with the given sql statement.
   *
   * @param sql		the statement to use
   */
  public SQLStatementChooserPanel(SQLStatement sql) {
    super();

    setCurrent(sql);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    if (getParentDialog() != null)
      m_DialogSQL = new SQLStatementDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      m_DialogSQL = new SQLStatementDialog(getParentFrame(), true);
    m_DialogSQL.setTitle("Edit SQL statement");
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected SQLStatement doChoose() {
    m_DialogSQL.setVisible(true);
    if (m_DialogSQL.getOption() != SQLStatementDialog.APPROVE_OPTION)
      return null;
    return m_DialogSQL.getStatement();
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  @Override
  protected String toString(SQLStatement value) {
    return value.getValue();
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  @Override
  protected SQLStatement fromString(String value) {
    return new SQLStatement(value);
  }
}
