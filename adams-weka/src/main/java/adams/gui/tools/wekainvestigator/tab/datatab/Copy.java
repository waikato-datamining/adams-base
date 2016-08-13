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
 * Copy.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.datatab;

import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import weka.core.Instances;

import java.awt.event.ActionEvent;

/**
 * Copies the selected dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Copy
  extends AbstractDataTabAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /**
   * Instantiates the action.
   */
  public Copy() {
    super();
    setName("Copy");
    setIcon("copy.gif");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    DataContainer 	cont;
    String  		newName;
    MemoryContainer	newCont;

    cont = getSelectedData()[0];
    logMessage("Copying dataset: " + cont.getData().relationName() + " [" + cont.getSourceFull() + "]");
    newName = GUIHelper.showInputDialog(getOwner(), "Please enter new relation name: ", "Copy of " + cont.getData().relationName());
    if (newName == null) {
      logMessage("Copying cancelled!");
      return;
    }
    newCont = new MemoryContainer(new Instances(cont.getData()));
    newCont.getData().setRelationName(newName);
    getData().add(newCont);
    fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, getData().size() - 1));
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(getTable().getSelectedRowCount() == 1);
  }
}
