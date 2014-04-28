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
 * PropertiesEmailFileReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.File;
import java.util.logging.Level;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.core.net.Email;
import adams.core.net.EmailAddress;
import adams.core.option.OptionUtils;

/**
 <!-- globalinfo-start -->
 * Reads emails stored in properties files.
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
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read the email from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertiesEmailFileReader
  extends AbstractEmailFileReader {

  /** for serialization. */
  private static final long serialVersionUID = -2721293194938632889L;

  /** the key for the sender. */
  public final static String KEY_FROM = "From";

  /** the key for the recipients. */
  public final static String KEY_TO = "To";

  /** the key for the cc recipients. */
  public final static String KEY_CC = "CC";

  /** the key for the bcc recipients. */
  public final static String KEY_BCC = "BCC";

  /** the key for the subject. */
  public final static String KEY_SUBJECT = "Subject";

  /** the key for the body. */
  public final static String KEY_BODY = "Body";

  /** the key for the attachments. */
  public final static String KEY_ATTACHMENTS = "Attachments";
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads emails stored in properties files.";
  }
  
  /**
   * Returns the description of the file format.
   * 
   * @return		the description
   */
  public String getFormatDescription() {
    return "Email properties files";
  }
  
  /**
   * Returns the extension(s) of the file format (without dot).
   * 
   * @return		the extensions (no dot!)
   */
  public String[] getFormatExtensions() {
    return new String[]{"props", "properties"};
  }

  /**
   * Reads the email addresses from the specified property.
   * 
   * @param props	the properties to use
   * @param key		the property to read
   * @return		the generated email addresses
   * @throws Exception	in case the splitting fails
   */
  protected EmailAddress[] getAddresses(Properties props, String key) throws Exception {
    EmailAddress[]	result;
    String[]		list;
    int			i;

    list   = OptionUtils.splitOptions(props.getProperty(key, ""));
    result = new EmailAddress[list.length];
    
    for (i = 0; i < list.length; i++)
      result[i] = new EmailAddress(list[i]);
    
    return result;
  }

  /**
   * Reads the files from the specified property.
   * 
   * @param props	the properties to use
   * @param key		the property to read
   * @return		the generated files
   * @throws Exception	in case the splitting fails
   */
  protected File[] getFiles(Properties props, String key) throws Exception {
    File[]	result;
    String[]	list;
    int		i;

    list   = OptionUtils.splitOptions(props.getProperty(key, ""));
    result = new File[list.length];
    
    for (i = 0; i < list.length; i++)
      result[i] = new PlaceholderFile(list[i]).getAbsoluteFile();
    
    return result;
  }
  
  /**
   * Performs the actual reading.
   * 
   * @return		the email that was read, null in case of error
   */
  @Override
  protected Email doRead() {
    Email		result;
    Properties		props;
    EmailAddress	from;
    EmailAddress[]	to;
    EmailAddress[]	cc;
    EmailAddress[]	bcc;
    String		subject;
    String		body;
    File[]		attachments;

    result = null;
    
    props = new Properties();
    if (!props.load(m_Input.getAbsolutePath())) {
      getLogger().severe("Failed to load email from " + m_Input);
      return result;
    }
    
    try {
      from        = new EmailAddress(props.getProperty(KEY_FROM, ""));
      to          = getAddresses(props, KEY_TO);
      cc          = getAddresses(props, KEY_CC);
      bcc         = getAddresses(props, KEY_BCC);
      subject     = props.getProperty(KEY_SUBJECT);
      body        = props.getProperty(KEY_BODY);
      attachments = getFiles(props, KEY_ATTACHMENTS);
      result      = new Email(from, to, cc, bcc, subject, body, attachments);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load email properties!", e);
      result = null;
    }
    
    return result;
  }
}
