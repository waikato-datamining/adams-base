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
 * HashableInstanceUsingString.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.core;

/**
 * TODO: what this class does
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HashableInstanceUsingString
  extends AbstractHashableInstance {
  
  /** for serialization. */
  private static final long serialVersionUID = -3580405212034106182L;

  /**
   * Initializes the wrapper. Class is included in hashcode by default.
   * 
   * @param data	the instance to wrap
   */
  public HashableInstanceUsingString(Instance data) {
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
    StringBuilder	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < m_Data.numAttributes(); i++) {
      if (i == m_Data.classIndex() && m_ExcludeClass)
	continue;
      if (result.length() > 0)
	result.append(",");
      result.append(m_Data.toString(i));
    }
    if (!m_ExcludeWeight)
      result.append(":" + m_Data.weight());
    
    return result.toString().hashCode();
  }

}
