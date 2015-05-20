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
 * SendEmail.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.net.AbstractSendEmail;
import adams.core.net.EmailHelper;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.SMTPConnection;

import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Actor for sending emails. The (optional) attachments are taken from the input.<br>
 * Variables in 'subject', 'body' and 'signature' are automatically replaced whenever the actor is executed.<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: Email
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
 * <pre>-send-email &lt;adams.core.net.AbstractSendEmail&gt; (property: sendEmail)
 * &nbsp;&nbsp;&nbsp;The engine for sending the emails.
 * &nbsp;&nbsp;&nbsp;default: adams.core.net.JavaMailSendEmail
 * </pre>
 * 
 * <pre>-queue (property: queue)
 * &nbsp;&nbsp;&nbsp;Whether to queue the emails rather than waiting for them to be sent.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SendEmail
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = -5959868605503747649L;
  
  /** for sending the emails. */
  protected AbstractSendEmail m_SendEmail;
  
  /** whether to queue the emails rather than waiting for sending to finish. */
  protected boolean m_Queue;

  /** the emails still to send. */
  protected List<SwingWorker> m_Sending;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor for sending emails.\n"
      + (EmailHelper.isEnabled() ? "" : "Email support not enabled, check email setup!");
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "send-email", "sendEmail",
	    EmailHelper.getDefaultSendEmail());

    m_OptionManager.add(
	    "queue", "queue",
	    false);
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Sending = new ArrayList<SwingWorker>();
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

    result = QuickInfoHelper.toString(this, "sendEmail", m_SendEmail.getClass(), "send: ");;

    value = QuickInfoHelper.toString(this, "queue", m_Queue, "queue", ",");
    if (value != null)
      result += value;
    
    return result;
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
   * Sets whether to queue the emails rather than waiting for each to be sent.
   *
   * @param value	true if to queue
   */
  public void setQueue(boolean value) {
    m_Queue = value;
    reset();
  }

  /**
   * Returns whether the emails are queue rather than being waited on.
   *
   * @return 		true if to queue
   */
  public boolean getQueue() {
    return m_Queue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String queueTipText() {
    return "Whether to queue the emails rather than waiting for them to be sent.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.lang.String[].class, java.io.File.class, java.io.File[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
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

    return result;
  }

  /**
   * Initializes the SMTP session if required.
   * 
   * @throws Exception		if initialization fails
   */
  protected void initSession() throws Exception {
    SMTPConnection	conn;
    
    if (m_SendEmail.requiresSmtpSessionInitialization()) {
      conn = (SMTPConnection) ActorUtils.findClosestType(this, SMTPConnection.class, true);
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SwingWorker			run;
    final adams.core.net.Email	email;

    result = null;

    email = (adams.core.net.Email) m_InputToken.getPayload();

    if (isLoggingEnabled())
      getLogger().info(email.toString());

    if (m_Queue) {
      run = new SwingWorker() {
	@Override
	protected Object doInBackground() throws Exception {
	  try {
	    initSession();
	    if (!m_SendEmail.sendMail(email))
	      m_Self.handleError(m_Self, "email", "Failed to send email, check console output!");
	  }
	  catch (Exception e) {
	    Utils.handleException(m_Self, "Failed to send email: ", e);
	  }
	  return null;
	}
	@Override
	protected void done() {
	  m_Sending.remove(this);
	  super.done();
	}
      };
      m_Sending.add(run);
      if (isLoggingEnabled())
	getLogger().info("Queuing email, queue size: " + m_Sending.size());
      run.execute();
    }
    else {
      try {
	initSession();
	if (!m_SendEmail.sendMail(email))
	  result = "Failed to send email, check console output!";
      }
      catch (Exception e) {
	result = handleException("Failed to send email: ", e);
      }
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    m_Sending.clear();
    super.stopExecution();
  }
  
  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    while ((m_Sending.size() > 0) && !isStopped()) {
      try {
	synchronized(this) {
	  wait(100);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }
    
    m_SendEmail.cleanUp();
    
    super.wrapUp();
  }
}
