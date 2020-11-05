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
 * NullOverlay.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.overlay;

import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.Graphics;

/**
 * Dummy, paints nothing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NullOverlay
  extends AbstractOverlay {

  private static final long serialVersionUID = 7620433880368599467L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, paints nothing.";
  }

  /**
   * Paints the overlay.
   *
   * @param panel 	the owning panel
   * @param g		the graphics context
   */
  @Override
  protected void doPaint(ObjectAnnotationPanel panel, Graphics g) {
  }
}
