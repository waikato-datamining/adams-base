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
 * StringArrayToURLParameters.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;

import java.net.URLEncoder;

/**
 <!-- globalinfo-start -->
 * Turns a string array into parameters of a URL, e.g., '?k1=v1&amp;k2=v2' (uses java.net.URLEncoder).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use for the encoding.
 * &nbsp;&nbsp;&nbsp;default: UTF-8
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StringArrayToURLParameters
  extends AbstractConversion
  implements EncodingSupporter {

  private static final long serialVersionUID = 8430779020000469313L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a string array into parameters of a URL, e.g., '?k1=v1&k2=v2' (uses " + Utils.classToString(URLEncoder.class) + ").";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset("UTF-8"));
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use for the encoding.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "encoding", m_Encoding);
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return String[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    StringBuilder	result;
    String[]		input;
    int			i;

    result = new StringBuilder();
    input  = (String[]) m_Input;
    for (i = 0; i < input.length; i++) {
      if (i == 0)
        result.append("?");
      else
        result.append("&");
      result.append(URLEncoder.encode(input[i], m_Encoding.stringValue()));
    }

    return result.toString();
  }
}
