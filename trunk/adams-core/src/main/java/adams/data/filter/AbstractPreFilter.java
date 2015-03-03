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
 * AbstractPreFilter.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.core.Mergeable;
import adams.data.container.DataContainer;

/**
 * Abstract ancestor for filters that use pre-filtered data as the basis
 * for manipulating the original data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data to process
 */
public abstract class AbstractPreFilter<T extends DataContainer & Mergeable>
  extends AbstractFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -631871037799637776L;

  /** the filter to apply to the data first. */
  protected AbstractFilter m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "A filter that removes noise from the GC data with a user-supplied "
      + "noise level algorithm.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    getDefaultFilter());
  }

  /**
   * Returns the default pre-filter to use.
   *
   * @return		the default
   */
  protected AbstractFilter getDefaultFilter() {
    return new PassThrough();
  }

  /**
   * Sets the pre-filter.
   *
   * @param value 	the filter
   */
  public void setFilter(AbstractFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the current pre-filter.
   *
   * @return 		the filter
   */
  public AbstractFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "Pre-filters the data, .";
  }

  /**
   * Performs the actual filtering, using the pre-filtered data to manipulate
   * the original data.
   *
   * @param filtered	the pref-filtered data
   * @param original	the original input data
   * @return		the final data
   */
  protected abstract T processData(T filtered, T original);

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected T processData(T data) {
    T			result;
    T			filtered;
    AbstractFilter<T>	filter;

    filter   = (AbstractFilter<T>) m_Filter.shallowCopy(true);
    filtered = filter.filter(data);
    filter.destroy();

    result = processData(filtered, data);

    return result;
  }
}
