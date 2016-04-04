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
 * FlowWorkerHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow;

import adams.core.CleanUpHandler;
import adams.core.StatusMessageHandler;
import adams.flow.core.Actor;
import adams.gui.core.BaseSplitPane;
import adams.gui.tools.VariableManagementPanel;

/**
 * Interface for classes that can make use of the {@link FlowWorker} worker
 * class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface FlowWorkerHandler
  extends StatusMessageHandler, CleanUpHandler {

  /**
   * updates the enabled state etc. of all the GUI elements.
   */
  public void update();

  /**
   * Returns whether the flow is flagged as modified.
   *
   * @return		true if the flow is modified
   */
  public boolean isModified();

  /**
   * Returns whether the flow gets executed in headless mode.
   *
   * @return		true if the flow gets executed in headless mode
   */
  public boolean isHeadless();

  /**
   * Returns whether the GC gets called after the flow execution.
   *
   * @return		true if to run GC
   */
  public boolean getRunGC();

  /**
   * Sets the flow that was last executed.
   *
   * @param actor	the flow
   */
  public void setLastFlow(Actor actor);

  /**
   * Returns the last executed flow (if any).
   *
   * @return		the flow, null if not available
   */
  public Actor getLastFlow();

  /**
   * Finishes up the execution, setting the worker to null.
   */
  public void finishedExecution();

  /**
   * Removes the notification.
   */
  public void clearNotification();

  /**
   * Displays the notification text.
   *
   * @param msg		the text to display
   * @param error	true if error message
   */
  public void showNotification(String msg, boolean error);

  /**
   * Returns the split pane.
   *
   * @return		the split pane
   */
  public BaseSplitPane getSplitPane();

  /**
   * Returns the panel with the variables.
   *
   * @return		the panel, null if not available
   */
  public VariableManagementPanel getVariablesPanel();
}
