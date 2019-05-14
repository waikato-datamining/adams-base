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
 * MultiNotification.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.sendnotification;

import com.github.fracpete.javautils.enumerate.Enumerated;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Executes the notifications sequentially.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiNotification
  extends AbstractNotification {

  private static final long serialVersionUID = -1644255635213178882L;

  /** the notifications to combine. */
  protected AbstractNotification[] m_Notifications;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the notifications sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "notification", "notifications",
      new AbstractNotification[0]);
  }

  /**
   * Sets the notifications to use.
   *
   * @param value 	the notifications
   */
  public void setNotifications(AbstractNotification[] value) {
    m_Notifications = value;
    reset();
  }

  /**
   * Returns the notifications in use.
   *
   * @return 		the notifications
   */
  public AbstractNotification[] getNotifications() {
    return m_Notifications;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String notificationsTipText() {
    return "The notifications to execute sequentially.";
  }

  /**
   * Hook method before attempting to send the message.
   *
   * @param msg		the message to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(String msg) {
    String	result;

    result = super.check(msg);

    if (result == null) {
      for (Enumerated<AbstractNotification> e: enumerate(m_Notifications)) {
        result = e.value.check(msg);
        if (result != null) {
          result = "Notification #" + (e.index + 1) + ": " + result;
          break;
	}
      }
    }

    return result;
  }

  /**
   * Sends the notification.
   *
   * @param msg		the message to send
   * @return		null if successfully sent, otherwise error message
   */
  @Override
  protected String doSendNotification(String msg) {
    String	result;

    result = null;

    for (Enumerated<AbstractNotification> e: enumerate(m_Notifications)) {
      result = e.value.sendNotification(msg);
      if (result != null) {
	result = "Notification #" + (e.index + 1) + ": " + result;
	break;
      }
    }

    return result;
  }
}
