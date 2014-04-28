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
 * LogViewer.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.QuickInfoHelper;
import adams.core.io.LogEntryWriter;
import adams.db.LogEntry;
import adams.flow.core.Token;
import adams.flow.sink.logview.AbstractLogEntryDialog;
import adams.flow.sink.logview.DefaultLogEntryDialog;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.tools.LogEntryViewerTableModel;

/**
 <!-- globalinfo-start -->
 * Actor that displays LogEntry objects, but doesn't store them in the database.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.db.LogEntry<br/>
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
 * &nbsp;&nbsp;&nbsp;default: LogViewer
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
 * <pre>-short-title (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
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
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-dialog &lt;adams.flow.sink.logview.AbstractLogEntryDialog&gt; (property: dialog)
 * &nbsp;&nbsp;&nbsp;The dialog to use for displaying the log entries.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.logview.DefaultLogEntryDialog
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogViewer
  extends AbstractDisplay {

  /** for serialization. */
  private static final long serialVersionUID = -1980631598893105134L;

  /** the table with the entries. */
  protected BaseTableWithButtons m_TableEntries;

  /** the button for displaying the message details. */
  protected JButton m_ButtonDisplay;

  /** the button for saving the selected entries (or all if none selected). */
  protected JButton m_ButtonSave;

  /** the dialog for displaying the message details. */
  protected AbstractLogEntryDialog m_Dialog;

  /** the base file chooser for saving the log entries. */
  protected transient BaseFileChooser m_BaseFileChooser;

  /** the dialogs displayed so far. */
  protected ArrayList<BaseDialog> m_Dialogs;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor that displays LogEntry objects, but doesn't store them in the database.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "dialog", "dialog",
	    new DefaultLogEntryDialog());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Dialogs = new ArrayList<BaseDialog>();
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return super.getQuickInfo() + QuickInfoHelper.toString(this, "dialog", m_Dialog, ", dialog: ");
  }

  /**
   * Sets the dialog to use.
   *
   * @param value 	the dialog
   */
  public void setDialog(AbstractLogEntryDialog value) {
    m_Dialog = value;
    reset();
  }

  /**
   * Returns the dialog to use.
   *
   * @return 		the dialog
   */
  public AbstractLogEntryDialog getDialog() {
    return m_Dialog;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dialogTipText() {
    return "The dialog to use for displaying the log entries.";
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
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.db.LogEntry.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{LogEntry.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_TableEntries != null)
      ((LogEntryViewerTableModel) m_TableEntries.getModel()).clear();
  }

  /**
   * Displays the message of the given entry in a separate window.
   *
   * @param entry	the entry to display
   */
  protected void displayMessage(LogEntry entry) {
    BaseDialog	dialog;
    try {
      dialog = m_Dialog.create(this, entry);
      m_Dialogs.add(dialog);
      dialog.setLocationRelativeTo(m_TableEntries);
      dialog.setVisible(true);
    }
    catch (Exception e) {
      handleException("Failed to display log entry!", e);
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;

    result = new BasePanel(new BorderLayout());
    result.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    m_TableEntries = new BaseTableWithButtons(new LogEntryViewerTableModel());
    m_TableEntries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_TableEntries.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_TableEntries.setInfoVisible(true);
    m_TableEntries.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	m_ButtonDisplay.setEnabled(m_TableEntries.getSelectedRowCount() == 1);
      }
    });
    result.add(m_TableEntries, BorderLayout.CENTER);

    m_ButtonDisplay = new JButton("Display...");
    m_ButtonDisplay.setMnemonic('D');
    m_ButtonDisplay.setEnabled(false);
    m_ButtonDisplay.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	int row = m_TableEntries.getSelectedRow();
	LogEntryViewerTableModel model = (LogEntryViewerTableModel) m_TableEntries.getModel();
	LogEntry entry = model.getLogEntryAt(row);
	displayMessage(entry);
      }
    });
    m_TableEntries.addToButtonsPanel(m_ButtonDisplay);
    m_TableEntries.setDoubleClickButton(m_ButtonDisplay);

    m_ButtonSave = new JButton("Save...");
    m_ButtonSave.setMnemonic('S');
    m_ButtonSave.setEnabled(true);
    m_ButtonSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	int retVal = getBaseFileChooser().showSaveDialog(m_TableEntries);
	if (retVal != BaseFileChooser.APPROVE_OPTION)
	  return;
	String filename = getBaseFileChooser().getSelectedFile().getAbsolutePath();
	LogEntryWriter.rewrite(filename);
	int[] rows = m_TableEntries.getSelectedRows();
	LogEntryViewerTableModel model = (LogEntryViewerTableModel) m_TableEntries.getModel();
	if (rows.length == 0) {
	  for (int i = 0; i < m_TableEntries.getRowCount(); i++)
	    LogEntryWriter.write(filename, model.getLogEntryAt(i));
	}
	else {
	  for (int i = 0; i < rows.length; i++)
	    LogEntryWriter.write(filename, model.getLogEntryAt(rows[i]));
	}
      }
    });
    m_TableEntries.addToButtonsPanel(m_ButtonSave);

    m_TableEntries.setOptimalColumnWidth();

    return result;
  }

  /**
   * Returns (and initializes if necessary) the file chooser.
   * 
   * @return		the file chooser
   */
  protected BaseFileChooser getBaseFileChooser() {
    BaseFileChooser 	fileChooser;
    ExtensionFileFilter	filter;
    
    if (m_BaseFileChooser == null) {
      fileChooser = new BaseFileChooser();
      fileChooser.addChoosableFileFilter(ExtensionFileFilter.getCsvFileFilter());
      filter = ExtensionFileFilter.getLogFileFilter();
      fileChooser.addChoosableFileFilter(filter);
      fileChooser.setDefaultExtension(filter.getExtensions()[0]);
      fileChooser.setAutoAppendExtension(true);
      fileChooser.setCurrentDirectory(new File("."));
      fileChooser.setSelectedFile(new File(getName() + "." + fileChooser.getDefaultExtension()));
    }
    
    return m_BaseFileChooser;
  }
  
  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    LogEntry	entry;

    entry = (LogEntry) token.getPayload();
    ((LogEntryViewerTableModel) m_TableEntries.getModel()).add(entry, true);
    m_TableEntries.setOptimalColumnWidth();
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    while (m_Dialogs.size() > 0) {
      m_Dialogs.get(0).setVisible(false);
      m_Dialogs.get(0).dispose();
      m_Dialogs.remove(0);
    }

    super.cleanUpGUI();
  }
}
