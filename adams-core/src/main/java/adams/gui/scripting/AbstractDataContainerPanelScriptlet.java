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
 * AbstractDataContainerPanelScriptlet.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.gui.visualization.container.DataContainerPanel;


/**
 * Ancestor for scriptlets that require a DataContainerPanel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataContainerPanelScriptlet
  extends AbstractUndoScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = 1693024372320455031L;

  /**
   * Returns the class(es) of an object that must be present for this action
   * to be executed.
   *
   * @return		the class(es) of which an instance must be present for
   * 			execution, null if none necessary
   */
  public Class[] getRequirements() {
    return new Class[]{DataContainerPanel.class};
  }

  /**
   * Returns the spectrum panel, if available.
   *
   * @return		the panel
   */
  public DataContainerPanel getDataContainerPanel() {
    if (hasOwner())
      return ((CommandProcessor) getOwner()).getDataContainerPanel();
    else
      return null;
  }
}
