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
 * JavaMailSendEmail.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import java.io.File;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import adams.core.base.BasePassword;

/**
 * Uses JavaMail for sending emails.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see EmailHelper#initializeSmtpSession(String, int, boolean, int, boolean, String, BasePassword)
 * @see EmailHelper#sendMail(Session, String, String[], String[], String[], String, String, File[])
 */
public class JavaMailSendEmail
  extends AbstractSendEmail {

  /** for serialization. */
  private static final long serialVersionUID = 4065886204614191616L;

  /** the system-wide property for the SMTP host. */
  public final static String KEY_SMTPHOST = "mail.smtp.host";

  /** the system-wide property for the SMTP port. */
  public final static String KEY_SMTPPORT = "mail.smtp.port";

  /** the system-wide property for StartTLS. */
  public final static String KEY_STARTTLS = "mail.smtp.starttls.enable";

  /** the system-wide property for SMTP Auth. */
  public final static String KEY_SMTPAUTH = "mail.smtp.auth";

  /** the system-wide property for SMTP timeout. */
  public final static String KEY_SMTPTIMEOUT = "mail.smtp.timeout";
  
  /** the SMTP session object. */
  protected transient Session m_Session;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses JavaMail to send emails.";
  }

  /**
   * Returns whether the SMTP session needs to be initialized.
   * 
   * @return		true if the SMTP session needs to be initialized
   */
  @Override
  public boolean requiresSmtpSessionInitialization() {
    return (m_Session == null);
  }

  /**
   * Initializes the SMTP session.
   *
   * @param server		the SMTP server
   * @param port		the SMTP port
   * @param useTLS		whether to use TLS
   * @param timeout		the timeout
   * @param requiresAuth	whether authentication is required
   * @param user		the SMTP user
   * @param pw			the SMTP password
   * @return			the session
   * @throws Exception		if initialization fails
   */
  @Override
  public void initializeSmtpSession(String server, int port, boolean useTLS, int timeout, boolean requiresAuth, final String user, final BasePassword pw) throws Exception {
    java.util.Properties 	props;

    props = (java.util.Properties) System.getProperties().clone();
    props.setProperty(KEY_SMTPHOST,    server);
    props.setProperty(KEY_SMTPPORT,    "" + port);
    props.setProperty(KEY_STARTTLS,    "" + useTLS);
    props.setProperty(KEY_SMTPAUTH,    "" + requiresAuth);
    props.setProperty(KEY_SMTPTIMEOUT, "" + timeout);

    if (requiresAuth) {
      m_Session = Session.getInstance(
	  props,
	  new Authenticator() {
	    @Override
	    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
	      return new PasswordAuthentication(user, pw.getValue());
	    }
	  });
    }
    else {
      m_Session = Session.getInstance(props);
    }

    m_Session.setDebug(isLoggingEnabled());
  }

  /**
   * Creates a new email message.
   *
   * @param fromAddress		the sender
   * @param toAddress		the recipients, can be null
   * @param ccAddress		the CC recipients, can be null
   * @param bccAddress		the BCC recipients, can be null
   * @param subject		the subject
   * @return			the email message
   * @throws AddressException	in case of invalid internet addresses
   * @throws MessagingException	in case of a messaging problem
   */
  protected MimeMessage newMessage(EmailAddress fromAddress, EmailAddress[] toAddress, EmailAddress[] ccAddress, EmailAddress[] bccAddress, String subject) throws AddressException, MessagingException {
    MimeMessage	message;
    int		i;
    boolean	noRecipient;

    message = new MimeMessage(m_Session);
    message.setFrom(new InternetAddress(fromAddress.getValue()));

    noRecipient = true;
    if (toAddress != null) {
      noRecipient = noRecipient && (toAddress.length == 0);
      for (i = 0; i < toAddress.length; i++)
	message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress[i].getValue()));
    }
    if (ccAddress != null) {
      noRecipient = noRecipient && (ccAddress.length == 0);
      for (i = 0; i < ccAddress.length; i++)
	message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccAddress[i].getValue()));
    }
    if (bccAddress != null) {
      noRecipient = noRecipient && (bccAddress.length == 0);
      for (i = 0; i < bccAddress.length; i++)
	message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccAddress[i].getValue()));
    }
    if (noRecipient)
      throw new MessagingException("No recipients specified!");

    message.setSubject(subject);

    return message;
  }
  /**
   * Sends an email.
   *
   * @param email	the email to send
   * @return		true if successfully sent
   * @throws Exception	in case of invalid internet addresses or messaging problem
   */
  @Override
  public boolean sendMail(Email email) throws Exception {
    MimeMessage 		message;
    BodyPart 			messageBodyPart;
    Multipart 			multipart;
    int				i;

    if (m_Session == null)
      throw new IllegalStateException("SMTP session not initialized!");

    // setup message
    message   = newMessage(email.getFrom(), email.getTo(), email.getCC(), email.getBCC(), email.getSubject());
    multipart = new MimeMultipart();

    // body
    messageBodyPart = new MimeBodyPart();
    messageBodyPart.setText(email.getBody());
    multipart.addBodyPart(messageBodyPart);

    // attachments
    for (i = 0; i < email.getAttachments().length; i++) {
      messageBodyPart = new MimeBodyPart();
      messageBodyPart.setDataHandler(new DataHandler(email.getAttachments()[i].toURI().toURL()));
      messageBodyPart.setFileName(email.getAttachments()[i].getName());
      multipart.addBodyPart(messageBodyPart);
    }

    // set content
    message.setContent(multipart);

    // send
    Transport.send(message);

    return true;
  }
}
