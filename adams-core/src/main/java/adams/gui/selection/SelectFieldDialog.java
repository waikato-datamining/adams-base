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
 * SelectFieldDialog.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import adams.data.report.AbstractField;
import adams.data.report.FieldType;
import adams.gui.dialog.ApprovalDialog;

/**
 * Dialog for selecting fields (obtained from a database).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SelectFieldDialog
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = -8270032576082341389L;

  /** the selection panel. */
  protected SelectFieldPanel m_Panel;

  /** the chosen items. */
  protected AbstractField[] m_Current;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   */
  public SelectFieldDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public SelectFieldDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   */
  public SelectFieldDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public SelectFieldDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * initializes the GUI elements.
   */
  protected void initGUI() {
    super.initGUI();

    m_Panel = newPanel();
    getContentPane().add(m_Panel, BorderLayout.CENTER);

    pack();
  }

  /**
   * Returns a new instance of the panel to use in the GUI.
   *
   * @return		the panel to use
   */
  protected SelectFieldPanel newPanel() {
    return new SelectFieldPanel();
  }

  /**
   * Sets the field type.
   *
   * @param value	the new field type
   */
  public void setFieldType(FieldType value) {
    m_Panel.setFieldType(value);
  }

  /**
   * Returns the field type.
   *
   * @return		the current field type
   */
  public FieldType getFieldType() {
    return m_Panel.getFieldType();
  }

  /**
   * Sets the initially selected set name.
   *
   * @param value	the set name
   */
  public void setItem(AbstractField value) {
    m_Panel.setItem(value);
  }

  /**
   * Returns the set name to load, null if none chosen or dialog canceled.
   *
   * @return		the set name of the data to load
   */
  public AbstractField getItem() {
    return m_Panel.getItem();
  }

  /**
   * Sets the initially selected set names.
   *
   * @param value	the set names
   */
  public void setItems(AbstractField[] value) {
    m_Current = value.clone();
    m_Panel.setItems(m_Current);
  }

  /**
   * Returns the selected set names to load, null if none chosen or dialog
   * canceled.
   *
   * @return		the set names of the data to load
   */
  public AbstractField[] getItems() {
    return m_Current;
  }
}
