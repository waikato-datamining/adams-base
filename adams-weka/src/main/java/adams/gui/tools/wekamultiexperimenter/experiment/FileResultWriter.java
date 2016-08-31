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

/**
 * FileResultWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.experiment;

import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.data.io.output.ArffSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Writes the experiment results to a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileResultWriter
  extends AbstractResultWriter
  implements FileWriter {

  private static final long serialVersionUID = 2071016272406676626L;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /** the spreadsheet writer to use. */
  protected SpreadSheetWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stores the results in the specified file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "writer", "writer",
      new ArffSpreadSheetWriter());
  }

  /**
   * Set output file.
   *
   * @param value	file
   */
  @Override
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Get output file.
   *
   * @return	file
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
    return "The file to write the output to.";
  }

  /**
   * Sets the spreadsheet writer to use.
   *
   * @param value	file
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the spreadsheet writer to use.
   *
   * @return	the writer
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The spreadsheet writer to use.";
  }

  /**
   * Stores the results.
   *
   * @param results	the results to store
   * @return		null if successful, otherwise error message
   */
  @Override
  public String store(SpreadSheet results) {
    if (m_Writer == null)
      return "Failed to determine spreadsheet writer for file: " + m_OutputFile;
    if (!m_Writer.write(results, m_OutputFile))
      return "Failed to write results to: " + m_OutputFile;

    return null;
  }
}
