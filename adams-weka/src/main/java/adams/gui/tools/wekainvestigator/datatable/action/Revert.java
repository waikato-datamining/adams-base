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
 * Revert.java
 * Copyright (C) 2016-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable.action;

import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MonitoringDataContainer;

import java.awt.event.ActionEvent;

/**
 * Reverts the selected dataset (if possible).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Revert
  extends AbstractEditableDataTableAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /**
   * Instantiates the action.
   */
  public Revert() {
    super();
    setName("Revert");
    setIcon("revert.png");
    setAsynchronous(true);
  }

  /**
   * Checks whether the container got changed or the source has changed.
   *
   * @param cont	the container to check
   * @return		true if changed
   */
  protected boolean hasChanged(DataContainer cont) {
    boolean result;

    result = cont.isModified();

    if (!result && (cont instanceof MonitoringDataContainer))
      result = ((MonitoringDataContainer) cont).hasSourceChanged();

    return result;
  }

  /**
   * Checks whether any selected container can be reverted.
   *
   * @return		true if any container can be reverted
   */
  protected boolean canRevert() {
    boolean	result;

    result = false;

    for (DataContainer cont: getSelectedData()) {
      if (hasChanged(cont) && cont.canReload()) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    for (DataContainer cont: getSelectedData()) {
      if (hasChanged(cont) && cont.canReload()) {
        logMessage("Reverting dataset: " + cont.getID() + "/" + cont.getData().relationName() + " [" + cont.getSource() + "]");
        if (cont.reload()) {
	  getOwner().getOwner().updateClassAttribute(cont.getData());
	  logMessage("Successfully reverted " + cont.getID() + "!");
          fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, getData().indexOf(cont)));
	}
        else {
	  logMessage("Failed to revert!");
	}
      }
    }
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(!isBusy() && (getTable().getSelectedRowCount() > 0) && canRevert());
  }
}
