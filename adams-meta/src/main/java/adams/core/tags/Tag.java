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
 * Tag.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.tags;

import adams.core.Utils;
import adams.core.base.AbstractBaseString;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for a tag (key/value pair).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Tag
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7223597009565454854L;

  /** the separator. */
  public final static String SEPARATOR = "=";

  /**
   * Initializes the string with length 0.
   */
  public Tag() {
    this("");
  }

  /**
   * Initializes the object with the key/value pair.
   *
   * @param key		the key to use
   * @param value	the value to use
   */
  public Tag(String key, String value) {
    super(key + SEPARATOR + value);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public Tag(String s) {
    super(s);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    return value.isEmpty() || value.contains(SEPARATOR);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Tag (= key/value pair), uses '" + SEPARATOR + "' as separator.";
  }

  /**
   * Returns the backquoted pair (name=value).
   *
   * @return		the backquoted pair
   */
  public String pairValue() {
    return Utils.backQuoteChars(getValue());
  }

  /**
   * Returns the name part.
   *
   * @return		the key
   */
  // TODO rename to tagName
  public String getPairKey() {
    if (getValue().contains(SEPARATOR))
      return getValue().substring(0, getValue().indexOf(SEPARATOR));
    else
      return getValue();
  }

  /**
   * Returns the value part.
   *
   * @return		the value
   */
  // TODO rename to tagValue
  public String getPairValue() {
    if (getValue().contains(SEPARATOR))
      return getValue().substring(getValue().indexOf(SEPARATOR) + 1);
    else
      return "";
  }

  /**
   * Turns the array into a string map.
   *
   * @param pairs	the key/value pairs to use
   * @return		the generated map
   */
  public static Map<String,String> toMap(Tag[] pairs) {
    Map<String,String>	result;

    result = new HashMap<>();
    for (Tag pair: pairs)
      result.put(pair.getPairKey(), pair.getPairValue());

    return result;
  }

  /**
   * Turns the string map into pairs.
   *
   * @param map		the map
   * @return		the generated pairs
   */
  public static Tag[] fromMap(Map<String,String> map) {
    Tag[]	result;
    int			i;

    result = new Tag[map.size()];
    i      = 0;
    for (String key: map.keySet()) {
      result[i] = new Tag(key, map.get(key));
      i++;
    }

    return result;
  }
}
