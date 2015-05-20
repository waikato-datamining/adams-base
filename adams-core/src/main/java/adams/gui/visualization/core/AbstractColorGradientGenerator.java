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
 * AbstractColorGradientGenerator.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * Ancestor for classes that generate color gradients.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColorGradientGenerator
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1744081070964662031L;

  /**
   * Hook method for performing checks on the setup.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void check() {
  }

  /**
   * Performs the actual generation.
   *
   * @return		the generated colors
   */
  protected abstract Color[] doGenerate();

  /**
   * Generates the color gradients.
   *
   * @return		the colors
   */
  public Color[] generate() {
    check();
    return doGenerate();
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractColorGradientGenerator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractColorGradientGenerator shallowCopy(boolean expand) {
    return (AbstractColorGradientGenerator) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of generators.
   *
   * @return		the generator classnames
   */
  public static String[] getGenerators() {
    return ClassLister.getSingleton().getClassnames(AbstractColorGradientGenerator.class);
  }

  /**
   * Instantiates the generator with the given options.
   *
   * @param classname	the classname of the generator to instantiate
   * @param options	the options for the generator
   * @return		the instantiated generator or null if an error occurred
   */
  public static AbstractColorGradientGenerator forName(String classname, String[] options) {
    AbstractColorGradientGenerator	result;

    try {
      result = (AbstractColorGradientGenerator) OptionUtils.forName(AbstractColorGradientGenerator.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the generator from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			generator to instantiate
   * @return		the instantiated generator
   * 			or null if an error occurred
   */
  public static AbstractColorGradientGenerator forCommandLine(String cmdline) {
    return (AbstractColorGradientGenerator) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
