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
 * LocatedObjectsTableModel.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.image;

import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.AbstractBaseTableModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Table model for located objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public final class LocatedObjectsTableModel
  extends AbstractBaseTableModel {

  private static final long serialVersionUID = 4825483348025409231L;

  public static final String COLUMN_INDEX = "Index";

  public static final String COLUMN_X = "X";

  public static final String COLUMN_Y = "Y";

  public static final String COLUMN_WIDTH = "Width";

  public static final String COLUMN_HEIGHT = "Height";

  public static final String COLUMN_METADATA = "Metadata";

  public static final String COLUMN_PREFIX_META = "Meta-";

  /**
   * How to display the meta-data.
   */
  public enum MetaDataDisplay {
    HIDDEN,
    SINGLE_COLUMN,
    MULTI_COLUMN,
  }

  /** the underlying objects. */
  protected LocatedObjects m_Objects;

  /** how the meta-data is being displayed. */
  protected MetaDataDisplay m_MetaDataDisplay;

  /** all the meta-data keys. */
  protected List<String> m_MetaDataKeys;

  /**
   * Initializes the table model.
   *
   * @param metaDataDisplay	how to display the meta-data
   */
  public LocatedObjectsTableModel(MetaDataDisplay metaDataDisplay) {
    this(metaDataDisplay, null);
  }

  /**
   * Initializes the table model.
   *
   * @param metaDataDisplay	how to display the meta-data
   * @param objects		the underlying objects, can be null
   */
  public LocatedObjectsTableModel(MetaDataDisplay metaDataDisplay, LocatedObjects objects) {
    super();

    m_MetaDataDisplay = metaDataDisplay;
    initModel(objects);
  }

  /**
   * Initializes the model.
   *
   * @param objects	the objects to initialize with
   */
  protected void initModel(LocatedObjects objects) {
    Set<String> 	keys;

    if (objects == null)
      objects = new LocatedObjects();

    m_Objects = objects;

    // determine meta-data keys
    keys = new HashSet<>();
    for (LocatedObject obj: m_Objects)
      keys.addAll(obj.getMetaData().keySet());
    m_MetaDataKeys = new ArrayList<>(keys);
    Collections.sort(m_MetaDataKeys);

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
    return m_Objects.size();
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
    switch (m_MetaDataDisplay) {
      case HIDDEN:
        return 5;
      case SINGLE_COLUMN:
        return 6;
      case MULTI_COLUMN:
        return 5 + m_MetaDataKeys.size();
      default:
        throw new IllegalStateException("Unhandled meta-data display: " + m_MetaDataDisplay);
    }
  }

  /**
   * Returns the name for the column.
   *
   * @param column	the column to get the name for
   * @return		the name
   */
  @Override
  public String getColumnName(int column) {
    switch (column) {
      case 0:
	return COLUMN_INDEX;
      case 1:
	return COLUMN_X;
      case 2:
	return COLUMN_Y;
      case 3:
	return COLUMN_WIDTH;
      case 4:
	return COLUMN_HEIGHT;
    }

    switch (m_MetaDataDisplay) {
      case HIDDEN:
        // already covered
        break;
      case SINGLE_COLUMN:
        if (column == 5)
          return COLUMN_METADATA;
        break;
      case MULTI_COLUMN:
        if (column < m_MetaDataKeys.size() + 5)
	  return COLUMN_PREFIX_META + m_MetaDataKeys.get(column - 5);
    }

    return "";
  }

  /**
   *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   *  @param columnIndex  the column being queried
   *  @return the Object.class
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case 0:
        return Integer.class;
      case 1:
      case 2:
      case 3:
      case 4:
	return Double.class;
      default:
        return String.class;
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
    LocatedObject	obj;

    if ((rowIndex < 0) || (rowIndex >= m_Objects.size()))
      return null;

    obj = m_Objects.get(rowIndex);

    switch (columnIndex) {
      case 0:
        return (rowIndex + 1);
      case 1:
	return obj.getX();
      case 2:
	return obj.getY();
      case 3:
	return obj.getWidth();
      case 4:
	return obj.getHeight();
    }

    switch (m_MetaDataDisplay) {
      case HIDDEN:
        // already covered;
        return null;

      case SINGLE_COLUMN:
        if (columnIndex == 5)
          return obj.getMetaData().toString();
        break;

      case MULTI_COLUMN:
	if (columnIndex < m_MetaDataKeys.size() + 5)
	  return obj.getMetaData().get(m_MetaDataKeys.get(columnIndex - 5));
        break;
    }

    return null;
  }

  /**
   * Sets how the meta-data is being displayed.
   *
   * @param value	the type of display
   */
  public void setMetaDataDisplay(MetaDataDisplay value) {
    m_MetaDataDisplay = value;
    fireTableStructureChanged();
  }

  /**
   * Returns how the meta-data is being displayed.
   *
   * @return		the type of display
   */
  public MetaDataDisplay getMetaDataDisplay() {
    return m_MetaDataDisplay;
  }

  /**
   * Sets the objects to display.
   *
   * @param value	the objects
   */
  public void setObjects(LocatedObjects value) {
    initModel(value);
    fireTableStructureChanged();
  }

  /**
   * Returns the underlying located objects.
   *
   * @return		the objects
   */
  public LocatedObjects getObjects() {
    return m_Objects;
  }

  /**
   * Returns the index of the object in the underlying list.
   *
   * @param obj		the object to located
   * @return		the index, -1 if failed to locate
   */
  public int indexOf(LocatedObject obj) {
    return m_Objects.indexOf(obj);
  }
}
