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
 * ClassCompatibility.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.flow.core.Unknown;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.io.Serializable;

/**
 * Class that determines compatibility between inputs and outputs.
 * <br><br>
 * An input and output are compatible, if...
 * <ul>
 *   <li>input is Object.class</li>
 *   <li>output and input are the same class</li>
 * </ul>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Unknown
 */
public class ClassCompatibility
  implements Serializable, ClassCompatibilityChecker {

  /** for serialization. */
  private static final long serialVersionUID = -8139225807701691972L;

  /**
   * Initializes the checker.
   */
  public ClassCompatibility() {
    super();
  }

  /**
   * Checks whether the two classes are compatible.
   *
   * @param output	the generated output of the first actor
   * @param input	the accepted input of the second actor
   * @return		true if compatible
   */
  public boolean isCompatible(Class output, Class input) {
    // both arrays?
    if (output.isArray() && input.isArray())
      return isCompatible(output.getComponentType(), input.getComponentType());

    if (output.isArray() != input.isArray())
      return false;

    if (input == Object.class) {
      return true;
    }
    else {
      // exact match?
      if (output == input)
	return true;
      // does input accept a superclass?
      else if (ClassLocator.isSubclass(input, output))
	return true;
      // does input accept a interface?
      else if (ClassLocator.hasInterface(input, output))
	return true;
      else
	return false;
    }
  }

  /**
   * Checks whether the two class sets are compatible.
   *
   * @param outCls	the classes of the generating actor
   * @param inCls	the classes of the accepting actor
   * @return		true if compatible
   */
  public boolean isCompatible(Class[] outCls, Class[] inCls) {
    boolean	result;
    int		i;
    int		n;

    result = false;

    for (i = 0; i < outCls.length; i++) {
      for (n = 0; n < inCls.length; n++) {
	if (isCompatible(outCls[i], inCls[n])) {
	  result = true;
	  break;
	}
      }
      if (result)
	break;
    }

    return result;
  }

  /**
   * Returns a short string representation of this object.
   *
   * @return		the string representation
   */
  public String toString() {
    return getClass().getName();
  }
}
