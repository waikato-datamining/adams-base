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
 * ForecastingHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import weka.classifiers.timeseries.gui.ExplorerTSPanelHandler;
import weka.classifiers.timeseries.gui.explorer.ExplorerTSPanel;
import weka.gui.explorer.Explorer.ExplorerPanel;

/**
 * Manages the {@link ExplorerTSPanel} (wrapper handler).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see weka.classifiers.timeseries.gui.ForecastingHandler
 */
public class ForecastingHandler
  extends AbstractExplorerPanelHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3524073619785515675L;

  /** the actual handler. */
  protected AbstractExplorerPanelHandler m_Handler;
  
  /**
   * Initializes the handler.
   */
  public ForecastingHandler() {
    m_Handler = new ExplorerTSPanelHandler();
  }
  
  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		always true
   */
  @Override
  public boolean handles(ExplorerPanel panel) {
    return m_Handler.handles(panel);
  }

  /**
   * Generates a view of the explorer panel that can be serialized.
   * 
   * @param panel	the panel to serialize
   * @return		the data to serialize
   */
  @Override
  public Object serialize(ExplorerPanel panel) {
    return m_Handler.serialize(panel);
  }

  /**
   * Deserializes the data and configures the panel.
   * 
   * @param panel	the panel to update
   * @param data	the serialized data to restore the panel with
   */
  @Override
  public void deserialize(ExplorerPanel panel, Object data) {
    m_Handler.deserialize(panel, data);
  }
}
