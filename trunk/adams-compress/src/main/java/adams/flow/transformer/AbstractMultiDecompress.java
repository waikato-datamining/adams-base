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
 * AbstractMultiDecompress.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

/**
 * Ancestor for compression schemes that manage archives with multiple files
 * (incl directory structure) like zip archives.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiDecompress
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4547786117935917444L;

  /** the output directory. */
  protected PlaceholderDirectory m_OutputDir;

  /** the regular expression that the filenames must match to be extracted. */
  protected BaseRegExp m_RegExp;

  /** invert matching sense. */
  protected boolean m_InvertMatching;

  /** whether to restore the directory structure. */
  protected boolean m_CreateDirectories;

  /** the buffer size to use. */
  protected int m_BufferSize;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "out-dir", "outputDir",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "reg-exp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "invert", "invertMatching",
	    false);

    m_OptionManager.add(
	    "create-dirs", "createDirectories",
	    false);

    m_OptionManager.add(
	    "buffer", "bufferSize",
	    1024);
  }

  /**
   * Sets output directory to use.
   *
   * @param value	the directory
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the alternative output directory to use.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String outputDirTipText() {
    return "The output directory to use.";
  }

  /**
   * Sets the regular expression that the filenames must match.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the filenames must match.
   *
   * @return 		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return
        "The regular expression that the file names must match in order to "
      + "be extracted.";
  }

  /**
   * Sets whether to invert the matching sense of the regular expression.
   *
   * @param value	true if the matching sense is to be inverted
   */
  public void setInvertMatching(boolean value) {
    m_InvertMatching = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense of the regular expression.
   *
   * @return 		true if the matching sense is to be inverted
   */
  public boolean getInvertMatching() {
    return m_InvertMatching;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String invertMatchingTipText() {
    return
        "If set to true, the matching sense of the regular expression is inverted.";
  }

  /**
   * Sets whether to restore the directory structure from the archive.
   *
   * @param value	true if the directory structure is to be restored
   */
  public void setCreateDirectories(boolean value) {
    m_CreateDirectories = value;
    reset();
  }

  /**
   * Returns whether to restore the directory structure from the archive.
   *
   * @return 		true if the directory structure will be restored
   */
  public boolean getCreateDirectories() {
    return m_CreateDirectories;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String createDirectoriesTipText() {
    return
        "If set to true, the directory structure stored in the archive "
      + "will be restored.";
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
    String	result;

    result  = QuickInfoHelper.toString(this, "outputDir", m_OutputDir);
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", ");
    result += QuickInfoHelper.toString(this, "invertMatching", m_InvertMatching, "inverted", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted input
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated output
   */
  @Override
  public Class[] generates() {
    return new Class[]{String[].class};
  }

  /**
   * Decompresses the archive.
   *
   * @param inFile	the archive to decompress
   * @param result	for storing any error output
   * @return		the decompressed files (full paths)
   */
  protected abstract List<File> decompress(File inFile, StringBuilder result);

  /**
   * Performs the actual transformation.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    StringBuilder	result;
    File		inFile;
    List<File>		output;
    String[]		files;
    int			i;

    result = new StringBuilder();

    // get zip file
    inFile = null;
    if (m_InputToken.getPayload() instanceof File)
      inFile = (File) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof String)
      inFile = new PlaceholderFile((String) m_InputToken.getPayload());

    // unzip
    output = decompress(inFile, result);

    if (output.size() > 0) {
      files = new String[output.size()];
      for (i = 0; i < output.size(); i++)
	files[i] = output.get(i).getAbsolutePath();
      m_OutputToken = new Token(files);
    }

    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }
}
