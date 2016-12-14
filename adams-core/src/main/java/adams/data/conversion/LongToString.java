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
 * LongToString.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.ByteFormat;
import adams.core.ByteFormatString;

/**
 <!-- globalinfo-start -->
 * Turns a Long into a String.<br>
 * Can be optionally formatted using a byte format string.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-use-format &lt;boolean&gt; (property: useFormat)
 * &nbsp;&nbsp;&nbsp;If enabled, the byte format string will be used.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-format &lt;adams.core.ByteFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The byte format string to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LongToString
  extends AbstractConversionToString {

  /** for serialization. */
  private static final long serialVersionUID = 6744245717394758406L;

  /** whether to use byte format string. */
  protected boolean m_UseFormat;

  /** the byte format string to use. */
  protected ByteFormatString m_Format;

  /** the byteformat instance in use. */
  protected transient ByteFormat m_ByteFormat;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Turns a Long into a String.\n"
	+ "Can be optionally formatted using a byte format string.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-format", "useFormat",
      false);

    m_OptionManager.add(
      "format", "format",
      new ByteFormatString());
  }

  /**
   * Sets whether to use the format string.
   *
   * @param value	true if to use format
   */
  public void setUseFormat(boolean value) {
    m_UseFormat = value;
    reset();
  }

  /**
   * Returns whether to use the format string.
   *
   * @return 		true if to use format
   */
  public boolean getUseFormat() {
    return m_UseFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useFormatTipText() {
    return "If enabled, the byte format string will be used.";
  }

  /**
   * Sets the byte format string.
   *
   * @param value	the format
   */
  public void setFormat(ByteFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the byte format string.
   *
   * @return 		the format
   */
  public ByteFormatString getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The byte format string to use.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  public Class accepts() {
    return Long.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    if (m_UseFormat) {
      if (m_ByteFormat == null)
	m_ByteFormat = m_Format.toByteFormat();
      return m_ByteFormat.format((Long) m_Input);
    }
    else {
      return m_Input.toString();
    }
  }
}
