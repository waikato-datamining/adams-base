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
 * PropertiesEmailFileWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.Properties;
import adams.core.net.Email;
import adams.core.option.OptionUtils;
import adams.data.io.input.PropertiesEmailFileReader;

/**
 <!-- globalinfo-start -->
 * Writes emails to properties files.
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the email to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertiesEmailFileWriter
  extends AbstractEmailFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 8613890718581689507L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes emails to properties files.";
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
   * Turns the object array into a blank-separated string.
   * 
   * @param obj		the objects to turn into string
   * @return		the generated string
   */
  protected String toBlankSeparated(Object[] obj) {
    String	result;
    String[]	list;
    int		i;
    
    list = new String[obj.length];
    for (i = 0; i < obj.length; i++)
      list[i] = obj[i].toString();
    
    result = OptionUtils.joinOptions(list);
    
    return result;
  }
  
  /**
   * Performs the actual writing.
   * 
   * @param email	the email to write
   * @return		the error message, null if everything OK
   */
  @Override
  protected String doWrite(Email email) {
    String	result;
    Properties	props;
    
    result = null;
    props  = new Properties();

    props.setProperty(PropertiesEmailFileReader.KEY_FROM, email.getFrom().getValue());
    props.setProperty(PropertiesEmailFileReader.KEY_TO, toBlankSeparated(email.getTo()));
    props.setProperty(PropertiesEmailFileReader.KEY_CC, toBlankSeparated(email.getCC()));
    props.setProperty(PropertiesEmailFileReader.KEY_BCC, toBlankSeparated(email.getBCC()));
    props.setProperty(PropertiesEmailFileReader.KEY_SUBJECT, email.getSubject());
    props.setProperty(PropertiesEmailFileReader.KEY_BODY, email.getBody());
    props.setProperty(PropertiesEmailFileReader.KEY_ATTACHMENTS, toBlankSeparated(email.getAttachments()));

    if (!props.save(m_Output.getAbsolutePath()))
      result = "Failed to write properties to " + m_Output + ", check console!";
    
    return result;
  }
}
