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
 * ComposeEmailPanel.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.net.AbstractSendEmail;
import adams.core.net.Email;
import adams.core.net.EmailAddress;
import adams.core.net.EmailHelper;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericArrayEditorPanel;

/**
 * A panel for composing an email.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ComposeEmailPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 8667404133601287157L;

  /** the "from". */
  protected JTextField m_TextFrom;

  /** the "to recipients". */
  protected GenericArrayEditorPanel m_GAEPanelTO;

  /** the "cc recipients". */
  protected GenericArrayEditorPanel m_GAEPanelCC;

  /** the "bcc recipients". */
  protected GenericArrayEditorPanel m_GAEPanelBCC;

  /** the attachments. */
  protected GenericArrayEditorPanel m_GAEPanelAttachments;

  /** the subject. */
  protected JTextField m_TextSubject;

  /** the body. */
  protected BaseTextArea m_TextBody;

  /** the signature. */
  protected BaseTextArea m_TextSignature;

  /** the button for sending. */
  protected JButton m_ButtonSend;

  /** the tabbed pane for recipients and attachments. */
  protected BaseTabbedPane m_TabbedPane;

  /** the last "send" result. */
  protected String m_LastSendResult;

  /** the send listeners. */
  protected HashSet<ActionListener> m_Listeners;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Listeners = new HashSet<ActionListener>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel		panel;
    JPanel		panel2;
    JPanel		panelBodyFooter;
    ParameterPanel	paramPanel;
    JLabel		label;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.NORTH);

    // recipients
    paramPanel = new ParameterPanel();
    m_TabbedPane.addTab("Addresses", paramPanel);

    m_TextFrom = new JTextField(40);
    paramPanel.addParameter("_From", m_TextFrom);

    m_GAEPanelTO = new GenericArrayEditorPanel(new EmailAddress[0]);
    m_GAEPanelTO.setMaxDisplayItems(2);
    paramPanel.addParameter("_To", m_GAEPanelTO);

    m_GAEPanelCC = new GenericArrayEditorPanel(new EmailAddress[0]);
    m_GAEPanelCC.setMaxDisplayItems(2);
    paramPanel.addParameter("_CC", m_GAEPanelCC);

    m_GAEPanelBCC = new GenericArrayEditorPanel(new EmailAddress[0]);
    m_GAEPanelBCC.setMaxDisplayItems(2);
    paramPanel.addParameter("_BCC", m_GAEPanelBCC);

    // attachements
    paramPanel = new ParameterPanel();
    m_TabbedPane.addTab("Attachments", paramPanel);

    m_GAEPanelAttachments = new GenericArrayEditorPanel(new PlaceholderFile[0]);
    paramPanel.addParameter("_Files", m_GAEPanelAttachments);

    // subject and body
    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.CENTER);

    m_TextSubject = new JTextField(40);
    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    label = new JLabel("Subject");
    label.setLabelFor(m_TextSubject);
    label.setDisplayedMnemonic('u');
    panel2.add(label);
    panel2.add(m_TextSubject);
    panel.add(panel2, BorderLayout.NORTH);

    panelBodyFooter = new JPanel(new BorderLayout());
    panel.add(panelBodyFooter, BorderLayout.CENTER);

    m_TextBody = new BaseTextArea();
    m_TextBody.setFont(GUIHelper.getMonospacedFont());
    panelBodyFooter.add(new BaseScrollPane(m_TextBody), BorderLayout.CENTER);

    m_TextSignature = new BaseTextArea(4, 80);
    m_TextSignature.setFont(GUIHelper.getMonospacedFont());
    panelBodyFooter.add(new BaseScrollPane(m_TextSignature), BorderLayout.SOUTH);

    // send button
    panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panel2, BorderLayout.SOUTH);

    m_ButtonSend = new JButton("Send");
    m_ButtonSend.setMnemonic('S');
    m_ButtonSend.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String msg = send();
	if (msg != null)
	  GUIHelper.showErrorMessage(ComposeEmailPanel.this, "Failed to send email:\n" + msg);
      }
    });
    panel2.add(m_ButtonSend);

    if (!EmailHelper.isEnabled()) {
      m_ButtonSend.setText("Send - Check email setup!");
      m_ButtonSend.setEnabled(false);
    }

    clear();
  }

  /**
   * Resets the dialog to default settings.
   */
  public void clear() {
    setFrom(new EmailAddress(EmailHelper.getDefaultFromAddress()));
    setTO(null);
    setCC(null);
    setBCC(null);
    setAttachments(null);
    setSubject("");
    setBody("");
    setSignature(Utils.unbackQuoteChars(EmailHelper.getDefaultSignature()));

    grabFocus();
    m_LastSendResult = null;
  }

  /**
   * Sets the "from" email address.
   *
   * @param value	the address
   */
  public void setFrom(EmailAddress value) {
    m_TextFrom.setText(value.getValue());
  }

  /**
   * Returns the current from address.
   *
   * @return		the "from" address
   */
  public EmailAddress getFrom() {
    return new EmailAddress(m_TextFrom.getText());
  }

  /**
   * Sets the recipients.
   *
   * @param value	the recipients
   */
  public void setTO(EmailAddress[] value) {
    if (value == null)
      value = new EmailAddress[0];
    m_GAEPanelTO.setCurrent(value);
  }

  /**
   * Returns the recipients.
   *
   * @return		the recipients
   */
  public EmailAddress[] getTO() {
    return (EmailAddress[]) m_GAEPanelTO.getCurrent();
  }

  /**
   * Sets the CC recipients.
   *
   * @param value	the CC Brecipients
   */
  public void setCC(EmailAddress[] value) {
    if (value == null)
      value = new EmailAddress[0];
    m_GAEPanelCC.setCurrent(value);
  }

  /**
   * Returns the CC recipients.
   *
   * @return		the CC recipients
   */
  public EmailAddress[] getCC() {
    return (EmailAddress[]) m_GAEPanelCC.getCurrent();
  }

  /**
   * Sets the BCC recipients.
   *
   * @param value	the BCC recipients
   */
  public void setBCC(EmailAddress[] value) {
    if (value == null)
      value = new EmailAddress[0];
    m_GAEPanelBCC.setCurrent(value);
  }

  /**
   * Returns the BCC recipients.
   *
   * @return		the BCC recipients
   */
  public EmailAddress[] getBCC() {
    return (EmailAddress[]) m_GAEPanelBCC.getCurrent();
  }

  /**
   * Sets the attachments.
   *
   * @param value	the attachments
   */
  public void setAttachments(PlaceholderFile[] value) {
    if (value == null)
      value = new PlaceholderFile[0];
    m_GAEPanelAttachments.setCurrent(value);
  }

  /**
   * Returns the attachments.
   *
   * @return		the attachements
   */
  public PlaceholderFile[] getAttachments() {
    return (PlaceholderFile[]) m_GAEPanelAttachments.getCurrent();
  }

  /**
   * Sets the "subject".
   *
   * @param value	the subject
   */
  public void setSubject(String value) {
    m_TextSubject.setText(value);
  }

  /**
   * Returns the "subject".
   *
   * @return		the subject
   */
  public String getSubject() {
    return m_TextSubject.getText();
  }

  /**
   * Sets the "body".
   *
   * @param value	the body
   */
  public void setBody(String value) {
    m_TextBody.setText(value);
  }

  /**
   * Returns the "body".
   *
   * @return		the body
   */
  public String getBody() {
    return m_TextBody.getText();
  }

  /**
   * Sets the "signature".
   *
   * @param value	the signature
   */
  public void setSignature(String value) {
    m_TextSignature.setText(value);
  }

  /**
   * Returns the "signature".
   *
   * @return		the signature
   */
  public String getSignature() {
    return m_TextSignature.getText();
  }

  /**
   * Checks whether we can send an email.
   *
   * @return		null if the email can get sent, otherwise error message
   */
  protected String check() {
    int			recipients;
    int			i;
    PlaceholderFile[]	files;

    // sender
    if (m_TextFrom.getText().trim().length() == 0)
      return "No sender!";
    
    // any recipients?
    recipients = 0;
    recipients += ((EmailAddress[]) m_GAEPanelTO.getCurrent()).length;
    recipients += ((EmailAddress[]) m_GAEPanelCC.getCurrent()).length;
    recipients += ((EmailAddress[]) m_GAEPanelBCC.getCurrent()).length;
    if (recipients == 0)
      return "No recipients added!";

    // subject
    if (m_TextSubject.getText().trim().length() == 0)
      return "No subject entered!";

    // body
    if (m_TextBody.getText().trim().length() == 0)
      return "No message body entered!";

    // do files exist?
    files = (PlaceholderFile[]) m_GAEPanelAttachments.getCurrent();
    for (i = 0; i < files.length; i++) {
      if (files[i].isDirectory())
	return "File #" + (i+1) + " points to a directory:\n" + files[i];
      if (!files[i].exists())
	return "File #" + (i+1) + " does not exist:\n" + files[i];
    }

    return null;
  }

  /**
   * Attempts to send the email.
   *
   * @return		null if successfully sent
   */
  public String send() {
    String		msg;
    Runnable		run;
    AbstractSendEmail	send;
    Email		email;

    m_LastSendResult = check();

    if (m_LastSendResult == null) {
      try {
	send = EmailHelper.getDefaultSendEmail();
	send.initializeSmtpSession(
	    EmailHelper.getSmtpServer(), 
	    EmailHelper.getSmtpPort(), 
	    EmailHelper.getSmtpStartTLS(), 
	    EmailHelper.getSmtpUseSSL(),
	    EmailHelper.getSmtpTimeout(), 
	    EmailHelper.getSmtpRequiresAuthentication(), 
	    EmailHelper.getSmtpUser(), 
	    EmailHelper.getSmtpPassword());
	email = new Email(
	    new EmailAddress(m_TextFrom.getText()),
	    (EmailAddress[]) m_GAEPanelTO.getCurrent(),
	    (EmailAddress[]) m_GAEPanelCC.getCurrent(),
	    (EmailAddress[]) m_GAEPanelBCC.getCurrent(),
	    m_TextSubject.getText(),
	    EmailHelper.combine(m_TextBody.getText(), m_TextSignature.getText()),
	    (PlaceholderFile[]) m_GAEPanelAttachments.getCurrent());
	send.sendMail(email);
      }
      catch (Exception e) {
	msg = "Failed to send email: ";
	System.err.println(msg);
	e.printStackTrace();
	m_LastSendResult = msg + e;
      }
    }

    // notify listeners
    run = new Runnable() {
      public void run() {
	notifySendListeners();
      };
    };
    SwingUtilities.invokeLater(run);

    return m_LastSendResult;
  }

  /**
   * Returns the last result obtained when sending an email.
   *
   * @return		null if successfully (or nothing yet) sent, otherwise
   * 			the error message
   */
  public String getLastSendResult() {
    return m_LastSendResult;
  }

  /**
   * Adds the listener for send events.
   *
   * @param l		the listener to add
   */
  public void addSendListener(ActionListener l) {
    m_Listeners.add(l);
  }

  /**
   * Removes the listener for send events.
   *
   * @param l		the listener to remove
   */
  public void removeSendListener(ActionListener l) {
    m_Listeners.remove(l);
  }

  /**
   * Sends out an event to all send listeners.
   */
  protected void notifySendListeners() {
    ActionEvent	event;

    event = new ActionEvent(this, -1, "send");

    for (ActionListener l: m_Listeners)
      l.actionPerformed(event);
  }

  /**
   * The body text area grabs the focus.
   */
  @Override
  public void grabFocus() {
    m_TextBody.grabFocus();
  }
}
