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
 * AbstractInvestigatorTabWithDataTable.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTable;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

/**
 * Ancestor for tabs that have the data table on top.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInvestigatorTabWithDataTable
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -94945456385486233L;

  /** the table model. */
  protected DataTableModel m_Model;

  /** the table. */
  protected SortableAndSearchableTableWithButtons m_Table;

  /** the panel with the data. */
  protected JPanel m_PanelData;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    add(m_SplitPane, BorderLayout.CENTER);

    m_Model = new DataTableModel(new ArrayList<>());
    m_Table = new SortableAndSearchableTableWithButtons(m_Model);
    m_Table.setPreferredSize(new Dimension(200, 150));
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.setSelectionMode(getDataTableListSelectionMode());
    m_Table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> dataTableSelectionChanged());
    m_SplitPane.setTopComponent(new BaseScrollPane(m_Table));
    m_SplitPane.setTopComponentHidden(false);

    m_PanelData = new JPanel(new BorderLayout());
    m_SplitPane.setBottomComponent(m_PanelData);
    m_SplitPane.setBottomComponentHidden(true);
  }

  /**
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see		ListSelectionModel
   */
  protected abstract int getDataTableListSelectionMode();

  /**
   * Gets called when the used changes the selection.
   */
  protected abstract void dataTableSelectionChanged();

  /**
   * Returns the table.
   *
   * @return		the table
   */
  public SortableAndSearchableTableWithButtons getTable() {
    return m_Table;
  }

  /**
   * Notifies the tab that the data changed.
   */
  @Override
  public void dataChanged() {
    m_Model = new DataTableModel(getData());
    m_Table.setModel(m_Model);
    m_Table.setOptimalColumnWidth();
    if (m_Table.getSelectedRow() == -1) {
      if (m_Model.getRowCount() > 0)
	m_Table.getComponent().setRowSelectionInterval(0, 0);
    }
  }
}
