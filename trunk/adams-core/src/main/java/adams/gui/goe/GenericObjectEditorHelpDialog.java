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
 * GenericObjectEditorHelpDialog.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.awt.Dialog;
import java.awt.Frame;

import adams.gui.dialog.HelpDialog;

/**
 * Dialog for displaying help on options of an object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GenericObjectEditorHelpDialog
  extends HelpDialog {

  /** for serialization. */
  private static final long serialVersionUID = 2394384614696298610L;

  /** the owning property sheet. */
  protected PropertySheetPanel m_PropertySheet;

  /**
   * Initializes the dialog.
   *
   * @param parent	the parent window
   */
  public GenericObjectEditorHelpDialog(Dialog parent) {
    this(parent, null);
  }

  /**
   * Initializes the dialog.
   *
   * @param parent	the parent window
   * @param owner	the property sheet that this dialog belongs to
   */
  public GenericObjectEditorHelpDialog(Dialog parent, PropertySheetPanel owner) {
    super(parent);
    setPropertySheet(owner);
  }

  /**
   * Initializes the dialog.
   *
   * @param parent	the parent window
   */
  public GenericObjectEditorHelpDialog(Frame parent) {
    this(parent, null);
  }

  /**
   * Initializes the dialog.
   *
   * @param parent	the parent window
   * @param owner	the property sheet that this dialog belongs to
   */
  public GenericObjectEditorHelpDialog(Frame parent, PropertySheetPanel owner) {
    super(parent);
    setPropertySheet(owner);
  }

  /**
   * Sets the owning property sheet.
   *
   * @param value	the owner
   */
  public void setPropertySheet(PropertySheetPanel value) {
    m_PropertySheet = value;
  }

  /**
   * Returns the owning property sheet.
   *
   * @return		the owner
   */
  public PropertySheetPanel getPropertySheet() {
    return m_PropertySheet;
  }

  /**
   * Checks whether a property sheet panel is available.
   *
   * @return		true if a sheet panel is available
   */
  public boolean hasPropertySheet() {
    return (m_PropertySheet != null);
  }

  /**
   * Closes the dialog.
   */
  protected void close() {
    super.close();
    if (hasPropertySheet()) {
      if (getPropertySheet().getHelpDialog() == GenericObjectEditorHelpDialog.this)
	getPropertySheet().getHelpButton().setEnabled(true);
    }
  }
}
