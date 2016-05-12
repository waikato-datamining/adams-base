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

package adams.ml.data;

import adams.core.DateTime;
import adams.core.DateTimeMsec;
import adams.core.Time;
import adams.core.TimeMsec;
import adams.core.Utils;
import adams.core.base.BaseDateTimeMsec;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import weka.core.Attribute;
import weka.core.Instance;

import java.util.Date;

/**
 * Wrapper for single cell values in a {@link Instance} object.
 * All dates are treated as {@link DateTimeMsec}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataCellView
  implements Cell {

  private static final long serialVersionUID = -1124569966977121103L;

  /** the owning row. */
  protected InstanceView m_Owner;

  /** the column index. */
  protected int m_ColIndex;

  /**
   * Initializes the cell.
   *
   * @param owner	the owning row
   * @param colIndex	the column index
   */
  public DataCellView(InstanceView owner, int colIndex) {
    m_Owner    = owner;
    m_ColIndex = colIndex;
  }

  /**
   * Sets the row this cell belongs to.
   *
   * @param owner	the owner
   */
  @Override
  public void setOwner(Row owner) {
    if (owner instanceof InstanceView)
      m_Owner = (InstanceView) owner;
    else
      throw new IllegalArgumentException("Owner can only be " + InstanceView.class.getName());
  }

  /**
   * Returns the row this cell belongs to.
   *
   * @return		the owner
   */
  @Override
  public InstanceView getOwner() {
    return m_Owner;
  }

  /**
   * Returns the spreadsheet this cell belongs to.
   *
   * @return		the spreadsheet
   */
  @Override
  public SpreadSheet getSpreadSheet() {
    if (m_Owner != null)
      return m_Owner.getOwner();
    else
      return null;
  }

  /**
   * Obtains the content/type of the other cell, but not the owner.
   *
   * @param cell	the cell to get the content/type from
   */
  @Override
  public void assign(Cell cell) {
    switch (m_Owner.getData().attribute(m_ColIndex).type()) {
      case Attribute.NUMERIC:
	m_Owner.getData().setValue(m_ColIndex, cell.toDouble());
	break;
      case Attribute.DATE:
	m_Owner.getData().setValue(m_ColIndex, cell.toAnyDateType().getTime());
	break;
      case Attribute.NOMINAL:
      case Attribute.STRING:
	m_Owner.getData().setValue(m_ColIndex, cell.getContent());
	break;
      default:
	throw new IllegalArgumentException(
	  "Cannot handle attribute type: "
	    + Attribute.typeToString(m_Owner.getData().attribute(m_ColIndex).type()));
    }
  }

  /**
   * Sets the cell to missing.
   */
  @Override
  public void setMissing() {
    m_Owner.getData().setMissing(m_ColIndex);
  }

  /**
   * Ignored.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Boolean value) {
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Byte value) {
    if (value != null)
      m_Owner.getData().setValue(m_ColIndex, value.doubleValue());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Short value) {
    if (value != null)
      m_Owner.getData().setValue(m_ColIndex, value.doubleValue());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Integer value) {
    if (value != null)
      m_Owner.getData().setValue(m_ColIndex, value.doubleValue());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Long value) {
    if (value != null)
      m_Owner.getData().setValue(m_ColIndex, value.doubleValue());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Float value) {
    if (value != null)
      m_Owner.getData().setValue(m_ColIndex, value.doubleValue());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Double value) {
    if (value != null)
      m_Owner.getData().setValue(m_ColIndex, value);
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Date value) {
    if ((value != null) && isAnyDateType())
      m_Owner.getData().setValue(m_ColIndex, value.getTime());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(DateTime value) {
    if ((value != null) && isAnyDateType())
      m_Owner.getData().setValue(m_ColIndex, value.getTime());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(DateTimeMsec value) {
    if ((value != null) && isAnyDateType())
      m_Owner.getData().setValue(m_ColIndex, value.getTime());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(Time value) {
    if ((value != null) && isAnyDateType())
      m_Owner.getData().setValue(m_ColIndex, value.getTime());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null interpreted as missing value
   * @return		the cell itself
   */
  @Override
  public Cell setContent(TimeMsec value) {
    if ((value != null) && isAnyDateType())
      m_Owner.getData().setValue(m_ColIndex, value.getTime());
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Attempts to determine the data type of the string.
   *
   * @param value	the non-empty string to parse
   * @return		the cell itself
   */
  @Override
  public Cell parseContent(String value) {
    if (value == null)
      setMissing();
    else if (Utils.isDouble(value))
      parseContent(value, ContentType.DOUBLE);
    else if (value.contains(":"))
      parseContent(value, ContentType.DATETIMEMSEC);
    else
      parseContent(value, ContentType.STRING);
    return this;
  }

  /**
   * Sets the content of the cell. Tries to determine whether the cell
   * content is numeric or not.
   *
   * @param value	the content
   * @return		the cell itself
   */
  @Override
  public Cell setContent(String value) {
    if ((value == null) || value.equals(SpreadSheet.MISSING_VALUE)) {
      setMissing();
    }
    else {
      if (value.length() > 0)
	parseContent(value);
      else
	setContentAsString(value);
    }
    return this;
  }

  /**
   * Parses the content of the cell using the specified type. If the string
   * cannot be parsed according to the type, missing is used instead.
   *
   * @param value	the content
   * @param type	the expected type
   * @return		the parsed content
   */
  @Override
  public Object parseContent(String value, ContentType type) {
    switch (type) {
      case DOUBLE:
	if ((value == null) || !Utils.isDouble(value))
	  return SpreadSheet.MISSING_VALUE;
	else
	  return Double.parseDouble(value);
      case STRING:
	if (value == null)
	  return SpreadSheet.MISSING_VALUE;
	else
	  return value;
      case DATETIMEMSEC:
	if ((value == null) || !new BaseDateTimeMsec().isValid(value))
	  return SpreadSheet.MISSING_VALUE;
	else
	  return new BaseDateTimeMsec(value);
      default:
	return SpreadSheet.MISSING_VALUE;
    }
  }

  /**
   * Sets the string content of the cell.
   *
   * @param value	the content
   * @return		the cell itself
   */
  @Override
  public Cell setContentAsString(String value) {
    if (value != null)
      m_Owner.getData().setValue(m_ColIndex, value);
    else
      m_Owner.getData().setMissing(m_ColIndex);
    return this;
  }

  /**
   * Ignored.
   *
   * @param value	the content
   * @return		the cell itself
   */
  @Override
  public Cell setObject(Object value) {
    return this;
  }

  /**
   * Ignored.
   *
   * @param value	the content
   * @return		the cell itself
   */
  @Override
  public Cell setFormula(String value) {
    return this;
  }

  /**
   * Returns the formula.
   *
   * @return		the formula, null if none used
   */
  @Override
  public String getFormula() {
    return null;
  }

  /**
   * Returns the object.
   *
   * @return		the object, null if none set
   */
  @Override
  public Object getObject() {
    return null;
  }

  /**
   * Returns the content of the cell.
   *
   * @return		the content
   */
  @Override
  public String getContent() {
    switch (m_Owner.getData().attribute(m_ColIndex).type()) {
      case Attribute.NUMERIC:
	return "" + m_Owner.getData().value(m_ColIndex);
      case Attribute.DATE:
      case Attribute.NOMINAL:
      case Attribute.STRING:
	return m_Owner.getData().stringValue(m_ColIndex);
      default:
	throw new IllegalStateException(
	  "Unhandled attribute type: " +
	    Attribute.typeToString(m_Owner.getData().attribute(m_ColIndex).type()));
    }
  }

  /**
   * Returns the content type.
   *
   * @return		the type
   */
  @Override
  public ContentType getContentType() {
    switch (m_Owner.getData().attribute(m_ColIndex).type()) {
      case Attribute.NUMERIC:
	return ContentType.DOUBLE;
      case Attribute.DATE:
	return ContentType.DATETIMEMSEC;
      case Attribute.NOMINAL:
      case Attribute.STRING:
	return ContentType.STRING;
      default:
	throw new IllegalStateException(
	  "Unhandled attribute type: " +
	    Attribute.typeToString(m_Owner.getData().attribute(m_ColIndex).type()));
    }
  }

  /**
   * Determines the best set-method based on the class of the provided object.
   * Fallback is the {@link #setObject(Object)} method.
   *
   * @param value	the value to set
   * @return		the cell itself
   */
  @Override
  public Cell setNative(Object value) {
    if (value == null)
      setMissing();
    // common
    else if (value instanceof String)
      setContentAsString((String) value);
    else if (value instanceof Integer)
      setContent((Integer) value);
    else if (value instanceof Long)
      setContent((Long) value);
    else if (value instanceof Double)
      setContent((Double) value);
    else if (value instanceof DateTime)
      setContent((DateTime) value);
    else if (value instanceof DateTimeMsec)
      setContent((DateTimeMsec) value);
    else if (value instanceof Time)
      setContent((Time) value);
    else if (value instanceof TimeMsec)
      setContent((TimeMsec) value);
    else if (value instanceof Date)
      setContent((Date) value);
    else if (value instanceof Boolean)
      setContent((Boolean) value);
    // less common
    else if (value instanceof Byte)
      setContent((Byte) value);
    else if (value instanceof Short)
      setContent((Short) value);
    else if (value instanceof Float)
      setContent((Float) value);
    else
      setObject(value);
    return this;
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
    switch (getContentType()) {
      case MISSING:
	return SpreadSheet.MISSING_VALUE;
      case BOOLEAN:
	return toBoolean();
      case TIME:
	return toTime();
      case TIMEMSEC:
	return toTimeMsec();
      case DATE:
	return toDate();
      case DATETIME:
	return toDateTime();
      case DATETIMEMSEC:
	return toDateTimeMsec();
      case LONG:
	return toLong();
      case DOUBLE:
	return toDouble();
      case STRING:
	return toString();
      case OBJECT:
	return getObject();
      default:
	throw new IllegalStateException("Unhandled content type: " + getContentType());
    }
  }

  /**
   * Returns the column this cell is in.
   *
   * @return		the column index, -1 if not available
   */
  @Override
  public int index() {
    return m_ColIndex;
  }

  /**
   * Checks whether the stored string is numeric.
   *
   * @return		true if the content is numeric
   */
  @Override
  public boolean isNumeric() {
    return m_Owner.getData().attribute(m_ColIndex).isNumeric();
  }

  /**
   * Checks whether the cell contains a missing value.
   *
   * @return		true if missing value
   */
  @Override
  public boolean isMissing() {
    return m_Owner.getData().isMissing(m_ColIndex);
  }

  /**
   * Checks whether the cell represents a boolean value.
   *
   * @return		true if boolean value
   */
  @Override
  public boolean isBoolean() {
    return false;
  }

  /**
   * Checks whether the cell represents a boolean value.
   *
   * @return		true if boolean value
   */
  @Override
  public Boolean toBoolean() {
    return null;
  }

  /**
   * Checks whether the cell represents a date, time or date/time value.
   *
   * @return		true if date, time or date/time value
   */
  @Override
  public boolean isAnyDateType() {
    return isDateTimeMsec();
  }

  /**
   * Returns the date content, null if not a date, time or date/time.
   *
   * @return		the date, null if not date, time or date/time
   */
  @Override
  public Date toAnyDateType() {
    return toDateTimeMsec();
  }

  /**
   * Checks whether the cell represents a date value.
   *
   * @return		true if date value
   */
  @Override
  public boolean isDate() {
    return false;
  }

  /**
   * Returns the date content, null if not a date.
   *
   * @return		the date, null if not date
   */
  @Override
  public Date toDate() {
    return null;
  }

  /**
   * Checks whether the cell represents a date/time value.
   *
   * @return		true if date/time value
   */
  @Override
  public boolean isDateTime() {
    return false;
  }

  /**
   * Returns the date/time content, null if not a date/time.
   *
   * @return		the date/time, null if not date/time
   */
  @Override
  public DateTime toDateTime() {
    return null;
  }

  /**
   * Checks whether the cell represents a date/time with msec value.
   *
   * @return		true if date/time msec value
   */
  @Override
  public boolean isDateTimeMsec() {
    return m_Owner.getData().attribute(m_ColIndex).isDate();
  }

  /**
   * Returns the date/time msec content, null if not a date/time.
   *
   * @return		the date/time msec, null if not date/time
   */
  @Override
  public DateTimeMsec toDateTimeMsec() {
    return new DateTimeMsec(new Date((long) m_Owner.getData().value(m_ColIndex)));
  }

  /**
   * Checks whether the cell represents a time value.
   *
   * @return		true if time value
   */
  @Override
  public boolean isTime() {
    return false;
  }

  /**
   * Returns the time content, null if not a time.
   *
   * @return		the time, null if not time
   */
  @Override
  public Time toTime() {
    return null;
  }

  /**
   * Checks whether the cell represents a time/msec value.
   *
   * @return		true if time/msec value
   */
  @Override
  public boolean isTimeMsec() {
    return false;
  }

  /**
   * Returns the time/msec content, null if not a time/msec.
   *
   * @return		the time/msec, null if not time/msec
   */
  @Override
  public TimeMsec toTimeMsec() {
    return null;
  }

  /**
   * Returns whether the content represents a double number.
   *
   * @return		true if a double
   */
  @Override
  public boolean isDouble() {
    return (m_Owner.getData().attribute(m_ColIndex).type() == Attribute.NUMERIC);
  }

  /**
   * Returns the content as double, if possible.
   *
   * @return		the content as double, if representing a number,
   * 			otherwise null
   */
  @Override
  public Double toDouble() {
    if (isDouble())
      return m_Owner.getData().value(m_ColIndex);
    else
      return null;
  }

  /**
   * Returns whether the content represents a long number.
   *
   * @return		true if a long
   */
  @Override
  public boolean isLong() {
    return false;
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
    return null;
  }

  /**
   * Returns whether the content represents a formula.
   *
   * @return		true if a formula
   */
  @Override
  public boolean isFormula() {
    return false;
  }

  /**
   * Returns whether the content represents an object.
   *
   * @return		true if an object
   */
  @Override
  public boolean isObject() {
    return false;
  }

  /**
   * Does nothing.
   */
  @Override
  public void calculate() {
  }
}
