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
 * AbstractFlowScriptlet.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.flow.control.SubProcess;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ColorContainer;
import adams.gui.visualization.container.VisibilityContainerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for scriptlets that run flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFlowScriptlet
  extends AbstractDataContainerUpdatingScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -2467186252442407554L;

  /**
   * Returns a one-line listing of the options of the action.
   *
   * @return		the options or null if none
   */
  protected String getOptionsDescription() {
    return "<filename>";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @param overlay	whether to overlay the data
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  public String process(String options, boolean overlay) throws Exception {
    SubProcess			actor;
    SubProcess			runActor;
    String			result;
    String[]			list;
    int				i;
    AbstractContainerManager	manager;
    List<DataContainer> 	runInput;
    List<DataContainer>		runOutput;
    Class[]			flowClasses;
    List<String>		errors;

    manager = getDataContainerPanel().getContainerManager();

    if (((VisibilityContainerManager) manager).countVisible() == 0)
      return "No visible chromatograms!";

    // instantiate flow
    list    = OptionUtils.splitOptions(options);
    if (list.length != 1)
      return "Needs a single filename as parameter!";
    errors = new ArrayList<>();
    actor  = (SubProcess) ActorUtils.read(list[0], errors);
    if (!errors.isEmpty())
      return "Failed to load actor from '" + list[0] + "':\n" + Utils.flatten(errors, "\n");
    if (actor == null)
      return "Could not instantiate actor from '" + list[0] + "'!";

    // does flow accept chromatograms?
    flowClasses = actor.accepts();
    result      = "Flow '" + list[0] + "' does not accept single chromatograms!";
    for (i = 0; i < flowClasses.length; i++) {
      if (flowClasses[i] == getOwner().getRequiredFlowClass()) {
	result = null;
	break;
      }
    }
    if (result != null)
      return result;

    // does flow generate chromatograms?
    flowClasses = actor.generates();
    result      = "Flow '" + list[0] + "' does not generate single chromatograms!";
    for (i = 0; i < flowClasses.length; i++) {
      if (flowClasses[i] == getOwner().getRequiredFlowClass()) {
	result = null;
	break;
      }
    }
    if (result != null)
      return result;

    // get data that is to be run through flow
    runInput = new ArrayList<>();
    for (i = 0; i < manager.count(); i++) {
      if (((VisibilityContainerManager) manager).isVisible(i))
	runInput.add((DataContainer) manager.get(i).getPayload());
    }

    // process data
    runOutput = new ArrayList<>();
    for (i = 0; i < runInput.size(); i++) {
      showStatus("Passing data through flow " + (i+1) + "/" + ((VisibilityContainerManager) manager).countVisible());

      runActor = (SubProcess) actor.shallowCopy(true);
      runActor.setUp();
      runActor.input(new Token(runInput.get(i)));
      result = runActor.execute();
      if (result == null)
	runOutput.add((DataContainer) runActor.output().getPayload());
      runActor.wrapUp();
      runActor.destroy();

      if (result != null)
	break;
    }
    actor.destroy();
    showStatus("");
    if (result != null)
      return result;

    // transfer color
    if (!overlay && (runOutput.size() == runInput.size())) {
      for (i = 0; i < runOutput.size(); i++) {
	if (runOutput.get(i) instanceof ColorContainer)
	  ((ColorContainer) runOutput.get(i)).setColor(((ColorContainer) runInput.get(i)).getColor());
      }
    }

    // undo
    addUndoPoint("Saving undo data...", "Flow: " + list[0]);

    // update data
    updateDataContainers(runOutput, overlay);

    return null;
  }
}
