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
 * Mat5ArrayDimensions.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Utils;
import adams.data.statistics.StatUtils;

/**
 * For specifying the dimensions of a Matlab matrix.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Mat5ArrayDimensions
    extends AbstractBaseString {

  private static final long serialVersionUID = -8138523980228144441L;

  /** the separator between dimensions. */
  public final static String SEPARATOR = ";";

  /**
   * Initializes the index with no index (empty string).
   */
  public Mat5ArrayDimensions() {
    this("");
  }

  /**
   * Initializes the index with the specified string.
   *
   * @param index	the index
   */
  public Mat5ArrayDimensions(String index) {
    super(index);
  }

  /**
   * Initializes the index with the specified index array.
   *
   * @param index	the index
   */
  public Mat5ArrayDimensions(int[] index) {
    super(Utils.flatten(StatUtils.toNumberArray(index), SEPARATOR));
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    boolean	result;
    String[]	parts;
    int		i;

    result = (value.length() > 0) && !value.startsWith(SEPARATOR) && !value.endsWith(SEPARATOR);

    if (result) {
      parts = value.split(SEPARATOR);
      for (i = 0; i < parts.length; i++) {
	// unspecified?
	if (parts[i].trim().length() == 0) {
	  result = false;
          break;
        }
	// not an integer?
	if (!Utils.isInteger(parts[i].trim())) {
	  result = false;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Returns the value as dimensional index. Uses -1 for unspecified/empty dimensions (eg for iterating).
   *
   * @return		the array of positions, empty array if nothing specified
   */
  public int[] indexValue() {
    int[]	result;
    String[]	parts;
    int		i;

    if (getValue().isEmpty()) {
      result = new int[0];
    }
    else {
      parts  = getValue().split(SEPARATOR);
      result = new int[parts.length];
      for (i = 0; i < parts.length; i++)
        result[i] = Integer.parseInt(parts[i]);
    }

    return result;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return the tool tip
   */
  @Override
  public String getTipText() {
    return "Array dimensions format: \"DIM1;DIM2;...\".";
  }
}
