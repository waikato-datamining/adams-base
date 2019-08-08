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
 * ViewCell.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.gui.core.GUIHelper;
import adams.gui.dialog.TextDialog;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;

/**
 * For viewing the cell content.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ViewCell
  extends AbstractProcessCell {

  private static final long serialVersionUID = -6533662585721463186L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the current cell content in a separate dialog.";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "View cell";
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  @Override
  public String getIconName() {
    return "editor.gif";
  }

  /**
   * Processes the specified cell.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  protected boolean doProcessCell(TableState state) {
    TextDialog				textDlg;

    if (state.selCol == -1)
      return false;
    if (state.selRow == -1)
      return false;

    if (GUIHelper.getParentDialog(state.table) != null)
      textDlg = new TextDialog(GUIHelper.getParentDialog(state.table));
    else
      textDlg = new TextDialog(GUIHelper.getParentFrame(state.table));
    textDlg.setUpdateParentTitle(false);
    textDlg.setTitle("Cell content: row " + (state.actRow + 1) + "/col " + (state.actCol + 1));
    textDlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    textDlg.setContent("" + state.table.getValueAt(state.selRow, state.selCol));
    GUIHelper.pack(textDlg, GUIHelper.getDefaultTinyDialogDimension(), GUIHelper.getDefaultDialogDimension());
    textDlg.setLocationRelativeTo(state.table.getParent());
    textDlg.setVisible(true);
    return true;
  }
}
