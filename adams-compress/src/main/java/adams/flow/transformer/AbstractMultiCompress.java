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
 * AbstractMultiCompress.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

/**
 * Abstract ancestor for compression algorithms that allow the compression
 * of multiple files (incl directory structure).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiCompress
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -4546660303745271704L;

  /** the filename of the archive output. */
  protected PlaceholderFile m_Output;

  /** the regular expression to use for stripping the path. */
  protected String m_StripPath;

  /** the buffer size to use. */
  protected int m_BufferSize;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output", "output",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "strip-path", "stripPath",
	    "");

    m_OptionManager.add(
	    "buffer", "bufferSize",
	    1024);
  }

  /**
   * Sets the archive output filename.
   *
   * @param value	the filename
   */
  public void setOutput(PlaceholderFile value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the archive output filename.
   *
   * @return 		the filename
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public abstract String outputTipText();

  /**
   * Sets the regular expression to use for stripping the path.
   *
   * @param value	the regular expression
   */
  public void setStripPath(String value) {
    m_StripPath = value;
    reset();
  }

  /**
   * Returns the regular expression used for stripping the path.
   *
   * @return 		the regular expression
   */
  public String getStripPath() {
    return m_StripPath;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String stripPathTipText() {
    return "The regular expression for stripping the path (use '.*' to remove the path completely).";
  }

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
    return QuickInfoHelper.toString(this, "output", m_Output);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accpted input
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class, String[].class, File[].class};
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
   * Adds all the files to archive.
   *
   * @param inFiles	the files store in the
   * @return		null if successful, otherwise error message
   */
  protected abstract String compress(File[] inFiles);

  /**
   * Performs the actual transformation.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    File[]		inFiles;
    int			i;

    result = null;

    // get files
    inFiles = null;
    if (m_InputToken.getPayload() instanceof File) {
      inFiles = new File[]{(File) m_InputToken.getPayload()};
    }
    else if (m_InputToken.getPayload() instanceof File[]) {
      inFiles = (File[]) m_InputToken.getPayload();
    }
    else if (m_InputToken.getPayload() instanceof String) {
      inFiles = new File[]{new PlaceholderFile((String) m_InputToken.getPayload())};
    }
    else if (m_InputToken.getPayload() instanceof String[]) {
      inFiles = new File[((String[]) m_InputToken.getPayload()).length];
      for (i = 0; i < inFiles.length; i++)
	inFiles[i] = new PlaceholderFile(((String[]) m_InputToken.getPayload())[i]);
    }

    result = compress(inFiles);
    if (result == null)
      m_OutputToken = new Token(m_Output.getAbsolutePath());

    return result;
  }
}
