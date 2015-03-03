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
 * EmailFileReader.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.core.net.Email;
import adams.data.io.input.MultiEmailReader;
import adams.data.io.input.PropertiesEmailFileReader;

/**
 <!-- globalinfo-start -->
 * Reads email(s) from a file.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.core.net.Email<br/>
 * <p/>
 <!-- flow-summary-end -->
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
 * &nbsp;&nbsp;&nbsp;default: EmailFileReader
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
 * <pre>-output-array (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the emails get output as array rather than one-by-one.
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.EmailFileReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader for reading the emails.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.PropertiesEmailFileReader
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailFileReader
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -1585651878523228177L;

  /** the reader to use. */
  protected adams.data.io.input.EmailFileReader m_Reader;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads email(s) from a file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reader", "reader",
	    new PropertiesEmailFileReader());
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
    
    result = QuickInfoHelper.toString(this, "reader", m_Reader, "reader: ");
    value  = QuickInfoHelper.toString(this, "outputArray", m_OutputArray, (m_OutputArray ? "as array" : "one-by-one"), ", ");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Email.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the emails get output as array rather than one-by-one.";
  }

  /**
   * Sets the reader to use.
   *
   * @param value	the reader to use
   */
  public void setReader(adams.data.io.input.EmailFileReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader in use.
   *
   * @return		the reader in use
   */
  public adams.data.io.input.EmailFileReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader for reading the emails.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		fileObj;
    PlaceholderFile	file;
    Email		email;
    List<Email>		emails;

    result = null;

    fileObj = m_InputToken.getPayload();
    if (fileObj instanceof File)
      file = new PlaceholderFile((File) fileObj);
    else
      file = new PlaceholderFile((String) fileObj);

    m_Reader.setInput(file);
    
    try {
      if (m_Reader instanceof MultiEmailReader) {
	emails = ((MultiEmailReader) m_Reader).readAll();
	m_Queue.addAll(emails);
      }
      else {
	email = m_Reader.read();
	m_Queue.add(email);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to write email(s)!", e);
    }

    return result;
  }
}
