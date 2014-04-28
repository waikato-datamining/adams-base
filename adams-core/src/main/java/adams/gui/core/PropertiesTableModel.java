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
 * PropertiesTableModel.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adams.core.Properties;

/**
 * Table model for displaying a properties object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertiesTableModel
  extends KeyValuePairTableModel {

  /** for serialization. */
  private static final long serialVersionUID = -7651644993347866043L;

  /**
   * Initializes the table model with no data.
   */
  public PropertiesTableModel() {
    super(new String[0][]);
  }

  /**
   * Initializes the table model.
   *
   * @param props	the props to display
   */
  public PropertiesTableModel(java.util.Properties props) {
    super(convert(new Properties(props)));
  }

  /**
   * Initializes the table model.
   *
   * @param props	the props to display
   */
  public PropertiesTableModel(Properties props) {
    super(convert(props));
  }

  /**
   * Returns the current content as properties.
   *
   * @return		the properties
   */
  public Properties getProperties() {
    Properties	result;
    int		i;

    result = new Properties();

    for (i = 0; i < m_Data.length; i++)
      result.setProperty(m_Data[i][0].toString(), m_Data[i][1].toString());

    return result;
  }

  /**
   * Turns the properties into key-value pairs. The keys are sorted
   * alphabetically.
   *
   * @param data	the data to convert
   * @return		the converted data
   */
  protected static String[][] convert(Properties data) {
    String[][]		result;
    List<String> 	keys;

    keys = new ArrayList<String>(data.keySetAll());
    Collections.sort(keys);
    result = new String[keys.size()][2];
    for (int i = 0; i < keys.size(); i++) {
      result[i][0] = keys.get(i);
      result[i][1] = data.getProperty(keys.get(i));
    }

    return result;
  }
}