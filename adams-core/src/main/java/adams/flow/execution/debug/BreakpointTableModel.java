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
 * BreakpointTableModel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.execution.debug;

import adams.core.option.OptionUtils;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.gui.core.AbstractBaseTableModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Table model for displaying the current breakpoints.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10983 $
 */
public class BreakpointTableModel
  extends AbstractBaseTableModel {

  private static final long serialVersionUID = 8719550408646036355L;

  /** the owner. */
  protected ControlPanel m_Owner;

  /**
   * Initializes the model.
   *
   * @param owner	the owning debug object
   */
  public BreakpointTableModel(ControlPanel owner) {
    m_Owner = owner;
  }

  /**
   * Returns the number of breakpoints.
   *
   * @return		the number of breakpoints
   */
  @Override
  public int getRowCount() {
    return m_Owner.getOwner().getBreakpoints().length;
  }

  /**
   * Returns the number of columns.
   *
   * @return		the number of columns
   */
  @Override
  public int getColumnCount() {
    return 11;
  }

  /**
   * Returns the name of the column.
   *
   * @param column	the index of the column
   * @return		the name
   */
  @Override
  public String getColumnName(int column) {
    switch (column) {
      case 0:
	return "Current";
      case 1:
	return "Enabled";
      case 2:
	return "PreIn";
      case 3:
	return "PostIn";
      case 4:
	return "PreEx";
      case 5:
	return "PostEx";
      case 6:
	return "PreOut";
      case 7:
	return "PostOut";
      case 8:
	return "Type";
      case 9:
	return "Quickinfo";
      case 10:
	return "Condition";
      default:
	throw new IllegalArgumentException("Illegal column index: " + column);
    }
  }

  /**
   * Returns the class for the specified column.
   *
   * @param columnIndex	the index
   * @return			the class
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
	return Boolean.class;
      case 8:
      case 9:
      case 10:
	return String.class;
      default:
	throw new IllegalArgumentException("Illegal column index: " + columnIndex);
    }
  }

  /**
   * Returns whether a cell is editable.
   *
   * @param rowIndex		the row, i.e., breakpoint
   * @param columnIndex	the column
   * @return			true if editable
   */
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return (columnIndex >= 1) && (columnIndex <= 7);
  }

  /**
   * Sets the cell value at the specified location.
   *
   * @param aValue		the value to set
   * @param rowIndex		the row, i.e., the breakpoint
   * @param columnIndex	the column
   */
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    AbstractBreakpoint breakpoint;

    breakpoint = m_Owner.getOwner().getBreakpoints()[rowIndex];

    switch (columnIndex) {
      case 1:
	breakpoint.setDisabled(!((Boolean) aValue));
	break;
      case 2:
	breakpoint.setOnPreInput((Boolean) aValue);
	break;
      case 3:
	breakpoint.setOnPostInput((Boolean) aValue);
	break;
      case 4:
	breakpoint.setOnPreExecute((Boolean) aValue);
	break;
      case 5:
	breakpoint.setOnPostExecute((Boolean) aValue);
	break;
      case 6:
	breakpoint.setOnPreOutput((Boolean) aValue);
	break;
      case 7:
	breakpoint.setOnPostOutput((Boolean) aValue);
	break;
    }
  }

  /**
   * Returns the cell value at the specified location.
   *
   * @param rowIndex		the row, i.e., breakpoint
   * @param columnIndex	the column
   * @return			the value
   */
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    AbstractBreakpoint	breakpoint;

    breakpoint = m_Owner.getOwner().getBreakpoints()[rowIndex];

    switch (columnIndex) {
      case 0:
	return (m_Owner.getCurrentBreakpoint() != null) && (breakpoint == m_Owner.getCurrentBreakpoint());
      case 1:
	return !breakpoint.getDisabled();
      case 2:
	return breakpoint.getOnPreInput();
      case 3:
	return breakpoint.getOnPostInput();
      case 4:
	return breakpoint.getOnPreExecute();
      case 5:
	return breakpoint.getOnPostExecute();
      case 6:
	return breakpoint.getOnPreOutput();
      case 7:
	return breakpoint.getOnPostOutput();
      case 8:
	return breakpoint.getClass().getSimpleName();
      case 9:
	if (breakpoint instanceof BooleanConditionSupporter)
	  return ((BooleanConditionSupporter) breakpoint).getCondition().getQuickInfo();
	else
	  return null;
      case 10:
	if (breakpoint instanceof BooleanConditionSupporter)
	  return OptionUtils.getCommandLine(((BooleanConditionSupporter) breakpoint).getCondition());
	else
	  return null;
      default:
	throw new IllegalArgumentException("Illegal column index: " + columnIndex);
    }
  }

  /**
   * Removes all breakpoints.
   */
  public void clearBreakpoints() {
    m_Owner.getOwner().setBreakpoints(new AbstractBreakpoint[0]);
    fireTableDataChanged();
  }

  /**
   * Returns the breakpoint at the specified location.
   *
   * @param rowIndex	the row
   * @return		the breakpoints
   */
  public AbstractBreakpoint getBreakpointAt(int rowIndex) {
    return m_Owner.getOwner().getBreakpoints()[rowIndex];
  }

  /**
   * Sets the breakpoint at the specified location.
   *
   * @param rowIndex	the row
   * @param value	the breakpoint
   */
  public void setBreakpointAt(int rowIndex, AbstractBreakpoint value) {
    m_Owner.getOwner().getBreakpoints()[rowIndex] = value;
    fireTableRowsUpdated(rowIndex, rowIndex);
  }

  /**
   * Returns the breakpoint at the specified location.
   *
   * @param value	the breakpoint to add
   */
  public void addBreakpoint(AbstractBreakpoint value) {
    List<AbstractBreakpoint> list;

    list = new ArrayList<>(Arrays.asList(m_Owner.getOwner().getBreakpoints()));
    list.add(value);
    m_Owner.getOwner().setBreakpoints(list.toArray(new AbstractBreakpoint[list.size()]));
    fireTableRowsUpdated(list.size() - 1, list.size() - 1);
  }

  /**
   * Removes the breakpoint at the specified location.
   *
   * @param rowIndex	the row
   * @return		the breakpoint
   */
  public AbstractBreakpoint removeBreakpointAt(int rowIndex) {
    AbstractBreakpoint	result;
    List<AbstractBreakpoint>	list;

    result = m_Owner.getOwner().getBreakpoints()[rowIndex];
    list   = new ArrayList<>(Arrays.asList(m_Owner.getOwner().getBreakpoints()));
    list.remove(rowIndex);
    m_Owner.getOwner().setBreakpoints(list.toArray(new AbstractBreakpoint[list.size()]));

    fireTableRowsDeleted(rowIndex, rowIndex);

    return result;
  }

  /**
   * Triggers an update.
   */
  public void refresh() {
    fireTableDataChanged();
  }
}
