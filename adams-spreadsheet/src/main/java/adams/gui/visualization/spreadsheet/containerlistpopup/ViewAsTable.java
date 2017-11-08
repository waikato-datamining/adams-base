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
 * ViewAsTable.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.spreadsheet.containerlistpopup;

import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.containerlistpopup.AbstractContainerListPopupCustomizer;
import adams.gui.visualization.spreadsheet.SpreadSheetRow;
import adams.gui.visualization.spreadsheet.SpreadSheetRowContainer;
import adams.gui.visualization.spreadsheet.SpreadSheetRowContainerManager;
import adams.gui.visualization.spreadsheet.SpreadSheetRowPanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;

/**
 * Views the selected instance as table.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ViewAsTable
  extends AbstractContainerListPopupCustomizer<SpreadSheetRow, SpreadSheetRowContainerManager, SpreadSheetRowContainer>{

  private static final long serialVersionUID = -7796583803269239174L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "View as table";
  }

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "view";
  }

   /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
 @Override
  public boolean handles(DataContainerPanelWithContainerList<SpreadSheetRow, SpreadSheetRowContainerManager, SpreadSheetRowContainer> panel) {
    return (panel instanceof SpreadSheetRowPanel);
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param context	the context
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(final Context<SpreadSheetRow,SpreadSheetRowContainerManager,SpreadSheetRowContainer> context, JPopupMenu menu) {
    JMenuItem		item;
    final int[]		indices;

    indices = context.actualSelectedContainerIndices;
    item = new JMenuItem("View as table");
    item.setEnabled(indices.length == 1);
    item.addActionListener((ActionEvent e) -> ((SpreadSheetRowPanel) context.panel).viewInstance(context.panel.getContainerManager().get(indices[0])));
  }
}
