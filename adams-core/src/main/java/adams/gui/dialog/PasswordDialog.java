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
 * PasswordDialog.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import adams.core.base.BasePassword;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePasswordFieldWithButton;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Simple dialog for entering a password.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PasswordDialog
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = -2132414163522707681L;

  /** the password label. */
  protected JLabel m_LabelPassword;

  /** the text field for entering the password. */
  protected BasePasswordFieldWithButton m_TextPassword;

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

    super.initGUI();
    
    setTitle("Enter password");
    setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
    
    // password field
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    getContentPane().add(panel, BorderLayout.NORTH);
    
    m_TextPassword = new BasePasswordFieldWithButton(20);
    m_TextPassword.setShowPopupMenu(true);
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
    m_LabelPassword = new JLabel("Password");
    m_LabelPassword.setLabelFor(m_TextPassword);
    panel.add(m_LabelPassword);
    panel.add(m_TextPassword);

    // show password
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    getContentPane().add(panel, BorderLayout.CENTER);

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
    return new BasePassword(new String(m_TextPassword.getPassword()));
  }

  /**
   * Sets the text for the password label.
   *
   * @param value	the label text
   */
  public void setLabelPassword(String value) {
    m_LabelPassword.setText(value);
  }

  /**
   * Returns the text of the password label.
   *
   * @return		the label text
   */
  public String getLabelPassword() {
    return m_LabelPassword.getText();
  }
}
