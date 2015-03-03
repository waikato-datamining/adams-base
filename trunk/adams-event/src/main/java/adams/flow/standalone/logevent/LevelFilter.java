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
 * LevelFilter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.logevent;

import java.util.logging.LogRecord;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;

/**
 <!-- globalinfo-start -->
 * Filters records based on their logging level, i.e., if the fall in the specified min&#47;max (inclusive).
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
 * <pre>-minimum &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: minimum)
 * &nbsp;&nbsp;&nbsp;The minimum level to accept.
 * &nbsp;&nbsp;&nbsp;default: SEVERE
 * </pre>
 * 
 * <pre>-maximum &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: maximum)
 * &nbsp;&nbsp;&nbsp;The maximum level to accept.
 * &nbsp;&nbsp;&nbsp;default: FINEST
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see	LoggingHelper#levelToOutputType(java.util.logging.Level)
 */
public class LevelFilter
  extends AbstractLogRecordFilter {

  /** for serialization. */
  private static final long serialVersionUID = 7462983936603453991L;

  /** the minimum level to accept. */
  protected LoggingLevel m_Minimum;

  /** the maximum level to accept. */
  protected LoggingLevel m_Maximum;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Filters records based on their logging level, i.e., if the fall in the specified min/max (inclusive).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "minimum", "minimum",
	    LoggingLevel.SEVERE);

    m_OptionManager.add(
	    "maximum", "maximum",
	    LoggingLevel.FINEST);
  }

  /**
   * Sets the minimum level of records to accept.
   *
   * @param value	the level
   */
  public void setMinimum(LoggingLevel value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum level of records to accept.
   *
   * @return		the level
   */
  public LoggingLevel getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumTipText() {
    return "The minimum level to accept.";
  }

  /**
   * Sets the maximum level of records to accept.
   *
   * @param value	the level
   */
  public void setMaximum(LoggingLevel value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum level of records to accept.
   *
   * @return		the level
   */
  public LoggingLevel getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maximumTipText() {
    return "The maximum level to accept.";
  }

  /**
   * Returns whether the log record is accepted or not for further processing.
   * 
   * @param record	the record to check
   * @return		true if the record's level falls in the specified min/max
   */
  @Override
  public boolean acceptRecord(LogRecord record) {
    return 
	   LoggingHelper.isAtLeast(record.getLevel(), m_Minimum.getLevel())
	&& LoggingHelper.isAtMost(record.getLevel(), m_Maximum.getLevel());
  }
}
