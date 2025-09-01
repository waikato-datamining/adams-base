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
 * ReceivedEmailToMap.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Utils;
import jodd.mail.EmailAddress;
import jodd.mail.ReceivedEmail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts the properties of a jodd.mail.ReceivedEmail to a map. Properties such as to&#47;reply-to can contain multiple addresses and are stored as Java list.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReceivedEmailToMap
  extends AbstractConversion {

  private static final long serialVersionUID = -7253109423685145161L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts the properties of a " + Utils.classToString(ReceivedEmail.class) + " to a map. "
	     + "Properties such as to/reply-to can contain multiple addresses and are stored as Java list.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return ReceivedEmail.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Map.class;
  }

  /**
   * Turns the email addresses into a list.
   *
   * @param addresses	the addresses to convert
   * @return		the generated list
   */
  protected List<String> toList(EmailAddress[] addresses) {
    List<String>	result;

    result = new ArrayList<>();
    for (EmailAddress address: addresses)
      result.add(address.getEmail());

    return result;
  }

  /**
   * Turns the priority value into a string representation.
   *
   * @param priority	the priority value
   * @return		the generated string
   */
  protected String priorityToStr(int priority) {
    switch (priority) {
      case ReceivedEmail.PRIORITY_HIGHEST:
	return "highest";
      case ReceivedEmail.PRIORITY_HIGH:
	return "high";
      case ReceivedEmail.PRIORITY_NORMAL:
	return "normal";
      case ReceivedEmail.PRIORITY_LOW:
	return "low";
      case ReceivedEmail.PRIORITY_LOWEST:
	return "lowest";
      default:
	return "unknown (" + priority + ")";
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    Map<String,Object>	result;
    ReceivedEmail	email;
    DateFormat		df;
    int			i;

    result = new HashMap<>();
    email  = (ReceivedEmail) m_Input;
    df     = DateUtils.getTimestampFormatter();
    result.put("message-id", email.messageId());
    result.put("from", email.from().getEmail());
    if (email.to().length > 0)
      result.put("to", toList(email.to()));
    if (email.replyTo().length > 0)
      result.put("reply-to", toList(email.replyTo()));
    result.put("subject", email.subject());
    result.put("subject-encoding", email.subjectEncoding());
    result.put("received-date", df.format(email.receivedDate()));
    result.put("sent-date", df.format(email.sentDate()));
    result.put("answered", "" + email.isAnswered());
    result.put("deleted", "" + email.isDeleted());
    result.put("draft", "" + email.isDraft());
    result.put("seen", "" + email.isSeen());
    result.put("recent", "" + email.isRecent());
    result.put("priority", "" + email.priority());
    result.put("priorityText", priorityToStr(email.priority()));
    for (i = 0; i < email.messages().size(); i++) {
      result.put("message-" + (i + 1) + "-content", email.messages().get(i).getContent());
      result.put("message-" + (i + 1) + "-encoding", email.messages().get(i).getEncoding());
      result.put("message-" + (i + 1) + "-mimetype", email.messages().get(i).getMimeType());
    }

    return result;
  }
}
