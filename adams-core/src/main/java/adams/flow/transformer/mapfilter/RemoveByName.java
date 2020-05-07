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
 * RemoveByName.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mapfilter;

import adams.core.Utils;
import adams.core.base.BaseRegExp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Filters the map by removing values which keys match the regular expression.
 * The matching can be inverted.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemoveByName
  extends AbstractMapFilter {

  private static final long serialVersionUID = -283220118803834279L;

  /** the regular expression that the names must match. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Filters the map by removing values which keys match the regular expression. "
      + "The matching can be inverted.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "invert", "invert",
      false);
  }

  /**
   * Sets the regular expression to match the keys against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the keys against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the keys.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Filters the map.
   *
   * @param map		the map to filter
   * @return		the filtered map
   */
  @Override
  protected Map doFilterMap(Map map) {
    List<Object> 	remove;
    String		keyStr;
    Pattern		pattern;

    remove  = new ArrayList<>();
    pattern = m_RegExp.patternValue();
    for (Object keyObj: map.keySet()) {
      keyStr = "" + keyObj;
      if (m_Invert) {
        if (!pattern.matcher(keyStr).matches())
          remove.add(keyObj);
      }
      else {
        if (pattern.matcher(keyStr).matches())
          remove.add(keyObj);
      }
    }

    if (isLoggingEnabled())
      getLogger().info("Removing keys: " + Utils.flatten(remove, ", "));

    for (Object key: remove)
      map.remove(key);

    return map;
  }
}
