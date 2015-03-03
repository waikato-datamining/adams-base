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
 * AbstractLogRecordProcessor.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.logevent;

import java.util.logging.LogRecord;

import adams.core.option.AbstractOptionHandler;

/**
 * Processes log records.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <O> the generated output
 */
public abstract class AbstractLogRecordProcessor<O>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4423786134523474554L;

  /**
   * Turns the log record into an output.
   * 
   * @param record	the record to process
   * @return		the generated output, null if none generated
   */
  public abstract O processRecord(LogRecord record);
  
  /**
   * Returns the class that the processor generates (used in the flow).
   * 
   * @return		the generated class
   */
  public abstract Class generates();
}
