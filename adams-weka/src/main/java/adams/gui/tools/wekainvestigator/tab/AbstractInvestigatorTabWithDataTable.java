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

import adams.core.Range;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.datatable.DataTableModel;
import adams.gui.tools.wekainvestigator.datatable.DataTableWithButtons;
import adams.gui.tools.wekainvestigator.datatable.action.Rename;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
  extends AbstractInvestigatorTab
  implements TableModelListener {

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

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DefaultDataTableHeight = InvestigatorPanel.getProperties().getInteger("General.DefaultDataTableHeight", 150);
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
    m_Model.addTableModelListener(this);
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
	else if (e.getKeyCode() == KeyEvent.VK_F2) {
	  renameData(m_Table.getSelectedRows());
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

  @Override
  protected void finishInit() {
    super.finishInit();
    m_SplitPane.setDividerLocation(m_DefaultDataTableHeight + 20);
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
   *
   * @param e		the event
   */
  public void dataChanged(WekaInvestigatorDataEvent e) {
    Range 		range;
    int[][]		segs;

    segs = new int[0][];
    if (e.getRows() != null) {
      range = new Range();
      range.setIndices(e.getRows());
      segs = range.getIntSegments();
    }
    switch (e.getType()) {
      case WekaInvestigatorDataEvent.ROWS_DELETED:
	for (int[] seg: segs)
	  m_Model.fireTableRowsDeleted(seg[0], seg[1]);
	m_Table.repaint();
	break;
      case WekaInvestigatorDataEvent.ROWS_MODIFIED:
	for (int[] seg: segs)
	  m_Model.fireTableRowsUpdated(seg[0], seg[1]);
	m_Table.repaint();
	break;
      default:
	m_Model.removeTableModelListener(this);
	m_Model = new DataTableModel(getOwner().getData(), hasReadOnlyTable());
	m_Model.addTableModelListener(this);
	m_Table.setModel(m_Model);
	m_Table.setOptimalColumnWidth();
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
      fireDataChange(new WekaInvestigatorDataEvent(getOwner()));
    }
    else {
      Arrays.sort(rows);
      for (i = rows.length - 1; i >= 0; i--) {
	logMessage("Removing: " + getData().get(i).getSourceFull());
	getData().remove(rows[i]);
      }
      fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_DELETED, rows));
    }
  }

  /**
   * Renames the selected row, does nothing if 0 or more than 1 selected.
   *
   * @param rows	the row to rename
   */
  protected void renameData(int[] rows) {
    Rename	rename;

    if (hasReadOnlyTable())
      return;
    if (!(this instanceof AbstractInvestigatorTabWithEditableDataTable))
      return;
    if (rows.length != 1)
      return;

    rename = new Rename();
    rename.setOwner((AbstractInvestigatorTabWithEditableDataTable) this);
    rename.actionPerformed(new ActionEvent(this, 1, ""));
  }

  /**
   * This fine grain notification tells listeners the exact range
   * of cells, rows, or columns that changed.
   *
   * @see	#fireDataChange(WekaInvestigatorDataEvent)
   */
  public void tableChanged(TableModelEvent e) {
    Range	range;

    switch (e.getType()) {
      case TableModelEvent.DELETE:
	range = new Range((e.getFirstRow() + 1) + "-" + (e.getLastRow()));
	range.setMax(e.getLastRow() + 1);
	fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_DELETED, range.getIntIndices()));
	break;
      case TableModelEvent.INSERT:
	range = new Range((e.getFirstRow() + 1) + "-" + (e.getLastRow()));
	range.setMax(e.getLastRow() + 1);
	fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, range.getIntIndices()));
	break;
      case TableModelEvent.UPDATE:
	range = new Range((e.getFirstRow() + 1) + "-" + (e.getLastRow()));
	range.setMax(e.getLastRow() + 1);
	fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, range.getIntIndices()));
	break;
      default:
	fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.TABLE_CHANGED, null));
    }
  }
}
