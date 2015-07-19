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
 * DelayedActionRunnable.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.Utils;
import adams.flow.core.RunnableWithLogging;

/**
 * Runnable that executes actions after the specified delay has been reached.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DelayedActionRunnable
  extends RunnableWithLogging {

  private static final long serialVersionUID = 7479660819444650932L;

  /**
   * Ancestor for actions to be executed after a delay.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class AbstractAction {

    /** the owning thread. */
    protected DelayedActionRunnable m_Owner;

    /**
     * Initializes the action.
     *
     * @param owner	the owning runnable
     */
    protected AbstractAction(DelayedActionRunnable owner) {
      m_Owner = owner;
    }

    /**
     * Returns the owning runnable.
     *
     * @return		the owner
     */
    public DelayedActionRunnable getOwner() {
      return m_Owner;
    }

    /**
     * Executes the action.
     *
     * @return		null if successful, otherwise error message
     */
    public abstract String execute();
  }

  /** the delay in msec. */
  protected int m_Delay;

  /** the increment for counting down. */
  protected int m_Decrement;

  /** the remaining delay. */
  protected int m_Remaining;

  /** the action to execute. */
  protected AbstractAction m_Action;

  /**
   * Initializes the runnable with
   */
  public DelayedActionRunnable(int delay, int decrement) {
    super();
    m_Delay     = delay;
    m_Decrement = decrement;
    m_Action    = null;
  }

  /**
   * Performs the actual execution.
   */
  @Override
  protected void doRun() {
    String	msg;

    while (m_Running) {
      if (m_Action != null) {
	if (m_Remaining > 0) {
	  m_Remaining -= m_Decrement;
	  Utils.wait(this, m_Decrement, m_Decrement);
	}
	else {
	  msg = m_Action.execute();
	  if (msg != null)
	    getLogger().severe(msg);
	  m_Action = null;
	}
      }
      else {
	Utils.wait(this, m_Decrement, m_Decrement);
      }
    }
  }

  /**
   * Resets the count down.
   */
  protected void reset() {
    m_Remaining = m_Delay;
  }

  /**
   * Queues the action for execution after the countdown.
   *
   * @param action	the action to queue
   */
  public void queue(AbstractAction action) {
    reset();
    m_Action = action;
  }
}
