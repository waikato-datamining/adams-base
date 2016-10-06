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
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.Token;
import adams.flow.source.valuedefinition.AbstractValueDefinition;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Pops up a dialog, prompting the user to enter one or more values.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: EnterManyValues
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
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with.
 * &nbsp;&nbsp;&nbsp;default: Please enter values
 * </pre>
 * 
 * <pre>-value &lt;adams.flow.source.valuedefinition.AbstractValueDefinition&gt; [-value ...] (property: values)
 * &nbsp;&nbsp;&nbsp;The value definitions that define the dialog prompting the user to enter 
 * &nbsp;&nbsp;&nbsp;the values.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-output-type &lt;SPREADSHEET|KEY_VALUE_PAIRS|KEY_VALUE_PAIRS_ARRAY|MAP&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;How to output the entered data.
 * &nbsp;&nbsp;&nbsp;default: SPREADSHEET
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

  /**
   * Defines how to output the data that the user entered.
   */
  public enum OutputType {
    SPREADSHEET,
    KEY_VALUE_PAIRS,
    KEY_VALUE_PAIRS_ARRAY,
    MAP
  }

  /** the message for the user. */
  protected String m_Message;

  /** the value definitions. */
  protected AbstractValueDefinition[] m_Values;

  /** how to output the data. */
  protected OutputType m_OutputType;

  /** whether to automate the actor. */
  protected boolean m_NonInteractive;
  
  /** the list of tokens to output. */
  protected List m_Queue;

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
	    new AbstractValueDefinition[0]);

    m_OptionManager.add(
	    "output-type", "outputType",
	    OutputType.SPREADSHEET);

    m_OptionManager.add(
	    "non-interactive", "nonInteractive",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Queue = new ArrayList();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Queue = new ArrayList();
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
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputType", m_OutputType));
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
  public void setValues(AbstractValueDefinition[] value) {
    m_Values = value;
    reset();
  }

  /**
   * Returns the value definitions.
   *
   * @return 		the definitions
   */
  public AbstractValueDefinition[] getValues() {
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
   * Sets how to output the entered data.
   *
   * @param value	the type
   */
  public void setOutputType(OutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns how to output the entered data.
   *
   * @return 		the type
   */
  public OutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return "How to output the entered data.";
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
   * Returns the class of objects that it generates.
   *
   * @return		the classes
   */
  public Class[] generates() {
    switch (m_OutputType) {
      case SPREADSHEET:
	return new Class[]{SpreadSheet.class};
      case KEY_VALUE_PAIRS:
	return new Class[]{String[].class};
      case KEY_VALUE_PAIRS_ARRAY:
	return new Class[]{String[][].class};
      case MAP:
	return new Class[]{Map.class};
      default:
	throw new IllegalStateException("Unhandled output type: " + m_OutputType);
    }
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
    for (AbstractValueDefinition val: m_Values)
      result.setProperty(val.getName(), getVariables().expand(val.getDefaultValueAsString()));
    
    return result;
  }
  
  /**
   * Converts the properties into a spreadsheet (single row, with property 
   * names for column names).
   * 
   * @param props	the properties to convert
   * @return		the generated spreadsheet in a token
   */
  protected SpreadSheet propertiesToSpreadSheet(Properties props) {
    SpreadSheet	result;
    Row		row;
    
    result = new DefaultSpreadSheet();
    
    // header
    row   = result.getHeaderRow();
    for (AbstractValueDefinition val: m_Values)
      row.addCell(val.getName()).setContent(val.getName());
    
    // data
    row = result.addRow();
    for (AbstractValueDefinition val: m_Values) {
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
   * Converts the properties into the requested output type.
   *
   * @param props	the properties to convert
   * @return		the generated output type
   */
  protected Token[] propertiesToOutputType(Properties props) {
    Token[]		result;
    SpreadSheet		sheet;
    String[]		pair;
    String[][]		pairArray;
    int			i;
    Map<String,Object>	map;

    switch (m_OutputType) {
      case SPREADSHEET:
	result = new Token[]{new Token(propertiesToSpreadSheet(props))};
	break;

      case KEY_VALUE_PAIRS:
	sheet  = propertiesToSpreadSheet(props);
	result = new Token[sheet.getColumnCount()];
	for (i = 0; i < sheet.getColumnCount(); i++) {
	  pair = new String[]{
	    sheet.getHeaderRow().getCell(i).getContent(),
	    sheet.getRow(0).getCell(i).getContent()};
	  result[i] = new Token(pair);
	}
	break;

      case KEY_VALUE_PAIRS_ARRAY:
	sheet     = propertiesToSpreadSheet(props);
	result    = new Token[1];
	pairArray = new String[sheet.getColumnCount()][2];
	for (i = 0; i < sheet.getColumnCount(); i++) {
	  pair = new String[]{
	    sheet.getHeaderRow().getCell(i).getContent(),
	    sheet.getRow(0).getCell(i).getContent()};
	  pairArray[i] = pair;
	}
	result[0] = new Token(pairArray);
	break;

      case MAP:
	sheet = propertiesToSpreadSheet(props);
	map   = new HashMap<>();
	for (i = 0; i < sheet.getColumnCount(); i++) {
	  map.put(
	    sheet.getHeaderRow().getCell(i).getContent(),
	    sheet.getRow(0).getCell(i).getNative());
	}
	result = new Token[]{new Token(map)};
	break;

      default:
	throw new IllegalStateException("Unhandled output type: " + m_OutputType);
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
    Boolean                     sync;
    String			msg;

    m_Queue.clear();

    if (m_NonInteractive) {
      m_Queue.addAll(Arrays.asList(propertiesToOutputType(getDefaultProperties())));
      return true;
    }

    // show dialog
    panel = new PropertiesParameterPanel();
    panel.setButtonPanelVisible(true);
    order = new ArrayList<>();
    for (AbstractValueDefinition val: m_Values) {
      order.add(val.getName());
      if (!val.addToPanel(panel)) {
	getLogger().severe("Failed to add value definition: " + val.toCommandLine());
	return false;
      }
    }
    panel.setPropertyOrder(order);
    panel.setProperties(getDefaultProperties());
    panelMsg = new JPanel(new FlowLayout(FlowLayout.LEFT));
    msg = m_Message;
    msg = getVariables().expand(msg);
    panelMsg.add(new JLabel(msg));
    dialog = new ApprovalDialog(null, ModalityType.MODELESS);
    dialog.setTitle(getName());
    dialog.getContentPane().add(panelMsg, BorderLayout.NORTH);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);

    sync = new Boolean(true);
    // wait till dialog visible
    while (!dialog.isVisible()) {
      try {
        synchronized (sync) {
          sync.wait(10);
        }
      }
      catch (Exception e) {
        // ignored
      }
    }
    // wait till dialog closed
    while (dialog.isVisible() && !isStopped()) {
      try {
        synchronized (sync) {
          sync.wait(100);
        }
      }
      catch (Exception e) {
        // ignored
      }
    }

    if (isStopped())
      dialog.setVisible(false);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      props = panel.getProperties();
      m_Queue.addAll(Arrays.asList(propertiesToOutputType(props)));
      return true;
    }
    else {
      return false;
    }
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
    boolean	result;
    String	value;
    Properties	props;

    if (m_NonInteractive) {
      m_Queue.addAll(Arrays.asList(propertiesToOutputType(getDefaultProperties())));
      return true;
    }

    result = true;
    props  = new Properties();
    for (AbstractValueDefinition valueDef: m_Values) {
      value = valueDef.headlessInteraction();
      if (value == null) {
	result = false;
	break;
      }
      props.setProperty(valueDef.getName(), value);
    }
    if (result)
      m_Queue.addAll(Arrays.asList(propertiesToOutputType(props)));

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result = null;
    if ((m_Queue != null) && !m_Queue.isEmpty())
      result = (Token) m_Queue.remove(0);

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_Queue.size() > 0);
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_Queue = null;
  }
}
