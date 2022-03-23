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
 * OutlinePlotter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.outline;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.transformer.locateobjects.LocatedObject;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Interface for classes that plot the outlines of shapes.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface OutlinePlotter
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Plots the outline of the object.
   *
   * @param object	the object to plot
   * @param color	the color to use
   * @param g		the graphics context
   */
  public void plotOutline(LocatedObject object, Color color, Graphics2D g);
}
