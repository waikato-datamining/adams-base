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
 * TableModel.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.setup;

import adams.event.FlowSetupStateEvent;
import adams.event.FlowSetupStateListener;
import adams.flow.setup.FlowSetup;
import adams.flow.setup.FlowSetupManager;
import adams.gui.core.AbstractMoveableTableModel;

/**
 * A table model for FlowSetup objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowSetupTableModel
  extends AbstractMoveableTableModel
  implements FlowSetupStateListener {

  /** for serialization. */
  private static final long serialVersionUID = 3035229549624468704L;

  /** the name column. */
  public final static int COLUMN_NAME = 0;

  /** the description/file column. */
  public final static int COLUMN_DESC = 1;

  /** the headless column. */
  public final static int COLUMN_HEADLESS = 2;

  /** the running column. */
  public final static int COLUMN_RUNNING = 3;

  /** the onFinish column. */
  public final static int COLUMN_ONFINISH = 4;

  /** the onError column. */
  public final static int COLUMN_ONERROR = 5;

  /** the error column. */
  public final static int COLUMN_ERROR = 6;

  /** the underlying setups. */
  protected FlowSetupManager m_Manager;

  /**
   * Initializes the model.
   *
   * @param manager	the underlying setups
   */
  public FlowSetupTableModel(FlowSetupManager manager) {
    super();

    m_Manager = manager;
    for (int i = 0; i < m_Manager.size(); i++)
      m_Manager.get(i).addFlowSetupStateChangeListener(this);
  }

  /**
   * Returns the number of columns.
   *
   * 1. Name
   * 2. Desc/File
   * 3. Headless
   * 4. Running
   * 5. OnFinish
   * 6. OnError
   * 7. Error
   *
   * @return		always 7
   */
  public int getColumnCount() {
    return 7;
  }

  /**
   * Returns the name of the column.
   *
   * @param column	the column index
   * @return		the name of the column
   */
  public String getColumnName(int column) {
    if (column == COLUMN_NAME)
      return "Name";
    else if (column == COLUMN_DESC)
      return "Description/File";
    else if (column == COLUMN_HEADLESS)
      return "Headless";
    else if (column == COLUMN_RUNNING)
      return "Running";
    else if (column == COLUMN_ONFINISH)
      return "OnFinish";
    else if (column == COLUMN_ONERROR)
      return "OnError";
    else if (column == COLUMN_ERROR)
      return "Error";
    else
      throw new IllegalArgumentException("Invalid column index: " + column);
  }

  /**
   * Returns the number of setups in the model.
   *
   * @return		the number of setups
   */
  public int getRowCount() {
    return m_Manager.size();
  }

  /**
   * Returns the column class for the specified column.
   *
   * @param columnIndex	the index of the column
   * @return		the class
   */
  public Class getColumnClass(int columnIndex) {
    if (columnIndex == COLUMN_NAME)
      return String.class;
    else if (columnIndex == COLUMN_DESC)
      return String.class;
    else if (columnIndex == COLUMN_HEADLESS)
      return Boolean.class;
    else if (columnIndex == COLUMN_RUNNING)
      return Boolean.class;
    else if (columnIndex == COLUMN_ONFINISH)
      return String.class;
    else if (columnIndex == COLUMN_ONERROR)
      return String.class;
    else if (columnIndex == COLUMN_ERROR)
      return String.class;
    else
      throw new IllegalArgumentException("Invalid column index: " + columnIndex);
  }

  /**
   * Returns whether a cell can be edited.
   *
   * @param rowIndex	the row of the cell
   * @param columnIndex	the column of the cell
   * @return		true if the cell can be edited
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    FlowSetup	setup;
    boolean	result;

    result = false;

    if ((rowIndex < 0) || (rowIndex >= m_Manager.size()))
      return result;

    setup = m_Manager.get(rowIndex);
    if (setup.isRunning()) {
      if (columnIndex == COLUMN_RUNNING)
	result = true;
    }
    else {
      if (columnIndex == COLUMN_HEADLESS)
	result = true;
      else if (columnIndex == COLUMN_RUNNING)
	result = true;
      else if (columnIndex == COLUMN_ONFINISH)
	result = true;
      else if (columnIndex == COLUMN_ONERROR)
	result = true;
    }

    return result;
  }

  /**
   * Returns the cell value at the specified location.
   *
   * @param rowIndex	the row of the cell
   * @param columnIndex	the column of the cell
   * @return		the value at the position
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    Object	result;
    FlowSetup	setup;

    if ((rowIndex < 0) || (rowIndex >= m_Manager.size()))
      return null;

    setup = m_Manager.get(rowIndex);

    if (columnIndex == COLUMN_NAME)
      result = setup.getName();
    else if (columnIndex == COLUMN_DESC)
      result =
          "<html>"
      	+ (setup.getDescription().length() > 0 ? setup.getDescription() + "<br>" : "")
      	+ "<font color='blue' size='-2'>" + setup.getFile().getPath() + "</font>"
      	+ "</html>";
    else if (columnIndex == COLUMN_HEADLESS)
      result = setup.isHeadless();
    else if (columnIndex == COLUMN_RUNNING)
      result = setup.isRunning();
    else if (columnIndex == COLUMN_ONFINISH)
      result = setup.getOnFinish();
    else if (columnIndex == COLUMN_ONERROR)
      result = setup.getOnError();
    else if (columnIndex == COLUMN_ERROR)
      result = setup.retrieveLastError();
    else
      throw new IllegalArgumentException("Invalid column index: " + columnIndex);

    return result;
  }

  /**
   * Sets the value at the specified position.
   *
   * @param value	the value to set
   * @param rowIndex	the row of the cell
   * @param columnIndex	the column of the cell
   */
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    FlowSetup	setup;

    setup = getSetup(rowIndex);

    if (columnIndex == COLUMN_HEADLESS) {
      m_Manager.setModified(true);
      setup.setHeadless((Boolean) value);
      fireTableCellUpdated(rowIndex, columnIndex);
    }
    else if (columnIndex == COLUMN_RUNNING) {
      if (!setup.isRunning())
	setup.execute();
      else
	setup.stopExecution();
      fireTableCellUpdated(rowIndex, columnIndex);
    }
    else if (columnIndex == COLUMN_ONFINISH) {
      m_Manager.setModified(true);
      setup.setOnFinish((String) value);
      fireTableCellUpdated(rowIndex, columnIndex);
    }
    else if (columnIndex == COLUMN_ONERROR) {
      m_Manager.setModified(true);
      setup.setOnError((String) value);
      fireTableCellUpdated(rowIndex, columnIndex);
    }
  }

  /**
   * Returns the index of the given flow setup.
   *
   * @param setup	the setup to look for
   * @return		the index, -1 if not found
   */
  public int indexOf(FlowSetup setup) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < m_Manager.size(); i++) {
      if (m_Manager.get(i).equals(setup)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns a copy of the setup at the specified location.
   *
   * @param row		the row of the setup
   * @return		the setup
   */
  public FlowSetup getSetup(int row) {
    return m_Manager.get(row);
  }

  /**
   * Sets the setup at the specified location.
   *
   * @param row		the row of the setup
   * @param setup	the setup
   */
  public void setSetup(int row, FlowSetup setup) {
    m_Manager.set(row, setup);
    fireTableRowsUpdated(row, row);
  }

  /**
   * Adds a shallow copy of the setup.
   *
   * @param setup	the setup to add
   */
  public void addSetup(FlowSetup setup) {
    m_Manager.add(setup.shallowCopy());
    fireTableRowsInserted(m_Manager.size() - 1, m_Manager.size() - 1);
  }

  /**
   * Inserts a shallow copy of the setup at the specified location.
   *
   * @param row		the row of the setup
   * @param setup	the setup to add
   */
  public void insertSetup(int row, FlowSetup setup) {
    m_Manager.add(row, setup.shallowCopy());
    fireTableRowsInserted(row, row);
  }

  /**
   * Removes the setup at the specified location.
   *
   * @param row		the row of the setup
   */
  public void removeSetup(int row) {
    m_Manager.remove(row);
    fireTableRowsDeleted(row, row);
  }

  /**
   * Removes all setups.
   */
  public void clearSetups() {
    m_Manager.clear();
    fireTableDataChanged();
  }

  /**
   * Gets called when the flow execution finished. Used for updating the
   * table.
   *
   * @param e		the event
   */
  public void flowSetupStateChanged(FlowSetupStateEvent e) {
    int		row;

    row = indexOf(e.getFlowSetup());
    if (row > -1)
      fireTableRowsUpdated(row, row);
    else
      fireTableDataChanged();
  }

  /**
   * Swaps the two rows.
   *
   * @param firstIndex	the index of the first row
   * @param secondIndex	the index of the second row
   */
  protected void swap(int firstIndex, int secondIndex) {
    FlowSetup	firstSetup;
    FlowSetup	secondSetup;

    firstSetup  = getSetup(firstIndex);
    secondSetup = getSetup(secondIndex);

    setSetup(secondIndex, firstSetup);
    setSetup(firstIndex, secondSetup);
  }
}
