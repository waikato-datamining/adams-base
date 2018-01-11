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
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.instances.InstancesTable;
import adams.gui.visualization.instances.InstancesTableModel;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Lists the currently loaded datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTab
  extends AbstractInvestigatorTabWithEditableDataTable
  implements ChangeListener, PopupMenuCustomizer {

  private static final long serialVersionUID = -94945456385486233L;

  /** the cache for the tables. */
  protected Map<DataContainer,InstancesTable> m_TableCache;

  /** the cache for the last update cache. */
  protected Map<DataContainer,Date> m_TimestampCache;

  /** the default max column width. */
  protected Integer m_MaxColWidth;

  /** the currently displayed table. */
  protected InstancesTable m_CurrentTable;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TableCache     = new HashMap<>();
    m_TimestampCache = new HashMap<>();
    m_MaxColWidth    = null;
    m_CurrentTable   = null;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.addSearchListener((SearchEvent e) -> {
      if (m_CurrentTable != null) {
	if (e.getParameters().getSearchString().isEmpty())
	  m_CurrentTable.search(null, false);
	else
	  m_CurrentTable.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
  }

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
    Set<DataContainer>	cached;
    Set<DataContainer>	current;
    InstancesTable	table;
    DataContainer	con;

    super.dataChanged(e);

    // update cache
    // 1. remove containers no longer present
    cached  = new HashSet<>(m_TableCache.keySet());
    current = new HashSet<>();
    for (DataContainer cont: getOwner().getData())
      current.add(cont);
    cached.removeAll(current);
    for (DataContainer cont: cached) {
      table = m_TableCache.remove(cont);
      table.removeChangeListener(this);
      table.setCellPopupMenuCustomizer(null);
      m_TimestampCache.remove(cont);
    }
    // 2. remove containers that were modified
    for (DataContainer cont: current) {
      if (m_TimestampCache.containsKey(cont)) {
	if (!cont.lastUpdated().equals(m_TimestampCache.get(cont))) {
	  table = m_TableCache.remove(cont);
	  table.removeChangeListener(this);
	  table.setCellPopupMenuCustomizer(null);
	  m_TimestampCache.remove(cont);
	}
      }
    }
    // other modified containers, e.g. UNDO
    if (e.getType() == WekaInvestigatorDataEvent.ROWS_MODIFIED) {
      for (int row: e.getRows()) {
	con = getData().get(row);
	table = m_TableCache.remove(con);
	if (table != null) {
	  table.removeChangeListener(this);
	  table.setCellPopupMenuCustomizer(null);
	}
	m_TimestampCache.remove(con);
      }
    }

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
    DataContainer		cont;
    int				index;
    InstancesTableModel 	model;
    boolean			setOptimal;

    if (m_MaxColWidth == null)
      m_MaxColWidth = InvestigatorPanel.getProperties().getInteger("Data.MaxColWidth", 100);

    if ((m_Table.getRowCount() > 0) && (m_Table.getSelectedRow() > -1)) {
      panel = new JPanel(new BorderLayout());
      index = m_Table.getSelectedRow();
      cont  = getData().get(index);
      // table
      if (m_TableCache.containsKey(cont)) {
	m_CurrentTable = m_TableCache.get(cont);
	m_CurrentTable.setCellPopupMenuCustomizer(this);
	setOptimal     = false;
      }
      else {
	model = new InstancesTableModel(cont.getData());
	model.setUndoHandler(getData().get(index));
	model.setShowAttributeIndex(true);
	m_CurrentTable = new InstancesTable(model);
	m_CurrentTable.setCellPopupMenuCustomizer(this);
	m_CurrentTable.setUndoEnabled(true);
	m_CurrentTable.addChangeListener(this);
	m_TableCache.put(cont, m_CurrentTable);
	m_TimestampCache.put(cont, new Date(cont.lastUpdated().getTime()));
	setOptimal = true;
      }
      panel.add(new BaseScrollPane(m_CurrentTable), BorderLayout.CENTER);
      // search
      panel.add(m_PanelSearch, BorderLayout.SOUTH);
      m_PanelData.removeAll();
      m_PanelData.add(panel, BorderLayout.CENTER);
      if (m_SplitPane.isBottomComponentHidden()) {
	m_SplitPane.setDividerLocation(m_DefaultDataTableHeight);
	m_SplitPane.setBottomComponentHidden(false);
      }
      if (setOptimal)
	m_CurrentTable.setOptimalColumnWidthBounded(m_MaxColWidth);
    }
    else {
      m_PanelData.removeAll();
      m_SplitPane.setBottomComponentHidden(true);
    }
    invalidate();
    revalidate();
    doLayout();
  }

  /**
   * Gets called when the data in the table changed.
   *
   * @param e		the event
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    if (getSelectedRows().length > 0)
      fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, getSelectedRows()[0]));
    else
      fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.TABLE_CHANGED));
  }

  /**
   * Optional customizing of the menu that is about to be popped up.
   *
   * @param e		The mouse event
   * @param menu	The menu to customize.
   */
  @Override
  public void customizePopupMenu(MouseEvent e, JPopupMenu menu) {
    JMenuItem	menuitem;

    menuitem = new JMenuItem("Insert as dataset", GUIHelper.getIcon("new.gif"));
    menuitem.setEnabled((m_CurrentTable != null) && (m_CurrentTable.getSelectedRowCount() > 0));
    menuitem.addActionListener((ActionEvent ae) -> insertAsDataset());
    menu.add(menuitem);
  }

  /**
   * Inserts the currently selected rows as a new dataset.
   */
  protected void insertAsDataset() {
    Instances		data;
    Instances		newData;
    int[]		indices;
    int			i;
    MemoryContainer	cont;

    if ((m_CurrentTable == null) || (m_CurrentTable.getSelectedRowCount() == 0))
      return;

    indices = m_CurrentTable.getSelectedRows();
    data    = m_CurrentTable.getInstances();
    newData = new Instances(data, indices.length);
    for (i = 0; i < indices.length; i++)
      newData.add((Instance) data.instance(m_CurrentTable.getActualRow(indices[i])).copy());

    cont = new MemoryContainer(newData);
    getData().add(cont);
    fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, getData().size() - 1));
  }
}
