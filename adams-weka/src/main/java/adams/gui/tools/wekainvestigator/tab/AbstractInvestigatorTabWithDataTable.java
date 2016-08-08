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
import adams.gui.core.BaseTableWithButtons;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

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
  protected DataTableWithButtons m_Table;

  /** the panel with the data. */
  protected JPanel m_PanelData;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the default data table height. */
  protected int m_DefaultDataTableHeight;

  /** the last number of datasets. */
  protected int m_LastNumDatasets;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DefaultDataTableHeight = InvestigatorPanel.getProperties().getInteger("DefaultDataTableHeight", 150);
    m_LastNumDatasets        = 0;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    add(m_SplitPane, BorderLayout.CENTER);

    m_Model = new DataTableModel(new ArrayList<>(), hasReadOnlyTable());
    m_Table = new DataTableWithButtons(m_Model);
    m_Table.setPreferredSize(new Dimension(200, m_DefaultDataTableHeight));
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.setSelectionMode(getDataTableListSelectionMode());
    m_Table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> dataTableSelectionChanged());
    m_Table.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_DELETE) {
	  removeData(m_Table.getSelectedRows());
	  e.consume();
	}
	super.keyPressed(e);
      }
    });
    m_SplitPane.setTopComponent(new BaseScrollPane(m_Table));
    m_SplitPane.setTopComponentHidden(false);

    m_PanelData = new JPanel(new BorderLayout());
    m_SplitPane.setBottomComponent(m_PanelData);
    m_SplitPane.setBottomComponentHidden(true);
  }

  /**
   * Returns whether a readonly table is used.
   *
   * @return		true if readonly
   */
  protected abstract boolean hasReadOnlyTable();

  /**
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see		ListSelectionModel
   */
  protected abstract int getDataTableListSelectionMode();

  /**
   * Gets called when the user changes the selection.
   */
  protected abstract void dataTableSelectionChanged();

  /**
   * Returns the table.
   *
   * @return		the table
   */
  public BaseTableWithButtons getTable() {
    return m_Table;
  }

  /**
   * Notifies the tab that the data changed.
   */
  @Override
  public void dataChanged() {
    final TIntArrayList	widths;
    int			i;

    widths = new TIntArrayList();
    for (i = 0; i < m_Table.getColumnModel().getColumnCount(); i++)
      widths.add(m_Table.getColumnModel().getColumn(i).getWidth());
    m_Model = new DataTableModel(getData(), hasReadOnlyTable());
    m_Table.setModel(m_Model);
    if (m_LastNumDatasets != getData().size()) {
      m_Table.setOptimalColumnWidth();
    }
    else {
      SwingUtilities.invokeLater(() -> {
	for (int n = 0; n < m_Table.getColumnModel().getColumnCount(); n++)
	  m_Table.getColumnModel().getColumn(n).setPreferredWidth(widths.get(n));
	m_Table.getComponent().doLayout();
	m_Table.getComponent().getTableHeader().repaint();
      });
    }
    m_LastNumDatasets = getData().size();
    if (m_Table.getSelectedRow() == -1) {
      if (m_Model.getRowCount() > 0)
	m_Table.getComponent().setRowSelectionInterval(0, 0);
    }
  }

  /**
   * Returns the selected rows.
   *
   * @return		the rows
   */
  protected int[] getSelectedRows() {
    return m_Table.getSelectedRows();
  }

  /**
   * Removes the selected rows, removes all if rows are null.
   *
   * @param rows	the rows to remove, null for all
   */
  protected void removeData(int[] rows) {
    int		i;

    if (hasReadOnlyTable())
      return;

    if (rows == null) {
      getData().clear();
      fireDataChange();
    }
    else {
      Arrays.sort(rows);
      for (i = rows.length - 1; i >= 0; i--) {
	logMessage("Removing: " + getData().get(i).getSourceFull());
	getData().remove(rows[i]);
      }
      fireDataChange();
    }
  }
}
