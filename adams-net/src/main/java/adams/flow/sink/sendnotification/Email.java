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
 * Email.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.sendnotification;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.core.logging.LoggingHelper;
import adams.core.net.AbstractSendEmail;
import adams.core.net.EmailAddress;
import adams.core.net.EmailHelper;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.SMTPConnection;

/**
 * Uses the incoming message as body in the email being sent.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Email
  extends AbstractNotification {

  private static final long serialVersionUID = 1302036525857934850L;

  /** the sender. */
  protected EmailAddress m_Sender;

  /** the recipients. */
  protected EmailAddress[] m_Recipients;

  /** the recipients (CC). */
  protected EmailAddress[] m_CC;

  /** the recipients (BCC). */
  protected EmailAddress[] m_BCC;

  /** the subject. */
  protected String m_Subject;

  /** the signature. */
  protected BaseText m_Signature;

  /** for sending the emails. */
  protected AbstractSendEmail m_SendEmail;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the incoming message as body in the email being sent.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "sender", "sender",
      new EmailAddress(EmailHelper.getDefaultFromAddress()), false);

    m_OptionManager.add(
      "recipient", "recipients",
      new EmailAddress[0]);

    m_OptionManager.add(
      "cc", "CC",
      new EmailAddress[0]);

    m_OptionManager.add(
      "bcc", "BCC",
      new EmailAddress[0]);

    m_OptionManager.add(
      "subject", "subject",
      "");

    m_OptionManager.add(
      "signature", "signature",
      new BaseText(Utils.unbackQuoteChars(EmailHelper.getDefaultSignature())));

    m_OptionManager.add(
      "send-email", "sendEmail",
      EmailHelper.getDefaultSendEmail());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    if (!EmailHelper.isEnabled())
      return "No email support enabled, check email setup!";

    result = QuickInfoHelper.toString(this, "sender", m_Sender, "From: ");
    if ((m_Recipients != null) && (m_Recipients.length > 0))
      value = Utils.flatten(m_Recipients, ", ");
    else
      value = "<no recipients>";
    result += QuickInfoHelper.toString(this, "recipients", value, ", To: ");

    if ((m_CC != null) && (m_CC.length > 0))
      value = Utils.flatten(m_CC, ", ");
    else
      value = null;
    value = QuickInfoHelper.toString(this, "CC", value, ", CC: ");
    if (value != null)
      result += value;

    if ((m_BCC != null) && (m_BCC.length > 0))
      value = Utils.flatten(m_BCC, ", ");
    else
      value = null;
    value = QuickInfoHelper.toString(this, "BCC", value, ", BCC: ");
    if (value != null)
      result += value;

    result += QuickInfoHelper.toString(this, "sendEmail", m_SendEmail.getClass(), ", send: ");

    return result;
  }

  /**
   * Sets the sender.
   *
   * @param value	the sender
   */
  public void setSender(EmailAddress value) {
    m_Sender = value;
    reset();
  }

  /**
   * Returns the sender.
   *
   * @return 		the sender
   */
  public EmailAddress getSender() {
    return m_Sender;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String senderTipText() {
    return "The sender address to use.";
  }

  /**
   * Sets the recipients.
   *
   * @param value	the recipients
   */
  public void setRecipients(EmailAddress[] value) {
    m_Recipients = value;
    reset();
  }

  /**
   * Returns the recipients.
   *
   * @return 		the recipients
   */
  public EmailAddress[] getRecipients() {
    return m_Recipients;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String recipientsTipText() {
    return "The recipients to send the email to.";
  }

  /**
   * Sets the CC recipients.
   *
   * @param value	the recipients
   */
  public void setCC(EmailAddress[] value) {
    m_CC = value;
    reset();
  }

  /**
   * Returns the CC recipients.
   *
   * @return 		the recipients
   */
  public EmailAddress[] getCC() {
    return m_CC;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String CCTipText() {
    return "The CC recipients to send the email to.";
  }

  /**
   * Sets the BCC recipients.
   *
   * @param value	the recipients
   */
  public void setBCC(EmailAddress[] value) {
    m_BCC = value;
    reset();
  }

  /**
   * Returns the BCC recipients.
   *
   * @return 		the recipients
   */
  public EmailAddress[] getBCC() {
    return m_BCC;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String BCCTipText() {
    return "The BCC recipients to send the email to.";
  }

  /**
   * Sets the subject.
   *
   * @param value	the subject
   */
  public void setSubject(String value) {
    m_Subject = value;
    reset();
  }

  /**
   * Returns the subject.
   *
   * @return 		the subject
   */
  public String getSubject() {
    return m_Subject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String subjectTipText() {
    return "The subject of the email, can contain variables.";
  }

  /**
   * Sets the body of the email.
   *
   * @param value	the body
   */
  public void setSignature(BaseText value) {
    m_Signature = value;
    reset();
  }

  /**
   * Returns the body of the email.
   *
   * @return 		the body
   */
  public BaseText getSignature() {
    return m_Signature;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String signatureTipText() {
    return
        "The signature of the email, gets separated by an extra line "
      + "consisting of '" + EmailHelper.SIGNATURE_SEPARATOR + "', can contain variables.";
  }

  /**
   * Sets the object for sending emails.
   *
   * @param value	the object
   */
  public void setSendEmail(AbstractSendEmail value) {
    m_SendEmail = value;
    reset();
  }

  /**
   * Returns the object for sending emails.
   *
   * @return 		the object
   */
  public AbstractSendEmail getSendEmail() {
    return m_SendEmail;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String sendEmailTipText() {
    return "The engine for sending the emails.";
  }

  /**
   * Hook method before attempting to send the message.
   *
   * @param msg		the message to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(String msg) {
    String	result;

    result = super.check(msg);

    if (result == null) {
      if (!EmailHelper.isEnabled())
	result = "No email support enabled, check email setup!";
    }

    return result;
  }

  /**
   * Initializes the SMTP session if required.
   *
   * @throws Exception		if initialization fails
   */
  protected void initSession() throws Exception {
    SMTPConnection conn;

    if (m_SendEmail.requiresSmtpSessionInitialization()) {
      conn = (SMTPConnection) ActorUtils.findClosestType(m_FlowContext, SMTPConnection.class, true);
      if (conn != null)
	conn.initializeSmtpSession(m_SendEmail);
      else
	m_SendEmail.initializeSmtpSession(
	    EmailHelper.getSmtpServer(),
	    EmailHelper.getSmtpPort(),
	    EmailHelper.getSmtpStartTLS(),
	    EmailHelper.getSmtpUseSSL(),
	    EmailHelper.getSmtpTimeout(),
	    EmailHelper.getSmtpRequiresAuthentication(),
	    EmailHelper.getSmtpUser(),
	    EmailHelper.getSmtpPassword());
    }
  }

  /**
   * Sends the notification.
   *
   * @param msg		the message to send
   * @return		null if successfully sent, otherwise error message
   */
  @Override
  protected String doSendNotification(String msg) {
    String			result;
    String			subject;
    adams.core.net.Email	email;

    result = null;

    // create email
    subject = m_FlowContext.getVariables().expand(m_Subject);
    email = null;
    try {
      email = new adams.core.net.Email(
	  m_Sender,
	  m_Recipients,
	  m_CC,
	  m_BCC,
	  subject,
	  msg,
	  null);
      if (isLoggingEnabled())
	getLogger().info(email.toString());
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to generate email!", e);
    }

    // send email
    if (result == null) {
      try {
	initSession();
	if (!m_SendEmail.sendMail(email))
	  result = "Failed to send email, check console output!";
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to send email: ", e);
      }
    }

    return result;
  }
}
