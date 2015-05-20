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
 * AbstractRecursiveOptionProducer.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.util.Stack;

/**
 * Generates output from visiting the options recursively.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <O> the type of output data that gets generated
 * @param <I> the internal type used while nesting
 */
public abstract class AbstractRecursiveOptionProducer<O,I>
  extends AbstractOptionProducer<O,I>
  implements RecursiveOptionProducer {

  /** for serialization. */
  private static final long serialVersionUID = 4502704821224667069L;

  /** keeping track of nesting. */
  protected Stack<I> m_Nesting;

  /**
   * Used for initializing members.
   */
  protected void reset() {
    super.reset();

    m_Nesting = new Stack<I>();
  }

  /**
   * Generates a debug string, e.g., based on the method name.
   * <br><br>
   * Default implementation merely returns the string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String generateLoggingString(String s) {
    return getDebugIndentation() + s + "(" + getRecursionLevel() + ")";
  }

  /**
   * Returns the current nesting level.
   *
   * @return		the current level
   */
  public int getRecursionLevel() {
    return m_Nesting.size();
  }

  /**
   * Returns the indentation string based on the current nesting level.
   *
   * @return		the indentation
   * @see		#getRecursionLevel()
   */
  protected String getDebugIndentation() {
    return getDebugIndentation(getRecursionLevel());
  }

  /**
   * Returns the indentation string based on the nesting level.
   *
   * @param level	the nesting level
   * @return		the indentation
   */
  protected String getDebugIndentation(int level) {
    StringBuilder	result;
    int			i;

    result = new StringBuilder(level * 2);
    for (i = 0; i < level; i++)
      result.append("  ");

    return result.toString();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();

    m_Nesting = null;
  }
}
