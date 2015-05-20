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
 * AbstractRandomNumberGenerator.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;

/**
 * Ancestor for random number generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of random number to return
 */
public abstract class AbstractRandomNumberGenerator<T extends Number>
  extends AbstractOptionHandler
  implements RandomNumberGenerator<T> {

  /** for serialization. */
  private static final long serialVersionUID = 5803268124112742362L;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
  }

  /**
   * Resets the generator.
   */
  @Override
  public void reset() {
    super.reset();
  }

  /**
   * Performs optional checks.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void check() {
  }

  /**
   * Returns the next random number. Does the actual computation.
   *
   * @return		the next number
   */
  protected abstract T doNext();

  /**
   * Returns the nexct random number.
   *
   * @return		the next number
   */
  public synchronized T next() {
    check();
    return doNext();
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractRandomNumberGenerator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractRandomNumberGenerator shallowCopy(boolean expand) {
    return (AbstractRandomNumberGenerator) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of statistics.
   *
   * @return		the statistic classnames
   */
  public static String[] getGenerators() {
    return ClassLister.getSingleton().getClassnames(AbstractRandomNumberGenerator.class);
  }

  /**
   * Instantiates the statistic with the given options.
   *
   * @param classname	the classname of the statistic to instantiate
   * @param options	the options for the statistic
   * @return		the instantiated statistic or null if an error occurred
   */
  public static AbstractRandomNumberGenerator forName(String classname, String[] options) {
    AbstractRandomNumberGenerator	result;

    try {
      result = (AbstractRandomNumberGenerator) OptionUtils.forName(AbstractRandomNumberGenerator.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the statistic from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			statistic to instantiate
   * @return		the instantiated statistic
   * 			or null if an error occurred
   */
  public static AbstractRandomNumberGenerator forCommandLine(String cmdline) {
    return (AbstractRandomNumberGenerator) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
