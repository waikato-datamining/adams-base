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
 * PreprocessHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import java.util.ArrayList;

import weka.core.Instances;
import weka.gui.explorer.Explorer.ExplorerPanel;

/**
 * Manages the {@link PreprocessPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreprocessHandler
  extends AbstractExplorerPanelHandler {

  /** for serialization. */
  private static final long serialVersionUID = 6826999786685140073L;

  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		always true
   */
  @Override
  public boolean handles(ExplorerPanel panel) {
    return (panel instanceof PreprocessPanel);
  }

  /**
   * Generates a view of the explorer panel that can be serialized.
   * 
   * @param panel	the panel to serialize
   * @return		the data to serialize
   */
  @Override
  public Object serialize(ExplorerPanel panel) {
    ArrayList		result;
    PreprocessPanel	pnl;
    
    pnl = (PreprocessPanel) panel;
    result = new ArrayList();
    result.add(pnl.getInstances());
    result.add(serialize(pnl.m_FilterEditor));
    
    return result;
  }

  /**
   * Deserializes the data and configures the panel.
   * 
   * @param panel	the panel to update
   * @param data	the serialized data to restore the panel with
   */
  @Override
  public void deserialize(ExplorerPanel panel, Object data) {
    ArrayList		list;
    PreprocessPanel	pnl;
    
    pnl  = (PreprocessPanel) panel;
    list = (ArrayList) data;
    
    if (list.get(0) != null)
      pnl.setInstances((Instances) list.get(0));
    deserialize(list.get(1), pnl.m_FilterEditor);
  }
}
