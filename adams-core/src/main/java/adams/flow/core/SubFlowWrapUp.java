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
 * SubFlowWrapUp.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

/**
 * Interface for actors that can wrap-up (i.e., save memory) their sub-actors
 * and, if need be, re-initialize again.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see	Actor#wrapUp()
 */
public interface SubFlowWrapUp 
  extends Actor {

  /**
   * Wraps up the sub-actors, freeing up memory.
   */
  public void wrapUpSubFlow();
  
  /**
   * Checks whether the sub-flow has been wrapped up.
   * 
   * @return		true if sub-flow was wrapped up
   */
  public boolean isSubFlowWrappedUp();
}
