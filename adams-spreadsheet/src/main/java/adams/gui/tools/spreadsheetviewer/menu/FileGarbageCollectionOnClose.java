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
 * FileGarbageCollectionOnClose.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.menu;

import adams.gui.tools.SpreadSheetViewerPanel;
import adams.gui.tools.spreadsheetviewer.SpreadSheetPanel;

import java.awt.event.ActionEvent;

/**
 * Whether to run garbage collection when closing panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileGarbageCollectionOnClose
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
    return "GC on close";
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    for (SpreadSheetPanel panel: m_State.getAllPanels())
      panel.setGarbageCollectionOnClose(isSelected());
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(true);
  }

  /**
   * Returns the initial selected state of the menu item.
   *
   * @return true if selected initially
   */
  @Override
  protected boolean isInitiallySelected() {
    return SpreadSheetViewerPanel.getProperties().getBoolean("GarbageCollectionOnClose", true);
  }
}
