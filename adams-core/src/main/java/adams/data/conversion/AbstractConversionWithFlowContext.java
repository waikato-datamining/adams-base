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
 * AbstractConversionWithFlowContext.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

/**
 * Ancestor for conversions that require a flow context.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractConversionWithFlowContext
  extends AbstractConversion
  implements FlowContextHandler {

  private static final long serialVersionUID = 9018096484709428360L;

  /** the flow context. */
  protected transient Actor m_FlowContext;

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  @Override
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  @Override
  public Actor getFlowContext() {
    return m_FlowContext;
  }
}
