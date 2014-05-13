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
 * VariablesInspectionHandler.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.Serializable;

/**
 * Interface for classes that can restrict the inspection of certain classes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see VariablesFinder
 */
public interface VariablesInspectionHandler
  extends Serializable {
  
  /**
   * Checks whether the class' options can be inspected.
   *
   * @param cls		the class to check
   * @return		true if it can be inspected, false otherwise
   */
  public boolean canInspectOptions(Class cls);
}
