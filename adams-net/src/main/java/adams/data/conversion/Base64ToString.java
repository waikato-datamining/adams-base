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
 * Base64ToString.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.net.Base64Type;

import java.util.Base64;

/**
 <!-- globalinfo-start -->
 * Decodes a base64 string.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-type &lt;AUTO|BASIC|URL_FILENAME_SAFE|MIME&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of conversion to use; AUTO attempts all of them.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Base64ToString
  extends AbstractStringConversion {

  /** for serialization. */
  private static final long serialVersionUID = 1383459505178870114L;

  /** the type of conversion to apply. */
  protected Base64Type m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Decodes a base64 string.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      Base64Type.AUTO);
  }

  /**
   * Sets the conversion to apply. AUTO attempts all.
   *
   * @param value	the type
   */
  public void setType(Base64Type value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the conversion to apply. AUTO attempts all.
   *
   * @return		the type
   */
  public Base64Type getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of conversion to use; " + Base64Type.AUTO + " attempts all of them.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    return new String(Base64.getDecoder().decode((String) m_Input));
  }
}
