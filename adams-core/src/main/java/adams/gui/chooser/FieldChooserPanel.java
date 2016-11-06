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
 * FieldChooserPanel.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.FieldType;
import adams.data.report.FieldUtils;
import adams.gui.core.GUIHelper;
import adams.gui.selection.SelectFieldDialog;

/**
 * A panel that contains a text field with the current field and a
 * button for bringing up a dialog for selecting from all available fields.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FieldChooserPanel
  extends AbstractChooserPanel<AbstractField> {

  /** for serialization. */
  private static final long serialVersionUID = -7800388925398386462L;

  /** the dialog for selecting a field. */
  protected SelectFieldDialog m_Dialog;

  /**
   * Initializes the panel with no field.
   */
  public FieldChooserPanel() {
    this(null);
  }

  /**
   * Initializes the panel with the given field.
   *
   * @param field	the field to use, can be null
   */
  public FieldChooserPanel(AbstractField field) {
    super();

    if (field != null)
      setCurrent(field);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    if (getParentDialog() != null)
      m_Dialog = new SelectFieldDialog(getParentDialog());
    else
      m_Dialog = new SelectFieldDialog(getParentFrame());
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  protected AbstractField doChoose() {
    if (hasValue())
      m_Dialog.setItem(getCurrent());
    m_Dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
    m_Dialog.setVisible(true);
    if (m_Dialog.getOption() == SelectFieldDialog.APPROVE_OPTION)
      return m_Dialog.getItem();
    else
      return null;
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  protected String toString(AbstractField value) {
    return value.toString();
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  protected AbstractField fromString(String value) {
    return (Field) FieldUtils.fixClass(getFieldType(), new Field(value, DataType.UNKNOWN));
  }

  /**
   * Sets the field type.
   *
   * @param value	the new field type
   */
  public void setFieldType(FieldType value) {
    setCurrent(null);
    m_Dialog.setFieldType(value);
  }

  /**
   * Returns the field type.
   *
   * @return		the current field type
   */
  public FieldType getFieldType() {
    return m_Dialog.getFieldType();
  }
}
