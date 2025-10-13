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
 * TagTableModel.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.tags.Tag;
import adams.core.tags.TagInfo;
import adams.core.tags.TagProcessorHelper;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Table model for tags.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TagTableModel
  extends AbstractTableModel {

  private static final long serialVersionUID = -8241315467452666133L;

  /** the underlying tags. */
  protected List<Tag> m_Tags;

  /**
   * Default constructor.
   */
  public TagTableModel() {
    this(new Tag[0]);
  }

  /**
   * Initializes the model with the provided tags.
   *
   * @param tags 	the tags to use
   */
  public TagTableModel(Tag[] tags) {
    this(Arrays.asList(tags));
  }

  /**
   * Initializes the model with the provided tags.
   *
   * @param tags 	the tags to use
   */
  public TagTableModel(List<Tag> tags) {
    super();
    m_Tags = new ArrayList<>(tags);
  }

  /**
   * Removes all tags.
   */
  public void clear() {
    m_Tags.clear();
    fireTableDataChanged();
  }

  /**
   * Adds the tag.
   *
   * @param tag		the tag to add
   */
  public void add(Tag tag) {
    int		index;
    int		i;

    index = -1;
    for (i = 0; i < m_Tags.size(); i++) {
      if (m_Tags.get(i).tagName().equals(tag.tagName())) {
	index = i;
	break;
      }
    }
    if (index == -1) {
      m_Tags.add(tag);
      fireTableRowsInserted(m_Tags.size() - 1, m_Tags.size() - 1);
    }
    else {
      m_Tags.set(index, tag);
      fireTableRowsUpdated(index, index);
    }
  }

  /**
   * Adds all the tags.
   *
   * @param tags	the tags to add
   */
  public void addAll(Tag[] tags) {
    addAll(Arrays.asList(tags));
  }

  /**
   * Adds all the tags.
   *
   * @param tags	the tags to add
   */
  public void addAll(List<Tag> tags) {
    m_Tags.addAll(tags);
    fireTableRowsInserted(m_Tags.size() - tags.size() - 1, m_Tags.size() - 1);
  }

  /**
   * Removes the tag at the specified index.
   *
   * @param rowIndex	the index to remove
   * @return		the removed tag
   */
  public Tag remove(int rowIndex) {
    Tag		result;

    result = m_Tags.remove(rowIndex);
    fireTableRowsDeleted(rowIndex, rowIndex);

    return result;
  }

  /**
   * Returns the tag at the specified location.
   *
   * @param rowIndex	the index of the tag
   * @return		the associated tag
   */
  public Tag get(int rowIndex) {
    return m_Tags.get(rowIndex);
  }

  /**
   * Updates the tag.
   *
   * @param rowIndex	the index of the tag
   * @param tag		the new tag
   */
  public void set(int rowIndex, Tag tag) {
    m_Tags.set(rowIndex, tag);
    fireTableRowsUpdated(rowIndex, rowIndex);
  }

  /**
   * Returns whether there are no tags present.
   *
   * @return		true if empty
   */
  public boolean isEmpty() {
    return m_Tags.isEmpty();
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
    return m_Tags.size();
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
    return 4;
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
	return "Value";
      case 2:
	return "Type";
      case 3:
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
    Tag		tag;
    TagInfo	info;

    tag  = m_Tags.get(rowIndex);
    info = TagProcessorHelper.getTagInfo(tag.tagName());
    switch (columnIndex) {
      case 0:
	return tag.tagName();
      case 1:
	return tag.tagValue();
      case 2:
	return (info == null) ? null : info.getDataType();
      case 3:
	return (info == null) ? null : info.getInformation();
    }

    return null;
  }

  /**
   * Returns the underlying tags as array.
   *
   * @return		the array
   */
  public Tag[] toArray() {
    return m_Tags.toArray(new Tag[0]);
  }
}
