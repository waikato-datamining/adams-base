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
 * RemoveData.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.core.Range;
import adams.gui.visualization.container.AbstractContainerManager;

/**
 <!-- scriptlet-parameters-start -->
 * Action parameters:<br/>
 * <pre>   remove-data &lt;comma-separated list of 1-based indices&gt;</pre>
 * <p/>
 <!-- scriptlet-parameters-end -->
 *
 <!-- scriptlet-description-start -->
 * Description:
 * <pre>   Removes the data containers with the specified indices.
 *    NB: index is based on the order the data containers have beeen loaded into
 *    the system, includes all data containers, not just visible ones.</pre>
 * <p/>
 <!-- scriptlet-description-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveData
  extends AbstractDataContainerPanelScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -4530704396431542201L;

  /** the action to execute. */
  public final static String ACTION = "remove-data";

  /**
   * Returns the action string used in the command processor.
   *
   * @return		the action string
   */
  public String getAction() {
    return ACTION;
  }

  /**
   * Returns a one-line listing of the options of the action.
   *
   * @return		the options or null if none
   */
  protected String getOptionsDescription() {
    return "<comma-separated list of 1-based indices (or ranges of indices)>";
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  public String getDescription() {
    return
        "Removes the data containers with the specified indices.\n"
      + "NB: index is based on the order the data containers have beeen loaded "
      + "into the system, includes all data containers, not just visible ones.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  public String process(String options) throws Exception {
    int[]		indices;
    int			i;
    AbstractContainerManager	manager;
    Range		range;

    addUndoPoint("Saving undo data...", "Remove container(s): " + options);

    manager = getDataContainerPanel().getContainerManager();
    range   = new Range(options);
    range.setMax(manager.count());
    indices = range.getIntIndices();
    manager.startUpdate();
    for (i = indices.length - 1; i >= 0; i--)
      manager.remove(indices[i]);
    manager.finishUpdate();

    return null;
  }
}
