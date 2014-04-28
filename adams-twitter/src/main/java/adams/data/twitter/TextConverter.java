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
 * TextConverter.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.twitter;

import java.util.Date;
import java.util.Hashtable;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.DateFormatString;

/**
 <!-- globalinfo-start -->
 * A simple text converter.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-field &lt;ID|USER_ID|USER_NAME|SOURCE|TEXT|CREATED|FAVORITED|RETWEET|RETWEET_COUNT|RETWEET_BY_ME|POSSIBLY_SENSITIVE|GEO_LATITUDE|GEO_LONGITUDE|LANGUAGE_CODE|PLACE|PLACE_TYPE|PLACE_URL|STREET_ADDRESS|COUNTRY|COUNTRY_CODE&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to use for generating the output.
 * &nbsp;&nbsp;&nbsp;default: TEXT
 * </pre>
 * 
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator to use when generating strings as output; tab, new line, carriage 
 * &nbsp;&nbsp;&nbsp;return and backslash need to be escaped, ie, '&nbsp;&nbsp;&nbsp;', '
 * &nbsp;&nbsp;&nbsp;', '
', '\'.
 * &nbsp;&nbsp;&nbsp;default: \t
 * </pre>
 * 
 * <pre>-quote &lt;boolean&gt; (property: quote)
 * &nbsp;&nbsp;&nbsp;If enabled all sub-strings are quoted if necessary when generating string 
 * &nbsp;&nbsp;&nbsp;output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-date-format &lt;adams.data.DateFormatString&gt; (property: dateFormat)
 * &nbsp;&nbsp;&nbsp;The format for the dates.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextConverter
  extends AbstractTwitterStatusConverter<String> {

  /** for serialization. */
  private static final long serialVersionUID = 1409389451601382272L;

  /** the separator, when generating a string. */
  protected String m_Separator;

  /** whether to quote strings. */
  protected boolean m_Quote;

  /** for format for dates. */
  protected DateFormatString m_DateFormat;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A simple text converter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "separator", "separator",
	    "\t");

    m_OptionManager.add(
	    "quote", "quote",
	    false);

    m_OptionManager.add(
	    "date-format", "dateFormat",
	    new DateFormatString(Constants.TIMESTAMP_FORMAT));
  }

  /**
   * Sets the separator to use. \t, \n, \r, \\ must be quoted.
   *
   * @param value	the separator
   */
  public void setSeparator(String value) {
    m_Separator = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the separator in use. \t, \r, \n, \\ get returned quoted.
   *
   * @return		the separator
   */
  public String getSeparator() {
    return Utils.backQuoteChars(m_Separator);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorTipText() {
    return
        "The separator to use when generating strings as output; tab, "
      + "new line, carriage return and backslash need to be escaped, ie, "
      + "'\t', '\n', '\r', '\\'.";
  }

  /**
   * Sets whether to quote the sub-strings when generating string output.
   *
   * @param value	if true then the sub-strings get quoted
   */
  public void setQuote(boolean value) {
    m_Quote = value;
    reset();
  }

  /**
   * Returns whether to quote the sub-strings when generating string output.
   *
   * @return		true if quoting enabled
   */
  public boolean getQuote() {
    return m_Quote;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String quoteTipText() {
    return
        "If enabled all sub-strings are quoted if necessary when generating "
      + "string output.";
  }

  /**
   * Sets the date format.
   *
   * @param value	the separator
   */
  public void setDateFormat(DateFormatString value) {
    m_DateFormat = value;
    reset();
  }

  /**
   * Returns the current date format.
   *
   * @return		the date format
   */
  public DateFormatString getDateFormat() {
    return m_DateFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dateFormatTipText() {
    return "The format for the dates.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "separator", Utils.backQuoteChars(m_Separator), ", separator: ");
    result += QuickInfoHelper.toString(this, "quote", m_Quote, "quote", ", ");
    
    return result;
  }

  /**
   * Returns the class of the output data that is generated.
   * 
   * @return		the data type
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Performs the actual conversion.
   * 
   * @param fields	the status data to convert
   * @return		the generated output
   */
  @Override
  protected String doConvert(Hashtable<TwitterField,Object> fields) {
    StringBuilder	result;
    int			i;
    Object		obj;
    DateFormat		dformat;
    String		str;

    result  = new StringBuilder();
    dformat = m_DateFormat.toDateFormat();
    for (i = 0; i < m_Fields.length; i++) {
      if (i > 0)
	result.append(m_Separator);
      obj = fields.get(m_Fields[i]);
      if (obj == null) {
	result.append("?");
      }
      else {
	if (obj instanceof Date)
	  str = dformat.format((Date) obj);
	else
	  str = "" + obj;
	if (m_Quote)
	  result.append(Utils.doubleQuote(str));
	else
	  result.append(str);
      }
    }

    return result.toString();
  }
}
