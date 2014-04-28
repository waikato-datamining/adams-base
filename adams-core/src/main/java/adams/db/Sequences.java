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
 * Sequences.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;

import adams.core.Properties;
import adams.core.logging.LoggingObject;
import adams.data.sequence.XYSequence;

/**
 * A class for retrieving sequences from database queries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class Sequences
  extends LoggingObject
  implements SequenceProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6223247854964334340L;

  /** the name of the props file. */
  public final static String FILENAME = "Sequences.props";

  /** the prefix for a template. */
  public final static String PREFIX_TEMPLATE = "Template.";

  /** the properties. */
  protected Properties m_Properties;

  /**
   * Initializes the object.
   */
  protected Sequences() {
    super();

    refresh();
  }

  /**
   * Reloads the templates.
   */
  public abstract void refresh();

  /**
   * Checks whether the SQL type represents a numeric one.
   *
   * @param type	the type to check
   * @return		true if numeric
   * @see		Types
   */
  protected boolean isTypeNumeric(int type) {
    boolean	result;

    result =    (type == Types.BIGINT)
             || (type == Types.BIT)
             || (type == Types.DECIMAL)
             || (type == Types.DOUBLE)
             || (type == Types.FLOAT)
             || (type == Types.INTEGER)
             || (type == Types.NUMERIC)
             || (type == Types.REAL)
             || (type == Types.SMALLINT)
             || (type == Types.TINYINT);

    return result;
  }

  /**
   * Checks whether the SQL type represents a date/time/timestamp one.
   *
   * @param type	the type to check
   * @return		true if date/time/timestamp
   * @see		Types
   */
  protected boolean isTypeDate(int type) {
    boolean	result;

    result =    (type == Types.DATE)
             || (type == Types.TIME)
             || (type == Types.TIMESTAMP);

    return result;
  }

  /**
   * Returns the index of the column in the resultset.
   *
   * @param rs		the resultset to use
   * @param colName	the name of the column
   * @param numeric	whether the column has to be numeric
   * @return		the index of the column, -1 if not found
   */
  protected int findColumn(ResultSet rs, String colName, boolean numeric) {
    int			result;
    int			count;
    int			i;
    int			type;
    String		name;
    String		label;
    ResultSetMetaData	meta;

    result  = -1;
    colName = colName.toLowerCase();

    try {
      meta  = rs.getMetaData();
      count = meta.getColumnCount();

      for (i = 1; i <= count; i++) {
	name  = meta.getColumnName(i).toLowerCase();
	label = meta.getColumnLabel(i).toLowerCase();
	type  = meta.getColumnType(i);

	// match?
	if (name.equals(colName) || label.equals(colName)) {
	  if (numeric && !isTypeNumeric(type) && !isTypeDate(type))
	    continue;
	  result = i;
	  break;
	}
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to find column: " + colName + " (numeric: " + numeric + ")", e);
    }

    return result;
  }

  /**
   * Filters the columns that can be displayed. <br/>
   * Relation: Index &lt;-&gt; type (of SQL Types).
   *
   * @param rs		the Resultset to analyze
   * @return		the relation
   */
  protected Hashtable<Integer,Integer> filterColumns(ResultSet rs) {
    Hashtable<Integer,Integer>	result;
    ResultSetMetaData		meta;
    int				i;
    int				count;
    int				type;

    result = new Hashtable<Integer,Integer>();

    try {
      meta  = rs.getMetaData();
      count = meta.getColumnCount();
      for (i = 1; i <= count; i++) {
	type = meta.getColumnType(i);
	if (isTypeNumeric(type))
	  result.put(i, Types.DOUBLE);
	else if (isTypeDate(type))
	  result.put(i, type);
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to filter columns", e);
    }

    return result;
  }

  /**
   * Returns the name of the column.
   *
   * @param rs		the ResultSet to use
   * @param index	the index (1-based) of the column
   * @return		the name, or null in case of error
   */
  protected String getColumnName(ResultSet rs, int index) {
    String		result;
    ResultSetMetaData	meta;

    result = null;

    try {
      meta   = rs.getMetaData();
      result = meta.getColumnLabel(index);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to get column name at index: " + index, e);
    }

    return result;
  }

  /**
   * Returns the value at the specified index.
   *
   * @param rs		the resultset to use
   * @param index	the column index (1-based)
   * @param type	the type of the column
   * @return		the value
   */
  protected double getValue(ResultSet rs, int index, int type) {
    double	result;

    result = -1;

    try {
      if (type == Types.INTEGER)
	result = rs.getInt(index);
      else if (type == Types.DATE)
	result = rs.getDate(index).getTime();
      else if (type == Types.TIME)
	result = rs.getTime(index).getTime();
      else if (type == Types.TIMESTAMP)
	result = rs.getTimestamp(index).getTime();
      else
	result = rs.getDouble(index);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to get value at index=" + index + " of type=" + type, e);
    }

    return result;
  }

  /**
   * Retrieves sequence(s) from a given SQL statement.
   *
   * @param query	the statement to retrieve the sequences
   * @return		the sequence(s)
   */
  public Vector<XYSequence> retrieve(String query) {
    return retrieve(query, null);
  }

  /**
   * Retrieves sequence(s) from a given SQL statement.
   *
   * @param query	the statement to retrieve the sequences
   * @param nameX	the name of the column to use as x-axis, if null then
   * 			the first column will be used
   * @return		the sequence(s)
   */
  public abstract Vector<XYSequence> retrieve(String query, String nameX);

  /**
   * Returns all the templates currently stored.
   *
   * @return		the templates (name &lt;-&gt; sql relation)
   */
  public Hashtable<String,String> getTemplates() {
    Hashtable<String,String>	result;
    Enumeration<String>		names;
    String			name;

    result = new Hashtable<String,String>();

    names = (Enumeration<String>) m_Properties.propertyNames();
    while (names.hasMoreElements()) {
      name = names.nextElement();
      if (name.startsWith(PREFIX_TEMPLATE))
	result.put(
	    name.substring(PREFIX_TEMPLATE.length()).replaceAll("_", " "),
	    m_Properties.getProperty(name, ""));
    }

    return result;
  }
}
