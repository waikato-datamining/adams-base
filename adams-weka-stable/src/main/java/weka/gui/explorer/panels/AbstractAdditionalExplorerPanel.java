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
 * AbstractAdditionalPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer.panels;

import weka.gui.explorer.AbstractExplorerPanelHandler;
import weka.gui.explorer.Explorer.ExplorerPanel;
import adams.core.ClassLister;

/**
 * Wrapper class for additional panels to be displayed in the Explorer.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAdditionalExplorerPanel
  implements AdditionalExplorerPanel {

  /**
   * Returns the panel to display.
   * 
   * @return		the panel
   */
  public abstract ExplorerPanel getExplorerPanel();
  
  /**
   * Returns the associated panel handler.
   * 
   * @return		the handler
   */
  public abstract AbstractExplorerPanelHandler getExplorerPanelHandler();

  /**
   * Returns a list with classnames of panels.
   *
   * @return		the panel classnames
   */
  public static String[] getPanels() {
    return ClassLister.getSingleton().getClassnames(AdditionalExplorerPanel.class);
  }
}
