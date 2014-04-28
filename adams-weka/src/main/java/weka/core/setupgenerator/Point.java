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
 * Point.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package weka.core.setupgenerator;

import weka.core.Utils;

import java.io.Serializable;

/**
 * A multi-dimensional point.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Point<E>
  implements Serializable, Cloneable {

  /** for serialization. */
  private static final long serialVersionUID = 1061326306122503731L;
  
  /** the values in the various dimensions. */
  protected Object[] m_Values;
  
  /**
   * Initializes the point with the given values.
   * 
   * @param values	the position in the various dimensions
   */
  public Point(E[] values) {
    m_Values = (E[]) values.clone();
  }
  
  /**
   * Returns a clone of itself.
   * 
   * @return		a clone of itself
   */
  public Object clone() {
    return new Point(m_Values.clone());
  }
  
  /**
   * Returns the number of dimensions this points uses.
   * 
   * @return		the number of dimensions
   */
  public int dimensions() {
    return m_Values.length;
  }
  
  /**
   * Returns the value in the specified dimension.
   * 
   * @param dimension	the dimension to get the value for
   * @return		the value
   */
  public E getValue(int dimension) {
    return (E) m_Values[dimension];
  }

  /**
   * Determines whether or not two points are equal.
   * 
   * @param obj 	an object to be compared with this PointDouble
   * @return 		true if the object to be compared has the same values; 
   * 			false otherwise.
   */
  public boolean equals(Object obj) {
    Point<E> 	pd;
    int	i;
    
    if (obj == null)
      return false;
    
    if (!(obj instanceof Point))
      return false;
    
    pd = (Point<E>) obj;
    
    if (dimensions() != pd.dimensions())
      return false;
    
    for (i = 0; i < dimensions(); i++) {
      if (getValue(i).getClass() != pd.getValue(i).getClass())
        return false;

      if (getValue(i) instanceof Double) {
        if (!Utils.eq((Double) getValue(i), (Double) pd.getValue(i)))
          return false;
      }
      else {
        if (!getValue(i).toString().equals(pd.getValue(i).toString()))
          return false;
      }
    }
    
    return true;
  }
  
  /**
   * returns a string representation of the Point.
   * 
   * @return the point as string
   */
  public String toString() {
    String	result;
    int	i;
    double	value;
    
    result = "";

    for (i = 0; i < dimensions(); i++) {
      if (i > 0)
        result += ", ";
      
      if (getValue(i) instanceof Double) {
        value = (Double) getValue(i);
        result += Utils.doubleToString(value, 6);
      }
      else {
        result += getValue(i).toString();
      }
    }
    
    return result;
  }
}