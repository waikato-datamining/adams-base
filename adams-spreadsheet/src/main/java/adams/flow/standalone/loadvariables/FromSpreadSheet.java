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
 * FromSpreadSheet.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.loadvariables;

import adams.core.MessageCollection;
import adams.core.Variables;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Loads the variables from the spreadsheet using the specified reader.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FromSpreadSheet
  extends AbstractFileBasedVariableLoader {

  private static final long serialVersionUID = -8874082255932485528L;

  /** the spreadsheet reader to use. */
  protected SpreadSheetReader m_Reader;

  /** the column with the key. */
  protected SpreadSheetColumnIndex m_ColumnKey;

  /** the column with the value. */
  protected SpreadSheetColumnIndex m_ColumnValue;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads the variables from the spreadsheet using the specified reader.\n"
	     + "The variables are loaded from the specified 'key' and 'value' columns.\n"
	     + "Only adds key/value pairs if cells are not missing.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new CsvSpreadSheetReader());

    m_OptionManager.add(
      "col-key", "columnKey",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "col-value", "columnValue",
      new SpreadSheetColumnIndex("2"));
  }

  /**
   * Sets the spreadsheet reader to use.
   *
   * @param value	the reader
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the spreadsheet reader in use.
   *
   * @return		the reader
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
    return "The spreadsheet reader to use for reading the raw data.";
  }

  /**
   * Sets the column containing the variable name.
   *
   * @param value	the column
   */
  public void setColumnKey(SpreadSheetColumnIndex value) {
    m_ColumnKey = value;
    reset();
  }

  /**
   * Returns the column containing the variable name.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnKey() {
    return m_ColumnKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnKeyTipText() {
    return "The column containing the variable name.";
  }

  /**
   * Sets the column containing the variable name.
   *
   * @param value	the column
   */
  public void setColumnValue(SpreadSheetColumnIndex value) {
    m_ColumnValue = value;
    reset();
  }

  /**
   * Returns the column containing the variable name.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnValue() {
    return m_ColumnValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnValueTipText() {
    return "The column containing the variable value.";
  }

  /**
   * Loads the variables.
   *
   * @param errors for collecting errors
   * @return the variables, null if failed to load
   */
  @Override
  protected Variables doLoadVariables(MessageCollection errors) {
    Variables		result;
    SpreadSheet		sheet;
    int			colKey;
    int			colVal;

    result = new Variables();

    sheet = m_Reader.read(m_InputFile);
    if (sheet == null) {
      errors.add("Failed to read spreadsheet: " + m_InputFile);
    }
    else {
      m_ColumnKey.setSpreadSheet(sheet);
      colKey = m_ColumnKey.getIntIndex();
      if (colKey == -1)
	errors.add("Failed to locate column with variable names: " + m_ColumnKey);

      m_ColumnValue.setSpreadSheet(sheet);
      colVal = m_ColumnValue.getIntIndex();
      if (colVal == -1)
	errors.add("Failed to locate column with variable values: " + m_ColumnValue);

      if (errors.isEmpty()) {
	for (Row row : sheet.rows()) {
	  if (!row.getCell(colKey).isEmpty() && row.hasCell(colVal) && !row.getCell(colVal).isMissing())
	    result.set(row.getCell(colKey).getContent(), row.getCell(colVal).getContent());
	}
      }
    }

    return result;
  }
}
