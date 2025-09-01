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
 * ReceiveEmail.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.imapsource;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.net.imap.Flag;
import adams.core.net.imap.PostReceptionAction;
import adams.flow.standalone.IMAPConnection;
import jodd.mail.EmailFilter;
import jodd.mail.ReceivedEmail;

import static jodd.mail.EmailFilter.filter;

/**
 * Receives emails according to the filters.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReceiveEmail
  extends AbstractIMAPFolderOperation<ReceivedEmail[]> {

  private static final long serialVersionUID = -2878098942673727975L;

  /** whether to received only envelopes. */
  protected boolean m_OnlyEnvelopes;

  /** the FROM filter. */
  protected String m_From;

  /** the TO filter. */
  protected String m_To;

  /** the SUBJECT filter. */
  protected String m_Subject;

  /** the flags to look for. */
  protected Flag[] m_Flags;

  /** the action to perform after receiving. */
  protected PostReceptionAction m_PostReceptionAction;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Receives emails according to the filters.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "only-envelopes", "onlyEnvelopes",
      false);

    m_OptionManager.add(
      "from", "from",
      "");

    m_OptionManager.add(
      "to", "to",
      "");

    m_OptionManager.add(
      "subject", "subject",
      "");

    m_OptionManager.add(
      "flag", "flags",
      new Flag[0]);

    m_OptionManager.add(
      "post-reception-action", "postReceptionAction",
      PostReceptionAction.NONE);
  }

  /**
   * Sets whether to only receive the envelopes or fully download them.
   *
   * @param value	true if to receive only the envelopes
   */
  public void setOnlyEnvelopes(boolean value) {
    m_OnlyEnvelopes = value;
    reset();
  }

  /**
   * Returns whether to only receive the envelopes or fully download them.
   *
   * @return 		true if to mark read
   */
  public boolean getOnlyEnvelopes() {
    return m_OnlyEnvelopes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String onlyEnvelopesTipText() {
    return "Whether to only receive the envelopes or fully download them.";
  }

  /**
   * Sets the FROM filter, ignored if empty.
   *
   * @param value	the filter value
   */
  public void setFrom(String value) {
    m_From = value;
    reset();
  }

  /**
   * Returns the FROM filter, ignored if empty.
   *
   * @return 		the filter value
   */
  public String getFrom() {
    return m_From;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String fromTipText() {
    return "Returns emails that match this author email.";
  }

  /**
   * Sets the TO filter, ignored if empty.
   *
   * @param value	the filter value
   */
  public void setTo(String value) {
    m_To = value;
    reset();
  }

  /**
   * Returns the TO filter, ignored if empty.
   *
   * @return 		the filter value
   */
  public String getTo() {
    return m_To;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String toTipText() {
    return "Returns emails that match this recipient email.";
  }

  /**
   * Sets the SUBJECT filter, ignored if empty.
   *
   * @param value	the filter value
   */
  public void setSubject(String value) {
    m_Subject = value;
    reset();
  }

  /**
   * Returns the SUBJECT filter, ignored if empty.
   *
   * @return 		the filter value
   */
  public String getSubject() {
    return m_Subject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String subjectTipText() {
    return "Returns emails that match this subject.";
  }

  /**
   * Sets the flags to look for.
   *
   * @param value	the flags
   */
  public void setFlags(Flag[] value) {
    if (Flag.isValid(value)) {
      m_Flags = value;
      reset();
    }
  }

  /**
   * Returns the flags to look for.
   *
   * @return 		the flags
   */
  public Flag[] getFlags() {
    return m_Flags;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String flagsTipText() {
    return "The flags that the messages must have.";
  }

  /**
   * Sets the action to perform after receiving the full messages (does not apply to envelopes).
   *
   * @param value	the action
   */
  public void setPostReceptionAction(PostReceptionAction value) {
    m_PostReceptionAction = value;
    reset();
  }

  /**
   * Returns the action to perform after receiving the full messages (does not apply to envelopes).
   *
   * @return 		the action
   */
  public PostReceptionAction getPostReceptionAction() {
    return m_PostReceptionAction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String postReceptionActionTipText() {
    return "The action to perform after receiving the full messages (does not apply to envelopes).";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "onlyEnvelopes", (m_OnlyEnvelopes ? "envelopes-only" : "full messages"), ", ");
    result += QuickInfoHelper.toString(this, "from", (m_From.isEmpty() ? "-everyone-" : m_From), ", from: ");
    result += QuickInfoHelper.toString(this, "to", (m_To.isEmpty() ? "-everyone-" : m_To), ", to: ");
    result += QuickInfoHelper.toString(this, "subject", (m_Subject.isEmpty() ? "-any-" : m_Subject), ", subject: ");
    result += QuickInfoHelper.toString(this, "flags", m_Flags, ", flags: ");
    result += QuickInfoHelper.toString(this, "postReceptionAction", m_PostReceptionAction, ", post-reception: ");

    return result;
  }

  /**
   * Returns the type of output the operation generates.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return ReceivedEmail[].class;
  }

  /**
   * Executes the operation and returns the generated output.
   *
   * @param conn   the connection to use
   * @param errors for collecting errors
   * @return the generated output, null in case of error or failed check
   */
  @Override
  protected ReceivedEmail[] doExecute(IMAPConnection conn, MessageCollection errors) {
    EmailFilter		emailFilter;

    conn.getImapSession().useFolder(m_Folder);

    emailFilter = filter();
    if (!m_From.isEmpty())
      emailFilter.from(m_From);
    if (!m_To.isEmpty())
      emailFilter.to(m_To);
    if (!m_Subject.isEmpty())
      emailFilter.subject(m_Subject);

    Flag.updateFilter(m_Flags, emailFilter);

    if (m_OnlyEnvelopes) {
      return conn.getImapSession().receiveEnvelopes(emailFilter);
    }
    else {
      switch (m_PostReceptionAction) {
	case NONE:
	  return conn.getImapSession().receiveEmail(emailFilter);
	case MARK_SEEN:
	  return conn.getImapSession().receiveEmailAndMarkSeen(emailFilter);
	case DELETE:
	  return conn.getImapSession().receiveEmailAndDelete(emailFilter);
	default:
	  throw new IllegalStateException("Unhandled post-reception action: " + m_PostReceptionAction);
      }
    }
  }
}
