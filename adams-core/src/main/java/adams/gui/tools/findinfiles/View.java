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
 * View.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.findinfiles;

import adams.core.io.PlaceholderFile;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.PreviewBrowserDialog;
import adams.gui.dialog.TextDialog;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * For viewing the selected file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class View
  extends AbstractFindInFilesAction {

  private static final long serialVersionUID = 2602074439493570865L;

  /**
   * Returns the text for the menu item.
   *
   * @return		the text
   */
  protected String getMenuItemText() {
    return "View...";
  }

  /**
   * Updates the action based on the current state of the owner.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_Owner.getSelectedFiles().length == 1);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    PreviewBrowserDialog dialog;

    if (getOwner().getParentDialog() != null)
      dialog = new PreviewBrowserDialog(getOwner().getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new PreviewBrowserDialog(getOwner().getParentFrame(), false);
    dialog.setSize(GUIHelper.getDefaultDialogDimension());
    dialog.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dialog.open(new PlaceholderFile(getOwner().getSelectedFile()));
    dialog.setLocationRelativeTo(getOwner());
    dialog.setVisible(true);
  }
}
