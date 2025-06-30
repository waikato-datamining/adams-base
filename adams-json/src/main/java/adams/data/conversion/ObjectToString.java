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
 * ObjectToString.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.flow.core.Unknown;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 <!-- globalinfo-start -->
 * Turns the object into a JSON string using Jackson's ObjectMapper (https:&#47;&#47;github.com&#47;FasterXML&#47;jackson-databind).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ObjectToString
  extends AbstractConversionToString {

  /** for serialization. */
  private static final long serialVersionUID = 1383459505178870114L;

  /** the mapper instance to use. */
  protected transient ObjectMapper m_Mapper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Turns the object into a JSON string using Jackson's ObjectMapper (https://github.com/FasterXML/jackson-databind).";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Unknown.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    if (m_Mapper == null)
      m_Mapper = new ObjectMapper();

    return m_Mapper.writeValueAsString(m_Input);
  }
}
