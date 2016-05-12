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
 * InstancesView.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.data;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Utils;
import adams.core.management.LocaleHelper;
import adams.data.SharedStringsTable;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.HeaderRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.spreadsheet.SpreadSheetView;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides a view of an {@link Instances} object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancesView
  implements Dataset {

  private static final long serialVersionUID = -6570030309091506840L;

  /** the underlying data. */
  protected Instances m_Data;

  /** the header row. */
  protected InstancesHeaderRow m_Header;

  /** the shared string table. */
  protected SharedStringsTable m_SharedStringsTable;

  /**
   * Initializes the view with a dummy dataset.
   */
  public InstancesView() {
    this(createDummy());
  }

  /**
   * Returns a dummy dataset.
   *
   * @return		the dataset
   */
  protected static Instances createDummy() {
    ArrayList<Attribute>	atts;

    atts = new ArrayList<>();
    atts.add(new Attribute("dummy"));

    return new Instances("dummy", atts, 0);
  }

  /**
   * Initializes the view.
   *
   * @param data	the data to use
   */
  public InstancesView(Instances data) {
    m_Data               = data;
    m_SharedStringsTable = new SharedStringsTable();
    m_Header             = new InstancesHeaderRow(this);
  }

  /**
   * Returns the underlying Instances.
   *
   * @return		the underlying data
   */
  public Instances getData() {
    return m_Data;
  }

  /**
   * Turns the rowKey into a row index.
   *
   * @param rowKey	the rowKey to convert
   * @return		the row index, -1 if failed to convert
   */
  protected int rowKeyToIndex(String rowKey) {
    if (Utils.isInteger(rowKey))
      return Integer.parseInt(rowKey);
    else
      return -1;
  }

  /**
   * Turns the cellKey into a column index.
   *
   * @param cellKey	the cellKey to convert
   * @return		the column index, -1 if failed to convert
   */
  protected int cellKeyToIndex(String cellKey) {
    if (Utils.isInteger(cellKey))
      return Integer.parseInt(cellKey);
    else
      return -1;
  }

  /**
   * Ignored.
   *
   * @param comment	the comment to add
   */
  @Override
  public void addComment(List<String> comment) {
  }

  /**
   * Returns the comments.
   *
   * @return		always empty
   */
  @Override
  public List<String> getComments() {
    return new ArrayList<>();
  }

  /**
   * Removes all cells, but leaves comments.
   */
  @Override
  public void clear() {
    m_Data.clear();
  }

  /**
   * Returns the header row.
   *
   * @return		the row
   */
  @Override
  public HeaderRow getHeaderRow() {
    return m_Header;
  }

  /**
   * Returns the name of the specified column.
   *
   * @param colIndex	the index of the column
   * @return		the name of the column
   */
  @Override
  public String getColumnName(int colIndex) {
    return m_Data.attribute(colIndex).name();
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

    result = new ArrayList<>();
    for (i = 0; i < m_Data.numAttributes(); i++)
      result.add(m_Data.attribute(i).name());

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
    return (rowIndex < m_Data.numInstances());
  }

  /**
   * Returns whether the spreadsheet already contains the row with the given key.
   *
   * @param rowKey	the key to look for
   * @return		true if the row already exists
   */
  @Override
  public boolean hasRow(String rowKey) {
    int		row;

    row = rowKeyToIndex(rowKey);
    return (row >= 0) || (row < m_Data.numInstances());
  }

  /**
   * Creates a new cell.
   *
   * @return		the new instance, null in case of an instantiation error
   */
  @Override
  public Cell newCell() {
    return null;
  }

  /**
   * Appends a row to the spreadsheet.
   *
   * @return		the created row
   */
  @Override
  public DataRow addRow() {
    DenseInstance	inst;

    inst = new DenseInstance(getColumnCount());
    inst.setDataset(m_Data);
    m_Data.add(inst);

    return new InstanceView(this, inst);
  }

  /**
   * Adds a row with the given key to the list and returns the created object.
   * If the row already exists, then this row is returned instead and no new
   * object created.
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
   *
   * @param index	the index where to insert the row
   * @return		the created row
   */
  @Override
  public DataRow insertRow(int index) {
    DenseInstance	inst;

    inst = new DenseInstance(getColumnCount());
    inst.setDataset(m_Data);
    m_Data.add(index, inst);

    return new InstanceView(this, inst);
  }

  /**
   * Removes the specified row.
   *
   * @param rowIndex	the row to remove
   * @return		the row that was removed, null if none removed
   */
  @Override
  public Row removeRow(int rowIndex) {
    if ((rowIndex >= 0) && (rowIndex < getRowCount()))
      return new InstanceView(this, m_Data.remove(rowIndex));
    else
      return null;
  }

  /**
   * Removes the specified row.
   *
   * @param rowKey	the row to remove
   * @return		the row that was removed, null if none removed
   */
  @Override
  public Row removeRow(String rowKey) {
    return removeRow(rowKeyToIndex(rowKey));
  }

  /**
   * Inserts a column at the specified location.
   * <br><br>
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
   * <br><br>
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
   * <br><br>
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
   * <br><br>
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
   * <br><br>
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
    return getRow(rowKeyToIndex(rowKey));
  }

  /**
   * Returns the row at the specified index.
   *
   * @param rowIndex	the 0-based index of the row to retrieve
   * @return		the row
   */
  @Override
  public DataRow getRow(int rowIndex) {
    if ((rowIndex >= 0) && (rowIndex < getRowCount()))
      return new InstanceView(this, m_Data.instance(rowIndex));
    else
      return null;
  }

  /**
   * Returns the row key at the specified index.
   *
   * @param rowIndex	the 0-based index of the row key to retrieve
   * @return		the row key
   */
  @Override
  public String getRowKey(int rowIndex) {
    return "" + rowIndex;
  }

  /**
   * Returns the row index of the specified row.
   *
   * @param rowKey	the row identifier
   * @return		the 0-based row index, -1 if not found
   */
  @Override
  public int getRowIndex(String rowKey) {
    return rowKeyToIndex(rowKey);
  }

  /**
   * Returns the cell index of the specified cell (in the header row).
   *
   * @param cellKey	the cell identifier
   * @return		the 0-based column index, -1 if not found
   */
  @Override
  public int getCellIndex(String cellKey) {
    return cellKeyToIndex(cellKey);
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
    return (rowIndex >= 0) && (rowIndex < getRowCount())
      && (columnIndex >= 0) && (columnIndex < getColumnCount());
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
    Row		row;

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
    return SpreadSheetUtils.getCellPosition(rowKeyToIndex(rowKey), cellKeyToIndex(cellKey));
  }

  /**
   * Returns a collection of all row keys.
   *
   * @return		the row keys
   */
  @Override
  public Collection<String> rowKeys() {
    List<String>	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < getRowCount(); i++)
      result.add("" + i);

    return result;
  }

  /**
   * Returns all rows.
   *
   * @return		the rows
   */
  @Override
  public Collection<DataRow> rows() {
    List<DataRow>	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < getRowCount(); i++)
      result.add(getRow(i));

    return result;
  }

  /**
   * Sorts the rows according to the row keys.
   * <br>
   * Does nothing.
   *
   * @see	#rowKeys()
   */
  @Override
  public void sortRowKeys() {
  }

  /**
   * Sorts the rows according to the row keys.
   * <br>
   * Does nothing.
   *
   * @param comp	the comparator to use
   * @see		#rowKeys()
   */
  @Override
  public void sortRowKeys(Comparator<String> comp) {
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
  @Override
  public void sort(int index, boolean asc) {
    m_Data.sort(index);
    if (!asc)
      Collections.reverse(m_Data);
  }

  /**
   * Sorts the rows using the given comparator.
   * <br><br>
   * Not implemented.
   *
   * @param comp	the row comparator to use
   */
  @Override
  public void sort(RowComparator comp) {

  }

  /**
   * Sorts the rows using the given comparator.
   * <br><br>
   * Not implemented.
   *
   * @param comp	the row comparator to use
   * @param unique	whether to drop any duplicate rows (based on row comparator)
   */
  @Override
  public void sort(RowComparator comp, boolean unique) {

  }

  /**
   * Returns the number of columns.
   *
   * @return		the number of columns
   */
  @Override
  public int getColumnCount() {
    return m_Data.numAttributes();
  }

  /**
   * Returns the number of rows currently stored.
   *
   * @return		the number of rows
   */
  @Override
  public int getRowCount() {
    return m_Data.numInstances();
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
  @Override
  public boolean isNumeric(int columnIndex, boolean allowMissing) {
    boolean	result;
    int		i;

    result = (m_Data.attribute(columnIndex).type() == Attribute.NUMERIC);
    if (result && !allowMissing) {
      for (i = 0; i < m_Data.numInstances(); i++) {
	if (m_Data.instance(i).isMissing(columnIndex)) {
	  result = false;
	  break;
	}
      }
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
  @Override
  public boolean isContentType(int columnIndex, ContentType type) {
    Attribute	att;

    att = m_Data.attribute(columnIndex);
    if ((type == ContentType.DOUBLE) && (att.type() == Attribute.NUMERIC))
      return true;
    else if ((type == ContentType.DATETIMEMSEC) && (att.type() == Attribute.DATE))
      return true;
    else if ((type == ContentType.STRING) && (att.type() == Attribute.NOMINAL))
      return true;
    else if ((type == ContentType.STRING) && (att.type() == Attribute.STRING))
      return true;

    return false;
  }

  /**
   * Returns the pure content type of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content type that this column consists of solely, null if mixed
   */
  @Override
  public ContentType getContentType(int columnIndex) {
    Collection<ContentType>	types;

    types = getContentTypes(columnIndex);
    if (types.size() == 1)
      return types.iterator().next();
    else
      return null;
  }

  /**
   * Returns the all content types of the given column, if available.
   *
   * @param columnIndex	the index of the column to check
   * @return		the content types that this column consists of
   */
  @Override
  public Collection<ContentType> getContentTypes(int columnIndex) {
    List<ContentType>	result;
    Attribute		att;
    int			i;

    result = new ArrayList<>();
    att    = m_Data.attribute(columnIndex);
    if (att.type() == Attribute.NUMERIC)
      result.add(ContentType.DOUBLE);
    else if (att.type() == Attribute.DATE)
      result.add(ContentType.DATETIMEMSEC);
    else if (att.type() == Attribute.NOMINAL)
      result.add(ContentType.STRING);
    else if (att.type() == Attribute.STRING)
      result.add(ContentType.STRING);

    // check for missing
    for (i = 0; i < m_Data.numInstances(); i++) {
      if (m_Data.instance(i).isMissing(columnIndex)) {
	result.add(ContentType.MISSING);
	break;
      }
    }

    return result;
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
    return getCellValues(cellKeyToIndex(colKey));
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
    List<String>	result;
    HashSet<String>	values;
    int			i;
    Cell		cell;

    result = new ArrayList<>();
    values = new HashSet<>();
    for (i = 0; i < getRowCount(); i++) {
      cell = getCell(i, colIndex);
      if ((cell != null) && !cell.isMissing())
	values.add(cell.getContent());
    }
    result.addAll(values);
    Collections.sort(result);

    return result;
  }

  /**
   * Compares the header of this spreadsheet with the other one.
   *
   * @param other	the other spreadsheet to compare with
   * @return		null if equal, otherwise details what differs
   */
  @Override
  public String equalsHeader(SpreadSheet other) {
    if (other instanceof InstancesView)
      return m_Data.equalHeadersMsg(((InstancesView) other).getData());
    else
      throw new IllegalArgumentException(
	"Can only compare with other " + InstancesView.class.getName() + " objects!");
  }

  /**
   * Returns the spreadsheet as matrix, with the header as the first row.
   * Missing values are represented as null values.
   *
   * @return		the row-wise matrix
   */
  @Override
  public Object[][] toMatrix() {
    Object[][]		result;
    int 		r;
    int 		c;
    Row			row;
    Cell		cell;

    result = new Object[getRowCount() + 1][getColumnCount()];

    // header
    for (c = 0; c < getColumnCount(); c++)
      result[0][c] = m_Data.attribute(c).name();

    // data
    for (r = 0; r < getRowCount(); r++) {
      row = getRow(r);
      for (c = 0; c < getColumnCount(); c++) {
	cell = row.getCell(c);
	if ((cell == null) || cell.isMissing())
	  result[r + 1][c] = null;
	else
	  result[r + 1][c] = cell.getNative();
      }
    }

    return result;
  }

  /**
   * Removes all cells marked "missing".
   *
   * @return		true if any cell was removed
   */
  @Override
  public boolean removeMissing() {
    return false;
  }

  /**
   * Returns the table for shared strings.
   *
   * @return		the table
   */
  @Override
  public SharedStringsTable getSharedStringsTable() {
    return m_SharedStringsTable;
  }

  /**
   * Sets whether parsing of dates is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  @Override
  public void setDateLenient(boolean value) {

  }

  /**
   * Returns whether the parsing of dates is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  @Override
  public boolean isDateLenient() {
    return false;
  }

  /**
   * Sets whether parsing of date/times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  @Override
  public void setDateTimeLenient(boolean value) {

  }

  /**
   * Returns whether the parsing of date/times is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  @Override
  public boolean isDateTimeLenient() {
    return false;
  }

  /**
   * Sets whether parsing of date/time mses is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   * @see		SimpleDateFormat#setLenient(boolean)
   */
  @Override
  public void setDateTimeMsecLenient(boolean value) {

  }

  /**
   * Returns whether the parsing of date/time msecs is lenient or not.
   *
   * @return		true if parsing is lenient
   * @see		SimpleDateFormat#isLenient()
   */
  @Override
  public boolean isDateTimeMsecLenient() {
    return false;
  }

  /**
   * Sets whether parsing of times is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  @Override
  public void setTimeLenient(boolean value) {

  }

  /**
   * Returns whether the parsing of times is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  @Override
  public boolean isTimeLenient() {
    return false;
  }

  /**
   * Sets whether parsing of times/msec is to be lenient or not.
   *
   * @param value	if true lenient parsing is used, otherwise not
   */
  @Override
  public void setTimeMsecLenient(boolean value) {

  }

  /**
   * Returns whether the parsing of times/msec is lenient or not.
   *
   * @return		true if parsing is lenient
   */
  @Override
  public boolean isTimeMsecLenient() {
    return false;
  }

  /**
   * Sets the timezone to use.
   *
   * @param value	the new timezone
   * @see		SimpleDateFormat#setTimeZone(TimeZone)
   */
  @Override
  public void setTimeZone(TimeZone value) {

  }

  /**
   * Returns the currently used timezone.
   *
   * @return		the current timezone
   * @see		SimpleDateFormat#getTimeZone()
   */
  @Override
  public TimeZone getTimeZone() {
    return null;
  }

  /**
   * Sets the locale. Used in formatting/parsing numbers.
   *
   * @param value	the locale to use
   */
  @Override
  public void setLocale(Locale value) {

  }

  /**
   * Returns the current locale.
   *
   * @return		the locale
   */
  @Override
  public Locale getLocale() {
    return null;
  }

  /**
   * Triggers all formula cells to recalculate their values.
   */
  @Override
  public void calculate() {

  }

  /**
   * Puts the content of the provided spreadsheet on the right.
   * <br><br>
   * Not implemented!
   *
   * @param other		the spreadsheet to merge with
   */
  @Override
  public void mergeWith(SpreadSheet other) {
    throw new NotImplementedException();
  }

  /**
   * Clears this spreadsheet and copies all the data from the given one.
   *
   * @param sheet	the data to copy
   */
  @Override
  public void assign(SpreadSheet sheet) {
    if (sheet instanceof InstancesView) {
      m_Data   = ((InstancesView) sheet).getData();
      m_Header = new InstancesHeaderRow(this);
    }
    else {
      throw new IllegalArgumentException("Other spreadsheet can only be " + InstancesView.class.getName());
    }
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

  }

  /**
   * Returns the class used for rows.
   *
   * @return		the class
   */
  @Override
  public Class getDataRowClass() {
    return InstanceView.class;
  }

  /**
   * Returns a new instance.
   *
   * @return		the new instance, null if failed to create new instance
   */
  @Override
  public SpreadSheet newInstance() {
    return null;
  }

  /**
   * Returns a clone of itself.
   *
   * @return		the clone
   */
  @Override
  public Dataset getClone() {
    return new InstancesView(m_Data);
  }

  /**
   * Returns the a spreadsheet with the same header and comments.
   *
   * @return		the spreadsheet
   */
  @Override
  public Dataset getHeader() {
    Instances 	data;

    data = new Instances(m_Data);
    return new InstancesView(data);
  }

  /**
   * Returns the date formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getDateFormatter()
   */
  @Override
  public DateFormat getDateFormat() {
    return DateUtils.getDateFormatter();
  }

  /**
   * Returns the date/time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatter()
   */
  @Override
  public DateFormat getDateTimeFormat() {
    return DateUtils.getTimestampFormatter();
  }

  /**
   * Returns the date/time msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimestampFormatterMsecs()
   */
  @Override
  public DateFormat getDateTimeMsecFormat() {
    return DateUtils.getTimestampFormatterMsecs();
  }

  /**
   * Returns the time formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatter()
   */
  @Override
  public DateFormat getTimeFormat() {
    return DateUtils.getTimeFormatter();
  }

  /**
   * Returns the time/msec formatter.
   *
   * @return		the formatter
   * @see		DateUtils#getTimeFormatterMsecs()
   */
  @Override
  public DateFormat getTimeMsecFormat() {
    return DateUtils.getTimeFormatterMsecs();
  }

  /**
   * Returns the number formatter.
   *
   * @return		the formatter
   */
  @Override
  public NumberFormat getNumberFormat() {
    return LocaleHelper.getSingleton().getNumberFormat(LocaleHelper.getSingleton().getDefault());
  }

  /**
   * Sets the name of the spreadsheet.
   *
   * @param value	the name
   */
  @Override
  public void setName(String value) {
    m_Data.setRelationName(value);
  }

  /**
   * Returns the name of the spreadsheet.
   *
   * @return		the name, can be null
   */
  @Override
  public String getName() {
    return m_Data.relationName();
  }

  /**
   * Returns whether the spreadsheet has a name.
   *
   * @return		true if the spreadsheet is named
   */
  @Override
  public boolean hasName() {
    return true;
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

  }

  /**
   * Returns the index of the column using the specified name.
   *
   * @param name	the name of the column to locate
   * @return		the index, -1 if failed to locate
   */
  @Override
  public int indexOfColumn(String name) {
    if (m_Data.attribute(name) != null)
      return m_Data.attribute(name).index();
    else
      return -1;
  }

  /**
   * Removes all set class attributes.
   */
  @Override
  public void removeClassAttributes() {
    m_Data.setClassIndex(-1);
  }

  /**
   * Returns whether the specified column is a class attribute.
   *
   * @param colKey	they key of the column to query
   * @return		true if column a class attribute
   */
  @Override
  public boolean isClassAttribute(String colKey) {
    int		col;

    col = cellKeyToIndex(colKey);
    return (col > -1) && (m_Data.classIndex() == col);
  }

  /**
   * Returns whether the specified column is a class attribute.
   *
   * @param name	they name of the column to query
   * @return		true if column a class attribute
   */
  @Override
  public boolean isClassAttributeByName(String name) {
    Attribute	att;

    att = m_Data.attribute(name);
    return (att != null) && (att.index() == m_Data.classIndex());
  }

  /**
   * Returns whether the specified column is a class attribute.
   *
   * @param colIndex	they index of the column to query
   * @return		true if column a class attribute
   */
  @Override
  public boolean isClassAttribute(int colIndex) {
    return (colIndex > -1) && (colIndex == m_Data.classIndex());
  }

  /**
   * Sets the class attribute status for a column.
   *
   * @param colKey	the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  @Override
  public boolean setClassAttribute(String colKey, boolean isClass) {
    return setClassAttribute(cellKeyToIndex(colKey), isClass);
  }

  /**
   * Sets the class attribute status for a column.
   *
   * @param name	the name of the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  @Override
  public boolean setClassAttributeByName(String name, boolean isClass) {
    Attribute	att;

    att = m_Data.attribute(name);
    return (att != null) && setClassAttribute(att.index(), isClass);
  }

  /**
   * Sets the class attribute status for a column.
   *
   * @param colIndex	the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  @Override
  public boolean setClassAttribute(int colIndex, boolean isClass) {
    if (colIndex > -1) {
      if (isClass) {
	m_Data.setClassIndex(colIndex);
	return true;
      }
      else {
	if (m_Data.classIndex() > -1) {
	  if (m_Data.classIndex() == colIndex) {
	    m_Data.setClassIndex(-1);
	    return true;
	  }
	}
      }
    }
    return false;
  }

  /**
   * Returns all the class attributes that are currently set.
   *
   * @return		the column keys of class attributes (not ordered)
   */
  @Override
  public String[] getClassAttributeKeys() {
    if (m_Data.classIndex() == -1)
      return new String[0];
    else
      return new String[]{"" + m_Data.classIndex()};
  }

  /**
   * Returns all the class attributes that are currently set.
   *
   * @return		the column names of class attributes (not ordered)
   */
  @Override
  public String[] getClassAttributeNames() {
    if (m_Data.classIndex() == -1)
      return new String[0];
    else
      return new String[]{m_Data.classAttribute().name()};
  }

  /**
   * Returns all the class attributes that are currently set.
   *
   * @return		the indices of class attributes (sorted asc)
   */
  @Override
  public int[] getClassAttributeIndices() {
    if (m_Data.classIndex() == -1)
      return new int[0];
    else
      return new int[]{m_Data.classIndex()};
  }

  /**
   * Returns a spreadsheet containing only the input columns, not class
   * columns.
   *
   * @return		the input features, null if data conists only of class columns
   */
  @Override
  public SpreadSheet getInputs() {
    Instances	data;

    if (m_Data.classIndex() == -1)
      return this;

    data = new Instances(m_Data);
    data.setClassIndex(-1);
    data.deleteAttributeAt(m_Data.classIndex());

    return new InstancesView(data);
  }

  /**
   * Returns a spreadsheet containing only output columns, i.e., the class
   * columns.
   *
   * @return		the output features, null if data has no class columns
   */
  @Override
  public SpreadSheet getOutputs() {
    Instances	data;
    Remove	remove;

    if (m_Data.classIndex() == -1)
      return null;

    data = new Instances(m_Data);
    data.setClassIndex(-1);
    remove = new Remove();
    remove.setAttributeIndicesArray(new int[]{m_Data.classIndex()});
    remove.setInvertSelection(true);
    try {
      remove.setInputFormat(data);
      data = Filter.useFilter(data, remove);
      return new InstancesView(data);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to apply Remove filter!", e);
    }
  }

  /**
   * Creates a view of the spreadsheet with the specified rows/columns.
   *
   * @param columns	the columns to use, null for all
   * @param rows	the rows to use, null for all
   * @return		the view
   */
  public SpreadSheetView toView(int[] rows, int[] columns) {
    return new SpreadSheetView(this, rows, columns);
  }
}
