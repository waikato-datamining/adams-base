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
 * AddData.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import java.util.ArrayList;
import java.util.List;

import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;

/**
 <!-- scriptlet-parameters-start -->
 * Action parameters:<br>
 * <pre>   add-data &lt;comma-separated list of DB-IDss&gt;</pre>
 * <br><br>
 <!-- scriptlet-parameters-end -->
 *
 <!-- scriptlet-description-start -->
 * Description:
 * <pre>   Adds the data containers to the currently loaded ones.</pre>
 * <br><br>
 <!-- scriptlet-description-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AddData
  extends AbstractDataContainerPanelScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = 4553264683463986572L;

  /** the action to execute. */
  public final static String ACTION = "add-data";

  /**
   * Returns the action string used in the command processor.
   *
   * @return		the action string
   */
  @Override
  public String getAction() {
    return ACTION;
  }

  /**
   * Returns a one-line listing of the options of the action.
   *
   * @return		the options or null if none
   */
  @Override
  protected String getOptionsDescription() {
    return "<comma-separated list of DB-IDss>";
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  @Override
  public String getDescription() {
    return "Adds the data containers to the currently loaded ones.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  @Override
  public String process(String options) throws Exception {
    String[]			list;
    String[]			list2;
    int[]			ids;
    int				i;
    List<DataContainer> 	data;
    List<AbstractContainer> 		cont;
    DataContainer		c;
    AbstractContainerManager		manager;

    manager  = getDataContainerPanel().getContainerManager();

    list = OptionUtils.splitOptions(options);

    // obtain IDs
    list2 = list[0].split(",");
    ids   = new int[list2.length];
    for (i = 0; i < list2.length; i++)
      ids[i] = new Integer(list2[i]);

    // undo
    addUndoPoint("Saving undo data...", "Load data: " + Utils.arrayToString(ids));

    // load data
    data = new ArrayList<DataContainer>();
    for (i = 0; i < ids.length; i++) {
      if (ids.length > 1)
	showStatus("Loading the data... " + (i+1) + "/" + ids.length);
      else
	showStatus("Loading the data...");
      c = m_DataProvider.load(ids[i]);
      if (c != null)
	data.add(c);
    }

    cont = new ArrayList<AbstractContainer>();
    for (i = 0; i < data.size(); i++)
      cont.add(manager.newContainer(data.get(i)));
    manager.addAll(cont);
    showStatus("");

    return null;
  }
}
