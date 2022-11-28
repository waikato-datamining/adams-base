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
 * JsonObjectToMap.java
 * Copyright (C) 2018-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.json.JsonHelper;
import net.minidev.json.JSONObject;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns a JSON object into a map.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-arrays-as-lists &lt;boolean&gt; (property: arraysAsLists)
 * &nbsp;&nbsp;&nbsp;If enabled, JSON arrays get converted to Java lists rather than Java arrays.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JsonObjectToMap
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -468714756281370533L;

  /** whether to return arrays as lists. */
  protected boolean m_ArraysAsLists;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a JSON object into a map.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "arrays-as-lists", "arraysAsLists",
      false);
  }

  /**
   * Sets whether to return JSON arrays as Java arrays or lists.
   *
   * @param value	true if as lists
   */
  public void setArraysAsLists(boolean value) {
    m_ArraysAsLists = value;
    reset();
  }

  /**
   * Returns whether to return JSON arrays as Java arrays or lists.
   *
   * @return		true if as lists
   */
  public boolean getArraysAsLists() {
    return m_ArraysAsLists;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String arraysAsListsTipText() {
    return "If enabled, JSON arrays get converted to Java lists rather than Java arrays.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return JSONObject.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Map.class;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "arraysAsLists", (m_ArraysAsLists ? "as-lists" : "as-arrays"), "output: ");
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return JsonHelper.toMap((JSONObject) m_Input, m_ArraysAsLists);
  }
}
