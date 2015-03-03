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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.logevent;

import java.util.logging.LogRecord;

import adams.core.logging.LoggingHelper;
import adams.gui.core.ConsolePanel.OutputType;

/**
 <!-- globalinfo-start -->
 * Filters records based on their output type (INFO, DEBUG, ERROR).
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
 * <pre>-type &lt;INFO|ERROR|DEBUG&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of records to accept.
 * &nbsp;&nbsp;&nbsp;default: INFO
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see	LoggingHelper#levelToOutputType(java.util.logging.Level)
 */
public class TypeFilter
  extends AbstractLogRecordFilter {

  /** for serialization. */
  private static final long serialVersionUID = 7462983936603453991L;

  /** the type of log record to accept. */
  protected OutputType m_Type;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Filters records based on their output type (INFO, DEBUG, ERROR).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    OutputType.INFO);
  }

  /**
   * Sets the type of records to accept.
   *
   * @param value	the type
   */
  public void setType(OutputType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of records to accept.
   *
   * @return		the type
   */
  public OutputType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of records to accept.";
  }

  /**
   * Returns whether the log record is accepted or not for further processing.
   * 
   * @param record	the record to check
   * @return		true if the record's level matches the specified type
   * @see		LoggingHelper#levelToOutputType(java.util.logging.Level)
   */
  @Override
  public boolean acceptRecord(LogRecord record) {
    return (m_Type == LoggingHelper.levelToOutputType(record.getLevel()));
  }
}
