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
 * HashableInstanceUsingSum.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.core;

/**
 * Computes the hashcode as sum of the internal double values.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HashableInstanceUsingSum
  extends AbstractHashableInstance {
  
  /** for serialization. */
  private static final long serialVersionUID = -2947327008088220713L;

  /**
   * Initializes the wrapper. Class is included in hashcode by default.
   * 
   * @param data	the instance to wrap
   */
  public HashableInstanceUsingSum(Instance data) {
    super(data);
  }

  /**
   * Computes the hashcode.
   * 
   * @return		the hash code
   * @see	`	{@link #m_ExcludeClass}
   */
  @Override
  protected int computeHashCode() {
    int		result;
    int		i;
    int		index;
    
    result = 0;
    
    if (m_Data instanceof SparseInstance) {
      for (i = 0; i < m_Data.numValues(); i++) {
	index = m_Data.index(i);
	if (index == m_Data.classIndex() && m_ExcludeClass)
	  continue;
	result += new Double(m_Data.valueSparse(i)).hashCode();
      }
    }
    else {
      for (i = 0; i < m_Data.numAttributes(); i++) {
	if (i == m_Data.classIndex() && m_ExcludeClass)
	  continue;
	result += new Double(m_Data.value(i)).hashCode();
      }
    }
    if (!m_ExcludeWeight)
      result += new Double(m_Data.weight()).hashCode();
    
    return result;
  }

}
