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
import adams.data.io.input.ArffSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.io.output.ArffSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Writes the experiment results to a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileResultsHandler
  extends AbstractResultsHandler
  implements FileWriter {

  private static final long serialVersionUID = 2071016272406676626L;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /** the spreadsheet reader to use. */
  protected SpreadSheetReader m_Reader;

  /** the spreadsheet writer to use. */
  protected SpreadSheetWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified file to store the results in and loads them as well if present.";
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
      "reader", "reader",
      new ArffSpreadSheetReader());

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
    return "The file to for the results.";
  }

  /**
   * Sets the spreadsheet reader to use.
   *
   * @param value	file
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the spreadsheet reader to use.
   *
   * @return	the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The spreadsheet reader to use.";
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
   * Loads the results (if possible).
   *
   * @return		the results, null if failed to obtain (or not available)
   */
  public SpreadSheet read() {
    if (!m_OutputFile.exists())
      return null;
    if (m_OutputFile.isDirectory())
      return null;

    return m_Reader.read(m_OutputFile);
  }

  /**
   * Stores the results.
   *
   * @param results	the results to store
   * @return		null if successful, otherwise error message
   */
  @Override
  public String write(SpreadSheet results) {
    if (m_OutputFile.isDirectory())
      return "Output file is pointing to a directory!";

    if (!m_Writer.write(results, m_OutputFile))
      return "Failed to write results to: " + m_OutputFile;

    return null;
  }
}
