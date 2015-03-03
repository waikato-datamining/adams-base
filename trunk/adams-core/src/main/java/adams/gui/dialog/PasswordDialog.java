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
 * PasswordDialog.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import adams.core.Constants;
import adams.core.base.BasePassword;
import adams.gui.core.BaseDialog;

/**
 * Simple dialog for entering a password.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PasswordDialog
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = -2132414163522707681L;
  
  /** the text field for entering the password. */
  protected JPasswordField m_TextPassword;
  
  /** whether to display the password or not. */
  protected JCheckBox m_CheckBoxShowPassword;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public PasswordDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public PasswordDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public PasswordDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public PasswordDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public PasswordDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public PasswordDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public PasswordDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public PasswordDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JLabel	label;
    
    super.initGUI();
    
    setTitle("Enter password");
    setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
    
    // password field
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    getContentPane().add(panel, BorderLayout.NORTH);
    
    m_TextPassword = new JPasswordField(20);
    m_TextPassword.setEchoChar(Constants.PASSWORD_CHAR);
    m_TextPassword.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  e.consume();
	  m_Option = ApprovalDialog.APPROVE_OPTION;
	  setVisible(false);
	}
	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  e.consume();
	  m_Option = ApprovalDialog.CANCEL_OPTION;
	  setVisible(false);
	}
	else {
	  super.keyPressed(e);
	}
      }
    });
    label = new JLabel("Password");
    label.setLabelFor(m_TextPassword);
    panel.add(label);
    panel.add(m_TextPassword);

    // show password
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    getContentPane().add(panel, BorderLayout.CENTER);
    
    m_CheckBoxShowPassword = new JCheckBox("Show password");
    m_CheckBoxShowPassword.setMnemonic('S');
    m_CheckBoxShowPassword.setToolTipText("If checked, the password will be shown in clear text as you type it");
    m_CheckBoxShowPassword.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_CheckBoxShowPassword.isSelected())
	  m_TextPassword.setEchoChar((char) 0);
	else
	  m_TextPassword.setEchoChar(Constants.PASSWORD_CHAR);
      }
    });
    panel.add(m_CheckBoxShowPassword);
    
    pack();
  }
  
  /**
   * Sets the initial password to display.
   * 
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_TextPassword.setText(value.getValue());
  }
  
  /**
   * Returns the current password.
   * 
   * @return		the password
   */
  public BasePassword getPassword() {
    return new BasePassword(m_TextPassword.getText());
  }
}
