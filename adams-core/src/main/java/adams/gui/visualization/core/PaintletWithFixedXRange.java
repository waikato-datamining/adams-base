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
 * XYSequencePaintletWithFixedXRange.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

/**
 * Interface for paintlets that use a fixed X range.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PaintletWithFixedXRange
  extends Paintlet {

  /**
   * Returns the minimum to use for the X range.
   *
   * @return		the minimum
   */
  public double getMinimumX();

  /**
   * Returns the maximum to use for the X range.
   *
   * @return		the maximum
   */
  public double getMaximumX();
}
