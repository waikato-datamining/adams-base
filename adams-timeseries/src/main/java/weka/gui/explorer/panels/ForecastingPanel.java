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
 * ForecastingPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer.panels;

import weka.classifiers.timeseries.gui.explorer.ExplorerTSPanelPublic;
import weka.gui.explorer.AbstractExplorerPanelHandler;
import weka.gui.explorer.Explorer.ExplorerPanel;
import weka.gui.explorer.ForecastingHandler;

/**
 * Panel for forecasting/timeseries.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ForecastingPanel
  extends AbstractAdditionalExplorerPanel {

  /**
   * Returns the panel to display.
   * 
   * @return		the panel
   */
  @Override
  public ExplorerPanel getExplorerPanel() {
    return new ExplorerTSPanelPublic();
  }

  /**
   * Returns the associated panel handler.
   * 
   * @return		the handler
   */
  @Override
  public AbstractExplorerPanelHandler getExplorerPanelHandler() {
    return new ForecastingHandler();
  }
}
