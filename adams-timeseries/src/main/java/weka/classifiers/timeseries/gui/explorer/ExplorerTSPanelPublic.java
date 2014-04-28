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
 * ExplorerTSPanelPublic.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.timeseries.gui.explorer;

import weka.classifiers.timeseries.gui.ForecastingPanel;

/**
 * Subclassed {@link ExplorerTSPanel} to get access to {@link ForecastingPanel}
 * panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExplorerTSPanelPublic
  extends ExplorerTSPanel {

  /** for serialization. */
  private static final long serialVersionUID = -1425230940488370152L;

  /**
   * Makes the forecasting panel available.
   * 
   * @return		the panel
   */
  public ForecastingPanel getForecastingPanel() {
    return m_forecastingPanel;
  }
}
