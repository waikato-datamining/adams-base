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
 * AttributeStatistics.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.gui.core.GUIHelper;
import adams.gui.dialog.TextDialog;
import adams.gui.visualization.instances.InstancesTable;
import weka.core.AttributeStats;
import weka.core.Instances;

import java.awt.Dialog.ModalityType;

/**
 * Displays statistics for the selected attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
   * Processes the specified column.
   *
   * @param table	the source table
   * @param data	the instances to use as basis
   * @param column	the column in the spreadsheet
   * @return		true if successful
   */
  @Override
  protected boolean doProcessColumn(InstancesTable table, Instances data, int column) {
    AttributeStats 	stats;
    TextDialog		dialog;

    stats = data.attributeStats(column);
    if (GUIHelper.getParentDialog(table) != null)
      dialog = new TextDialog(GUIHelper.getParentDialog(table), ModalityType.MODELESS);
    else
      dialog = new TextDialog(GUIHelper.getParentFrame(table), false);
    dialog.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Attribute statistics for column #" + (column+1) + "/" + data.attribute(column).name());
    dialog.setUpdateParentTitle(false);
    dialog.setContent(stats.toString());
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);

    return true;
  }
}
