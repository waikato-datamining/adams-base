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
 * MergeFiles.java
 * Copyright (C) 2014-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.transformer.SplitFile;
import adams.flow.transformer.splitfile.FileType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;

/**
 <!-- globalinfo-start -->
 * Combines the incoming files in a single one.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.SplitFile
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: MergeFiles
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The output file containing the combined content.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-file-type &lt;TEXT|BINARY&gt; (property: fileType)
 * &nbsp;&nbsp;&nbsp;Defines how to treat the file(s).
 * &nbsp;&nbsp;&nbsp;default: TEXT
 * </pre>
 *
 * <pre>-buffer-size &lt;int&gt; (property: bufferSize)
 * &nbsp;&nbsp;&nbsp;The size of byte-buffer used for reading the content.
 * &nbsp;&nbsp;&nbsp;default: 1024
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MergeFiles
  extends AbstractFileWriter
  implements ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = -6717503477934358351L;

  /** the file type. */
  protected FileType m_FileType;

  /** the buffer size to use. */
  protected int m_BufferSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines the incoming files in a single one.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{SplitFile.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "file-type", "fileType",
      FileType.TEXT);

    m_OptionManager.add(
      "buffer-size", "bufferSize",
      1024, 1, null);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileTypeTipText() {
    return "Defines how to treat the file(s).";
  }

  /**
   * Sets how to process the files.
   *
   * @param value	the type of file
   */
  public void setFileType(FileType value) {
    m_FileType = value;
    reset();
  }

  /**
   * Returns how to process the files.
   *
   * @return		the type of file
   */
  public FileType getFileType() {
    return m_FileType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The output file containing the combined content.";
  }

  /**
   * Sets the size of the buffer.
   *
   * @param value	the size
   */
  public void setBufferSize(int value) {
    if (getOptionManager().isValid("bufferSize", value)) {
      m_BufferSize = value;
      reset();
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
    return "The size of byte-buffer used for reading the content.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "fileType", m_FileType, ", type: ");
    result += QuickInfoHelper.toString(this, "bufferSize", m_BufferSize, ", buffer: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted input
   */
  public Class[] accepts() {
    return new Class[]{String[].class, File[].class};
  }

  /**
   * Merges the text files.
   *
   * @param files	the files to merge
   * @return		null if successful, otherwise error message
   */
  protected String mergeText(PlaceholderFile[] files) {
    String		result;
    FileReader		reader;
    FileWriter		writer;
    int			i;
    int			read;
    char[]		buffer;

    result = null;

    // set up readers/writer
    buffer = new char[m_BufferSize];
    try {
      writer = new FileWriter(m_OutputFile.getAbsoluteFile());
      for (i = 0; i < files.length; i++) {
	reader = new FileReader(files[i].getAbsoluteFile());
	while ((read = reader.read(buffer)) > -1) {
	  writer.write(buffer, 0, read);
	  if (isStopped())
	    break;
	}
	reader.close();
	writer.flush();
	if (isStopped())
	  break;
      }
      writer.close();
    }
    catch (Exception e) {
      result = handleException("Failed to merge files: ", e);
    }

    return result;
  }

  /**
   * Merges the binary files.
   *
   * @param files	the files to merge
   * @return		null if successful, otherwise error message
   */
  protected String mergeBinary(PlaceholderFile[] files) {
    String		result;
    InputStream 	in;
    OutputStream 	out;
    int			i;
    int			read;
    byte[]		buffer;

    result = null;

    // set up readers/writer
    buffer = new byte[m_BufferSize];
    try {
      out = new FileOutputStream(m_OutputFile.getAbsoluteFile());
      for (i = 0; i < files.length; i++) {
	in = new FileInputStream(files[i].getAbsoluteFile());
	while ((read = in.read(buffer)) > -1) {
	  out.write(buffer, 0, read);
	  if (isStopped())
	    break;
	}
	in.close();
	out.flush();
	if (isStopped())
	  break;
      }
      out.close();
    }
    catch (Exception e) {
      result = handleException("Failed to merge files: ", e);
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile[]	files;

    // get input files
    files = FileUtils.toPlaceholderFileArray(m_InputToken.getPayload());

    switch (m_FileType) {
      case BINARY:
	result = mergeBinary(files);
	break;

      case TEXT:
	result = mergeText(files);
	break;

      default:
	throw new IllegalArgumentException("Unhandled file type: " + m_FileType);
    }

    return result;
  }
}
