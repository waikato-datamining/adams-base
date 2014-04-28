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
 * AbstractManagementTableModel.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import adams.gui.core.ClearableModel;
import adams.gui.core.CustomSearchTableModel;
import adams.gui.core.SearchParameters;
import adams.gui.selection.AbstractTableBasedSelectionPanel.AbstractSelectionTableModel;

/**
 * A table model for displaying the details of the database object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractManagementTableModel<T extends Comparable>
  extends AbstractSelectionTableModel<T>
  implements ClearableModel, CustomSearchTableModel {

  /** for serialization. */
  private static final long serialVersionUID = 7481209191067222765L;

  /** the objects to display. */
  protected List<T> m_Values;

  /** whether the model is sorted. */
  protected boolean m_Sorted;
  
  /**
   * Default constructor (sorted).
   */
  public AbstractManagementTableModel() {
    this(false);
  }
  
  /**
   * Default constructor.
   * 
   * @param sorted	true if to sort items
   */
  public AbstractManagementTableModel(boolean sorted) {
    this(new ArrayList<T>(), sorted);
  }

  /**
   * The constructor (sorted).
   *
   * @param values	the values to display
   */
  public AbstractManagementTableModel(T[] values) {
    this(values, true);
  }

  /**
   * The constructor.
   *
   * @param values	the values to display
   * @param sorted	true if to sort items
   */
  public AbstractManagementTableModel(T[] values, boolean sorted) {
    this(new ArrayList<T>(Arrays.asList(values)), sorted);
  }

  /**
   * The constructor (sorted).
   *
   * @param values	the values to display
   */
  public AbstractManagementTableModel(List<T> values) {
    this(values, true);
  }

  /**
   * the constructor.
   *
   * @param values	the values to display
   * @param sorted	true if to sort items
   */
  public AbstractManagementTableModel(List<T> values, boolean sorted) {
    m_Values = new ArrayList(values);
    m_Sorted = sorted;
    if (m_Sorted)
      Collections.sort(m_Values);
  }
  
  /**
   * Returns whether the model is sorted.
   * 
   * @return		true if sorted
   */
  public boolean isSorted() {
    return m_Sorted;
  }

  /**
   * Returns the actual number of entries in the table.
   *
   * @return		the number of entries
   */
  public int getRowCount() {
    return m_Values.size();
  }

  /**
   * Returns the number of columns in the table.
   *
   * @return		the number of columns
   */
  public abstract int getColumnCount();

  /**
   * Returns the name of the column.
   *
   * @param column 	the column to get the name for
   * @return		the name of the column
   */
  @Override
  public abstract String getColumnName(int column);

  /**
   * Returns the class type of the column.
   *
   * @param columnIndex	the column to get the class for
   * @return			the class for the column
   */
  @Override
  public abstract Class getColumnClass(int columnIndex);

  /**
   * Returns the Object at the given position.
   *
   * @param row	the row
   * @param column	the column
   * @return		the Object
   */
  public abstract Object getValueAt(int row, int column);

  /**
   * Returns the Object at the specified position.
   *
   * @param row	the (actual, not visible) position of the Object
   * @return		the Object at the position, null if not valid index
   */
  @Override
  public T getItemAt(int row) {
    if ((row >= 0) && (row < m_Values.size()))
      return m_Values.get(row);
    else
      return null;
  }

  /**
   * Returns the index of the given (visible) Object, -1 if not found.
   *
   * @param value	the Object to look for
   * @return		the index, -1 if not found
   */
  @Override
  public int indexOf(T value) {
    int	result;
    int	i;

    result = -1;

    if (value != null) {
      for (i = 0; i < m_Values.size(); i++) {
        if (value.equals(m_Values.get(i))) {
          result = i;
          break;
        }
      }
    }

    return result;
  }

  /**
   * Tests whether the search matches the specified row.
   *
   * @param params	the search parameters
   * @param row	the row of the underlying, unsorted model
   * @return		true if the search matches this row
   */
  public abstract boolean isSearchMatch(SearchParameters params, int row);

  /**
   * Clears the internal model.
   */
  public void clear() {
    m_Values = new Vector<T>();

    fireTableDataChanged();
  }

  /**
   * Adds the Object to the model, if not alread present.
   *
   * @param value	the Object to add
   */
  public void add(T value) {
    if (m_Values.contains(value))
      return;
    m_Values.add(value);
    if (m_Sorted)
      Collections.sort(m_Values);

    fireTableDataChanged();
  }

  /**
   * Adds the Objects to the model, if not alread present.
   *
   * @param values	the Objects to add
   */
  public void addAll(List<T> values) {
    boolean	modified;

    modified = false;
    for (T value: values) {
      if (m_Values.contains(value))
        continue;
      modified = true;
      m_Values.add(value);
    }
    if (!modified)
      return;

    if (m_Sorted)
      Collections.sort(m_Values);

    fireTableDataChanged();
  }

  /**
   * Removes the Object to the model, if not alread present.
   *
   * @param value	the Object to remove
   */
  public void remove(T value) {
    if (!m_Values.contains(value))
      return;
    m_Values.remove(value);

    fireTableDataChanged();
  }

  /**
   * Moves the specified element one position up.
   * 
   * @param index	the index of the element to move up
   * @return		true if successfully moved
   */
  public boolean moveUp(int index) {
    boolean	result;
    T		backup;
    
    result = false;
    
    if ((index > 0) && (index < m_Values.size())) {
      backup = m_Values.get(index);
      m_Values.remove(index);
      m_Values.add(index - 1, backup);
      result = true;
    }
    
    fireTableDataChanged();

    return result;
  }

  /**
   * Moves the specified element one position down.
   * 
   * @param index	the index of the element to move down
   * @return		true if successfully moved
   */
  public boolean moveDown(int index) {
    boolean	result;
    T		backup;
    
    result = false;
    
    if ((index >= 0) && (index < m_Values.size() - 1)) {
      backup = m_Values.get(index);
      m_Values.remove(index);
      m_Values.add(index + 1, backup);
      result = true;
    }
    
    fireTableDataChanged();
    
    return result;
  }
  
  /**
   * Returns the model as list.
   * 
   * @return		the list
   */
  public List<T> toList() {
    return new ArrayList<T>(m_Values);
  }
}