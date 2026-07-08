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
 * Copyright (C) 2013-2026 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingObject;
import org.apache.tika.mime.MediaType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container object for an email.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Email
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -8762979365076025189L;

  /** the default subject if none provided. */
  public final static String NO_SUBJECT = "(no subject)";
  
  /** the sender. */
  protected EmailAddress m_From;

  /** the recipients. */
  protected EmailAddress[] m_To;

  /** the CC recipients. */
  protected EmailAddress[] m_CC;

  /** the BCC recipients. */
  protected EmailAddress[] m_BCC;
  
  /** the subject. */
  protected String m_Subject;
  
  /** the body. */
  protected String m_Body;
  
  /** the attachments. */
  protected File[] m_Attachments;

  /** the in-memory attachments (filename -> byte array). */
  protected Map<String,byte[]> m_InMemoryAttachments;

  /**
   * Default constructor.
   */
  public Email() {
    super();

    m_From                = new EmailAddress(EmailHelper.getDefaultFromAddress());
    m_To                  = new EmailAddress[0];
    m_CC                  = new EmailAddress[0];
    m_BCC                 = new EmailAddress[0];
    m_Subject             = NO_SUBJECT;
    m_Attachments         = new File[0];
    m_InMemoryAttachments = new HashMap<>();
  }

  /**
   * Sets the FROM email address.
   *
   * @param from	the FROM address
   * @return		itself
   */
  public Email from(String from) {
    return from(new EmailAddress(from));
  }

  /**
   * Sets the FROM email address.
   *
   * @param from	the FROM address
   * @return		itself
   */
  public Email from(EmailAddress from) {
    if (from == null)
      throw new IllegalArgumentException("Sender cannot be null!");
    m_From = from;
    return this;
  }

  /**
   * Sets the TO email address.
   *
   * @param to		the address, can be null
   * @return		itself
   */
  public Email to(String to) {
    return to(new EmailAddress(to));
  }

  /**
   * Sets the TO email address.
   *
   * @param to		the address, can be null
   * @return		itself
   */
  public Email to(EmailAddress to) {
    if (to == null)
      return to(new EmailAddress[0]);
    else
      return to(new EmailAddress[]{to});
  }

  /**
   * Sets the TO email addresses.
   *
   * @param to		the addresses, can be null
   * @return		itself
   */
  public Email to(EmailAddress[] to) {
    if (to == null)
      to = new EmailAddress[0];
    m_To = to;
    return this;
  }

  /**
   * Sets the CC email address.
   *
   * @param cc		the address, can be null
   * @return		itself
   */
  public Email cc(EmailAddress cc) {
    if (cc == null)
      return cc(new EmailAddress[0]);
    else
      return cc(new EmailAddress[]{cc});
  }

  /**
   * Sets the CC email addresses.
   *
   * @param cc		the addresses, can be null
   * @return		itself
   */
  public Email cc(EmailAddress[] cc) {
    if (cc == null)
      cc = new EmailAddress[0];
    m_CC = cc;
    return this;
  }

  /**
   * Sets the BCC email address.
   *
   * @param bcc		the address, can be null
   * @return		itself
   */
  public Email bcc(EmailAddress bcc) {
    if (bcc == null)
      return bcc(new EmailAddress[0]);
    else
      return bcc(new EmailAddress[]{bcc});
  }

  /**
   * Sets the BCC email addresses.
   *
   * @param bcc		the BCC addresses, can be null
   * @return		itself
   */
  public Email bcc(EmailAddress[] bcc) {
    if (bcc == null)
      bcc = new EmailAddress[0];
    m_BCC = bcc;
    return this;
  }

  /**
   * Returns the sender.
   * 
   * @return		the sender
   */
  public EmailAddress getFrom() {
    return m_From;
  }

  /**
   * Sets the subject.
   *
   * @param subject	the subject to use
   * @return		itself
   */
  public Email subject(String subject) {
    if (subject.trim().isEmpty())
      subject = NO_SUBJECT;
    m_Subject = subject;
    return this;
  }

  /**
   * Sets the body.
   *
   * @param body	the body to use
   * @return		itself
   */
  public Email body(String body) {
    if (body == null)
      body = "";
    m_Body = body;
    return this;
  }

  /**
   * Sets the attachments.
   *
   * @param attachments	the attachments, can be null
   * @return		itself
   */
  public Email attachments(File[] attachments) {
    if (attachments == null)
      attachments = new File[0];
    for (int i = 0; i < attachments.length; i++) {
      if (!attachments[i].exists())
	throw new IllegalArgumentException("Attachment #" + (i+1) + " does not exist: " + attachments[i]);
    }
    m_Attachments = attachments;
    return this;
  }

  /**
   * Sets the in-memory attachments.
   *
   * @param attachments	the attachments, can be null
   * @return		itself
   */
  public Email inMemoryAttachments(Map<String,byte[]> attachments) {
    if (attachments == null)
      attachments = new HashMap<>();
    m_InMemoryAttachments.clear();
    m_InMemoryAttachments.putAll(attachments);
    return this;
  }

  /**
   * Returns the TO recipients.
   * 
   * @return		the recipients
   */
  public EmailAddress[] getTo() {
    return m_To;
  }
  
  /**
   * Returns the CC recipients.
   * 
   * @return		the recipients
   */
  public EmailAddress[] getCC() {
    return m_CC;
  }
  
  /**
   * Returns the BCC recipients.
   * 
   * @return		the recipients
   */
  public EmailAddress[] getBCC() {
    return m_BCC;
  }
  
  /**
   * Returns the attachments.
   * 
   * @return		the attachments
   */
  public File[] getAttachments() {
    return m_Attachments;
  }

  /**
   * Returns the in-memory attachments.
   *
   * @return		the attachments
   */
  public Map<String,byte[]> getInMemoryAttachments() {
    return m_InMemoryAttachments;
  }

  /**
   * Returns the subject.
   * 
   * @return		the subject
   */
  public String getSubject() {
    return m_Subject;
  }
  
  /**
   * Returns the body.
   * 
   * @return		the body
   */
  public String getBody() {
    return m_Body;
  }
  
  /**
   * Simple info string for debugging purposes.
   * 
   * @return		the generated string
   */
  @Override
  public String toString() {
    return 
	  "from=" + getFrom() + ", #to=" + getTo().length 
	+ ", #cc=" + getCC().length + ", #bcc=" + getBCC().length 
	+ ", #attachments=" + getAttachments().length
	+ ", subject=" + getSubject() + ", len(body)=" + getBody().length();
  }
  
  /**
   * Turns the email into a plain text string.
   *
   * @return		the generated string
   */
  public String toPlainText() {
    List<String>	list;
    String		boundary;
    MediaType		mime;
    String[]		lines;
    byte[]		content;
    
    boundary = HttpRequestHelper.createBoundary();
    list  = new ArrayList<>();
    list.add("MIME-Version: 1.0");
    list.add("Sender: " + getFrom().strippedValue());
    for (EmailAddress addr: getCC())
      list.add("CC: " + addr.getValue());
    for (EmailAddress addr: getBCC())
      list.add("BCC: " + addr.getValue());
    list.add("Subject: " + getSubject());
    list.add("From: " + getFrom().getValue());
    for (EmailAddress addr: getTo())
      list.add("To: " + addr.getValue());
    list.add("Content-Type: multipart/mixed; boundary=" + boundary);
    list.add("");
    list.add("--" + boundary);
    list.add("Content-Type: text/plain; charset=ISO-8859-1");
    list.add("");
    list.add(getBody());
    list.add("");
    list.add("");

    for (File file : getAttachments()) {
      list.add("--" + boundary);
      mime = MimeTypeHelper.getMimeType(file);
      list.add("Content-Type: " + mime.toString() + "; name=\"" + file.getName() + "\"");
      list.add("Content-Disposition: attachment; filename=\"" + file.getName() + "\"");
      list.add("Content-Transfer-Encoding: base64");
      list.add("");
      content = FileUtils.loadFromBinaryFile(file);
      lines = HttpRequestHelper.breakUp(InternetHelper.encodeBase64(content), 76);
      Collections.addAll(list, lines);
    }

    // finish message
    list.add("--" + boundary + "--");
    
    return Utils.flatten(list, "\n");
  }
}
