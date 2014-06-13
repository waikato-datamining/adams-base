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
 * ApprovalDialog.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import adams.gui.core.GUIHelper;

/**
 * Ancestor for dialogs that offer approval/disapproval buttons, e.g.,
 * OK and Cancel (default). The option selected by the user can be retrieved
 * via <code>getOption()</code>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #getOption()
 */
public class ApprovalDialog
  extends DialogWithButtons {

  /** for serialization. */
  private static final long serialVersionUID = -7382983170735594052L;

  /** the approve option. */
  public final static int APPROVE_OPTION = JOptionPane.YES_OPTION;

  /** the discard option. */
  public final static int DISCARD_OPTION = JOptionPane.NO_OPTION;

  /** the cancel option. */
  public final static int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;

  /** the Approve button. */
  protected JButton m_ButtonApprove;

  /** the Discard button. */
  protected JButton m_ButtonDiscard;

  /** the Cancel button. */
  protected JButton m_ButtonCancel;

  /** the option selected by the user (CANCEL_OPTION, APPROVE_OPTION). */
  protected int m_Option;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public ApprovalDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public ApprovalDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public ApprovalDialog(Dialog owner, String title) {
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
  public ApprovalDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public ApprovalDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public ApprovalDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public ApprovalDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public ApprovalDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Option = CANCEL_OPTION;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ButtonApprove = new JButton();
    setApproveCaption("OK");
    setApproveMnemonic(KeyEvent.VK_O);
    m_ButtonApprove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String msg = checkInput();
	if (msg == null) {
	  m_Option = APPROVE_OPTION;
	  setVisible(false);
	}
	else {
	  GUIHelper.showErrorMessage(ApprovalDialog.this, msg);
	}
      }
    });
    m_PanelButtonsRight.add(m_ButtonApprove);

    m_ButtonDiscard = new JButton();
    setDiscardCaption("Discard");
    setDiscardMnemonic(KeyEvent.VK_D);
    m_ButtonDiscard.setVisible(false);
    m_ButtonDiscard.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Option = DISCARD_OPTION;
	setVisible(false);
      }
    });
    m_PanelButtonsRight.add(m_ButtonDiscard);

    m_ButtonCancel = new JButton();
    setCancelCaption("Cancel");
    setCancelMnemonic(KeyEvent.VK_C);
    m_ButtonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Option = CANCEL_OPTION;
	setVisible(false);
      }
    });
    m_PanelButtonsRight.add(m_ButtonCancel);
  }

  /**
   * Returns whether the user approved or canceled the dialog.
   *
   * @return		the result
   * @see		#APPROVE_OPTION
   * @see		#CANCEL_OPTION
   */
  public int getOption() {
    return m_Option;
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  @Override
  protected void beforeShow() {
    super.beforeShow();

    m_Option = CANCEL_OPTION;
  }

  /**
   * Hook method for the "approve" button. Button triggers only if this method
   * returns null.
   * <p/>
   * Default implementation returns null.
   * 
   * @return		null if the input is valid, otherwise error message
   */
  protected String checkInput() {
    return null;
  }
  
  /**
   * Returns the approve button.
   * 
   * @return		the button
   */
  public JButton getApproveButton() {
    return m_ButtonApprove;
  }

  /**
   * Sets the visbility of the approve button.
   *
   * @param value	true if to display button
   */
  public void setApproveVisible(boolean value) {
    m_ButtonApprove.setVisible(value);
  }

  /**
   * Returns the visibility of the approve button.
   *
   * @return		true if visible
   */
  public boolean isApproveVisible() {
    return m_ButtonApprove.isVisible();
  }

  /**
   * Sets the caption/text of the approve button.
   *
   * @param value	the new text
   */
  public void setApproveCaption(String value) {
    m_ButtonApprove.setText(value);
  }

  /**
   * Returns the caption/text of the approve button.
   *
   * @return		the current text
   */
  public String getApproveCaption() {
    return m_ButtonApprove.getText();
  }

  /**
   * Sets the mnemonic of the approve button.
   *
   * @param value	the new mnemonic, e.g., KeyEvent.VK_O
   */
  public void setApproveMnemonic(int value) {
    m_ButtonApprove.setMnemonic(value);
  }

  /**
   * Returns the mnemonic of the approve button.
   *
   * @return		the current mnemonic, e.g., KeyEvent.VK_O
   */
  public int getApproveMnemonic() {
    return m_ButtonApprove.getMnemonic();
  }

  /**
   * Returns the discard button.
   * 
   * @return		the button
   */
  public JButton getDiscardButton() {
    return m_ButtonDiscard;
  }

  /**
   * Sets the visbility of the discard button.
   *
   * @param value	true if to display button
   */
  public void setDiscardVisible(boolean value) {
    m_ButtonDiscard.setVisible(value);
  }

  /**
   * Returns the visibility of the discard button.
   *
   * @return		true if visible
   */
  public boolean isDiscardVisible() {
    return m_ButtonDiscard.isVisible();
  }

  /**
   * Sets the caption/text of the discard button.
   *
   * @param value	the new text
   */
  public void setDiscardCaption(String value) {
    m_ButtonDiscard.setText(value);
  }

  /**
   * Returns the caption/text of the discard button.
   *
   * @return		the current text
   */
  public String getDiscardCaption() {
    return m_ButtonDiscard.getText();
  }

  /**
   * Sets the mnemonic of the discard button.
   *
   * @param value	the new mnemonic, e.g., KeyEvent.VK_D
   */
  public void setDiscardMnemonic(int value) {
    m_ButtonDiscard.setMnemonic(value);
  }

  /**
   * Returns the mnemonic of the discard button.
   *
   * @return		the current mnemonic, e.g., KeyEvent.VK_D
   */
  public int getDiscardMnemonic() {
    return m_ButtonDiscard.getMnemonic();
  }

  /**
   * Returns the cancel button.
   * 
   * @return		the button
   */
  public JButton getCancelButton() {
    return m_ButtonCancel;
  }
  
  /**
   * Sets the visbility of the cancel button.
   *
   * @param value	true if to display button
   */
  public void setCancelVisible(boolean value) {
    m_ButtonCancel.setVisible(value);
  }

  /**
   * Returns the visibility of the cancel button.
   *
   * @return		true if visible
   */
  public boolean isCancelVisible() {
    return m_ButtonCancel.isVisible();
  }

  /**
   * Sets the caption/text of the Cancel button.
   *
   * @param value	the new text
   */
  public void setCancelCaption(String value) {
    m_ButtonCancel.setText(value);
  }

  /**
   * Returns the caption/text of the Cancel button.
   *
   * @return		the current text
   */
  public String getCancelCaption() {
    return m_ButtonCancel.getText();
  }

  /**
   * Sets the mnemonic of the Cancel button.
   *
   * @param value	the new mnemonic, e.g., KeyEvent.VK_C
   */
  public void setCancelMnemonic(int value) {
    m_ButtonCancel.setMnemonic(value);
  }

  /**
   * Returns the mnemonic of the Cancel button.
   *
   * @return		the current mnemonic, e.g., KeyEvent.VK_C
   */
  public int getCancelMnemonic() {
    return m_ButtonCancel.getMnemonic();
  }
  
  /**
   * Returns a panel for buttons. By default, only the right one is 
   * initialized with buttons.
   * 
   * @param left	whether to retrieve the left or right panel
   * @return		the specified panel
   */
  public JPanel getButtonsPanel(boolean left) {
    if (left)
      return m_PanelButtonsLeft;
    else
      return m_PanelButtonsRight;
  }

  /**
   * Returns a basic (modal) approval dialog (ok/cancel).
   *
   * @param owner	the owner of the dialog
   */
  public static ApprovalDialog getDialog(Dialog owner) {
    return getDialog(owner, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Returns a basic approval dialog (ok/cancel).
   *
   * @param owner	the owner of the dialog
   * @param modal	the modality of the dialog
   */
  public static ApprovalDialog getDialog(Dialog owner, ModalityType modal) {
    return new ApprovalDialog(owner, modal);
  }

  /**
   * Returns a basic (modal) approval dialog (ok/cancel).
   *
   * @param owner	the owner of the dialog
   */
  public static ApprovalDialog getDialog(Frame owner) {
    return getDialog(owner, true);
  }

  /**
   * Returns a basic (modal) approval dialog (ok/cancel).
   *
   * @param owner	the owner of the dialog
   * @param modal	whether to create a modal frame
   */
  public static ApprovalDialog getDialog(Frame owner, boolean modal) {
    return new ApprovalDialog(owner, modal);
  }

  /**
   * Returns a basic (modal) confirmation dialog (yes/no/cancel).
   *
   * @param owner	the owner of the dialog
   */
  public static ApprovalDialog getConfirmationDialog(Dialog owner) {
    return getConfirmationDialog(owner, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Returns a basic confirmation dialog (yes/no/cancel).
   *
   * @param owner	the owner of the dialog
   * @param modal	the modality of the dialog
   */
  public static ApprovalDialog getConfirmationDialog(Dialog owner, ModalityType modal) {
    ApprovalDialog	result;
    
    result = new ApprovalDialog(owner, modal);
    result.setApproveCaption("Yes");
    result.setApproveMnemonic(KeyEvent.VK_Y);
    result.setDiscardCaption("No");
    result.setDiscardMnemonic(KeyEvent.VK_N);
    result.setDiscardVisible(true);
    
    return result;
  }

  /**
   * Returns a basic (modal) confirmation dialog (yes/no/cancel).
   *
   * @param owner	the owner of the dialog
   */
  public static ApprovalDialog getConfirmationDialog(Frame owner) {
    return getConfirmationDialog(owner, true);
  }

  /**
   * Returns a basic confirmation dialog (yes/no/cancel).
   *
   * @param owner	the owner of the dialog
   * @param modal	whether to create a modal dialog
   */
  public static ApprovalDialog getConfirmationDialog(Frame owner, boolean modal) {
    ApprovalDialog	result;
    
    result = new ApprovalDialog(owner, modal);
    result.setApproveCaption("Yes");
    result.setApproveMnemonic(KeyEvent.VK_Y);
    result.setDiscardCaption("No");
    result.setDiscardMnemonic(KeyEvent.VK_N);
    result.setDiscardVisible(true);
    
    return result;
  }

  /**
   * Returns a basic (modal) info dialog (ok).
   *
   * @param owner	the owner of the dialog
   */
  public static ApprovalDialog getInformationDialog(Dialog owner) {
    return getInformationDialog(owner, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Returns a basic info dialog (ok).
   *
   * @param owner	the owner of the dialog
   * @param modal	the modality of the dialog
   */
  public static ApprovalDialog getInformationDialog(Dialog owner, ModalityType modal) {
    ApprovalDialog	result;
    
    result = new ApprovalDialog(owner, modal);
    result.setApproveVisible(true);
    result.setDiscardVisible(false);
    result.setCancelVisible(false);
    
    return result;
  }

  /**
   * Returns a basic (modal) info dialog (ok).
   *
   * @param owner	the owner of the dialog
   */
  public static ApprovalDialog getInformationDialog(Frame owner) {
    return getInformationDialog(owner, true);
  }

  /**
   * Returns a basic info dialog (ok).
   *
   * @param owner	the owner of the dialog
   * @param modal	whether to create a modal dialog
   */
  public static ApprovalDialog getInformationDialog(Frame owner, boolean modal) {
    ApprovalDialog	result;
    
    result = new ApprovalDialog(owner, modal);
    result.setApproveVisible(true);
    result.setDiscardVisible(false);
    result.setCancelVisible(false);
    
    return result;
  }
}
