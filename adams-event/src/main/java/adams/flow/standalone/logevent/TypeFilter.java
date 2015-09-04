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
 * TypeFilter.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.logevent;

import adams.core.logging.LoggingLevel;

import java.util.logging.LogRecord;

/**
 <!-- globalinfo-start -->
 * Filters records based on their output type (INFO, DEBUG, ERROR).
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
 * <pre>-type &lt;INFO|ERROR|DEBUG&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of records to accept.
 * &nbsp;&nbsp;&nbsp;default: INFO
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TypeFilter
  extends AbstractLogRecordFilter {

  /** for serialization. */
  private static final long serialVersionUID = 7462983936603453991L;

  /** the type of log record to accept. */
  protected LoggingLevel m_Level;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Filters records based on their logging level.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "level", "level",
	    LoggingLevel.INFO);
  }

  /**
   * Sets the logging level of records to accept.
   *
   * @param value	the level
   */
  public void setLevel(LoggingLevel value) {
    m_Level = value;
    reset();
  }

  /**
   * Returns the logging level of records to accept.
   *
   * @return		the level
   */
  public LoggingLevel getLevel() {
    return m_Level;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String levelTipText() {
    return "The type of records to accept.";
  }

  /**
   * Returns whether the log record is accepted or not for further processing.
   * 
   * @param record	the record to check
   * @return		true if the record's level matches the specified type
   */
  @Override
  public boolean acceptRecord(LogRecord record) {
    return (m_Level == getLevel());
  }
}
