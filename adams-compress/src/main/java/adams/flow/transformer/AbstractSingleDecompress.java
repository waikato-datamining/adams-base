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
 * AbstractSingleDecompress.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.BufferSupporter;
import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

import java.io.File;

/**
 * Ancestor for decompression algorithms that only work with archives
 * that consists of a single file, like gunzip.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSingleDecompress
  extends AbstractTransformer
  implements BufferSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -8337511248726861039L;

  /** the dummy extension if the file doesn't end with .gz. */
  public final static String DUMMY_EXTENSION = ".decompressed";

  /** whether to use an alternative location for the file. */
  protected boolean m_UseAlternativeOutputDir;

  /** the alternative output directory. */
  protected PlaceholderDirectory m_AlternativeOutputDir;

  /** the alternative filename to use. */
  protected String m_AlternativeFilename;

  /** the buffer size to use. */
  protected int m_BufferSize;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-out-dir", "useAlternativeOutputDir",
	    false);

    m_OptionManager.add(
	    "out-dir", "alternativeOutputDir",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "alt-filename", "alternativeFilename",
	    "");

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
   * Sets whether to use an alternative output directory.
   *
   * @param value	true if an alternative output directory is used
   */
  public void setUseAlternativeOutputDir(boolean value) {
    m_UseAlternativeOutputDir = value;
    reset();
  }

  /**
   * Returns whether to use an alternative output directory.
   *
   * @return 		true if an alternative output directory is used
   */
  public boolean getUseAlternativeOutputDir() {
    return m_UseAlternativeOutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public abstract String useAlternativeOutputDirTipText();

  /**
   * Sets alternative output directory to use.
   *
   * @param value	the directory
   */
  public void setAlternativeOutputDir(PlaceholderDirectory value) {
    m_AlternativeOutputDir = value;
    reset();
  }

  /**
   * Returns the alternative output directory to use.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getAlternativeOutputDir() {
    return m_AlternativeOutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String alternativeOutputDirTipText() {
    return "The alternative output directory to use.";
  }

  /**
   * Sets alternative filename to use.
   *
   * @param value	the filename
   */
  public void setAlternativeFilename(String value) {
    m_AlternativeFilename = value;
    reset();
  }

  /**
   * Returns the alternative filename to use.
   *
   * @return 		the filename
   */
  public String getAlternativeFilename() {
    return m_AlternativeFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public abstract String alternativeFilenameTipText();

  /**
   * Sets the buffer size for the stream.
   *
   * @param value	the size in bytes
   */
  public void setBufferSize(int value) {
    if (getOptionManager().isValid("bufferSize", value)) {
      m_BufferSize = value;
      reset();
    }
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
    String	variable;

    if (QuickInfoHelper.hasVariable(this, "useAlternativeOutputDir") || m_UseAlternativeOutputDir)
      result = QuickInfoHelper.toString(this, "alternativeOutputDir", m_AlternativeOutputDir);
    else
      result = "<from input>";

    variable = QuickInfoHelper.getVariable(this, "alternativeFilename");
    if (variable != null)
      result += File.separator + variable;
    else if ((m_AlternativeFilename != null) && (m_AlternativeFilename.length() > 0))
      result += File.separator + m_AlternativeFilename;

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
   * Decompresses the archive.
   *
   * @param inFile	the compressed archive
   * @param outFile	the decompressed output file
   * @return		null if successful, otherwise error message
   */
  protected abstract String decompress(File inFile, File outFile);

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
    if (m_InputToken.getPayload() instanceof File)
      inFile = (File) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof String)
      inFile = new PlaceholderFile((String) m_InputToken.getPayload());

    // determine output filename
    if (m_UseAlternativeOutputDir)
      output = m_AlternativeOutputDir.getAbsolutePath();
    else
      output = inFile.getParentFile().getAbsolutePath();
    output += File.separator;
    if (m_AlternativeFilename.length() > 0) {
      output += m_AlternativeFilename;
    }
    else {
      if (inFile.getName().endsWith(getDefaultExtension()))
	output += inFile.getName().replaceAll("\\" + getDefaultExtension() + "$", "");
      else
	output += inFile.getName() + DUMMY_EXTENSION;
    }
    outFile = new File(output);

    result = decompress(inFile, outFile);

    if (result == null)
      m_OutputToken = new Token(output);

    return result;
  }
}
