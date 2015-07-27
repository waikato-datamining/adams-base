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
 * Cell.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import adams.core.DateTime;
import adams.core.DateTimeMsec;
import adams.core.Time;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a single cell.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Cell
  extends Serializable {

  /** the type of content. */
  public enum ContentType {
    /** missing value. */
    MISSING,
    /** string. */
    STRING,
    /** boolean */
    BOOLEAN,
    /** integer. */
    LONG,
    /** integer. */
    DOUBLE,
    /** date. */
    DATE,
    /** date+time. */
    DATETIME,
    /** date+time with msec. */
    DATETIMEMSEC,
    /** time. */
    TIME,
    /** object (needs custom handler, must implement Comparable). */
    OBJECT
  }

  /** the prefix for a formula. */
  public final static String PREFIX_FORMULA = "=";

  /** display string in case of an error in a formula. */
  public final static String FORMULA_ERROR = "#ERROR#";

  /**
   * Sets the row this cell belongs to.
   *
   * @param owner	the owner
   */
  public void setOwner(Row owner);

  /**
   * Returns the row this cell belongs to.
   *
   * @return		the owner
   */
  public Row getOwner();

  /**
   * Returns the spreadsheet this cell belongs to.
   *
   * @return		the spreadsheet
   */
  public SpreadSheet getSpreadSheet();

  /**
   * Obtains the content/type of the other cell, but not the owner.
   *
   * @param cell	the cell to get the content/type from
   */
  public void assign(Cell cell);

  /**
   * Sets the cell to missing.
   */
  public void setMissing();

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Boolean value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Byte value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Short value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Integer value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Long value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null or NaN is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Float value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null or NaN is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Double value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Date value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(DateTime value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(DateTimeMsec value);

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Time value);

  /**
   * Attempts to determine the data type of the string.
   *
   * @param value	the non-empty string to parse
   * @return		the cell itself
   */
  public Cell parseContent(String value);

  /**
   * Sets the content of the cell. Tries to determine whether the cell
   * content is numeric or not.
   *
   * @param value	the content
   * @return		the cell itself
   */
  public Cell setContent(String value);

  /**
   * Parses the content of the cell using the specified type. If the string
   * cannot be parsed according to the type, missing is used instead.
   *
   * @param value	the content
   * @param type	the expected type
   * @return		the parsed content
   */
  public Object parseContent(String value, ContentType type);

  /**
   * Sets the string content of the cell.
   *
   * @param value	the content
   * @return		the cell itself
   */
  public Cell setContentAsString(String value);

  /**
   * Sets the object content of the cell.
   *
   * @param value	the content
   * @return		the cell itself
   */
  public Cell setObject(Object value);

  /**
   * Sets the formula content of the cell.
   *
   * @param value	the content
   * @return		the cell itself
   */
  public Cell setFormula(String value);

  /**
   * Returns the formula.
   *
   * @return		the formula, null if none used
   */
  public String getFormula();

  /**
   * Returns the object.
   *
   * @return		the object, null if none set
   */
  public Object getObject();

  /**
   * Returns the content of the cell.
   *
   * @return		the content
   */
  public String getContent();

  /**
   * Returns the content type.
   *
   * @return		the type
   */
  public ContentType getContentType();

  /**
   * Determines the best set-method based on the class of the provided object.
   * Fallback is the {@link #setObject(Object)} method.
   *
   * @param value	the value to set
   * @return		the cell itself
   */
  public Cell setNative(Object value);

  /**
   * Returns the cell as native object, according to its type.
   * If a cell is missing, the result is the {@link SpreadSheet#MISSING_VALUE}
   * string.
   *
   * @return		the corresponding object
   */
  public Object getNative();

  /**
   * Returns the column this cell is in.
   *
   * @return		the column index, -1 if not available
   */
  public int index();

  /**
   * Checks whether the stored string is numeric.
   *
   * @return		true if the content is numeric
   */
  public boolean isNumeric();

  /**
   * Checks whether the cell contains a missing value.
   *
   * @return		true if missing value
   */
  public boolean isMissing();

  /**
   * Checks whether the cell represents a boolean value.
   *
   * @return		true if boolean value
   */
  public boolean isBoolean();

  /**
   * Returns the boolean content, null if not a boolean.
   *
   * @return		the date, null if not boolean
   */
  public Boolean toBoolean();

  /**
   * Checks whether the cell represents a date, time or date/time value.
   *
   * @return		true if date, time or date/time value
   */
  public boolean isAnyDateType();

  /**
   * Returns the date content, null if not a date, time or date/time.
   *
   * @return		the date, null if not date, time or date/time
   */
  public Date toAnyDateType();

  /**
   * Checks whether the cell represents a date value.
   *
   * @return		true if date value
   */
  public boolean isDate();

  /**
   * Returns the date content, null if not a date.
   *
   * @return		the date, null if not date
   */
  public Date toDate();

  /**
   * Checks whether the cell represents a date/time value.
   *
   * @return		true if date/time value
   */
  public boolean isDateTime();

  /**
   * Returns the date/time content, null if not a date/time.
   *
   * @return		the date/time, null if not date/time
   */
  public DateTime toDateTime();

  /**
   * Checks whether the cell represents a date/time with msec value.
   *
   * @return		true if date/time msec value
   */
  public boolean isDateTimeMsec();

  /**
   * Returns the date/time msec content, null if not a date/time.
   *
   * @return		the date/time msec, null if not date/time
   */
  public DateTimeMsec toDateTimeMsec();

  /**
   * Checks whether the cell represents a time value.
   *
   * @return		true if time value
   */
  public boolean isTime();

  /**
   * Returns the time content, null if not a time.
   *
   * @return		the time, null if not time
   */
  public Time toTime();

  /**
   * Returns the content of the cell.
   *
   * @return		the content
   * @see		#getContent()
   */
  @Override
  public String toString();

  /**
   * Returns whether the content represents a double number.
   *
   * @return		true if a double
   */
  public boolean isDouble();

  /**
   * Returns the content as double, if possible.
   *
   * @return		the content as double, if representing a number,
   * 			otherwise null
   */
  public Double toDouble();

  /**
   * Returns whether the content represents a long number.
   *
   * @return		true if a long
   */
  public boolean isLong();

  /**
   * Returns the content as long, if possible. First, a Double object is
   * created and then the longValue() method called to return the value.
   *
   * @return		the content as long, if representing a number,
   * 			otherwise null
   */
  public Long toLong();

  /**
   * Returns whether the content represents a formula.
   *
   * @return		true if a formula
   */
  public boolean isFormula();

  /**
   * Returns whether the content represents an object.
   *
   * @return		true if an object
   */
  public boolean isObject();

  /**
   * Recalculates the value from the cell's formula.
   */
  public void calculate();
}