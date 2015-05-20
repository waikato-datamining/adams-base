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
 * PromptUser.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.AutomatableInteraction;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.gui.core.GUIHelper;
import adams.gui.core.GUIHelper.DialogCommunication;

/**
 <!-- globalinfo-start -->
 * Prompts the user to click on 'positive' or 'negative' button.<br>
 * The actor's name can be used in the message using the following placeholders:<br>
 * {SHORT} - the short name<br>
 * {FULL} - the full name (incl path)
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with.
 * &nbsp;&nbsp;&nbsp;default: Do you want to execute {SHORT}?
 * </pre>
 * 
 * <pre>-caption-positive &lt;java.lang.String&gt; (property: captionPositive)
 * &nbsp;&nbsp;&nbsp;The caption for the 'positive' button.
 * &nbsp;&nbsp;&nbsp;default: yes
 * </pre>
 * 
 * <pre>-caption-negative &lt;java.lang.String&gt; (property: captionNegative)
 * &nbsp;&nbsp;&nbsp;The caption for the 'negative' button.
 * &nbsp;&nbsp;&nbsp;default: no
 * </pre>
 * 
 * <pre>-initial-selection &lt;java.lang.String&gt; (property: initialSelection)
 * &nbsp;&nbsp;&nbsp;The initial selection to prompt the user with.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-non-interactive &lt;boolean&gt; (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, the condition automatically evaluates to the button that matches 
 * &nbsp;&nbsp;&nbsp;the initial selection or, if that is left empty, to 'true'.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PromptUser
  extends AbstractBooleanCondition
  implements AutomatableInteraction {

  /** for serialization. */
  private static final long serialVersionUID = 3278345095591806425L;

  /** the placeholder for the short actor name. */
  public final static String PLACEHOLDER_SHORT = "{SHORT}";

  /** the placeholder for the full actor name. */
  public final static String PLACEHOLDER_FULL = "{FULL}";

  /** the message to prompt the user with. */
  protected String m_Message;

  /** the caption for the 'positive' button. */
  protected String m_CaptionPositive;

  /** the caption for the 'negative' button. */
  protected String m_CaptionNegative;

  /** the initial selection. */
  protected String m_InitialSelection;

  /** whether the prompt is non-interactive. */
  protected boolean m_NonInteractive;

  /** for cancelation. */
  protected DialogCommunication m_Communication;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Prompts the user to click on 'positive' or 'negative' button.\n"
      + "The actor's name can be used in the message using the following placeholders:\n"
      + PLACEHOLDER_SHORT + " - the short name\n"
      + PLACEHOLDER_FULL + " - the full name (incl path)";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "message", "message",
      "Do you want to execute " + PLACEHOLDER_SHORT + "?");

    m_OptionManager.add(
      "caption-positive", "captionPositive",
      "yes");

    m_OptionManager.add(
      "caption-negative", "captionNegative",
      "no");

    m_OptionManager.add(
      "initial-selection", "initialSelection",
      "");

    m_OptionManager.add(
      "non-interactive", "nonInteractive",
      false);
  }

  /**
   * Sets the message to display.
   *
   * @param value	the message
   */
  public void setMessage(String value) {
    m_Message = value;
    reset();
  }

  /**
   * Returns the message to display.
   *
   * @return		the message
   */
  public String getMessage() {
    return m_Message;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String messageTipText() {
    return "The message to prompt the user with.";
  }

  /**
   * Sets the caption for the 'positive' button.
   *
   * @param value	the caption
   */
  public void setCaptionPositive(String value) {
    m_CaptionPositive = value;
    reset();
  }

  /**
   * Returns the caption for the 'negative' button.
   *
   * @return		the caption
   */
  public String getCaptionPositive() {
    return m_CaptionPositive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String captionPositiveTipText() {
    return "The caption for the 'positive' button.";
  }

  /**
   * Sets the caption for the 'negative' button.
   *
   * @param value	the caption
   */
  public void setCaptionNegative(String value) {
    m_CaptionNegative = value;
    reset();
  }

  /**
   * Returns the caption for the 'negative' button.
   *
   * @return		the caption
   */
  public String getCaptionNegative() {
    return m_CaptionNegative;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String captionNegativeTipText() {
    return "The caption for the 'negative' button.";
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
   * Sets the initial selection to prompt the user with.
   *
   * @param value	the initial selection
   */
  public void setInitialSelection(String value) {
    m_InitialSelection = value;
    reset();
  }

  /**
   * Returns the initial selection the user is prompted with.
   *
   * @return 		the initial selection
   */
  public String getInitialSelection() {
    return m_InitialSelection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String initialSelectionTipText() {
    return "The initial selection to prompt the user with.";
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
    return
      "If enabled, the condition automatically evaluates to the button that "
        + "matches the initial selection or, if that is left empty, to 'true'.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		always 'true'
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "message", m_Message, "message: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		always true
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    String      message;
    String      answer;
    String      initial;

    message = m_Message;
    if (owner != null)
      message = message.replace(PLACEHOLDER_SHORT, owner.getName()).replace(PLACEHOLDER_FULL, owner.getFullName());

    initial = m_InitialSelection.isEmpty() ? m_CaptionPositive : m_InitialSelection;
    if (!m_NonInteractive) {
      m_Communication = new DialogCommunication();
      answer = GUIHelper.showInputDialog(
        (owner == null) ? null : owner.getParentComponent(),
        message,
        initial,
        new String[]{m_CaptionPositive, m_CaptionNegative},
        false,
        "Please choose",
        m_Communication);
      m_Communication = null;
    }
    else {
      answer = initial;
    }

    return (answer != null) && (answer.equals(m_CaptionPositive));
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    if (m_Communication != null)
      m_Communication.requestClose();
  }
}
