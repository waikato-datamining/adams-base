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
 * AbstractIndexedSplitsRunsCompatibility.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunspredictions;

import adams.core.MessageCollection;
import adams.core.option.AbstractOptionHandler;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Actor;

/**
 * Ancestor for generates predictions on indexed splits runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the accepted input data
 */
public abstract class AbstractIndexedSplitsRunsPredictions<T>
  extends AbstractOptionHandler
  implements IndexedSplitsRunsPredictions<T> {

  private static final long serialVersionUID = -7790713819190384379L;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** whether the evaluation was stopped. */
  protected boolean m_Stopped;

  /**
   * Sets the flow context.
   *
   * @param value the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns whether flow context is actually required.
   *
   * @return		true if required
   */
  public abstract boolean requiresFlowContext();

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * The default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * For checking the data.
   *
   * @param data	the data to use for evaluation
   * @param runs 	the indexed splits to use
   * @return		null if passed checks, otherwise error message
   */
  public String check(T data, IndexedSplitsRuns runs) {
    if (data == null)
      return "No data supplied!";
    if (runs == null)
      return "No indexed splits runs supplied!";
    if (requiresFlowContext() && (m_FlowContext == null))
      return "No flow context supplied!";
    return null;
  }

  /**
   * Generates predictions by applying the indexed splits runs to the data.
   *
   * @param data	the data to use for evaluation
   * @param runs 	the indexed splits to use
   * @param errors 	for collecting errors
   * @return		the generated predictions, null in case of error
   */
  protected abstract SpreadSheet doGenerate(T data, IndexedSplitsRuns runs, MessageCollection errors);

  /**
   * Generates predictions by applying the indexed splits runs to the data.
   *
   * @param data	the data to use for evaluation
   * @param runs 	the indexed splits to use
   * @param errors 	for collecting errors
   * @return		the generated evaluations, null in case of error
   */
  public SpreadSheet generate(T data, IndexedSplitsRuns runs, MessageCollection errors) {
    SpreadSheet	result;
    String 	msg;

    m_Stopped = false;

    msg = check(data, runs);
    if (msg != null) {
      errors.add(msg);
      return null;
    }

    result = doGenerate(data, runs, errors);

    if (m_Stopped) {
      errors.add("Evaluation was stopped!");
      return null;
    }
    else {
      return result;
    }
  }
}
