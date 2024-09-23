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
 * BaseTime.java
 * Copyright (C) 2010-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.DateValueSupporter;
import adams.core.Time;
import adams.core.logging.LoggingHelper;
import adams.parser.BaseTimeExpression;
import adams.parser.GrammarSupplier;

import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;

/**
 * Wrapper for a Time string to be editable in the GOE. Dates have to be of
 * format "HH:mm:ss".
 * <pre>
 * parses expressions as follows:
 *   (&lt;date&gt;|NOW|-INF|+INF|START|END) [(+&lt;int&gt;|-&lt;int&gt;) (SECOND|MINUTE|HOUR)]
 * Examples:
 *   01:02:03
 *   01:02:03 +1 MINUTE
 *   NOW
 *   +INF
 *   NOW +1 HOUR
 *   NOW +14 MINNUTE
 * Amounts can be chained as well:
 *   NOW -1 MINUTE +1 HOUR
 * START and END can only be set programmatically; by default they equal to -INF and +INF.
 * </pre>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see #FORMAT
 * @see #setStart(Date)
 * @see #setEnd(Date)
 */
public class BaseTime
  extends BaseObject
  implements GrammarSupplier, DateValueSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -5853830144343397434L;

  /** the placeholder for "-INF" (infinity in the past). */
  public final static String INF_PAST = "-INF";

  /** the "-INF" date. */
  public final static String INF_PAST_DATE = "00:00:00";

  /** the placeholder for "+INF" (infinity in the future). */
  public final static String INF_FUTURE = "+INF";

  /** the "+INF" date. */
  public final static String INF_FUTURE_DATE = "23:59:59";

  /** the placeholder for "now". */
  public final static String NOW = "NOW";

  /** the placeholder for "start". */
  public final static String START = "START";

  /** the placeholder for "end". */
  public final static String END = "END";

  /** the date format. */
  public final static String FORMAT = Constants.TIME_FORMAT;

  /** for formatting/parsing the dates. */
  protected static DateFormat m_Format;

  /** the start time to use. */
  protected Date m_Start = null;

  /** the end time to use. */
  protected Date m_End = null;

  /**
   * Initializes the date as NOW.
   *
   * @see	#NOW
   */
  public BaseTime() {
    this(NOW);
  }

  /**
   * Initializes the object with the date to parse.
   *
   * @param s		the date to parse
   */
  public BaseTime(String s) {
    super(s);
  }

  /**
   * Initializes the object with the specified date.
   *
   * @param date	the date to use
   */
  public BaseTime(Date date) {
    this(getFormat().format(date));
  }

  /**
   * Initializes the internal object.
   */
  @Override
  protected void initialize() {
    m_Internal = NOW;
  }

  /**
   * Returns the formatter.
   *
   * @return		the formatter
   */
  protected static synchronized DateFormat getFormat() {
    if (m_Format == null) {
      m_Format = new DateFormat(FORMAT);
      //m_Format.setTimeZone(TimeZone.getDefault());
    }
    return m_Format;
  }

  /**
   * Sets the optional start time.
   *
   * @param value 	the start time
   */
  public void setStart(Date value) {
    m_Start = value;
  }

  /**
   * Returns the optional start time.
   *
   * @return 		the start time
   */
  public Date getStart() {
    return m_Start;
  }

  /**
   * Sets the optional end time.
   *
   * @param value 	the end time
   */
  public void setEnd(Date value) {
    m_End = value;
  }

  /**
   * Returns the optional end time.
   *
   * @return 		the end time
   */
  public Date getEnd() {
    return m_End;
  }

  /**
   * Parses the given date string.
   *
   * @param s		the date string to parse
   * @param quiet	whether to print exceptions or not
   * @return		the parsed date, null in case of an error
   */
  protected Date parse(String s, boolean quiet) {
    try {
      return BaseTimeExpression.evaluate(s, m_Start, m_End);
    }
    catch (Exception e) {
      if (!quiet)
	LoggingHelper.global().log(Level.SEVERE, "Failed to parse: " + s, e);
      return null;
    }
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if the string can be parsed
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    
    value = value.toUpperCase();

    if (value.length() == 0)
      return true;

    return (parse(value, true) != null);
  }

  /**
   * Sets the string value.
   *
   * @param value	the string value
   */
  @Override
  public void setValue(String value) {
    if (!isValid(value))
      return;

    if (value.equals(INF_FUTURE_DATE))
      m_Internal = INF_FUTURE;
    else if (value.equals(INF_PAST_DATE))
      m_Internal = INF_PAST;
    else if (value.length() == 0)
      m_Internal = NOW;
    else
      m_Internal = value;
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return (String) m_Internal;
  }

  /**
   * Returns the Date value.
   *
   * @return		the Date value
   */
  public Date dateValue() {
    return parse(getValue(), false);
  }

  /**
   * Returns the Time value.
   *
   * @return		the Time value
   */
  public Time timeValue() {
    return new Time(parse(getValue(), false));
  }

  /**
   * Returns the actual Date as string.
   *
   * @return		the actual date as string
   */
  public String stringValue() {
    return getFormat().format(dateValue());
  }

  /**
   * Returns the Date value formatted as a string.
   *
   * @return		the formatted string
   */
  public String formatDateValue(String format) {
    DateFormat	formatter;

    formatter = new DateFormat(format);
    formatter.setTimeZone(TimeZone.getDefault());

    return formatter.format(dateValue());
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return
        "A date of format '" + FORMAT + "' "
      + "("
      + "'" + INF_PAST + "' = '" + INF_PAST_DATE + "', "
      + "'" + INF_FUTURE + "' = '" + INF_FUTURE_DATE + "', "
      + "'" + NOW + "' = the current date/time"
      + ").";
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new BaseTimeExpression().getGrammar();
  }

  /**
   * Checks whether the date/time is +INF.
   *
   * @return		true if infinity future
   */
  public boolean isInfinityFuture() {
    return getValue().equals(INF_FUTURE);
  }

  /**
   * Checks whether the date/time is -INF.
   *
   * @return		true if infinity future
   */
  public boolean isInfinityPast() {
    return getValue().equals(INF_PAST);
  }

  /**
   * Checks whether the date/time is -INF or +INF.
   *
   * @return		true if any infinity
   */
  public boolean isInfinity() {
    return isInfinityPast() || isInfinityFuture();
  }

  /**
   * Returns a new BaseTime object initialized with the NOW placeholder.
   *
   * @return		the BaseTime object
   * @see		#NOW
   */
  public static BaseTime now() {
    return new BaseTime(NOW);
  }

  /**
   * Returns a new BaseTime object initialized with the INF_FUTURE placeholder.
   *
   * @return		the BaseTime object
   * @see		#INF_FUTURE
   */
  public static BaseTime infinityFuture() {
    return new BaseTime(INF_FUTURE);
  }

  /**
   * Returns a new BaseTime object initialized with the INF_PAST placeholder.
   *
   * @return		the BaseTime object
   * @see		#INF_PAST
   */
  public static BaseTime infinityPast() {
    return new BaseTime(INF_PAST);
  }
}
