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
 * StringToJson.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.flow.transformer.JsonFileReader.OutputType;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 <!-- globalinfo-start -->
 * Turns a string into a JSON object or array.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-type &lt;ANY|OBJECT|ARRAY&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of object to forward.
 * &nbsp;&nbsp;&nbsp;default: ANY
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class StringToJson
  extends AbstractConversionFromString {

  /** for serialization. */
  private static final long serialVersionUID = 1484255065339335859L;

  /** the type of object to output. */
  protected OutputType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a string into a JSON object or array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      OutputType.ANY);
  }

  /**
   * Sets the type of object to forward.
   *
   * @param value 	the type
   */
  public void setType(OutputType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of object to forward.
   *
   * @return 		the type
   */
  public OutputType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of object to forward.";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    switch (m_Type) {
      case ANY:
	return JSONAware.class;
      case ARRAY:
	return JSONArray.class;
      case OBJECT:
	return JSONObject.class;
      default:
	throw new IllegalStateException("Unhandled type: " + m_Type);
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
    Object	result;

    try {
      result = new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse((String) m_Input);
      switch (m_Type) {
	case ANY:
	  return result;
	case ARRAY:
	  return (JSONArray) result;
	case OBJECT:
	  return (JSONObject) result;
	default:
	  throw new IllegalStateException("Unhandled type: " + m_Type);
      }
    }
    catch (Exception e) {
      throw new Exception("Failed to parse: " + m_Input, e);
    }
  }
}
