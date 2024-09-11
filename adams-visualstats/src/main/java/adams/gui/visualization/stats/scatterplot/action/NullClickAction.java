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
 * NullClickAction.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.stats.scatterplot.action;

import adams.gui.visualization.stats.scatterplot.ScatterPlot;

import java.awt.event.MouseEvent;

/**
 * Dummy action, does nothing.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class NullClickAction
  extends AbstractMouseClickAction {

  /** for serialization. */
  private static final long serialVersionUID = -5891356167241337630L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy action, does nothing";
  }

  /**
   * Gets triggered if the user clicks on the canvas.
   *
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  @Override
  public void mouseClickOccurred(ScatterPlot panel, MouseEvent e) {
  }
}
