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
 * SimpleFileList.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.generatefilebaseddataset;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.flow.container.FileBasedDatasetContainer;

import java.util.Arrays;

/**
 * Simply outputs the specified list of files in the container as a text file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleFileList
  extends AbstractFileBasedDatasetGeneration<String>
  implements FileWriter  {

  private static final long serialVersionUID = -770254975357506229L;

  /** the value in the container to use. */
  protected String m_Value;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply outputs the specified list of files in the container as a text file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "value", "value",
      FileBasedDatasetContainer.VALUE_TRAIN);

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile());
  }

  /**
   * Sets the name of the container value to save.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the name of the container value to save.
   *
   * @return		the value
   */
  public String getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The name of the value in the container to use.";
  }

  /**
   * Sets the output file.
   *
   * @param value	the file
   */
  @Override
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return		the file
   */
  @Override
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The file to save the file list to.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "value", m_Value, "value: ");
    result += QuickInfoHelper.toString(this, "outputFile", m_OutputFile, ", output: ");

    return result;
  }

  /**
   * Returns the class that gets generated.
   *
   * @return		the generated class
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * The keys of the values that need to be present in the container.
   *
   * @return		the keys
   */
  @Override
  protected String[] requiredValues() {
    return new String[]{m_Value};
  }

  /**
   * Performs checks on the container.
   *
   * @param cont	the container to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(FileBasedDatasetContainer cont) {
    String	result;

    result = super.check(cont);

    if (result == null) {
      if (!m_OutputFile.exists())
        result = "Output file does not exist: " + m_OutputFile;
      else if (m_OutputFile.isDirectory())
        result = "Output file points to directory: " + m_OutputFile;
    }

    return result;
  }

  /**
   * Generates the dataset.
   *
   * @param cont	the container to use
   * @return		the generated output
   */
  @Override
  protected String doGenerate(FileBasedDatasetContainer cont) {
    String	msg;
    String[]	files;

    files = cont.getValue(m_Value, String[].class);
    msg   = FileUtils.saveToFileMsg(Arrays.asList(files), m_OutputFile, null);
    if (msg != null)
      throw new IllegalStateException(msg);

    return m_OutputFile.getAbsolutePath();
  }
}
