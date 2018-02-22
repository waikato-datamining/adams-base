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
 * AbstractRemoteCommandOnFlowWithResponse.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command;

import adams.core.VariablesHandler;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.control.RunningFlowsRegistry;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;

/**
 * Ancestor for commands that work on flows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRemoteCommandOnFlowWithResponse
  extends AbstractCommandWithResponse
  implements RemoteCommandOnFlow {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the ID of the flow to retrieve. */
  protected Integer m_ID;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "id", "ID",
      -1, -1, null);
  }

  /**
   * Sets the ID of the flow.
   *
   * @param value	the ID, -1 if to use the only one
   */
  public void setID(int value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID of the flow to get.
   *
   * @return		the ID, -1 if to use the only one
   */
  public int getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public abstract String IDTipText();

  /**
   * Retrieves the flow.
   * <br>
   * NB: Sets the error message if it fails to retrieve flow.
   *
   * @param loadFromDisk	whether to load the flow from disk
   * @return			the flow, null if failed to retrieve
   * @see			#hasErrorMessage()
   * @see			#getErrorMessage()
   */
  protected Actor retrieveFlow(boolean loadFromDisk) {
    Actor   	result;
    String	flowFile;

    result = null;

    if (m_ID == -1) {
      if (RunningFlowsRegistry.getSingleton().size() == 1)
        result = RunningFlowsRegistry.getSingleton().flows()[0];
      else
	m_ErrorMessage = "Using ID '-1' is only allowed if there is just a single flow registered (registered: " + RunningFlowsRegistry.getSingleton().size() + ")";
    }
    else {
      result = RunningFlowsRegistry.getSingleton().getFlow(m_ID);
      if (result == null)
	m_ErrorMessage = "Failed to retrieve flow for ID " + m_ID + "!";
    }

    if ((result != null) && loadFromDisk) {
      flowFile = result.getVariables().get(ActorUtils.FLOW_FILENAME_LONG);
      if (flowFile == null) {
	m_ErrorMessage = "Variable '" + ActorUtils.FLOW_FILENAME_LONG + "' not set, cannot load from disk!";
      }
      else {
	if (FileUtils.fileExists(flowFile)) {
	  result = ActorUtils.read(flowFile);
	  if (result == null)
	    m_ErrorMessage = "Failed to load flow from  '" + flowFile + "'!";
	  else
	    ActorUtils.updateProgrammaticVariables((VariablesHandler & Actor) result, new PlaceholderFile(flowFile));
	}
	else {
	  m_ErrorMessage = "Flow '" + flowFile + "' does not exist!";
	}
      }
    }

    if (m_ErrorMessage != null)
      getLogger().severe(m_ErrorMessage);

    return result;
  }
}
