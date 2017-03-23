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
 * ColorProvider.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

import adams.core.ShallowCopySupporter;
import adams.core.option.OptionHandler;

import java.awt.Color;

/**
 * Interface for color providing classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ColorProvider
  extends OptionHandler, ShallowCopySupporter<ColorProvider> {

  /**
   * Returns the next color.
   *
   * @return		the next color
   */
  public Color next();

  /**
   * Resets the colors.
   */
  public void resetColors();

  /**
   * "Recycles" the specified colors, i.e., makes it available for future use.
   *
   * @param c		the color to re-use
   */
  public void recycle(Color c);

  /**
   * "Excludes" the specified colors, i.e., makes it unavailable for future use.
   *
   * @param c		the color to exclude
   */
  public void exclude(Color c);
}
