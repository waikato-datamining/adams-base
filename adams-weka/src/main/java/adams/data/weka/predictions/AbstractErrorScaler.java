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
 * AbstractErrorScaler.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.predictions;

import java.util.ArrayList;

import weka.core.Capabilities;
import weka.core.CapabilitiesHandler;
import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * Ancestor for classes that scale predictions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractErrorScaler
  extends AbstractOptionHandler
  implements CapabilitiesHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8939995790855583352L;

  /**
   * Returns the capabilities of this object. Returns what types of classes
   * the scaler can handle.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public abstract Capabilities getCapabilities();

  /**
   * Scales the errors.
   *
   * @param data	the data containing the errors to scale
   * @return 		the scaled errors
   */
  public abstract ArrayList<Integer> scale(ArrayList data);

  /**
   * Returns the commandline of this object.
   *
   * @return		the commandline
   */
  public String toString() {
    return OptionUtils.getCommandLine(this);
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

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine((AbstractErrorScaler) o));
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
  public AbstractErrorScaler shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractErrorScaler shallowCopy(boolean expand) {
    return (AbstractErrorScaler) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of scalers.
   *
   * @return		the scaler classnames
   */
  public static String[] getErrorScalers() {
    return ClassLister.getSingleton().getClassnames(AbstractErrorScaler.class);
  }

  /**
   * Instantiates the scaler with the given options.
   *
   * @param classname	the classname of the scaler to instantiate
   * @param options	the options for the scaler
   * @return		the instantiated scaler or null if an error occurred
   */
  public static AbstractErrorScaler forName(String classname, String[] options) {
    AbstractErrorScaler	result;

    try {
      result = (AbstractErrorScaler) OptionUtils.forName(AbstractErrorScaler.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the scaler from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			scaler to instantiate
   * @return		the instantiated scaler
   * 			or null if an error occurred
   */
  public static AbstractErrorScaler forCommandLine(String cmdline) {
    return (AbstractErrorScaler) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
