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
 * AbstractOptionTraverser.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;


/**
 * Ancestor for option traversers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOptionTraverser
  extends AbstractOptionHandler
  implements OptionTraverser {

  /** for serialization. */
  private static final long serialVersionUID = -5833903231639878367L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "TODO";
  }

  /**
   * Handles the encountered boolean option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  @Override
  public abstract void handleBooleanOption(BooleanOption option, OptionTraversalPath path);

  /**
   * Handles the encountered class option. Precedence over argument option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  @Override
  public abstract void handleClassOption(ClassOption option, OptionTraversalPath path);

  /**
   * Handles the encountered argument option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  @Override
  public abstract void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path);

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
  @Override
  public abstract boolean canHandle(AbstractOption option);

  /**
   * Returns whether the traverser can recurse the specified class
   * (base class from a ClassOption).
   *
   * @param cls		the class to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  @Override
  public abstract boolean canRecurse(Class cls);

  /**
   * Returns whether the traverser can recurse the specified object.
   *
   * @param obj		the Object to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  @Override
  public abstract boolean canRecurse(Object obj);
}
