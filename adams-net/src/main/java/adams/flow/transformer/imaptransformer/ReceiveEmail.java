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

package adams.flow.transformer.imaptransformer;

import adams.core.MessageCollection;
import adams.flow.standalone.IMAPConnection;
import jodd.mail.EmailFilter;
import jodd.mail.ReceivedEmail;

/**
 * Loads the email from the specified folder using its message ID.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReceiveEmail
  extends AbstractIMAPFolderOperation<String, ReceivedEmail>{

  private static final long serialVersionUID = 1596186764308013790L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads the email from the specified folder using its message ID.";
  }

  /**
   * Returns the type of input the operation accepts.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return String.class;
  }

  /**
   * Returns the type of output the operation generates.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return ReceivedEmail.class;
  }

  /**
   * Executes the operation and returns the generated output.
   *
   * @param conn   the connection to use
   * @param input  the input data
   * @param errors for collecting errors
   * @return the generated output, null in case of error or failed check
   */
  @Override
  protected ReceivedEmail doExecute(IMAPConnection conn, String input, MessageCollection errors) {
    ReceivedEmail	result;
    ReceivedEmail[] 	emails;
    EmailFilter 	emailFilter;

    result = null;

    conn.getImapSession().useFolder(m_Folder);

    emailFilter = new EmailFilter();
    emailFilter.messageId(input);

    emails = conn.getImapSession().receiveEmail(emailFilter);
    if (emails.length == 1)
      result = emails[0];
    else
      errors.add("Failed to retrieve message '" + input + "' from folder '" + m_Folder + "'!");

    return result;
  }
}
