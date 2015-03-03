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
 * ApacheSendEmail.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Apache Software Foundation (original SMTPMail example)
 */
package adams.core.net;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SMTPSClient;
import org.apache.commons.net.smtp.SimpleSMTPHeader;
import org.apache.tika.mime.MediaType;

import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BasePassword;
import adams.core.io.FileUtils;

/**
 * Uses Apache commons-net {@link SMTPClient} or {@link SMTPSClient} for 
 * sending emails.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright ="Apache Software Foundation",
    license = License.APACHE2,
    url = "http://commons.apache.org/proper/commons-net/examples/mail/SMTPMail.java",
    note = "Code adapted from SMTPMail"
  )
public class ApacheSendEmail
  extends AbstractSendEmail {

  /** for serialization. */
  private static final long serialVersionUID = 4065886204614191616L;
  
  /** the SMTP client. */
  protected transient SMTPClient m_Client;

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
    return "Uses the Apache SMTPClient/SMTPSClient to send emails.";
  }

  /**
   * Returns whether the SMTP session needs to be initialized.
   * 
   * @return		true if the SMTP session needs to be initialized
   */
  @Override
  public boolean requiresSmtpSessionInitialization() {
    return (m_Client == null);
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
    
    // TODO SSL?
    
    if (m_UseTLS) {
      if (m_RequiresAuth)
	m_Client = new AuthenticatingSMTPClient();
      else
	m_Client = new SMTPSClient();
    }
    else {
      m_Client = new SMTPClient();
    }
    m_Client.setConnectTimeout(m_Timeout);
    if (isLoggingEnabled())
      m_Client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
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
    SimpleSMTPHeader	header;
    int			i;
    Writer		writer;
    String		boundary;
    MediaType		mime;
    byte[]		content;
    String[]		lines;
    
    if (m_Client == null)
      throw new IllegalStateException("SMTP session not initialized!");

    // header
    header = new SimpleSMTPHeader(email.getFrom().getValue(), Utils.flatten(email.getTo(), ", "), email.getSubject());
    for (i = 0; i < email.getCC().length; i++)
      header.addCC(email.getCC()[i].getValue());

    // create boundary string
    boundary = EmailHelper.createBoundary();
    header.addHeaderField("Content-Type", "multipart/mixed; boundary=" + boundary);
    
    // connect
    m_Client.connect(m_Server, m_Port);
    if (!SMTPReply.isPositiveCompletion(m_Client.getReplyCode())) {
      m_Client.disconnect();
      getLogger().severe("SMTP server " + m_Server + ":" + m_Port + " refused connection: " + m_Client.getReplyCode());
      return false;
    }
    
    // login
    if (!m_Client.login()) {
      m_Client.disconnect();
      getLogger().severe("Failed to login to SMTP server " + m_Server + ":" + m_Port + "!");
      return false;
    }

    // TLS?
    if (m_UseTLS) {
      if (!((SMTPSClient) m_Client).execTLS()) {
	m_Client.logout();
	m_Client.disconnect();
	getLogger().severe("SMTP server " + m_Server + ":" + m_Port + " failed to start TLS!");
	return false;
      }
    }
    
    // authentication?
    if (m_RequiresAuth) {
      if (!((AuthenticatingSMTPClient) m_Client).auth(AuthenticatingSMTPClient.AUTH_METHOD.PLAIN, m_User, m_Password.getValue())) {
	m_Client.logout();
	m_Client.disconnect();
	getLogger().severe("Failed to authenticate: user=" + m_User + ", pw=" + m_Password.getMaskedValue());
	return false;
      }
    }
    
    // fill in recipients
    m_Client.setSender(email.getFrom().stringValue());
    for (i = 0; i < email.getTo().length; i++)
      m_Client.addRecipient(email.getTo()[i].getValue());
    for (i = 0; i < email.getCC().length; i++)
      m_Client.addRecipient(email.getCC()[i].strippedValue());
    for (i = 0; i < email.getBCC().length; i++)
      m_Client.addRecipient(email.getBCC()[i].stringValue());

    // start message
    writer = m_Client.sendMessageData();
    if (writer == null) {
      m_Client.logout();
      m_Client.disconnect();
      getLogger().severe("Cannot send data!");
      return false;
    }
    writer.write(header.toString());
    
    // body
    writer.write("--" + boundary + "\n");
    writer.write("Content-Type: text/plain; charset=ISO-8859-1\n");
    writer.write("\n");
    writer.write(email.getBody());
    writer.write("\n");
    writer.write("\n");
    
    // attachements
    if (email.getAttachments().length > 0) {
      for (File file: email.getAttachments()) {
	writer.write("--" + boundary + "\n");
	mime = MimeTypeHelper.getMimeType(file);
	writer.write("Content-Type: " + mime.toString() + "; name=\"" + file.getName() + "\"\n");
	writer.write("Content-Disposition: attachment; filename=\"" + file.getName() + "\"\n");
	writer.write("Content-Transfer-Encoding: base64\n");
	writer.write("\n");
	content = FileUtils.loadFromBinaryFile(file);
	lines   = EmailHelper.breakUp(InternetHelper.encodeBase64(content), 76);
	for (String line: lines)
	  writer.write(line + "\n");
      }
    }

    // finish message
    writer.write("--" + boundary + "--\n");
    writer.close();
    
    if (!m_Client.completePendingCommand()) {
      m_Client.logout();
      m_Client.disconnect();
      getLogger().severe("Failed to complete pending command!");
      return false;
    }
    
    m_Client.logout();
    m_Client.disconnect();
    
    return true;
  }
}
