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
 * DataTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.visualization.instances.InstancesTable;
import adams.gui.visualization.instances.InstancesTableModel;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;

/**
 * Lists the currently loaded datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTab
  extends AbstractInvestigatorTabWithEditableDataTable {

  private static final long serialVersionUID = -94945456385486233L;

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Data";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "spreadsheet.png";
  }

  /**
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see                ListSelectionModel
   */
  protected int getDataTableListSelectionMode() {
    return ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  public void dataChanged(WekaInvestigatorDataEvent e) {
    super.dataChanged(e);
    displayData();
  }

  /**
   * Gets called when the user changes the selection.
   */
  protected void dataTableSelectionChanged() {
    super.dataTableSelectionChanged();
    displayData();
  }

  /**
   * Displays the data.
   */
  protected void displayData() {
    JPanel			panel;
    SearchPanel			search;
    InstancesTable		table;
    int				index;
    InstancesTableModel 	model;

    if ((m_Table.getRowCount() > 0) && (m_Table.getSelectedRow() > -1)) {
      panel = new JPanel(new BorderLayout());
      // table
      // TODO cache tables?
      index = m_Table.getSelectedRow();
      model = new InstancesTableModel(getData().get(index).getData());
      model.setUndoHandler(getData().get(index));
      model.setShowAttributeIndex(true);
      table = new InstancesTable(model);
      table.setUndoEnabled(true);
      panel.add(new BaseScrollPane(table), BorderLayout.CENTER);
      // search
      search = new SearchPanel(LayoutType.HORIZONTAL, true);
      search.addSearchListener((SearchEvent e) -> {
	if (e.getParameters().getSearchString().isEmpty())
	  table.search(null, false);
	else
	  table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      });
      panel.add(search, BorderLayout.SOUTH);
      m_PanelData.removeAll();
      m_PanelData.add(panel, BorderLayout.CENTER);
      if (m_SplitPane.isBottomComponentHidden()) {
	m_SplitPane.setDividerLocation(m_DefaultDataTableHeight);
	m_SplitPane.setBottomComponentHidden(false);
      }
      table.setOptimalColumnWidth();
    }
    else {
      m_PanelData.removeAll();
      m_SplitPane.setBottomComponentHidden(true);
    }
    invalidate();
    revalidate();
    doLayout();
  }
}
