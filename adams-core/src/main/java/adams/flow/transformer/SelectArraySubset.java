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
 * SelectArraySubset.java
 * Copyright (C) 2016-2022 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.SearchableBaseListWithButtons;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 * Allows the user to select a subset of the incoming array to be forwarded in the flow.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: SelectArraySubset
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
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 400
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
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
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with; variables get expanded prior to prompting
 * &nbsp;&nbsp;&nbsp;user.
 * &nbsp;&nbsp;&nbsp;default: Please make your selection
 * </pre>
 *
 * <pre>-allow-search &lt;boolean&gt; (property: allowSearch)
 * &nbsp;&nbsp;&nbsp;Whether to allow the user to search the list.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-initial-selection &lt;adams.core.Range&gt; (property: initialSelection)
 * &nbsp;&nbsp;&nbsp;Defines the initial selection of the array elements.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-initial-search &lt;java.lang.String&gt; (property: initialSearch)
 * &nbsp;&nbsp;&nbsp;The initial search string to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SelectArraySubset
    extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = -7861621358380784108L;

  /** the message for the user. */
  protected String m_Message;

  /** whether to allow searching. */
  protected boolean m_AllowSearch;

  /** the initial selection. */
  protected Range m_InitialSelection;

  /** the initial search string. */
  protected String m_InitialSearch;

  /** the list model to use. */
  protected DefaultListModel<Object> m_ListModel;

  /** the list in use. */
  protected SearchableBaseListWithButtons m_List;

  /** the label for the message. */
  protected JLabel m_LabelMessage;

  /** whether the data was accepted. */
  protected boolean m_Accepted;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Allows the user to select a subset of the incoming array to be "
            + "forwarded in the flow.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "message", "message",
        "Please make your selection");

    m_OptionManager.add(
        "allow-search", "allowSearch",
        false);

    m_OptionManager.add(
        "initial-selection", "initialSelection",
        new Range());

    m_OptionManager.add(
        "initial-search", "initialSearch",
        "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "message", m_Message, ", message: ");
    result += QuickInfoHelper.toString(this, "allowSearch", (m_AllowSearch ? "searchable" : "not searchable"), ", ");
    result += QuickInfoHelper.toString(this, "initialSelection", (m_InitialSelection.isEmpty() ? "-none-" : m_InitialSelection.getRange()), ", selection: ");
    if (m_AllowSearch)
      result += QuickInfoHelper.toString(this, "initialSearch", (m_InitialSearch.isEmpty() ? "-none-" : m_InitialSearch), ", search: ");

    return result;
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 600;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 400;
  }

  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X position
   */
  @Override
  protected int getDefaultX() {
    return -2;
  }

  /**
   * Returns the default Y position for the dialog.
   *
   * @return		the default Y position
   */
  @Override
  protected int getDefaultY() {
    return -2;
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
   * Sets whether to allow the user to search the table.
   *
   * @param value 	true if to allow search
   */
  public void setAllowSearch(boolean value) {
    m_AllowSearch = value;
    reset();
  }

  /**
   * Returns whether to allow the user to search the table.
   *
   * @return 		true if to allow search
   */
  public boolean getAllowSearch() {
    return m_AllowSearch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowSearchTipText() {
    return "Whether to allow the user to search the list.";
  }

  /**
   * Sets what elements get selected initially.
   *
   * @param value	the initial selection
   */
  public void setInitialSelection(Range value) {
    m_InitialSelection = value;
    reset();
  }

  /**
   * Returns what elements get selected initially.
   *
   * @return 		the initial selection
   */
  public Range getInitialSelection() {
    return m_InitialSelection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String initialSelectionTipText() {
    return "Defines the initial selection of the array elements.";
  }

  /**
   * Sets the initial search string to use.
   *
   * @param value	the initial search
   */
  public void setInitialSearch(String value) {
    m_InitialSearch = value;
    reset();
  }

  /**
   * Returns the initial search string in use.
   *
   * @return 		the initial search
   */
  public String getInitialSearch() {
    return m_InitialSearch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String initialSearchTipText() {
    return "The initial search string to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    m_ListModel.clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel		result;
    JPanel		panel;
    JPanel		panelButtons;
    final BaseButton 	buttonAll;
    final BaseButton 	buttonNone;
    final BaseButton 	buttonInvert;
    final BaseButton 	buttonOK;
    final BaseButton	buttonCancel;

    result = new BasePanel(new BorderLayout());

    m_ListModel = new DefaultListModel<>();
    m_List      = new SearchableBaseListWithButtons(m_ListModel);
    m_List.setAllowSearch(m_AllowSearch);
    result.add(m_List, BorderLayout.CENTER);

    m_LabelMessage = new JLabel();
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_LabelMessage);
    result.add(panel, BorderLayout.NORTH);

    panelButtons = new JPanel(new BorderLayout());
    result.add(panelButtons, BorderLayout.SOUTH);

    // buttons left
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelButtons.add(panel, BorderLayout.WEST);

    buttonAll = new BaseButton("All");
    buttonAll.setMnemonic('l');
    buttonAll.addActionListener((ActionEvent e) -> m_List.selectAll());
    panel.add(buttonAll);

    buttonNone = new BaseButton("None");
    buttonNone.setMnemonic('N');
    buttonNone.addActionListener((ActionEvent e) -> m_List.selectNone());
    panel.add(buttonNone);

    buttonInvert = new BaseButton("Invert");
    buttonInvert.setMnemonic('I');
    buttonInvert.addActionListener((ActionEvent e) -> m_List.invertSelection());
    panel.add(buttonInvert);

    // buttons right
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.EAST);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener((ActionEvent e) -> {
      m_Accepted = true;
      m_Dialog.setVisible(false);
    });
    panel.add(buttonOK);
    m_List.setDoubleClickButton(buttonOK);

    buttonCancel = new BaseButton("Cancel");
    buttonCancel.setMnemonic('C');
    buttonCancel.addActionListener((ActionEvent e) -> {
      m_Accepted = false;
      m_Dialog.setVisible(false);
    });
    panel.add(buttonCancel);

    return result;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    Object	array;
    int[]	indices;
    int		i;

    m_LabelMessage.setText(getVariables().expand(m_Message));
    m_ListModel.clear();
    array = m_InputToken.getPayload();
    for (i = 0; i < Array.getLength(array); i++)
      m_ListModel.addElement(Array.get(array, i));
    m_List.setModel(m_ListModel);

    // initial search
    if (m_AllowSearch)
      m_List.search(m_InitialSearch);

    // initial selection
    if (m_InitialSelection.isEmpty()) {
      m_List.selectNone();
    }
    else {
      m_InitialSelection.setMax(m_ListModel.getSize());
      m_List.setSelectedIndices(m_InitialSelection.getIntIndices());
    }

    registerWindow(m_Dialog, m_Dialog.getTitle());
    m_Accepted = false;
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);

    if (m_Accepted) {
      indices = m_List.getSelectedIndices();
      array   = Array.newInstance(m_InputToken.getPayload().getClass().getComponentType(), indices.length);
      for (i = 0; i < indices.length; i++)
        Array.set(array, i, m_ListModel.get(m_List.getActualIndex(indices[i])));
      m_OutputToken = new Token(array);
    }

    return m_Accepted;
  }
}
