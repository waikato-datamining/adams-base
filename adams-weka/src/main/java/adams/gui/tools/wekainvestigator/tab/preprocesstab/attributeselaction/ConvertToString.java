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
 * ConvertToString.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction;

import adams.data.weka.WekaAttributeRange;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import weka.core.Instances;
import weka.filters.Filter;

import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;

/**
 * Converts the selected attributes to string ones.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConvertToString
  extends AbstractSelectedAttributesAction {

  private static final long serialVersionUID = -217537095007987947L;

  /**
   * Instantiates the action.
   */
  public ConvertToString() {
    super();
    setName("Convert to string");
    setIcon("to_string.png");
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
    StringBuilder	indicesStr;
    int			i;
    int			index;
    DataContainer 	cont;
    Runnable		run;

    if (getSelectedRows().length != 1)
      return;
    index = getSelectedRows()[0];
    cont  = getData().get(index);

    indices = getOwner().getAttributeSelectionPanel().getSelectedAttributes();
    if (indices.length == 0)
      return;
    indicesStr = new StringBuilder();
    for (i = 0; i < indices.length; i++) {
      if (i > 0)
        indicesStr.append(",");
      indicesStr.append("" + (indices[i] + 1));
    }

    run = () -> {
      showStatus("Converting checked attributes to string...");
      boolean keep = getOwner().getCheckBoxKeepName().isSelected();
      String oldName = cont.getData().relationName();
      weka.filters.unsupervised.attribute.AnyToString anytostring = new weka.filters.unsupervised.attribute.AnyToString();
      anytostring.setRange(new WekaAttributeRange(indicesStr.toString()));
      try {
	anytostring.setInputFormat(cont.getData());
	Instances filtered = Filter.useFilter(cont.getData(), anytostring);
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
	logError("Failed to convert checked attributes to string!", ex, getName());
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
