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
 * LogEntryDatabaseViewerPanel.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.db.AbstractConditions;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.db.LogEntry;
import adams.db.LogEntryConditions;
import adams.db.LogT;
import adams.env.Environment;
import adams.env.LogEntryDBViewerPanelDefinition;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;
import adams.gui.core.PropertiesTableModel;
import adams.gui.dialog.TextDialog;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * For viewing LogEntry records stored in the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogEntryDatabaseViewerPanel
  extends AbstractLogEntryViewerPanel
  implements DatabaseConnectionHandler, DatabaseConnectionChangeListener, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5878423449289564310L;

  /** the name of the props file. */
  public final static String FILENAME = "LogEntryDatabaseViewer.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the button for displaying the options. */
  protected JButton m_ButtonOptions;

  /** the button for refreshing the entries. */
  protected JButton m_ButtonRefresh;

  /** the button for deleting the entries. */
  protected JButton m_ButtonDelete;

  /** the conditions currently in use. */
  protected LogEntryConditions m_Conditions;

  /** the GOE dialog for the conditions. */
  protected GenericObjectEditorDialog m_OptionsDialog;

  /** the database connection. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Properties	props;

    super.initialize();

    props = getProperties();

    m_Conditions = (LogEntryConditions) LogEntryConditions.forCommandLine(props.getProperty("LogEntryConditions"));
    if (m_Conditions == null)
      m_Conditions = new LogEntryConditions();
    m_DatabaseConnection = DatabaseConnection.getSingleton();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties	props;

    super.initGUI();

    props = getProperties();

    m_ButtonOptions = new JButton("Options");
    m_ButtonOptions.setMnemonic('O');
    m_ButtonOptions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	showOptions();
      }
    });

    m_ButtonRefresh = new JButton("Refresh");
    m_ButtonRefresh.setMnemonic('R');
    m_ButtonRefresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	refresh();
      }
    });

    m_ButtonDelete = new JButton("Delete");
    m_ButtonDelete.setMnemonic('D');
    m_ButtonDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	delete();
      }
    });

    m_TableEntries.addToButtonsPanel(m_ButtonOptions);
    m_TableEntries.addToButtonsPanel(m_ButtonRefresh);
    m_TableEntries.addToButtonsPanel(new JLabel(""));
    m_TableEntries.addToButtonsPanel(m_ButtonDelete);

    m_SplitPane.setDividerLocation(props.getInteger("DividerLocation", 250));
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    DatabaseConnection.getSingleton().addChangeListener(this);

    update();
  }

  /**
   * Initializes the details dialog.
   *
   * @return		the dialog
   */
  @Override
  protected TextDialog createDetailsDialog() {
    TextDialog	result;
    Properties	props;

    result = super.createDetailsDialog();

    props  = getProperties();
    result.setSize(
	  props.getInteger("DetailsDialog.Width", 400),
	  props.getInteger("DetailsDialog.Height", 300));

    return result;
  }

  /**
   * Displays the options.
   */
  protected void showOptions() {
    if (m_OptionsDialog == null) {
      if (getParentDialog() != null)
	m_OptionsDialog = GenericObjectEditorDialog.createDialog(getParentDialog());
      else
	m_OptionsDialog = GenericObjectEditorDialog.createDialog(getParentFrame());
      m_OptionsDialog.getGOEEditor().setCanChangeClassInDialog(false);
      m_OptionsDialog.getGOEEditor().setClassType(AbstractConditions.class);
    }

    m_OptionsDialog.setCurrent(m_Conditions);
    m_OptionsDialog.setLocationRelativeTo(this);
    m_OptionsDialog.setVisible(true);
    if (m_OptionsDialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    m_Conditions = (LogEntryConditions) m_OptionsDialog.getCurrent();
  }

  /**
   * Refreshes the log entries.
   */
  protected void refresh() {
    Runnable	run;

    run = new Runnable() {
      public void run() {
	setEnabled(false);
	List<LogEntry> entries = LogT.getSingleton(getDatabaseConnection()).load(m_Conditions);
	m_TableModelEntries.clear();
	m_TableModelEntries.addAll(entries);
	m_TableEntries.setOptimalColumnWidth();
	m_TableMessage.setModel(new PropertiesTableModel());
	setEnabled(true);
	update();
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Removes the currently selected log entries.
   */
  protected void delete() {
    SwingWorker		worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	setEnabled(false);
	int[] sel = m_TableEntries.getSelectedRows();
	LogEntry[] entries = new LogEntry[sel.length];
	for (int i = 0; i < sel.length; i++)
	  entries[i] = m_TableModelEntries.getLogEntryAt(sel[i]);
	for (int i = 0; i < entries.length; i++)
	  LogT.getSingleton(getDatabaseConnection()).remove(entries[i]);
        return null;
      }
      @Override
      protected void done() {
	setEnabled(true);
        super.done();
	refresh();
      }
    };
    worker.execute();
  }

  /**
   * Updates the state of the buttons.
   */
  @Override
  protected void updateButtons() {
    boolean	connected;

    super.updateButtons();

    connected = DatabaseConnection.getSingleton().isConnected();

    m_ButtonOptions.setEnabled(connected);
    m_ButtonRefresh.setEnabled(connected);
    m_ButtonDelete.setEnabled((m_TableEntries.getSelectedRowCount() > 0) && connected);
  }

  /**
   * Sets the enabled state of the panel.
   *
   * @param value	if true then the panel gets enabled
   */
  @Override
  public void setEnabled(boolean value) {
    boolean	connected;

    connected = DatabaseConnection.getSingleton().isConnected();

    m_ButtonOptions.setEnabled(value && connected);
    m_ButtonRefresh.setEnabled(value && connected);
    m_ButtonDelete.setEnabled(value && connected);

    super.setEnabled(value);
  }

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    if (e.getType() == EventType.CONNECT)
      setDatabaseConnection(e.getDatabaseConnection());
    updateButtons();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_DatabaseConnection.removeChangeListener(this);
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DatabaseConnection.removeChangeListener(this);
    m_DatabaseConnection = value;
    m_DatabaseConnection.addChangeListener(this);
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(LogEntryDBViewerPanelDefinition.KEY);

    return m_Properties;
  }
}
