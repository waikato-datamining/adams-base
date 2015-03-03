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
 * orFilter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.logevent;

import java.util.logging.LogRecord;

/**
 <!-- globalinfo-start -->
 * Combines the filter results using a boolean OR.
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
 * <pre>-filter &lt;adams.flow.standalone.logevent.AbstractLogRecordFilter&gt; [-filter ...] (property: filters)
 * &nbsp;&nbsp;&nbsp;The filters to combine using OR.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.logevent.AcceptAllFilter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OrFilter
  extends AbstractMultiFilter {

  /** for serialization. */
  private static final long serialVersionUID = -4172095198694669640L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines the filter results using a boolean OR.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String filtersTipText() {
    return "The filters to combine using OR.";
  }

  /**
   * Returns whether the log record is accepted or not for further processing.
   * 
   * @param record	the record to check
   * @return		true if accepted
   */
  @Override
  public boolean acceptRecord(LogRecord record) {
    boolean	result;
    int		i;
    
    result = false;
    
    for (i = 0; i < m_Filters.length; i++) {
      result = result || m_Filters[i].acceptRecord(record);
      if (result)
	break;
    }
    
    return result;
  }
}
