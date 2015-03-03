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
 * AtomicExecution.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.flow.core.Actor;

/**
 * Interface for actors that can be configured to finish execution first 
 * before attempting to be stopped. Useful for encapsulating critical steps.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface AtomicExecution 
  extends Actor {

  /**
   * Sets whether to finish processing before stopping execution.
   * 
   * @param value	if true then actor finishes processing first 
   */
  public void setFinishBeforeStopping(boolean value);
  
  /**
   * Returns whether to finish processing before stopping execution.
   * 
   * @return		true if actor finishes processing first
   */
  public boolean getFinishBeforeStopping();
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finishBeforeStoppingTipText();
}
