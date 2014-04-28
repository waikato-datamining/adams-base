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
 * LogEntryViewer.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.Properties;
import adams.db.LogEntry;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.PropertiesTableModel;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.dialog.TextDialog;

/**
 * For viewing LogEntry records.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractLogEntryViewerPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -3659016045308129874L;

  /** the table displaying the LogEntry objects. */
  protected BaseTableWithButtons m_TableEntries;

  /** the model containing the LogEntry objects. */
  protected LogEntryViewerTableModel m_TableModelEntries;

  /** the table for displaying the properties (message). */
  protected BaseTableWithButtons m_TableMessage;

  /** the button for displaying a key-value pair of the message. */
  protected JButton m_ButtonDisplay;

  /** the split pane for entries/message details. */
  protected BaseSplitPane m_SplitPane;

  /** the dialog for displaying entries from the message. */
  protected TextDialog m_DetailsDialog;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // entries
    m_TableModelEntries = new LogEntryViewerTableModel();
    m_TableEntries = new BaseTableWithButtons(m_TableModelEntries);
    m_TableEntries.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_TableEntries.setInfoVisible(true);
    m_TableEntries.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	update();
	if (m_TableEntries.getSelectedRowCount() >= 1) {
	  int[] indices = m_TableEntries.getSelectedRows();
	  LogEntry entry = m_TableModelEntries.getLogEntryAt(indices[0]);
	  displayMessage(entry);
	}
      }
    });

    // message
    m_TableMessage = new BaseTableWithButtons(new PropertiesTableModel());
    m_TableMessage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_TableMessage.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    m_TableMessage.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateButtons();
      }
    });

    m_ButtonDisplay = new JButton("Display");
    m_ButtonDisplay.setMnemonic('i');
    m_ButtonDisplay.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	displayKeyValue();
      }
    });

    m_TableMessage.addToButtonsPanel(m_ButtonDisplay);
    m_TableMessage.setDoubleClickButton(m_ButtonDisplay);

    // split pane
    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setTopComponent(m_TableEntries);
    m_SplitPane.setBottomComponent(m_TableMessage);
    add(m_SplitPane, BorderLayout.CENTER);
  }

  /**
   * finishes the initialization.
   */
  protected void finishInit() {
    super.finishInit();

    update();
  }

  /**
   * Displays the message of the chosen entry.
   *
   * @param entry	the entry to display
   */
  protected void displayMessage(final LogEntry entry) {
    Runnable	run;

    run = new Runnable() {
      public void run() {
	Properties props = entry.getMessageAsProperties();
	PropertiesTableModel model = new PropertiesTableModel(props);
	m_TableMessage.setModel(model);
	m_TableMessage.setOptimalColumnWidth();
	update();
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Initializes the details dialog.
   *
   * @return		the dialog
   */
  protected TextDialog createDetailsDialog() {
    TextDialog	result;

    if (getParentDialog() != null)
      result = new TextDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      result = new TextDialog(getParentFrame(), true);

    return result;
  }

  /**
   * Displays the currently selected key-value pair of the message in a dialog.
   */
  protected void displayKeyValue() {
    int				row;
    PropertiesTableModel	model;

    if (m_DetailsDialog == null)
      m_DetailsDialog = createDetailsDialog();

    row = m_TableMessage.getSelectedRow();
    if (row == -1)
      return;

    model = (PropertiesTableModel) m_TableMessage.getModel();
    m_DetailsDialog.setTitle(model.getKeyAt(row));
    m_DetailsDialog.setContent((String) model.getValueAt(row));
    m_DetailsDialog.setLocationRelativeTo(this);
    m_DetailsDialog.setVisible(true);
  }

  /**
   * Updates buttons etc.
   */
  protected void update() {
    updateButtons();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonDisplay.setEnabled(m_TableMessage.getSelectedRowCount() > 0);
  }

  /**
   * Sets the enabled state of the panel.
   *
   * @param value	if true then the panel gets enabled
   */
  public void setEnabled(boolean value) {
    super.setEnabled(value);

    m_TableEntries.setEnabled(value);
    m_TableMessage.setEnabled(value);

    update();
  }
}
