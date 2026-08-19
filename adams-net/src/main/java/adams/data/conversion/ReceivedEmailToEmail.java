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
 * ReceivedEmailToEmail.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.Utils;
import adams.core.net.Email;
import adams.core.net.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.ReceivedEmail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns a jodd.mail.ReceivedEmail object into a adams.core.net.Email one.
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
public class ReceivedEmailToEmail
  extends AbstractConversion {

  private static final long serialVersionUID = -3743807199892836835L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a " + Utils.classToString(ReceivedEmail.class) + " object into a " + Utils.classToString(Email.class) + " one.";
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
    return Email.class;
  }

  /**
   * Turns the jodd email address arrays into string ones.
   *
   * @param addresses	the array to convert
   * @return		the converted array
   */
  protected String[] toString(jodd.mail.EmailAddress[] addresses) {
    String[]	result;
    int		i;

    result = new String[addresses.length];
    for (i = 0; i < addresses.length; i++)
      result[i] = addresses[i].getEmail();

    return result;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    Email		result;
    ReceivedEmail	email;
    List<EmailMessage> msgs;
    StringBuilder	body;
    int			i;
    Map<String,byte[]>	attachments;

    email = (ReceivedEmail) m_Input;
    msgs  = email.messages();
    body  = new StringBuilder();
    for (i = 0; i < msgs.size(); i++) {
      if (msgs.size() > 1)
	body.append("---Message #").append(i + 1).append(" ").append(msgs.get(i).getMimeType()).append(" ").append(msgs.get(i).getEncoding()).append("\n");
      body.append(msgs.get(i).getContent());
      body.append("\n");
    }

    result = new Email()
	       .from(email.from().getEmail())
	       .to((EmailAddress[]) EmailAddress.toObjectArray(toString(email.to()), EmailAddress.class))
	       .cc((EmailAddress[]) EmailAddress.toObjectArray(toString(email.cc()), EmailAddress.class))
	       .subject(email.subject())
	       .body(body.toString());

    attachments = new HashMap<>();
    for (EmailAttachment attachment: email.attachments())
      attachments.put(attachment.getName(), attachment.toByteArray());
    if (!attachments.isEmpty())
      result.inMemoryAttachments(attachments);

    return result;
  }
}
