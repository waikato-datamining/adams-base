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
 * AbstractInteractiveSource.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.flow.core.InteractiveActor;

/**
 * Ancestor for source actors that interact with the user.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInteractiveSource
  extends AbstractSource
  implements InteractiveActor {

  /** for serialization. */
  private static final long serialVersionUID = 9035095174755816475L;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "stop-if-canceled", "stopFlowIfCanceled",
	    false);

    m_OptionManager.add(
	    "custom-stop-message", "customStopMessage",
	    "");
  }

  /**
   * Sets whether to stop the flow if dialog canceled.
   *
   * @param value	if true flow gets stopped if dialog canceled
   */
  public void setStopFlowIfCanceled(boolean value) {
    m_StopFlowIfCanceled = value;
    reset();
  }

  /**
   * Returns whether to stop the flow if dialog canceled.
   *
   * @return 		true if the flow gets stopped if dialog canceled
   */
  public boolean getStopFlowIfCanceled() {
    return m_StopFlowIfCanceled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String stopFlowIfCanceledTipText() {
    return "If enabled, the flow gets stopped in case the user cancels the dialog.";
  }

  /**
   * Sets the custom message to use when stopping the flow.
   *
   * @param value	the stop message
   */
  public void setCustomStopMessage(String value) {
    m_CustomStopMessage = value;
    reset();
  }

  /**
   * Returns the custom message to use when stopping the flow.
   *
   * @return		the stop message
   */
  public String getCustomStopMessage() {
    return m_CustomStopMessage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String customStopMessageTipText() {
    return
        "The custom stop message to use in case a user cancelation stops the "
      + "flow (default is the full name of the actor)";
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  public abstract boolean doInteract();

  /**
   * Returns whether headless interaction is supported.
   *
   * @return		true if interaction in headless environment is possible
   */
  public boolean supportsHeadlessInteraction() {
    return false;
  }

  /**
   * Performs the interaction with the user in a headless environment.
   *
   * @return		true if successfully interacted
   */
  public boolean doInteractHeadless() {
    return true;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    if (!isHeadless()) {
      if (!doInteract()) {
	if (m_StopFlowIfCanceled) {
	  if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
	    getRoot().stopExecution("Flow canceled: " + getFullName());
	  else
	    getRoot().stopExecution(m_CustomStopMessage);
	}
      }
    }
    else if (supportsHeadlessInteraction()) {
      if (!doInteractHeadless()) {
	if (m_StopFlowIfCanceled) {
	  if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
	    getRoot().stopExecution("Flow canceled: " + getFullName());
	  else
	    getRoot().stopExecution(m_CustomStopMessage);
	}
      }
    }

    return m_StopMessage;
  }
}
