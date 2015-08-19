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
 * ViewShowCellTypes.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.menu;

import java.awt.event.ActionEvent;

/**
 * Allows the user to display the cell types or values.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ViewShowCellTypes
  extends AbstractSpreadSheetViewerCheckBoxMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Show cell types";
  }

  /**
   * Returns the initial selected state of the menu item.
   * 
   * @return		true if selected initially
   */
  @Override
  protected boolean isInitiallySelected() {
    return false;
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    if (m_State.getApplyToAll())
      getTabbedPane().setShowCellTypes(isSelected());
    else
      getTabbedPane().setShowCellTypesAt(getTabbedPane().getSelectedIndex(), isSelected());
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.getTabbedPane().getTabCount() > 0);
    if (isEnabled() && (getTabbedPane().getSelectedIndex() > -1))
      setSelected(m_State.getTabbedPane().getShowCellTypes(getTabbedPane().getSelectedIndex()));
  }
}
