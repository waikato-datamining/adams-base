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
 * Filter.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.core.CleanUpHandler;
import adams.core.ShallowCopySupporter;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingSupporter;
import adams.core.option.OptionHandler;
import adams.data.container.DataContainer;

/**
 * Interface for filters.
 *
 * Derived classes only have to override the <code>processData()</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options, which invalidate
 * the previously generated data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data type to pass through the filter
 */
public interface Filter<T extends DataContainer>
  extends Comparable, LoggingSupporter, LoggingLevelHandler, CleanUpHandler,
          ShallowCopySupporter<Filter>, OptionHandler {

  /**
   * Sets whether ID update is suppressed.
   *
   * @param value 	true if to suppress
   */
  public void setDontUpdateID(boolean value);

  /**
   * Returns whether ID update is suppressed.
   *
   * @return 		true if suppressed
   */
  public boolean getDontUpdateID();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dontUpdateIDTipText();

  /**
   * Resets the filter.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  public void reset();

  /**
   * Cleans up data structures, frees up memory.
   * Sets the input and generated data to null.
   */
  public void cleanUp();

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Calls cleanUp() and cleans up the options.
   */
  @Override
  public void destroy();

  /**
   * Returns the filtered data.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  public T filter(T data);

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
  public int compareTo(Object o);

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o);

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public Filter<T> shallowCopy();

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public Filter<T> shallowCopy(boolean expand);
}
