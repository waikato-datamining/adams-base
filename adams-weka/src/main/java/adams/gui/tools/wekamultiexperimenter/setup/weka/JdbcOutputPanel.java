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
 * JdbcOutputPanel.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.setup.weka;

import adams.gui.core.BaseButton;
import adams.gui.core.BasePasswordFieldWithButton;
import adams.gui.core.BaseTextField;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import weka.experiment.DatabaseResultListener;
import weka.experiment.DatabaseUtils;
import weka.experiment.ResultListener;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Stores the results in a JDBC database.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JdbcOutputPanel
  extends AbstractOutputPanel {

  /** for serialization. */
  private static final long serialVersionUID = 3142999120128854278L;

  /** the JDBC URL. */
  protected BaseTextField m_TextURL;
  
  /** the button for bringing up the dialog for the user credentials. */
  protected BaseButton m_ButtonCredentials;
  
  /** the user name. */
  protected BaseTextField m_TextUser;
  
  /** the password. */
  protected BasePasswordFieldWithButton m_TextPassword;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    ParameterPanel	panel;
    JPanel		panel2;
    DatabaseUtils	dbutils;
    
    super.initGUI();
    
    panel = new ParameterPanel();
    
    try {
      dbutils = new DatabaseUtils();
    }
    catch (Exception e) {
      logError("Failed to instantiate " + DatabaseUtils.class.getName(), "Initializing database");
      dbutils = null;
    }
    
    m_TextURL = new BaseTextField(40);
    m_TextURL.setText((dbutils == null) ? "" : dbutils.getDatabaseURL());
    m_ButtonCredentials = new BaseButton("...");
    m_ButtonCredentials.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	showCredentials();
      }
    });

    panel2 = new JPanel(new BorderLayout());
    panel2.add(m_TextURL, BorderLayout.CENTER);
    panel2.add(m_ButtonCredentials, BorderLayout.EAST);
    panel.addParameter("URL", panel2);
    
    m_TextUser = new BaseTextField(20);
    m_TextUser.setText((dbutils == null) ? "" : dbutils.getUsername());
    m_TextPassword = new BasePasswordFieldWithButton(20);
    m_TextPassword.setShowPopupMenu(true);
    m_TextPassword.setText((dbutils == null) ? "" : dbutils.getPassword());

    add(panel, BorderLayout.CENTER);
  }

  /**
   * Displays a dialog for the user credentials.
   */
  protected void showCredentials() {
    ApprovalDialog	dialog;
    ParameterPanel	panel;
    String		user;
    String		pw;
    
    panel = new ParameterPanel();
    panel.addParameter("User", m_TextUser);
    panel.addParameter("Password", m_TextPassword);

    // backup values
    user = m_TextUser.getText();
    pw   = new String(m_TextPassword.getPassword());
    
    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setTitle("Database credentials");
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setDiscardVisible(false);
    dialog.setApproveVisible(true);
    dialog.setCancelVisible(true);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION) {
      m_TextUser.setText(user);
      m_TextPassword.setText(pw);
    }
  }

  /**
   * Returns the name to display in the GUI.
   * 
   * @return		the name
   */
  @Override
  public String getOutputName() {
    return "JDBC";
  }
  
  /**
   * Returns whether this panel handles the specified {@link ResultListener}.
   * 
   * @param listener	the listener to check
   * @return		true if the panel handles this listener
   */
  @Override
  public boolean handlesResultListener(ResultListener listener) {
    return (listener.getClass() == DatabaseResultListener.class);
  }

  /**
   * Sets the {@link ResultListener}.
   * 
   * @param value	the listener
   */
  @Override
  public void setResultListener(ResultListener value) {
    m_TextURL.setText(((DatabaseResultListener) value).getDatabaseURL());
    m_TextUser.setText(((DatabaseResultListener) value).getUsername());
    m_TextPassword.setText(((DatabaseResultListener) value).getPassword());
  }

  /**
   * Returns the configured {@link ResultListener}.
   * 
   * @return		the listener, null if failed to set up
   */
  @Override
  public ResultListener getResultListener() {
    DatabaseResultListener	result;
    
    try {
      result = new DatabaseResultListener();
      result.setDatabaseURL(m_TextURL.getText());
      result.setUsername(m_TextUser.getText());
      result.setPassword(new String(m_TextPassword.getPassword()));
    }
    catch (Exception e) {
      result = null;
    }
    
    return result;
  }
}
