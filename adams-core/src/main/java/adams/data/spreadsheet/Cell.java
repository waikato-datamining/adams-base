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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import adams.core.DateTime;
import adams.core.Time;
import adams.core.Utils;
import adams.parser.SpreadSheetFormula;

/**
 * Represents a single cell.
 * <p/>
 * Any integer type gets turned into a Long and any floating point type
 * into a Double.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Cell
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -3912508808391288142L;

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
    /** time. */
    TIME,
    /** object (needs custom handler, must implement Comparable). */
    OBJECT
  }

  /** the prefix for a formula. */
  public final static String PREFIX_FORMULA = "=";

  /** display string in case of an error in a formula. */
  public final static String FORMULA_ERROR = "#ERROR#";

  /** the row this cell belongs to. */
  protected Row m_Owner;

  /** the content of the cell. */
  protected double m_Content;

  /** the formula. */
  protected String m_Formula;

  /** the object. */
  protected Object m_Object;

  /** whether the content is numeric. */
  protected ContentType m_ContentType;

  /**
   * Constructor.
   *
   * @param owner	the row this cell belongs to
   */
  public Cell(Row owner) {
    super();

    m_Owner   = owner;
    m_Formula = null;
    m_Object  = null;
    if (m_Owner != null)
      setContentAsString("");
    else
      setMissing();
  }

  /**
   * Sets the row this cell belongs to.
   *
   * @param owner	the owner
   */
  public void setOwner(Row owner) {
    m_Owner = owner;
  }

  /**
   * Returns the row this cell belongs to.
   *
   * @return		the owner
   */
  public Row getOwner() {
    return m_Owner;
  }

  /**
   * Returns the spreadsheet this cell belongs to.
   *
   * @return		the spreadsheet
   */
  public SpreadSheet getSpreadSheet() {
    return getOwner().getOwner();
  }

  /**
   * Obtains the content/type of the other cell, but not the owner.
   *
   * @param cell	the cell to get the content/type from
   */
  public void assign(Cell cell) {
    String	content;
    
    m_ContentType = cell.m_ContentType;

    if (cell.isFormula()) {
      setFormula(cell.getFormula());
    }
    else if (cell.isObject()) {
      setObject(cell.getObject());
    }
    else {
      // for string we need to ensure that the shared strings table
      // gets updated correctly
      switch (m_ContentType) {
	case STRING:
	  content = cell.getContent();
	  if (!cell.isMissing() && content.equals(SpreadSheet.MISSING_VALUE))
	    setContentAsString("'" + SpreadSheet.MISSING_VALUE + "'");
	  else
	    setContentAsString(content);
	  break;
	default:
	  m_Content = cell.m_Content;
      }
    }
  }

  /**
   * Sets the cell to missing.
   */
  public void setMissing() {
    m_Content     = Double.NaN;
    m_Formula     = null;
    m_Object      = null;
    m_ContentType = ContentType.MISSING;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Boolean value) {
    if (value == null) {
      setMissing();
    }
    else {
      m_Content     = (value ? 1 : 0);
      m_ContentType = ContentType.BOOLEAN;
    }
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Byte value) {
    if (value == null) {
      setMissing();
    }
    else {
      m_Content     = value.longValue();
      m_ContentType = ContentType.LONG;
    }
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Short value) {
    if (value == null) {
      setMissing();
    }
    else {
      m_Content     = value.longValue();
      m_ContentType = ContentType.LONG;
    }
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Integer value) {
    if (value == null) {
      setMissing();
    }
    else {
      m_Content     = value.longValue();
      m_ContentType = ContentType.LONG;
    }
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Long value) {
    if (value == null) {
      setMissing();
    }
    else {
      m_Content     = value;
      m_ContentType = ContentType.LONG;
    }
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null or NaN is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Float value) {
    if ((value == null) || (Float.isNaN(value))) {
      setMissing();
    }
    else {
      m_Content     = value.floatValue();
      m_ContentType = ContentType.DOUBLE;
    }
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null or NaN is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Double value) {
    if ((value == null) || (Double.isNaN(value))) {
      setMissing();
    }
    else {
      m_Content     = value;
      m_ContentType = ContentType.DOUBLE;
    }
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Date value) {
    if (value == null) {
      setMissing();
    }
    else {
      m_Content     = value.getTime();
      m_ContentType = ContentType.DATE;
    }
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(DateTime value) {
    if (value == null) {
      setMissing();
    }
    else {
      m_Content     = value.getTime();
      m_ContentType = ContentType.DATETIME;
    }
    return this;
  }

  /**
   * Sets the content of the cell.
   *
   * @param value	the content; null is intepreted as missing value
   * @return		the cell itself
   */
  public Cell setContent(Time value) {
    if (value == null) {
      setMissing();
    }
    else {
      m_Content     = value.getTime();
      m_ContentType = ContentType.TIME;
    }
    return this;
  }

  /**
   * Checks whether the string represents a valid formula.
   *
   * @param s		the string to check
   * @return		true if valid formula
   */
  protected boolean checkFormula(String s) {
    try {
      SpreadSheetFormula.evaluate(s, new HashMap(), getSpreadSheet());
      return true;
    }
    catch (Throwable t) {
      return false;
    }
  }

  /**
   * Checks whether the string represents a boolean.
   *
   * @param s		the string to check
   * @return		true if boolean
   */
  protected boolean checkBoolean(String s) {
    s = s.toLowerCase();
    return (s.equals("true") || s.equals("false"));
  }

  /**
   * Checks whether the string represents a time.
   *
   * @param s		the string to check
   * @return		true if time
   * @see		SpreadSheet#getTimeFormat()
   */
  protected boolean checkTime(String s) {
    if (s.indexOf(':') > -1)
      return getSpreadSheet().getTimeFormat().check(s);
    else
      return false;
  }

  /**
   * Checks whether the string represents a date.
   *
   * @param s		the string to check
   * @return		true if date
   * @see		SpreadSheet#getDateFormat()
   */
  protected boolean checkDate(String s) {
    if (s.indexOf('-') > -1)
      return getSpreadSheet().getDateFormat().check(s);
    else
      return false;
  }

  /**
   * Checks whether the string represents a date/time.
   *
   * @param s		the string to check
   * @return		true if date/time
   * @see		SpreadSheet#getDateTimeFormat()
   */
  protected boolean checkDateTime(String s) {
    if ((s.indexOf('-') > -1) && (s.indexOf(':') > -1))
      return getSpreadSheet().getDateTimeFormat().check(s);
    else
      return false;
  }

  /**
   * Checks whether the string represents a long.
   *
   * @param s		the string to check
   * @return		true if time
   * @see		SpreadSheet#getTimeFormat()
   */
  protected boolean checkLong(String s) {
    return Utils.isLong(s);
  }

  /**
   * Checks whether the string represents a time.
   *
   * @param s		the string to check
   * @return		true if time
   * @see		SpreadSheet#getTimeFormat()
   */
  protected boolean checkDouble(String s) {
    return Utils.isDouble(s, getSpreadSheet().getLocale());
  }

  /**
   * Attempts to determine the data type of the string.
   *
   * @param value	the non-empty string to parse
   * @return		the cell itself
   */
  public Cell parseContent(String value) {
    if (checkBoolean(value)) {
      setContent(Boolean.parseBoolean(value));
    }
    else if (checkDateTime(value)) {
      setContent(new DateTime(getSpreadSheet().getDateTimeFormat().parse(value)));
    }
    else if (checkTime(value)) {
      setContent(new Time(getSpreadSheet().getTimeFormat().parse(value).getTime()));
    }
    else if (checkDate(value)) {
      setContent(getSpreadSheet().getDateFormat().parse(value));
    }
    else if (checkLong(value)) {
      setContent(new Long(value));
    }
    else if (checkDouble(value)) {
      try {
	setContent(Utils.toDouble(value, getSpreadSheet().getLocale()));
      }
      catch (Exception e) {
	setContentAsString(null);
      }
    }
    else {
      setContentAsString(value);
    }
    return this;
  }

  /**
   * Sets the content of the cell. Tries to determine whether the cell
   * content is numeric or not.
   *
   * @param value	the content
   * @return		the cell itself
   */
  public Cell setContent(String value) {
    if ((value == null) || (value.equals(SpreadSheet.MISSING_VALUE))) {
      setMissing();
    }
    else {
      if (value.length() > 0) {
	if (value.startsWith(PREFIX_FORMULA)) {
	  if (checkFormula(value)) {
	    m_Formula = value;
	    m_Object  = null;
	    m_Content = Double.NaN;
	  }
	  else {
	    m_Formula = null;
	    m_Object  = null;
	    setContentAsString(value);
	  }
	}
	else {
	  m_Formula = null;
	  m_Object  = null;
	  parseContent(value);
	}
      }
      else {
	m_Formula = null;
	m_Object  = null;
	setContentAsString(value);
      }
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
  public Object parseContent(String value, ContentType type) {
    switch (type) {
      case BOOLEAN:
	if (checkBoolean(value))
	  return Boolean.parseBoolean(value);
	else
	  return SpreadSheet.MISSING_VALUE;
      case LONG:
	if (checkLong(value))
	  return new Long(value);
	else
	  return SpreadSheet.MISSING_VALUE;
      case DOUBLE:
	if (checkDouble(value))
	  return new Double(value);
	else
	  return SpreadSheet.MISSING_VALUE;
      case STRING:
	return value;
      case TIME:
	if (checkTime(value))
	  return new Time(getSpreadSheet().getTimeFormat().parse(value));
	else
	  return SpreadSheet.MISSING_VALUE;
      case DATE:
	if (checkDate(value))
	  return getSpreadSheet().getDateFormat().parse(value);
	else
	  return SpreadSheet.MISSING_VALUE;
      case DATETIME:
	if (checkDateTime(value))
	  return new DateTime(getSpreadSheet().getDateFormat().parse(value));
	else
	  return SpreadSheet.MISSING_VALUE;
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
  public Cell setContentAsString(String value) {
    if ((value == null) || (value.equals(SpreadSheet.MISSING_VALUE))) {
      setMissing();
    }
    else {
      if (value.equals("'" + SpreadSheet.MISSING_VALUE + "'"))
	value = SpreadSheet.MISSING_VALUE;
      m_Content     = getSpreadSheet().getSharedStringsTable().getIndex(value);
      m_Object      = null;
      m_ContentType = ContentType.STRING;
    }
    return this;
  }

  /**
   * Sets the object content of the cell.
   *
   * @param value	the content
   * @return		the cell itself
   */
  public Cell setObject(Object value) {
    if (value == null) {
      setMissing();
    }
    else {
      m_Content     = Double.NaN;
      m_Object      = value;
      m_ContentType = ContentType.OBJECT;
    }
    return this;
  }

  /**
   * Sets the formula content of the cell.
   *
   * @param value	the content
   * @return		the cell itself
   */
  public Cell setFormula(String value) {
    if ((value == null) || (value.equals(SpreadSheet.MISSING_VALUE))) {
      setMissing();
    }
    else {
      if (!value.startsWith("=")) {
	setContentAsString(value);
      }
      else {
	m_Formula = value;
	m_Object  = null;
	m_Content = Double.NaN;
      }
    }
    return this;
  }

  /**
   * Returns the formula.
   *
   * @return		the formula, null if none used
   */
  public String getFormula() {
    return m_Formula;
  }

  /**
   * Returns the object.
   *
   * @return		the object, null if none set
   */
  public Object getObject() {
    return m_Object;
  }

  /**
   * Returns the content of the cell.
   *
   * @return		the content
   */
  public String getContent() {
    AbstractObjectHandler	handler;

    calculateIfRequired();
    switch (m_ContentType) {
      case MISSING:
	return SpreadSheet.MISSING_VALUE;
      case TIME:
	return getSpreadSheet().getTimeFormat().format(new Time((long) m_Content));
      case DATE:
	return getSpreadSheet().getDateFormat().format(new Date((long) m_Content));
      case DATETIME:
	return getSpreadSheet().getDateTimeFormat().format(new Date((long) m_Content));
      case STRING:
	return getSpreadSheet().getSharedStringsTable().getString((int) m_Content);
      case LONG:
	return Long.toString((long) m_Content);
      case BOOLEAN:
	return toBoolean().toString();
      case OBJECT:
	if (m_Object == null)
	  return SpreadSheet.MISSING_VALUE;
	handler = AbstractObjectHandler.getHandler(m_Object);
	if (handler == null)
	  return SpreadSheet.MISSING_VALUE;
	return handler.format(m_Object);
      default:
	return Double.toString(m_Content);
    }
  }

  /**
   * Returns the content type.
   *
   * @return		the type
   */
  public ContentType getContentType() {
    calculateIfRequired();
    return m_ContentType;
  }

  /**
   * Determines the best set-method based on the class of the provided object.
   * Fallback is the {@link #setObject(Object)} method.
   *
   * @param value	the value to set
   * @return		the cell itself
   */
  public Cell setNative(Object value) {
    if (value == null)
      setMissing();
    else if (value instanceof String)
      setContentAsString((String) value);
    else if (value instanceof Long)
      setContent((Long) value);
    else if (value instanceof Double)
      setContent((Double) value);
    else if (value instanceof DateTime)
      setContent((DateTime) value);
    else if (value instanceof Time)
      setContent((Time) value);
    else if (value instanceof Date)
      setContent((Date) value);
    else if (value instanceof Boolean)
      setContent((Boolean) value);
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
  public Object getNative() {
    switch (m_ContentType) {
      case MISSING:
	return SpreadSheet.MISSING_VALUE;
      case BOOLEAN:
	return toBoolean();
      case TIME:
	return toTime();
      case DATE:
	return toDate();
      case DATETIME:
	return toDateTime();
      case LONG:
	return toLong();
      case DOUBLE:
	return toDouble();
      case STRING:
	return toString();
      case OBJECT:
	return getObject();
      default:
	throw new IllegalStateException("Unhandled content type: " + m_ContentType);
    }
  }

  /**
   * Returns the column this cell is in.
   *
   * @return		the column index, -1 if not available
   */
  public int index() {
    return getOwner().indexOf(this);
  }

  /**
   * Checks whether the stored string is numeric.
   *
   * @return		true if the content is numeric
   */
  public boolean isNumeric() {
    calculateIfRequired();
    return
	   (m_ContentType == ContentType.LONG)
	|| (m_ContentType == ContentType.DOUBLE);
  }

  /**
   * Checks whether the cell contains a missing value.
   *
   * @return		true if missing value
   */
  public boolean isMissing() {
    calculateIfRequired();
    return (m_ContentType == ContentType.MISSING);
  }

  /**
   * Checks whether the cell represents a boolean value.
   *
   * @return		true if boolean value
   */
  public boolean isBoolean() {
    calculateIfRequired();
    return (m_ContentType == ContentType.BOOLEAN);
  }

  /**
   * Returns the boolean content, null if not a boolean.
   *
   * @return		the date, null if not boolean
   */
  public Boolean toBoolean() {
    calculateIfRequired();
    if (m_ContentType == ContentType.BOOLEAN)
      return new Boolean(m_Content == 1.0);
    else
      return null;
  }

  /**
   * Checks whether the cell represents a date, time or date/time value.
   *
   * @return		true if date, time or date/time value
   */
  public boolean isAnyDateType() {
    calculateIfRequired();
    return (m_ContentType == ContentType.TIME) || (m_ContentType == ContentType.DATE) || (m_ContentType == ContentType.DATETIME);
  }

  /**
   * Returns the date content, null if not a date, time or date/time.
   *
   * @return		the date, null if not date, time or date/time
   */
  public Date toAnyDateType() {
    if (isTime())
      return toTime();
    else if (isDate())
      return toDate();
    else if (isDateTime())
      return toDateTime();
    else
      return null;
  }

  /**
   * Checks whether the cell represents a date value.
   *
   * @return		true if date value
   */
  public boolean isDate() {
    calculateIfRequired();
    return (m_ContentType == ContentType.DATE);
  }

  /**
   * Returns the date content, null if not a date.
   *
   * @return		the date, null if not date
   */
  public Date toDate() {
    calculateIfRequired();
    if (m_ContentType == ContentType.DATE)
      return new Date((long) m_Content);
    else
      return null;
  }

  /**
   * Checks whether the cell represents a date/time value.
   *
   * @return		true if date/time value
   */
  public boolean isDateTime() {
    calculateIfRequired();
    return (m_ContentType == ContentType.DATETIME);
  }

  /**
   * Returns the date/time content, null if not a date/time.
   *
   * @return		the date/time, null if not date/time
   */
  public DateTime toDateTime() {
    calculateIfRequired();
    if (m_ContentType == ContentType.DATETIME)
      return new DateTime((long) m_Content);
    else
      return null;
  }

  /**
   * Checks whether the cell represents a time value.
   *
   * @return		true if time value
   */
  public boolean isTime() {
    calculateIfRequired();
    return (m_ContentType == ContentType.TIME);
  }

  /**
   * Returns the time content, null if not a time.
   *
   * @return		the time, null if not time
   */
  public Time toTime() {
    calculateIfRequired();
    if (m_ContentType == ContentType.TIME)
      return new Time((long) m_Content);
    else
      return null;
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

  /**
   * Returns whether the content represents a double number.
   *
   * @return		true if a double
   */
  public boolean isDouble() {
    calculateIfRequired();
    if (m_ContentType == ContentType.DOUBLE)
      return true;
    else if (m_ContentType == ContentType.STRING)
      return checkDouble(getContent());
    else
      return false;
  }

  /**
   * Returns the content as double, if possible.
   *
   * @return		the content as double, if representing a number,
   * 			otherwise null
   */
  public Double toDouble() {
    calculateIfRequired();
    if (m_ContentType == ContentType.DOUBLE) {
      return m_Content;
    }
    else if (m_ContentType == ContentType.LONG) {
      return new Double((long) m_Content);
    }
    else if (m_ContentType == ContentType.STRING) {
      try {
	return Utils.toDouble(getContent(), getSpreadSheet().getLocale());
      }
      catch (Exception e) {
	return null;
      }
    }
    else {
      return null;
    }
  }

  /**
   * Returns whether the content represents a long number.
   *
   * @return		true if a long
   */
  public boolean isLong() {
    calculateIfRequired();
    if (m_ContentType == ContentType.LONG)
      return true;
    else if (m_ContentType == ContentType.STRING)
      return Utils.isLong(getContent());
    else
      return false;
  }

  /**
   * Returns the content as long, if possible. First, a Double object is
   * created and then the longValue() method called to return the value.
   *
   * @return		the content as long, if representing a number,
   * 			otherwise null
   */
  public Long toLong() {
    calculateIfRequired();
    if (m_ContentType == ContentType.LONG)
      return (long) m_Content;
    else if (m_ContentType == ContentType.STRING)
      return new Double(getContent()).longValue();
    return null;
  }

  /**
   * Returns whether the content represents a formula.
   *
   * @return		true if a formula
   */
  public boolean isFormula() {
    return (m_Formula != null);
  }

  /**
   * Returns whether the content represents an object.
   *
   * @return		true if an object
   */
  public boolean isObject() {
    return (m_Object != null);
  }

  /**
   * Recalculates the value from the cell's formula.
   */
  public void calculate() {
    Object	eval;

    if (!isFormula())
      return;

    // parse formula
    eval = null;
    try {
      eval = SpreadSheetFormula.evaluate(getFormula(), new HashMap(), getSpreadSheet());
      if ((eval instanceof Double) && (Double.isNaN((Double) eval)))
	eval = null;
    }
    catch (Throwable t) {
      System.err.println("Failed to parse formula: " + getFormula());
      t.printStackTrace();
      eval = FORMULA_ERROR;
    }

    if (eval != null) {
      if (eval instanceof Double)
	setContent((Double) eval);
      else if (eval instanceof Boolean)
	setContent((Boolean) eval);
      else
	parseContent("" + eval);
    }
  }

  /**
   * Calculates the cell value if necessary.
   */
  protected void calculateIfRequired() {
    if (isFormula() && Double.isNaN(m_Content))
      calculate();
  }
}