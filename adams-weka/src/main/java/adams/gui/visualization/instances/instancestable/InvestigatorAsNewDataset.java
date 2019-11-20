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
 * InvestigatorAsNewDataset.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.core.Utils;
import adams.gui.core.GUIHelper;
import adams.gui.core.TableRowRange;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import adams.gui.visualization.instances.instancestable.InstancesTablePopupMenuItemHelper.TableState;
import weka.core.Instances;

/**
 * Allows the user to add the selected rows as new dataset in the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InvestigatorAsNewDataset
  extends AbstractProcessSelectedRows
  implements ProcessRow {

  private static final long serialVersionUID = 8866236994813131751L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to add the selected rows as new dataset in the Investigator.";
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  public String getIconName() {
    return "new.gif";
  }

  /**
   * Returns whether the menu item is available.
   *
   * @param state 	the state to use
   * @return            true if available
   */
  @Override
  public boolean isAvailable(TableState state) {
    return (GUIHelper.getParent(state.table, InvestigatorPanel.class) != null);
  }

  /**
   * Returns the default name for the menu item.
   *
   * @return            the name
   */
  @Override
  protected String getDefaultMenuItem() {
    return "As new dataset...";
  }

  /**
   * Returns the minimum number of rows that the plugin requires.
   *
   * @return		the minimum
   */
  @Override
  public int minNumRows() {
    return 1;
  }

  /**
   * Returns the maximum number of rows that the plugin requires.
   *
   * @return		the maximum, -1 for none
   */
  @Override
  public int maxNumRows() {
    return -1;
  }

  /**
   * Processes the specified rows.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  protected boolean doProcessSelectedRows(TableState state) {
    InvestigatorPanel	panel;
    Instances		dataNew;
    MemoryContainer 	newCont;

    panel = (InvestigatorPanel) GUIHelper.getParent(state.table, InvestigatorPanel.class);

    // create new dataset
    dataNew = state.table.toInstances(TableRowRange.SELECTED);
    dataNew.setRelationName(dataNew.relationName() + ":" + Utils.arrayToString(Utils.adjustIndices(state.actRows, 1)));

    // add container
    newCont = new MemoryContainer(dataNew);
    panel.getData().add(newCont);
    panel.logMessage("Added new subset from rows: " + Utils.arrayToString(Utils.adjustIndices(state.actRows, 1)));
    panel.fireDataChange(new WekaInvestigatorDataEvent(panel, WekaInvestigatorDataEvent.ROWS_ADDED, panel.getData().size() - 1));

    return true;
  }

  /**
   * Processes the specified row.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  public boolean processRow(TableState state) {
    return processSelectedRows(state);
  }
}
