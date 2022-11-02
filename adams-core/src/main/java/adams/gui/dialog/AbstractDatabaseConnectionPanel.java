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
 * AbstractDatabaseConnectionPanel.java
 * Copyright (C) 2011-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import adams.core.Constants;
import adams.core.StatusMessageHandler;
import adams.core.logging.LoggingLevel;
import adams.db.AbstractDatabaseConnection;
import adams.db.ConnectionParameters;
import adams.db.DatabaseConnectionProvider;
import adams.db.DatabaseManager;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;


/**
 * A panel for connecting to a database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDatabaseConnectionPanel
  extends BasePanel
  implements StatusMessageHandler, Comparable<AbstractDatabaseConnectionPanel>,
             DatabaseConnectionProvider {

  /** for serialization. */
  private static final long serialVersionUID = -8207475445903090661L;

  /** the panel itself. */
  protected AbstractDatabaseConnectionPanel m_Self;

  /** the combobox with the available connections. */
  protected BaseComboBox<ConnectionParameters> m_ComboBoxConnections;

  /** the edit field for the database URL. */
  protected BaseTextField m_TextURL;

  /** the edit field for the database user. */
  protected BaseTextField m_TextUser;

  /** the edit field for the database password. */
  protected JPasswordField m_TextPassword;

  /** the checkbox for showing the password. */
  protected BaseCheckBox m_CheckBoxShowPassword;

  /** the combobox for the logging level. */
  protected BaseComboBox<LoggingLevel> m_ComboBoxLoggingLevel;

  /** the checkbox for connecting on startup. */
  protected BaseCheckBox m_CheckBoxConnectOnStartUp;

  /** the checkbox for auto-commit. */
  protected BaseCheckBox m_CheckBoxAutoCommit;

  /** the button for creating a new connection. */
  protected BaseButton m_ButtonNew;

  /** the button removing the database connection. */
  protected BaseButton m_ButtonRemove;

  /** the button for making a connection the default one. */
  protected BaseButton m_ButtonMakeDefault;

  /** the button connecting/disconnecting the database. */
  protected BaseButton m_ButtonConnect;

  /** the label for status messages. */
  protected JLabel m_LabelStatus;

  /** for the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the change listeners. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Self            = this;
    m_ChangeListeners = new HashSet<>();
  }

  /**
   * Returns the default database connection to use.
   *
   * @return		the database connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Initializes the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.NORTH);

    m_ComboBoxConnections = new BaseComboBox<>(getDatabaseConnection().getAllConnectionParameters().toArray(new ConnectionParameters[0]));
    m_ComboBoxConnections.addActionListener((ActionEvent e) -> {
      if (m_ComboBoxConnections.getSelectedIndex() == -1)
        return;
      displayConnection(m_ComboBoxConnections.getSelectedItem());
    });
    m_PanelParameters.addParameter("_Connections", m_ComboBoxConnections);

    m_TextURL = new BaseTextField(20);
    m_PanelParameters.addParameter("_URL", m_TextURL);

    m_TextUser = new BaseTextField(20);
    m_PanelParameters.addParameter("U_ser", m_TextUser);

    m_TextPassword = new JPasswordField(20);
    m_TextPassword.setEchoChar(Constants.PASSWORD_CHAR);
    m_PanelParameters.addParameter("_Password", m_TextPassword);

    m_CheckBoxShowPassword = new BaseCheckBox();
    m_CheckBoxShowPassword.setSelected(false);
    m_CheckBoxShowPassword.addActionListener((ActionEvent e) -> {
      if (m_CheckBoxShowPassword.isSelected())
        m_TextPassword.setEchoChar((char) 0);
      else
        m_TextPassword.setEchoChar(Constants.PASSWORD_CHAR);
    });
    m_PanelParameters.addParameter("Sho_w password", m_CheckBoxShowPassword);

    m_ComboBoxLoggingLevel = new BaseComboBox<>(LoggingLevel.values());
    m_PanelParameters.addParameter("_Logging level", m_ComboBoxLoggingLevel);

    m_CheckBoxConnectOnStartUp = new BaseCheckBox();
    m_CheckBoxConnectOnStartUp.setSelected(false);
    m_PanelParameters.addParameter("Co_nnect on startup", m_CheckBoxConnectOnStartUp);

    m_CheckBoxAutoCommit = new BaseCheckBox();
    m_CheckBoxAutoCommit.setSelected(true);
    m_PanelParameters.addParameter("Auto co_mmit", m_CheckBoxAutoCommit);

    panel2 = new JPanel(new BorderLayout());
    add(panel2, BorderLayout.SOUTH);

    // status
    m_LabelStatus = new JLabel();
    panel         = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_LabelStatus);
    panel2.add(panel, BorderLayout.WEST);

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    m_ButtonNew = new BaseButton(ImageManager.getIcon("new.gif"));
    m_ButtonNew.setToolTipText("Create new connection");
    panel.add(m_ButtonNew);
    m_ButtonNew.addActionListener((ActionEvent e) -> {
      m_ButtonNew.setEnabled(false);
      newConnection();
      m_ButtonNew.setEnabled(true);
    });

    m_ButtonRemove = new BaseButton(ImageManager.getIcon("delete.gif"));
    m_ButtonRemove.setToolTipText("Delete current connection");
    panel.add(m_ButtonRemove);
    m_ButtonRemove.addActionListener((ActionEvent e) -> {
      m_ButtonRemove.setEnabled(false);
      removeConnection();
      m_ButtonRemove.setEnabled(true);
    });

    m_ButtonMakeDefault = new BaseButton("Make default");
    panel.add(m_ButtonMakeDefault);
    m_ButtonMakeDefault.setMnemonic('m');
    m_ButtonMakeDefault.addActionListener((ActionEvent e) -> {
      m_ButtonMakeDefault.setEnabled(false);
      makeDefault();
      m_ButtonMakeDefault.setEnabled(true);
      update();
    });

    m_ButtonConnect = new BaseButton("Connect");
    panel.add(m_ButtonConnect);
    m_ButtonConnect.setMnemonic('C');
    m_ButtonConnect.addActionListener((ActionEvent e) -> {
      AbstractDatabaseConnection conn = getActiveConnectionFor(getCurrentParameters());
      if ((conn != null) && conn.isConnected())
	performDisconnect();
      else
        performConnect();
    });
    panel2.add(panel, BorderLayout.EAST);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    update();
  }

  /**
   * The title of the connection panel.
   *
   * @return		the title
   */
  protected abstract String getTitle();

  /**
   * Updates the fields with the parameters.
   *
   * @param conn	the parameters to display
   */
  protected void connectionParametersToFields(ConnectionParameters conn) {
    m_TextURL.setText(conn.getURL());
    m_TextUser.setText(conn.getUser());
    m_TextPassword.setText(conn.getPassword().getValue());
    m_ComboBoxLoggingLevel.setSelectedItem(conn.getLoggingLevel());
    m_CheckBoxConnectOnStartUp.setSelected(conn.getConnectOnStartUp());
    m_CheckBoxAutoCommit.setSelected(conn.getAutoCommit());
  }

  /**
   * Returns the connection that is represented by the connection parameters.
   *
   * @param params	the parameters to get the connection for (if any)
   * @return		the connection, null if none active
   */
  protected AbstractDatabaseConnection getActiveConnectionFor(ConnectionParameters params) {
    AbstractDatabaseConnection result;

    result = null;
    for (AbstractDatabaseConnection c: DatabaseManager.getActiveConnectionObjects()) {
      if (c.getCurrentConnectionParameters().equals(params)) {
        result = c;
        break;
      }
    }

    return result;
  }

  /**
   * Displays the connection parameters.
   *
   * @param params	the database connection to display
   */
  protected void displayConnection(ConnectionParameters params) {
    AbstractDatabaseConnection	conn;
    boolean 			connected;

    connectionParametersToFields(params);

    conn      = getActiveConnectionFor(params);
    connected = (conn != null) && conn.isConnected();

    m_TextURL.setEditable(!connected);
    m_TextUser.setEditable(!connected);
    m_TextPassword.setEditable(!connected);
    m_CheckBoxShowPassword.setEnabled(!connected);
    m_ComboBoxLoggingLevel.setEnabled(!connected);
    m_CheckBoxConnectOnStartUp.setEnabled(!connected);
    m_CheckBoxAutoCommit.setEnabled(!connected);

    m_ButtonRemove.setEnabled(!connected);
    m_ButtonMakeDefault.setEnabled((conn != null) && !params.equals(conn.getDefaultConnectionParameters()));

    if (connected)
      m_ButtonConnect.setText("Disconnect");
    else
      m_ButtonConnect.setText("Connect");
  }

  /**
   * Displays the connection.
   *
   * @param conn	the database connection to display
   */
  protected void displayConnection(AbstractDatabaseConnection conn) {
    boolean 			connected;
    List<ConnectionParameters> 	connections;
    ConnectionParameters 	current;
    int 			index;

    current     = conn.getCurrentConnectionParameters();
    connections = conn.getAllConnectionParameters();
    index       = connections.indexOf(current);

    connectionParametersToFields(current);

    connected = conn.isConnected();
    m_ComboBoxConnections.setModel(new DefaultComboBoxModel<>(connections.toArray(new ConnectionParameters[0])));
    if ((index == -1) && (connections.size() > 0)) {
      displayConnection(connections.get(0));
      return;
    }
    else {
      m_ComboBoxConnections.setSelectedIndex(index);
    }

    m_TextURL.setEditable(!connected);
    m_TextUser.setEditable(!connected);
    m_TextPassword.setEditable(!connected);
    m_CheckBoxShowPassword.setEnabled(!connected);
    m_ComboBoxLoggingLevel.setEnabled(!connected);
    m_CheckBoxConnectOnStartUp.setEnabled(!connected);
    m_CheckBoxAutoCommit.setEnabled(!connected);

    m_ButtonRemove.setEnabled(!connected);
    m_ButtonMakeDefault.setEnabled(!current.equals(conn.getDefaultConnectionParameters()));

    if (connected)
      m_ButtonConnect.setText("Disconnect");
    else
      m_ButtonConnect.setText("Connect");
  }

  /**
   * Performs the connect.
   */
  protected abstract void performConnect();

  /**
   * Performs the disconnect.
   */
  protected abstract void performDisconnect();

  /**
   * Allows adding a new connection.
   */
  protected void newConnection() {
    displayConnection(getDatabaseConnection().newConnectionParameters());
  }

  /**
   * Removes the current parameters as available connection.
   */
  protected void removeConnection() {
    if (!getDatabaseConnection().removeConnectionParameters(getCurrentParameters())) {
      GUIHelper.showErrorMessage(m_Self, "Failed to remove connection!");
    }
    else {
      if (m_ComboBoxConnections.getModel().getSize() > 0)
	update();
      else
	newConnection();
    }
  }

  /**
   * Makes the current parameters the default.
   */
  protected void makeDefault() {
    if (!getDatabaseConnection().makeDefaultConnection(getCurrentParameters()))
      GUIHelper.showErrorMessage(m_Self, "Failed to make current connection the default one!");
  }

  /**
   * Returns a new instance of a ConnectionParameters object.
   *
   * @return		the empty parameters object
   */
  protected ConnectionParameters newConnectionParameters() {
    return new ConnectionParameters();
  }

  /**
   * Returns the current parameters as connection object.
   *
   * @return		the current setup
   */
  protected ConnectionParameters getCurrentParameters() {
    ConnectionParameters 	result;

    result = newConnectionParameters();
    result.setParameter(ConnectionParameters.PARAM_URL,              m_TextURL.getText());
    result.setParameter(ConnectionParameters.PARAM_USER,             m_TextUser.getText());
    result.setParameter(ConnectionParameters.PARAM_PASSWORD,         new String(m_TextPassword.getPassword()));
    result.setParameter(ConnectionParameters.PARAM_LOGGINGLEVEL,     m_ComboBoxLoggingLevel.getSelectedItem().toString());
    result.setParameter(ConnectionParameters.PARAM_CONNECTONSTARTUP, "" + m_CheckBoxConnectOnStartUp.isSelected());
    result.setParameter(ConnectionParameters.PARAM_AUTOCOMMIT,       "" + m_CheckBoxAutoCommit.isSelected());

    return result;
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public abstract AbstractDatabaseConnection getDatabaseConnection();

  /**
   * updates the enabled state content etc. of all the GUI elements, based on
   * the DatabaseConnection object of the scripting engine.
   */
  public void update() {
    SwingUtilities.invokeLater(() -> displayConnection(getDatabaseConnection()));
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(final String msg) {
    SwingUtilities.invokeLater(() -> m_LabelStatus.setText(msg));
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * Merely uses the title of the panels for comparison.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(AbstractDatabaseConnectionPanel o) {
    return getTitle().compareTo(o.getTitle());
  }

  /**
   * Checks whether this object is equal to the specified one.
   *
   * @param o		the object to compare with
   * @return		true if the same (title)
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof AbstractDatabaseConnectionPanel)
      return (compareTo((AbstractDatabaseConnectionPanel) o) == 0);
    else
      return false;
  }

  /**
   * Adds the listener for changes in the connection.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the listener for changes in the connection.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners about a change in the connection.
   */
  protected void notifyChangeListeners() {
    ChangeEvent	event;

    event = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(event);
  }

  /**
   * Clears the connections.
   *
   * @return true if able to clear connections
   */
  public boolean disconnectConnections() {
    if (getDatabaseConnection() == null)
      return false;
    if (getDatabaseConnection().getOwner() == null)
      return false;
    getDatabaseConnection().getOwner().disconnectConnections();
    DatabaseManager.disconnectAllConnections();
    notifyChangeListeners();
    update();
    return true;
  }
}
