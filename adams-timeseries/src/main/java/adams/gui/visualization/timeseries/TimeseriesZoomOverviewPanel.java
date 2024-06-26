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
 * TimeseriesZoomOverviewPanel.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.timeseries;

import adams.data.timeseries.Timeseries;
import adams.gui.visualization.container.AbstractDataContainerZoomOverviewPanel;

/**
 * Panel that shows the zoom in the timeseries panel as overlay.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesZoomOverviewPanel
  extends AbstractDataContainerZoomOverviewPanel<TimeseriesPanel, AbstractTimeseriesPaintlet, TimeseriesZoomOverviewPaintlet, Timeseries, TimeseriesContainerManager> {

  /** for serialization. */
  private static final long serialVersionUID = 3177044172306748613L;

  /**
   * Creates a new zoom paintlet.
   * 
   * @return		the paintlet
   */
  @Override
  protected TimeseriesZoomOverviewPaintlet newZoomPaintlet() {
    return new TimeseriesZoomOverviewPaintlet();
  }
}
