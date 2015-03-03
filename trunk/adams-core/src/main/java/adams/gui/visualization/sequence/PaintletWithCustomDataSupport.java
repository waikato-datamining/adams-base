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
 * PaintletWithCustomDataSupport.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

import java.awt.Color;
import java.awt.Graphics;

import adams.data.sequence.XYSequence;
import adams.gui.event.PaintEvent.PaintMoment;

/**
 * Interface for paintlets that support drawing of custom data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PaintletWithCustomDataSupport
  extends XYSequencePaintlet {

  /**
   * Draws the custom data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   */
  public void drawCustomData(Graphics g, PaintMoment moment, XYSequence data, Color color);
}
