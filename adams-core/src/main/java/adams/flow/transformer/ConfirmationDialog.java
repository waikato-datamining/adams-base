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
 * ConfirmationDialog.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.ConsoleHelper;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Pops up a confirmation dialog, prompting the user to select 'yes', 'no' or 'cancel'.<br>
 * If no custom tokens are used, the current token is only forwarded when the user selects 'yes'. Otherwise the token simply gets dropped.<br>
 * In case of custom tokens, depending on the user's selection, either the user-defined 'yes', 'no' or 'cancel' string get forwarded as string tokens.<br>
 * Closing the dialog gets interpreted as selecting the 'cancel' button.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: ConfirmationDialog
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
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
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
 * </pre>
 *
 * <pre>-parent-component-actor &lt;adams.flow.core.CallableActorReference&gt; (property: parentComponentActor)
 * &nbsp;&nbsp;&nbsp;The (optional) callable actor to use as parent component instead of the
 * &nbsp;&nbsp;&nbsp;flow panel.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 *
 * <pre>-use-outer-window &lt;boolean&gt; (property: useOuterWindow)
 * &nbsp;&nbsp;&nbsp;If enabled, the outer window (dialog&#47;frame) is used instead of the component
 * &nbsp;&nbsp;&nbsp;of the callable actor.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title to prompt the user with (variables get expanded).
 * &nbsp;&nbsp;&nbsp;default: Confirm
 * </pre>
 *
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with (variables get expanded).
 * &nbsp;&nbsp;&nbsp;default: Continue with processing of token?
 * </pre>
 *
 * <pre>-custom-tokens &lt;boolean&gt; (property: useCustomTokens)
 * &nbsp;&nbsp;&nbsp;If enabled, custom string tokens are forwarded based on the selection the
 * &nbsp;&nbsp;&nbsp;user makes; otherwise the current token gets forwarded when the user selects
 * &nbsp;&nbsp;&nbsp;'yes' and nothing for 'no'.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-yes-token &lt;java.lang.String&gt; (property: yesToken)
 * &nbsp;&nbsp;&nbsp;The string to forward as token if the user chooses 'yes' in case custom
 * &nbsp;&nbsp;&nbsp;tokens are enabled.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-no-token &lt;java.lang.String&gt; (property: noToken)
 * &nbsp;&nbsp;&nbsp;The string to forward as token if the user chooses 'no' in case custom tokens
 * &nbsp;&nbsp;&nbsp;are enabled.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-cancel-token &lt;java.lang.String&gt; (property: cancelToken)
 * &nbsp;&nbsp;&nbsp;The string to forward as token if the user chooses 'cancel' (or closes the
 * &nbsp;&nbsp;&nbsp;dialog) in case custom tokens are enabled.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ConfirmationDialog
  extends AbstractInteractiveTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8200691218381875131L;

  public static final String YES_OPTION = "Yes";

  public static final String NO_OPTION = "No";

  public static final String CANCEL_OPTION = "Cancel";

  /** the dialog title. */
  protected String m_Title;

  /** the message for the user. */
  protected String m_Message;

  /** whether to forward a custom token instead of the one currently passed
   * through. */
  protected boolean m_UseCustomTokens;

  /** the "yes" token. */
  protected String m_YesToken;

  /** the "no" token. */
  protected String m_NoToken;

  /** the "cancel" token. */
  protected String m_CancelToken;

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
        "Pops up a confirmation dialog, prompting the user to select 'yes', "
      + "'no' or 'cancel'.\n"
      + "If no custom tokens are used, the current token is only forwarded "
      + "when the user selects 'yes'. Otherwise the token simply gets dropped.\n"
      + "In case of custom tokens, depending on the user's selection, either "
      + "the user-defined 'yes', 'no' or 'cancel' string get forwarded as "
      + "string tokens.\n"
      + "Closing the dialog gets interpreted as selecting the 'cancel' button.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "title", "title",
	    "Confirm");

    m_OptionManager.add(
	    "message", "message",
	    "Continue with processing of token?");

    m_OptionManager.add(
	    "custom-tokens", "useCustomTokens",
	    false);

    m_OptionManager.add(
	    "yes-token", "yesToken",
	    "");

    m_OptionManager.add(
	    "no-token", "noToken",
	    "");

    m_OptionManager.add(
	    "cancel-token", "cancelToken",
	    "");
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

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useCustomTokens", m_UseCustomTokens, "using custom tokens"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stops flow if canceled"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the title to prompt the user with.
   *
   * @param value	the title
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the title the user is prompted with.
   *
   * @return 		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String titleTipText() {
    return "The title to prompt the user with (variables get expanded).";
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
    return "The message to prompt the user with (variables get expanded).";
  }

  /**
   * Sets whether to use forward custom string tokens instead of current one.
   *
   * @param value	if true custom tokens get forwarded
   */
  public void setUseCustomTokens(boolean value) {
    m_UseCustomTokens = value;
    reset();
  }

  /**
   * Returns whether custom string tokens are forwared instead of current one.
   *
   * @return 		true if custom tokens get forwarded
   */
  public boolean getUseCustomTokens() {
    return m_UseCustomTokens;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String useCustomTokensTipText() {
    return
        "If enabled, custom string tokens are forwarded based on the selection "
      + "the user makes; otherwise the current token gets forwarded when the "
      + "user selects 'yes' and nothing for 'no'.";
  }

  /**
   * Sets the string to forward as string token in case the user chooses 'yes',
   * in case custom tokens are enabled.
   *
   * @param value	the yes token
   * @see		#getUseCustomTokens()
   */
  public void setYesToken(String value) {
    m_YesToken = value;
    reset();
  }

  /**
   * Returns the string that gets forwarded as token in case the user chooses
   * 'yes' as option, in case custom tokens are enabled.
   *
   * @return 		the yes token
   * @see		#getUseCustomTokens()
   */
  public String getYesToken() {
    return m_YesToken;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String yesTokenTipText() {
    return
        "The string to forward as token if the user chooses 'yes' in case "
      + "custom tokens are enabled.";
  }

  /**
   * Sets the string to forward as string token in case the user chooses 'no',
   * in case custom tokens are enabled.
   *
   * @param value	the no token
   * @see		#getUseCustomTokens()
   */
  public void setNoToken(String value) {
    m_NoToken = value;
    reset();
  }

  /**
   * Returns the string that gets forwarded as token in case the user chooses
   * 'no' as option, in case custom tokens are enabled.
   *
   * @return 		the no token
   * @see		#getUseCustomTokens()
   */
  public String getNoToken() {
    return m_NoToken;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String noTokenTipText() {
    return
        "The string to forward as token if the user chooses 'no' in case "
      + "custom tokens are enabled.";
  }

  /**
   * Sets the string to forward as string token in case the user chooses 'cancel'
   * (or closes the dialog), in case custom tokens are enabled.
   *
   * @param value	the cancel token
   * @see		#getUseCustomTokens()
   */
  public void setCancelToken(String value) {
    m_CancelToken = value;
    reset();
  }

  /**
   * Returns the string that gets forwarded as token in case the user chooses
   * 'cancel' as option (or closes the dialog), in case custom tokens are enabled.
   *
   * @return 		the cancel token
   * @see		#getUseCustomTokens()
   */
  public String getCancelToken() {
    return m_CancelToken;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String cancelTokenTipText() {
    return
        "The string to forward as token if the user chooses 'cancel' (or closes the dialog) in case "
      + "custom tokens are enabled.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the class fo the generated objects
   */
  public Class[] generates() {
    if (m_UseCustomTokens)
      return new Class[]{String.class};
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    int		retVal;
    boolean	canceled;
    String	title;
    String	message;

    m_OutputToken = null;

    title    = getVariables().expand(m_Title);
    message  = getVariables().expand(m_Message);
    m_Comm   = new GUIHelper.DialogCommunication();
    retVal   = GUIHelper.showConfirmMessage(
      getActualParentComponent(), null, message, title,
      YES_OPTION, NO_OPTION, CANCEL_OPTION, m_Comm);
    canceled = (retVal == ApprovalDialog.CANCEL_OPTION);

    if (!canceled) {
      if (m_UseCustomTokens) {
	if (retVal == ApprovalDialog.APPROVE_OPTION)
	  m_OutputToken = new Token(m_YesToken);
	else if (retVal == ApprovalDialog.DISCARD_OPTION)
	  m_OutputToken = new Token(m_NoToken);
      }
      else {
	if (retVal == ApprovalDialog.APPROVE_OPTION)
	  m_OutputToken = m_InputToken;
      }
    }
    else {
      if (m_UseCustomTokens)
        m_OutputToken = new Token(m_CancelToken);
    }

    m_Comm = null;

    return !canceled;
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
    boolean	canceled;
    String	value;

    m_OutputToken = null;

    value    = ConsoleHelper.selectOption(m_Message, new String[]{YES_OPTION, NO_OPTION, CANCEL_OPTION});
    canceled = value.equals(CANCEL_OPTION);

    if (!canceled) {
      if (m_UseCustomTokens) {
	if (value.equals(YES_OPTION))
	  m_OutputToken = new Token(m_YesToken);
	else if (value.equals(NO_OPTION))
	  m_OutputToken = new Token(m_NoToken);
      }
      else {
	if (value.equals(YES_OPTION))
	  m_OutputToken = m_InputToken;
      }
    }
    else {
      if (m_UseCustomTokens)
        m_OutputToken = new Token(m_CancelToken);
    }

    return !canceled;
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
