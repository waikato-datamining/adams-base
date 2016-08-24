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
 * SpreadSheetToCreateTableStatement.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet into a SQL 'CREATE TABLE' statement.<br>
 * Requires two columns: column names, SQL column types.<br>
 * An optional 3rd column can be used to indicate whether a column is to be used as an index (boolean).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-table-name &lt;java.lang.String&gt; (property: tableName)
 * &nbsp;&nbsp;&nbsp;The name of the table.
 * &nbsp;&nbsp;&nbsp;default: newtable
 * </pre>
 * 
 * <pre>-name-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: nameColumn)
 * &nbsp;&nbsp;&nbsp;The column in the spreadsheet that holds the name of the table columns.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-type-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: typeColumn)
 * &nbsp;&nbsp;&nbsp;The column in the spreadsheet that holds the SQL types for the table columns.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-index-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: indexColumn)
 * &nbsp;&nbsp;&nbsp;The column in the spreadsheet that indicates whether a column should be 
 * &nbsp;&nbsp;&nbsp;an index.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToCreateTableStatement
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4890225060389916155L;

  /** the name of the table to create. */
  protected String m_TableName;

  /** the name colum. */
  protected SpreadSheetColumnIndex m_NameColumn;

  /** the type colum. */
  protected SpreadSheetColumnIndex m_TypeColumn;

  /** the colum with the indicator for index (optional). */
  protected SpreadSheetColumnIndex m_IndexColumn;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Turns a spreadsheet into a SQL 'CREATE TABLE' statement.\n"
        + "Requires two columns: column names, SQL column types.\n"
        + "An optional 3rd column can be used to indicate whether a column is to be used as an index (boolean).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "table-name", "tableName",
      "newtable");

    m_OptionManager.add(
      "name-column", "nameColumn",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "type-column", "typeColumn",
      new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
      "index-column", "indexColumn",
      new SpreadSheetColumnIndex("3"));
  }

  /**
   * Sets the name of the table.
   *
   * @param value	the name
   */
  public void setTableName(String value) {
    m_TableName = value;
    reset();
  }

  /**
   * Returns the name of the table.
   *
   * @return		the name
   */
  public String getTableName() {
    return m_TableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tableNameTipText() {
    return "The name of the table.";
  }

  /**
   * Sets the name column.
   *
   * @param value	the column
   */
  public void setNameColumn(SpreadSheetColumnIndex value) {
    m_NameColumn = value;
    reset();
  }

  /**
   * Returns the name column.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getNameColumn() {
    return m_NameColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameColumnTipText() {
    return "The column in the spreadsheet that holds the name of the table columns.";
  }

  /**
   * Sets the type column.
   *
   * @param type	the column
   */
  public void setTypeColumn(SpreadSheetColumnIndex type) {
    m_TypeColumn = type;
    reset();
  }

  /**
   * Returns the type column.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getTypeColumn() {
    return m_TypeColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeColumnTipText() {
    return "The column in the spreadsheet that holds the SQL types for the table columns.";
  }

  /**
   * Sets the index column.
   *
   * @param index	the column
   */
  public void setIndexColumn(SpreadSheetColumnIndex index) {
    m_IndexColumn = index;
    reset();
  }

  /**
   * Returns the index column.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getIndexColumn() {
    return m_IndexColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexColumnTipText() {
    return "The column in the spreadsheet that indicates whether a column should be an index.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet 	sheet;
    StringBuilder	result;
    int 		colName;
    int 		colType;
    int 		colIndex;
    boolean		first;
    StringBuilder	index;

    result = new StringBuilder();
    index  = new StringBuilder();
    sheet  = (SpreadSheet) m_Input;
    m_NameColumn.setData(sheet);
    colName = m_NameColumn.getIntIndex();
    if (colName == -1)
      throw new Exception("Name column '" + m_NameColumn + "' not found!");
    m_TypeColumn.setData(sheet);
    colType = m_TypeColumn.getIntIndex();
    if (colType == -1)
      throw new Exception("Type column '" + m_TypeColumn + "' not found!");
    m_IndexColumn.setData(sheet);
    colIndex = m_IndexColumn.getIntIndex();

    result.append("CREATE TABLE " + m_TableName + " (");
    first = true;
    for (Row row: sheet.rows()) {
      if (!row.hasCell(colName) || row.getCell(colName).isMissing())
	continue;
      if (!row.hasCell(colType) || row.getCell(colType).isMissing())
	continue;
      if (!first)
	result.append(",");
      result.append("\n");
      result.append("  ").append(row.getCell(colName).getContent());
      result.append(" ").append(row.getCell(colType).getContent());
      if (colIndex > -1) {
	if (row.hasCell(colIndex) && !row.getCell(colIndex).isMissing()) {
	  index.append("CREATE INDEX ");
	  index.append(m_TableName.toLowerCase()).append("_idx_").append(row.getCell(colName).getContent().toLowerCase());
	  index.append(" ON ").append(m_TableName);
	  index.append(" (").append(row.getCell(colName).getContent()).append(");\n");
	}
      }
      first = false;
    }
    result.append("\n);");
    if (index.length() > 0)
      result.append("\n").append(index.toString());

    return result;
  }
}
