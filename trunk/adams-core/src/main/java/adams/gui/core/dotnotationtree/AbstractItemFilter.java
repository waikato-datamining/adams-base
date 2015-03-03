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
 * AbstractItemFilter.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core.dotnotationtree;

/**
 * Ancestor for filtering the items of a DotNotationTree.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see DotNotationTree
 */
public abstract class AbstractItemFilter {

  /** whether the filter is enabled. */
  protected boolean m_Enabled;

  /**
   * Initializes the filter.
   */
  public AbstractItemFilter() {
    super();
    initialize();
  }

  /**
   * Initializes the filter.
   */
  protected void initialize() {
    m_Enabled = true;
  }

  /**
   * Sets whether the filter is enabled.
   *
   * @param value	if true the filter is enabled and checks classes
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
  }

  /**
   * Returns whether the filter is enabled. If a filter is not enabled, all
   * checks will return "true".
   *
   * @return		true if the filter is enabled
   */
  public boolean isEnabled() {
    return m_Enabled;
  }

  /**
   * Performs the actual filtering.
   *
   * @param item	the item to check
   * @return		true if label can be displayed in the tree
   */
  protected abstract boolean doFilter(String item);

  /**
   * Checks an item whether it should be displayed or not.
   *
   * @param item	the item to check
   * @return		true if item can be displayed in the tree
   */
  public boolean filter(String item) {
    if (!m_Enabled)
      return true;
    else
      return doFilter(item);
  }

  /**
   * Returns a short representation of the filter.
   *
   * @return		the representation
   */
  public abstract String toString();
}
