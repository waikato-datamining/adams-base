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

/*
 * Itself.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.operation;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.RunnableWithLogging;

/**
 * Restarts the flow as defined in variable {@link adams.flow.core.ActorUtils#FLOW_FILENAME_LONG}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RestartItself
  extends AbstractRestartOperation {

  private static final long serialVersionUID = 5721670854550551855L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Restarts the flow as defined in variable " + ActorUtils.FLOW_FILENAME_LONG + ".";
  }

  /**
   * Restarts the flow.
   *
   * @param flow	the flow to handle
   * @return		null if successfully restarted, otherwise the error message
   */
  @Override
  public String restart(Flow flow) {
    String		result;
    String		flowFile;
    MessageCollection	errors;
    Actor		actor;
    final Flow		newFlow;
    RunnableWithLogging	runnable;

    result = null;

    flowFile = flow.getVariables().get(ActorUtils.FLOW_FILENAME_LONG);
    if (flowFile == null)
      result = "Variable " + ActorUtils.FLOW_FILENAME_LONG + " not defined?";
    else if (!FileUtils.fileExists(flowFile))
      result = "Flow file does not exist: " + flowFile;

    actor = null;
    if (result == null) {
      if (isLoggingEnabled())
        getLogger().info("Stopping flow...");
      flow.stopExecution();
      if (isLoggingEnabled())
        getLogger().info("Reading flow: " + flowFile);
      errors = new MessageCollection();
      actor = ActorUtils.read(flowFile, errors);
      if (!(actor instanceof Flow))
        result = "Loaded actor is not of type Flow: " + Utils.classToString(actor);
    }

    if (result == null) {
      newFlow = (Flow) actor;
      ActorUtils.updateProgrammaticVariables(newFlow, new PlaceholderFile(flowFile));
      result = newFlow.setUp();
      if (result == null) {
	ActorUtils.updateProgrammaticVariables(newFlow, new PlaceholderFile(flowFile));
	runnable = new RunnableWithLogging() {
	  private static final long serialVersionUID = -5446295909630418597L;
	  @Override
	  protected void doRun() {
	    String msg = newFlow.execute();
	    if (msg != null)
	      getLogger().severe(msg);
	  }
	};
	new Thread(runnable).start();
      }
    }

    return result;
  }
}
