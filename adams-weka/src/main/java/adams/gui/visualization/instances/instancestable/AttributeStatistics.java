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
 * AttributeStatistics.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.gui.core.GUIHelper;
import adams.gui.core.TableRowRange;
import adams.gui.dialog.TextDialog;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;
import weka.core.AttributeStats;
import weka.core.Instances;

import java.awt.Dialog.ModalityType;

/**
 * Displays statistics for the selected attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AttributeStatistics
  extends AbstractProcessColumn {

  private static final long serialVersionUID = 3101728458818516005L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays statistics for the selected attribute.";
  }

  /**
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Attribute statistics...";
  }

  /**
   * Checks whether the row range can be handled.
   *
   * @param range	the range to check
   * @return		true if handled
   */
  public boolean handlesRowRange(TableRowRange range) {
    return true;
  }

  /**
   * Processes the specified column.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  protected boolean doProcessColumn(TableState state) {
    AttributeStats 	stats;
    TextDialog		dialog;
    Instances		data;

    data = state.table.toInstances(state.range, true);
    stats = data.attributeStats(state.actCol);
    if (GUIHelper.getParentDialog(state.table) != null)
      dialog = new TextDialog(GUIHelper.getParentDialog(state.table), ModalityType.MODELESS);
    else
      dialog = new TextDialog(GUIHelper.getParentFrame(state.table), false);
    dialog.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Attribute statistics for column #" + (state.actCol+1) + "/" + data.attribute(state.actCol).name());
    dialog.setUpdateParentTitle(false);
    dialog.setContent(stats.toString());
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);

    return true;
  }
}
