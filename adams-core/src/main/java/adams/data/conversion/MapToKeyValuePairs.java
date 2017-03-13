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
 * MapToKeyValuePairs.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts the java.util.Map into key-value pairs.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-use-strings &lt;boolean&gt; (property: useStrings)
 * &nbsp;&nbsp;&nbsp;If enabled, a String array is output instead of one made up of adams.core.base.BaseKeyValuePair.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10824 $
 */
public class MapToKeyValuePairs
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4017583319699378889L;

  /** whether to just generate simple strings. */
  protected boolean m_UseStrings;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Converts the " + Map.class.getName() + " into key-value pairs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-strings", "useStrings",
      false);
  }

  /**
   * Sets whether to output strings instead of {@link BaseKeyValuePair}.
   *
   * @param value	true if to use strings
   */
  public void setUseStrings(boolean value) {
    m_UseStrings = value;
    reset();
  }

  /**
   * Returns whether to output strings instead of {@link BaseKeyValuePair}.
   *
   * @return		true if to use strings
   */
  public boolean getUseStrings() {
    return m_UseStrings;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useStringsTipText() {
    return "If enabled, a String array is output instead of one made up of " + BaseKeyValuePair.class.getName() + ".";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "useStrings", m_UseStrings ? "strings" : "key-value objects");
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
    if (m_UseStrings)
      return String[].class;
    else
      return BaseKeyValuePair[].class;
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
    List 	list;
    Map		map;
    int		i;

    list = new ArrayList();
    map    = (Map) m_Input;
    for (Object key: map.keySet())
      list.add(new BaseKeyValuePair("" + key, "" + map.get(key)));

    if (m_UseStrings) {
      for (i = 0; i < list.size(); i++)
	list.set(i, ((BaseKeyValuePair) list.get(i)).getValue());
      result = list.toArray(new String[list.size()]);
    }
    else {
      result = list.toArray(new BaseKeyValuePair[list.size()]);
    }

    return result;
  }
}
