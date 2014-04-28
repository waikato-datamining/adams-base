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

/*
 * TimeWindowDatabaseTool.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.tools;

import adams.core.base.BaseDateTime;

/**
 * Abstract ancestor for database tools that act within a time frame.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTimeWindowDatabaseTool
  extends AbstractDatabaseTool {

  /** for serialization. */
  private static final long serialVersionUID = 253961492399585127L;

  /** the start date of the window. */
  protected BaseDateTime m_StartDate;

  /** the end date of the window. */
  protected BaseDateTime m_EndDate;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "start-date", "startDate",
	    new BaseDateTime(BaseDateTime.INF_PAST));

    m_OptionManager.add(
	    "end-date", "endDate",
	    new BaseDateTime(BaseDateTime.INF_FUTURE));
  }

  /**
   * Sets the start date.
   *
   * @param value	the start date
   */
  public void setStartDate(BaseDateTime value) {
    m_StartDate = value;
  }

  /**
   * Returns the start date.
   *
   * @return		the start date
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
    return "The start date of the window (format: '" + BaseDateTime.FORMAT + "')";
  }

  /**
   * Sets the end date.
   *
   * @param value	the end date
   */
  public void setEndDate(BaseDateTime value) {
    m_EndDate = value;
  }

  /**
   * Returns the end date.
   *
   * @return		the end date
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
    return "The end date of the window (format: '" + BaseDateTime.FORMAT + "')";
  }
}
