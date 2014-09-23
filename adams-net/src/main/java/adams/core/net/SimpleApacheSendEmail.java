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
 * SimpleApacheSendEmail.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Apache Software Foundation (original SMTPMail example)
 */
package adams.core.net;

import java.util.logging.Level;

import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BasePassword;

/**
 * Uses Apache commons-email for sending emails.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7482 $
 */
@MixedCopyright(
    copyright ="Apache Software Foundation",
    license = License.APACHE2,
    url = "http://commons.apache.org/proper/commons-email/userguide.html"
  )
public class SimpleApacheSendEmail
  extends AbstractSendEmail {

  /** for serialization. */
  private static final long serialVersionUID = 4065886204614191616L;

  /** the server to connect to. */
  protected String m_Server;
  
  /** the server port. */
  protected int m_Port;
  
  /** whether to use TLS. */
  protected boolean m_UseTLS;
  
  /** whether to use SSL. */
  protected boolean m_UseSSL;

  /** the timeout for the server. */
  protected int m_Timeout;
  
  /** whether authentication is required. */
  protected boolean m_RequiresAuth;
  
  /** the user to use for authenticating. */
  protected String m_User;
  
  /** the password to use for authentication. */
  protected BasePassword m_Password;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the Apache commons-email library to send emails.";
  }

  /**
   * Returns whether the SMTP session needs to be initialized.
   * 
   * @return		true if the SMTP session needs to be initialized
   */
  @Override
  public boolean requiresSmtpSessionInitialization() {
    return true;
  }

  /**
   * Initializes the SMTP session.
   *
   * @param server		the SMTP server
   * @param port		the SMTP port
   * @param useTLS		whether to use TLS
   * @param useSSL		whether to use SSL
   * @param timeout		the timeout
   * @param requiresAuth	whether authentication is required
   * @param user		the SMTP user
   * @param pw			the SMTP password
   * @return			the session
   * @throws Exception		if initialization fails
   */
  @Override
  public void initializeSmtpSession(String server, int port, boolean useTLS, boolean useSSL, int timeout, boolean requiresAuth, String user, BasePassword pw) throws Exception {
    m_Server       = server;
    m_Port         = port;
    m_UseTLS       = useTLS;
    m_UseSSL       = useSSL;
    m_Timeout      = timeout;
    m_RequiresAuth = requiresAuth;
    m_User         = user;
    m_Password     = pw;
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
    org.apache.commons.mail.Email	mail;
    String				id;
    
    if (email.getAttachments().length > 0)
      mail = new MultiPartEmail();
    else
      mail = new SimpleEmail();
    mail.setFrom(email.getFrom().getValue());
    for (EmailAddress address: email.getTo())
      mail.addTo(address.getValue());
    for (EmailAddress address: email.getCC())
      mail.addCc(address.getValue());
    for (EmailAddress address: email.getBCC())
      mail.addBcc(address.getValue());
    mail.setSubject(email.getSubject());
    mail.setMsg(email.getBody());
    mail.setHostName(m_Server);
    mail.setSmtpPort(m_Port);
    mail.setStartTLSEnabled(m_UseTLS);
    mail.setSSLOnConnect(m_UseSSL);
    if (m_RequiresAuth)
      mail.setAuthentication(m_User, m_Password.getValue());
    mail.setSocketTimeout(m_Timeout);
    try {
      id = mail.send();
      if (isLoggingEnabled())
	getLogger().info("Message sent: " + id);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to send email: " + mail, e);
      return false;
    }

    return true;
  }
}
