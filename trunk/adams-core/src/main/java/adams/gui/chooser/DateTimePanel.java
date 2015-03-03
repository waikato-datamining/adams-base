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
 * DateTimePanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.Date;

import adams.core.DateUtils;
import adams.gui.core.BasePanel;

/**
 * A combined panel that allows the user to choose date and time.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DateTimePanel
  extends BasePanel
  implements DateProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6960393229067593340L;

  /** the date panel. */
  protected DatePanel m_PanelDate;
  
  /** the time panel. */
  protected TimePanel m_PanelTime;
  
  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_PanelDate = new DatePanel();
    add(m_PanelDate, BorderLayout.CENTER);
    
    m_PanelTime = new TimePanel();
    add(m_PanelTime, BorderLayout.SOUTH);
  }
  
  /**
   * Sets the current time.
   */
  protected void finishInit() {
    super.finishInit();
    
    setDate(new Date());
  }
  
  /**
   * Sets the current date.
   * 
   * @param value	the date to use
   */
  public void setDate(Date value) {
    m_PanelDate.setDate(value);
    m_PanelTime.setDate(value);
  }
  
  /**
   * Returns the current date.
   * 
   * @return		the date in use
   */
  public Date getDate() {
    Calendar	result;
    Calendar	calDate;
    Calendar	calTime;
    
    calDate = DateUtils.getCalendar();
    calDate.setTime(m_PanelDate.getDate());
    
    calTime = DateUtils.getCalendar();
    calTime.setTime(m_PanelTime.getDate());

    result = DateUtils.getCalendar();
    result.set(Calendar.YEAR, calDate.get(Calendar.YEAR));
    result.set(Calendar.MONTH, calDate.get(Calendar.MONTH));
    result.set(Calendar.DAY_OF_MONTH, calDate.get(Calendar.DAY_OF_MONTH));
    result.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
    result.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
    result.set(Calendar.SECOND, calTime.get(Calendar.SECOND));
    
    return result.getTime();
  }

  /**
   * Sets the enabled state.
   * 
   * @param b		if true then the panel is enabled
   */
  public void setEnabled(boolean b) {
    super.setEnabled(b);
    m_PanelDate.setEnabled(b);
    m_PanelTime.setEnabled(b);
  }
}
