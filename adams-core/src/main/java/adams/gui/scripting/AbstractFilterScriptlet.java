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
 * AbstractFilterScriptlet.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.data.container.DataContainer;
import adams.data.filter.AbstractFilter;
import adams.data.filter.BatchFilter;
import adams.db.DatabaseConnectionHandler;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.VisibilityContainerManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract filter scriptlet.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFilterScriptlet
  extends AbstractDataContainerUpdatingScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -9200477598539423747L;

  /**
   * Returns a one-line listing of the options of the action.
   *
   * @return		the options or null if none
   */
  @Override
  protected String getOptionsDescription() {
    return "<classname + options>";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @param overlay	whether to overlay the original spectra
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  public String process(String options, boolean overlay) throws Exception {
    adams.data.filter.Filter 	runScheme;
    int				i;
    List<DataContainer> 	runInput;
    Object			runInputA;
    List<DataContainer> 	runOutput;
    DataContainer[] 		runOutputA;
    List<DataContainer> 	runOutputC;
    AbstractContainerManager	manager;
    adams.data.filter.Filter 	actualScheme;

    manager = getDataContainerPanel().getContainerManager();

    if (((VisibilityContainerManager) manager).countVisible() == 0)
      return "No visible containers!";

    // get data that is to be filtered
    runInput = new ArrayList<>();
    for (i = 0; i < manager.count(); i++) {
      if (((VisibilityContainerManager) manager).isVisible(i))
	runInput.add((DataContainer) manager.get(i).getPayload());
    }

    // run filter
    runScheme = AbstractFilter.forCommandLine(options);
    if (runScheme instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) runScheme).setDatabaseConnection(getOwner().getDatabaseConnection());

    // undo
    addUndoPoint("Saving undo data...", "Filter: " + runScheme.getClass().getName().replaceAll(".*\\.", ""));

    showStatus("Filtering...");

    // pass through filter
    actualScheme = runScheme.shallowCopy(true);
    runOutputC   = new ArrayList<>();
    if (actualScheme instanceof BatchFilter) {
      runInputA  = Array.newInstance(runInput.get(0).getClass(), runInput.size());
      for (i = 0; i < runInput.size(); i++)
	Array.set(runInputA, i, runInput.get(i));
      runOutputA = ((BatchFilter) actualScheme).batchFilter((DataContainer[]) (runInputA));
      runOutput  = new ArrayList<>();
      runOutput.addAll(Arrays.asList(runOutputA));
    }
    else {
      runOutput = AbstractFilter.filter(actualScheme, runInput);
    }
    for (i = 0; i < runOutput.size(); i++)
      runOutputC.add(runOutput.get(i));

    // update containers
    updateDataContainers(runOutputC, overlay);

    runScheme.destroy();
    actualScheme.destroy();

    showStatus("");

    return null;
  }
}
