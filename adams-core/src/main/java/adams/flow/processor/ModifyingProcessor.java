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
 * ModifyingProcessor.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.flow.core.AbstractActor;

/**
 * Interface for processors that potentially modify the actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ModifyingProcessor {
  
  /**
   * Sets whether to suppress copying the actor first before processing.
   * 
   * @param value	if true, no copy of the actor is generated
   */
  public void setNoCopy(boolean value);
  
  /**
   * Returns whether the copying of the actor is suppressed.
   * 
   * @return		true if no copy generated
   */
  public boolean getNoCopy();

  /**
   * Returns whether the actor was modified.
   *
   * @return		true if the actor was modified
   */
  public boolean isModified();

  /**
   * Returns the modified actor.
   *
   * @return		the modified actor, null if not modified
   */
  public AbstractActor getModifiedActor();
}
