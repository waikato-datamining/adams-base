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
 * AbstractInteractiveTransformer.java
 * Copyright (C) 2011-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.flow.core.AbstractDisplay;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.InteractiveActorWithCustomParentComponent;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import adams.gui.core.GUIHelper;

import java.awt.Component;

/**
 * Ancestor for transformers that interact with the user.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInteractiveTransformer
  extends AbstractTransformer
  implements InteractiveActorWithCustomParentComponent {

  /** for serialization. */
  private static final long serialVersionUID = 9035095174755816475L;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** how to perform the stop. */
  protected StopMode m_StopMode;

  /** the (optional) parent component to use. */
  protected CallableActorReference m_ParentComponentActor;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /** whether the callable actor has been configured. */
  protected boolean m_ParentComponentActorConfigured;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** whether to use the outer window as parent. */
  protected boolean m_UseOuterWindow;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "stop-if-canceled", "stopFlowIfCanceled",
      false);

    m_OptionManager.add(
      "custom-stop-message", "customStopMessage",
      "");

    m_OptionManager.add(
      "stop-mode", "stopMode",
      StopMode.GLOBAL);

    m_OptionManager.add(
      "parent-component-actor", "parentComponentActor",
      new CallableActorReference(CallableActorReference.UNKNOWN));

    m_OptionManager.add(
      "use-outer-window", "useOuterWindow",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor                  = null;
    m_ParentComponentActorConfigured = false;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
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
   * Sets the stop mode.
   *
   * @param value	the mode
   */
  @Override
  public void setStopMode(StopMode value) {
    m_StopMode = value;
    reset();
  }

  /**
   * Returns the stop mode.
   *
   * @return		the mode
   */
  @Override
  public StopMode getStopMode() {
    return m_StopMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String stopModeTipText() {
    return "The stop mode to use.";
  }

  /**
   * Sets the (optional) callable actor to use as parent component instead of
   * the flow panel.
   *
   * @param value	the callable actor
   */
  public void setParentComponentActor(CallableActorReference value) {
    m_ParentComponentActor = value;
    reset();
  }

  /**
   * Returns the (optional) callable actor to use as parent component instead
   * of the flow panel.
   *
   * @return 		the callable actor
   */
  public CallableActorReference getParentComponentActor() {
    return m_ParentComponentActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String parentComponentActorTipText() {
    return "The (optional) callable actor to use as parent component instead of the flow panel.";
  }

  /**
   * Sets whether to use the outer window as parent.
   *
   * @param value	true if to use outer window
   */
  public void setUseOuterWindow(boolean value) {
    m_UseOuterWindow = value;
    reset();
  }

  /**
   * Returns whether to use the outer window as parent.
   *
   * @return 		true if to use outer window
   */
  public boolean getUseOuterWindow() {
    return m_UseOuterWindow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String useOuterWindowTipText() {
    return
      "If enabled, the outer window (dialog/frame) is used instead of the "
        + "component of the callable actor.";
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getParentComponentActor());
  }

  /**
   * Returns the parent component to use.
   *
   * @return		the parent
   */
  public Component getActualParentComponent() {
    Component	result;
    Component	panel;

    result = getParentComponent();

    if (m_CallableActor == null) {
      if (!m_ParentComponentActorConfigured) {
        m_CallableActor                  = findCallableActor();
        m_ParentComponentActorConfigured = true;
      }
    }

    if (m_CallableActor != null) {
      if (m_CallableActor instanceof AbstractDisplay) {
        panel = ((AbstractDisplay) m_CallableActor).getPanel();
        if (panel != null)
          result = panel;
      }
    }

    // component or window?
    if (m_UseOuterWindow)
      result = GUIHelper.getParentComponent(result);

    return result;
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
  @Override
  protected String doExecute() {
    if (!isHeadless()) {
      if (!doInteract()) {
        if (m_StopFlowIfCanceled) {
          if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
            StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
          else
            StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
        }
      }
    }
    else if (supportsHeadlessInteraction()) {
      if (!doInteractHeadless()) {
        if (m_StopFlowIfCanceled) {
          if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
            StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
          else
            StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
        }
      }
    }

    return m_StopMessage;
  }
}
