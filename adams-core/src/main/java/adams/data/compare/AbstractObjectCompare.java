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
 * AbstractObjectCompare.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.compare;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that compare objects and return a result.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the output of the comparison
 */
public abstract class AbstractObjectCompare<T, R>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = -5679831532921630787L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the classes that it can handle.
   *
   * @return		the array of classes
   */
  public abstract Class[] accepts();

  /**
   * Returns the type of output that it generates.
   *
   * @return		the class of the output
   */
  public abstract Class generates();

  /**
   * Checks the provided objects.
   * <br>
   * Default implementation only ensures that objects are not null.
   *
   * @param o1		the first object
   * @param o2		the second object
   */
  protected void check(T o1, T o2) {
    if (o1 == null)
      throw new IllegalStateException("First object is null!");
    if (o2 == null)
      throw new IllegalStateException("Second object is null!");
  }

  /**
   * Performs the actual comparison of the two objects.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		the result of the comparison
   */
  protected abstract R doCompareObjects(T o1, T o2);

  /**
   * Compares the two objects.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		the result of the comparison
   */
  public R compareObjects(T o1, T o2) {
    check(o1, o2);
    return doCompareObjects(o1, o2);
  }
}
