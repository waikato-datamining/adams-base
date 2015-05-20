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
 * SelectDateTime.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.util.Date;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseObject;
import adams.core.base.BaseTime;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.Token;
import adams.gui.chooser.DatePanel;
import adams.gui.chooser.DateProvider;
import adams.gui.chooser.DateTimePanel;
import adams.gui.chooser.TimePanel;
import adams.gui.core.BasePanel;
import adams.gui.dialog.ApprovalDialog;

/**
 <!-- globalinfo-start -->
 * Pops up a dialog, prompting the user to select a date&#47;time, date or time value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.core.base.BaseDateTime<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: SelectDateTime
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
 * <pre>-type &lt;DATE_AND_TIME|DATE|TIME&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of data to output.
 * &nbsp;&nbsp;&nbsp;default: DATE_AND_TIME
 * </pre>
 * 
 * <pre>-output-as-string (property: outputAsString)
 * &nbsp;&nbsp;&nbsp;If enabled, a string is output rather than a typed object.
 * </pre>
 * 
 * <pre>-non-interactive (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, a value based on the current date&#47;time is forwarded without 
 * &nbsp;&nbsp;&nbsp;user interaction.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SelectDateTime
  extends AbstractInteractiveSource
  implements AutomatableInteractiveActor {

  /** for serialization. */
  private static final long serialVersionUID = 8200691218381875131L;

  /**
   * Determines what dialog is presented to the user and what the generated 
   * data type is.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** date and time. */
    DATE_AND_TIME,
    /** just date. */
    DATE,
    /** just time. */
    TIME
  }
  
  /** the type of data to output. */
  protected Type m_Type;

  /** whether to output a string rather than objects. */
  protected boolean m_OutputAsString;

  /** whether to automate the actor. */
  protected boolean m_NonInteractive;
  
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
        "Pops up a dialog, prompting the user to select a date/time, date or time value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    Type.DATE_AND_TIME);

    m_OptionManager.add(
	    "output-as-string", "outputAsString",
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
    String	result;

    result  = QuickInfoHelper.toString(this, "type", m_Type);
    result += QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stops flow if canceled", ", ");
    result += QuickInfoHelper.toString(this, "outputAsString", m_OutputAsString, "output string", ", ");
    result += QuickInfoHelper.toString(this, "nonInteractive", m_NonInteractive, "non-interactive", ", ");

    return result;
  }

  /**
   * Sets the type of data to output.
   *
   * @param value	the data type
   */
  public void setType(Type value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of data to output.
   *
   * @return 		the data type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of data to output.";
  }

  /**
   * Sets the initial value to prompt the user with.
   *
   * @param value	the initial value
   */
  public void setOutputAsString(boolean value) {
    m_OutputAsString = value;
    reset();
  }

  /**
   * Returns the initial value the user is prompted with.
   *
   * @return 		the initial value
   */
  public boolean getOutputAsString() {
    return m_OutputAsString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String outputAsStringTipText() {
    return "If enabled, a string is output rather than a typed object.";
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
    return "If enabled, a value based on the current date/time is forwarded without user interaction.";
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
   * @return		<!-- flow-generates-start -->adams.core.base.BaseDateTime.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    if (m_OutputAsString) {
      return new Class[]{String.class};
    }
    else {
      switch (m_Type) {
	case DATE_AND_TIME:
	  return new Class[]{BaseDateTime.class};
	case DATE:
	  return new Class[]{BaseDate.class};
	case TIME:
	  return new Class[]{BaseTime.class};
	default:
	  throw new IllegalStateException("Unhandled type: " + m_Type);
      }
    }
  }
  
  /**
   * Creates a token from the provided date object.
   * 
   * @param dateTime	the date to use, null if to use current date/time
   * @return		the generated token
   */
  protected Token createToken(Date dateTime) {
    Token	result;
    BaseObject	value;

    if (dateTime == null)
      dateTime = new Date();
    
    switch (m_Type) {
      case DATE_AND_TIME:
	value = new BaseDateTime(dateTime);
	break;
      case DATE:
	value = new BaseDate(dateTime);
	break;
      case TIME:
	value = new BaseTime(dateTime);
	break;
      default:
	throw new IllegalStateException("Unhandled type: " + m_Type);
    }

    if (m_OutputAsString)
      result = new Token(value.toString());
    else
      result = new Token(value);
    
    return result;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    ApprovalDialog	dialog;
    BasePanel		panel;
    String		title;

    if (m_NonInteractive) {
      m_OutputToken = createToken(null);
      return true;
    }

    switch (m_Type) {
      case DATE_AND_TIME:
	title = "date and time";
	panel = new DateTimePanel();
	break;
      case DATE:
	title = "date";
	panel = new DatePanel();
	break;
      case TIME:
	title = "time";
	panel = new TimePanel();
	break;
      default:
	throw new IllegalStateException("Unhandled type: " + m_Type);
    }
    title = "Select " + title;
    
    dialog = new ApprovalDialog(null, ModalityType.DOCUMENT_MODAL);
    dialog.setTitle(title);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(getParentComponent());
    dialog.setVisible(true);
    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      m_OutputToken = createToken(((DateProvider) panel).getDate());
      return true;
    }

    return false;
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
