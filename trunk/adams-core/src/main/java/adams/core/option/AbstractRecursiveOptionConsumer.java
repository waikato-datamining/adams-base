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
 * AbstractRecursiveOptionConsumer.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.util.Stack;

/**
 * Generates output from visiting the options recursively.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <C> the type of data to consume
 * @param <V> the type of data used for values
 */
public abstract class AbstractRecursiveOptionConsumer<C,V>
  extends AbstractOptionConsumer<C,V>
  implements RecursiveOptionConsumer {

  /** for serialization. */
  private static final long serialVersionUID = 4502704821224667069L;

  /** keeping track of nesting. */
  protected Stack m_Nesting;

  /**
   * Used for initializing members.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Nesting = new Stack();
  }

  /**
   * Generates a debug string, e.g., based on the method name.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  @Override
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
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_Nesting = null;
  }
}
