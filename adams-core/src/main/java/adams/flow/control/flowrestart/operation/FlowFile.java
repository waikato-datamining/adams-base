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
 * FlowFile.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.operation;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.RunnableWithLogging;

/**
 * Starts the specified flow.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FlowFile
  extends AbstractRestartOperation {

  private static final long serialVersionUID = 5721670854550551855L;

  /** the command to start. */
  protected adams.core.io.FlowFile m_FlowFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Starts the specified flow.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "flow-file", "flowFile",
      new adams.core.io.FlowFile("."));
  }

  /**
   * Sets the flow to start.
   *
   * @param value	the file
   */
  public void setFlowFile(adams.core.io.FlowFile value) {
    m_FlowFile = value;
    reset();
  }

  /**
   * Returns the flow to start.
   *
   * @return		the file
   */
  public adams.core.io.FlowFile getFlowFile() {
    return m_FlowFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowFileTipText() {
    return "The flow file to start.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "flowFile", m_FlowFile, "flow: ");
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
    final Flow		newFlow;
    MessageCollection	errors;
    Actor		actor;
    RunnableWithLogging	runnable;

    result = null;

    stopFlow(flow);

    if (isLoggingEnabled())
      getLogger().info("Reading flow: " + m_FlowFile);
    errors = new MessageCollection();
    actor = ActorUtils.read(m_FlowFile.getAbsolutePath(), errors);
    if (!(actor instanceof Flow))
      result = "Loaded actor is not of type Flow: " + Utils.classToString(actor);

    if (result == null) {
      newFlow = (Flow) actor;
      ActorUtils.updateProgrammaticVariables(newFlow, new PlaceholderFile(m_FlowFile));
      result = newFlow.setUp();
      if (result == null) {
	ActorUtils.updateProgrammaticVariables(newFlow, new PlaceholderFile(m_FlowFile));
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

    return null;
  }
}
