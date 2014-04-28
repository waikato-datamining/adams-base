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
 * TimestampToDouble.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Constants;
import adams.core.DateFormat;

/**
 <!-- globalinfo-start -->
 * Turns a timestamp string into a Double (time in msecs since 1970).<br/>
 * See Javadoc of java.text.SimpleDateFormat class for more information:<br/>
 * http:&#47;&#47;download.oracle.com&#47;javase&#47;1.5.0&#47;docs&#47;api&#47;java&#47;text&#47;DateFormat.html
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
 * <pre>-format &lt;java.lang.String&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format of the timestamp.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
public class TimestampToDouble
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 2114949459692278604L;

  /** the format to use for parsing the timestamp string. */
  protected String m_Format;

  /** the actual formatter. */
  protected DateFormat m_Formatter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Deprecated! Use the following conversion instead:\n"
      + "  " + StringToDateTimeType.class.getName() + "\n\n"
      + "Turns a timestamp string into a Double (time in msecs since 1970).\n"
      + "See Javadoc of java.text.SimpleDateFormat class for more information:\n"
      + "http://download.oracle.com/javase/1.5.0/docs/api/java/text/DateFormat.html";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "format", "format",
	    Constants.TIMESTAMP_FORMAT);
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Formatter = null;
  }

  /**
   * Sets the format to use for parsing the timestamp string.
   *
   * @param value	the format
   */
  public void setFormat(String value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format to use for parsing the timestamp string.
   *
   * @return 		the format
   */
  public String getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format of the timestamp.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return String.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Double.class;
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
      m_Formatter = new DateFormat(m_Format);

    return new Double(m_Formatter.parse((String) m_Input).getTime());
  }
}
