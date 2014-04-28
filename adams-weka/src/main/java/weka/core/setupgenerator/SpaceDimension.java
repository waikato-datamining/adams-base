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
 * SpaceDimension.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package weka.core.setupgenerator;

import weka.core.SetupGenerator;
import weka.core.Utils;

import java.io.Serializable;

/**
 * Represents a single dimension in a multi-dimensional space.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpaceDimension 
  implements Serializable {
  
  /** for serialization. */
  private static final long serialVersionUID = -7709016830854739486L;

  /** the type of dimension. */
  protected int m_Type;
  
  /** the minimum on the axis. */
  protected double m_Min;
  
  /** the maximum on the axis. */
  protected double m_Max;
  
  /** the step size for the axis. */
  protected double m_Step;

  /** the label for the axis. */
  protected String m_Label;
  
  /** the number of points on the axis. */
  protected int m_Width;
  
  /** the underlying list of values. */
  protected String[] m_List;
  
  /**
   * initializes the dimension (for numeric values).
   * 
   * @param param 	the search parameter to obtain the data from
   * @throws Exception	if splitting of list fails using Utils.splitOptions(String)
   * @see		AbstractParameter#getList()
   * @see		Utils#splitOptions(String)
   */
  public SpaceDimension(AbstractParameter param) throws Exception {
    super();
    
    if (param instanceof MathParameter) {
      MathParameter math = (MathParameter) param;
      initFunction(math.getMin(), math.getMax(), math.getStep(), math.getProperty());
    }
    else if (param instanceof ListParameter) {
      ListParameter list = (ListParameter) param;
      initList(0, Utils.splitOptions(list.getList()).length - 1, Utils.splitOptions(list.getList()), list.getProperty());
    }
    else {
      throw new IllegalStateException("Parameter class '" + param.getClass().getName() + "' not handled!");
    }
  }
  
  /**
   * initializes the dimension (for numeric values).
   * 
   * @param min 	the minimum on the axis
   * @param max 	the maximum on the axis
   * @param step 	the step size for the axis
   * @param label	the label for the axis
   */
  public SpaceDimension(double min, double max, double step, String label) {
    super();
    
    initFunction(min, max, step, label);
  }
  
  /**
   * initializes the dimension (for list values).
   * 
   * @param min 	the minimum index in the list (0-based index)
   * @param max 	the maximum index in the list (0-based index)
   * @param list 	the available values
   * @param label	the label for the axis
   */
  public SpaceDimension(int min, int max, String[] list, String label) {
    super();
    
    initList(min, max, list, label);
  }
  
  /**
   * initializes the dimension (for numeric values).
   * 
   * @param min 	the minimum on the axis
   * @param max 	the maximum on the axis
   * @param step 	the step size for the axis
   * @param label	the label for the axis
   */
  protected void initFunction(double min, double max, double step, String label) {
    m_Type  = SetupGenerator.TYPE_FUNCTION;
    m_Min   = min;
    m_Max   = max;
    m_Step  = step;
    m_Label = label;
    m_Width = (int) StrictMath.round((m_Max - m_Min) / m_Step) + 1;
    m_List  = null;
    
    // is min < max?
    if (m_Min >= m_Max)
      throw new IllegalArgumentException("Min must be smaller than Max!");
    
    // steps positive?
    if (m_Step <= 0)
      throw new IllegalArgumentException("Step must be a positive number!");
    
    // check borders
    if (!Utils.eq(m_Min + (m_Width-1)*m_Step, m_Max))
      throw new IllegalArgumentException(
          "Axis doesn't match! Provided max: " + m_Max 
          + ", calculated max via min and step size: " 
          + (m_Min + (m_Width-1)*m_Step));
  }
  
  /**
   * initializes the dimension (for list values).
   * 
   * @param min 	the minimum index in the list (0-based index)
   * @param max 	the maximum index in the list (0-based index)
   * @param list 	the available values
   * @param label	the label for the axis
   */
  protected void initList(int min, int max, String[] list, String label) {
    m_Type  = SetupGenerator.TYPE_LIST;
    m_Min   = min;
    m_Max   = max;
    m_Step  = -1;
    m_Label = label;
    m_Width = max - min + 1;
    m_List  = list.clone();
    
    // min within range of list?
    if (m_Min >= m_List.length)
      throw new IllegalArgumentException(
	  "Min must be smaller than list length (min=" + min + ", list=" + list.length + ")!");

    // max within range of list?
    if (m_Max >= m_List.length)
      throw new IllegalArgumentException(
	  "Max must be smaller than list length (max=" + max + ", list=" + list.length + ")!");
    
    // is min < max?
    if (m_Min >= m_Max)
      throw new IllegalArgumentException(
	  "Min must be smaller than Max (min=" + min + ", max=" + max + ")!");
  }

  /**
   * Tests itself against the provided dimension object.
   * 
   * @param o		the dimension object to compare against
   * @return		if the two dimensions have the same setup
   */
  public boolean equals(Object o) {
    SpaceDimension	dim;
    int		i;
    
    if (o == null)
      return false;
    
    if (!(o instanceof SpaceDimension))
      return false;
    
    dim = (SpaceDimension) o;
    
    if (getType() != dim.getType())
      return false;
    
    if (getType() == SetupGenerator.TYPE_FUNCTION) {
      if (width() != dim.width())
        return false;

      if (getMin() != dim.getMin())
        return false;

      if (getStep() != dim.getStep())
        return false;

      if (!getLabel().equals(dim.getLabel()))
        return false;
    }
    else if (getType() == SetupGenerator.TYPE_LIST) {
      if (getList().length != dim.getList().length)
        return false;
      
      for (i = 0; i < getList().length; i++) {
        if (!getList()[i].equals(dim.getList()[i]))
          return false;
      }
    }
    else {
      throw new IllegalStateException("Type '" + getType() + "' not handled!");
    }
    
    return true;
  }
  
  /**
   * returns the tye of dimension.
   * 
   * @return		the type
   * @see		MultiSearch#TYPE_FUNCTION
   * @see		MultiSearch#TYPE_LIST
   */
  public int getType() {
    return m_Type;
  }
  
  /**
   * returns the left border.
   * 
   * @return 		the left border
   */
  public double getMin() {
    return m_Min;
  }
  
  /**
   * returns the right border.
   * 
   * @return 		the right border
   */
  public double getMax() {
    return m_Max;
  }
  
  /**
   * returns the step size on the axis.
   * 
   * @return 		the step size
   */
  public double getStep() {
    return m_Step;
  }
  
  /**
   * returns the label for the axis.
   * 
   * @return		the label
   */
  public String getLabel() {
    return m_Label;
  }
  
  /**
   * returns the number of points on the axis (incl. borders)
   * 
   * @return 		the number of points on the axis
   */
  public int width() {
    return m_Width;
  }
  
  /**
   * Returns the list of values, null in case of a numeric dimension that
   * is based on a mathematical function.
   * 
   * @return		the list
   */
  public String[] getList() {
    return m_List;
  }

  /**
   * returns the value at the given point in the dimension.
   * 
   * @param x		the x-th point on the axis
   * @return		the value at the given position
   */
  public Object getValue(int x) {
    if (x >= width())
      throw new IllegalArgumentException("Index out of scope on axis (" + x + " >= " + width() + ")!");

    if (getType() == SetupGenerator.TYPE_FUNCTION)
      return new Double(m_Min + m_Step*x);
    else if (getType() == SetupGenerator.TYPE_LIST)
      return new String(m_List[(int) m_Min + x]);
    else
      throw new IllegalStateException("Type '" + getType() + "' not handled!");
  }

  /**
   * returns the closest index for the given value in the dimension.
   * 
   * @param value	the value to get the index for
   * @return		the closest index in the dimension
   */
  public int getLocation(Object value) {
    int	result;
    double	distance;
    double	currDistance;
    int	i;
    String	valueStr;

    result = 0;
    
    if (getType() == SetupGenerator.TYPE_FUNCTION) {
      // determine x
      distance = m_Step;
      for (i = 0; i < width(); i++) {
        currDistance = StrictMath.abs(((Double) value) - ((Double) getValue(i)));
        if (Utils.sm(currDistance, distance)) {
          distance = currDistance;
          result   = i;
        }
      }
    }
    else if (getType() == SetupGenerator.TYPE_LIST) {
      valueStr = value.toString();
      for (i = 0; i < width(); i++) {
        if (((String) getValue(i)).equals(valueStr)) {
          result = ((int) m_Min) + i;
          break;
        }
      }
    }
    else {
      throw new IllegalStateException("Type '" + getType() + "' not handled!");
    }
    
    return result;
  }

  /**
   * checks whether the given value is on the border of the dimension.
   * 
   * @param value		the value to check
   * @return			true if the the value is on the border
   */
  public boolean isOnBorder(double value) {
    return isOnBorder(getLocation(value));
  }

  /**
   * checks whether the given location is on the border of the dimension.
   * 
   * @param location 		the location to check
   * @return			true if the the location is on the border
   */
  public boolean isOnBorder(int location) {
    if (location == 0)
      return true;
    else if (location == width() - 1)
      return true;
    else
      return false;
  }
  
  /**
   * returns a sub-dimension with the same type/step/list, but different borders.
   * 
   * @param left	the left index
   * @param right	the right index
   * @return 		the sub-dimension
   */
  public SpaceDimension subdimension(int left, int right) {
    if (getType() == SetupGenerator.TYPE_FUNCTION)
      return new SpaceDimension((Double) getValue(left), (Double) getValue(right), getStep(), getLabel());
    else if (getType() == SetupGenerator.TYPE_LIST)
      return new SpaceDimension(left, right, getList(), getLabel());
    else
      throw new IllegalStateException("Type '" + getType() + "' not handled!");
  }
  
  /**
   * Returns a string representation of the dimension.
   * 
   * @return		a string representation
   */
  public String toString() {
    if (getType() == SetupGenerator.TYPE_FUNCTION)
      return "dimension: " + getLabel() + ", min: " + getMin() + ", max: " + getMax() + ", step: " + getStep();
    else if (getType() == SetupGenerator.TYPE_LIST)
      return "dimension: " + getLabel() + ", min: " + getMin() + ", max: " + getMax() + ", list: " + Utils.arrayToString(getList());
    else
      throw new IllegalStateException("Type '" + getType() + "' not handled!");
  }
}