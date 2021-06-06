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
 * PaintletWithContainerIDMatching.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import adams.core.base.BaseRegExp;

/**
 * Interface for paintlets that only work with containers that match the
 * provided regular expression.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface PaintletWithContainerIDMatching
  extends Paintlet {

  /**
   * Sets the regular expression the container IDs must match.
   *
   * @param value	the expression
   */
  public void setContainerIDRegExp(BaseRegExp value);

  /**
   * Returns the regular expression the container IDs must match.
   *
   * @return		the expression
   */
  public BaseRegExp getContainerIDRegExp();
}
