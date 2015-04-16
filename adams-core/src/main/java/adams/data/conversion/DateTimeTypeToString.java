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
 * DateTimeTypeToString.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.DateTime;
import adams.core.DateTimeType;
import adams.core.DateUtils;
import adams.core.Time;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseTime;
import adams.data.DateFormatString;
import jodd.datetime.JDateTime;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Turns instances of the the specified date&#47;time type into a string using the specified format.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-datetime-type &lt;MSECS|SECONDS|DATE|DATETIME|TIME|BASEDATE|BASEDATETIME|BASETIME|JULIANDATE&gt; (property: dateTimeType)
 * &nbsp;&nbsp;&nbsp;The date&#47;time type to convert into a string.
 * &nbsp;&nbsp;&nbsp;default: DATE
 * </pre>
 * 
 * <pre>-format &lt;adams.data.DateFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format for turning the date&#47;time type into a string.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DateTimeTypeToString
  extends AbstractConversionToString {

  /** for serialization. */
  private static final long serialVersionUID = -2353313471489671117L;

  /** the datetime type to convert. */
  protected DateTimeType m_DateTimeType;
  
  /** the format string to use. */
  protected DateFormatString m_Format;

  /** the formatter. */
  protected transient DateFormat m_Formatter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns instances of the the specified date/time type into a string using the specified format.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "datetime-type", "dateTimeType",
	    DateTimeType.DATE);

    m_OptionManager.add(
	    "format", "format",
	    new DateFormatString(Constants.DATE_FORMAT));
  }

  /**
   * Resets the conversion.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Formatter = null;
  }
  
  /**
   * Sets the date/time type to convert.
   *
   * @param value	the type
   */
  public void setDateTimeType(DateTimeType value) {
    m_DateTimeType = value;
    reset();
  }

  /**
   * Returns the date/time type to convert.
   *
   * @return		the type
   */
  public DateTimeType getDateTimeType() {
    return m_DateTimeType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dateTimeTypeTipText() {
    return "The date/time type to convert into a string.";
  }

  /**
   * Sets the format to use for the conversion.
   *
   * @param value	the format
   */
  public void setFormat(DateFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format in use for the conversion.
   *
   * @return		the format
   */
  public DateFormatString getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format for turning the date/time type into a string.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    switch (m_DateTimeType) {
      case MSECS:
	return Double.class;
      case SECONDS:
	return Double.class;
      case DATE:
	return Date.class;
      case DATETIME:
	return DateTime.class;
      case TIME:
	return Time.class;
      case BASEDATE:
	return BaseDate.class;
      case BASEDATETIME:
	return BaseDateTime.class;
      case BASETIME:
	return BaseTime.class;
      case JULIANDATE:
	return Double.class;
      case SERIAL_DATETIME:
        return Double.class;
      default:
	throw new IllegalStateException("Unhandled data/time type: " + m_DateTimeType);
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    if (m_Formatter == null)
      m_Formatter = m_Format.toDateFormat();
    
    switch (m_DateTimeType) {
      case MSECS:
	return m_Formatter.format(new Date(((Double) m_Input).longValue()));
      case SECONDS:
	return m_Formatter.format(new Date(((Double) m_Input).longValue() * 1000));
      case DATE:
	return m_Formatter.format((Date) m_Input);
      case DATETIME:
	return m_Formatter.format((DateTime) m_Input);
      case TIME:
	return m_Formatter.format((Time) m_Input);
      case BASEDATE:
	return m_Formatter.format(((BaseDate) m_Input).dateValue());
      case BASEDATETIME:
	return m_Formatter.format(((BaseDateTime) m_Input).dateValue());
      case BASETIME:
	return m_Formatter.format(((BaseTime) m_Input).dateValue());
      case JULIANDATE:
	return m_Formatter.format(new JDateTime((Double) m_Input).convertToDate());
      case SERIAL_DATETIME:
        return m_Formatter.format(new Date(DateUtils.serialDateToMsec((Double) m_Input)));
      default:
	throw new IllegalStateException("Unhandled data/time type: " + m_DateTimeType);
    }
  }
}
