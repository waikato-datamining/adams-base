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
 * InstanceComparator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.instances;

import adams.core.Utils;
import weka.core.Instance;
import weka.core.Instances;

import java.io.Serializable;
import java.util.Comparator;

/**
 * For comparing instance objects. It is assumed that the dataset that the two
 * instance objects belong to are compatible.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class InstanceComparator
  implements Serializable, Comparator<Instance> {
  
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
  public InstanceComparator(int[] indices) {
    this(indices, null);
  }
  
  /**
   * Initializes the comparator.
   * 
   * @param indices	the indices of the columns to compare
   * @param ascending	whether to sort ascending or descending, ascending if null
   */
  public InstanceComparator(int[] indices, boolean[] ascending) {
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
  public int compare(Instance o1, Instance o2) {
    int		result;
    Instances 	header;
    int		i;
    int		weight;
    double	d1;
    double	d2;
    
    result = 0;
    header = o1.dataset();
    i      = 0;
    while ((result == 0) && (i < m_Indices.length)) {
      if (o1.isMissing(m_Indices[i]) && o2.isMissing(m_Indices[i]))
        result = 0;
      else if (o1.isMissing(m_Indices[i]))
        result = -1;
      else if (o2.isMissing(m_Indices[i]))
        result = +1;
      else if (header.attribute(m_Indices[i]).isNumeric()) {
	d1 = o1.value(m_Indices[i]);
	d2 = o2.value(m_Indices[i]);
	if (d1 < d2)
	  result = -1;
	else if (d1 == d2)
	  result = 0;
	else
	  result = +1;
      }
      else {
        result = o1.stringValue(m_Indices[i]).compareTo(o2.stringValue(m_Indices[i]));
      }

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
