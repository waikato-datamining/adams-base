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
 * Remove.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction;

import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import weka.core.Instances;
import weka.filters.Filter;

import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;

/**
 * Removes the selected attributes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Remove
  extends AbstractSelectedAttributesAction {

  private static final long serialVersionUID = -217537095007987947L;

  /**
   * Instantiates the action.
   */
  public Remove() {
    super();
    setName("Remove");
    setIcon("delete.gif");
    setAsynchronous(true);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    int[]		indices;
    int			index;
    DataContainer cont;
    Runnable		run;

    if (getSelectedRows().length != 1)
      return;
    index = getSelectedRows()[0];
    cont  = getData().get(index);

    indices = getOwner().getAttributeSelectionPanel().getSelectedAttributes();
    if (indices.length == 0)
      return;

    run = () -> {
      showStatus("Removing selected attributes...");
      boolean keep = getOwner().getCheckBoxKeepName().isSelected();
      String oldName = cont.getData().relationName();
      weka.filters.unsupervised.attribute.Remove remove = new weka.filters.unsupervised.attribute.Remove();
      remove.setAttributeIndicesArray(indices);
      try {
	remove.setInputFormat(cont.getData());
	Instances filtered = Filter.useFilter(cont.getData(), remove);
	if (keep)
	  filtered.setRelationName(oldName);
	cont.setData(filtered);
	fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, getSelectedRows()[0]));
	SwingUtilities.invokeLater(() -> {
	  if (getOwner().getAttributeSelectionPanel().getTable().getRowCount() > 0)
	    getOwner().getAttributeSelectionPanel().getTable().setSelectedRow(0);
	});
      }
      catch (Throwable ex) {
	logError("Failed to remove selected attributes!", ex, getName());
      }
      m_Owner.executionFinished();
      showStatus("");
    };
    m_Owner.submitJob(run);
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
        && (getOwner().getAttributeSelectionPanel().getSelectedAttributes().length > 0)
        && m_Owner.canStartExecution());
  }
}
