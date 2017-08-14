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
 * ClassCompatibilityChecker.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

/**
 * Interface for compatibility checkers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 15570 $
 */
public interface ClassCompatibilityChecker {

  /**
   * Checks whether the two classes are compatible.
   *
   * @param output	the generated output of the first actor
   * @param input	the accepted input of the second actor
   * @return		true if compatible
   */
  public boolean isCompatible(Class output, Class input);

  /**
   * Checks whether the two class sets are compatible.
   *
   * @param outCls	the classes of the generating actor
   * @param inCls	the classes of the accepting actor
   * @return		true if compatible
   */
  public boolean isCompatible(Class[] outCls, Class[] inCls);
}
