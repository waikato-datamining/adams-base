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
 * NotFilter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.logevent;

import java.util.logging.LogRecord;

/**
 <!-- globalinfo-start -->
 * Inverts the filtering result of the base filter.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
public class NotFilter
  extends AbstractLogRecordFilter {

  /** for serialization. */
  private static final long serialVersionUID = 7462983936603453991L;

  /** the base filter to invert. */
  protected AbstractLogRecordFilter m_Filter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inverts the filtering result of the base filter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new AcceptAllFilter());
  }

  /**
   * Sets the filter to invert.
   *
   * @param value	the filter
   */
  public void setFilter(AbstractLogRecordFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter to invert.
   *
   * @return		the filter
   */
  public AbstractLogRecordFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter which filter outcome gets inverted.";
  }

  /**
   * Returns whether the log record is accepted or not for further processing.
   * 
   * @param record	the record to check
   * @return		always true
   */
  @Override
  public boolean acceptRecord(LogRecord record) {
    return !m_Filter.acceptRecord(record);
  }
}
