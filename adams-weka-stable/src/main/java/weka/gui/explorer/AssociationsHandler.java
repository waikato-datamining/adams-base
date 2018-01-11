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
 * AssociationsHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import java.util.ArrayList;

import weka.gui.explorer.Explorer.ExplorerPanel;

/**
 * Manages the {@link AssociationsPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AssociationsHandler
  extends AbstractExplorerPanelHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2284676357783882049L;

  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		always true
   */
  @Override
  public boolean handles(ExplorerPanel panel) {
    return (panel instanceof AssociationsPanel);
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
    AssociationsPanel	pnl;
    
    pnl    = (AssociationsPanel) panel;
    result = new ArrayList();
    result.add(serialize(pnl.m_AssociatorEditor));
    result.add(serialize(pnl.m_History));
    
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
    AssociationsPanel	pnl;
    
    pnl  = (AssociationsPanel) panel;
    list = (ArrayList) data;

    deserialize(list.get(0), pnl.m_AssociatorEditor);
    deserialize(list.get(1), pnl.m_History);
  }
}
