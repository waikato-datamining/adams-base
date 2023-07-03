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
 * AbstractSimpleOutput.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.simplelogging.output;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;

/**
 * Ancestor for output schemes.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSimpleOutput
  extends AbstractOptionHandler
  implements SimpleOutput {

  private static final long serialVersionUID = 5393500867826251394L;

  /** the flow context. */
  protected transient Actor m_FlowContext;

  /**
   * Sets the flow context.
   *
   * @param value	the context
   */
  @Override
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context.
   *
   * @return		the context, null if not available
   */
  @Override
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns whether flow context is really required.
   *
   * @return true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return false;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for checks before attempting to log the message.
   * <br>
   * Default implementation accepts everything and only ensures that
   * flow context is set if required.
   *
   * @param msg		the message to log
   * @return		null if checks passed, otherwise error message
   */
  protected String check(String msg) {
    if (requiresFlowContext() && (getFlowContext() == null))
      return "Requires flow context, but none set!";

    return null;
  }

  /**
   * Logs the (formatted) logging message.
   *
   * @param msg		the message to log
   * @return		null if successful, otherwise error message
   */
  protected abstract String doLogMessage(String msg);

  /**
   * Logs the (formatted) logging message.
   *
   * @param msg		the message to log
   * @return		null if successful, otherwise error message
   */
  public String logMessage(String msg) {
    String	result;

    result = check(msg);
    if (result == null)
      result = doLogMessage(msg);

    return result;
  }
}
