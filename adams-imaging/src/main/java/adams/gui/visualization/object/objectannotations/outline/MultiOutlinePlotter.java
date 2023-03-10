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
 * MultiOutlinePlotter.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.outline;

import adams.flow.transformer.locateobjects.LocatedObject;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Applies the specified plotters subsequently.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiOutlinePlotter
  extends AbstractOutlinePlotter {

  private static final long serialVersionUID = 7476304350100694571L;

  /** the plotters to use. */
  protected OutlinePlotter[] m_Plotters;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified plotters subsequently.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "plotter", "plotters",
      new OutlinePlotter[0]);
  }

  /**
   * Sets the plotters to use.
   *
   * @param value 	the plotters
   */
  public void setPlotters(OutlinePlotter[] value) {
    m_Plotters = value;
    reset();
  }

  /**
   * Returns the plotters in use.
   *
   * @return 		the plotters
   */
  public OutlinePlotter[] getPlotters() {
    return m_Plotters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plottersTipText() {
    return "The outline plotters to apply subsequently.";
  }

  /**
   * Plots the outline.
   *
   * @param object the object to plot
   * @param color  the color to use
   * @param g      the graphics context
   */
  @Override
  protected void doPlotOutline(LocatedObject object, Color color, Graphics2D g) {
    for (OutlinePlotter plotter: m_Plotters)
      plotter.plotOutline(object, color, g);
  }
}
