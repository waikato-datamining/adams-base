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
 * InstanceTableModel.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.instance;

import weka.core.Attribute;
import weka.core.Instances;
import adams.data.weka.ArffUtils;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.CustomSearchTableModel;
import adams.gui.core.SearchParameters;

/**
 * A generic table model for displaying weka.core.Instances objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceTableModel
  extends AbstractBaseTableModel
  implements CustomSearchTableModel {

  /** for serialization. */
  private static final long serialVersionUID = -1824525024174249640L;

  /** the underlying data. */
  protected Instances m_Data;

  /**
   * Initializes the model.
   *
   * @param data	the underlying data
   */
  public InstanceTableModel(Instances data) {
    super();

    m_Data = data;
  }

  /**
   * Returns the attribute for the given column.
   *
   * @param column	the column to get the underlying attribute for
   * @return		the attribute
   */
  protected Attribute getAttribute(int column) {
    if ((m_Data != null) && (column > 0))
      return m_Data.attribute(column - 1);
    else
      return null;
  }

  /**
   * Returns the underlying data.
   *
   * @return		the data
   */
  public Instances getData() {
    return m_Data;
  }

  /**
   * Returns the number of rows.
   *
   * @return		the number of rows
   */
  public int getRowCount() {
    if (m_Data == null)
      return 0;
    else
      return m_Data.numInstances();
  }

  /**
   * Returns the number of columns in the table.
   *
   * @return		the number of columns
   */
  public int getColumnCount() {
    if (m_Data == null)
      return 0;
    else
      return m_Data.numAttributes() + 1;
  }

  /**
   * Returns the name of the column.
   *
   * @param column	the column to retrieve the name for
   * @return		the name of the column
   */
  public String getColumnName(int column) {
    if (column == 0)
      return "Index";
    else
      return m_Data.attribute(column - 1).name();
  }

  /**
   * Returns the value at the given position.
   *
   * @param row	the row in the table
   * @param column	the column in the table
   * @return		the value
   */
  public Object getValueAt(int row, int column) {
    Attribute	att;

    att = getAttribute(column);
    if (column == 0) {
      return row + 1;
    }
    else if (att == null) {
      return "";
    }
    else if (m_Data.instance(row).isMissing(att)) {
      return null;
    }
    else if (att.name().equals(ArffUtils.getDBIDName())) {
      return (int) m_Data.instance(row).value(att);
    }
    else if (att.name().equals(ArffUtils.getIDName())) {
      return m_Data.instance(row).stringValue(att).replaceAll("\'", "");
    }
    else {
      switch (att.type()) {
	case Attribute.NUMERIC:
	  return m_Data.instance(row).value(att);

	case Attribute.DATE:
	case Attribute.NOMINAL:
	case Attribute.STRING:
	case Attribute.RELATIONAL:
	  return m_Data.instance(row).stringValue(att);

	default:
	  return "???";
      }
    }
  }

  /**
   * Returns the class for the column.
   *
   * @param column	the column to retrieve the class for
   * @return		the class
   */
  public Class getColumnClass(int column) {
    Attribute		att;

    att = getAttribute(column);
    if (column == 0) {
      return Integer.class;
    }
    else if (att == null) {
      return String.class;
    }
    else if (att.name().equals(ArffUtils.getDBIDName())) { // special case
      return Integer.class;
    }
    else {
      switch (att.type()) {
	case Attribute.NUMERIC:
	  return Double.class;

	case Attribute.DATE:
	case Attribute.NOMINAL:
	case Attribute.STRING:
	case Attribute.RELATIONAL:
	  return String.class;

	default:
	  return String.class;
      }
    }
  }

  /**
   * Tests whether the search matches the specified row.
   *
   * @param params	the search parameters
   * @param row		the row of the underlying, unsorted model
   * @return		true if the search matches this row
   */
  public boolean isSearchMatch(SearchParameters params, int row) {
    int		n;
    Attribute	att;
    String	valStr;

    for (n = 1; n < m_Data.numAttributes(); n++) {
      att = m_Data.attribute(n);
      if ((att.type() == Attribute.NUMERIC) && params.isDouble()) {
	if (params.matches(m_Data.instance(row).value(att)))
	  return true;
      }
      else {
	if (att.type() == Attribute.NUMERIC)
	  valStr = "" + m_Data.instance(row).value(att);
	else
	  valStr = m_Data.instance(row).stringValue(att);
	if (params.matches(valStr))
	  return true;
      }
    }

    return false;
  }
}
