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
 * AbstractConditions.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.db;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.core.option.PreGetOptionslistHook;


/**
 * Abstract ancestor for classes that encapsulate conditions for database
 * retrieval.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractConditions
  extends AbstractOptionHandler
  implements PreGetOptionslistHook {

  /** for serialization. */
  private static final long serialVersionUID = 7146388930313616510L;

  /**
   * Gets executed before list of options can get obtained.
   */
  public void preGetOptionsList() {
    update();
  }

  /**
   * Automatically corrects values, but does not throw any exceptions.
   * Derived classes must override this method.
   */
  protected abstract void update();

  /**
   * Checks the correctness of the provided values, may throw unchecked
   * Exceptions.
   * <p/>
   * Default implementation merely calls "update()" to ensure corrected values.
   * Derived classes can add checks/throw exceptions.
   */
  public void check() {
    update();
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
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

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine((AbstractConditions) o));
  }

  /**
   * Returns whether the two objects are the same.
   * <p/>
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
  public AbstractConditions shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractConditions shallowCopy(boolean expand) {
    return (AbstractConditions) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of conditions.
   *
   * @return		the conditions classnames
   */
  public static String[] getFilters() {
    return ClassLister.getSingleton().getClassnames(AbstractConditions.class);
  }

  /**
   * Instantiates the conditions with the given options.
   *
   * @param classname	the classname of the conditions to instantiate
   * @param options	the options for the conditions
   * @return		the instantiated conditions or null if an error occurred
   */
  public static AbstractConditions forName(String classname, String[] options) {
    AbstractConditions	result;

    try {
      result = (AbstractConditions) OptionUtils.forName(AbstractConditions.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the conditions from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			conditions to instantiate
   * @return		the instantiated conditions
   * 			or null if an error occurred
   */
  public static AbstractConditions forCommandLine(String cmdline) {
    return (AbstractConditions) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
