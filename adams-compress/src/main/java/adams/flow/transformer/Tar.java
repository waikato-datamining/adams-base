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
 * Tar.java
 * Copyright (C) 2011-2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.TarUtils;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Creates a Tar file containing one or more files. Outputs the filename of the Tar file generated. Always uses archives with GNU long filenamesupport. The compression (gzip or bzip2) is automatically determined based on the file extension.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Tar
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The Tar file to create.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-strip-path &lt;java.lang.String&gt; (property: stripPath)
 * &nbsp;&nbsp;&nbsp;The regular expression for stripping the path; use '.*' to remove the path
 * &nbsp;&nbsp;&nbsp;completely; NB: uses platform-dependent slashes of the absolute path when
 * &nbsp;&nbsp;&nbsp;applying the expression.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-strip-prefix &lt;java.lang.String&gt; (property: stripPrefix)
 * &nbsp;&nbsp;&nbsp;The prefix to remove from the path, case-insensitive
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-buffer &lt;int&gt; (property: bufferSize)
 * &nbsp;&nbsp;&nbsp;The size of the buffer in bytes for the data stream.
 * &nbsp;&nbsp;&nbsp;default: 1024
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Tar
  extends AbstractMultiCompress {

  /** for serialization. */
  private static final long serialVersionUID = -5229130758529233965L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Creates a Tar file containing one or more files. Outputs the filename "
	+ "of the Tar file generated. Always uses archives with GNU long filename"
	+ "support. The compression (gzip or bzip2) is automatically determined "
	+ "based on the file extension.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return "The Tar file to create.";
  }

  /**
   * Adds all the files to archive.
   *
   * @param inFiles	the files store in the
   * @return		null if successful, otherwise error message
   */
  protected String compress(File[] inFiles) {
    return TarUtils.compress(m_Output, inFiles, m_StripPath, m_StripPrefix, m_BufferSize);
  }
}
