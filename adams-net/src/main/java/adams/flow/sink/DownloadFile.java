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
 * DownloadFile.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import adams.core.io.FileUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 <!-- globalinfo-start -->
 * Downloads a file from a URL and saves it locally.<br/>
 * Also handles basic authentication when using URLs like this:<br/>
 * http:&#47;&#47;user:pass&#64;domain.com&#47;url
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.net.URL<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DownloadFile
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to save the downloaded content to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-buffer-size &lt;int&gt; (property: bufferSize)
 * &nbsp;&nbsp;&nbsp;The size of byte-buffer used for reading&#47;writing the content.
 * &nbsp;&nbsp;&nbsp;default: 1024
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DownloadFile
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 8688918591152139449L;

  /** the buffer size to use. */
  protected int m_BufferSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Downloads a file from a URL and saves it locally.\n"
      + "Also handles basic authentication when using URLs like this:\n"
      + "http://user:pass@domain.com/url";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "buffer-size", "bufferSize",
	    1024, 1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "bufferSize", m_BufferSize, "Buffer: ");
  }

  /**
   * Sets the size of the buffer.
   *
   * @param value	the size
   */
  public void setBufferSize(int value) {
    if (value > 0) {
      m_BufferSize = value;
      reset();
    }
    else {
      getLogger().severe("Buffer must be >0, provided: " + value);
    }
  }

  /**
   * Get output file.
   *
   * @return	file
   */
  public int getBufferSize() {
    return m_BufferSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bufferSizeTipText() {
    return "The size of byte-buffer used for reading/writing the content.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The file to save the downloaded content to.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.net.URL.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, URL.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  @MixedCopyright(
    author = "http://stackoverflow.com/users/2920131/lboix",
    license = License.CC_BY_SA_3,
    url = "http://stackoverflow.com/a/13122190",
    note = "handling basic authentication"
  )
  protected String doExecute() {
    String			result;
    URL				url;
    BufferedInputStream		input;
    BufferedOutputStream	output;
    FileOutputStream		fos;
    byte[]			buffer;
    int				len;
    int				count;
    URLConnection 		conn;
    String 			basicAuth;

    input  = null;
    output = null;
    fos    = null;
    try {
      if (m_InputToken.getPayload() instanceof String)
	url = new URL((String) m_InputToken.getPayload());
      else
	url = (URL) m_InputToken.getPayload();

      conn = url.openConnection();
      if (url.getUserInfo() != null) {
	basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
	conn.setRequestProperty("Authorization", basicAuth);
      }
      input  = new BufferedInputStream(conn.getInputStream());
      fos    = new FileOutputStream(m_OutputFile.getAbsoluteFile());
      output = new BufferedOutputStream(fos);
      buffer = new byte[m_BufferSize];
      count  = 0;
      while ((len = input.read(buffer)) > 0) {
	count++;
	output.write(buffer, 0, len);
	if (count % 100 == 0)
	  output.flush();
      }
      output.flush();

      result = null;
    }
    catch (Exception e) {
      result = handleException("Problem downloading '" + m_InputToken.getPayload() + "': ", e);
    }
    finally {
      FileUtils.closeQuietly(input);
      FileUtils.closeQuietly(output);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }
}
