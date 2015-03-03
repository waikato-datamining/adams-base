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
 * SelectEmailAddressDialog.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.awt.Dialog;
import java.awt.Frame;

import adams.core.net.EmailAddress;

/**
 * Dialog for selecting email addresses.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SelectEmailAddressDialog
  extends AbstractTableBasedSelectionDialog<EmailAddress, SelectEmailAddressPanel> {

  /** for serialization. */
  private static final long serialVersionUID = -8270032576082341389L;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   */
  public SelectEmailAddressDialog(Dialog owner) {
    super(owner, "Email address");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public SelectEmailAddressDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   */
  public SelectEmailAddressDialog(Frame owner) {
    super(owner, "Email address");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public SelectEmailAddressDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Returns a new instance of the panel to use in the GUI.
   *
   * @return		the panel to use
   */
  @Override
  protected SelectEmailAddressPanel newPanel() {
    return new SelectEmailAddressPanel();
  }
}
