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
 * GZIP.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import adams.core.io.GzipUtils;

/**
 <!-- globalinfo-start -->
 * Creates a GZIP archive from a single file. Outputs the filename of the GZIP file generated. Optionally, the original input file can be deleted.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: GZIP
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
 * <pre>-remove (property: removeInputFile)
 * &nbsp;&nbsp;&nbsp;If set to true, then the original input file will be deleted after a successful
 * &nbsp;&nbsp;&nbsp;compression.
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The GZIP file to create; if pointing to a directory, then the output filename
 * &nbsp;&nbsp;&nbsp;is based on the file that is being compressed and the 'gz' extension added.
 * &nbsp;&nbsp;&nbsp;default: .
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
public class GZIP
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
        "Creates a GZIP archive from a single file. Outputs the filename "
      + "of the GZIP file generated. Optionally, the original input file can "
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
        "The GZIP file to create; if pointing to a directory, then the output "
      + "filename is based on the file that is being compressed and the '" + GzipUtils.EXTENSION + "' "
      + "extension added.";
  }

  /**
   * Returns the default extension that the compressed archive has, e.g.,
   * ".gz" for gzipped files.
   *
   * @return		the extension, including the dot
   */
  protected String getDefaultExtension() {
    return GzipUtils.EXTENSION;
  }

  /**
   * Compresses the file.
   *
   * @param inFile	the uncompressed input file
   * @param outFile	the compressed output file
   * @return		null if successfully compressed, otherwise error message
   */
  protected String compress(File inFile, File outFile) {
    return GzipUtils.compress(inFile, m_BufferSize, outFile, m_RemoveInputFile);
  }
}
