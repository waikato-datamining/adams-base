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
 * SpreadSheetQueryDialog.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import adams.parser.SpreadSheetQueryText;

/**
 * Dialog for displaying a spreadsheet query editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetQueryDialog
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = -4162026774918001534L;
  
  /** the panel with the SQL statement editor. */
  protected SpreadSheetQueryPanel m_PanelSQL;
  
  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public SpreadSheetQueryDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public SpreadSheetQueryDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public SpreadSheetQueryDialog(Dialog owner, String title) {
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
  public SpreadSheetQueryDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public SpreadSheetQueryDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public SpreadSheetQueryDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public SpreadSheetQueryDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public SpreadSheetQueryDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_PanelSQL = new SpreadSheetQueryPanel();
    m_PanelSQL.setQuery(new SpreadSheetQueryText("SELECT *"));
    getContentPane().add(m_PanelSQL, BorderLayout.CENTER);
    
    setSize(600, 300);
  }
  
  /**
   * Sets the query.
   * 
   * @param value	the query to use
   */
  public void setQuery(SpreadSheetQueryText value) {
    m_PanelSQL.setQuery(value);
  }
  
  /**
   * Returns the current query.
   * 
   * @return		the current query
   */
  public SpreadSheetQueryText getQuery() {
    return m_PanelSQL.getQuery();
  }
}
