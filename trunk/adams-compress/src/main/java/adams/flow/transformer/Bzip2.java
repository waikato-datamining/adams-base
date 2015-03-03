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
 * Bzip2.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import adams.core.io.Bzip2Utils;

/**
 <!-- globalinfo-start -->
 * Creates a bzip2 archive from a single file. Outputs the filename of the bzip2 file generated. Optionally, the original input file can be deleted.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
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
 * &nbsp;&nbsp;&nbsp;default: Bzip2
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
 * <pre>-remove (property: removeInputFile)
 * &nbsp;&nbsp;&nbsp;If set to true, then the original input file will be deleted after a successful 
 * &nbsp;&nbsp;&nbsp;compression.
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The bzip2 file to create; if pointing to a directory, then the output filename 
 * &nbsp;&nbsp;&nbsp;is based on the file that is being compressed and the '.bz2' extension added.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-buffer &lt;int&gt; (property: bufferSize)
 * &nbsp;&nbsp;&nbsp;The size of the buffer in bytes for the data stream.
 * &nbsp;&nbsp;&nbsp;default: 1024
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Bzip2
  extends AbstractSingleCompress {

  /** for serialization. */
  private static final long serialVersionUID = 6718580828800815681L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Creates a bzip2 archive from a single file. Outputs the filename "
      + "of the bzip2 file generated. Optionally, the original input file can "
      + "be deleted.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return
        "The bzip2 file to create; if pointing to a directory, then the output "
      + "filename is based on the file that is being compressed and the '" + Bzip2Utils.EXTENSION + "' "
      + "extension added.";
  }

  /**
   * Returns the default extension that the compressed archive has, e.g.,
   * ".bz2" for compressed files.
   *
   * @return		the extension, including the dot
   */
  protected String getDefaultExtension() {
    return Bzip2Utils.EXTENSION;
  }

  /**
   * Compresses the file.
   *
   * @param inFile	the uncompressed input file
   * @param outFile	the compressed output file
   * @return		null if successfully compressed, otherwise error message
   */
  protected String compress(File inFile, File outFile) {
    return Bzip2Utils.compress(inFile, m_BufferSize, outFile, m_RemoveInputFile);
  }
}
