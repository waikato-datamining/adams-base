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
 * InteractiveProcessor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

/**
 * Interface for processors that interact with the user.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface InteractiveProcessor {

  /**
   * Checks whether interaction can happen with the given object.
   * 
   * @param obj		the current object that the traversal came across
   * @return		true if interaction should occur for this object
   */
  public boolean canInteract(Object obj);
  
  /**
   * Does the actual interaction with the user on the given object.
   * 
   * @param obj		the object to use for interaction
   * @return		true if successful interaction
   */
  public boolean doInteract(Object obj);
}
