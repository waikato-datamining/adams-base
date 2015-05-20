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
 * AbstractMultiFilter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.logevent;


/**
 <!-- globalinfo-start -->
 * Inverts the filtering result of the base filter.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-filter &lt;adams.flow.standalone.logevent.AbstractLogRecordFilter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The filter which filter outcome gets inverted.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.logevent.AcceptAllFilter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiFilter
  extends AbstractLogRecordFilter {

  /** for serialization. */
  private static final long serialVersionUID = 7462983936603453991L;

  /** the base filters. */
  protected AbstractLogRecordFilter[] m_Filters;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filters",
	    new AbstractLogRecordFilter[]{new AcceptAllFilter()});
  }

  /**
   * Sets the filters to use.
   *
   * @param value	the filters
   */
  public void setFilters(AbstractLogRecordFilter[] value) {
    m_Filters = value;
    reset();
  }

  /**
   * Returns the filters in use.
   *
   * @return		the filters
   */
  public AbstractLogRecordFilter[] getFilters() {
    return m_Filters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String filtersTipText();
}
