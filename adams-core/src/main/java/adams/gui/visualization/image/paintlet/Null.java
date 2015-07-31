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
 * Null.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.paintlet;

import java.awt.Graphics;

/**
 * Dummy paintlet.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Null
  extends AbstractPaintlet {

  private static final long serialVersionUID = 4251094891194904815L;

  @Override
  public String globalInfo() {
    return "Dummy paintlet, does nothing.";
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   */
  @Override
  public void performPaint(Graphics g) {
  }
}
