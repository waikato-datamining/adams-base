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
 * PropertiesSpreadSheetWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.Properties;
import adams.data.io.input.PropertiesSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 <!-- globalinfo-start -->
 * Outputs two columns (key and value) from a spreadsheet as Java properties file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-key &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The column that acts as the key for the properties.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-value &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The column that acts as the value for the properties.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertiesSpreadSheetWriter
  extends AbstractSpreadSheetWriter {

  private static final long serialVersionUID = -6004699877865566744L;

  /** the column to use as key column. */
  protected SpreadSheetColumnIndex m_Key;

  /** the column to use as value column. */
  protected SpreadSheetColumnIndex m_Value;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs two columns (key and value) from a spreadsheet as Java properties file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "key",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "value", "value",
      new SpreadSheetColumnIndex("2"));
  }

  /**
   * Sets the column to act as key for properties.
   *
   * @param value	the column
   */
  public void setKey(SpreadSheetColumnIndex value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the column to act as key for properties.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String keyTipText() {
    return "The column that acts as the key for the properties.";
  }

  /**
   * Sets the column to act as value for properties.
   *
   * @param value	the column
   */
  public void setValue(SpreadSheetColumnIndex value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the column to act as value for properties.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String valueTipText() {
    return "The column that acts as the value for the properties.";
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public SpreadSheetReader getCorrespondingReader() {
    return new PropertiesSpreadSheetReader();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new PropertiesSpreadSheetReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new PropertiesSpreadSheetReader().getFormatExtensions();
  }

  /**
   * Returns how the data is written.
   *
   * @return		the type
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.FILE;
  }

  /**
   * Performs the actual writing.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet content, String filename) {
    Properties		props;
    int			keyCol;
    int			valueCol;
    String		key;
    String		value;

    m_Key.setData(content);
    keyCol = m_Key.getIntIndex();
    if (keyCol == -1) {
      getLogger().severe("Key column not found: " + m_Key);
      return false;
    }
    m_Value.setData(content);
    valueCol = m_Value.getIntIndex();
    if (valueCol == -1) {
      getLogger().severe("Value column not found: " + m_Value);
      return false;
    }

    // generate props
    props = new Properties();
    for (Row row: content.rows()) {
      if (row.hasCell(keyCol) && !row.getCell(keyCol).isMissing()) {
	key = row.getCell(keyCol).getContent();
	value = "";
	if (row.hasCell(valueCol))
	  value = row.getCell(valueCol).getContent();
	props.setProperty(key, value);
      }
    }

    return props.save(filename);
  }
}
