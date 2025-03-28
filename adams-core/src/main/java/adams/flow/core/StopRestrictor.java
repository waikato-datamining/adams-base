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
 * StopRestrictor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

/**
 * Interface for actors that can limit the scope of the
 * {@link adams.flow.control.Stop} control actor or other actors
 * that can stop the execution.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface StopRestrictor
  extends Actor {

  /**
   * Returns whether stops are being restricted.
   *
   * @return		true if restricting stops
   */
  public boolean isRestrictingStops();

  /**
   * Stops the (restricted) execution. No message set.
   */
  public void restrictedStopExecution();

  /**
   * Stops the (restricted) execution.
   *
   * @param msg		the message to set as reason for stopping, can be null
   */
  public void restrictedStopExecution(String msg);

  /**
   * Returns whether the stop was a restricted one (that can be resumed).
   *
   * @return		true if restricted stop occurred
   */
  public boolean isRestrictedStop();
}
