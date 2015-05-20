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
 * ReportFilter.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.container.DataContainer;
import adams.data.report.AbstractReportFilter;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * A filter that modifies the reports of data containers being passed through. The supplied report filter updates/modifies the report.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-debug-out &lt;java.io.File&gt; (property: debugOutputFilePrefix)
 *         If the file is not pointing to a directory, then the filtered data gets
 *         dumped to a file with a filename consisting of this prefix, the database
 *          ID and the extension 'chrom'.
 *         default: .
 * </pre>
 *
 * <pre>-filter &lt;gcms.data.quantitation.AbstractReportFilter [options]&gt; (property: filter)
 *         The filter to use for updating/modifying the quantitation report.
 *         default: gcms.data.quantitation.PassThrough -debug-out .
 * </pre>
 *
 * Default options for gcms.data.quantitation.PassThrough (-filter/filter):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-debug-out &lt;java.io.File&gt; (property: debugOutputFilePrefix)
 *         If the file is not pointing to a directory, then the filtered data gets
 *         dumped to a file with a filename consisting of this prefix, the database
 *          ID and the extension 'chrom'.
 *         default: .
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to filter
 */
public class ReportFilter<T extends DataContainer>
  extends AbstractDatabaseConnectionFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -754895778604425899L;

  /** the report filter. */
  protected AbstractReportFilter<T> m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "A filter that modifies the reports of data containers "
      + "being passed through. The supplied report filter "
      + "updates/modifies the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new adams.data.report.PassThrough());
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(AbstractReportFilter value) {
    m_Filter = value;
    updateDatabaseConnection();
    reset();
  }

  /**
   * Returns the current filter.
   *
   * @return		the filter
   */
  public AbstractReportFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String filterTipText() {
    return "The filter to use for updating/modifying the report.";
  }

  /**
   * Updates the database connection in the filter.
   */
  protected void updateDatabaseConnection() {
    if (m_Filter instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Filter).setDatabaseConnection(getDatabaseConnection());
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected T processData(T data) {
    T	result;

    result = m_Filter.filter((T) data.getClone());
    // free up memory
    m_Filter.cleanUp();

    return result;
  }
}
