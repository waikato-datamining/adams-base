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
 * AbstractNotification.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.sendnotification;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

/**
 * Ancestor for notification schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNotification
  extends AbstractOptionHandler
  implements QuickInfoSupporter, FlowContextHandler {

  private static final long serialVersionUID = -1079401368469595261L;

  /** the flow context. */
  protected Actor m_FlowContext;

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
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method before attempting to send the message.
   *
   * @param msg		the message to send
   * @return		null if successful, otherwise error message
   */
  protected String check(String msg) {
    if (m_FlowContext == null)
      return "No flow context set!";
    if (msg == null)
      return "No message provided!";
    return null;
  }

  /**
   * Sends the notification.
   *
   * @param msg		the message to send
   * @return		null if successfully sent, otherwise error message
   */
  protected abstract String doSendNotification(String msg);

  /**
   * Sends the notification.
   *
   * @param msg		the message to send
   * @return		null if successfully sent, otherwise error message
   */
  public String sendNotification(String msg) {
    String	result;

    result = check(msg);
    if (result == null) {
      if (isLoggingEnabled())
        getLogger().info(msg);
      result = doSendNotification(msg);
    }

    return result;
  }
}
