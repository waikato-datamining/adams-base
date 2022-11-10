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
 * MapToString.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.QuickInfoHelper;
import adams.core.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns a map object into a simple string.<br>
 * When encountering date objects as values, they get turned into a string using: yyyy-MM-dd HH:mm:ss<br>
 * Output format:<br>
 * &lt;key&gt;:<br>
 *   &lt;value&gt;
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-sort-keys &lt;boolean&gt; (property: sortKeys)
 * &nbsp;&nbsp;&nbsp;If enabled, the keys in the map get sorted before generating the output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MapToString
    extends AbstractConversion {

  private static final long serialVersionUID = -9077969984646598771L;

  /** whether to sort the keys. */
  protected boolean m_SortKeys;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a map object into a simple string.\n"
	+ "When encountering date objects as values, they get turned into a string "
	+ "using: " + DateUtils.getTimestampFormatter().toPattern() + "\n"
	+ "Output format:\n"
	+ "<key>:\n"
	+ "  <value>";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"sort-keys", "sortKeys",
	false);
  }

  /**
   * Sets whether to sort the keys before generating the output.
   *
   * @param value true if to sort the keys
   */
  public void setSortKeys(boolean value) {
    m_SortKeys = value;
    reset();
  }

  /**
   * Returns whether to sort the keys before generating the output.
   *
   * @return true if to sort the keys
   */
  public boolean getSortKeys() {
    return m_SortKeys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String sortKeysTipText() {
    return "If enabled, the keys in the map get sorted before generating the output.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "sortKeys", (m_SortKeys ? "sorted" : "unsorted"), "keys: ");
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Map.class;
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
    StringBuilder 	result;
    Map			input;
    String		key;
    Object		valueObj;
    String 		value;
    DateFormat		dformat;
    List<String> 	keys;

    input   = (Map) m_Input;
    keys    = new ArrayList<>(input.keySet());
    if (m_SortKeys)
      Collections.sort(keys);
    result  = new StringBuilder();
    dformat = DateUtils.getTimestampFormatter();

    for (Object keyObj: keys) {
      key      = "" + keyObj;
      valueObj = input.get(keyObj);
      if (valueObj instanceof Date)
	value = dformat.format((Date) valueObj);
      else if (valueObj.getClass().isArray())
        value = Utils.arrayToString(valueObj);
      else
	value = "" + valueObj;
      result.append(key).append(":\n");
      result.append("  ").append(value).append("\n");
    }

    return result.toString();
  }
}
