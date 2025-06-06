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
 * TagInfoTableModel.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.tags.TagInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Table model for TagInfo items.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TagInfoTableModel
  extends AbstractTableModel {

  private static final long serialVersionUID = -8241315467452666133L;

  /** the underlying tag infos. */
  protected List<TagInfo> m_Infos;

  /**
   * Default constructor.
   */
  public TagInfoTableModel() {
    this(new TagInfo[0]);
  }

  /**
   * Initializes the model with the provided tag infos.
   *
   * @param infos 	the tag infos to use
   */
  public TagInfoTableModel(TagInfo[] infos) {
    this(Arrays.asList(infos));
  }

  /**
   * Initializes the model with the provided tag infos.
   *
   * @param infos 	the tag infos to use
   */
  public TagInfoTableModel(List<TagInfo> infos) {
    super();
    m_Infos = new ArrayList<>(infos);
  }

  /**
   * Removes all tags.
   */
  public void clear() {
    m_Infos.clear();
    fireTableDataChanged();
  }

  /**
   * Adds the tag info.
   *
   * @param info		the info to add
   */
  public void add(TagInfo info) {
    m_Infos.add(info);
    fireTableRowsInserted(m_Infos.size() - 1, m_Infos.size() - 1);
  }

  /**
   * Adds all the tag infos.
   *
   * @param infos	the tag infos to add
   */
  public void addAll(TagInfo[] infos) {
    addAll(Arrays.asList(infos));
  }

  /**
   * Adds all the tag infos.
   *
   * @param infos	the tag infos to add
   */
  public void addAll(List<TagInfo> infos) {
    m_Infos.addAll(infos);
    fireTableRowsInserted(m_Infos.size() - infos.size() - 1, m_Infos.size() - 1);
  }

  /**
   * Removes the tag info at the specified index.
   *
   * @param rowIndex	the index to remove
   * @return		the removed tag info
   */
  public TagInfo remove(int rowIndex) {
    TagInfo	result;

    result = m_Infos.remove(rowIndex);
    fireTableRowsDeleted(rowIndex, rowIndex);

    return result;
  }

  /**
   * Returns the tag info at the specified location.
   *
   * @param rowIndex	the index of the tag
   * @return		the associated tag
   */
  public TagInfo get(int rowIndex) {
    return m_Infos.get(rowIndex);
  }

  /**
   * Updates the tag info.
   *
   * @param rowIndex	the index of the tag info
   * @param info		the new tag info
   */
  public void set(int rowIndex, TagInfo info) {
    m_Infos.set(rowIndex, info);
    fireTableRowsUpdated(rowIndex, rowIndex);
  }

  /**
   * Returns whether there are no tags present.
   *
   * @return		true if empty
   */
  public boolean isEmpty() {
    return m_Infos.isEmpty();
  }

  /**
   * Returns the number of rows in the model. A
   * <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it
   * is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  @Override
  public int getRowCount() {
    return m_Infos.size();
  }

  /**
   * Returns the number of columns in the model. A
   * <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  @Override
  public int getColumnCount() {
    return 3;
  }

  /**
   * Returns the type of the column.
   *
   * @param columnIndex the column being queried
   * @return the type
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  /**
   * Returns the name for the column.
   *
   * @param column  the column being queried
   * @return a string containing the name of <code>column</code>
   */
  @Override
  public String getColumnName(int column) {
    switch (column) {
      case 0:
	return "Name";
      case 1:
	return "Type";
      case 2:
	return "Information";
      default:
	throw new IllegalStateException("Invalid column index: " + column);
    }
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and
   * <code>rowIndex</code>.
   *
   * @param rowIndex    the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    TagInfo	info;

    info = m_Infos.get(rowIndex);
    switch (columnIndex) {
      case 0:
	return info.getName();
      case 1:
	return info.getDataType();
      case 2:
	return info.getInformation();
    }

    return null;
  }

  /**
   * Returns the underlying tags as array.
   *
   * @return		the array
   */
  public TagInfo[] toArray() {
    return m_Infos.toArray(new TagInfo[0]);
  }
}
