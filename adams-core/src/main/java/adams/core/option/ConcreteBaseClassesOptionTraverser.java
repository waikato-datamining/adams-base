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
 * ConcreteBaseClassesOptionTraverser.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import nz.ac.waikato.cms.locator.ClassLocator;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Determines all the base classes, must be not abstract and actual classes, not interfaces.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ConcreteBaseClassesOptionTraverser
  extends AbstractOptionTraverserWithResult<Set<Class>> {

  private static final long serialVersionUID = -4917443862975286313L;

  /** for collecting the classes. */
  protected Set<Class> m_Classes;

  /**
   * Handles the encountered boolean option.
   *
   * @param option the option to handle
   * @param path   the property path so far
   */
  @Override
  public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
    // ignored
  }

  /**
   * Handles the encountered class option. Precedence over argument option.
   *
   * @param option the option to handle
   * @param path   the property path so far
   */
  @Override
  public void handleClassOption(ClassOption option, OptionTraversalPath path) {
    if (!option.getBaseClass().isInterface() && !Modifier.isAbstract(option.getBaseClass().getModifiers()))
      m_Classes.add(option.getBaseClass());
  }

  /**
   * Handles the encountered argument option.
   *
   * @param option the option to handle
   * @param path   the property path so far
   */
  @Override
  public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
    // ignored
  }

  /**
   * Returns whether the traverser is allowed to "handle" this option.
   *
   * @param option the option to check whether it can be handled
   * @return true if handling via
   * {@link #handleArgumentOption(AbstractArgumentOption, OptionTraversalPath)},
   * {@link #handleClassOption(ClassOption, OptionTraversalPath)} or
   * {@link #handleBooleanOption(BooleanOption, OptionTraversalPath)}
   * is allowed
   */
  @Override
  public boolean canHandle(AbstractOption option) {
    return (option instanceof ClassOption);
  }

  /**
   * Returns whether the traverser can recurse the specified class
   * (base class from a ClassOption).
   *
   * @param cls the class to determine for whether recursing is
   *            possible or not
   * @return true if to traverse the options recursively
   */
  @Override
  public boolean canRecurse(Class cls) {
    return ClassLocator.hasInterface(OptionHandler.class, cls);
  }

  /**
   * Returns whether the traverser can recurse the specified object.
   *
   * @param obj the Object to determine for whether recursing is
   *            possible or not
   * @return true if to traverse the options recursively
   */
  @Override
  public boolean canRecurse(Object obj) {
    return (obj instanceof OptionHandler);
  }

  /**
   * Resets the result before traversing.
   */
  @Override
  public void resetResult() {
    m_Classes = new HashSet<>();
  }

  /**
   * Returns the result of the traversal.
   *
   * @return the result
   */
  @Override
  public Set<Class> getResult() {
    return m_Classes;
  }
}
