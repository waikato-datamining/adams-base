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
 * EnterManyValues.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.Token;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.dialog.ApprovalDialog;

/**
 <!-- globalinfo-start -->
 * Pops up a dialog, prompting the user to enter one or more values.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: EnterManyValues
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with.
 * &nbsp;&nbsp;&nbsp;default: Please enter values
 * </pre>
 * 
 * <pre>-value &lt;adams.flow.source.ValueDefinition&gt; [-value ...] (property: values)
 * &nbsp;&nbsp;&nbsp;The value definitions that define the dialog prompting the user to enter 
 * &nbsp;&nbsp;&nbsp;the values.
 * &nbsp;&nbsp;&nbsp;default: 
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
public class EnterManyValues
  extends AbstractInteractiveSource
  implements AutomatableInteractiveActor {

  /** for serialization. */
  private static final long serialVersionUID = 8200691218381875131L;

  /** the message for the user. */
  protected String m_Message;

  /** the value definitions. */
  protected ValueDefinition[] m_Values;

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
    return "Pops up a dialog, prompting the user to enter one or more values.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "message", "message",
	    "Please enter values");

    m_OptionManager.add(
	    "value", "values",
	    new ValueDefinition[0]);

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
    return "The message to prompt the user with.";
  }

  /**
   * Sets the value definitions.
   *
   * @param value	the definitions
   */
  public void setValues(ValueDefinition[] value) {
    m_Values = value;
    reset();
  }

  /**
   * Returns the value definitions.
   *
   * @return 		the definitions
   */
  public ValueDefinition[] getValues() {
    return m_Values;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String valuesTipText() {
    return "The value definitions that define the dialog prompting the user to enter the values.";
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
   * @return		<!-- flow-generates-start -->adams.data.spreadsheet.SpreadSheet.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null) {
      if (m_Values.length == 0)
	result = "No values defined!";
    }
    
    return result;
  }
  
  /**
   * Returns the default properties.
   * 
   * @return		the default properties
   */
  protected Properties getDefaultProperties() {
    Properties	result;
    
    result = new Properties();
    for (ValueDefinition val: m_Values)
      result.setProperty(val.getName(), getVariables().expand(val.getDefaultValue()));
    
    return result;
  }
  
  /**
   * Converts the properties into a spreadsheet (single row, with property 
   * names for column names).
   * 
   * @param props	the properties to convert
   * @return		the generated spreadsheet
   */
  protected SpreadSheet propertiesToSpreadSheet(Properties props) {
    SpreadSheet	result;
    Row		row;
    
    result = new SpreadSheet();
    
    // header
    row   = result.getHeaderRow();
    for (ValueDefinition val: m_Values)
      row.addCell(val.getName()).setContent(val.getName());
    
    // data
    row = result.addRow();
    for (ValueDefinition val: m_Values) {
      switch (val.getType()) {
	case INTEGER:
	  row.addCell(val.getName()).setContent(props.getInteger(val.getName()));
	  break;
	case DOUBLE:
	  row.addCell(val.getName()).setContent(props.getDouble(val.getName()));
	  break;
	case BOOLEAN:
	  row.addCell(val.getName()).setContent(props.getBoolean(val.getName()));
	  break;
	case TIME:
	  row.addCell(val.getName()).setContent(props.getTime(val.getName()));
	  break;
	case DATE:
	  row.addCell(val.getName()).setContent(props.getDate(val.getName()));
	  break;
	case DATETIME:
	  row.addCell(val.getName()).setContent(props.getDateTime(val.getName()));
	  break;
	default:
	  row.addCell(val.getName()).setContentAsString(props.getProperty(val.getName()));
	  break;
      }
    }
    
    return result;
  }
  
  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    Properties			props;
    ApprovalDialog		dialog;
    PropertiesParameterPanel	panel;
    JPanel			panelMsg;
    List<String>		order;

    if (m_NonInteractive) {
      m_OutputToken = new Token(propertiesToSpreadSheet(getDefaultProperties()));
      return true;
    }

    // show dialog
    panel = new PropertiesParameterPanel();
    panel.setButtonPanelVisible(true);
    order = new ArrayList<String>();
    for (ValueDefinition val: m_Values) {
      order.add(val.getName());
      panel.addPropertyType(val.getName(), val.getType());
      if (!val.getDisplay().trim().isEmpty())
        panel.setLabel(val.getName(), val.getDisplay());
    }
    panel.setPropertyOrder(order);
    panel.setProperties(getDefaultProperties());
    panelMsg = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelMsg.add(new JLabel(m_Message));
    dialog = new ApprovalDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dialog.setTitle(getName());
    dialog.getContentPane().add(panelMsg, BorderLayout.NORTH);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      props         = panel.getProperties();
      m_OutputToken = new Token(propertiesToSpreadSheet(props));
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    if (isHeadless()) {
      m_OutputToken = new Token(propertiesToSpreadSheet(getDefaultProperties()));
      return null;
    }
    else {
      return super.doExecute();
    }
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
