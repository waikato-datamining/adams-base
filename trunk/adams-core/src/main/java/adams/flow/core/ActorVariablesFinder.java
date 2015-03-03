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
 * ActorVariablesFinder.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.VariablesFinder;
import adams.core.option.AbstractOption;

/**
 * Locates variables in actors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorVariablesFinder
  extends VariablesFinder {
  
  /** for serialization. */
  private static final long serialVersionUID = -3100646194459471635L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Option traverser for locating variables in actors.";
  }
  
  /**
   * Checks whether to skip this option.
   */
  @Override
  protected boolean isSkipped(AbstractOption option) {
    boolean result = false;
    if (option.getOptionHandler() instanceof AbstractActor) {
      // skip property is true and no variable attached to it
      result =    ((AbstractActor) option.getOptionHandler()).getSkip()
	  && (option.getOwner().getVariableForProperty("skip") == null);
    }
    return result;
  }
}
