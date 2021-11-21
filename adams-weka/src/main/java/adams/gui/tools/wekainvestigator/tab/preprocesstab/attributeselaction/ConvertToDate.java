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
 * ConvertToDate.java
 * Copyright (C) 2020-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction;

import adams.core.option.OptionUtils;
import adams.data.weka.WekaAttributeRange;
import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToDate;

import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;

/**
 * Converts the selected string attributes to date ones.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConvertToDate
  extends AbstractSelectedAttributesAction {

  private static final long serialVersionUID = -217537095007987947L;

  /**
   * Instantiates the action.
   */
  public ConvertToDate() {
    super();
    setName("Convert to date");
    setIcon("to_date.png");
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
    String		format;
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
      if (!cont.getData().attribute(indices[i]).isString()) {
	logError("Attribute at #" + (indices[i]+1) + " is not of type string!", getName());
	return;
      }
      indicesStr.append("#" + (indices[i] + 1));
    }

    format = GUIHelper.showInputDialog(getOwner().getParent(), "Please enter parse format:", StringToDate.DEFAULT_FORMAT);
    if (format == null)
      return;

    run = () -> {
      showStatus("Converting selected attributes to date: " + indicesStr);
      boolean keep = getOwner().getCheckBoxKeepName().isSelected();
      String oldName = cont.getData().relationName();
      weka.filters.unsupervised.attribute.StringToDate stringtodate = new weka.filters.unsupervised.attribute.StringToDate();
      stringtodate.setRange(new WekaAttributeRange(indicesStr.toString()));
      stringtodate.setFormat(format);
      logMessage("Filter: " + OptionUtils.getCommandLine(stringtodate));
      try {
	stringtodate.setInputFormat(cont.getData());
	Instances filtered = Filter.useFilter(cont.getData(), stringtodate);
	if (keep)
	  filtered.setRelationName(oldName);
	cont.setData(filtered);
	fireDataChange(new WekaInvestigatorDataEvent(getOwner().getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, getSelectedRows()[0]));
	SwingUtilities.invokeLater(() -> {
	  if (getOwner().getAttributeSelectionPanel().getTable().getRowCount() > 0)
	    getOwner().getAttributeSelectionPanel().getTable().setSelectedRow(0);
	});
        showStatus("Finished converting selected attributes to date...");
      }
      catch (Throwable ex) {
	logError("Failed to convert selected attributes to date!", ex, getName());
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
