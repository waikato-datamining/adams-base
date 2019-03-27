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
 * WekaInstanceContainer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.instance;

import adams.data.container.AbstractSimpleContainer;
import weka.core.Instance;

/**
 * Encapsulates a {@link weka.core.Instance} object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaInstanceContainer
  extends AbstractSimpleContainer<weka.core.Instance> {

  private static final long serialVersionUID = -6461878118927918040L;

  /**
   * Returns a clone of the content.
   *
   * @return		the clone
   */
  @Override
  protected Instance cloneContent() {
    return (Instance) m_Content.copy();
  }
}
