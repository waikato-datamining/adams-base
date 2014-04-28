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
 * SQLStatementDialog.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import adams.db.SQLStatement;

/**
 * Dialog for displaying an SQL statement editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SQLStatementDialog
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = -4162026774918001534L;
  
  /** the panel with the SQL statement editor. */
  protected SQLStatementPanel m_PanelSQL;
  
  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public SQLStatementDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public SQLStatementDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public SQLStatementDialog(Dialog owner, String title) {
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
  public SQLStatementDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public SQLStatementDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public SQLStatementDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public SQLStatementDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public SQLStatementDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_PanelSQL = new SQLStatementPanel();
    m_PanelSQL.setStatement(new SQLStatement("SELECT * FROM table"));
    getContentPane().add(m_PanelSQL, BorderLayout.CENTER);
    
    setSize(600, 300);
  }
  
  /**
   * Sets the SQL statement.
   * 
   * @param value	the statement to use
   */
  public void setStatement(SQLStatement value) {
    m_PanelSQL.setStatement(value);
  }
  
  /**
   * Returns the current SQL statement.
   * 
   * @return		the current statement
   */
  public SQLStatement getStatement() {
    return m_PanelSQL.getStatement();
  }
  
  /**
   * Hook method just after the dialog was hidden.
   */
  @Override
  protected void afterHide() {
    super.afterHide();
    
    if (getOption() == APPROVE_OPTION)
      m_PanelSQL.addStatementToHistory();
  }
}
