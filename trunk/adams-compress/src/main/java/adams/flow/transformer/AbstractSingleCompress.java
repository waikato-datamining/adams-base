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
 * AbstractSingleCompress.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

/**
 * Ancestor for compression algorithms that only take a single file, like
 * gzip.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSingleCompress
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -7648311276417258437L;

  /** whether to remove the original input file. */
  protected boolean m_RemoveInputFile;

  /** the filename of the GZIP output. */
  protected PlaceholderFile m_Output;

  /** the buffer size to use. */
  protected int m_BufferSize;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "remove", "removeInputFile",
	    false);

    m_OptionManager.add(
	    "output", "output",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "buffer", "bufferSize",
	    1024);
  }

  /**
   * Returns the default extension that the compressed archive has, e.g.,
   * ".gz" for gzipped files.
   *
   * @return		the extension, including the dot
   */
  protected abstract String getDefaultExtension();

  /**
   * Sets whether to remove the original input after the file has been
   * compressed.
   *
   * @param value	true if the input file should be removed
   */
  public void setRemoveInputFile(boolean value) {
    m_RemoveInputFile = value;
    reset();
  }

  /**
   * Returns whether to remove the original input after the file has been
   * compressed.
   *
   * @return 		true if the input file will be removed
   */
  public boolean getRemoveInputFile() {
    return m_RemoveInputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String removeInputFileTipText() {
    return
        "If set to true, then the original input file will be deleted after "
      + "a successful compression.";
  }

  /**
   * Sets the GZIP output filename.
   *
   * @param value	the filename
   */
  public void setOutput(PlaceholderFile value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the GZIP output filename.
   *
   * @return 		the filename
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public abstract String outputTipText();

  /**
   * Sets the buffer size for the stream.
   *
   * @param value	the size in bytes
   */
  public void setBufferSize(int value) {
    m_BufferSize = value;
    reset();
  }

  /**
   * Returns the buffer size for the stream.
   *
   * @return 		the size in bytes
   */
  public int getBufferSize() {
    return m_BufferSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String bufferSizeTipText() {
    return "The size of the buffer in bytes for the data stream.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = "output is ";
    result += QuickInfoHelper.toString(this, "output", (m_Output.isDirectory() ? "<incoming>" + getDefaultExtension() : m_Output));
    result += QuickInfoHelper.toString(this, "removeInputFile", m_RemoveInputFile, "remove <incoming>", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted input
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated output
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Compresses the file.
   *
   * @param inFile	the uncompressed input file
   * @param outFile	the compressed output file
   * @return		null if successfully compressed, otherwise error message
   */
  protected abstract String compress(File inFile, File outFile);

  /**
   * Performs the actual transformation.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    File	inFile;
    String	output;
    File	outFile;

    inFile = null;
    if (m_InputToken.getPayload() instanceof File) {
      inFile = (File) m_InputToken.getPayload();
    }
    else if (m_InputToken.getPayload() instanceof String) {
      inFile = new PlaceholderFile((String) m_InputToken.getPayload());
    }

    // determine output filename
    if (m_Output.isDirectory())
      output = inFile.getAbsolutePath() + getDefaultExtension();
    else
      output = m_Output.getAbsolutePath();
    outFile = new File(output);

    result = compress(inFile, outFile);

    if (result == null)
      m_OutputToken = new Token(output);

    return result;
  }
}
