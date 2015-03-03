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
 * AbstractOptionTraversal.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Interface for code that is being executed while traversing options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface OptionTraverser {

  /**
   * Handles the encountered boolean option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  public void handleBooleanOption(BooleanOption option, OptionTraversalPath path);

  /**
   * Handles the encountered class option. Precedence over argument option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  public void handleClassOption(ClassOption option, OptionTraversalPath path);

  /**
   * Handles the encountered argument option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path);

  /**
   * Returns whether the traverser is allowed to "handle" this option.
   *
   * @param option	the option to check whether it can be handled
   * @return		true if handling via 
   * 			{@link #handleArgumentOption(AbstractArgumentOption, OptionTraversalPath)},
   * 			{@link #handleClassOption(ClassOption, OptionTraversalPath)} or
   * 			{@link #handleBooleanOption(BooleanOption, OptionTraversalPath)} 
   * 			is allowed
   */
  public boolean canHandle(AbstractOption option);

  /**
   * Returns whether the traverser can recurse the specified class
   * (base class from a ClassOption).
   *
   * @param cls		the class to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  public boolean canRecurse(Class cls);

  /**
   * Returns whether the traverser can recurse the specified object.
   *
   * @param obj		the Object to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  public boolean canRecurse(Object obj);
}
