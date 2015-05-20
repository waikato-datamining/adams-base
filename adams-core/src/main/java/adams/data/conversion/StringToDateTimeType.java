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
 * StringToDateTimeType.java
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
 * Parses a string and turns it into an instance of the the specified date&#47;time type. The string may contain variables, which get expanded at conversion time.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-format &lt;adams.data.DateFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format for parsing the date&#47;time string.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 * <pre>-datetime-type &lt;MSECS|SECONDS|DATE|DATETIME|TIME|BASEDATE|BASEDATETIME|BASETIME|JULIANDATE&gt; (property: dateTimeType)
 * &nbsp;&nbsp;&nbsp;The date&#47;time type to generate from the string.
 * &nbsp;&nbsp;&nbsp;default: DATE
 * </pre>
 * 
 * <pre>-use-base-parsing &lt;boolean&gt; (property: useBaseParsing)
 * &nbsp;&nbsp;&nbsp;If enabled, uses the parsing capability of the BaseTime, BaseDate, BaseDateTime 
 * &nbsp;&nbsp;&nbsp;classes to parse the string rather than the specified format string (string 
 * &nbsp;&nbsp;&nbsp;must be in the appropriate format).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringToDateTimeType
  extends AbstractConversionFromString {

  /** for serialization. */
  private static final long serialVersionUID = -2353313471489671117L;

  /** the datetime type to convert. */
  protected DateTimeType m_DateTimeType;
  
  /** the format string to use. */
  protected DateFormatString m_Format;

  /** whether to use the base* class's parsing. */
  protected boolean m_UseBaseParsing;
  
  /** the formatter. */
  protected transient DateFormat m_Formatter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Parses a string and turns it into an instance of the the specified "
	+ "date/time type. The string may contain variables, which get expanded "
	+ "at conversion time.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "format", "format",
	    new DateFormatString(Constants.DATE_FORMAT));

    m_OptionManager.add(
	    "datetime-type", "dateTimeType",
	    DateTimeType.DATE);

    m_OptionManager.add(
	    "use-base-parsing", "useBaseParsing",
	    false);
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
   * Sets the format for parsing the date/time string.
   *
   * @param value	the format
   */
  public void setFormat(DateFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format for parsing the date/time string.
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
    return "The format for parsing the date/time string.";
  }
  
  /**
   * Sets the date/time type to generate.
   *
   * @param value	the type
   */
  public void setDateTimeType(DateTimeType value) {
    m_DateTimeType = value;
    reset();
  }

  /**
   * Returns the date/time type to generate.
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
    return "The date/time type to generate from the string.";
  }
  
  /**
   * Sets the date/time type to generate.
   *
   * @param value	the type
   */
  public void setUseBaseParsing(boolean value) {
    m_UseBaseParsing = value;
    reset();
  }

  /**
   * Returns the date/time type to generate.
   *
   * @return		the type
   */
  public boolean getUseBaseParsing() {
    return m_UseBaseParsing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useBaseParsingTipText() {
    return 
	"If enabled, uses the parsing capability of the BaseTime, BaseDate, "
	+ "BaseDateTime classes to parse the string rather than the specified "
	+ "format string (string must be in the appropriate format).";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
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
    Date	date;
    String	input;

    try {
      input = (String) m_Input;
      input = getOptionManager().getVariables().expand(input);
      date  = null;

      if (m_UseBaseParsing) {
	switch (m_DateTimeType) {
	  case BASEDATE:
	    date = new BaseDate(input).dateValue();
	    break;
	  case BASEDATETIME:
	    date = new BaseDateTime(input).dateValue();
	    break;
	  case BASETIME:
	    date = new BaseTime(input).dateValue();
	    break;
	  default:
	    date = null;
	}
      }

      if (date == null) {
	if (m_Formatter == null)
	  m_Formatter = m_Format.toDateFormat();
	date = m_Formatter.parse(input);
      }

      switch (m_DateTimeType) {
	case MSECS:
	  return new Double(date.getTime());
	case SECONDS:
	  return new Double(date.getTime() / 1000);
	case DATE:
	  return date;
	case DATETIME:
	  return new DateTime(date);
	case TIME:
	  return new Time(date);
	case BASEDATE:
	  return new BaseDate(date);
	case BASEDATETIME:
	  return new BaseDateTime(date);
	case BASETIME:
	  return new BaseTime(date);
	case JULIANDATE:
	  return new JDateTime(date).getJulianDateDouble();
        case SERIAL_DATETIME:
          return new Double(DateUtils.msecToSerialDate(date.getTime()));
	default:
	  throw new IllegalStateException("Unhandled data/time type: " + m_DateTimeType);
      }
    }
    catch (Exception e) {
      throw new Exception("Failed to parse: " + m_Input, e);
    }
  }
}
