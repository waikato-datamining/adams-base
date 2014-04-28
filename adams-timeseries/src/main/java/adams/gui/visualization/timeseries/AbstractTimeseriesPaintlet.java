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
 * AbstractTimeseriesPaintlet.java
 * Copyright (C) 2011-201 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import adams.gui.visualization.container.AbstractDataContainerPaintlet;

/**
 * A specialized paintlet for timeseries panels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTimeseriesPaintlet
  extends AbstractDataContainerPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 6076626028535605027L;

  /**
   * Returns the timeseries panel currently in use.
   *
   * @return		the panel in use
   */
  public TimeseriesPanel getTimeseriesPanel() {
    return (TimeseriesPanel) m_Panel;
  }
}
