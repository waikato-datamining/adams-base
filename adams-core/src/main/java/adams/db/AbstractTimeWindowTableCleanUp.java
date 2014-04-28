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
 * AbstractTimeWindowTableCleanUp.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.core.DateUtils;
import adams.core.base.BaseDateTime;

/**
 * Ancestor for clean up schemes that use a time window.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTimeWindowTableCleanUp
  extends AbstractTableCleanUp {

  /** for serialization. */
  private static final long serialVersionUID = 2617358965818813327L;

  /** the start date. */
  protected BaseDateTime m_StartDate;

  /** the end date. */
  protected BaseDateTime m_EndDate;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "start", "startDate",
	    BaseDateTime.infinityPast());

    m_OptionManager.add(
	    "end", "endDate",
	    BaseDateTime.infinityFuture());
  }

  /**
   * Sets the start date.
   *
   * @param value 	the start date
   */
  public void setStartDate(BaseDateTime value) {
    m_StartDate = value;
    reset();
  }

  /**
   * Returns the start date.
   *
   * @return 		the start date
   */
  public BaseDateTime getStartDate() {
    return m_StartDate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startDateTipText() {
    return "The start date for the clean-up (yyyy-MM-dd HH:mm:ss).";
  }

  /**
   * Sets the end date.
   *
   * @param value 	the end date
   */
  public void setEndDate(BaseDateTime value) {
    m_EndDate = value;
    reset();
  }

  /**
   * Returns the end date.
   *
   * @return 		the end date
   */
  public BaseDateTime getEndDate() {
    return m_EndDate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String endDateTipText() {
    return "The end date for the clean-up (yyyy-MM-dd HH:mm:ss).";
  }
  
  /**
   * Performs checks before cleaning up the table.
   * 
   * @return		null if checks successful, otherwise error message
   */
  protected String check() {
    String	result;
    
    result = super.check();
    
    if (result == null) {
      if (DateUtils.isBefore(m_StartDate.dateValue(), m_EndDate.dateValue()))
	result = "End date is earlier than start date!";
    }
    
    return result;
  }
}
