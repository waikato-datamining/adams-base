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
 * EnterValue.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.io.ConsoleHelper;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.Token;
import adams.gui.core.GUIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Pops up a dialog, prompting the user to enter a value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: EnterValue
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow 
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with; variables get expanded prior to prompting 
 * &nbsp;&nbsp;&nbsp;user.
 * &nbsp;&nbsp;&nbsp;default: Please enter a value
 * </pre>
 * 
 * <pre>-initial-value &lt;java.lang.String&gt; (property: initialValue)
 * &nbsp;&nbsp;&nbsp;The initial value to prompt the user with; variables get expanded prior 
 * &nbsp;&nbsp;&nbsp;to prompting user.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-selection-values &lt;adams.core.base.BaseString&gt; [-selection-values ...] (property: selectionValues)
 * &nbsp;&nbsp;&nbsp;The options to let the user choose from.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-use-buttons &lt;boolean&gt; (property: useButtons)
 * &nbsp;&nbsp;&nbsp;If enabled and selection values are available, then instead of a dropdown 
 * &nbsp;&nbsp;&nbsp;list a button per selection value is displayed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-non-interactive &lt;boolean&gt; (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, the initial value is forwarded without user interaction.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EnterValue
  extends AbstractInteractiveSource
  implements AutomatableInteractiveActor {

  /** for serialization. */
  private static final long serialVersionUID = 8200691218381875131L;

  /** the message for the user. */
  protected String m_Message;

  /** the initial value. */
  protected String m_InitialValue;

  /** options to select from. */
  protected BaseString[] m_SelectionValues;

  /** whether to automate the actor. */
  protected boolean m_NonInteractive;

  /** whether to use buttons instead of a dropdown list. */
  protected boolean m_UseButtons;
  
  /** the output token to broadcast. */
  protected Token m_OutputToken;

  /** for communicating with the input dialog. */
  protected GUIHelper.DialogCommunication m_Comm;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Pops up a dialog, prompting the user to enter a value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "message", "message",
	    "Please enter a value");

    m_OptionManager.add(
	    "initial-value", "initialValue",
	    "");

    m_OptionManager.add(
	    "selection-values", "selectionValues",
	    new BaseString[0]);

    m_OptionManager.add(
	    "use-buttons", "useButtons",
	    false);

    m_OptionManager.add(
	    "non-interactive", "nonInteractive",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result = QuickInfoHelper.toString(this, "message", m_Message);

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stops flow if canceled"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "nonInteractive", m_NonInteractive, "non-interactive"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useButtons", m_UseButtons, "buttons"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the message to prompt the user with.
   *
   * @param value	the message
   */
  public void setMessage(String value) {
    m_Message = value;
    reset();
  }

  /**
   * Returns the message the user is prompted with.
   *
   * @return 		the message
   */
  public String getMessage() {
    return m_Message;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String messageTipText() {
    return "The message to prompt the user with; variables get expanded prior to prompting user.";
  }

  /**
   * Sets the initial value to prompt the user with.
   *
   * @param value	the initial value
   */
  public void setInitialValue(String value) {
    m_InitialValue = value;
    reset();
  }

  /**
   * Returns the initial value the user is prompted with.
   *
   * @return 		the initial value
   */
  public String getInitialValue() {
    return m_InitialValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String initialValueTipText() {
    return "The initial value to prompt the user with; variables get expanded prior to prompting user.";
  }

  /**
   * Sets the selection values to prompt the user with.
   *
   * @param value	the selection values
   */
  public void setSelectionValues(BaseString[] value) {
    m_SelectionValues = value;
    reset();
  }

  /**
   * Returns the selection values to prompt the user with.
   *
   * @return 		the selection values
   */
  public BaseString[] getSelectionValues() {
    return m_SelectionValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String selectionValuesTipText() {
    return "The options to let the user choose from.";
  }

  /**
   * Sets whether to use buttons or a drop-down list for the selection values.
   *
   * @param value	true if to use buttons
   */
  public void setUseButtons(boolean value) {
    m_UseButtons = value;
    reset();
  }

  /**
   * Returns whether to use buttons or a drop-down list for the selection values.
   *
   * @return 		true if to use buttons
   */
  public boolean getUseButtons() {
    return m_UseButtons;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String useButtonsTipText() {
    return 
	"If enabled and selection values are available, then instead of a "
	+ "dropdown list a button per selection value is displayed.";
  }

  /**
   * Sets whether to enable/disable interactiveness.
   *
   * @param value	if true actor is not interactive, but automated
   */
  public void setNonInteractive(boolean value) {
    m_NonInteractive = value;
    reset();
  }

  /**
   * Returns whether interactiveness is enabled/disabled.
   *
   * @return 		true if actor is not interactive i.e., automated
   */
  public boolean isNonInteractive() {
    return m_NonInteractive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String nonInteractiveTipText() {
    return "If enabled, the initial value is forwarded without user interaction.";
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_OutputToken = null;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    String	value;
    String	msg;
    String	initial;

    msg     = m_Message;
    msg     = getVariables().expand(msg);
    initial = m_InitialValue;
    initial = getVariables().expand(initial);
    m_Comm  = new GUIHelper.DialogCommunication();

    if (m_NonInteractive) {
      m_OutputToken = new Token(initial);
      return true;
    }
    
    if (m_SelectionValues.length > 0)
      value = GUIHelper.showInputDialog(
        GUIHelper.getParentComponent(getParentComponent()),
        msg, initial, BaseObject.toStringArray(m_SelectionValues),
        !m_UseButtons, getName(), m_Comm);
    else
      value = GUIHelper.showInputDialog(
        GUIHelper.getParentComponent(getParentComponent()),
        msg, initial, getName(), m_Comm);

    if ((value != null) && (value.length() > 0))
      m_OutputToken = new Token(value);

    m_Comm = null;

    return ((value != null) && (value.length() > 0));
  }

  /**
   * Returns whether headless interaction is supported.
   *
   * @return		true if interaction in headless environment is possible
   */
  public boolean supportsHeadlessInteraction() {
    return true;
  }

  /**
   * Performs the interaction with the user in a headless environment.
   *
   * @return		true if successfully interacted
   */
  public boolean doInteractHeadless() {
    String	value;
    String	msg;
    String	initial;

    msg     = m_Message;
    msg     = getVariables().expand(msg);
    initial = m_InitialValue;
    initial = getVariables().expand(initial);

    if (m_NonInteractive) {
      m_OutputToken = new Token(initial);
      return true;
    }

    if (m_SelectionValues.length > 0)
      value = ConsoleHelper.selectOption(msg, BaseObject.toStringArray(m_SelectionValues), initial);
    else
      value = ConsoleHelper.enterValue(msg, initial);

    if ((value != null) && (value.length() > 0))
      m_OutputToken = new Token(value);

    return ((value != null) && (value.length() > 0));
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result        = m_OutputToken;
    m_OutputToken = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_OutputToken != null);
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Comm != null) {
      synchronized(m_Comm) {
        m_Comm.requestClose();
      }
    }
    super.stopExecution();
  }
}
