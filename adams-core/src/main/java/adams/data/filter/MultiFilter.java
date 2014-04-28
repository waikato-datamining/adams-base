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
 * MultiFilter.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * A meta-filter that runs multiple filters over the data.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-filter &lt;adams.data.filter.AbstractFilter [options]&gt; [-filter ...] (property: subFilters)
 *         The array of filters to use.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to pass through the filter
 */
public class MultiFilter<T extends DataContainer>
  extends AbstractDatabaseConnectionFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = 805661569976845842L;

  /** the filters. */
  protected AbstractFilter<T>[] m_Filters;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A meta-filter that runs multiple filters over the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "subFilters",
	    new AbstractFilter[]{new PassThrough<T>()});
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String subFiltersTipText() {
    return "The array of filters to use.";
  }

  /**
   * Sets the filters to use.
   *
   * @param value	the filters to use
   */
  public void setSubFilters(AbstractFilter<T>[] value) {
    if (value != null) {
      m_Filters = value;
      updateDatabaseConnection();
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": filters cannot be null!");
    }
  }

  /**
   * Returns the filters in use.
   *
   * @return		the filters
   */
  public AbstractFilter<T>[] getSubFilters() {
    return m_Filters;
  }

  /**
   * Updates the database connection in the sub-filters.
   */
  @Override
  protected void updateDatabaseConnection() {
    for (AbstractFilter filter: m_Filters) {
      if (filter instanceof DatabaseConnectionHandler)
	((DatabaseConnectionHandler) filter).setDatabaseConnection(getDatabaseConnection());
    }
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T			result;
    int			i;
    T			input;
    T			output;
    AbstractFilter<T>	filter;

    input  = data;
    output = data;  // in case there are no filters provided

    for (i = 0; i < m_Filters.length; i++) {
      getLogger().info(
	    "Filter " + (i+1) + "/" + m_Filters.length + ": "
	    + OptionUtils.getCommandLine(m_Filters[i]));

      filter = m_Filters[i];
      output = filter.filter(input);
      filter.cleanUp();

      // prepare input for next filter
      input = output;
    }

    getLogger().info("Finished!");

    // final output
    result = (T) output.getClone();

    return result;
  }
}
