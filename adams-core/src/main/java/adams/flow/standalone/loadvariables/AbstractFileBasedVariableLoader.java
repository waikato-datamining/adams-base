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
 * AbstractFileBasedVariableLoader.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.loadvariables;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;

/**
 * Ancestor for loaders that load the variables from files.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileBasedVariableLoader
  extends AbstractVariableLoader {

  private static final long serialVersionUID = 3848272844493416721L;

  /** the file to process. */
  protected PlaceholderFile m_InputFile;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "input-file", "inputFile",
      new PlaceholderFile("."));
  }

  /**
   * Sets the input file to process.
   *
   * @param value	the file
   */
  public void setInputFile(PlaceholderFile value) {
    m_InputFile = value;
    reset();
  }

  /**
   * Returns the input file to process.
   *
   * @return 		the file
   */
  public PlaceholderFile getInputFile() {
    return m_InputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String inputFileTipText() {
    return "The file to process by the PDF processors.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "inputFile", m_InputFile, "file: ");
  }

  /**
   * Checks whether the variables can be loaded.
   *
   * @param errors	for collecting errors
   */
  @Override
  protected void check(MessageCollection errors) {
    super.check(errors);

    if (!m_InputFile.exists())
      errors.add("Input file does not exist: " + m_InputFile);
    if (m_InputFile.isDirectory())
      errors.add("Input file points to a directory: " + m_InputFile);
  }
}
