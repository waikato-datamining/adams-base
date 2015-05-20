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
 * Compatibility.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import java.io.Serializable;
import java.util.HashSet;

import adams.core.ClassLocator;

/**
 * Class that determines compatibility between inputs and outputs.
 * <br><br>
 * An input and output are compatible, if...
 * <ul>
 *   <li>either output or input is Unknown.class</li>
 *   <li>input is Object.class</li>
 *   <li>output and input are the same class</li>
 * </ul>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Unknown
 */
public class Compatibility
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -8139225807701691972L;

  /** whether to use strict compatibility, ie, no special handling for Unknown/Object. */
  protected boolean m_Strict;

  /**
   * Sets whether to use strict or relaxed compatibility checks. "Strict" does
   * not cater for Unknown/Object.
   *
   * @param value	if true strict mode is enabled
   */
  public void setStrict(boolean value) {
    m_Strict = value;
  }

  /**
   * Returns whether strict or relaxed compatibility checks are used.
   *
   * @return		true if strict mode is enabled
   */
  public boolean isStrict() {
    return m_Strict;
  }

  /**
   * Checks whether the two classes are compatible.
   *
   * @param output	the generated output of the first actor
   * @param input	the accepted input of the second actor
   * @return		true if compatible
   */
  public boolean isCompatible(Class output, Class input) {
    // unknown matches always
    if (!m_Strict) {
      if ((input == Unknown.class) || (output == Unknown.class))
	return true;
    }

    // both arrays?
    if (output.isArray() && input.isArray())
      return isCompatible(output.getComponentType(), input.getComponentType());

    if (output.isArray() != input.isArray())
      return false;

    if ((input == Object.class) && !m_Strict) {
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
   * Checks whether the two actors are compatible.
   *
   * @param output	the generating actor
   * @param input	the accepting actor
   * @return		true if compatible
   */
  public boolean isCompatible(OutputProducer output, InputConsumer input) {
    return isCompatible(output.generates(), input.accepts());
  }

  /**
   * Returns all the classes that the two actors have in common, in producing
   * and consuming.
   *
   * @param output	the generating actor
   * @param input	the accepting actor
   * @return		the classes that are in common
   */
  public HashSet<Class> getCompatibleClasses(OutputProducer output, InputConsumer input) {
    HashSet<Class>	result;
    Class[]		outCls;
    Class[]		inCls;
    int			i;
    int			n;

    result = new HashSet<Class>();

    outCls = output.generates();
    inCls  = input.accepts();

    for (i = 0; i < outCls.length; i++) {
      for (n = 0; n < inCls.length; n++) {
	if (isCompatible(outCls[i], inCls[n])) {
	  result.add(outCls[i]);
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Returns a short string representation of this object.
   *
   * @return		the string representation
   */
  public String toString() {
    return getClass().getName() + ": strict=" + isStrict();
  }
}
