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
 * AbstractTimedFlowExecutionListenerWithTable.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;


/**
 * Ancestor for listeners that use tables and update them at regular intervals.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTimedFlowExecutionListenerWithTable
  extends AbstractFlowExecutionListenerWithTable
  implements TimedFlowExecutionListener {
  
  /** for serialization. */
  private static final long serialVersionUID = 271762138481763147L;

  /** the update interval. */
  protected int m_UpdateInterval;
  
  /** the current execution counter. */
  protected int m_Counter;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	    "update-interval", "updateInterval",
	    getDefaultUpdateInterval(), 1, null);
  }

  /**
   * Returns the default update interval.
   */
  protected int getDefaultUpdateInterval() {
    return 1;
  }
  
  /**
   * Sets the interval after which the GUI gets refreshed.
   *
   * @param value	the interval
   */
  public void setUpdateInterval(int value) {
    m_UpdateInterval = value;
    reset();
  }

  /**
   * Returns the interval after which the GUI gets refreshed.
   *
   * @return		the interval
   */
  public int getUpdateInterval() {
    return m_UpdateInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String updateIntervalTipText();
  
  /**
   * Gets called when the flow execution starts.
   */
  @Override
  public void startListening() {
    super.startListening();
    
    m_Counter = 0;
  }
  
  /**
   * Increments the counter and checks whether the GUI needs updating.
   */
  protected void incCounter() {
    m_Counter++;
    if (m_Counter % m_UpdateInterval == 0)
      updateGUI();
  }
  
  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_Counter = 0;
  }
}
