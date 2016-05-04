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
 * SpreadSheet.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import adams.core.CloneHandler;
import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Mergeable;
import adams.core.management.LocaleSupporter;
import adams.data.SharedStringsTable;
import adams.data.spreadsheet.Cell.ContentType;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents a generic spreadsheet object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 12570 $
 */
public interface SpreadSheet
  extends Serializable, CloneHandler<SpreadSheet>, LocaleSupporter,
             Mergeable<SpreadSheet> {

  /** the line comment start. */
  public final static String COMMENT = "#";

  /** the default missing value. */
  public final static String MISSING_VALUE = "?";

  /**
   * Clears this spreadsheet and copies all the data from the given one.
   * 
   * @param sheet	the data to copy
   */
  public void assign(SpreadSheet sheet);

  /**
   * Sets the default data row class to use.
   * Must implement {@link DataRow}.
   *
   * @param cls				the class, null resets it to the default one
   * @throws IllegalArgumentException	if class does not implement {@link DataRow}
   */
  public void setDataRowClass(Class cls);

  /**
   * Returns the class used for rows.
   *
   * @return		the class
   */
  public Class getDataRowClass();

  /**
   * Returns a new instance.
   * 
   * @return		the new instance, null if failed to create new instance
   */
  public SpreadSheet newInstance();

  /**
   * Returns a clone of itself.
   *
   * @return		the clone
   */
  @Override
  public SpreadSheet getClone();

  /**
   * Returns the a spreadsheet with the same header and comments.
   *
   * @return		the spreadsheet
   */
  public SpreadSheet getHeader();

  /**
   * Returns the date formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getDateFormatter()
   */
  public DateFormat getDateFormat();

  /**
   * Returns the date/time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatter()
   */
  public DateFormat getDateTimeFormat();

  /**
   * Returns the date/time msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatterMsecs()
   */
  public DateFormat getDateTimeMsecFormat();

  /**
   * Returns the time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatter()
   */
  public DateFormat getTimeFormat();

  /**
   * Returns the time/msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatterMsecs()
   */
  public DateFormat getTimeMsecFormat();

  /**
   * Returns the number formatter.
   * 
   * @return		the formatter
   */
  public NumberFormat getNumberFormat();

  /**
   * Sets the name of the spreadsheet.
   *
   * @param value	the name
   */
  public void setName(String value);

  /**
   * Returns the name of the spreadsheet.
   *
   * @return		the name, can be null
   */
  public String getName();

  /**
   * Returns whether the spreadsheet has a name.
   *
   * @return		true if the spreadsheet is named
   */
  public boolean hasName();

  /**
   * Adds the comment to the internal list of comments.
   * If the comment contains newlines, then it gets automatically split
   * into multiple lines and added one by one.
   *
   * @param comment	the comment to add
   */
  public void addComment(String comment);

  /**
   * Adds the comments to the internal list of comments.
   *
   * @param comment	the comment to add
   */
  public void addComment(List<String> comment);

  /**
   * Returns the comments.
   *
   * @return		the comments
   */
  public List<String> getComments();

  /**
   * Removes all cells, but leaves comments.
   */
  public void clear();

  /**
   * Returns the header row.
   *
   * @return		the row
   */
  public HeaderRow getHeaderRow();

  /**
   * Returns the name of the specified column.
   *
   * @param colIndex	the index of the column
   * @return		the name of the column
   */
  public String getColumnName(int colIndex);

  /**
   * Returns a list of the names of all columns (i.e., the content the header
   * row cells).
   *
   * @return		the names of the columns
   */
  public List<String> getColumnNames();

  /**
   * Returns whether the spreadsheet already contains the row with the given index.
   *
   * @param rowIndex	the index to look for
   * @return		true if the row already exists
   */
  public boolean hasRow(int rowIndex);

  /**
   * Returns whether the spreadsheet already contains the row with the given key.
   *
   * @param rowKey	the key to look for
   * @return		true if the row already exists
   */
  public boolean hasRow(String rowKey);

  /**
   * Creates a new cell.
   *
   * @return		the new instance, null in case of an instantiation error
   */
  public Cell newCell();

  /**
   * Appends a row to the spreadsheet.
   *
   * @return		the created row
   */
  public DataRow addRow();

  /**
   * Adds a row with the given key to the list and returns the created object.
   * If the row already exists, then this row is returned instead and no new
   * object created.
   *
   * @param rowKey	the key for the row to create
   * @return		the created row or the already existing row
   */
  public DataRow addRow(String rowKey);

  /**
   * Inserts a row at the specified location.
   *
   * @param index	the index where to insert the row
   * @return		the created row
   */
  public DataRow insertRow(int index);

  /**
   * Removes the specified row.
   *
   * @param rowIndex	the row to remove
   * @return		the row that was removed, null if none removed
   */
  public Row removeRow(int rowIndex);

  /**
   * Removes the specified row.
   *
   * @param rowKey	the row to remove
   * @return		the row that was removed, null if none removed
   */
  public Row removeRow(String rowKey);

  /**
   * Inserts a column at the specified location.
   *
   * @param columnIndex	the position of the column
   * @param header	the name of the column
   */
  public void insertColumn(int columnIndex, String header);

  /**
   * Inserts a column at the specified location.
   *
   * @param columnIndex	the position of the column
   * @param header	the name of the column
   * @param initial	the initial value for the cells, "null" for missing
   * 			values (in that case no cells are added)
   */
  public void insertColumn(int columnIndex, String header, String initial);

  /**
   * Inserts a column at the specified location.
   *
   * @param columnIndex	the position of the column
   * @param header	the name of the column
   * @param initial	the initial value for the cells, "null" for missing
   * 			values (in that case no cells are added)
   * @param forceString	whether to enforce the value to be set as string
   */
  public void insertColumn(int columnIndex, String header, String initial, boolean forceString);

  /**
   * Removes the specified column.
   *
   * @param columnIndex	the column to remove
   * @return		true if removed
   */
  public boolean removeColumn(int columnIndex);

  /**
   * Removes the specified column.
   *
   * @param columnKey	the column to remove
   * @return		true if removed
   */
  public boolean removeColumn(String columnKey);

  /**
   * Returns the row associated with the given row key, null if not found.
   *
   * @param rowKey	the key of the row to retrieve
   * @return		the row or null if not found
   */
  public DataRow getRow(String rowKey);

  /**
   * Returns the row at the specified index.
   *
   * @param rowIndex	the 0-based index of the row to retrieve
   * @return		the row
   */
  public DataRow getRow(int rowIndex);

  /**
   * Returns the row key at the specified index.
   *
   * @param rowIndex	the 0-based index of the row key to retrieve
   * @return		the row key
   */
  public String getRowKey(int rowIndex);

  /**
   * Returns the row index of the specified row.
   *
   * @param rowKey	the row identifier
   * @return		the 0-based row index, -1 if not found
   */
  public int getRowIndex(String rowKey);

  /**
   * Returns the cell index of the specified cell (in the header row).
   *
   * @param cellKey	the cell identifier
   * @return		the 0-based column index, -1 if not found
   */
  public int getCellIndex(String cellKey);

  /**
   * Checks whether the cell with the given indices already exists.
   *
   * @param rowIndex	the index of the row to look for
   * @param columnIndex	the index of the cell in the row to look for
   * @return		true if the cell exists
   */
  public boolean hasCell(int rowIndex, int columnIndex);

  /**
   * Returns the corresponding cell or null if not found.
   *
   * @param rowIndex	the index of the row the cell is in
   * @param columnIndex	the column of the cell to retrieve
   * @return		the cell or null if not found
   */
  public Cell getCell(int rowIndex, int columnIndex);

  /**
   * Returns the position of the cell or null if not found. A position is a
   * combination of a number of letters (for the column) and number (for the
   * row).
   *
   * @param rowKey	the key of the row the cell is in
   * @param cellKey	the key of the cell to retrieve
   * @return		the position string or null if not found
   */
  public String getCellPosition(String rowKey, String cellKey);

  /**
   * Returns a collection of all row keys.
   *
   * @return		the row keys
   */
  public Collection<String> rowKeys();

  /**
   * Returns all rows.
   *
   * @return		the rows
   */
  public Collection<DataRow> rows();

  /**
   * Sorts the rows according to the row keys.
   *
   * @see	#rowKeys()
   */
  public void sortRowKeys();

  /**
   * Sorts the rows according to the row keys.
   *
   * @param comp	the comparator to use
   * @see		#rowKeys()
   */
  public void sortRowKeys(Comparator<String> comp);

  /**
   * Sorts the rows based on the values in the specified column.
   * <br><br>
   * NB: the row keys will change!
   *
   * @param index	the index (0-based) of the column to sort on
   * @param asc		wether sorting is ascending or descending
   * @see 		#sort(RowComparator)
   */
  public void sort(int index, boolean asc);

  /**
   * Sorts the rows using the given comparator.
   * <br><br>
   * NB: the row keys will change!
   *
   * @param comp	the row comparator to use
   */
  public void sort(RowComparator comp);

  /**
   * Sorts the rows using the given comparator.
   * <br><br>
   * NB: the row keys will change!
   *
   * @param comp	the row comparator to use
   * @param unique	whether to drop any duplicate rows (based on row comparator)
   */
  public void sort(RowComparator comp, boolean unique);

  /**
   * Returns the number of columns.
   *
   * @return		the number of columns
   */
  public int getColumnCount();

  /**
   * Returns the number of rows currently stored.
   *
   * @return		the number of rows
   */
  public int getRowCount();

  /**
   * Checks whether the given column is numeric or not. Does not accept
   * missing values.
   *
   * @param columnIndex	the index of the column to check
   * @return		true if purely numeric
   * @see		#getContentTypes(int)
   */
  public boolean isNumeric(int columnIndex);

  /**
   * Checks whether the given column is numeric or not. Can accept missing
   * values.
   *
   * @param columnIndex	the index of the column to check
   * @return		true if purely numeric
   * @see		#getContentTypes(int)
   */
  public boolean isNumeric(int columnIndex, boolean allowMissing);

  /**
   * Checks whether the given column is of the specific content type or not.
   *
   * @param columnIndex	the index of the column to check
   * @param type	the content type to check
   * @return		true if column purely consists of this content type
   * @see		#getContentType(int)
   */
  public boolean isContentType(int columnIndex, ContentType type);

  /**
   * Returns the pure content type of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content type that this column consists of solely, null if mixed
   */
  public ContentType getContentType(int columnIndex);

  /**
   * Returns the all content types of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content types that this column consists of
   */
  public Collection<ContentType> getContentTypes(int columnIndex);

  /**
   * Returns the unique string values of the specified column. The returned
   * list is sorted.
   * 
   * @param colKey	the column to retrieve the values for
   * @return		the sorted, list of unique values
   */
  public List<String> getCellValues(String colKey);

  /**
   * Returns the unique string values of the specified column. The returned
   * list is sorted.
   * 
   * @param colIndex	the column to retrieve the values for
   * @return		the sorted, list of unique values
   */
  public List<String> getCellValues(int colIndex);

  /**
   * Compares the header of this spreadsheet with the other one.
   *
   * @param other	the other spreadsheet to compare with
   * @return		null if equal, otherwise details what differs
   */
  public String equalsHeader(SpreadSheet other);

  /**
   * Returns the spreadsheet as string, i.e., CSV formatted.
   *
   * @return		the string representation
   */
  @Override
  public String toString();

  /**
   * Returns the spreadsheet as matrix, with the header as the first row.
   * Missing values are represented as null values.
   *
   * @return		the row-wise matrix
   */
  public Object[][] toMatrix();

  /**
   * Removes all cells marked "missing".
   *
   * @return		true if any cell was removed
   */
  public boolean removeMissing();

  /**
   * Returns the table for shared strings.
   *
   * @return		the table
   */
  public SharedStringsTable getSharedStringsTable();

  /**
   * Sets whether parsing of dates is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateLenient(boolean value);

  /**
   * Returns whether the parsing of dates is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isDateLenient();

  /**
   * Sets whether parsing of date/times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateTimeLenient(boolean value);

  /**
   * Returns whether the parsing of date/times is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isDateTimeLenient();

  /**
   * Sets whether parsing of date/time mses is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateTimeMsecLenient(boolean value);

  /**
   * Returns whether the parsing of date/time msecs is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isDateTimeMsecLenient();

  /**
   * Sets whether parsing of times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  public void setTimeLenient(boolean value);

  /**
   * Returns whether the parsing of times is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  public boolean isTimeLenient();

  /**
   * Sets whether parsing of times/msec is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  public void setTimeMsecLenient(boolean value);

  /**
   * Returns whether the parsing of times/msec is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  public boolean isTimeMsecLenient();

  /**
   * Sets the timezone to use.
   *
   * @param value	the new timezone
   * @see		SimpleDateFormat#setTimeZone(TimeZone)
   */
  public void setTimeZone(TimeZone value);

  /**
   * Returns the currently used timezone.
   *
   * @return		the current timezone
   * @see		SimpleDateFormat#getTimeZone()
   */
  public TimeZone getTimeZone();

  /**
   * Sets the locale. Used in formatting/parsing numbers.
   * 
   * @param value	the locale to use
   */
  public void setLocale(Locale value);

  /**
   * Returns the current locale.
   * 
   * @return		the locale
   */
  public Locale getLocale();

  /**
   * Triggers all formula cells to recalculate their values.
   */
  public void calculate();

  /**
   * Puts the content of the provided spreadsheet on the right.
   * 
   * @param other		the spreadsheet to merge with
   */
  public void mergeWith(SpreadSheet other);
}
