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
 * NoScopeRestriction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.execution.debug;

import adams.flow.core.Actor;
import adams.flow.execution.ExecutionStage;

/**
 <!-- globalinfo-start -->
 * Does not restrict the scope.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NoScopeRestriction
  extends AbstractScopeRestriction {

  private static final long serialVersionUID = 7714227816612692794L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Does not restrict the scope.";
  }

  /**
   * Checks whether the specified actor falls within the scope.
   *
   * @param actor	the actor to check
   * @param stage	the execution stage
   * @return		true if within scope
   */
  @Override
  public boolean checkScope(Actor actor, ExecutionStage stage) {
    return true;
  }
}
