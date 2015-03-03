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
 * AbstractVisibilityScriplet.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.core.Range;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.VisibilityContainer;

/**
 * Ancestor for visibility-related scriptlets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractVisibilityScriplet
  extends AbstractDataContainerPanelScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = 8803594650276164515L;

  /**
   * Returns a one-line listing of the options of the action.
   *
   * @return		the options or null if none
   */
  protected String getOptionsDescription() {
    return "<comma-separated list of 1-based indices (or ranges of indices)>";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @param visible	whether to make them visible or not
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  protected String process(String options, boolean visible) throws Exception {
    int			i;
    int[]		indices;
    AbstractContainer		cont;
    AbstractContainerManager	manager;
    Range		range;

    manager = getDataContainerPanel().getContainerManager();
    range   = new Range(options);
    range.setMax(manager.count());
    indices = range.getIntIndices();
    if (indices.length > 1) {
      manager.startUpdate();
      for (i = 0; i < indices.length; i++) {
	cont  = manager.get(indices[i]);
	if (cont instanceof VisibilityContainer)
	  ((VisibilityContainer) cont).setVisible(visible);
      }
      manager.finishUpdate();
    }
    else if (indices.length == 1) {
      cont = manager.get(indices[0]);
      if (cont instanceof VisibilityContainer)
	((VisibilityContainer) cont).setVisible(visible);
    }

    return null;
  }
}
