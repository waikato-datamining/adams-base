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
 * DatasetView.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.data;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.exception.NotImplementedException;
import adams.data.SharedStringsTable;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.DataRowView;
import adams.data.spreadsheet.HeaderRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import gnu.trove.list.array.TIntArrayList;

import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides a view of another dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetView
  implements Dataset {

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
  protected Dataset m_Dataset;

  /** the cached header row. */
  protected HeaderRow m_HeaderRow;

  /**
   * Initializes the view with a dummy dataset.
   */
  public DatasetView() {
    this(new DefaultDataset(), null, null);
  }

  /**
   * Initializes the view.
   *
   * @param dataset	the underlying dataset
   * @param rows	the rows to use, null for all
   * @param columns	the columns to use, null for all
   */
  public DatasetView(Dataset dataset, int[] rows, int[] columns) {
    super();

    if (dataset == null)
      throw new IllegalArgumentException("Underlying spreadsheet cannot be null!");

    m_Dataset = dataset;
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
      throw new IllegalArgumentException("Underlying dataset cannot be null!");
    if (!(sheet instanceof Dataset))
      throw new IllegalArgumentException("Must be a dataset!");
    m_Dataset = (Dataset) sheet;
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
    m_Dataset.setDataRowClass(cls);
  }

  /**
   * Returns the class used for rows.
   *
   * @return		the class
   */
  @Override
  public Class getDataRowClass() {
    return m_Dataset.getDataRowClass();
  }

  /**
   * Returns a new instance.
   *
   * @return		the new instance, null if failed to create new instance
   */
  @Override
  public SpreadSheet newInstance() {
    return m_Dataset.newInstance();
  }

  /**
   * Returns a clone of itself.
   * Creates a copy of the underlying spreadsheet!
   *
   * @return		the clone
   */
  @Override
  public Dataset getClone() {
    return new DatasetView(m_Dataset.getClone(), m_RowArray, m_ColumnArray);
  }

  /**
   * Returns the view with the same header and comments.
   *
   * @return		the spreadsheet
   */
  @Override
  public Dataset getHeader() {
    return new DatasetView(m_Dataset.getHeader(), null, m_ColumnArray);
  }

  /**
   * Returns the index of the column using the specified name.
   *
   * @param name	the name of the column to locate
   * @return		the index, -1 if failed to locate
   */
  public int indexOfColumn(String name) {
    return getHeaderRow().indexOfContent(name);
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
      row = m_Dataset.getRowIndex(rowKey);
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
      cell = m_Dataset.getHeaderRow().indexOf(cellKey);
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
    return m_Dataset.getDateFormat();
  }

  /**
   * Returns the date/time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatter()
   */
  @Override
  public DateFormat getDateTimeFormat() {
    return m_Dataset.getDateTimeFormat();
  }

  /**
   * Returns the date/time msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatterMsecs()
   */
  @Override
  public DateFormat getDateTimeMsecFormat() {
    return m_Dataset.getDateTimeMsecFormat();
  }

  /**
   * Returns the time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatter()
   */
  @Override
  public DateFormat getTimeFormat() {
    return m_Dataset.getTimeFormat();
  }

  /**
   * Returns the time/msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatterMsecs()
   */
  @Override
  public DateFormat getTimeMsecFormat() {
    return m_Dataset.getTimeMsecFormat();
  }

  /**
   * Returns the number formatter.
   *
   * @return		the formatter
   */
  @Override
  public NumberFormat getNumberFormat() {
    return m_Dataset.getNumberFormat();
  }

  /**
   * Sets the name of the spreadsheet.
   *
   * @param value	the name
   */
  @Override
  public void setName(String value) {
    m_Dataset.setName(value);
  }

  /**
   * Returns the name of the spreadsheet.
   *
   * @return		the name, can be null
   */
  @Override
  public String getName() {
    return m_Dataset.getName();
  }

  /**
   * Returns whether the spreadsheet has a name.
   *
   * @return		true if the spreadsheet is named
   */
  @Override
  public boolean hasName() {
    return m_Dataset.hasName();
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
    m_Dataset.addComment(comment);
  }

  /**
   * Adds the comments to the internal list of comments.
   *
   * @param comment	the comment to add
   */
  @Override
  public void addComment(List<String> comment) {
    m_Dataset.addComment(comment);
  }

  /**
   * Returns the comments.
   *
   * @return		the comments
   */
  @Override
  public List<String> getComments() {
    return m_Dataset.getComments();
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
      result = m_Dataset.getHeaderRow();
    }
    else {
      if (m_HeaderRow == null) {
	result = new HeaderRow(this);
	other = m_Dataset.getHeaderRow();
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
    return m_Dataset.getColumnName(getActualColumn(colIndex));
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
      result = m_Dataset.getColumnNames();
    }
    else {
      result = new ArrayList<>();
      for (i = 0; i < m_Columns.size(); i++)
	result.add(m_Dataset.getColumnName(i));
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
    return m_Dataset.hasRow(getActualRow(rowIndex));
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
    return m_Dataset.newCell();
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
      return wrap(m_Dataset.getRow(rowKey));
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
    return wrap(m_Dataset.getRow(getActualRow(rowIndex)));
  }

  /**
   * Returns the row key at the specified index.
   *
   * @param rowIndex	the 0-based index of the row key to retrieve
   * @return		the row key
   */
  @Override
  public String getRowKey(int rowIndex) {
    return m_Dataset.getRowKey(getActualRow(rowIndex));
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

    row = m_Dataset.getRowIndex(rowKey);
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

    col = m_Dataset.getCellIndex(cellKey);
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
      result = m_Dataset.rowKeys();
    }
    else {
      result = new ArrayList<>();
      for (i = 0; i < m_Rows.size(); i++)
	result.add(m_Dataset.getRowKey(m_Rows.get(i)));
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
      for (DataRow row: m_Dataset.rows())
	result.add(wrap(row));
    }
    else {
      for (i = 0; i < m_Rows.size(); i++)
	result.add(wrap(m_Dataset.getRow(m_Rows.get(i))));
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
      return m_Dataset.getColumnCount();
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
      return m_Dataset.getRowCount();
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
    return m_Dataset.isNumeric(getActualColumn(columnIndex));
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
    return m_Dataset.isNumeric(getActualColumn(columnIndex), allowMissing);
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
    return m_Dataset.isContentType(getActualColumn(columnIndex), type);
  }

  /**
   * Returns the pure content type of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content type that this column consists of solely, null if mixed
   */
  @Override
  public ContentType getContentType(int columnIndex) {
    return m_Dataset.getContentType(getActualColumn(columnIndex));
  }

  /**
   * Returns the all content types of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content types that this column consists of
   */
  @Override
  public Collection<ContentType> getContentTypes(int columnIndex) {
    return m_Dataset.getContentTypes(getActualColumn(columnIndex));
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
      return m_Dataset.getCellValues(colKey);
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
    return m_Dataset.getCellValues(getActualColumn(colIndex));
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
    return m_Dataset.getSharedStringsTable();
  }

  /**
   * Sets whether parsing of dates is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  @Override
  public void setDateLenient(boolean value) {
    m_Dataset.setDateLenient(value);
  }

  /**
   * Returns whether the parsing of dates is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  @Override
  public boolean isDateLenient() {
    return m_Dataset.isDateLenient();
  }

  /**
   * Sets whether parsing of date/times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  @Override
  public void setDateTimeLenient(boolean value) {
    m_Dataset.setDateTimeLenient(value);
  }

  /**
   * Returns whether the parsing of date/times is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  @Override
  public boolean isDateTimeLenient() {
    return m_Dataset.isDateTimeLenient();
  }

  /**
   * Sets whether parsing of date/time mses is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  @Override
  public void setDateTimeMsecLenient(boolean value) {
    m_Dataset.setDateTimeMsecLenient(value);
  }

  /**
   * Returns whether the parsing of date/time msecs is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  @Override
  public boolean isDateTimeMsecLenient() {
    return m_Dataset.isDateTimeMsecLenient();
  }

  /**
   * Sets whether parsing of times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  @Override
  public void setTimeLenient(boolean value) {
    m_Dataset.setTimeLenient(value);
  }

  /**
   * Returns whether the parsing of times is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  @Override
  public boolean isTimeLenient() {
    return m_Dataset.isTimeLenient();
  }

  /**
   * Sets whether parsing of times/msec is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  @Override
  public void setTimeMsecLenient(boolean value) {
    m_Dataset.setTimeMsecLenient(value);
  }

  /**
   * Returns whether the parsing of times/msec is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  @Override
  public boolean isTimeMsecLenient() {
    return m_Dataset.isTimeMsecLenient();
  }

  /**
   * Sets the timezone to use.
   *
   * @param value	the new timezone
   * @see		SimpleDateFormat#setTimeZone(TimeZone)
   */
  @Override
  public void setTimeZone(TimeZone value) {
    m_Dataset.setTimeZone(value);
  }

  /**
   * Returns the currently used timezone.
   *
   * @return		the current timezone
   * @see		SimpleDateFormat#getTimeZone()
   */
  @Override
  public TimeZone getTimeZone() {
    return m_Dataset.getTimeZone();
  }

  /**
   * Sets the locale. Used in formatting/parsing numbers.
   *
   * @param value	the locale to use
   */
  @Override
  public void setLocale(Locale value) {
    m_Dataset.setLocale(value);
  }

  /**
   * Returns the current locale.
   *
   * @return		the locale
   */
  @Override
  public Locale getLocale() {
    return m_Dataset.getLocale();
  }

  /**
   * Triggers all formula cells to recalculate their values.
   */
  @Override
  public void calculate() {
    m_Dataset.calculate();
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
   * Returns the underlying dataset.
   *
   * @return		the underlying dataset
   */
  public Dataset getDataset() {
    return m_Dataset;
  }

  /**
   * Removes all set class attributes.
   * <br>
   * Not implemented!
   */
  public void removeClassAttributes() {
    throw new NotImplementedException();
  }

  /**
   * Returns whether the specified column is a class attribute.
   *
   * @param colKey	they key of the column to query
   * @return		true if column a class attribute
   */
  public boolean isClassAttribute(String colKey) {
    if ((colKey == null) || (getActualColumn(colKey) == null))
      return false;
    else
      return m_Dataset.isClassAttribute(getActualColumn(colKey));
  }

  /**
   * Returns whether the specified column is a class attribute.
   *
   * @param colIndex	they index of the column to query
   * @return		true if column a class attribute
   */
  public boolean isClassAttribute(int colIndex) {
    if (colIndex > -1)
      return isClassAttribute(m_HeaderRow.getCellKey(colIndex));
    else
      return false;
  }

  /**
   * Returns whether the specified column is a class attribute.
   *
   * @param name	they name of the column to query
   * @return		true if column a class attribute
   */
  public boolean isClassAttributeByName(String name) {
    return isClassAttribute(getHeaderRow().indexOfContent(name));
  }

  /**
   * Sets the class attribute status for a column.
   * <br>
   * Not implemented!
   *
   * @param colKey	the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  public boolean setClassAttribute(String colKey, boolean isClass) {
    throw new NotImplementedException();
  }

  /**
   * Sets the class attribute status for a column.
   * <br>
   * Not implemented!
   *
   * @param colIndex	the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  public boolean setClassAttribute(int colIndex, boolean isClass) {
    throw new NotImplementedException();
  }

  /**
   * Sets the class attribute status for a column.
   * <br>
   * Not implemented!
   *
   * @param name	the name of the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  public boolean setClassAttributeByName(String name, boolean isClass) {
    throw new NotImplementedException();
  }

  /**
   * Returns all the class attributes that are currently set.
   *
   * @return		the column keys of class attributes (not ordered)
   */
  public String[] getClassAttributeKeys() {
    List<String>	result;

    result = new ArrayList<>();
    for (String key: m_Dataset.getClassAttributeKeys()) {
      key = getActualColumn(key);
      if (key != null)
	result.add(key);
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns all the class attributes that are currently set.
   *
   * @return		the column names of class attributes (not ordered)
   */
  public String[] getClassAttributeNames() {
    List<String>	result;

    result = new ArrayList<>();
    for (int index: getClassAttributeIndices())
      result.add(getColumnName(index));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns all the class attributes that are currently set.
   *
   * @return		the indices of class attributes (sorted asc)
   */
  public int[] getClassAttributeIndices() {
    int[]	result;
    String[]	keys;
    int		i;

    keys   = getClassAttributeKeys();
    result = new int[keys.length];
    for (i = 0; i < keys.length; i++)
      result[i] = getHeaderRow().indexOf(keys[i]);
    Arrays.sort(result);

    return result;
  }

  /**
   * Returns a spreadsheet containing only the input columns, not class
   * columns.
   *
   * @return		the input features, null if data conists only of class columns
   */
  public SpreadSheet getInputs() {
    SpreadSheet		result;
    TIntArrayList	indices;
    int			i;
    Row			newRow;

    if (getClassAttributeKeys().length == 0)
      return getClone();
    else if (getClassAttributeKeys().length == getColumnCount())
      return null;

    // determine indices
    indices = new TIntArrayList();
    for (i = 0; i < getColumnCount(); i++) {
      if (!isClassAttribute(i))
	indices.add(i);
    }

    result = newInstance();

    // header
    newRow = result.getHeaderRow();
    for (i = 0; i < indices.size(); i++)
      newRow.addCell("" + i).assign(getHeaderRow().getCell(indices.get(i)));

    // data
    for (Row row: rows()) {
      newRow = result.addRow();
      for (i = 0; i < indices.size(); i++) {
	if (row.hasCell(indices.get(i)))
	  newRow.addCell(i).assign(row.getCell(indices.get(i)));
      }
    }

    return result;
  }

  /**
   * Returns a spreadsheet containing only output columns, i.e., the class
   * columns.
   *
   * @return		the output features, null if data has no class columns
   */
  public SpreadSheet getOutputs() {
    SpreadSheet		result;
    TIntArrayList	indices;
    int			i;
    Row			newRow;

    if (getClassAttributeKeys().length == 0)
      return null;
    else if (getClassAttributeKeys().length == getColumnCount())
      return getClone();

    // determine indices
    indices = new TIntArrayList();
    for (i = 0; i < getColumnCount(); i++) {
      if (isClassAttribute(i))
	indices.add(i);
    }

    result = newInstance();

    // header
    newRow = result.getHeaderRow();
    for (i = 0; i < indices.size(); i++)
      newRow.addCell("" + i).assign(getHeaderRow().getCell(indices.get(i)));

    // data
    for (Row row: rows()) {
      newRow = result.addRow();
      for (i = 0; i < indices.size(); i++) {
	if (row.hasCell(indices.get(i)))
	  newRow.addCell(i).assign(row.getCell(indices.get(i)));
      }
    }

    return result;
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

  /**
   * Creates a view of the spreadsheet with the specified rows/columns.
   *
   * @param columns	the columns to use, null for all
   * @param rows	the rows to use, null for all
   * @return		the view
   */
  public DatasetView toView(int[] rows, int[] columns) {
    return new DatasetView(this, rows, columns);
  }
}
