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
 * EmlEmailFileReader.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import jodd.mail.EmailMessage;
import jodd.mail.EmailUtil;
import jodd.mail.ReceivedEmail;
import adams.core.net.Email;
import adams.core.net.EmailAddress;

/**
 <!-- globalinfo-start -->
 * Reads emails stored in EML files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read the email from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8291 $
 */
public class EmlEmailFileReader
  extends AbstractEmailFileReader {

  /** for serialization. */
  private static final long serialVersionUID = -341050738394654936L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads emails stored in EML files.";
  }
  
  /**
   * Returns the description of the file format.
   * 
   * @return		the description
   */
  public String getFormatDescription() {
    return "EML files";
  }
  
  /**
   * Returns the extension(s) of the file format (without dot).
   * 
   * @return		the extensions (no dot!)
   */
  public String[] getFormatExtensions() {
    return new String[]{"eml"};
  }
  
  /**
   * Performs the actual reading.
   * 
   * @return		the email that was read, null in case of error
   */
  @Override
  protected Email doRead() {
    Email		result;
    ReceivedEmail	email;
    List<EmailMessage>	msgs;
    StringBuilder	body;
    int			i;

    result = null;

    try {
      email = EmailUtil.parseEML(m_Input.getAbsoluteFile());
      msgs  = email.getAllMessages();
      body  = new StringBuilder();
      for (i = 0; i < msgs.size(); i++) {
	if (msgs.size() > 1)
	  body.append("---Message #" + (i+1) + " " + msgs.get(i).getMimeType() + " " + msgs.get(i).getEncoding() + "\n");
	body.append(msgs.get(i).getContent());
	body.append("\n");
      }
      result = new Email(
	  new EmailAddress(email.getFrom()), 
	  (EmailAddress[]) EmailAddress.toObjectArray(email.getTo(), EmailAddress.class),
	  (EmailAddress[]) EmailAddress.toObjectArray(email.getCc(), EmailAddress.class),
	  (EmailAddress[]) EmailAddress.toObjectArray(email.getBcc(), EmailAddress.class),
	  email.getSubject(),
	  body.toString(),
	  new File[0]);
      
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read: " + m_Input, e);
    }
    
    return result;
  }
}
