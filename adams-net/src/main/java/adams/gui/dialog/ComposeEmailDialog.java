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
 * ComposeEmailDialog.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import adams.core.io.PlaceholderFile;
import adams.core.net.EmailAddress;
import adams.gui.core.BaseDialog;

/**
 * A standalone dialog for composing emails.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ComposeEmailDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = 3653191762492506746L;

  /** the compose panel. */
  protected ComposeEmailPanel m_Panel;

  /** whether to close the dialog when successfully sent. */
  protected boolean m_CloseOnSend;

  /** whether the user closed the dialog. */
  protected boolean m_DialogClosedByUser;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   */
  public ComposeEmailDialog(Dialog owner) {
    this(owner, "Compose email");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public ComposeEmailDialog(Dialog owner, String title) {
    super(owner, title, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   */
  public ComposeEmailDialog(Frame owner) {
    this(owner, "Compose email");
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public ComposeEmailDialog(Frame owner, String title) {
    super(owner, title, true);
  }

  /**
   * initializes the GUI elements.
   */
  protected void initGUI() {
    super.initGUI();

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout());

    // panel
    m_Panel = new ComposeEmailPanel();
    m_Panel.addSendListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeOnSuccessfulSend();
      }
    });
    add(m_Panel, BorderLayout.CENTER);

    pack();

    // adjust size
    setSize(600, 500);
  }

  /**
   * Resets the dialog to default settings.
   */
  public void clear() {
    m_Panel.clear();
  }

  /**
   * Closes the dialog on a successful send.
   *
   * @see		#getCloseOnSend()
   * @see		#getLastSendResult()
   */
  protected void closeOnSuccessfulSend() {
    if (getCloseOnSend() && (getLastSendResult() == null)) {
      m_DialogClosedByUser = false;
      setVisible(false);
    }
  }

  /**
   * Attempts to send the email.
   *
   * @return		null if successfully sent
   * @see		#setCloseOnSend(boolean)
   */
  public String send() {
    String	result;

    result = m_Panel.send();
    closeOnSuccessfulSend();

    return result;
  }

  /**
   * Sets the "from" email address.
   *
   * @param value	the address
   */
  public void setFrom(EmailAddress value) {
    m_Panel.setFrom(value);
  }

  /**
   * Returns the current from address.
   *
   * @return		the "from" address
   */
  public EmailAddress getFrom() {
    return m_Panel.getFrom();
  }

  /**
   * Sets the recipients.
   *
   * @param value	the recipients
   */
  public void setTO(EmailAddress[] value) {
    m_Panel.setTO(value);
  }

  /**
   * Returns the recipients.
   *
   * @return		the recipients
   */
  public EmailAddress[] getTO() {
    return m_Panel.getTO();
  }

  /**
   * Sets the CC recipients.
   *
   * @param value	the CC Brecipients
   */
  public void setCC(EmailAddress[] value) {
    m_Panel.setCC(value);
  }

  /**
   * Returns the CC recipients.
   *
   * @return		the CC recipients
   */
  public EmailAddress[] getCC() {
    return m_Panel.getCC();
  }

  /**
   * Sets the BCC recipients.
   *
   * @param value	the BCC recipients
   */
  public void setBCC(EmailAddress[] value) {
    m_Panel.setBCC(value);
  }

  /**
   * Returns the BCC recipients.
   *
   * @return		the BCC recipients
   */
  public EmailAddress[] getBCC() {
    return m_Panel.getBCC();
  }

  /**
   * Sets the attachments.
   *
   * @param value	the attachments
   */
  public void setAttachments(PlaceholderFile[] value) {
    m_Panel.setAttachments(value);
  }

  /**
   * Returns the attachments.
   *
   * @return		the attachements
   */
  public PlaceholderFile[] getAttachments() {
    return m_Panel.getAttachments();
  }

  /**
   * Sets the "subject".
   *
   * @param value	the subject
   */
  public void setSubject(String value) {
    m_Panel.setSubject(value);
  }

  /**
   * Returns the "subject".
   *
   * @return		the subject
   */
  public String getSubject() {
    return m_Panel.getSubject();
  }

  /**
   * Sets the "body".
   *
   * @param value	the body
   */
  public void setBody(String value) {
    m_Panel.setBody(value);
  }

  /**
   * Returns the "signature".
   *
   * @return		the signature
   */
  public String getBody() {
    return m_Panel.getBody();
  }

  /**
   * Sets the "signature".
   *
   * @param value	the signature
   */
  public void setSignature(String value) {
    m_Panel.setSignature(value);
  }

  /**
   * Returns the "signature".
   *
   * @return		the signature
   */
  public String getSignature() {
    return m_Panel.getSignature();
  }

  /**
   * Returns the last result of sending an email.
   *
   * @return		null if successfully (or nothing yet) sent, otherwise
   * 			the error message
   */
  public String getLastSendResult() {
    return m_Panel.getLastSendResult();
  }

  /**
   * Sets whether to close the dialog on successfully sending the email.
   *
   * @param value	if true the dialog gets closed as soon as email got
   * 			successfully sent
   */
  public void setCloseOnSend(boolean value) {
    m_CloseOnSend = value;
  }

  /**
   * Returns whether the dialog gets closed when successfully sending the email.
   *
   * @return		true if the dialog gets closed as soon as the email
   * 			got sent successfully
   */
  public boolean getCloseOnSend() {
    return m_CloseOnSend;
  }

  /**
   * Returns whether the dialog was closed by the user.
   *
   * @return		true if closed by the user
   */
  public boolean getDialogClosedByUser() {
    return m_DialogClosedByUser;
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
    super.beforeShow();

    m_Panel.grabFocus();
    m_DialogClosedByUser = true;
  }
}
