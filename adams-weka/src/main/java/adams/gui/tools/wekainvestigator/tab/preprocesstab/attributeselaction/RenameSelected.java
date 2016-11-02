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
 * RenameSelected.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction;

import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RenameAttribute;

import java.awt.event.ActionEvent;

/**
 * Renames the selected attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RenameSelected
  extends AbstractSelectedAttributesAction {

  private static final long serialVersionUID = -217537095007987947L;

  /**
   * Instantiates the action.
   */
  public RenameSelected() {
    super();
    setName("Rename selected");
    setIcon("rename.png");
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
    String			nameOld;
    String			nameNew;
    RenameAttribute		rename;
    Instances			data;
    Instances			filtered;
    WekaInvestigatorDataEvent	event;

    cont     = getSelectedData()[0];
    data     = cont.getData();
    attIndex = getOwner().getAttributeSelectionPanel().getSelectedRows()[0];
    nameOld  = data.attribute(attIndex).name();
    nameNew  = GUIHelper.showInputDialog(getOwner(), "Please enter new attribute name", nameOld);
    if ((nameNew == null) || (nameNew.equals(nameOld)))
      return;

    cont.addUndoPoint("Renaming attribute #" + (attIndex+1) + " '" + nameOld + "' to '" + nameNew + "'");
    rename = new RenameAttribute();
    rename.setAttributeIndices("" + (attIndex + 1));
    rename.setFind("([\\s\\S]+)");
    rename.setReplace(nameNew);
    try {
      rename.setInputFormat(data);
      filtered = Filter.useFilter(data, rename);
      cont.setData(filtered);
      event = new WekaInvestigatorDataEvent(
	getOwner().getOwner(),
	WekaInvestigatorDataEvent.ROWS_MODIFIED,
	new int[]{getSelectedRows()[0]});
      getOwner().fireDataChange(event);
    }
    catch (Exception ex) {
      logError(
	"Failed to rename attribute #" + (attIndex+1) + " '" + nameOld + "' to '" + nameNew + "'",
	ex, "Rename failed");
    }
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
