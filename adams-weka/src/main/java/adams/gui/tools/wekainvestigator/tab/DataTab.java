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

import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BaseTable;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;
import weka.gui.arffviewer.ArffSortedTableModel;
import weka.gui.arffviewer.ArffTable;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lists the currently loaded datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -94945456385486233L;

  /**
   * Model for displaying the loaded data.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DataTableModel
    extends AbstractBaseTableModel {

    private static final long serialVersionUID = 8586181476263855804L;

    /** the underlying data. */
    protected List<DataContainer> m_Data;

    /**
     * Initializes the model.
     *
     * @param data	the data to use
     */
    public DataTableModel(List<DataContainer> data) {
      super();
      m_Data = data;
    }

    /**
     * The number of datasets loaded.
     *
     * @return		the number of datasets
     */
    @Override
    public int getRowCount() {
      return m_Data.size();
    }

    /**
     * The number of columns.
     *
     * @return		the number of columns
     */
    @Override
    public int getColumnCount() {
      int	result;

      result = 0;
      result++;  // index
      result++;  // relation
      result++;  // class
      result++;  // source

      return result;
    }

    @Override
    public String getColumnName(int column) {
      switch (column) {
	case 0:
	  return "Index";
	case 1:
	  return "Relation";
	case 2:
	  return "Class";
	case 3:
	  return "Source";
	default:
	  return null;
      }
    }

    /**
     * Returns the value at the specified position.
     *
     * @param rowIndex		the row
     * @param columnIndex	the column
     * @return			the value
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      DataContainer	cont;

      cont = m_Data.get(rowIndex);

      switch (columnIndex) {
	case 0:
	  return (rowIndex + 1);
	case 1:
	  return cont.getData().relationName();
	case 2:
	  return (cont.getData().classIndex() == -1) ? "<none>" : cont.getData().classAttribute().name();
	case 3:
	  return cont.getSource();
	default:
	  return null;
      }
    }
  }

  /** the table model. */
  protected DataTableModel m_Model;

  /** the table. */
  protected SortableAndSearchableTableWithButtons m_Table;

  /** the button for removing a dataset. */
  protected JButton m_ButtonRemove;

  /** the panel with the data. */
  protected JPanel m_PanelData;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_Model = new DataTableModel(new ArrayList<>());
    m_Table = new SortableAndSearchableTableWithButtons(m_Model);
    m_Table.setPreferredSize(new Dimension(200, 100));
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_Table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
      updateButtons();
      displayData();
    });
    add(new BaseScrollPane(m_Table), BorderLayout.NORTH);

    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.addActionListener((ActionEvent e) -> removeData(m_Table.getSelectedRows()));
    m_Table.addToButtonsPanel(m_ButtonRemove);

    m_PanelData = new JPanel(new BorderLayout());
    m_PanelData.setVisible(false);
    add(m_PanelData, BorderLayout.CENTER);
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
   * Displays the data.
   */
  protected void displayData() {
    ArffTable			table;
    int				index;
    ArffSortedTableModel	model;

    if (m_Table.getSelectedRow() > -1) {
      index = m_Table.getActualRow(m_Table.getSelectedRow());
      model = new ArffSortedTableModel(getOwner().getData().get(index).getData());
      table = new ArffTable(model);
      m_PanelData.removeAll();
      m_PanelData.add(new BaseScrollPane(table), BorderLayout.CENTER);
      m_PanelData.setVisible(true);
    }
    else {
      m_PanelData.removeAll();
      m_PanelData.setVisible(false);
    }
    invalidate();
    revalidate();
    doLayout();
  }

  /**
   * Notifies the tab that the data changed.
   */
  @Override
  public void dataChanged() {
    m_Model = new DataTableModel(getOwner().getData());
    m_Table.setModel(m_Model);
    m_Table.setOptimalColumnWidth();
    if (m_Table.getSelectedRow() == -1) {
      if (m_Model.getRowCount() > 0)
	m_Table.getComponent().setRowSelectionInterval(0, 0);
    }
    updateButtons();
  }

  /**
   * Removes the selected rows, removes all if rows are null.
   *
   * @param rows	the rows to remove, null for all
   */
  protected void removeData(int[] rows) {
    int[]	actRows;
    int		i;

    if (rows == null) {
      getOwner().getData().clear();
      getOwner().fireDataChange();
    }
    else {
      actRows = new int[rows.length];
      for (i = 0; i < actRows.length; i++)
	actRows[i] = m_Table.getActualRow(rows[i]);
      Arrays.sort(actRows);
      for (i = actRows.length - 1; i >= 0; i--)
	getOwner().getData().remove(actRows[i]);
      getOwner().fireDataChange();
    }
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonRemove.setEnabled(m_Table.getSelectedRowCount() > 0);
  }
}
