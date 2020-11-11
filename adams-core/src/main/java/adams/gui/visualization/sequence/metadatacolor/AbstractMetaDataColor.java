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
 * AbstractMetaDataColor.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.sequence.metadatacolor;

import adams.core.option.AbstractOptionHandler;
import adams.data.sequence.XYSequencePoint;

import java.awt.Color;
import java.util.List;

/**
 * Ancestor for schemes extracting the color from a sequence point's meta-data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMetaDataColor<T extends XYSequencePoint>
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -3184029850003382239L;

  /**
   * Initializes the meta-data color scheme.
   *
   * @param points	the points to initialize with
   */
  public abstract void initialize(List<T> points);

  /**
   * Extracts the color from the meta-data.
   *
   * @param point	the point to get the color from
   * @param defColor 	the default color to use
   * @return		the color
   */
  public abstract Color getColor(T point, Color defColor);
}
