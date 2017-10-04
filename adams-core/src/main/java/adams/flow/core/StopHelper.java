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
 * StopHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

/**
 * Helper class for stopping.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StopHelper {

  /**
   * Returns the enclosing {@link StopRestrictor}.
   *
   * @param context	the flow context
   * @return		the restrictor
   */
  protected static Actor getStopRestrictor(Actor context) {
    Actor	result;
    Actor	parent;

    result = null;
    parent = context.getParent();
    do {
      if (parent instanceof StopRestrictor)
	result = parent;
      else if (parent != null)
        parent = parent.getParent();
    }
    while ((parent != null) && (result == null));

    if (result == null)
      result = context.getRoot();

    return result;
  }

  /**
   * Returns the root actor.
   *
   * @param context	the flow context
   * @return		the restrictor
   */
  protected static Actor getRoot(Actor context) {
    return context.getRoot();
  }

  /**
   * Determines the actor to call the stopExecution method on.
   *
   * @param context	the flow context
   * @param mode	the stop mode to use
   * @return		the actor
   */
  public static Actor getStopActor(Actor context, StopMode mode) {
    switch (mode) {
      case GLOBAL:
	return getRoot(context);
      case STOP_RESTRICTOR:
	return getStopRestrictor(context);
      default:
	throw new IllegalStateException("Unhandled stop mode: " + mode);
    }
  }

  /**
   * Stops the flow using the specified stop mode, with no message.
   *
   * @param context	the flow context
   * @param mode	the stop mode to use
   * @return		null if successful, otherwise error message
   */
  public static String stop(Actor context, StopMode mode) {
    return stop(context, mode, null);
  }

  /**
   * Stops the flow using the specified stop mode, with no message.
   *
   * @param context	the flow context
   * @param mode	the stop mode to use
   * @param message 	the stop message, can be null
   * @return		null if successful, otherwise error message
   */
  public static String stop(Actor context, StopMode mode, String message) {
    String	result;
    Actor	actor;

    result = null;

    actor = StopHelper.getStopActor(context, mode);
    if (actor != null) {
      if (message.length() == 0)
	actor.stopExecution();
      else
	actor.stopExecution(context.getVariables().expand(message));
    }
    else {
      result = "Failed to determine actor for stopping!";
    }

    return result;
  }
}
