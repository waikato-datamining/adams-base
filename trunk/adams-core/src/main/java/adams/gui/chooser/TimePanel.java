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
 * TimePanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import java.awt.FlowLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.DateUtils;
import adams.gui.core.BasePanel;

/**
 * Allows the user to select a time (24h).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimePanel
  extends BasePanel 
  implements DateProvider {

  /** for serialization. */
  private static final long serialVersionUID = -4509241746496910365L;

  /** the spinner for the hours. */
  protected JSpinner m_SpinnerHour;

  /** the spinner for the minutes. */
  protected JSpinner m_SpinnerMinute;

  /** the spinner for the seconds. */
  protected JSpinner m_SpinnerSecond;
  
  /** the change listeners. */
  protected HashSet<ChangeListener> m_ChangeListeners;
  
  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();
    
    m_ChangeListeners = new HashSet<ChangeListener>();
  }
  
  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new FlowLayout(FlowLayout.LEFT));
    
    m_SpinnerHour = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
    m_SpinnerHour.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	notifyChangeListeners();
      }
    });
    m_SpinnerMinute = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
    m_SpinnerMinute.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	notifyChangeListeners();
      }
    });
    m_SpinnerSecond = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
    m_SpinnerSecond.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	notifyChangeListeners();
      }
    });
    
    add(m_SpinnerHour);
    add(new JLabel(":"));
    add(m_SpinnerMinute);
    add(new JLabel(":"));
    add(m_SpinnerSecond);
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
    Calendar	cal;
    
    cal = DateUtils.getCalendar();
    cal.setTime(value);
    
    m_SpinnerHour.setValue(cal.get(Calendar.HOUR_OF_DAY));
    m_SpinnerMinute.setValue(cal.get(Calendar.MINUTE));
    m_SpinnerSecond.setValue(cal.get(Calendar.SECOND));
  }
  
  /**
   * Returns the current date.
   * 
   * @return		the date in use
   */
  public Date getDate() {
    Calendar	result;
    
    result = DateUtils.getCalendar();
    result.set(Calendar.YEAR,  0);
    result.set(Calendar.MONTH, 0);
    result.set(Calendar.DAY_OF_MONTH, 1);
    result.set(Calendar.HOUR_OF_DAY, ((SpinnerNumberModel) m_SpinnerHour.getModel()).getNumber().intValue());
    result.set(Calendar.MINUTE,      ((SpinnerNumberModel) m_SpinnerMinute.getModel()).getNumber().intValue());
    result.set(Calendar.SECOND,      ((SpinnerNumberModel) m_SpinnerSecond.getModel()).getNumber().intValue());
    
    return result.getTime();
  }
  
  /**
   * Adds the listener to the internal list.
   * 
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }
  
  /**
   * Removes the listener from the internal list.
   * 
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }
  
  /**
   * Notifies all change listeners.
   */
  protected void notifyChangeListeners() {
    Iterator<ChangeListener> 	iter;
    ChangeEvent			e;
    
    synchronized(m_ChangeListeners) {
      iter = m_ChangeListeners.iterator();
      e    = new ChangeEvent(this);
      while (iter.hasNext())
	iter.next().stateChanged(e);
    }
  }

  /**
   * Sets the enabled state.
   * 
   * @param b		if true then the panel is enabled
   */
  public void setEnabled(boolean b) {
    super.setEnabled(b);
    m_SpinnerHour.setEnabled(b);
    m_SpinnerMinute.setEnabled(b);
    m_SpinnerSecond.setEnabled(b);
  }
}
