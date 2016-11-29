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
 * Orientation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.jfreechartplot.chart;

import org.jfree.chart.plot.PlotOrientation;

/**
 * Enumeration of the possible orientations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum Orientation {

  HORIZONTAL(PlotOrientation.HORIZONTAL),

  VERTICAL(PlotOrientation.VERTICAL);

  /** the associated orientation. */
  private PlotOrientation m_Orientation;

  /**
   * Constructor.
   *
   * @param orientation	the associated orientation
   */
  private Orientation(PlotOrientation orientation) {
    m_Orientation = orientation;
  }

  /**
   * Returns the associated orientation.
   *
   * @return		the orientation
   */
  public PlotOrientation getOrientation() {
    return m_Orientation;
  }
}
