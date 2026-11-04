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
 * MapToJson.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.map;

import adams.data.json.JsonHelper;
import net.minidev.json.JSONObject;

import java.util.Map;

/**
 * Turns a JSON object into a string.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MapToJson 
  extends AbstractMapToString {

  private static final long serialVersionUID = -1081844079008094794L;

  /** whether to use pretty-printing. */
  protected boolean m_PrettyPrinting;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a JSON object into a string.";
  }

  /**
   * Sets whether to use pretty-printing or not.
   *
   * @param value	true if to use pretty-printing
   */
  public void setPrettyPrinting(boolean value) {
    m_PrettyPrinting = value;
    reset();
  }

  /**
   * Returns whether pretty-printing is used or not.
   *
   * @return		true if to use pretty-printing
   */
  public boolean getPrettyPrinting() {
    return m_PrettyPrinting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prettyPrintingTipText() {
    return "If enabled, the output is printed in a 'pretty' format.";
  }

  /**
   * Converts the map into a string.
   *
   * @param map the map to convert
   * @return the generated string
   */
  @Override
  protected String toString(Map map) {
    String	result;
    JSONObject	jobj;

    jobj   = JsonHelper.fromMap(map);
    result = jobj.toJSONString();
    if (m_PrettyPrinting)
      result = JsonHelper.prettyPrint(result);

    return result;
  }
}
