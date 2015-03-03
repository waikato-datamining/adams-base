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
 * SetData.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.NamedContainer;

/**
 <!-- scriptlet-parameters-start -->
 * Action parameters:<br/>
 * <pre>   set-data &lt;index&gt; &lt;DB-ID&gt;</pre>
 * <p/>
 <!-- scriptlet-parameters-end -->
 *
 <!-- scriptlet-description-start -->
 * Description:
 * <pre>   Replaces the currently loaded data container at the specified 1-based index
 *    with the one associated with the database ID.</pre>
 * <p/>
 <!-- scriptlet-description-end -->
 *
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetData
  extends AbstractDataContainerPanelScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -5936800338572570006L;

  /** the action to execute. */
  public final static String ACTION = "set-data";

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
    return "<index> <DB-ID>";
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  public String getDescription() {
    return
        "Replaces the currently loaded data container at the specified 1-based "
      + "index with the one associated with the database ID.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  public String process(String options) throws Exception {
    String[]		list;
    int			index;
    int 		id;
    DataContainer	data;
    AbstractContainer		cont;
    AbstractContainer		contNew;
    AbstractContainerManager	manager;
    String		idNew;

    list  = OptionUtils.splitOptions(options);
    index = Integer.parseInt(list[0]) - 1;
    id    = new Integer(list[1]);

    // undo
    addUndoPoint("Saving undo data...", "Set data at " + (index+1) + ": " + id);

    // load data
    showStatus("Loading the data...");
    data    = m_DataProvider.load(id);
    manager = getDataContainerPanel().getContainerManager();
    showStatus("Setting the data...");
    if (data != null) {
      cont    = manager.get(index);
      contNew = manager.newContainer(data);
      idNew   = null;
      if (contNew instanceof NamedContainer)
	idNew = ((NamedContainer) contNew).getID();
      contNew.assign(cont);
      contNew.setPayload(data);
      if (contNew instanceof NamedContainer)
	((NamedContainer) contNew).setID(idNew);
      manager.set(index, contNew);
    }
    showStatus("");

    return null;
  }
}
