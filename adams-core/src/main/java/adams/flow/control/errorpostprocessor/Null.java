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
 * Null.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.errorpostprocessor;

import adams.flow.core.Actor;
import adams.flow.core.ErrorHandler;

/**
 <!-- globalinfo-start -->
 * Dummy post-processor, does nothing.
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
public class Null
  extends AbstractErrorPostProcessor {

  private static final long serialVersionUID = -5136181833130378152L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy post-processor, does nothing.";
  }

  /**
   * Does nothing.
   *
   * @param handler	the error handler that this call comes from
   * @param source	the source actor where the error originated
   * @param type	the type of error
   * @param msg		the error message
   */
  @Override
  protected void doPostProcessError(ErrorHandler handler, Actor source, String type, String msg) {
  }
}
