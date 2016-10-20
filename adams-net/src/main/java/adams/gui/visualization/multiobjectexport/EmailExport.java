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
 * EmailExport.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.multiobjectexport;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.net.AbstractSendEmail;
import adams.core.net.Email;
import adams.core.net.EmailAddress;
import adams.core.net.EmailHelper;
import adams.gui.visualization.debug.objectexport.AbstractObjectExporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends the outputs via email.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailExport
  extends AbstractMultiObjectExportWithPreferredExtensions {

  private static final long serialVersionUID = 9186664398391763175L;

  /** the prefix for the files. */
  protected String m_Prefix;

  /** the sender. */
  protected EmailAddress m_Sender;

  /** the recipients. */
  protected EmailAddress[] m_Recipients;

  /** the subject. */
  protected String m_Subject;

  /** the body. */
  protected BaseText m_Body;

  /** for sending the emails. */
  protected AbstractSendEmail m_SendEmail;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends the outputs via email.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      "");

    m_OptionManager.add(
      "sender", "sender",
      new EmailAddress(EmailHelper.getDefaultFromAddress()));

    m_OptionManager.add(
      "recipient", "recipients",
      new EmailAddress[0]);

    m_OptionManager.add(
      "subject", "subject",
      "");

    m_OptionManager.add(
      "body", "body",
      new BaseText());

    m_OptionManager.add(
      "send-email", "sendEmail",
      EmailHelper.getDefaultSendEmail());
  }

  /**
   * Sets the optional prefix for the file names.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the optional prefix for the file names.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The optional prefix for the files (<outputdir>/<prefix><name>.<ext>).";
  }

  /**
   * Sets the email address of the sender.
   *
   * @param value	the address
   */
  public void setSender(EmailAddress value) {
    m_Sender = value;
    reset();
  }

  /**
   * Returns the email address of the sender.
   *
   * @return		the address
   */
  public EmailAddress getSender() {
    return m_Sender;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String senderTipText() {
    return "The address of the sender.";
  }

  /**
   * Sets the email addresses to send the output to.
   *
   * @param value	the addresses
   */
  public void setRecipients(EmailAddress[] value) {
    m_Recipients = value;
    reset();
  }

  /**
   * Returns the email addresses to send the output to.
   *
   * @return		the addresses
   */
  public EmailAddress[] getRecipients() {
    return m_Recipients;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String recipientsTipText() {
    return "The addresses to the send the generated output to.";
  }

  /**
   * Sets the subject for the email.
   *
   * @param value	the subject
   */
  public void setSubject(String value) {
    m_Subject = value;
    reset();
  }

  /**
   * Returns the subject for the email.
   *
   * @return		the subject
   */
  public String getSubject() {
    return m_Subject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String subjectTipText() {
    return "The subject for email.";
  }

  /**
   * Sets the body of the email.
   *
   * @param value	the body
   */
  public void setBody(BaseText value) {
    m_Body = value;
    reset();
  }

  /**
   * Returns the body of the email.
   *
   * @return		the body
   */
  public BaseText getBody() {
    return m_Body;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bodyTipText() {
    return "The body of email.";
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
   * Performs the actual export of the objects using the given names.
   *
   * @param names	the names for the objects
   * @param objects	the objects
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExport(String[] names, Object[] objects) {
    MessageCollection 		errors;
    AbstractObjectExporter 	exporter;
    int				i;
    String			ext;
    PlaceholderFile 		file;
    List<File> 			files;
    String			msg;
    File			outputDir;
    Email			email;

    for (i = 0; i < names.length; i++)
      names[i] = FileUtils.createFilename(m_Prefix + names[i], "");
    names  = disambiguateNames(names);
    errors = new MessageCollection();

    // temp output dir
    outputDir = TempUtils.createTempFile("emailexport", "");
    if (!outputDir.mkdirs())
      errors.add("Failed to create temporary output directory: " + outputDir);

    files = new ArrayList<>();
    if (errors.isEmpty()) {
      for (i = 0; i < names.length; i++) {
	exporter = determineExporter(names[i], objects[i], errors);
	ext = determineExtension(exporter);
	file = new PlaceholderFile(outputDir.getAbsolutePath() + File.separator + names[i] + "." + ext);
	msg = exporter.export(objects[i], file);
	if (msg != null)
	  errors.add("Failed to find export '" + names[i] + "'/" + Utils.classToString(objects[i].getClass()) + "\n" + msg);
	else
	  files.add(file);
      }
    }
    
    // create and send email
    if (errors.isEmpty()) {
      email = new Email(m_Sender, m_Recipients, m_Subject, m_Body.getValue(), files.toArray(new File[files.size()]));
      try {
	if (m_SendEmail.requiresSmtpSessionInitialization()) {
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
	if (!m_SendEmail.sendMail(email))
	  errors.add("Failed to send email!");
      }
      catch (Exception e) {
	errors.add("Failed to send email!", e);
      }
    }

    if (outputDir.exists())
      FileUtils.delete(outputDir);

    if (errors.isEmpty())
      return null;
    else
      return errors.toString();
  }
}
