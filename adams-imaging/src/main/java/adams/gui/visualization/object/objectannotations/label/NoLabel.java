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
 * NoLabel.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.label;

import adams.flow.transformer.locateobjects.LocatedObject;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Does not plot a label.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class NoLabel
  extends AbstractLabelPlotter {

  private static final long serialVersionUID = -2032898070308967178L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Does not plot a label.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  protected String generateQuickInfo() {
    return null;
  }

  /**
   * Plots the label.
   *
   * @param object the object to plot
   * @param color  the color to use
   * @param g      the graphics context
   */
  @Override
  protected void doPlotLabel(LocatedObject object, Color color, Graphics2D g) {
  }
}
