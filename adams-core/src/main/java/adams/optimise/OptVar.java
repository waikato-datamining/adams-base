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
 * OptVar.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.optimise;

import java.io.Serializable;

import adams.core.CloneHandler;

/**
 * Optimisation Variables.
 *
 * @author dale
 * @version $Revision$
 */
public class OptVar
  implements Serializable, CloneHandler<OptVar> {

  /**suid.*/
  private static final long serialVersionUID = 8200214909919052160L;

  /** This variable's name. */
  public String m_name;

  /** Maximum value this variable can assume. */
  public double m_max; //scale

  /** Minimum value this variable can assume. */
  public double m_min; //scale

  /** Can this variable only have integer values? */
  public boolean m_isInteger;

  /**
   * Initialise.
   *
   * @param name	var name.
   * @param min		var min.
   * @param max		var max.
   * @param isInteger	var is an integer only?
   */
  public OptVar(String name, double min, double max, boolean isInteger) {
    m_max=max;
    m_min=min;
    m_name=name;
    m_isInteger=isInteger;
  }

  /**
   * Initialise.
   *
   * @param name	var name.
   * @param min		var min.
   * @param max		var max.
   */
  public OptVar(String name, double min, double max) {
    m_max=max;
    m_min=min;
    m_name=name;
    m_isInteger=false;
  }

  /**
   * Copy this object.
   *
   * @return copy
   */
  public OptVar getClone() {
    return(new OptVar(m_name,m_min,m_max,m_isInteger));
  }

  /**
   * Get number of splits. Recalc if int.
   *
   * @param numSplits	number of initial splits
   * @return		number of recalculated splits
   */
  public int getSteps(int numSplits) {
    if (m_isInteger) {
      if (numSplits > Math.abs((m_max-m_min)+1)) {
	return((int)Math.abs((m_max-m_min)+1));
      }
    }
    return(numSplits);
  }

  /**
   * Get step size. Recalc for int.
   *
   * @param numSplits	number of initial splits
   * @return		splits recalc
   */
  public double getStepSize(int numSplits) {
    if (m_isInteger) {
      if (numSplits > Math.abs((m_max-m_min))) {
	return(1);
      }
    }
    return((Math.abs((m_max-m_min))/(double)(numSplits-1)));
  }
  public String toString() {
    return("min="+m_min+",max="+m_max);
  }
  
}
