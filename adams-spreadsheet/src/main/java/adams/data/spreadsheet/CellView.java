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
 * CellView.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet;

import adams.core.DateTime;
import adams.core.DateTimeMsec;
import adams.core.Time;
import adams.core.TimeMsec;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Date;

/**
 * Wrapper for a cell.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CellView
  implements Cell {

  private static final long serialVersionUID = -2460726233145475867L;

  /** the owner. */
  protected Row m_Owner;

  /** the wrapped cell. */
  protected Cell m_Cell;

  /**
   * Initializes the wrapper.
   *
   * @param owner 	the owning row
   * @param cell	the cell to wrap
   */
  public CellView(Row owner, Cell cell) {
    super();

    if (owner == null)
      throw new IllegalArgumentException("Owner cannot be null!");
    if (cell == null)
      throw new IllegalArgumentException("Cell cannot be null!");

    m_Owner = owner;
    m_Cell  = cell;
  }

  /**
   * Sets the row this cell belongs to.
   *
   * @param owner	the owner
   */
  @Override
  public void setOwner(Row owner) {
    throw new NotImplementedException();
  }

  /**
   * Returns the row this cell belongs to.
   *
   * @return		the owner
   */
  @Override
  public Row getOwner() {
    return m_Owner;
  }

  /**
   * Returns the spreadsheet this cell belongs to.
   *
   * @return		the spreadsheet
   */
  @Override
  public SpreadSheet getSpreadSheet() {
    return m_Owner.getOwner();
  }

  /**
   * Obtains the content/type of the other cell, but not the owner.
   * <br>
   * Not implemented!
   *
   * @param cell	the cell to get the content/type from
   */
  @Override
  public void assign(Cell cell) {
    throw new NotImplementedException();
  }

  /**
   * Sets the cell to missing.
   * <br>
   * Not implemented!
   */
  @Override
  public void setMissing() {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Boolean value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Byte value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Short value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Integer value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Long value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null or NaN is intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Float value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null or NaN is intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Double value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Date value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(DateTime value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(DateTimeMsec value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Time value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(TimeMsec value) {
    throw new NotImplementedException();
  }

  /**
   * Attempts to determine the data type of the string.
   * <br>
   * Not implemented!
   *
   * @param value	the non-empty string to parse
   * @return		the cell itself
   */
  @Override
  public Cell parseContent(String value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the content of the cell. Tries to determine whether the cell
   * content is numeric or not.
   * <br>
   * Not implemented!
   *
   * @param value	the content
   * @return		the cell itself
   */
  @Override
  public Cell setContent(String value) {
    throw new NotImplementedException();
  }

  /**
   * Parses the content of the cell using the specified type. If the string
   * cannot be parsed according to the type, missing is used instead.
   * <br>
   * Not implemented!
   *
   * @param value	the content
   * @param type	the expected type
   * @return		the parsed content
   */
  @Override
  public Object parseContent(String value, ContentType type) {
    throw new NotImplementedException();
  }

  /**
   * Sets the string content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content
   * @return		the cell itself
   */
  @Override
  public Cell setContentAsString(String value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the object content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content
   * @return		the cell itself
   */
  @Override
  public Cell setObject(Object value) {
    throw new NotImplementedException();
  }

  /**
   * Sets the formula content of the cell.
   * <br>
   * Not implemented!
   *
   * @param value	the content
   * @return		the cell itself
   */
  @Override
  public Cell setFormula(String value) {
    throw new NotImplementedException();
  }

  /**
   * Returns the formula.
   *
   * @return		the formula, null if none used
   */
  @Override
  public String getFormula() {
    return m_Cell.getFormula();
  }

  /**
   * Returns the object.
   *
   * @return		the object, null if none set
   */
  @Override
  public Object getObject() {
    return m_Cell.getObject();
  }

  /**
   * Returns the content of the cell.
   *
   * @return		the content
   */
  @Override
  public String getContent() {
    return m_Cell.getContent();
  }

  /**
   * Returns the content type.
   *
   * @return		the type
   */
  @Override
  public ContentType getContentType() {
    return m_Cell.getContentType();
  }

  /**
   * Determines the best set-method based on the class of the provided object.
   * Fallback is the {@link #setObject(Object)} method.
   * <br>
   * Not implemented!
   *
   * @param value	the value to set
   * @return		the cell itself
   */
  @Override
  public Cell setNative(Object value) {
    throw new NotImplementedException();
  }

  /**
   * Returns the cell as native object, according to its type.
   * If a cell is missing, the result is the {@link SpreadSheet#MISSING_VALUE}
   * string.
   *
   * @return		the corresponding object
   */
  @Override
  public Object getNative() {
    return m_Cell.getNative();
  }

  /**
   * Returns the column this cell is in.
   *
   * @return		the column index, -1 if not available
   */
  @Override
  public int index() {
    return m_Owner.indexOf(this);
  }

  /**
   * Checks whether the stored string is numeric.
   *
   * @return		true if the content is numeric
   */
  @Override
  public boolean isNumeric() {
    return m_Cell.isNumeric();
  }

  /**
   * Checks whether the cell contains a missing value.
   *
   * @return		true if missing value
   */
  @Override
  public boolean isMissing() {
    return m_Cell.isMissing();
  }

  /**
   * Checks whether the cell represents a boolean value.
   *
   * @return		true if boolean value
   */
  @Override
  public boolean isBoolean() {
    return m_Cell.isBoolean();
  }

  /**
   * Returns the boolean content, null if not a boolean.
   *
   * @return		the date, null if not boolean
   */
  @Override
  public Boolean toBoolean() {
    return m_Cell.toBoolean();
  }

  /**
   * Checks whether the cell represents a date, time or date/time value.
   *
   * @return		true if date, time or date/time value
   */
  @Override
  public boolean isAnyDateType() {
    return m_Cell.isAnyDateType();
  }

  /**
   * Returns the date content, null if not a date, time or date/time.
   *
   * @return		the date, null if not date, time or date/time
   */
  @Override
  public Date toAnyDateType() {
    return m_Cell.toAnyDateType();
  }

  /**
   * Checks whether the cell represents a date value.
   *
   * @return		true if date value
   */
  @Override
  public boolean isDate() {
    return m_Cell.isDate();
  }

  /**
   * Returns the date content, null if not a date.
   *
   * @return		the date, null if not date
   */
  @Override
  public Date toDate() {
    return m_Cell.toDate();
  }

  /**
   * Checks whether the cell represents a date/time value.
   *
   * @return		true if date/time value
   */
  @Override
  public boolean isDateTime() {
    return m_Cell.isDateTime();
  }

  /**
   * Checks whether the cell represents a date/time with msec value.
   *
   * @return		true if date/time msec value
   */
  @Override
  public DateTime toDateTime() {
    return m_Cell.toDateTime();
  }

  /**
   * Checks whether the cell represents a date/time with msec value.
   *
   * @return		true if date/time msec value
   */
  @Override
  public boolean isDateTimeMsec() {
    return m_Cell.isDateTimeMsec();
  }

  /**
   * Returns the date/time msec content, null if not a date/time.
   *
   * @return		the date/time msec, null if not date/time
   */
  @Override
  public DateTimeMsec toDateTimeMsec() {
    return m_Cell.toDateTimeMsec();
  }

  /**
   * Checks whether the cell represents a time value.
   *
   * @return		true if time value
   */
  @Override
  public boolean isTime() {
    return m_Cell.isTime();
  }

  /**
   * Returns the time content, null if not a time.
   *
   * @return		the time, null if not time
   */
  @Override
  public Time toTime() {
    return m_Cell.toTime();
  }

  /**
   * Checks whether the cell represents a time/msec value.
   *
   * @return		true if time/msec value
   */
  @Override
  public boolean isTimeMsec() {
    return m_Cell.isTimeMsec();
  }

  /**
   * Returns the time/msec content, null if not a time/msec.
   *
   * @return		the time/msec, null if not time/msec
   */
  @Override
  public TimeMsec toTimeMsec() {
    return m_Cell.toTimeMsec();
  }

  /**
   * Returns whether the content represents a double number.
   *
   * @return		true if a double
   */
  @Override
  public boolean isDouble() {
    return m_Cell.isDouble();
  }

  /**
   * Returns the content as double, if possible.
   *
   * @return		the content as double, if representing a number,
   * 			otherwise null
   */
  @Override
  public Double toDouble() {
    return m_Cell.toDouble();
  }

  /**
   * Returns whether the content represents a long number.
   *
   * @return		true if a long
   */
  @Override
  public boolean isLong() {
    return m_Cell.isLong();
  }

  /**
   * Returns the content as long, if possible. First, a Double object is
   * created and then the longValue() method called to return the value.
   *
   * @return		the content as long, if representing a number,
   * 			otherwise null
   */
  @Override
  public Long toLong() {
    return m_Cell.toLong();
  }

  /**
   * Returns whether the content represents a formula.
   *
   * @return		true if a formula
   */
  @Override
  public boolean isFormula() {
    return m_Cell.isFormula();
  }

  /**
   * Returns whether the content represents an object.
   *
   * @return		true if an object
   */
  @Override
  public boolean isObject() {
    return m_Cell.isObject();
  }

  /**
   * Recalculates the value from the cell's formula.
   */
  @Override
  public void calculate() {
    m_Cell.calculate();
  }

  /**
   * Returns the underlying cell.
   *
   * @return		the cell
   */
  public Cell getCell() {
    return m_Cell;
  }

  /**
   * Returns the content of the cell.
   *
   * @return		the content
   * @see		#getContent()
   */
  @Override
  public String toString() {
    return getContent();
  }
}
