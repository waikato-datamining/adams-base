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
 * FilteredReportFilter.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.report;

import adams.data.container.DataContainer;
import adams.data.filter.Filter;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * This filter first pushes the data through the provided data filter before applying the actual report filter. The updated filter obtained from the report filter is then replaces the report of the original data container.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-data-filter &lt;adams.data.filter.AbstractFilter [options]&gt; (property: dataFilter)
 * &nbsp;&nbsp;&nbsp;The filter to apply to the data before pushing it through the report filter.
 * &nbsp;&nbsp;&nbsp;default: adams.data.filter.PassThrough
 * </pre>
 *
 * <pre>-report-filter &lt;adams.data.report.AbstractReportFilter [options]&gt; (property: reportFilter)
 * &nbsp;&nbsp;&nbsp;The report filter to apply to the report.
 * &nbsp;&nbsp;&nbsp;default: adams.data.report.PassThrough
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to pass through the filter
 */
public class FilteredReportFilter<T extends DataContainer>
  extends AbstractDatabaseConnectionReportFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = 8646651693938769168L;

  /** the pre-filter for filtering the data. */
  protected Filter m_DataFilter;

  /** the post-filter for filtering the report. */
  protected AbstractReportFilter m_ReportFilter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "This filter first pushes the data through the provided data filter "
      + "before applying the actual report filter. The updated filter obtained "
      + "from the report filter is then replaces the report of the original "
      + "data container.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "data-filter", "dataFilter",
	    new adams.data.filter.PassThrough());

    m_OptionManager.add(
	    "report-filter", "reportFilter",
	    new adams.data.report.PassThrough());
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
   * Sets the data filter.
   *
   * @param value	the data filter
   */
  public void setDataFilter(adams.data.filter.Filter value) {
    m_DataFilter = value;
    updateDatabaseConnection();
    reset();
  }

  /**
   * Returns the data filter.
   *
   * @return		the data filter
   */
  public adams.data.filter.Filter getDataFilter() {
    return m_DataFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String dataFilterTipText() {
    return "The filter to apply to the data before pushing it through the report filter.";
  }

  /**
   * Sets the report filter.
   *
   * @param value	the report filter
   */
  public void setReportFilter(adams.data.report.AbstractReportFilter value) {
    m_ReportFilter = value;
    updateDatabaseConnection();
    reset();
  }

  /**
   * Returns the report filter.
   *
   * @return		the report filter
   */
  public adams.data.report.AbstractReportFilter getReportFilter() {
    return m_ReportFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String reportFilterTipText() {
    return "The report filter to apply to the report.";
  }

  /**
   * Updates the database connection in dependent schemes.
   */
  @Override
  protected void updateDatabaseConnection() {
    if (m_DataFilter instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_DataFilter).setDatabaseConnection(getDatabaseConnection());
    if (m_ReportFilter instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_ReportFilter).setDatabaseConnection(getDatabaseConnection());
  }

  /**
   * Processes the data/report.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T	result;
    T	filtered;
    
    if (!(data instanceof MutableReportHandler))
      return data;
    
    filtered = (T) m_DataFilter.filter(data);
    filtered = (T) m_ReportFilter.filter(filtered);
    result   = (T) data.getClone();

    if (!((MutableReportHandler) result).hasReport())
      return result;
    
    ((MutableReportHandler) result).setReport(((ReportHandler) filtered).getReport().getClone());

    return result;
  }
}
