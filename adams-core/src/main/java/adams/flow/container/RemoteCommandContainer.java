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
 * RemoteCommandContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import adams.scripting.command.RemoteCommand;
import adams.scripting.command.basic.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container to store RemoteCommand objects and associated messages as
 * intercepted by logging handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteCommandContainer
  extends AbstractContainer {

  private static final long serialVersionUID = 1960872156580346093L;

  /** the identifier for the the event. */
  public final static String VALUE_EVENT = "Event";

  /** the identifier for the command. */
  public final static String VALUE_COMMAND = "Command";

  /** the identifier for the optional message. */
  public final static String VALUE_MESSAGE = "Message";

  /** the request successful event. */
  public final static String EVENT_REQUESTSUCCESSFUL = "RequestSuccessful";

  /** the request failed event. */
  public final static String EVENT_REQUESTFAILED = "RequestFailed";

  /** the request rejected event. */
  public final static String EVENT_REQUESTREJECTED = "RequestRejected";

  /** the response successful event. */
  public final static String EVENT_RESPONSESUCCESSFUL = "ResponseSuccessful";

  /** the response failed event. */
  public final static String EVENT_RESPONSEFAILED = "ResponseFailed";

  /**
   * Initializes the container with a dummy command.
   * <br><br>
   * Only used for generating help information.
   */
  public RemoteCommandContainer() {
    this(EVENT_REQUESTSUCCESSFUL, new Text());
  }

  /**
   * Initializes the container with the type of event and command but no message.
   *
   * @param event	the type of event
   * @param cmd		the command
   */
  public RemoteCommandContainer(String event, RemoteCommand cmd) {
    this(event, cmd, null);
  }

  /**
   * Initializes the container with the type of event and command but no message.
   *
   * @param event	the type of event
   * @param cmd		the command
   */
  public RemoteCommandContainer(String event, RemoteCommand cmd, String msg) {
    super();

    if ((event == null) || event.isEmpty())
      throw new IllegalArgumentException("Event cannot be null or empty!");
    if (cmd == null)
      throw new IllegalArgumentException("Remote command cannot be null!");

    store(VALUE_EVENT,   event);
    store(VALUE_COMMAND, cmd);
    store(VALUE_MESSAGE, msg);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_EVENT,   "event type; " + String.class.getName());
    addHelp(VALUE_COMMAND, "remote command; " + RemoteCommand.class.getName());
    addHelp(VALUE_MESSAGE, "optional message; " + String.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<>();

    result.add(VALUE_EVENT);
    result.add(VALUE_COMMAND);
    result.add(VALUE_MESSAGE);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_EVENT) && hasValue(VALUE_COMMAND);
  }
}
