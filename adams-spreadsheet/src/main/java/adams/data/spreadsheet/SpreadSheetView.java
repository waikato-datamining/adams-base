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
 * SpreadSheetView.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.data.SharedStringsTable;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.Cell.ContentType;
import gnu.trove.list.array.TIntArrayList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides a view of another spreadsheet.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetView
  implements SpreadSheet {

  private static final long serialVersionUID = -3933080370817564099L;

  /** the row subset to use (null for all). */
  protected TIntArrayList m_Rows;

  /** the row array. */
  protected int[] m_RowArray;

  /** the column subset to use (null for all). */
  protected TIntArrayList m_Columns;

  /** the column array. */
  protected int[] m_ColumnArray;

  /** the underlying spreadsheet. */
  protected SpreadSheet m_Sheet;

  /** the cached header row. */
  protected HeaderRow m_HeaderRow;

  /**
   * Initializes the view.
   *
   * @param sheet	the underlying spreadsheet
   * @param columns	the columns to use, null for all
   * @param rows	the rows to use, null for all
   */
  public SpreadSheetView(SpreadSheet sheet, int[] rows, int[] columns) {
    super();

    if (sheet == null)
      throw new IllegalArgumentException("Underlying spreadsheet cannot be null!");

    m_Sheet    = sheet;
    m_Rows     = null;
    m_RowArray = rows;
    if (rows != null) {
      m_Rows = new TIntArrayList();
      m_Rows.addAll(rows);
    }
    m_Columns     = null;
    m_ColumnArray = columns;
    if (columns != null) {
      m_Columns = new TIntArrayList();
      m_Columns.addAll(columns);
    }
    m_HeaderRow = null;
  }

  /**
   * Uses this spreadsheet instead, performs no copy.
   *
   * @param sheet	the sheet to use
   */
  @Override
  public void assign(SpreadSheet sheet) {
    if (sheet == null)
      throw new IllegalArgumentException("Underlying spreadsheet cannot be null!");
    m_Sheet     = sheet;
    m_HeaderRow = null;
  }

  /**
   * Sets the default data row class to use.
   * Must implement {@link DataRow}.
   *
   * @param cls				the class, null resets it to the default one
   * @throws IllegalArgumentException	if class does not implement {@link DataRow}
   */
  @Override
  public void setDataRowClass(Class cls) {
    m_Sheet.setDataRowClass(cls);
  }

  /**
   * Returns the class used for rows.
   *
   * @return		the class
   */
  @Override
  public Class getDataRowClass() {
    return m_Sheet.getDataRowClass();
  }

  /**
   * Returns a new instance.
   *
   * @return		the new instance, null if failed to create new instance
   */
  @Override
  public SpreadSheet newInstance() {
    return m_Sheet.newInstance();
  }

  /**
   * Returns a clone of itself.
   * Creates a copy of the underlying spreadsheet!
   *
   * @return		the clone
   */
  @Override
  public SpreadSheet getClone() {
    return new SpreadSheetView(m_Sheet.getClone(), m_RowArray, m_ColumnArray);
  }

  /**
   * Returns the view with the same header and comments.
   *
   * @return		the spreadsheet
   */
  @Override
  public SpreadSheet getHeader() {
    return new SpreadSheetView(m_Sheet.getHeader(), null, m_ColumnArray);
  }

  /**
   * Returns the actual row index.
   *
   * @param rowIndex	the row in the view
   * @return		the underlying row index
   */
  protected int getActualRow(int rowIndex) {
    if (m_Rows == null)
      return rowIndex;
    else
      return m_Rows.get(rowIndex);
  }

  /**
   * Returns the actual column index.
   *
   * @param colIndex	the col in the view
   * @return		the underlying col index
   */
  protected int getActualColumn(int colIndex) {
    if (m_Columns == null)
      return colIndex;
    else
      return m_Columns.get(colIndex);
  }

  /**
   * Returns the actual row key.
   *
   * @param rowKey	the row key in the view
   * @return		the underlying row key, null if not present
   */
  protected String getActualRow(String rowKey) {
    String	result;
    int		row;

    result = null;

    if (m_Rows == null) {
      result = rowKey;
    }
    else {
      row = m_Sheet.getRowIndex(rowKey);
      if (m_Rows.contains(row))
	result = rowKey;
    }

    return result;
  }

  /**
   * Returns the actual cell key.
   *
   * @param cellKey	the cell key in the view
   * @return		the underlying cell key, null if not present
   */
  protected String getActualColumn(String cellKey) {
    String	result;
    int		cell;

    result = null;

    if (m_Columns == null) {
      result = cellKey;
    }
    else {
      cell = m_Sheet.getRowIndex(cellKey);
      if (m_Columns.contains(cell))
	result = cellKey;
    }

    return result;
  }

  /**
   * Wraps the data row in a view container.
   *
   * @param row		the row to wrap
   * @return		the wrapped row
   */
  protected DataRowView wrap(DataRow row) {
    return new DataRowView(this, row, (m_Columns == null) ? null : m_Columns.toArray());
  }

  /**
   * Returns the date formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getDateFormatter()
   */
  @Override
  public DateFormat getDateFormat() {
    return m_Sheet.getDateFormat();
  }

  /**
   * Returns the date/time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatter()
   */
  @Override
  public DateFormat getDateTimeFormat() {
    return m_Sheet.getDateTimeFormat();
  }

  /**
   * Returns the date/time msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatterMsecs()
   */
  @Override
  public DateFormat getDateTimeMsecFormat() {
    return m_Sheet.getDateTimeMsecFormat();
  }

  /**
   * Returns the time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatter()
   */
  @Override
  public DateFormat getTimeFormat() {
    return m_Sheet.getTimeFormat();
  }

  /**
   * Returns the time/msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatterMsecs()
   */
  @Override
  public DateFormat getTimeMsecFormat() {
    return m_Sheet.getTimeMsecFormat();
  }

  /**
   * Returns the number formatter.
   *
   * @return		the formatter
   */
  @Override
  public NumberFormat getNumberFormat() {
    return m_Sheet.getNumberFormat();
  }

  /**
   * Sets the name of the spreadsheet.
   *
   * @param value	the name
   */
  @Override
  public void setName(String value) {
    m_Sheet.setName(value);
  }

  /**
   * Returns the name of the spreadsheet.
   *
   * @return		the name, can be null
   */
  @Override
  public String getName() {
    return m_Sheet.getName();
  }

  /**
   * Returns whether the spreadsheet has a name.
   *
   * @return		true if the spreadsheet is named
   */
  @Override
  public boolean hasName() {
    return m_Sheet.hasName();
  }

  /**
   * Adds the comment to the internal list of comments.
   * If the comment contains newlines, then it gets automatically split
   * into multiple lines and added one by one.
   *
   * @param comment	the comment to add
   */
  @Override
  public void addComment(String comment) {
    m_Sheet.addComment(comment);
  }

  /**
   * Adds the comments to the internal list of comments.
   *
   * @param comment	the comment to add
   */
  @Override
  public void addComment(List<String> comment) {
    m_Sheet.addComment(comment);
  }

  /**
   * Returns the comments.
   *
   * @return		the comments
   */
  @Override
  public List<String> getComments() {
    return m_Sheet.getComments();
  }

  /**
   * Removes all cells, but leaves comments.
   * <br>
   * Not implemented!
   */
  @Override
  public void clear() {
    throw new NotImplementedException();
  }

  /**
   * Returns the header row.
   *
   * @return		the row
   */
  @Override
  public synchronized HeaderRow getHeaderRow() {
    HeaderRow	result;
    HeaderRow	other;
    int		i;

    if (m_Columns == null) {
      result = m_Sheet.getHeaderRow();
    }
    else {
      if (m_HeaderRow == null) {
	result = new HeaderRow(this);
	other = m_Sheet.getHeaderRow();
	for (i = 0; i < m_Columns.size(); i++)
	  result.addCell(other.getCellKey(m_Columns.get(i))).assign(other.getCell(m_Columns.get(i)));
	m_HeaderRow = result;
      }
      else {
	result = m_HeaderRow;
      }
    }

    return result;
  }

  /**
   * Returns the name of the specified column.
   *
   * @param colIndex	the index of the column
   * @return		the name of the column
   */
  @Override
  public String getColumnName(int colIndex) {
    return m_Sheet.getColumnName(getActualColumn(colIndex));
  }

  /**
   * Returns a list of the names of all columns (i.e., the content the header
   * row cells).
   *
   * @return		the names of the columns
   */
  @Override
  public List<String> getColumnNames() {
    List<String>	result;
    int			i;

    if (m_Columns == null) {
      result = m_Sheet.getColumnNames();
    }
    else {
      result = new ArrayList<>();
      for (i = 0; i < m_Columns.size(); i++)
	result.add(m_Sheet.getColumnName(i));
    }

    return result;
  }

  /**
   * Returns whether the spreadsheet already contains the row with the given index.
   *
   * @param rowIndex	the index to look for
   * @return		true if the row already exists
   */
  @Override
  public boolean hasRow(int rowIndex) {
    return m_Sheet.hasRow(getActualRow(rowIndex));
  }

  /**
   * Returns whether the spreadsheet already contains the row with the given key.
   *
   * @param rowKey	the key to look for
   * @return		true if the row already exists
   */
  @Override
  public boolean hasRow(String rowKey) {
    return (getActualRow(rowKey) != null);
  }

  /**
   * Creates a new cell.
   *
   * @return		the new instance, null in case of an instantiation error
   */
  @Override
  public Cell newCell() {
    return m_Sheet.newCell();
  }

  /**
   * Appends a row to the spreadsheet.
   * <br>
   * Not implemented!
   *
   * @return		the created row
   */
  @Override
  public DataRow addRow() {
    throw new NotImplementedException();
  }

  /**
   * Adds a row with the given key to the list and returns the created object.
   * If the row already exists, then this row is returned instead and no new
   * object created.
   * <br>
   * Not implemented!
   *
   * @param rowKey	the key for the row to create
   * @return		the created row or the already existing row
   */
  @Override
  public DataRow addRow(String rowKey) {
    throw new NotImplementedException();
  }

  /**
   * Inserts a row at the specified location.
   * <br>
   * Not implemented!
   *
   * @param index	the index where to insert the row
   * @return		the created row
   */
  @Override
  public DataRow insertRow(int index) {
    throw new NotImplementedException();
  }

  /**
   * Removes the specified row.
   * <br>
   * Not implemented!
   *
   * @param rowIndex	the row to remove
   * @return		the row that was removed, null if none removed
   */
  @Override
  public Row removeRow(int rowIndex) {
    throw new NotImplementedException();
  }

   /**
   * Removes the specified row.
   * <br>
   * Not implemented!
   *
   * @param rowKey	the row to remove
   * @return		the row that was removed, null if none removed
   */
 @Override
  public Row removeRow(String rowKey) {
    throw new NotImplementedException();
  }

  /**
   * Inserts a column at the specified location.
   * <br>
   * Not implemented!
   *
   * @param columnIndex	the position of the column
   * @param header	the name of the column
   */
  @Override
  public void insertColumn(int columnIndex, String header) {
    throw new NotImplementedException();
  }

  /**
   * Inserts a column at the specified location.
   * <br>
   * Not implemented!
   *
   * @param columnIndex	the position of the column
   * @param header	the name of the column
   * @param initial	the initial value for the cells, "null" for missing
   * 			values (in that case no cells are added)
   */
  @Override
  public void insertColumn(int columnIndex, String header, String initial) {
    throw new NotImplementedException();
  }

  /**
   * Inserts a column at the specified location.
   * <br>
   * Not implemented!
   *
   * @param columnIndex	the position of the column
   * @param header	the name of the column
   * @param initial	the initial value for the cells, "null" for missing
   * 			values (in that case no cells are added)
   * @param forceString	whether to enforce the value to be set as string
   */
  @Override
  public void insertColumn(int columnIndex, String header, String initial, boolean forceString) {
    throw new NotImplementedException();
  }

  /**
   * Removes the specified column.
   * <br>
   * Not implemented!
   *
   * @param columnIndex	the column to remove
   * @return		true if removed
   */
  @Override
  public boolean removeColumn(int columnIndex) {
    throw new NotImplementedException();
  }

  /**
   * Removes the specified column.
   * <br>
   * Not implemented!
   *
   * @param columnKey	the column to remove
   * @return		true if removed
   */
  @Override
  public boolean removeColumn(String columnKey) {
    throw new NotImplementedException();
  }

  /**
   * Returns the row associated with the given row key, null if not found.
   *
   * @param rowKey	the key of the row to retrieve
   * @return		the row or null if not found
   */
  @Override
  public DataRow getRow(String rowKey) {
    rowKey = getActualRow(rowKey);
    if (rowKey != null)
      return wrap(m_Sheet.getRow(rowKey));
    else
      return null;
  }

  /**
   * Returns the row at the specified index.
   *
   * @param rowIndex	the 0-based index of the row to retrieve
   * @return		the row
   */
  @Override
  public DataRow getRow(int rowIndex) {
    return wrap(m_Sheet.getRow(getActualRow(rowIndex)));
  }

  /**
   * Returns the row key at the specified index.
   *
   * @param rowIndex	the 0-based index of the row key to retrieve
   * @return		the row key
   */
  @Override
  public String getRowKey(int rowIndex) {
    return m_Sheet.getRowKey(getActualRow(rowIndex));
  }

  /**
   * Returns the row index of the specified row.
   *
   * @param rowKey	the row identifier
   * @return		the 0-based row index, -1 if not found
   */
  @Override
  public int getRowIndex(String rowKey) {
    int		result;
    int		row;

    result = -1;

    row = m_Sheet.getRowIndex(rowKey);
    if (m_Rows == null) {
      result = row;
    }
    else {
      if (m_Rows.contains(row))
	result = row;
    }

    return result;
  }

  /**
   * Returns the cell index of the specified cell (in the header row).
   *
   * @param cellKey	the cell identifier
   * @return		the 0-based column index, -1 if not found
   */
  @Override
  public int getCellIndex(String cellKey) {
    int		result;
    int		col;

    result = -1;

    col = m_Sheet.getCellIndex(cellKey);
    if (m_Columns == null) {
      result = col;
    }
    else {
      if (m_Columns.contains(col))
	result = col;
    }

    return result;
  }

  /**
   * Checks whether the cell with the given indices already exists.
   *
   * @param rowIndex	the index of the row to look for
   * @param columnIndex	the index of the cell in the row to look for
   * @return		true if the cell exists
   */
  @Override
  public boolean hasCell(int rowIndex, int columnIndex) {
    DataRow	row;

    row = getRow(rowIndex);
    return (row != null) && row.hasCell(columnIndex);
  }

  /**
   * Returns the corresponding cell or null if not found.
   *
   * @param rowIndex	the index of the row the cell is in
   * @param columnIndex	the column of the cell to retrieve
   * @return		the cell or null if not found
   */
  @Override
  public Cell getCell(int rowIndex, int columnIndex) {
    DataRow	row;

    row = getRow(rowIndex);
    if (row != null)
      return row.getCell(columnIndex);
    else
      return null;
  }

  /**
   * Returns the position of the cell or null if not found. A position is a
   * combination of a number of letters (for the column) and number (for the
   * row).
   *
   * @param rowKey	the key of the row the cell is in
   * @param cellKey	the key of the cell to retrieve
   * @return		the position string or null if not found
   */
  @Override
  public String getCellPosition(String rowKey, String cellKey) {
    if ((getRowIndex(rowKey) == -1) || (getCellIndex(cellKey) == -1))
      return null;
    else
      return SpreadSheetUtils.getCellPosition(getRowIndex(rowKey) + 1, getCellIndex(cellKey));
  }

  /**
   * Returns a collection of all row keys.
   *
   * @return		the row keys
   */
  @Override
  public Collection<String> rowKeys() {
    Collection<String>	result;
    int			i;

    if (m_Rows == null) {
      result = m_Sheet.rowKeys();
    }
    else {
      result = new ArrayList<>();
      for (i = 0; i < m_Rows.size(); i++)
	result.add(m_Sheet.getRowKey(m_Rows.get(i)));
    }

    return result;
  }

  /**
   * Returns all rows.
   *
   * @return		the rows
   */
  @Override
  public Collection<DataRow> rows() {
    Collection<DataRow>		result;
    int				i;

    result = new ArrayList<>();
    if (m_Rows == null) {
      for (DataRow row: m_Sheet.rows())
	result.add(wrap(row));
    }
    else {
      for (i = 0; i < m_Rows.size(); i++)
	result.add(wrap(m_Sheet.getRow(m_Rows.get(i))));
    }

    return result;
  }

  /**
   * Sorts the rows according to the row keys.
   * <br>
   * Not implemented!
   *
   * @see	#rowKeys()
   */
  @Override
  public void sortRowKeys() {
    throw new NotImplementedException();
  }

  /**
   * Sorts the rows according to the row keys.
   * <br>
   * Not implemented!
   *
   * @param comp	the comparator to use
   * @see		#rowKeys()
   */
  @Override
  public void sortRowKeys(Comparator<String> comp) {
    throw new NotImplementedException();
  }

  /**
   * Sorts the rows based on the values in the specified column.
   * <br>
   * Not implemented!
   *
   * @param index	the index (0-based) of the column to sort on
   * @param asc		wether sorting is ascending or descending
   * @see 		#sort(RowComparator)
   */
  @Override
  public void sort(int index, boolean asc) {
    throw new NotImplementedException();
  }

  /**
   * Sorts the rows using the given comparator.
   * <br>
   * Not implemented!
   *
   * @param comp	the row comparator to use
   */
  @Override
  public void sort(RowComparator comp) {
    throw new NotImplementedException();
  }

  /**
   * Sorts the rows using the given comparator.
   * <br>
   * Not implemented!
   *
   * @param comp	the row comparator to use
   * @param unique	whether to drop any duplicate rows (based on row comparator)
   */
  @Override
  public void sort(RowComparator comp, boolean unique) {
    throw new NotImplementedException();
  }

  /**
   * Returns the number of columns.
   *
   * @return		the number of columns
   */
  @Override
  public int getColumnCount() {
    if (m_Columns == null)
      return m_Sheet.getColumnCount();
    else
      return m_Columns.size();
  }

  /**
   * Returns the number of rows currently stored.
   *
   * @return		the number of rows
   */
  @Override
  public int getRowCount() {
    if (m_Rows == null)
      return m_Sheet.getRowCount();
    else
      return m_Rows.size();
  }

  /**
   * Checks whether the given column is numeric or not. Does not accept
   * missing values.
   *
   * @param columnIndex	the index of the column to check
   * @return		true if purely numeric
   * @see		#getContentTypes(int)
   */
  @Override
  public boolean isNumeric(int columnIndex) {
    return m_Sheet.isNumeric(getActualColumn(columnIndex));
  }

  /**
   * Checks whether the given column is numeric or not. Can accept missing
   * values.
   *
   * @param columnIndex	the index of the column to check
   * @return		true if purely numeric
   * @see		#getContentTypes(int)
   */
  @Override
  public boolean isNumeric(int columnIndex, boolean allowMissing) {
    return m_Sheet.isNumeric(getActualColumn(columnIndex), allowMissing);
  }

  /**
   * Checks whether the given column is of the specific content type or not.
   *
   * @param columnIndex	the index of the column to check
   * @param type	the content type to check
   * @return		true if column purely consists of this content type
   * @see		#getContentType(int)
   */
  @Override
  public boolean isContentType(int columnIndex, ContentType type) {
    return m_Sheet.isContentType(getActualColumn(columnIndex), type);
  }

  /**
   * Returns the pure content type of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content type that this column consists of solely, null if mixed
   */
  @Override
  public ContentType getContentType(int columnIndex) {
    return m_Sheet.getContentType(getActualColumn(columnIndex));
  }

  /**
   * Returns the all content types of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content types that this column consists of
   */
  @Override
  public Collection<ContentType> getContentTypes(int columnIndex) {
    return m_Sheet.getContentTypes(getActualColumn(columnIndex));
  }

  /**
   * Returns the unique string values of the specified column. The returned
   * list is sorted.
   *
   * @param colKey	the column to retrieve the values for
   * @return		the sorted, list of unique values
   */
  @Override
  public List<String> getCellValues(String colKey) {
    colKey = getActualColumn(colKey);
    if (colKey != null)
      return m_Sheet.getCellValues(colKey);
    else
      return new ArrayList<>();
  }

  /**
   * Returns the unique string values of the specified column. The returned
   * list is sorted.
   *
   * @param colIndex	the column to retrieve the values for
   * @return		the sorted, list of unique values
   */
  @Override
  public List<String> getCellValues(int colIndex) {
    return m_Sheet.getCellValues(getActualColumn(colIndex));
  }

  /**
   * Compares the header of this spreadsheet with the other one.
   *
   * @param other	the other spreadsheet to compare with
   * @return		null if equal, otherwise details what differs
   */
  @Override
  public String equalsHeader(SpreadSheet other) {
    String	result;
    Row		header;
    Row		otherHeader;
    int		i;

    result = null;

    if (other == null)
      return result;

    header      = getHeaderRow();
    otherHeader = other.getHeaderRow();

    if (header.getCellCount() != otherHeader.getCellCount())
      result = "Number of columns differ: " + header.getCellCount() + " != " + otherHeader.getCellCount();

    if (result == null) {
      for (i = 0; i < header.getCellCount(); i++) {
	if (!header.getCell(i).getContent().equals(otherHeader.getCell(i).getContent())) {
	  result = "Column header #" + (i+1) + " differs: " + header.getCell(i).getContent() + " != " + otherHeader.getCell(i).getContent();
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Removes all cells marked "missing".
   * <br>
   * Not implemented!
   *
   * @return		true if any cell was removed
   */
  @Override
  public boolean removeMissing() {
    throw new NotImplementedException();
  }

  /**
   * Returns the table for shared strings.
   *
   * @return		the table
   */
  @Override
  public SharedStringsTable getSharedStringsTable() {
    return m_Sheet.getSharedStringsTable();
  }

  /**
   * Sets whether parsing of dates is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  @Override
  public void setDateLenient(boolean value) {
    m_Sheet.setDateLenient(value);
  }

  /**
   * Returns whether the parsing of dates is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  @Override
  public boolean isDateLenient() {
    return m_Sheet.isDateLenient();
  }

  /**
   * Sets whether parsing of date/times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  @Override
  public void setDateTimeLenient(boolean value) {
    m_Sheet.setDateTimeLenient(value);
  }

  /**
   * Returns whether the parsing of date/times is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  @Override
  public boolean isDateTimeLenient() {
    return m_Sheet.isDateTimeLenient();
  }

  /**
   * Sets whether parsing of date/time mses is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  @Override
  public void setDateTimeMsecLenient(boolean value) {
    m_Sheet.setDateTimeMsecLenient(value);
  }

  /**
   * Returns whether the parsing of date/time msecs is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  @Override
  public boolean isDateTimeMsecLenient() {
    return m_Sheet.isDateTimeMsecLenient();
  }

  /**
   * Sets whether parsing of times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  @Override
  public void setTimeLenient(boolean value) {
    m_Sheet.setTimeLenient(value);
  }

  /**
   * Returns whether the parsing of times is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  @Override
  public boolean isTimeLenient() {
    return m_Sheet.isTimeLenient();
  }

  /**
   * Sets whether parsing of times/msec is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  @Override
  public void setTimeMsecLenient(boolean value) {
    m_Sheet.setTimeMsecLenient(value);
  }

  /**
   * Returns whether the parsing of times/msec is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  @Override
  public boolean isTimeMsecLenient() {
    return m_Sheet.isTimeMsecLenient();
  }

  /**
   * Sets the timezone to use.
   *
   * @param value	the new timezone
   * @see		SimpleDateFormat#setTimeZone(TimeZone)
   */
  @Override
  public void setTimeZone(TimeZone value) {
    m_Sheet.setTimeZone(value);
  }

  /**
   * Returns the currently used timezone.
   *
   * @return		the current timezone
   * @see		SimpleDateFormat#getTimeZone()
   */
  @Override
  public TimeZone getTimeZone() {
    return m_Sheet.getTimeZone();
  }

  /**
   * Sets the locale. Used in formatting/parsing numbers.
   *
   * @param value	the locale to use
   */
  @Override
  public void setLocale(Locale value) {
    m_Sheet.setLocale(value);
  }

  /**
   * Returns the current locale.
   *
   * @return		the locale
   */
  @Override
  public Locale getLocale() {
    return m_Sheet.getLocale();
  }

  /**
   * Triggers all formula cells to recalculate their values.
   */
  @Override
  public void calculate() {
    m_Sheet.calculate();
  }

  /**
   * Puts the content of the provided spreadsheet on the right.
   * <br>
   * Not implemented!
   *
   * @param other	the spreadsheet to merge with
   */
  @Override
  public void mergeWith(SpreadSheet other) {
    throw new NotImplementedException();
  }

  /**
   * Returns the underlying sheet.
   *
   * @return		the underlying sheet
   */
  public SpreadSheet getSheet() {
    return m_Sheet;
  }

  /**
   * Returns the spreadsheet as matrix, with the header as the first row.
   * Missing values are represented as null values.
   *
   * @return		the row-wise matrix
   */
  @Override
  public Object[][] toMatrix() {
    Object[][]	result;
    int		i;
    int		n;
    Row		row;
    String	key;
    Cell	cell;

    result = new Object[getRowCount() + 1][getColumnCount()];

    // header
    row = getHeaderRow();
    for (i = 0; i < getColumnCount(); i++) {
      if (row.getCell(i).isMissing())
	continue;
      result[0][i] = row.getCell(i).getContent();
    }

    // data
    for (n = 0; n < getRowCount(); n++) {
      row = getRow(n);
      for (i = 0; i < getColumnCount(); i++) {
	key = getHeaderRow().getCellKey(i);
	if (!row.hasCell(key))
	  continue;
	cell = row.getCell(i);
	if (cell.isMissing())
	  continue;
	if (cell.isNumeric())
	  result[n + 1][i] = row.getCell(i).toDouble();
	else
	  result[n + 1][i] = row.getCell(i).getContent();
      }
    }

    return result;
  }

  /**
   * Returns the spreadsheet as string, i.e., CSV formatted.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringWriter writer;

    writer = new StringWriter();
    new CsvSpreadSheetWriter().write(this, writer);

    return writer.toString();
  }
}
