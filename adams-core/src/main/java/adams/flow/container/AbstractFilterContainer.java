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
 * AbstractFilterContainer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import adams.core.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for filters and the filtered data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <F> 	the type of filter
 * @param <D> 	the type of data
 */
public class AbstractFilterContainer<F extends adams.data.filter.Filter, D extends adams.data.container.DataContainer>
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 5581530171877321061L;

  /** the identifier for the Filter. */
  public final static String VALUE_FILTER = "Filter";

  /** the identifier for the Data. */
  public final static String VALUE_DATA = "Data";

  /** the identifier for the Input Data. */
  public final static String VALUE_INPUT = "Input";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public AbstractFilterContainer() {
    this(null, null, null);
  }

  /**
   * Initializes the container with the filter and the associated data.
   *
   * @param input	the input data
   * @param filter	the filter
   * @param data	the dataset, can be null
   */
  public AbstractFilterContainer(Object input, F filter, D data) {
    super();

    store(VALUE_INPUT, input);
    store(VALUE_FILTER, filter);
    store(VALUE_DATA, data);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();
    initFilterHelp();
  }

  /**
   * Initializes help strings specific to the filter.
   */
  protected void initFilterHelp() {
    addHelp(VALUE_FILTER, "filter object; " + adams.data.filter.Filter.class.getName());
    addHelp(VALUE_DATA, "data object; " + Utils.classesToString(new Class[]{adams.data.container.DataContainer.class, adams.data.container.DataContainer[].class}));
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<>();

    result.add(VALUE_FILTER);
    result.add(VALUE_DATA);
    result.add(VALUE_INPUT);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return (hasValue(VALUE_FILTER) && hasValue(VALUE_DATA));
  }
}
