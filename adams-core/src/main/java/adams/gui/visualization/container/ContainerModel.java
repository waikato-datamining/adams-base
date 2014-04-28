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
 * ContainerModel.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import javax.swing.SwingUtilities;

import adams.core.CleanUpHandler;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;
import adams.gui.event.DataChangeListener;

/**
 * A model for displaying the currently loaded containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <M> the type of container manager to use
 * @param <C> the type of container that is used
 */
public class ContainerModel<M extends AbstractContainerManager, C extends AbstractContainer>
  extends AbstractBaseTableModel
  implements DataChangeListener, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5056182487242887045L;

  /** the underlying data. */
  protected M m_Manager;

  /** for displaying the containers. */
  protected AbstractContainerDisplayStringGenerator m_Generator;

  /** for the column names. */
  protected AbstractContainerTableColumnNameGenerator m_ColumnNameGenerator;

  /** whether to display the visibility column or not (if possible). */
  protected boolean m_DisplayVisibility;

  /** whether to display the database ID column or not (if possible). */
  protected boolean m_DisplayDatabaseID;

  /**
   * Initializes the model.
   */
  public ContainerModel() {
    this((M) null);
  }

  /**
   * Initializes the model.
   *
   * @param manager	the managing object to obtain the data from
   */
  public ContainerModel(ContainerListManager<M> manager) {
    this((manager == null) ? (M) null : manager.getContainerManager());
  }

  /**
   * Initializes the model.
   *
   * @param manager	the manager to obtain the data from
   */
  public ContainerModel(M manager) {
    super();

    m_Manager = manager;
    if (m_Manager != null)
      m_Manager.addDataChangeListener(this);

    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_DisplayVisibility   = true;
    m_DisplayDatabaseID   = false;
    m_Generator           = new DefaultContainerDisplayStringGenerator();
    m_ColumnNameGenerator = new DefaultContainerTableColumnNameGenerator();
  }

  /**
   * Returns the underlying manager.
   *
   * @return		the manager or null if not set (e.g., empty model)
   */
  public M getManager() {
    return m_Manager;
  }

  /**
   * Unregisters this model as listener.
   */
  public void unregister() {
    if (m_Manager != null)
      m_Manager.removeDataChangeListener(this);
  }

  /**
   * Sets the display string generator.
   *
   * @param value	the new generator
   */
  public void setDisplayStringGenerator(AbstractContainerDisplayStringGenerator value) {
    m_Generator = value;

    fireTableDataChanged();
  }

  /**
   * Returns the current display string generator.
   *
   * @return		the generator
   */
  public AbstractContainerDisplayStringGenerator getDisplayStringGenerator() {
    return m_Generator;
  }

  /**
   * Sets the generator for the column names.
   *
   * @param value	the new generator
   */
  public void setColumnNameGenerator(AbstractContainerTableColumnNameGenerator value) {
    m_ColumnNameGenerator = value;

    fireTableStructureChanged();
  }

  /**
   * Returns the current generator for the column names.
   *
   * @return		the generator
   */
  public AbstractContainerTableColumnNameGenerator getColumnNameGenerator() {
    return m_ColumnNameGenerator;
  }

  /**
   * Whether to display the visibility column (if possible).
   *
   * @param value	if true then the column will be displayed where possible
   */
  public void setDisplayVisibility(boolean value) {
    m_DisplayVisibility = value;
    fireTableStructureChanged();
  }

  /**
   * Returns whether the visibility column will be displayed where possible.
   *
   * @return		true if the column will be displayed where possible
   */
  public boolean getDisplayVisibility() {
    return m_DisplayVisibility;
  }

  /**
   * Whether to display the database ID column (if possible).
   *
   * @param value	if true then the column will be displayed where possible
   */
  public void setDisplayDatabaseID(boolean value) {
    m_DisplayDatabaseID = value;
    fireTableStructureChanged();
  }

  /**
   * Returns whether the database ID column will be displayed where possible.
   *
   * @return		true if the column will be displayed where possible
   */
  public boolean getDisplayDatabaseID() {
    return m_DisplayDatabaseID;
  }

  /**
   * Returns whether the visibility column is to be displayed.
   *
   * @return		true if the column is to be displayed
   */
  protected boolean showVisibilityColumn() {
    return (m_Manager instanceof VisibilityContainerManager) && m_DisplayVisibility;
  }

  /**
   * Returns whether the database ID column is to be displayed.
   *
   * @return		true if the column is to be displayed
   */
  protected boolean showDatabaseIDColumn() {
    return (m_Manager instanceof DatabaseContainerManager) && m_DisplayDatabaseID;
  }

  /**
   * Returns the number of columns.
   * <pre>
   * - Checkbox (if manager implements VisibilityContainerManager and m_DisplayVisibility is true)
   * - Database ID (if manager implements DatabaseContainerManager and m_DisplayDatabaseID is true)
   * - Display string
   * </pre>
   *
   * @return		the number of columns
   * @see		#m_DisplayVisibility
   * @see		#m_DisplayDatabaseID
   * @see		VisibilityContainerManager
   */
  public int getColumnCount() {
    int		result;

    result = 1;
    if (showVisibilityColumn())
      result++;
    if (showDatabaseIDColumn())
      result++;

    return result;
  }

  /**
   * Returns the column index of the visibility column.
   *
   * @return		the column index, -1 if not available
   */
  protected int getVisibilityColumn() {
    int		result;

    result = -1;

    if (showVisibilityColumn())
      result = 0;

    return result;
  }

  /**
   * Returns the column index of the database ID column.
   *
   * @return		the column index, -1 if not available
   */
  protected int getDatabaseIDColumn() {
    int		result;

    result = -1;

    if (showDatabaseIDColumn()) {
      result = 0;
      if (showVisibilityColumn())
	result++;
    }

    return result;
  }

  /**
   * Returns the column index of the data column.
   *
   * @return		the column index, -1 if not available
   */
  protected int getDataColumn() {
    int		result;

    result = 0;

    if (showDatabaseIDColumn())
      result++;
    if (showVisibilityColumn())
      result++;

    return result;
  }

  /**
   * Returns the default width for the column.
   *
   * @param columnIndex	the index of the column
   * @return		the width
   */
  public int getColumnWidth(int columnIndex) {
    if (columnIndex == getVisibilityColumn())
      return m_ColumnNameGenerator.getVisibilityWidth();
    else if (columnIndex == getDatabaseIDColumn())
      return m_ColumnNameGenerator.getDatabaseIDWidth();
    else if (columnIndex == getDataColumn())
      return m_ColumnNameGenerator.getDataWidth();

    throw new IllegalStateException("Invalid column index: " + columnIndex);
  }

  /**
   * Returns the number of rows to display.
   *
   * @return		the number of rows
   */
  public int getRowCount() {
    if (m_Manager != null)
      return m_Manager.count();
    else
      return 0;
  }

  /**
   * Returns the class of the column.
   *
   * @param columnIndex	the column
   * @return			the class of the column
   */
  @Override
  public Class getColumnClass(int columnIndex) {
    if (columnIndex == getVisibilityColumn())
      return Boolean.class;
    else if (columnIndex == getDatabaseIDColumn())
      return Integer.class;
    else if (columnIndex == getDataColumn())
      return String.class;

    throw new IllegalStateException("Invalid column index: " + columnIndex);
  }

  /**
   * Returns the name of the column.
   *
   * @param column	the index
   * @return		the name
   */
  @Override
  public String getColumnName(int column) {
    if (column == getVisibilityColumn())
      return m_ColumnNameGenerator.getVisibility();
    else if (column == getDatabaseIDColumn())
      return m_ColumnNameGenerator.getDatabaseID();
    else if (column == getDataColumn())
      return m_ColumnNameGenerator.getData();

    throw new IllegalStateException("Invalid column index: " + column);
  }

  /**
   * Returns the value at the specified position.
   *
   * @param rowIndex		the row
   * @param columnIndex	the column
   * @return			the value
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (m_Manager.isUpdating())
      return null;

    if (rowIndex < m_Manager.count()) {
      if (columnIndex == getVisibilityColumn())
        return new Boolean(((VisibilityContainerManager) getManager()).isVisible(rowIndex));
      else if (columnIndex == getDatabaseIDColumn())
        return new Integer(((DatabaseContainer) getManager().get(rowIndex)).getDatabaseID());
      else if (columnIndex == getDataColumn())
	return m_Generator.getDisplay(getManager().get(rowIndex));

      throw new IllegalStateException("Invalid column index: " + columnIndex);
    }
    else {
      return null;
    }
  }

  /**
   * Sets the value at the given position.
   *
   * @param aValue		the value to set
   * @param rowIndex		the row
   * @param columnIndex	the column
   */
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (rowIndex < m_Manager.count()) {
      if (showVisibilityColumn()) {
        if (columnIndex == 0)
          ((VisibilityContainerManager) getManager()).setVisible(rowIndex, (Boolean) aValue);
      }
    }
  }

  /**
   * Returns whether a cell is editable or not.
   *
   * @param rowIndex		the row
   * @param columnIndex	the column
   * @return			true if editable
   */
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (showVisibilityColumn()) {
      return (columnIndex == 0);
    }
    else {
      return false;
    }
  }

  /**
   * Gets called if the data of the manager noticed a changed.
   *
   * @param e		the event that was sent
   */
  public void dataChanged(DataChangeEvent e) {
    final int[]	indices;
    Runnable	runnable;

    indices  = e.getIndices();
    runnable = null;

    if ((indices == null) || (indices.length == 0)) {
      runnable = new Runnable() {
	public void run() {
	  fireTableDataChanged();
	}
      };
    }
    else {
      if (e.getType() == Type.ADDITION) {
	runnable = new Runnable() {
	  public void run() {
	    fireTableRowsInserted(indices[0], indices[indices.length - 1]);
	  }
	};
      }
      else if (e.getType() == Type.REMOVAL) {
	runnable = new Runnable() {
	  public void run() {
	    fireTableRowsDeleted(indices[0], indices[0]);
	  }
	};
      }
      else if (e.getType() == Type.CLEAR) {
	runnable = new Runnable() {
	  public void run() {
	    fireTableDataChanged();
	  }
	};
      }
      else if (e.getType() == Type.REPLACEMENT) {
	runnable = new Runnable() {
	  public void run() {
	    fireTableRowsUpdated(indices[0], indices[0]);
	  }
	};
      }
      else if (e.getType() == Type.VISIBILITY) {
	runnable = new Runnable() {
	  public void run() {
	    fireTableRowsUpdated(indices[0], indices[0]);
	  }
	};
      }
      else {
	runnable = new Runnable() {
	  public void run() {
	    fireTableDataChanged();
	  }
	};
      }
    }

    if (runnable != null)
      SwingUtilities.invokeLater(runnable);
  }

  /**
   * Returns the container at the specified row.
   *
   * @param row		the row of the container to retrieve
   * @return		the container
   */
  public C getContainerAt(int row) {
    return (C) getManager().get(row);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_Manager != null)
      m_Manager.removeDataChangeListener(this);
  }
}