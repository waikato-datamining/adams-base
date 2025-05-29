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
 * ConvertDateTimeType.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.DateTime;
import adams.core.DateTimeMsec;
import adams.core.DateTimeType;
import adams.core.DateUtils;
import adams.core.QuickInfoHelper;
import adams.core.Time;
import adams.core.TimeMsec;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseDateTimeMsec;
import adams.core.base.BaseTime;
import adams.core.base.BaseTimeMsec;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Turns instances of the specified input date&#47;time type into instances of the specified output date&#47;time type.
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
 * <pre>-input-datetime-type &lt;MSECS|MSECS_LONG|SECONDS|SECONDS_LONG|DATE|DATETIME|DATETIMEMSEC|TIME|TIMEMSEC|BASEDATE|BASEDATETIME|BASEDATETIMEMSEC|BASETIME|BASETIMEMSEC|SERIAL_DATETIME|SERIAL_DATETIME_LONG&gt; (property: inputDateTimeType)
 * &nbsp;&nbsp;&nbsp;The date&#47;time type of the input data.
 * &nbsp;&nbsp;&nbsp;default: DATE
 * </pre>
 *
 * <pre>-output-datetime-type &lt;MSECS|MSECS_LONG|SECONDS|SECONDS_LONG|DATE|DATETIME|DATETIMEMSEC|TIME|TIMEMSEC|BASEDATE|BASEDATETIME|BASEDATETIMEMSEC|BASETIME|BASETIMEMSEC|SERIAL_DATETIME|SERIAL_DATETIME_LONG&gt; (property: outputDateTimeType)
 * &nbsp;&nbsp;&nbsp;The date&#47;time type of the output data.
 * &nbsp;&nbsp;&nbsp;default: MSECS
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ConvertDateTimeType
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -2353313471489671117L;

  /** the input datetime type. */
  protected DateTimeType m_InputDateTimeType;

  /** the output datetime type. */
  protected DateTimeType m_OutputDateTimeType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns instances of the specified input date/time type into instances of the specified output date/time type.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "input-datetime-type", "inputDateTimeType",
      DateTimeType.DATE);

    m_OptionManager.add(
      "output-datetime-type", "outputDateTimeType",
      DateTimeType.MSECS);
  }

  /**
   * Sets the input date/time type.
   *
   * @param value	the type
   */
  public void setInputDateTimeType(DateTimeType value) {
    m_InputDateTimeType = value;
    reset();
  }

  /**
   * Returns the input date/time type.
   *
   * @return		the type
   */
  public DateTimeType getInputDateTimeType() {
    return m_InputDateTimeType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputDateTimeTypeTipText() {
    return "The date/time type of the input data.";
  }

  /**
   * Sets the output date/time type.
   *
   * @param value	the type
   */
  public void setOutputDateTimeType(DateTimeType value) {
    m_OutputDateTimeType = value;
    reset();
  }

  /**
   * Returns the output date/time type.
   *
   * @return		the type
   */
  public DateTimeType getOutputDateTimeType() {
    return m_OutputDateTimeType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDateTimeTypeTipText() {
    return "The date/time type of the output data.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "inputDateType", m_InputDateTimeType);
    result += " -> ";
    result += QuickInfoHelper.toString(this, "outputDateType", m_OutputDateTimeType);

    return result;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    switch (m_InputDateTimeType) {
      case NANOSECS:
      case MSECS:
      case SECONDS:
      case SERIAL_DATETIME:
	return Double.class;
      case NANOSECS_LONG:
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
	throw new IllegalStateException("Unhandled input data/time type: " + m_InputDateTimeType);
    }
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    switch (m_OutputDateTimeType) {
      case NANOSECS:
      case MSECS:
      case SECONDS:
      case SERIAL_DATETIME:
	return Double.class;
      case NANOSECS_LONG:
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
	throw new IllegalStateException("Unhandled output data/time type: " + m_OutputDateTimeType);
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
    long 	nanosecs;

    // no conversion necessary?
    if (m_InputDateTimeType == m_OutputDateTimeType)
      return m_Input;

    switch (m_InputDateTimeType) {
      case NANOSECS:
	nanosecs = ((Double) m_Input).longValue();
	break;
      case NANOSECS_LONG:
	nanosecs = (Long) m_Input;
	break;
      case MSECS:
	nanosecs = ((Double) m_Input).longValue() * 1000;
	break;
      case MSECS_LONG:
	nanosecs = (Long) m_Input * 1000;
	break;
      case SECONDS:
	nanosecs = ((Double) m_Input).longValue() * 1000000;
	break;
      case SECONDS_LONG:
	nanosecs = ((Long) m_Input) * 1000000;
	break;
      case DATE:
	nanosecs = ((Date) m_Input).getTime() * 1000;
	break;
      case DATETIME:
	nanosecs = ((DateTime) m_Input).getTime() * 1000;
	break;
      case DATETIMEMSEC:
	nanosecs = ((DateTimeMsec) m_Input).getTime() * 1000;
	break;
      case TIME:
	nanosecs = ((Time) m_Input).getTime() * 1000;
	break;
      case TIMEMSEC:
	nanosecs = ((TimeMsec) m_Input).getTime() * 1000;
	break;
      case BASEDATE:
	nanosecs = ((BaseDate) m_Input).dateValue().getTime() * 1000;
	break;
      case BASEDATETIME:
	nanosecs = ((BaseDateTime) m_Input).dateValue().getTime() * 1000;
	break;
      case BASEDATETIMEMSEC:
	nanosecs = ((BaseDateTimeMsec) m_Input).dateValue().getTime() * 1000;
	break;
      case BASETIME:
	nanosecs = ((BaseTime) m_Input).dateValue().getTime() * 1000;
	break;
      case BASETIMEMSEC:
	nanosecs = ((BaseTimeMsec) m_Input).dateValue().getTime() * 1000;
	break;
      case SERIAL_DATETIME:
	nanosecs = DateUtils.serialDateToMsec((Double) m_Input) * 1000;
	break;
      case SERIAL_DATETIME_LONG:
	nanosecs = DateUtils.serialDateToMsec((Long) m_Input) * 1000;
	break;
      default:
	throw new IllegalStateException("Unhandled input data/time type: " + m_InputDateTimeType);
    }

    switch (m_OutputDateTimeType) {
      case NANOSECS:
	return (double) (nanosecs);
      case NANOSECS_LONG:
	return nanosecs;
      case MSECS:
	return (double) (nanosecs / 1000);
      case MSECS_LONG:
	return nanosecs / 1000;
      case SECONDS:
	return (double) (nanosecs / 1000000);
      case SECONDS_LONG:
	return (nanosecs / 1000000);
      case DATE:
	return new Date(nanosecs / 1000);
      case DATETIME:
	return new DateTime(nanosecs / 1000);
      case DATETIMEMSEC:
	return new DateTimeMsec(nanosecs / 1000);
      case TIME:
	return new Time(nanosecs / 1000);
      case TIMEMSEC:
	return new TimeMsec(nanosecs / 1000);
      case BASEDATE:
	return new BaseDate(new Date(nanosecs / 1000));
      case BASEDATETIME:
	return new BaseDateTime(new DateTime(nanosecs / 1000));
      case BASEDATETIMEMSEC:
	return new BaseDateTimeMsec(new DateTime(nanosecs / 1000));
      case BASETIME:
	return new BaseTime(new Time(nanosecs / 1000));
      case BASETIMEMSEC:
	return new BaseTimeMsec(new TimeMsec(nanosecs / 1000));
      case SERIAL_DATETIME:
	return DateUtils.msecToSerialDate(nanosecs / 1000);
      case SERIAL_DATETIME_LONG:
	return (long) DateUtils.msecToSerialDate(nanosecs / 1000);
      default:
	throw new IllegalStateException("Unhandled output data/time type: " + m_OutputDateTimeType);
    }
  }
}
