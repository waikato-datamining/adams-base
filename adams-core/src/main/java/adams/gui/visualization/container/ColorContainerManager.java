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
 * ColorContainerManager.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import adams.gui.visualization.core.ColorProvider;

import java.awt.Color;

/**
 * Indicator interface for container managers that manage containers
 * encapsulating color as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ColorContainerManager<T extends AbstractContainer> {

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public void setColorProvider(ColorProvider value);

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider in use
   */
  public ColorProvider getColorProvider();

  /**
   * Returns the color for the container.
   *
   * @param cont	the container to get the color for
   * @return		the color
   */
  public Color getColor(T cont);
}