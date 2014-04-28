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
 * RangeCheckHelper.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import weka.core.Instance;
import weka.core.Utils;

/**
 * Helper class for generating range checks.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RangeCheckHelper {

  /**
   * Calculates the percentage of how much the value is outside the range.
   * 
   * @param value	the value to calculate the percentage for
   * @param min		the minimum of the range
   * @param max		the maximum of the range
   * @return		the generated string
   */
  protected static String computePercentage(double value, double min, double max) {
    if (max - min == 0)
      return "N/A";
    if (value < min)
      return Utils.doubleToString(((min - value) / (max - min)) * 100, 2) + "%";
    else
      return Utils.doubleToString(((value - max) / (max - min)) * 100, 2) + "%";
  }
  
  /**
   * Performs a check for the given instance, whether it exceeds the range.
   * The attribute must be numeric.
   * 
   * @param inst	the instance to check
   * @param attIndex	the attribute to check
   * @param min		the minimum of the range (incl)
   * @param max		the maximum of the range (excl)
   * @return		null if not outside, otherwise error message
   */
  public static String isOutside(Instance inst, int attIndex, double min, double max) {
    double	value;
    
    if (!inst.attribute(attIndex).isNumeric())
      return null;
    
    value = inst.value(attIndex);
    
    if (value < min)
      return 
	  inst.attribute(attIndex).name() 
	  + ": " 
	  + Utils.doubleToString(value, 8) 
	  + " < " 
	  + Utils.doubleToString(min, 8) 
	  + " (" + computePercentage(value, min, max) + ")";

    if (value > max)
      return 
	  inst.attribute(attIndex).name() 
	  + ": " 
	  + Utils.doubleToString(value, 8) 
	  + " > " 
	  + Utils.doubleToString(max, 8) 
	  + " (" + computePercentage(value, min, max) + ")";

    return null;
  }
}
