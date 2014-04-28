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
 * RowComparator.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import java.io.Serializable;
import java.util.Comparator;

import adams.core.Utils;

/**
 * For comparing rows. It is assumed that the spreadsheets that the two
 * rows belong to are compatible.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see	SpreadSheet#equalsHeader(SpreadSheet)
 */
public class RowComparator
  implements Serializable, Comparator<Row> {
  
  /** for serialization. */
  private static final long serialVersionUID = 7477661638560986045L;

  /** the column indices to use in the comparison. */
  protected int[] m_Indices;
  
  /** whether to sort ascending or descending. */
  protected boolean[] m_Ascending;
  
  /**
   * Initializes the comparator. Uses ascending sort order for all columns.
   * 
   * @param indices	the indices of the columns to compare
   */
  public RowComparator(int[] indices) {
    this(indices, null);
  }
  
  /**
   * Initializes the comparator.
   * 
   * @param indices	the indices of the columns to compare
   * @param ascending	whether to sort ascending or descending, asending if null
   */
  public RowComparator(int[] indices, boolean[] ascending) {
    if (indices == null)
      throw new IllegalArgumentException("Indices cannot be null!");
    if (ascending == null) {
      ascending = new boolean[indices.length];
      for (int i = 0; i < ascending.length; i++)
	ascending[i] = true;
    }
    if (indices.length != ascending.length)
      throw new IllegalArgumentException("Length of indices and sorting order differ: " + indices.length + " != " + ascending.length);
    
    m_Indices   = indices.clone();
    m_Ascending = ascending.clone();
  }
  
  /**
   * Returns the indices used for sorting.
   * 
   * @return		the (0-based) indices
   */
  public int[] getIndices() {
    return m_Indices;
  }
  
  /**
   * Returns whether a column is sorted in ascending or descending order.
   * 
   * @return		the sorting order
   */
  public boolean[] getAscending() {
    return m_Ascending;
  }
  
  /**
   * Compares its two arguments for order.  Returns a negative integer,
   * zero, or a positive integer as the first argument is less than, equal
   * to, or greater than the second.
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the
   * 	       first argument is less than, equal to, or greater than the
   *	       second.
   */
  @Override
  public int compare(Row o1, Row o2) {
    int		result;
    Row		header;
    int		i;
    String	key;
    Cell	cell1;
    Cell	cell2;
    int		weight;
    double	d1;
    double	d2;
    
    result = 0;
    header = o1.getOwner().getHeaderRow();
    i      = 0;
    while ((result == 0) && (i < m_Indices.length)) {
      key   = header.getCellKey(m_Indices[i]);
      cell1 = o1.getCell(key);
      cell2 = o2.getCell(key);

      if ((cell1 == null) && (cell2 == null))
	result = 0;
      else if (cell1 == null)
	result = -1;
      else if (cell2 == null)
	result = +1;
      else if (cell1.isMissing() && cell2.isMissing())
	result = 0;
      else if (cell1.isMissing())
	result = -1;
      else if (cell2.isMissing())
	result = +1;
      else if (cell1.isNumeric() && cell2.isNumeric()) {
	d1 = cell1.toDouble();
	d2 = cell2.toDouble();
	if (d1 < d2)
	  result = -1;
	else if (d1 == d2)
	  result = 0;
	else
	  result = +1;
	//result = cell1.toDouble().compareTo(cell2.toDouble());
      }
      else if (cell1.isDate() && cell2.isDate())
	result = cell1.toDate().compareTo(cell2.toDate());
      else if (cell1.isTime() && cell2.isTime())
	result = cell1.toTime().compareTo(cell2.toTime());
      else if (cell1.isDateTime() && cell2.isDateTime())
	result = cell1.toDateTime().compareTo(cell2.toDateTime());
      else if (cell1.isObject() && cell2.isObject() && (cell1.getObject() instanceof Comparable) && (cell2.getObject() instanceof Comparable))
	result = ((Comparable) cell1.getObject()).compareTo((Comparable) cell2.getObject());
      else
	result = cell1.getContent().compareTo(cell2.getContent());

      if (!m_Ascending[i])
	result = -result;
      
      // add weight to index
      weight = (int) Math.pow(10, (m_Indices.length - i));
      result *= weight;
      
      i++;
    }
    
    return result;
  }

  /**
   * Returns a short description of the comparator.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return "indices=" + Utils.arrayToString(m_Indices) + ", asc=" + Utils.arrayToString(m_Ascending);
  }
}
