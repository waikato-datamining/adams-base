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
 * MapTableModel.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The model for displaying a map.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MapTableModel
  extends KeyValuePairTableModel {

  /** for serialization. */
  private static final long serialVersionUID = -8212085458244592181L;

  /** the type of the value column. */
  protected Class m_TypeValue;

  /**
   * Initializes the table model with no data.
   */
  public MapTableModel() {
    super(new String[0][]);
  }

  /**
   * Initializes the table model with no data.
   *
   * @param colNames	the column names to use
   */
  public MapTableModel(String[] colNames) {
    super(new String[0][], colNames);
  }

  /**
   * Initializes the table model.
   *
   * @param data	the map to display
   */
  public MapTableModel(Map data) {
    this(data, (Class) null);
  }

  /**
   * Initializes the table model.
   *
   * @param data	the map to display
   * @param typeValue 	the type for the value column, ignored if null
   */
  public MapTableModel(Map data, Class typeValue) {
    super(convert(data));
    m_TypeValue = typeValue;
  }

  /**
   * Initializes the table model.
   *
   * @param data	the map to display
   * @param colNames	the column names to use
   */
  public MapTableModel(Map data, String[] colNames) {
    this(data, colNames, null);
  }

  /**
   * Initializes the table model.
   *
   * @param data	the map to display
   * @param colNames	the column names to use
   * @param typeValue 	the type for the value column, ignored if null
   */
  public MapTableModel(Map data, String[] colNames, Class typeValue) {
    super(convert(data), colNames);
    m_TypeValue = typeValue;
  }

  /**
   * Returns the class for the column.
   *
   * @param column	the column to retrieve the class for
   * @return		the class
   */
  public Class getColumnClass(int column) {
    if ((m_TypeValue != null) && (column == 1))
      return m_TypeValue;

    if (column == 0)
      return String.class;
    else
      return Object.class;
  }

  /**
   * Turns the hashtable into key-value pairs. The keys are sorted
   * alphabetically.
   *
   * @param data	the data to convert
   * @return		the converted data
   */
  protected static Object[][] convert(Map data) {
    Object[][]	result;
    List 	keys;

    result = new Object[data.size()][2];
    keys   = new ArrayList(data.keySet());
    Collections.sort(keys);
    for (int i = 0; i < keys.size(); i++) {
      result[i][0] = "" + keys.get(i);
      result[i][1] = data.get(keys.get(i));
    }

    return result;
  }
}