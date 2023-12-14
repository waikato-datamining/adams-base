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
 * DateTimeTypeToString.java
 * Copyright (C) 2013-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.DateTime;
import adams.core.DateTimeMsec;
import adams.core.DateTimeType;
import adams.core.DateUtils;
import adams.core.Time;
import adams.core.TimeMsec;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseDateTimeMsec;
import adams.core.base.BaseTime;
import adams.core.base.BaseTimeMsec;
import adams.data.DateFormatString;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Turns instances of the specified date&#47;time type into a string using the specified format.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-datetime-type &lt;MSECS|MSECS_LONG|SECONDS|SECONDS_LONG|DATE|DATETIME|DATETIMEMSEC|TIME|TIMEMSEC|BASEDATE|BASEDATETIME|BASEDATETIMEMSEC|BASETIME|BASETIMEMSEC|SERIAL_DATETIME|SERIAL_DATETIME_LONG&gt; (property: dateTimeType)
 * &nbsp;&nbsp;&nbsp;The date&#47;time type to convert into a string.
 * &nbsp;&nbsp;&nbsp;default: DATE
 * </pre>
 *
 * <pre>-format &lt;adams.data.DateFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format for turning the date&#47;time type into a string.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
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
    return "Turns instances of the specified date/time type into a string using the specified format.";
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
      case SECONDS:
      case SERIAL_DATETIME:
	return Double.class;
      case MSECS_LONG:
      case SECONDS_LONG:
      case SERIAL_DATETIME_LONG:
	return Long.class;
      case DATE:
	return Date.class;
      case DATETIME:
	return DateTime.class;
      case DATETIMEMSEC:
	return DateTimeMsec.class;
      case TIME:
	return Time.class;
      case TIMEMSEC:
	return TimeMsec.class;
      case BASEDATE:
	return BaseDate.class;
      case BASEDATETIME:
	return BaseDateTime.class;
      case BASEDATETIMEMSEC:
	return BaseDateTimeMsec.class;
      case BASETIME:
	return BaseTime.class;
      case BASETIMEMSEC:
	return BaseTimeMsec.class;
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
      case MSECS_LONG:
	return m_Formatter.format(new Date((Long) m_Input));
      case SECONDS:
	return m_Formatter.format(new Date(((Double) m_Input).longValue() * 1000));
      case SECONDS_LONG:
	return m_Formatter.format(new Date(((Long) m_Input) * 1000));
      case DATE:
	return m_Formatter.format((Date) m_Input);
      case DATETIME:
	return m_Formatter.format((DateTime) m_Input);
      case DATETIMEMSEC:
	return m_Formatter.format((DateTimeMsec) m_Input);
      case TIME:
	return m_Formatter.format((Time) m_Input);
      case TIMEMSEC:
	return m_Formatter.format((TimeMsec) m_Input);
      case BASEDATE:
	return m_Formatter.format(((BaseDate) m_Input).dateValue());
      case BASEDATETIME:
	return m_Formatter.format(((BaseDateTime) m_Input).dateValue());
      case BASEDATETIMEMSEC:
	return m_Formatter.format(((BaseDateTimeMsec) m_Input).dateValue());
      case BASETIME:
	return m_Formatter.format(((BaseTime) m_Input).dateValue());
      case BASETIMEMSEC:
	return m_Formatter.format(((BaseTimeMsec) m_Input).dateValue());
      case SERIAL_DATETIME:
	return m_Formatter.format(new Date(DateUtils.serialDateToMsec((Double) m_Input)));
      case SERIAL_DATETIME_LONG:
	return m_Formatter.format(new Date(DateUtils.serialDateToMsec((Long) m_Input)));
      default:
	throw new IllegalStateException("Unhandled data/time type: " + m_DateTimeType);
    }
  }
}
