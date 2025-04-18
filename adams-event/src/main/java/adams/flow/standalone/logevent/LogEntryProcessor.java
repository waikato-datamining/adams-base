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
 * LogEntryProcessor.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.logevent;

import adams.core.Properties;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.db.LogEntry;

import java.util.Date;
import java.util.logging.LogRecord;

/**
 <!-- globalinfo-start -->
 * Turns the log record into a simple string.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogEntryProcessor
  extends AbstractLogRecordProcessor<LogEntry> {

  /** for serialization. */
  private static final long serialVersionUID = -5912851978968280646L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the log record into a log entry.";
  }

  /**
   * Turns the log record into an output.
   * 
   * @param record	the record to process
   * @return		the generated output, null if none generated
   */
  @Override
  public LogEntry processRecord(LogRecord record) {
    LogEntry	result;
    Properties	props;
    String	msg;

    result = new LogEntry();
    msg    = record.getMessage() + "\n";
    if (record.getThrown() != null)
      msg += LoggingHelper.throwableToString(record.getThrown()) + "\n";
    props  = new Properties();
    props.setProperty("Message", msg);
    result.setGeneration(new Date());
    result.setSource(record.getLoggerName());
    result.setType(LoggingLevel.valueOf(record.getLevel()).toString());
    result.setStatus(LogEntry.STATUS_NEW);
    result.setMessage(props);

    return result;
  }
  
  /**
   * Returns the class that the processor generates (used in the flow).
   * 
   * @return		the generated class
   */
  @Override
  public Class generates() {
    return LogEntry.class;
  }
}
