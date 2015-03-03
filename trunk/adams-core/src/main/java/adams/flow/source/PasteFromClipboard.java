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
 * PasteFromClipboard.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import javax.swing.JOptionPane;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.gui.clipboard.AbstractClipboardData;

/**
 <!-- globalinfo-start -->
 * Pops up a dialog, prompting the user to accept content from clipboard.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.gui.clipboard.String<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: PasteFromClipboard
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-stop-if-canceled (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * </pre>
 * 
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow 
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with.
 * &nbsp;&nbsp;&nbsp;default: Click \'OK\' to obtain text from clipboard
 * </pre>
 * 
 * <pre>-clipboard-data &lt;adams.gui.clipboard.AbstractClipboardData&gt; (property: clipboardData)
 * &nbsp;&nbsp;&nbsp;The class for pasting a specific type of data from the clipboard.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.clipboard.String
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PasteFromClipboard
  extends AbstractInteractiveSource {

  /** for serialization. */
  private static final long serialVersionUID = 8200691218381875131L;
  
  /** the message for the user. */
  protected String m_Message;

  /** the data type to paste. */
  protected AbstractClipboardData m_ClipboardData;
  
  /** the output token to broadcast. */
  protected Token m_OutputToken;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Pops up a dialog, prompting the user to accept content from clipboard.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "message", "message",
	    "Click 'OK' to obtain text from clipboard");

    m_OptionManager.add(
	    "clipboard-data", "clipboardData",
	    new adams.gui.clipboard.String());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "message", m_Message);
    result += QuickInfoHelper.toString(this, "clipboardData", m_ClipboardData, ", data: ");

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
    return "The message to prompt the user with.";
  }

  /**
   * Sets the type of data to paste.
   *
   * @param value	the data type
   */
  public void setClipboardData(AbstractClipboardData value) {
    m_ClipboardData = value;
    reset();
  }

  /**
   * Returns the type of data to paste.
   *
   * @return 		the data type
   */
  public AbstractClipboardData getClipboardData() {
    return m_ClipboardData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String clipboardDataTipText() {
    return "The class for pasting a specific type of data from the clipboard.";
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
   * @return		<!-- flow-generates-start -->adams.gui.clipboard.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{m_ClipboardData.getFlowClass()};
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    int		retVal;

    if (m_Headless)
      return true;
	  
    retVal = JOptionPane.showConfirmDialog(getParentComponent(), m_Message, getName(), JOptionPane.OK_CANCEL_OPTION);
    if ((retVal == JOptionPane.OK_OPTION) && (m_ClipboardData.canPaste()))
      m_OutputToken = m_ClipboardData.pasteAsToken();

    return (retVal == JOptionPane.OK_OPTION);
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
}
