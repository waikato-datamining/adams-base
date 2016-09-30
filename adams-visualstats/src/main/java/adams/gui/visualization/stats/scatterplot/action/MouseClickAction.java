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
 * MouseClickAction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.stats.scatterplot.action;

import adams.gui.visualization.stats.scatterplot.ScatterPlot;

import java.awt.event.MouseEvent;
import java.io.Serializable;

/**
 * Interface for classes that react to mouse click actions on the plot
 * canvas.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MouseClickAction
  extends Serializable {

  /**
   * Gets triggered if the user clicks on the canvas.
   * 
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  public abstract void mouseClickOccurred(ScatterPlot panel, MouseEvent e);
}
