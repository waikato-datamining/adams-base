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
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import adams.core.ClassLocator;
import adams.core.CloneHandler;
import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Mergeable;
import adams.core.Utils;
import adams.core.management.LocaleHelper;
import adams.core.management.LocaleSupporter;
import adams.data.SharedStringsTable;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.Cell.ContentType;
import adams.env.Environment;
import adams.event.SpreadSheetColumnInsertionEvent;

import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents a generic spreadsheet object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheet
  implements Serializable, CloneHandler<SpreadSheet>, LocaleSupporter,
             Mergeable<SpreadSheet> {

  /** for serialization. */
  private static final long serialVersionUID = -5500678849412481001L;

  /** the line comment start. */
  public final static String COMMENT = "#";

  /** the default missing value. */
  public final static String MISSING_VALUE = "?";

  /** the row keys of the spreadsheet. */
  protected ArrayList<String> m_RowKeys;

  /** the rows of the spreadsheet. */
  protected HashMap<String, DataRow> m_Rows;

  /** the header row. */
  protected HeaderRow m_HeaderRow;

  /** optional comments. */
  protected List<String> m_Comments;

  /** the name of the spreadsheet. */
  protected String m_Name;

  /** for formatting dates. */
  protected DateFormat m_DateFormat;

  /** for formatting date/times. */
  protected DateFormat m_DateTimeFormat;

  /** for formatting date/time msecs. */
  protected DateFormat m_DateTimeMsecFormat;

  /** for formatting times. */
  protected DateFormat m_TimeFormat;

  /** for formatting times with msec. */
  protected DateFormat m_TimeMsecFormat;

  /** for number format. */
  protected NumberFormat m_NumberFormat;
  
  /** the current locale. */
  protected Locale m_Locale;

  /** for conserving memory. */
  protected SharedStringsTable m_StringsTable;

  /** the default data row class to use. */
  protected Class m_DataRowClass;

  /**
   * default constructor.
   */
  public SpreadSheet() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_RowKeys            = new ArrayList<String>();
    m_Rows               = new HashMap<String, DataRow>();
    m_HeaderRow          = new HeaderRow(this);
    m_Comments           = new ArrayList<String>();
    m_Name               = null;
    m_DateFormat         = DateUtils.getDateFormatter();
    m_DateTimeFormat     = DateUtils.getTimestampFormatter();
    m_DateTimeMsecFormat = DateUtils.getTimestampFormatterMsecs();
    m_TimeFormat         = DateUtils.getTimeFormatter();
    m_TimeMsecFormat     = DateUtils.getTimeFormatterMsecs();
    m_StringsTable       = new SharedStringsTable();
    m_DataRowClass       = DenseDataRow.class;
    m_Locale             = LocaleHelper.getSingleton().getDefault();
    m_NumberFormat       = LocaleHelper.getSingleton().getNumberFormat(m_Locale);
  }
  
  /**
   * Clears this spreadsheet and copies all the data from the given one.
   * 
   * @param sheet	the data to copy
   */
  public void assign(SpreadSheet sheet) {
    clear();

    setName(getName());
    setDataRowClass(sheet.getDataRowClass());
    setLocale(sheet.getLocale());
    setTimeZone(sheet.getTimeZone());
    m_DateFormat.applyPattern(sheet.getDateFormat().toPattern());
    setDateLenient(sheet.isDateLenient());
    m_DateTimeFormat.applyPattern(sheet.getDateTimeFormat().toPattern());
    setDateTimeLenient(sheet.isDateTimeLenient());
    m_DateTimeMsecFormat.applyPattern(sheet.getDateTimeMsecFormat().toPattern());
    setDateTimeMsecLenient(sheet.isDateTimeMsecLenient());
    m_TimeFormat.applyPattern(sheet.getTimeFormat().toPattern());
    setTimeLenient(sheet.isTimeLenient());
    m_TimeMsecFormat.applyPattern(sheet.getTimeMsecFormat().toPattern());
    setTimeMsecLenient(sheet.isTimeMsecLenient());

    addComment(sheet.getComments());
    getHeaderRow().assign(sheet.getHeaderRow());
    for (String key: sheet.rowKeys())
      addRow(key).assign(sheet.getRow(key));
  }
  
  /**
   * Sets the default data row class to use.
   * Must implement {@link DataRow}.
   *
   * @param cls				the class, null resets it to the default one
   * @throws IllegalArgumentException	if class does not implement {@link DataRow}
   */
  public void setDataRowClass(Class cls) {
    if (cls == null)
      cls = DenseDataRow.class;
    if (!ClassLocator.hasInterface(DataRow.class, cls))
      throw new IllegalArgumentException(
	  "Data row class " + cls.getName() + " does not implement " + DataRow.class.getName() + "!");
    m_DataRowClass = cls;
  }

  /**
   * Returns the class used for rows.
   *
   * @return		the class
   */
  public Class getDataRowClass() {
    return m_DataRowClass;
  }

  /**
   * Returns a new instance.
   * 
   * @return		the new instance, null if failed to create new instance
   */
  public SpreadSheet newInstance() {
    SpreadSheet	result;
    
    try {
      result = (SpreadSheet) getClass().newInstance();
      result.setDataRowClass(getDataRowClass());
    }
    catch (Exception e) {
      System.err.println("Failed to create new instance of " + getClass().getName());
      e.printStackTrace();
      result = null;
    }
    
    return result;
  }
  
  /**
   * Returns a clone of itself.
   *
   * @return		the clone
   */
  @Override
  public SpreadSheet getClone() {
    SpreadSheet	result;
    int		i;
    DataRow	row;

    result                = newInstance();
    result.m_Name         = m_Name;
    result.m_StringsTable.assign(m_StringsTable);
    result.m_DataRowClass = m_DataRowClass;
    result.m_HeaderRow    = m_HeaderRow.getClone(result);
    result.m_Comments.addAll(m_Comments);
    result.m_Locale       = m_Locale;
    result.m_NumberFormat = m_NumberFormat;
    result.m_DateFormat.applyPattern(m_DateFormat.toPattern());
    result.m_DateTimeFormat.applyPattern(m_DateTimeFormat.toPattern());
    result.m_TimeFormat.applyPattern(m_TimeFormat.toPattern());
    result.m_TimeMsecFormat.applyPattern(m_TimeMsecFormat.toPattern());
    result.m_RowKeys.addAll(m_RowKeys);
    for (i = 0; i < m_RowKeys.size(); i++) {
      row = m_Rows.get(m_RowKeys.get(i)).getClone(result);
      result.m_Rows.put(m_RowKeys.get(i), row);
    }

    return result;
  }

  /**
   * Returns the a spreadsheet with the same header and comments.
   *
   * @return		the spreadsheet
   */
  public SpreadSheet getHeader() {
    SpreadSheet	result;

    result                = newInstance();
    result.m_Name         = m_Name;
    result.m_HeaderRow    = m_HeaderRow.getClone(result);
    result.m_HeaderRow.setOwner(result);
    result.m_DataRowClass = m_DataRowClass;
    result.m_Comments.addAll(m_Comments);
    result.m_Locale       = m_Locale;
    result.m_NumberFormat = m_NumberFormat;
    result.m_DateFormat.applyPattern(m_DateFormat.toPattern());
    result.m_DateTimeFormat.applyPattern(m_DateTimeFormat.toPattern());
    result.m_TimeFormat.applyPattern(m_TimeFormat.toPattern());
    result.m_TimeMsecFormat.applyPattern(m_TimeMsecFormat.toPattern());

    return result;
  }

  /**
   * Returns the date formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getDateFormatter()
   */
  public DateFormat getDateFormat() {
    return m_DateFormat;
  }

  /**
   * Returns the date/time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatter()
   */
  public DateFormat getDateTimeFormat() {
    return m_DateTimeFormat;
  }

  /**
   * Returns the date/time msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatterMsecs()
   */
  public DateFormat getDateTimeMsecFormat() {
    return m_DateTimeMsecFormat;
  }

  /**
   * Returns the time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatter()
   */
  public DateFormat getTimeFormat() {
    return m_TimeFormat;
  }

  /**
   * Returns the time/msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatterMsecs()
   */
  public DateFormat getTimeMsecFormat() {
    return m_TimeMsecFormat;
  }

  /**
   * Returns the number formatter.
   * 
   * @return		the formatter
   */
  public NumberFormat getNumberFormat() {
    return m_NumberFormat;
  }

  /**
   * Sets the name of the spreadsheet.
   *
   * @param value	the name
   */
  public void setName(String value) {
    m_Name = value;
  }

  /**
   * Returns the name of the spreadsheet.
   *
   * @return		the name, can be null
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns whether the spreadsheet has a name.
   *
   * @return		true if the spreadsheet is named
   */
  public boolean hasName() {
    return (m_Name != null);
  }

  /**
   * Adds the comment to the internal list of comments.
   * If the comment contains newlines, then it gets automatically split
   * into multiple lines and added one by one.
   *
   * @param comment	the comment to add
   */
  public void addComment(String comment) {
    if (comment.indexOf('\n') > 0)
      addComment(Arrays.asList(comment.split("\n")));
    else
      m_Comments.add(comment);
  }

  /**
   * Adds the comments to the internal list of comments.
   *
   * @param comment	the comment to add
   */
  public void addComment(List<String> comment) {
    m_Comments.addAll(comment);
  }

  /**
   * Returns the comments.
   *
   * @return		the comments
   */
  public List<String> getComments() {
    return m_Comments;
  }

  /**
   * Removes all cells, but leaves comments.
   */
  public void clear() {
    String[]	header;
    int		i;
    
    // backup header strings
    header = new String[m_HeaderRow.getCellCount()];
    for (i = 0; i < header.length; i++) {
      if (m_HeaderRow.hasCell(i) && (m_HeaderRow.getCell(i).getContentType() == ContentType.STRING)) {
	header[i] = m_HeaderRow.getCell(i).getContent();
	m_HeaderRow.getCell(i).setContent(0L);
      }
    }
    
    m_RowKeys.clear();
    m_Rows.clear();
    m_StringsTable.clear();
    
    // restore header strings
    for (i = 0; i < header.length; i++) {
      if (header[i] != null)
	m_HeaderRow.getCell(i).setContentAsString(header[i]);
    }
  }

  /**
   * Returns the header row.
   *
   * @return		the row
   */
  public HeaderRow getHeaderRow() {
    return m_HeaderRow;
  }

  /**
   * Returns the name of the specified column.
   *
   * @param colIndex	the index of the column
   * @return		the name of the column
   */
  public String getColumnName(int colIndex) {
    return getHeaderRow().getCell(colIndex).getContent();
  }

  /**
   * Returns a list of the names of all columns (i.e., the content the header
   * row cells).
   *
   * @return		the names of the columns
   */
  public List<String> getColumnNames() {
    ArrayList<String>	result;
    int			i;

    result = new ArrayList<String>();

    for (i = 0; i < getColumnCount(); i++)
      result.add(getHeaderRow().getCell(i).getContent());

    return result;
  }

  /**
   * Returns whether the spreadsheet already contains the row with the given index.
   *
   * @param rowIndex	the index to look for
   * @return		true if the row already exists
   */
  public boolean hasRow(int rowIndex) {
    return (rowIndex >= 0) && (rowIndex < m_Rows.size());
  }

  /**
   * Returns whether the spreadsheet already contains the row with the given key.
   *
   * @param rowKey	the key to look for
   * @return		true if the row already exists
   */
  public boolean hasRow(String rowKey) {
    return m_Rows.containsKey(rowKey);
  }

  /**
   * Creates a new row instance.
   *
   * @return		the new instance, null in case of an instantiation error
   */
  protected synchronized DataRow newRow() {
    DataRow	result;
    Constructor	constr;

    try {
      constr = m_DataRowClass.getConstructor(new Class[]{SpreadSheet.class});
      result = (DataRow) constr.newInstance(new Object[]{this});
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate data row class: " + m_DataRowClass.getName());
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Creates a new cell.
   *
   * @return		the new instance, null in case of an instantiation error
   */
  public Cell newCell() {
    Cell	result;
    DataRow	row;

    result = null;
    row    = newRow();
    if (row != null)
      result = row.newCell(null);

    return result;
  }

  /**
   * Appends a row to the spreadsheet.
   *
   * @return		the created row
   */
  public DataRow addRow() {
    int		i;

    i = getRowCount();
    while (hasRow("" + i))
      i++;

    return addRow("" + i);
  }

  /**
   * Adds a row with the given key to the list and returns the created object.
   * If the row already exists, then this row is returned instead and no new
   * object created.
   *
   * @param rowKey	the key for the row to create
   * @return		the created row or the already existing row
   */
  public DataRow addRow(String rowKey) {
    DataRow	result;

    if (hasRow(rowKey)) {
      result = getRow(rowKey);
    }
    else {
      m_RowKeys.add(rowKey);
      m_Rows.put(rowKey, newRow());
      result = m_Rows.get(rowKey);
    }

    return result;
  }

  /**
   * Inserts a row at the specified location.
   *
   * @param index	the index where to insert the row
   * @return		the created row
   */
  public DataRow insertRow(int index) {
    DataRow	result;
    int		i;
    String	rowKey;

    // determine cell key for row
    i = 0;
    do {
      i++;
      rowKey = "inserted-" + i;
    }
    while (hasRow(rowKey));

    result = newRow();
    m_RowKeys.add(index, rowKey);
    m_Rows.put(rowKey, result);

    return result;
  }

  /**
   * Removes the specified row.
   *
   * @param rowIndex	the row to remove
   * @return		the row that was removed, null if none removed
   */
  public Row removeRow(int rowIndex) {
    return removeRow(getRowKey(rowIndex));
  }

  /**
   * Removes the specified row.
   *
   * @param rowKey	the row to remove
   * @return		the row that was removed, null if none removed
   */
  public Row removeRow(String rowKey) {
    Row		result;

    if (rowKey == null)
      return null;
    if (!hasRow(rowKey))
      return null;

    m_RowKeys.remove(rowKey);
    result = m_Rows.remove(rowKey);

    return result;
  }

  /**
   * Inserts a column at the specified location.
   *
   * @param columnIndex	the position of the column
   * @param header	the name of the column
   */
  public void insertColumn(int columnIndex, String header) {
    insertColumn(columnIndex, header, null);
  }

  /**
   * Inserts a column at the specified location.
   *
   * @param columnIndex	the position of the column
   * @param header	the name of the column
   * @param initial	the initial value for the cells, "null" for missing
   * 			values (in that case no cells are added)
   */
  public void insertColumn(int columnIndex, String header, String initial) {
    insertColumn(columnIndex, header, initial, false);
  }

  /**
   * Inserts a column at the specified location.
   *
   * @param columnIndex	the position of the column
   * @param header	the name of the column
   * @param initial	the initial value for the cells, "null" for missing
   * 			values (in that case no cells are added)
   * @param forceString	whether to enforce the value to be set as string
   */
  public void insertColumn(int columnIndex, String header, String initial, boolean forceString) {
    String				key;
    SpreadSheetColumnInsertionEvent	e;

    getHeaderRow().insertCell(columnIndex).setContent(header);

    e   = new SpreadSheetColumnInsertionEvent(this, columnIndex);
    key = getHeaderRow().getCellKey(columnIndex);

    for (DataRow row: rows()) {
      row.spreadSheetColumnInserted(e);
      if (initial != null) {
	if (forceString)
	  row.addCell(key).setContentAsString(initial);
	else
	  row.addCell(key).setContent(initial);
      }
    }
  }

  /**
   * Removes the specified column.
   *
   * @param columnIndex	the column to remove
   * @return		true if removed
   */
  public boolean removeColumn(int columnIndex) {
    return removeColumn(getHeaderRow().getCellKey(columnIndex));
  }

  /**
   * Removes the specified column.
   *
   * @param columnKey	the column to remove
   * @return		true if removed
   */
  public boolean removeColumn(String columnKey) {
    int		i;

    if (!getHeaderRow().hasCell(columnKey))
      return false;

    // data
    for (i = 0; i < getRowCount(); i++)
      getRow(i).removeCell(columnKey);

    // header
    getHeaderRow().removeCell(columnKey);

    return true;
  }

  /**
   * Returns the row associated with the given row key, null if not found.
   *
   * @param rowKey	the key of the row to retrieve
   * @return		the row or null if not found
   */
  public DataRow getRow(String rowKey) {
    return m_Rows.get(rowKey);
  }

  /**
   * Returns the row at the specified index.
   *
   * @param rowIndex	the 0-based index of the row to retrieve
   * @return		the row
   */
  public DataRow getRow(int rowIndex) {
    return m_Rows.get(m_RowKeys.get(rowIndex));
  }

  /**
   * Returns the row key at the specified index.
   *
   * @param rowIndex	the 0-based index of the row key to retrieve
   * @return		the row key
   */
  public String getRowKey(int rowIndex) {
    return m_RowKeys.get(rowIndex);
  }

  /**
   * Returns the row index of the specified row.
   *
   * @param rowKey	the row identifier
   * @return		the 0-based row index, -1 if not found
   */
  public int getRowIndex(String rowKey) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < m_RowKeys.size(); i++) {
      if (m_RowKeys.get(i).equals(rowKey)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the cell index of the specified cell (in the header row).
   *
   * @param cellKey	the cell identifier
   * @return		the 0-based column index, -1 if not found
   */
  public int getCellIndex(String cellKey) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < m_HeaderRow.getCellCount(); i++) {
      if (m_HeaderRow.getCellKey(i).equals(cellKey)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Checks whether the cell with the given keys already exists.
   *
   * @param rowKey	the key of the row to look for
   * @param cellKey	the key of the cell in the row to look for
   * @return		true if the cell exists
   */
  public boolean hasCell(String rowKey, String cellKey) {
    boolean	result;
    Row		row;

    result = hasRow(rowKey);

    if (result) {
      row    = getRow(rowKey);
      result = row.hasCell(cellKey);
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
  public boolean hasCell(int rowIndex, int columnIndex) {
    boolean	result;
    Row		row;

    result = hasRow(rowIndex);

    if (result) {
      row    = getRow(rowIndex);
      result = row.hasCell(columnIndex);
    }

    return result;
  }

  /**
   * Returns the corresponding cell or null if not found.
   *
   * @param rowKey	the key of the row the cell is in
   * @param cellKey	the key of the cell to retrieve
   * @return		the cell or null if not found
   */
  public Cell getCell(String rowKey, String cellKey) {
    Cell	result;
    Row		row;

    result = null;

    row    = getRow(rowKey);
    if (row != null)
      result = row.getCell(cellKey);

    return result;
  }

  /**
   * Returns the corresponding cell or null if not found.
   *
   * @param rowIndex	the index of the row the cell is in
   * @param columnIndex	the column of the cell to retrieve
   * @return		the cell or null if not found
   */
  public Cell getCell(int rowIndex, int columnIndex) {
    Cell	result;
    Row		row;

    result = null;

    row    = getRow(rowIndex);
    if (row != null)
      result = row.getCell(columnIndex);

    return result;
  }

  /**
   * Returns the position letter(s) of the column.
   *
   * @param col		the column index of the cell (0-based)
   * @return		the position string
   */
  public static String getColumnPosition(int col) {
    String		result;
    List<Integer>	digits;
    int			i;

    result = null;
    
    // A-Z, AA-ZZ, AAA-ZZZ, AAAA-ZZZZ, AAAAA-ZZZZZ, AAAAAA-ZZZZZZ
    if (col >= 26 + 676 + 17576 + 456976 + 11881376 + 308915776)
      throw new IllegalArgumentException("Column of cell too large: " + col + " >= " + (26 + 676 + 17576 + 456976 + 11881376 + 308915776));

    result = "";

    // A-Z
    if (col < 26) {
      digits = Utils.toBase(col, 26);
    }
    // AA-ZZ
    else if (col < 26 + 676) {
      digits = Utils.toBase(col - 26, 26);
      while (digits.size() < 2)
	digits.add(0);
    }
    // AAA-ZZZ
    else if (col < 26 + 676 + 17576) {
      digits = Utils.toBase(col - 26 - 676, 26);
      while (digits.size() < 3)
	digits.add(0);
    }
    // AAAA-ZZZZ
    else if (col < 26 + 676 + 17576 + 456976) {
      digits = Utils.toBase(col - 26 - 676 - 17576, 26);
      while (digits.size() < 4)
	digits.add(0);
    }
    // AAAAA-ZZZZZ
    else if (col < 26 + 676 + 17576 + 456976 + 11881376) {
      digits = Utils.toBase(col - 26 - 676 - 17576 - 456976, 26);
      while (digits.size() < 5)
	digits.add(0);
    }
    // AAAAAA-ZZZZZZ
    else /*if (col < 26 + 676 + 17576 + 456976 + 11881376 + 308915776)*/ {
      digits = Utils.toBase(col - 26 - 676 - 17576 - 456976 - 11881376, 26);
      while (digits.size() < 6)
	digits.add(0);
    }

    for (i = digits.size() - 1; i >= 0; i--)
      result += (char) ('A' + digits.get(i));

    return result;
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
  public String getCellPosition(String rowKey, String cellKey) {
    int		rowIndex;
    int		cellIndex;

    rowIndex  = getRowIndex(rowKey);
    cellIndex = getCellIndex(cellKey);

    return getCellPosition(rowIndex + 1, cellIndex);
  }

  /**
   * Returns the position of the cell. A position is a combination of a number
   * of letters (for the column) and number (for the row).
   * <br><br>
   * Note: add "1" to the row indices, since the header row does not count
   * towards the row count.
   *
   * @param row		the row index of the cell (0-based)
   * @param col		the column index of the cell (0-based)
   * @return		the position string or null if not found
   */
  public static String getCellPosition(int row, int col) {
    String	result;

    result = getColumnPosition(col);

    if ((row == -1) || (col == -1))
      return result;

    result += (row + 2);

    return result;
  }

  /**
   * Returns row/column index based on the provided position string (e.g., A12).
   *
   * @param position	the position string to parse
   * @return		the array with row and column index (0-based indices)
   * @throws Exception	in case of an invalid position string
   */
  public static int[] getCellLocation(String position) throws Exception {
    int[]	result;
    String	row;
    String	col;
    int		i;
    boolean	isCol;
    char	chr;
    int		factor;

    result = new int[2];

    isCol = true;
    row   = "";
    col   = "";
    for (i = 0; i < position.length(); i++) {
      chr = position.charAt(i);
      if ((chr >= '0') && (chr <= '9')) {
	isCol = false;
	row += chr;
      }
      else if ((chr >= 'A') && (chr <= 'Z') && isCol) {
	col += chr;
      }
      else {
	throw new Exception("Invalid character in cell position string: " + chr);
      }
    }

    // row
    result[0] = Integer.parseInt(row) - 2;

    // col
    factor = 1;
    for (i = col.length() - 1; i >= 0; i--) {
      result[1] += (col.charAt(i) - 'A' + 1) * factor;
      factor *= 26;
    }
    result[1]--;

    return result;
  }

  /**
   * Returns a collection of all row keys.
   *
   * @return		the row keys
   */
  public Collection<String> rowKeys() {
    return Collections.unmodifiableCollection(m_RowKeys);
  }

  /**
   * Returns all rows.
   *
   * @return		the rows
   */
  public Collection<DataRow> rows() {
    ArrayList<DataRow>	result;

    result = new ArrayList<DataRow>();
    for (String key: m_RowKeys)
      result.add(m_Rows.get(key));

    return result;
  }

  /**
   * Sorts the rows according to the row keys.
   *
   * @see	#rowKeys()
   */
  public void sort() {
    Collections.sort(m_RowKeys);
  }

  /**
   * Sorts the rows according to the row keys.
   *
   * @param comp	the comparator to use
   * @see		#rowKeys()
   */
  public void sort(Comparator<String> comp) {
    Collections.sort(m_RowKeys, comp);
  }

  /**
   * Sorts the rows based on the values in the specified column.
   * <br><br>
   * NB: the row keys will change!
   *
   * @param index	the index (0-based) of the column to sort on
   * @param asc		wether sorting is ascending or descending
   * @see 		#sort(RowComparator)
   */
  public void sort(int index, boolean asc) {
    sort(new RowComparator(new int[]{index}, new boolean[]{asc}));
  }

  /**
   * Sorts the rows using the given comparator.
   * <br><br>
   * NB: the row keys will change!
   *
   * @param comp	the row comparator to use
   */
  public void sort(RowComparator comp) {
    sort(comp, false);
  }

  /**
   * Sorts the rows using the given comparator.
   * <br><br>
   * NB: the row keys will change!
   *
   * @param comp	the row comparator to use
   * @param unique	whether to drop any duplicate rows (based on row comparator)
   */
  public void sort(RowComparator comp, boolean unique) {
    ArrayList<DataRow>	list;
    String		rkey;
    int			i;
    DataRow		last;
    DataRow		current;

    list = new ArrayList<DataRow>();
    for (String key: m_RowKeys)
      list.add(m_Rows.get(key));
    Collections.sort(list, comp);
    m_Rows.clear();
    m_RowKeys.clear();
    if (unique) {
      last = null;
      for (i = 0; i < list.size(); i++) {
	current = list.get(i);
	if ((last == null) || (comp.compare(last, current) != 0)) {
	  rkey = "" + m_Rows.size();
	  m_Rows.put(rkey, current);
	  m_RowKeys.add(rkey);
	  last = current;
	}
      }
    }
    else {
      for (DataRow row: list) {
	rkey = "" + m_Rows.size();
	m_Rows.put(rkey, row);
	m_RowKeys.add(rkey);
      }
    }
  }

  /**
   * Returns the number of columns.
   *
   * @return		the number of columns
   */
  public int getColumnCount() {
    return getHeaderRow().getCellCount();
  }

  /**
   * Returns the number of rows currently stored.
   *
   * @return		the number of rows
   */
  public int getRowCount() {
    return m_RowKeys.size();
  }

  /**
   * Checks whether the given column is numeric or not. Does not accept
   * missing values.
   *
   * @param columnIndex	the index of the column to check
   * @return		true if purely numeric
   * @see		#getContentTypes(int)
   */
  public boolean isNumeric(int columnIndex) {
    return isNumeric(columnIndex, false);
  }

  /**
   * Checks whether the given column is numeric or not. Can accept missing
   * values.
   *
   * @param columnIndex	the index of the column to check
   * @return		true if purely numeric
   * @see		#getContentTypes(int)
   */
  public boolean isNumeric(int columnIndex, boolean allowMissing) {
    boolean			result;
    Collection<ContentType>	found;

    result = false;
    found  = getContentTypes(columnIndex);
    if (found.size() > 0) {
      found.remove(ContentType.DOUBLE);
      found.remove(ContentType.LONG);
      if (allowMissing)
	found.remove(ContentType.MISSING);
      result = (found.size() == 0);
    }

    return result;
  }

  /**
   * Checks whether the given column is of the specific content type or not.
   *
   * @param columnIndex	the index of the column to check
   * @param type	the content type to check
   * @return		true if column purely consists of this content type
   * @see		#getContentType(int)
   */
  public boolean isContentType(int columnIndex, ContentType type) {
    boolean		result;
    ContentType		found;

    found  = getContentType(columnIndex);
    result = (found == type);

    return result;
  }

  /**
   * Returns the pure content type of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content type that this column consists of solely, null if mixed
   */
  public ContentType getContentType(int columnIndex) {
    ContentType			result;
    Collection<ContentType>	types;

    result = null;

    types = getContentTypes(columnIndex);
    if (types.size() == 1)
      result = (ContentType) types.toArray()[0];

    return result;
  }

  /**
   * Returns the all content types of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content types that this column consists of
   */
  public Collection<ContentType> getContentTypes(int columnIndex) {
    HashSet<ContentType>	results;
    int				i;
    String			colKey;
    Cell			cell;

    results = new HashSet<ContentType>();
    colKey  = m_HeaderRow.getCellKey(columnIndex);

    for (i = 0; i < getRowCount(); i++) {
      cell = getRow(i).getCell(colKey);
      if ((cell != null) && !cell.isMissing())
	results.add(cell.getContentType());
    }

    return results;
  }
  
  /**
   * Returns the unique string values of the specified column. The returned
   * list is sorted.
   * 
   * @param colKey	the column to retrieve the values for
   * @return		the sorted, list of unique values
   */
  public List<String> getCellValues(String colKey) {
    List<String>	result;
    HashSet<String>	unique;

    result = new ArrayList<String>();
    unique = new HashSet<String>();
    for (Row row: rows()) {
      if (row.hasCell(colKey) && !row.getCell(colKey).isMissing())
	unique.add(row.getCell(colKey).getContent());
    }
    result.addAll(unique);
    Collections.sort(result);
    
    return result;
  }
  
  /**
   * Returns the unique string values of the specified column. The returned
   * list is sorted.
   * 
   * @param colIndex	the column to retrieve the values for
   * @return		the sorted, list of unique values
   */
  public List<String> getCellValues(int colIndex) {
    return getCellValues(getHeaderRow().getCellKey(colIndex));
  }

  /**
   * Compares the header of this spreadsheet with the other one.
   *
   * @param other	the other spreadsheet to compare with
   * @return		null if equal, otherwise details what differs
   */
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
   * Returns the spreadsheet as string, i.e., CSV formatted.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringWriter	writer;

    writer = new StringWriter();
    new CsvSpreadSheetWriter().write(this, writer);

    return writer.toString();
  }

  /**
   * Returns the spreadsheet as matrix, with the header as the first row.
   * Missing values are represented as null values.
   *
   * @return		the row-wise matrix
   */
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
   * Removes all cells marked "missing".
   *
   * @return		true if any cell was removed
   */
  public boolean removeMissing() {
    boolean	result;

    result = false;

    for (Row row: m_Rows.values())
      result = result | row.removeMissing();

    return result;
  }

  /**
   * Returns the table for shared strings.
   *
   * @return		the table
   */
  public SharedStringsTable getSharedStringsTable() {
    return m_StringsTable;
  }

  /**
   * Sets whether parsing of dates is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateLenient(boolean value) {
    m_DateFormat.setLenient(value);
  }

  /**
   * Returns whether the parsing of dates is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isDateLenient() {
    return m_DateFormat.isLenient();
  }

  /**
   * Sets whether parsing of date/times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateTimeLenient(boolean value) {
    m_DateTimeFormat.setLenient(value);
  }

  /**
   * Returns whether the parsing of date/times is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isDateTimeLenient() {
    return m_DateTimeFormat.isLenient();
  }

  /**
   * Sets whether parsing of date/time mses is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  public void setDateTimeMsecLenient(boolean value) {
    m_DateTimeMsecFormat.setLenient(value);
  }

  /**
   * Returns whether the parsing of date/time msecs is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  public boolean isDateTimeMsecLenient() {
    return m_DateTimeMsecFormat.isLenient();
  }

  /**
   * Sets whether parsing of times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  public void setTimeLenient(boolean value) {
    m_TimeFormat.setLenient(value);
  }

  /**
   * Returns whether the parsing of times is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  public boolean isTimeLenient() {
    return m_TimeFormat.isLenient();
  }

  /**
   * Sets whether parsing of times/msec is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  public void setTimeMsecLenient(boolean value) {
    m_TimeMsecFormat.setLenient(value);
  }

  /**
   * Returns whether the parsing of times/msec is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  public boolean isTimeMsecLenient() {
    return m_TimeMsecFormat.isLenient();
  }

  /**
   * Sets the timezone to use.
   *
   * @param value	the new timezone
   * @see		SimpleDateFormat#setTimeZone(TimeZone)
   */
  public void setTimeZone(TimeZone value) {
    m_DateFormat.setTimeZone(value);
    m_DateTimeFormat.setTimeZone(value);
    m_DateTimeMsecFormat.setTimeZone(value);
    // the following must always be GMT!
    m_TimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    m_TimeMsecFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  /**
   * Returns the currently used timezone.
   *
   * @return		the current timezone
   * @see		SimpleDateFormat#getTimeZone()
   */
  public TimeZone getTimeZone() {
    return m_DateFormat.getTimeZone();
  }
  
  /**
   * Sets the locale. Used in formatting/parsing numbers.
   * 
   * @param value	the locale to use
   */
  public void setLocale(Locale value) {
    m_Locale       = value;
    m_NumberFormat = LocaleHelper.getSingleton().getNumberFormat(m_Locale);
  }
  
  /**
   * Returns the current locale.
   * 
   * @return		the locale
   */
  public Locale getLocale() {
    return m_Locale;
  }
  
  /**
   * Triggers all formula cells to recalculate their values.
   */
  public void calculate() {
    for (Row row: rows()) {
      for (Cell cell: row.cells()) {
	if (cell.isFormula())
	  cell.calculate();
      }
    }
  }

  /**
   * Puts the content of the provided spreadsheet on the right.
   * 
   * @param other		the spreadsheet to merge with
   */
  public void mergeWith(SpreadSheet other) {
    int		n;
    Row		rowOther;
    Row		rowThis;
    
    getHeaderRow().mergeWith(other.getHeaderRow());
    
    // do we need to append rows?
    while (getRowCount() < other.getRowCount())
      addRow();
    
    // add data
    for (n = 0; n < other.getRowCount(); n++) {
      rowThis  = getRow(n);
      rowOther = other.getRow(n);
      rowThis.mergeWith(rowOther);
    }
  }
  
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    SpreadSheet sheet = new SpreadSheet();
    sheet.getHeaderRow().addCell("Col").setContent("Col");
    Row row;
    row = sheet.addRow();
    row.addCell(0).setContent("C");
    row = sheet.addRow();
    row.addCell(0).setContent("C");
    row = sheet.addRow();
    row.addCell(0).setContent("C");
    row = sheet.addRow();
    row.addCell(0).setContent("A");
    row = sheet.addRow();
    row.addCell(0).setContent("A");
    row = sheet.addRow();
    row.addCell(0).setContent("B");
    row = sheet.addRow();
    row.addCell(0).setContent("B");
    System.out.println(sheet);
    sheet.sort(new RowComparator(new int[]{0}), true);
    System.out.println(sheet);
    sheet.clear();
    System.out.println(sheet);
  }
}
