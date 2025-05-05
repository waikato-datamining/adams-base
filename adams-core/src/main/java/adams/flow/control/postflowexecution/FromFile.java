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
 * FromFile.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control.postflowexecution;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.io.FlowFile;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;

/**
 * Loads the actor from the specified file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FromFile
  extends AbstractPostFlowExecution {

  private static final long serialVersionUID = -5816582392372151789L;

  /** the flow to load and configure. */
  protected FlowFile m_FlowFile;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads the actor from the specified file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "flow-file", "flowFile",
      new FlowFile("."));
  }

  /**
   * Sets the external flow to execute in case the flow finishes with an error.
   *
   * @param value 	the external flow
   */
  public void setFlowFile(FlowFile value) {
    m_FlowFile = value;
    reset();
  }

  /**
   * Returns the external flow to execute in case the flow finishes with an error.
   *
   * @return 		the external flow
   */
  public FlowFile getFlowFile() {
    return m_FlowFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowFileTipText() {
    return
      "The external flow to execute in case the flow finishes with an "
	+ "error; allows the user to call a clean-up flow.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "flowFile", m_FlowFile, "file: ");
  }

  /**
   * Configures the actor to execute after the flow has run (without calling setUp()).
   *
   * @param errors for collecting errors during configuration
   * @return the actor, null if none generated
   */
  @Override
  protected Actor doConfigureExecution(MessageCollection errors) {
    Actor	result;

    result = null;

    if (!m_FlowFile.isDirectory() && m_FlowFile.exists()) {
      result = ActorUtils.read(m_FlowFile.getAbsolutePath(), errors);
      if (!errors.isEmpty() || (result == null)) {
	errors.add("Error loading execute-on-error actor from: " + m_FlowFile);
	result = null;
      }
    }

    return result;
  }
}
