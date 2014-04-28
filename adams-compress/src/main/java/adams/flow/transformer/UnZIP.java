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
 * UnZIP.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.List;

import adams.core.io.ZipUtils;

/**
 <!-- globalinfo-start -->
 * Unzips a ZIP archive and broadcasts the full file names of the extracted files. A regular expression can be used to control the files that are being extracted. Whether the directory structure in the ZIP archive gets restored is optional.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: UnZIP
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
 * <pre>-out-dir &lt;adams.core.io.PlaceholderFile&gt; (property: outputDir)
 * &nbsp;&nbsp;&nbsp;The output directory to use.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-reg-exp &lt;java.lang.String&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the file names must match in order to be extracted.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-invert (property: invertMatching)
 * &nbsp;&nbsp;&nbsp;If set to true, the matching sense of the regular expression is inverted.
 * </pre>
 *
 * <pre>-create-dirs (property: createDirectories)
 * &nbsp;&nbsp;&nbsp;If set to true, the directory structure stored in the ZIP archive will be
 * &nbsp;&nbsp;&nbsp;restored.
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
public class UnZIP
  extends AbstractMultiDecompress {

  /** for serialization. */
  private static final long serialVersionUID = 7463671491943647599L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Unzips a ZIP archive and broadcasts the full file names of the "
      + "extracted files. A regular expression can be used to control the "
      + "files that are being extracted. Whether the directory structure "
      + "in the ZIP archive gets restored is optional.";
  }

  /**
   * Decompresses the archive.
   *
   * @param inFile	the archive to decompress
   * @param result	for storing any error output
   * @return		the decompressed files (full paths)
   */
  @Override
  protected List<File> decompress(File inFile, StringBuilder result) {
    return ZipUtils.decompress(
	inFile, m_OutputDir, m_CreateDirectories,
	m_RegExp, m_InvertMatching, m_BufferSize, result);
  }
}
