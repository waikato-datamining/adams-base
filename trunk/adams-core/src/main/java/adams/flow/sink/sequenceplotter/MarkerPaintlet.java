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
 * MarkerPaintlet.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.gui.visualization.core.Paintlet;

/**
 * Interface for paintlets that paint markers in the SequencePlotter.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MarkerPaintlet
  extends Paintlet {

  /**
   * Returns the plotter panel.
   *
   * @return		the plotter panel
   */
  public SequencePlotterPanel getPlotterPanel();
}
