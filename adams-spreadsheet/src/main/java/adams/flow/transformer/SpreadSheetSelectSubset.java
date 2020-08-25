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
 * SpreadSheetSelectSubset.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.MouseUtils;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.TableRowRange;
import adams.gui.event.SearchEvent;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 <!-- globalinfo-start -->
 * Allows the user to select a subset of the incoming spreadsheet to be forwarded in the flow.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetSelectSubset
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetSelectSubset
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = -7861621358380784108L;

  /** the message for the user. */
  protected String m_Message;

  /** whether to allow searching. */
  protected boolean m_AllowSearch;

  /** the table in use. */
  protected SpreadSheetTable m_Table;

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
      "Allows the user to select a subset of the incoming spreadsheet to be "
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
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    m_Table.setModel(new SpreadSheetTableModel());
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel		result;
    JPanel		panelCenter;
    JPanel		panelMessage;
    JPanel 		panelButtons;
    final BaseButton 	buttonOK;
    final BaseButton	buttonCancel;
    SearchPanel		panelSearch;

    result = new BasePanel(new BorderLayout());

    panelCenter = new JPanel(new BorderLayout());
    result.add(panelCenter, BorderLayout.CENTER);

    m_Table = new SpreadSheetTable(new SpreadSheetTableModel());
    panelCenter.add(new BaseScrollPane(m_Table), BorderLayout.CENTER);

    m_LabelMessage = new JLabel();
    panelMessage = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelMessage.add(m_LabelMessage);
    panelCenter.add(panelMessage, BorderLayout.NORTH);

    if (m_AllowSearch) {
      panelSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
      panelSearch.addSearchListener((SearchEvent e) -> {
	m_Table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      });
      panelCenter.add(panelSearch, BorderLayout.SOUTH);
    }

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    result.add(panelButtons, BorderLayout.SOUTH);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener((ActionEvent e) -> {
      m_Accepted = true;
      m_Dialog.setVisible(false);
    });
    panelButtons.add(buttonOK);

    buttonCancel = new BaseButton("Cancel");
    buttonCancel.setMnemonic('C');
    buttonCancel.addActionListener((ActionEvent e) -> {
      m_Accepted = false;
      m_Dialog.setVisible(false);
    });
    panelButtons.add(buttonCancel);

    m_Table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isDoubleClick(e)) {
          if (m_Table.getSelectedRowCount() > 0)
            buttonOK.doClick();
        }
        if (!e.isConsumed())
          super.mouseClicked(e);
      }
    });

    return result;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    SpreadSheet	selected;

    m_LabelMessage.setText(getVariables().expand(m_Message));
    m_Table.setModel(new SpreadSheetTableModel((SpreadSheet) m_InputToken.getPayload()));

    registerWindow(m_Dialog, m_Dialog.getTitle());
    m_Accepted = false;
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);

    if (m_Accepted) {
      selected = m_Table.toSpreadSheet(TableRowRange.SELECTED);
      m_OutputToken = new Token(selected);
    }

    return m_Accepted;
  }
}
