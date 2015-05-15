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
 * BinaryFileWriter.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.base.BaseCharset;
import adams.core.io.FileUtils;
import adams.data.blob.BlobContainer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

/**
 <!-- globalinfo-start -->
 * Writes a byte array or adams.data.blob.BlobContainer to a binary file.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;byte[]<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.blob.BlobContainer<br/>
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
 * &nbsp;&nbsp;&nbsp;default: BinaryFileWriter
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The name of the output file.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8887 $
 */
public class BinaryFileWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 3613018887562327088L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Writes a byte array or " + BlobContainer.class.getName() + " to a binary file.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The name of the output file.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->byte[].class, adams.data.blob.BlobContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{byte[].class, BlobContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    byte[]			content;
    BufferedOutputStream	out;

    result = null;

    out = null;
    try {
      if (m_InputToken.getPayload() instanceof byte[])
	content = (byte[]) m_InputToken.getPayload();
      else
	content = ((BlobContainer) m_InputToken.getPayload()).getContent();
      out = new BufferedOutputStream(new FileOutputStream(m_OutputFile.getAbsolutePath()));
      out.write(content);
      out.flush();
    }
    catch (Exception e) {
      result = handleException("Failed to write byte array to " + m_OutputFile, e);
    }
    finally {
      FileUtils.closeQuietly(out);
    }

    return result;
  }
}
