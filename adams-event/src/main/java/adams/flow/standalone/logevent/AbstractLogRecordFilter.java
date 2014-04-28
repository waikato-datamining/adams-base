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
 * AbstractLogRecordFilter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.logevent;

import java.util.logging.LogRecord;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for log record filters.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractLogRecordFilter
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8979323272758982220L;

  /**
   * Returns whether the log record is accepted or not for further processing.
   * 
   * @param record	the record to check
   * @return		true if accepted
   */
  public abstract boolean acceptRecord(LogRecord record);
}
