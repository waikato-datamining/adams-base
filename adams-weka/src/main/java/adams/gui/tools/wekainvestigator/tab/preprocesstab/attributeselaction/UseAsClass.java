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
 * UseAsClass.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction;

import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import weka.core.Instances;

import java.awt.event.ActionEvent;

/**
 * Uses the selected attribute as class attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class UseAsClass
  extends AbstractSelectedAttributesAction {

  private static final long serialVersionUID = -217537095007987947L;

  /**
   * Instantiates the action.
   */
  public UseAsClass() {
    super();
    setName("Use as class");
    setIcon("class_attribute.png");
    setAsynchronous(true);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    DataContainer 		cont;
    int				attIndex;
    String 			attName;
    Instances			data;
    WekaInvestigatorDataEvent	event;

    cont     = getSelectedData()[0];
    data     = cont.getData();
    attIndex = getOwner().getAttributeSelectionPanel().getSelectedAttributes()[0];
    attName  = data.attribute(attIndex).name();

    cont.addUndoPoint("Using attribute #" + (attIndex+1) + " '" + attName + "' as class attribute");
    data.setClassIndex(attIndex);
    cont.setData(data);
    event = new WekaInvestigatorDataEvent(
      getOwner().getOwner(),
      WekaInvestigatorDataEvent.ROWS_MODIFIED,
      new int[]{getSelectedRows()[0]});
    getOwner().fireDataChange(event);
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(
      (getOwner() != null)
	&& !isBusy()
	&& (getSelectedRows().length == 1)
	&& (getOwner().getAttributeSelectionPanel().getSelectedRows().length == 1)
	&& m_Owner.canStartExecution());
  }
}
