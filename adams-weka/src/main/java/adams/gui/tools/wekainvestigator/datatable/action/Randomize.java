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
 * Rename.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable.action;

import adams.core.Utils;
import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;

import java.awt.event.ActionEvent;
import java.util.Random;

/**
 * Randomizes the selected dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Randomize
  extends AbstractEditableDataTableAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /**
   * Instantiates the action.
   */
  public Randomize() {
    super();
    setName("Randomize");
    setIcon("randomize.gif");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    DataContainer 	cont;
    String 		seedStr;
    long		seed;

    cont = getSelectedData()[0];
    logMessage("Randomizing dataset: " + cont.getData().relationName() + " [" + cont.getSource() + "]");
    seedStr = GUIHelper.showInputDialog(getOwner(), "Please enter seed value for randomization: ", "1");
    if (seedStr == null) {
      logMessage("Randomization cancelled!");
      return;
    }
    if (!Utils.isLong(seedStr)) {
      logMessage("Seed not integer, randomization cancelled!");
      return;
    }
    seed = Long.parseLong(seedStr);
    cont.addUndoPoint("randomizing with seed " + seed);
    cont.getData().randomize(new Random(seed));
    cont.setModified(true);
    fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, getSelectedRows()[0]));
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(!isBusy() && getTable().getSelectedRowCount() == 1);
  }
}
