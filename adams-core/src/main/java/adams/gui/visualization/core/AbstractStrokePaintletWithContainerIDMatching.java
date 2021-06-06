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
 * AbstractStrokePaintletWithContainerNameMatching.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core;

import adams.core.base.BaseRegExp;

/**
 * Ancestor for paintlets that restrict painting to containers which ID
 * matches a regular expression.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractStrokePaintletWithContainerIDMatching
  extends AbstractStrokePaintlet
  implements PaintletWithContainerIDMatching {

  /** the regular expression the container IDs must match. */
  protected BaseRegExp m_ContainerIDRegExp;

  private static final long serialVersionUID = 1440663106892862065L;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "container-id-regexp", "containerIDRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Sets the regular expression the container IDs must match.
   *
   * @param value	the expression
   */
  public void setContainerIDRegExp(BaseRegExp value) {
    m_ContainerIDRegExp = value;
    memberChanged();
  }

  /**
   * Returns the regular expression the container IDs must match.
   *
   * @return		the expression
   */
  public BaseRegExp getContainerIDRegExp() {
    return m_ContainerIDRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String containerIDRegExpTipText() {
    return "The expression that the container IDs must match in order to get painted.";
  }

  /**
   * Returns whether the given container ID is a match.
   *
   * @param id		the ID to check
   * @return		true if a match, ie can be painted
   */
  protected boolean isContainerIDMatch(String id) {
    return m_ContainerIDRegExp.isMatchAll()
      || m_ContainerIDRegExp.isMatch(id);
  }
}
