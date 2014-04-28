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
 * AbstractIDGenerator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.id;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * Abstract base class for ID string generators.
 *
 * Derived classes only have to override the <code>assemble(Object)</code>
 * method. The <code>reset()</code> method can be used to reset a
 * scheme's internal state, e.g., after setting options, which invalidate
 * the previously generated data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data type to accept
 */
public abstract class AbstractIDGenerator<T>
  extends AbstractOptionHandler
  implements Comparable, ShallowCopySupporter<AbstractIDGenerator> {

  /** for serialization. */
  private static final long serialVersionUID = 5141285178186856446L;

  /**
   * For performing checks on the provided data. If data doesn't pass the
   * check then just throw an IllegalStateException.
   * <p/>
   * Default implementation does nothing.
   *
   * @param o		the object to check
   */
  protected void check(Object o) {
  }

  /**
   * Generates the actual ID.
   *
   * @param o		the object to generate the ID for
   * @return		the generated ID
   */
  protected abstract String assemble(Object o);

  /**
   * For post-processing the ID.
   * <p/>
   * The default implementation does nothing.
   *
   * @param id		the ID to post-process
   * @return		the post-processed id
   * @see		#m_MakeFilename
   */
  protected String postProcess(String id) {
    return id;
  }

  /**
   * Generates the ID.
   *
   * @param o		the object to generate the ID for
   * @return		the generated ID
   */
  public String generate(Object o) {
    String	result;

    check(o);
    result = assemble(o);
    result = postProcess(result);

    return result;
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

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
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
  public AbstractIDGenerator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractIDGenerator shallowCopy(boolean expand) {
    return (AbstractIDGenerator) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of generators.
   *
   * @return		the generator classnames
   */
  public static String[] getGenerators() {
    return ClassLister.getSingleton().getClassnames(AbstractIDGenerator.class);
  }

  /**
   * Instantiates the generator with the given options.
   *
   * @param classname	the classname of the generator to instantiate
   * @param options	the options for the generator
   * @return		the instantiated generator or null if an error occurred
   */
  public static AbstractIDGenerator forName(String classname, String[] options) {
    AbstractIDGenerator	result;

    try {
      result = (AbstractIDGenerator) OptionUtils.forName(AbstractIDGenerator.class, classname, options);
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
  public static AbstractIDGenerator forCommandLine(String cmdline) {
    return (AbstractIDGenerator) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
