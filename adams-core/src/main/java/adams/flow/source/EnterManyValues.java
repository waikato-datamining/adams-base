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
 * Copyright (C) 2013-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.core.UniqueIDs;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.InteractionDisplayLocation;
import adams.flow.core.InteractionDisplayLocationHelper;
import adams.flow.core.InteractionDisplayLocationSupporter;
import adams.flow.core.RestorableActor;
import adams.flow.core.RestorableActorHelper;
import adams.flow.core.Token;
import adams.flow.source.valuedefinition.AbstractValueDefinition;
import adams.gui.core.GUIHelper;
import adams.gui.core.GUIHelper.DialogCommunication;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-use-outer-window &lt;boolean&gt; (property: useOuterWindow)
 * &nbsp;&nbsp;&nbsp;If enabled, the outer window (dialog&#47;frame) is used instead of the component
 * &nbsp;&nbsp;&nbsp;of the callable actor.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with; variables get automatically expanded.
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
 * <pre>-display-location &lt;DIALOG|NOTIFICATION_AREA&gt; (property: displayLocation)
 * &nbsp;&nbsp;&nbsp;Determines where the interaction is being displayed.
 * &nbsp;&nbsp;&nbsp;default: DIALOG
 * </pre>
 *
 * <pre>-non-interactive &lt;boolean&gt; (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, the initial value is forwarded without user interaction.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-restoration-enabled &lt;boolean&gt; (property: restorationEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, the state of the actor is being preserved and attempted to read
 * &nbsp;&nbsp;&nbsp;in again next time this actor is executed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-restoration-file &lt;adams.core.io.PlaceholderFile&gt; (property: restorationFile)
 * &nbsp;&nbsp;&nbsp;The file to store the restoration information in.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width for the dialog, -1 for default.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class EnterManyValues
  extends AbstractInteractiveSource
  implements AutomatableInteractiveActor, RestorableActor, InteractionDisplayLocationSupporter {

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

  /** whether restoration is enabled. */
  protected boolean m_RestorationEnabled;

  /** the file to store the restoration state in. */
  protected PlaceholderFile m_RestorationFile;

  /** the width of the dialog. */
  protected int m_Width;

  /** where to display the prompt. */
  protected InteractionDisplayLocation m_DisplayLocation;

  /** the list of tokens to output. */
  protected List m_Queue;

  /** for communicating with the input dialog. */
  protected GUIHelper.DialogCommunication m_Comm;

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
      "display-location", "displayLocation",
      InteractionDisplayLocation.DIALOG);

    m_OptionManager.add(
      "non-interactive", "nonInteractive",
      false);

    m_OptionManager.add(
      "restoration-enabled", "restorationEnabled",
      false);

    m_OptionManager.add(
      "restoration-file", "restorationFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "width", "width",
      -1, -1, null);
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
    List<String>	names;
    String		namesStr;

    result = QuickInfoHelper.toString(this, "message", m_Message);

    names = new ArrayList<>();
    for (AbstractValueDefinition def: m_Values) {
      if (def.getEnabled())
	names.add(def.getName());
    }
    namesStr = QuickInfoHelper.toString(this, "values", (names.size() > 0 ? Utils.flatten(names, "|") : "-none-"), ", ");
    if (namesStr != null)
      result += namesStr;

    options = new ArrayList<>();
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
    return "The message to prompt the user with; variables get automatically expanded.";
  }

  /**
   * Adds a single value definition at the end.
   *
   * @param value 	the definition to add
   */
  public void addValue(AbstractValueDefinition value) {
    setValues((AbstractValueDefinition[]) Utils.adjustArray(m_Values, m_Values.length + 1, value));
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
   * Sets where the interaction is being displayed.
   *
   * @param value	the location
   */
  @Override
  public void setDisplayLocation(InteractionDisplayLocation value) {
    m_DisplayLocation = value;
    reset();
  }

  /**
   * Returns where the interaction is being displayed.
   *
   * @return 		the location
   */
  @Override
  public InteractionDisplayLocation getDisplayLocation() {
    return m_DisplayLocation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  @Override
  public String displayLocationTipText() {
    return "Determines where the interaction is being displayed.";
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
   * Sets whether to enable restoration.
   *
   * @param value	true if to enable restoration
   */
  @Override
  public void setRestorationEnabled(boolean value) {
    m_RestorationEnabled = value;
    reset();
  }

  /**
   * Returns whether restoration is enabled.
   *
   * @return		true if restoration enabled
   */
  @Override
  public boolean isRestorationEnabled() {
    return m_RestorationEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String restorationEnabledTipText() {
    return "If enabled, the state of the actor is being preserved and attempted to read in again next time this actor is executed.";
  }

  /**
   * Sets the file for storing the state.
   *
   * @param value	the file
   */
  @Override
  public void setRestorationFile(PlaceholderFile value) {
    m_RestorationFile = value;
    reset();
  }

  /**
   * Returns the file for storing the state.
   *
   * @return		the file
   */
  @Override
  public PlaceholderFile getRestorationFile() {
    return m_RestorationFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String restorationFileTipText() {
    return "The file to store the restoration information in.";
  }

  /**
   * Sets the width for the dialog.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width for the dialog.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width for the dialog, -1 for default.";
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
    for (AbstractValueDefinition val: m_Values) {
      if (val.getEnabled())
	result.setProperty(val.getName(), getVariables().expand(val.getDefaultValueAsString()));
    }

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
    for (AbstractValueDefinition val: m_Values) {
      if (val.getEnabled())
	row.addCell(val.getName()).setContent(val.getName());
    }

    // data
    row = result.addRow();
    for (AbstractValueDefinition val: m_Values) {
      if (!val.getEnabled())
	continue;
      switch (val.getType()) {
	case INTEGER:
	  row.addCell(val.getName()).setContent(props.getInteger(val.getName()));
	  break;
	case LONG:
	  row.addCell(val.getName()).setContent(props.getLong(val.getName()));
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
	  if (sheet.getRow(0).getCell(i).isAnyDateType()) {
	    map.put(
	      sheet.getHeaderRow().getCell(i).getContent(),
	      sheet.getRow(0).getCell(i).getContent());
	  }
	  else {
	    map.put(
	      sheet.getHeaderRow().getCell(i).getContent(),
	      sheet.getRow(0).getCell(i).getNative());
	  }
	}
	result = new Token[]{new Token(map)};
	break;

      default:
	throw new IllegalStateException("Unhandled output type: " + m_OutputType);
    }

    return result;
  }

  /**
   * Performs the interaction using a dialog.
   *
   * @param panel	the panel with the parameters
   * @param panelMsg 	the panel with the message for the user
   * @return		null if successfully interacted, otherwise error message
   */
  protected String doInteractInDialog(PropertiesParameterPanel panel, JPanel panelMsg) {
    ApprovalDialog	dialog;
    Long                sync;

    dialog = new ApprovalDialog(null, ModalityType.MODELESS);
    dialog.setTitle(getName());
    registerWindow(dialog, dialog.getTitle());
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.getContentPane().add(panelMsg, BorderLayout.NORTH);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    if (m_Width != -1)
      dialog.setSize(new Dimension(m_Width, dialog.getPreferredSize().height));
    dialog.setLocationRelativeTo(getActualParentComponent());
    dialog.setVisible(true);

    sync = UniqueIDs.nextLong();
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
    deregisterWindow(dialog);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION)
      return null;
    else
      return INTERACTION_CANCELED;
  }

  /**
   * Performs the interaction using the notification area.
   *
   * @param panel	the panel with the parameters
   * @param panelMsg 	the panel with the message for the user
   * @return		null if successfully interacted, otherwise error message
   */
  protected String doInteractInNotificationArea(PropertiesParameterPanel panel, JPanel panelMsg) {
    JPanel		panelAll;
    Boolean		result;

    panelAll = new JPanel(new BorderLayout());
    panelAll.add(panelMsg, BorderLayout.NORTH);
    panelAll.add(panel, BorderLayout.CENTER);
    panelAll.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

    m_Comm = new DialogCommunication();
    result = InteractionDisplayLocationHelper.display(this, m_Comm, panelAll, FlowLayout.LEFT);
    m_Comm = null;
    if ((result == null) || !result)
      return INTERACTION_CANCELED;
    else
      return null;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		null if successfully interacted, otherwise error message
   */
  @Override
  public String doInteract() {
    String			result;
    Properties			props;
    PropertiesParameterPanel	panel;
    JPanel			panelMsg;
    String			msg;
    List<String>		order;

    m_Queue.clear();

    if (m_RestorationEnabled && RestorableActorHelper.canRead(m_RestorationFile)) {
      props = getDefaultProperties();
      msg   = RestorableActorHelper.read(m_RestorationFile, props);
      if (msg != null) {
	getLogger().warning(msg);
	props = getDefaultProperties();
      }
    }
    else {
      props = getDefaultProperties();
    }

    if (m_NonInteractive) {
      m_Queue.addAll(Arrays.asList(propertiesToOutputType(props)));
      return null;
    }

    for (AbstractValueDefinition value: m_Values)
      value.setFlowContext(this);

    // assemble panel
    panel = new PropertiesParameterPanel();
    panel.setButtonPanelVisible(true);
    order = new ArrayList<>();
    for (AbstractValueDefinition val: m_Values) {
      if (!val.getEnabled())
	continue;
      order.add(val.getName());
      if (!val.addToPanel(panel)) {
	getLogger().severe("Failed to add value definition: " + val.toCommandLine());
	return "Failed to add value definition: " + val.toCommandLine();
      }
    }
    panel.setPropertyOrder(order);
    panel.setProperties(props);

    panelMsg = new JPanel(new FlowLayout(FlowLayout.LEFT));
    msg = m_Message;
    msg = getVariables().expand(msg);
    panelMsg.add(new JLabel(msg));


    // display panel
    switch (m_DisplayLocation) {
      case DIALOG:
	result = doInteractInDialog(panel, panelMsg);
	break;
      case NOTIFICATION_AREA:
	result = doInteractInNotificationArea(panel, panelMsg);
	break;
      default:
	throw new IllegalStateException("Unsupported display location: " + m_DisplayLocation);
    }

    // if accepted, obtain parameters
    if (result == null) {
      props = panel.getProperties();
      m_Queue.addAll(Arrays.asList(propertiesToOutputType(props)));
      if (m_RestorationEnabled) {
	props = panel.getProperties();
	for (AbstractValueDefinition val: m_Values) {
	  if (!val.canBeRestored() || !val.getEnabled())
	    props.removeKey(val.getName());
	}
	msg = RestorableActorHelper.write(props, m_RestorationFile);
	if (msg != null)
	  getLogger().warning(msg);
      }
    }

    return result;
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
   * @return		null if successfully interacted, otherwise error message
   */
  public String doInteractHeadless() {
    String	result;
    String	value;
    Properties	props;
    String	msg;

    props = getDefaultProperties();
    if (m_RestorationEnabled && RestorableActorHelper.canRead(m_RestorationFile)) {
      msg = RestorableActorHelper.read(m_RestorationFile, props);
      if (msg != null) {
	getLogger().warning(msg);
	props = new Properties();
      }
    }

    if (m_NonInteractive) {
      m_Queue.addAll(Arrays.asList(propertiesToOutputType(props)));
      return null;
    }

    result = null;
    for (AbstractValueDefinition valueDef: m_Values) {
      if (!valueDef.getEnabled())
	continue;
      if (props.hasKey(valueDef.getName()))
	valueDef.setDefaultValueAsString(props.getProperty(valueDef.getName()));
      value = valueDef.headlessInteraction();
      if (value == null) {
	result = "Value definition cannot be run in headless mode: " + valueDef;
	break;
      }
      props.setProperty(valueDef.getName(), value);
    }
    if (result == null) {
      m_Queue.addAll(Arrays.asList(propertiesToOutputType(props)));
      if (m_RestorationEnabled) {
	msg = RestorableActorHelper.write(props, m_RestorationFile);
	if (msg != null)
	  getLogger().warning(msg);
      }
    }

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

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_Queue = null;
  }
}
