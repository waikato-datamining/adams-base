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
 * TimedActor.java
 * Copyright (C) 2015-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

/**
 * Interface for actors that time their execution and send the timing data
 * to a callable actor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface TimedActor
  extends Actor, CallableActorUser, OptionalCallableActor {

  /**
   * Sets whether to perform timing on its execution.
   *
   * @param value 	true if timing enabled
   */
  public void setTimingEnabled(boolean value);

  /**
   * Returns whether to perform timing on its execution.
   *
   * @return 		true if timing enabled
   */
  public boolean getTimingEnabled();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timingEnabledTipText();

  /**
   * Sets the prefix to store in the timing container.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value);

  /**
   * Returns the prefix to store in the timing container.
   *
   * @return 		the prefix
   */
  public String getPrefix();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText();

  /**
   * Sets the name of the callable actor to use.
   *
   * @param value 	the callable name
   */
  public void setCallableName(CallableActorReference value);

  /**
   * Returns the name of the callable actor in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getCallableName();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String callableNameTipText();

  /**
   * Checks whether a reference to the callable actor is currently available.
   *
   * @return		true if a reference is available
   * @see		#getCallableActor()
   */
  public boolean hasCallableActor();

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public Actor getCallableActor();
}
