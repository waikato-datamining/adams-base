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
 * Email.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.mime.MediaType;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingObject;

/**
 * Container object for an email.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
  
  /**
   * Initializes the email.
   * 
   * @param from	the sender
   * @param to		the recipient
   * @param subject	the subject
   * @param body	the the content
   */
  public Email(EmailAddress from, EmailAddress to, String subject, String body) {
    this(from, new EmailAddress[]{to}, subject, body, new File[]{});
  }
  
  /**
   * Initializes the email.
   * 
   * @param from	the sender
   * @param to		the recipient
   * @param subject	the subject
   * @param body	the the content
   * @param attachments	the attachments
   */
  public Email(EmailAddress from, EmailAddress to, String subject, String body, File[] attachments) {
    this(from, new EmailAddress[]{to}, subject, body, attachments);
  }
  
  /**
   * Initializes the email.
   * 
   * @param from	the sender
   * @param to		the recipients
   * @param subject	the subject
   * @param body	the the content
   * @param attachments	the attachments
   */
  public Email(EmailAddress from, EmailAddress[] to, String subject, String body, File[] attachments) {
    this(from, to, new EmailAddress[]{}, new EmailAddress[]{}, subject, body, attachments);
  }
  
  /**
   * Initializes the email.
   * 
   * @param from	the sender
   * @param to		the recipients
   * @param cc		the CC recipients
   * @param bcc		the BCC recipients
   * @param subject	the subject
   * @param body	the the content
   * @param attachments	the attachments
   */
  public Email(EmailAddress from, EmailAddress[] to, EmailAddress[] cc, EmailAddress[] bcc, String subject, String body, File[] attachments) {
    super();
    
    if (to == null)
      to = new EmailAddress[0];
    if (cc == null)
      cc = new EmailAddress[0];
    if (bcc == null)
      bcc = new EmailAddress[0];
    if (subject.trim().length() == 0)
      subject = NO_SUBJECT;
    if (attachments == null)
      attachments = new File[0];
    
    if (from == null)
      throw new IllegalArgumentException("Sender cannot be null!");
    if (to.length + cc.length + bcc.length == 0)
      throw new IllegalArgumentException("At least one recipient must be specified (to, cc or bcc)!");
    for (int i = 0; i < attachments.length; i++) {
      if (!attachments[i].exists())
	throw new IllegalArgumentException("Attachment #" + (i+1) + " does not exist: " + attachments[i]);
    }
    
    m_From        = from;
    m_To          = to;
    m_CC          = cc;
    m_BCC         = bcc;
    m_Subject     = subject;
    m_Body        = body;
    m_Attachments = attachments;
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
   * 
   */
  public String toPlainText() {
    List<String>	list;
    String		boundary;
    MediaType		mime;
    String[]		lines;
    byte[]		content;
    
    boundary = EmailHelper.createBoundary();
    list  = new ArrayList<String>();
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

    if (getAttachments().length > 0) {
      for (File file: getAttachments()) {
	list.add("--" + boundary);
	mime = MimeTypeHelper.getMimeType(file);
	list.add("Content-Type: " + mime.toString() + "; name=\"" + file.getName() + "\"");
	list.add("Content-Disposition: attachment; filename=\"" + file.getName() + "\"");
	list.add("Content-Transfer-Encoding: base64");
	list.add("");
	content = FileUtils.loadFromBinaryFile(file);
	lines   = EmailHelper.breakUp(InternetHelper.encodeBase64(content), 76);
	for (String line: lines)
	  list.add(line);
      }
    }

    // finish message
    list.add("--" + boundary + "--");
    
    return Utils.flatten(list, "\n");
  }
}
