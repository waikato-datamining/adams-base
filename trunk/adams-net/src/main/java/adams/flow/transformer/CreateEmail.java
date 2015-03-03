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
 * CreateEmail.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.net.EmailAddress;
import adams.core.net.EmailHelper;
import adams.flow.core.NullToken;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Actor for creating emails to be sent. The (optional) attachments are taken from the input.<br/>
 * Variables in 'subject', 'body' and 'signature' are automatically replaced whenever the actor is executed.<br/>
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: CreateEmail
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-sender &lt;adams.core.net.EmailAddress&gt; (property: sender)
 * &nbsp;&nbsp;&nbsp;The sender address to use.
 * </pre>
 * 
 * <pre>-recipient &lt;adams.core.net.EmailAddress&gt; [-recipient ...] (property: recipients)
 * &nbsp;&nbsp;&nbsp;The recipients to send the email to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-cc &lt;adams.core.net.EmailAddress&gt; [-cc ...] (property: CC)
 * &nbsp;&nbsp;&nbsp;The CC recipients to send the email to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-bcc &lt;adams.core.net.EmailAddress&gt; [-bcc ...] (property: BCC)
 * &nbsp;&nbsp;&nbsp;The BCC recipients to send the email to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-subject &lt;java.lang.String&gt; (property: subject)
 * &nbsp;&nbsp;&nbsp;The subject of the email.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-body &lt;adams.core.base.BaseText&gt; (property: body)
 * &nbsp;&nbsp;&nbsp;The body of the email.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-signature &lt;adams.core.base.BaseText&gt; (property: signature)
 * &nbsp;&nbsp;&nbsp;The signature of the email, gets separated by an extra line consisting of 
 * &nbsp;&nbsp;&nbsp;'--'.
 * &nbsp;&nbsp;&nbsp;default: Peter Reutemann, Dept. of Computer Science, University of Waikato, NZ\\nhttp:&#47;&#47;www.cms.waikato.ac.nz&#47;~fracpete&#47;          Ph. +64 (7) 858-5174
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CreateEmail
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5959868605503747649L;

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

  /** the body. */
  protected BaseText m_Body;

  /** the signature. */
  protected BaseText m_Signature;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor for creating emails to be sent. The (optional) attachments are taken from the input.\n"
      + "Variables in 'subject', 'body' and 'signature' are automatically replaced "
      + "whenever the actor is executed.\n"
      + (EmailHelper.isEnabled() ? "" : "Email support not enabled, check email setup!");
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
	    "body", "body",
	    new BaseText(""));

    m_OptionManager.add(
	    "signature", "signature",
	    new BaseText(Utils.unbackQuoteChars(EmailHelper.getDefaultSignature())));
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
    return "The subject of the email.";
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
   * @return 		the body
   */
  public BaseText getBody() {
    return m_Body;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String bodyTipText() {
    return "The body of the email.";
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
      + "consisting of '" + EmailHelper.SIGNATURE_SEPARATOR + "'.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.lang.String[].class, java.io.File.class, java.io.File[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, String[].class, File.class, File[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.core.net.Email.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{adams.core.net.Email.class};
  }
  
  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!EmailHelper.isEnabled())
	result = "No email support enabled, check email setup!";
    }

    if (result == null) {
      if (!QuickInfoHelper.hasVariable(this, "recipients") && (m_Recipients.length == 0))
	result = "At least one email recipient must be defined!";
    }

    return result;
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    PlaceholderFile[]		attachments;
    String[]			recipients;
    String[]			cc;
    String[]			bcc;
    int				i;
    String			subject;
    String			body;
    adams.core.net.Email	email;
    

    result = null;

    // get attachments (if any)
    if ((m_InputToken == null) || (m_InputToken instanceof NullToken)) {
      attachments = new PlaceholderFile[0];
    }
    else {
      attachments = FileUtils.toPlaceholderFileArray(m_InputToken.getPayload());
    }

    // recipients
    recipients = new String[m_Recipients.length];
    for (i = 0; i < m_Recipients.length; i++)
      recipients[i] = m_Recipients[i].stringValue();
    cc = new String[m_CC.length];
    for (i = 0; i < m_CC.length; i++)
      cc[i] = m_CC[i].stringValue();
    bcc = new String[m_BCC.length];
    for (i = 0; i < m_BCC.length; i++)
      bcc[i] = m_BCC[i].stringValue();

    // replace variables
    subject = getVariables().expand(m_Subject);
    body    = EmailHelper.combine(
	getVariables().expand(m_Body.getValue()), 
	getVariables().expand(m_Signature.getValue()));

    try {
      email = new adams.core.net.Email(
	  m_Sender, 
	  m_Recipients,
	  m_CC,
	  m_BCC,
	  subject,
	  body,
	  attachments);
      if (isLoggingEnabled())
	getLogger().info(email.toString());
      m_OutputToken = new Token(email);
    }
    catch (Exception e) {
      result = handleException("Failed to generate email!", e);
    }

    return result;
  }
}
