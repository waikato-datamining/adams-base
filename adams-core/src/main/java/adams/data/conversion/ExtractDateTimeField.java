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
 * ExtractDateTimeField.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.DateTime;
import adams.core.DateTimeType;
import adams.core.Time;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseTime;
import adams.data.DateFormatString;

/**
 <!-- globalinfo-start -->
 * Extracts the specified field from a date&#47;time type.<br/>
 * A custom format string can be used with field CUSTOM.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-datetime-type &lt;MSECS|DATE|DATETIME|TIME|BASEDATE|BASEDATETIME|BASETIME&gt; (property: dateTimeType)
 * &nbsp;&nbsp;&nbsp;The date&#47;time type to extract the field from.
 * &nbsp;&nbsp;&nbsp;default: DATE
 * </pre>
 * 
 * <pre>-field &lt;YEAR|MONTH|DAY|HOUR|MINUTE|SECOND|MSEC|DAY_OF_YEAR|DAY_OF_MONTH|DAY_OF_WEEK|DAY_OF_WEEK_STR_EN|DAY_OF_WEEK_STR_LOCALE|WEEK_OF_YEAR|WEEK_OF_MONTH|CUSTOM&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The field to extract.
 * &nbsp;&nbsp;&nbsp;default: YEAR
 * </pre>
 * 
 * <pre>-format-custom &lt;adams.data.DateFormatString&gt; (property: formatCustom)
 * &nbsp;&nbsp;&nbsp;The format for turning the date&#47;time type into a string in case of field 
 * &nbsp;&nbsp;&nbsp;CUSTOM
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExtractDateTimeField
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 6801095206203068066L;

  /**
   * The field to extract.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum DateTimeField {
    /** year. */
    YEAR,
    /** month. */
    MONTH,
    /** day. */
    DAY,
    /** hour. */
    HOUR,
    /** minute. */
    MINUTE,
    /** second. */
    SECOND,
    /** millisecond. */
    MSEC,
    /** day of year. */
    DAY_OF_YEAR,
    /** day of month. */
    DAY_OF_MONTH,
    /** day of week (starts sunday). */
    DAY_OF_WEEK,
    /** day of week string (english). */
    DAY_OF_WEEK_STR_EN,
    /** day of week string (localized). */
    DAY_OF_WEEK_STR_LOCALE,
    /** the week of the year. */
    WEEK_OF_YEAR,
    /** the week of the month. */
    WEEK_OF_MONTH,
    /** other, defined via format string. */
    CUSTOM
  }

  /** the sunday constant. */
  public final static String SUNDAY = "Sunday";

  /** the monday constant. */
  public final static String MONDAY = "Monday";

  /** the tuesday constant. */
  public final static String TUESDAY = "Tuesday";

  /** the wednesday constant. */
  public final static String WEDNESDAY = "Wednesday";

  /** the thursday constant. */
  public final static String THURSDAY = "Thursday";

  /** the friday constant. */
  public final static String FRIDAY = "Friday";

  /** the saturday constant. */
  public final static String SATURDAY = "Saturday";
  
  /** the datetime type to process. */
  protected DateTimeType m_DateTimeType;

  /** the field to extract. */
  protected DateTimeField m_Field;
  
  /** the format string to use in case of {@link DateTimeField#CUSTOM}. */
  protected DateFormatString m_FormatCustom;

  /** the formatter for the custom format. */
  protected transient DateFormat m_FormatterCustom;

  /** the formatter for the localized day of week. */
  protected transient DateFormat m_FormatterDayOfWeek;
  
  /** the calendar for extracting the fields. */
  protected Calendar m_Calendar;
  
  /**
   * @return
   */
  @Override
  public String globalInfo() {
    return 
	"Extracts the specified field from a date/time type.\n"
	+ "A custom format string can be used with field " + DateTimeField.CUSTOM + ".";
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
	    "field", "field",
	    DateTimeField.YEAR);

    m_OptionManager.add(
	    "format-custom", "formatCustom",
	    new DateFormatString(Constants.DATE_FORMAT));
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Calendar           = new GregorianCalendar();
    m_FormatterDayOfWeek = new DateFormat("E");
  }
  
  /**
   * Resets the converter.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_FormatterCustom = null;
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
    return "The date/time type to extract the field from.";
  }
  
  /**
   * Sets the field to extract.
   *
   * @param value	the field
   */
  public void setField(DateTimeField value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to extract
   *
   * @return		the field
   */
  public DateTimeField getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The field to extract.";
  }

  /**
   * Sets the custom format to use for the conversion.
   *
   * @param value	the format
   */
  public void setFormatCustom(DateFormatString value) {
    m_FormatCustom = value;
    reset();
  }

  /**
   * Returns the custom format in use for the conversion.
   *
   * @return		the format
   */
  public DateFormatString getFormatCustom() {
    return m_FormatCustom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatCustomTipText() {
    return "The format for turning the date/time type into a string in case of field " + DateTimeField.CUSTOM;
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
      default:
	throw new IllegalStateException("Unhandled data/time type: " + m_DateTimeType);
    }
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    switch (m_Field) {
      case YEAR:
      case MONTH:
      case DAY:
      case HOUR:
      case MINUTE:
      case SECOND:
      case MSEC:
      case WEEK_OF_YEAR:
      case WEEK_OF_MONTH:
      case DAY_OF_YEAR:
      case DAY_OF_WEEK:
      case DAY_OF_MONTH:
	return Integer.class;
      case DAY_OF_WEEK_STR_EN:
      case DAY_OF_WEEK_STR_LOCALE:
      case CUSTOM:
	return String.class;
      default:
	throw new IllegalStateException("Unhandled field: " + m_Field);
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
    int		day;
    
    // update the calendar
    switch (m_DateTimeType) {
      case MSECS:
	m_Calendar.setTime(new Date(((Double) m_Input).longValue()));
	break;
      case SECONDS:
	m_Calendar.setTime(new Date(((Double) m_Input).longValue() * 1000));
	break;
      case DATE:
	m_Calendar.setTime((Date) m_Input);
	break;
      case DATETIME:
	m_Calendar.setTime((DateTime) m_Input);
	break;
      case TIME:
	m_Calendar.setTime((Time) m_Input);
	break;
      case BASEDATE:
	m_Calendar.setTime(((BaseDate) m_Input).dateValue());
	break;
      case BASEDATETIME:
	m_Calendar.setTime(((BaseDateTime) m_Input).dateValue());
	break;
      case BASETIME:
	m_Calendar.setTime(((BaseTime) m_Input).dateValue());
	break;
      default:
	throw new IllegalStateException("Unhandled data/time type: " + m_DateTimeType);
    }
    
    // extract the field
    switch (m_Field) {
      case YEAR:
	return m_Calendar.get(Calendar.YEAR);
      case MONTH:
	return m_Calendar.get(Calendar.MONTH) + 1;
      case DAY:
	return m_Calendar.get(Calendar.DAY_OF_YEAR);
      case HOUR:
	return m_Calendar.get(Calendar.HOUR);
      case MINUTE:
	return m_Calendar.get(Calendar.MINUTE);
      case SECOND:
	return m_Calendar.get(Calendar.SECOND);
      case MSEC:
	return m_Calendar.get(Calendar.MILLISECOND);
      case WEEK_OF_YEAR:
	return m_Calendar.get(Calendar.WEEK_OF_YEAR);
      case WEEK_OF_MONTH:
	return m_Calendar.get(Calendar.WEEK_OF_MONTH);
      case DAY_OF_YEAR:
	return m_Calendar.get(Calendar.DAY_OF_YEAR);
      case DAY_OF_MONTH:
	return m_Calendar.get(Calendar.DAY_OF_MONTH);
      case DAY_OF_WEEK:
	return m_Calendar.get(Calendar.DAY_OF_WEEK);
      case DAY_OF_WEEK_STR_EN:
	day = m_Calendar.get(Calendar.DAY_OF_WEEK);
	switch (day) {
	  case Calendar.SUNDAY:
	    return SUNDAY;
	  case Calendar.MONDAY:
	    return MONDAY;
	  case Calendar.TUESDAY:
	    return TUESDAY;
	  case Calendar.WEDNESDAY:
	    return WEDNESDAY;
	  case Calendar.THURSDAY:
	    return THURSDAY;
	  case Calendar.FRIDAY:
	    return FRIDAY;
	  case Calendar.SATURDAY:
	    return SATURDAY;
	}
	return null;
      case DAY_OF_WEEK_STR_LOCALE:
	return m_FormatterDayOfWeek.format(m_Calendar.getTime());
      case CUSTOM:
	if (m_FormatterCustom == null)
	  m_FormatterCustom = m_FormatCustom.toDateFormat();
	return m_FormatterCustom.format(m_Calendar.getTime());
      default:
	throw new IllegalStateException("Unhandled field: " + m_Field);
    }
  }
}
