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
 * PropertiesSpreadSheetReader.java
 * Copyright (C) 2016-2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Properties;
import adams.data.io.output.PropertiesSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Turns Java properties files into spreadsheets with two columns: Key and Value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-data-row-type &lt;adams.data.spreadsheet.DataRow&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DenseDataRow
 * </pre>
 * 
 * <pre>-spreadsheet-type &lt;adams.data.spreadsheet.SpreadSheet&gt; (property: spreadSheetType)
 * &nbsp;&nbsp;&nbsp;The type of spreadsheet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 * 
 * <pre>-force-string &lt;boolean&gt; (property: forceString)
 * &nbsp;&nbsp;&nbsp;If enabled, the property values are set as string, bypassing the spreadsheet's 
 * &nbsp;&nbsp;&nbsp;parsing.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PropertiesSpreadSheetReader
  extends AbstractSpreadSheetReader {

  private static final long serialVersionUID = -7201569718203967741L;

  /** the header for the key column. */
  public final static String HEADER_KEY = "Key";

  /** the header for the value column. */
  public final static String HEADER_VALUE = "Value";

  /** whether to set values as string. */
  protected boolean m_ForceString;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Turns Java properties files into spreadsheets with two columns: "
	+ HEADER_KEY + " and " + HEADER_VALUE + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "force-string", "forceString",
	    false);
  }

  /**
   * Sets whether to force setting the values as string, bypassing the
   * spreadsheet's parsing.
   *
   * @param value	true if to force string
   */
  public void setForceString(boolean value) {
    m_ForceString = value;
    reset();
  }

  /**
   * Returns whether to force setting the values as string, bypassing the
   * spreadsheet's parsing.
   *
   * @return		true if string type is enforced
   */
  public boolean getForceString() {
    return m_ForceString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forceStringTipText() {
    return "If enabled, the property values are set as string, bypassing the spreadsheet's parsing.";
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public SpreadSheetWriter getCorrespondingWriter() {
    return new PropertiesSpreadSheetWriter();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Properties files";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"props", "properties"};
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.FILE;
  }

  /**
   * Performs the actual reading. Must handle compression itself, if
   * {@link #supportsCompressedInput()} returns true.
   *
   * @param file	the file to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   * @see		#supportsCompressedInput()
   */
  @Override
  protected SpreadSheet doRead(File file) {
    SpreadSheet		result;
    Properties		props;
    List<String>	keys;
    Row			row;

    props  = new Properties();
    if (!props.load(file.getAbsolutePath()))
      return null;

    result = new DefaultSpreadSheet();

    // header
    row = result.getHeaderRow();
    row.addCell("K").setContentAsString(HEADER_KEY);
    row.addCell("V").setContentAsString(HEADER_VALUE);

    // data
    keys = new ArrayList<>(props.keySetAll());
    Collections.sort(keys);
    for (String key: keys) {
      row = result.addRow();
      row.addCell("K").setContentAsString(key);
      if (m_ForceString)
	row.addCell("V").setContentAsString(props.getProperty(key));
      else
	row.addCell("V").setContent(props.getProperty(key));
    }

    return result;
  }

  /**
   * Runs the reader from the command-line.
   *
   * Use the option {@link #OPTION_INPUT} to specify the input file.
   * If the option {@link #OPTION_OUTPUT} is specified then the read sheet
   * gets output as .csv files in that directory.
   *
   * @param args	the command-line options to use
   */
  public static void main(String[] args) {
    runReader(Environment.class, PropertiesSpreadSheetReader.class, args);
  }
}
