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
 * TimeToString.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.Date;

import adams.core.DateFormat;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;

/**
 <!-- globalinfo-start -->
 * Turns a number representing milli-seconds since 1970 (Java date) into a String.<br>
 * For more information on formatting the date, see:<br>
 * Javadoc. java.text.SimpleDateFormat.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{missing_id,
 *    author = {Javadoc},
 *    title = {java.text.SimpleDateFormat},
 *    HTTP = {http:&#47;&#47;download.oracle.com&#47;javase&#47;1,5.0&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-format &lt;java.lang.String&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format to use for converting the time into a string.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeToString
  extends AbstractConversionToString
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 6744245717394758406L;

  /** the format to use for converting the time. */
  protected String m_Format;

  /** the formatter to use. */
  protected transient DateFormat m_Formatter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Turns a number representing milli-seconds since 1970 (Java date) "
      + "into a String.\n"
      + "For more information on formatting the date, see:\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    return new DateFormat().getTechnicalInformation();
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "format", "format",
	    "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Resets the converter.
   */
  protected void reset() {
    super.reset();

    m_Formatter = null;
  }

  /**
   * Sets the format for the date/time.
   *
   * @param value	the format
   */
  public void setFormat(String value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format for the date/time.
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
    return "The format to use for converting the time into a string.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  public Class accepts() {
    return Number.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    if (m_Formatter == null)
      m_Formatter = new DateFormat(m_Format);
    return m_Formatter.format(new Date(((Number) m_Input).longValue()));
  }
}
