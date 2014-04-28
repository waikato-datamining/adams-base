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
 * AbstractSelectDatabaseFieldDialog.java
 * Copyright (C) 2008-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.awt.Dialog;
import java.awt.Frame;

import adams.data.report.AbstractField;
import adams.data.report.FieldType;

/**
 * Dialog for selecting fields (obtained from a database).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSelectDatabaseFieldDialog<T extends AbstractField>
  extends AbstractDatabaseSelectionDialog<T, AbstractSelectDatabaseFieldPanel> {

  /** for serialization. */
  private static final long serialVersionUID = -8270032576082341389L;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   */
  public AbstractSelectDatabaseFieldDialog(Dialog owner) {
    this(owner, "Select field");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public AbstractSelectDatabaseFieldDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   */
  public AbstractSelectDatabaseFieldDialog(Frame owner) {
    this(owner, "Select field");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public AbstractSelectDatabaseFieldDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Returns a new instance of the panel to use in the GUI.
   *
   * @return		the panel to use
   */
  protected abstract AbstractSelectDatabaseFieldPanel newPanel();

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
}
