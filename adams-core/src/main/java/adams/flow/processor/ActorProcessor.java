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
 * ActorProcessor.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.processor;

import adams.core.ShallowCopySupporter;
import adams.core.option.OptionHandler;
import adams.flow.core.Actor;

import java.util.List;

/**
 * Interface for actor processors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ActorProcessor
  extends OptionHandler, Comparable<ActorProcessor>, ShallowCopySupporter<ActorProcessor> {

  /**
   * Processes the actor.
   *
   * @param actor	the actor to process
   */
  public void process(Actor actor);

  /**
   * Checks whether any errors were encountered.
   *
   * @return		true if errors were encountered
   * @see		#getErrors()
   */
  public boolean hasErrors();

  /**
   * Returns the list of errors (if any).
   *
   * @return		the errors
   */
  public List<String> getErrors();

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(ActorProcessor o);

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public ActorProcessor shallowCopy();

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public ActorProcessor shallowCopy(boolean expand);
}
