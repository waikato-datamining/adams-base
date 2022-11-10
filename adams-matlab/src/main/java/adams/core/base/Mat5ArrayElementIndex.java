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
 * Mat5ArrayElementIndex.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Utils;
import adams.data.statistics.StatUtils;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import us.hebi.matlab.mat.types.Array;

/**
 * For specifying a matrix index (or matrix subset).
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Mat5ArrayElementIndex
  extends AbstractBaseString {

  private static final long serialVersionUID = -8138523980228144441L;

  /** the separator between dimensions. */
  public final static String SEPARATOR = ";";

  /**
   * Initializes the index with no index (empty string).
   */
  public Mat5ArrayElementIndex() {
    this("");
  }

  /**
   * Initializes the index with the specified string.
   *
   * @param index	the index
   */
  public Mat5ArrayElementIndex(String index) {
    super(index);
  }

  /**
   * Initializes the index with the specified index array.
   *
   * @param index	the index
   */
  public Mat5ArrayElementIndex(int[] index) {
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

    result = (value.length() > 0);

    if (result) {
      parts = value.split(SEPARATOR);
      for (i = 0; i < parts.length; i++) {
	// unspecified, ie iterate over all values?
	if (parts[i].trim().length() == 0)
	  continue;
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
    return indexValue(false);
  }

  /**
   * Returns the value as dimensional index. Uses -1 for unspecified/empty dimensions (eg for iterating).
   *
   * @param convertToZeroBased	true: convert from 1-based to 0-based, false: leave unchanged
   * @return		the array of positions, empty array if nothing specified
   */
  public int[] indexValue(boolean convertToZeroBased) {
    int[]	result;
    String[]	parts;
    int		i;

    if (getValue().isEmpty()) {
      result = new int[0];
    }
    else {
      parts  = getValue().split(SEPARATOR);
      result = new int[parts.length];
      for (i = 0; i < parts.length; i++) {
	if (parts[i].trim().length() == 0)
	  result[i] = -1;
	else
	  result[i] = Integer.parseInt(parts[i]);
      }
    }

    if (convertToZeroBased) {
      for (i = 0; i < result.length; i++) {
        if (result[i] > 0)
          result[i]--;
      }
    }

    return result;
  }

  /**
   * Returns the indices of the unspecified/open dimensions.
   *
   * @return		the unspecified dimensions, empty array if all specified
   */
  public int[] openDimensions() {
    TIntList	result;
    int[]	index;
    int		i;

    result = new TIntArrayList();
    index  = indexValue();
    for (i = 0; i < index.length; i++) {
      if (index[i] == -1)
	result.add(i);
    }

    return result.toArray();
  }

  /**
   * Checks whether the array is compatible with the dimension specifications.
   *
   * @param array	the array to check against
   * @return		true if the same number of dimensions
   */
  public boolean isCompatible(Array array) {
    int[]	index;

    index = indexValue();
    return (index.length == array.getNumDimensions());
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return the tool tip
   */
  @Override
  public String getTipText() {
    return "Array element index using format: \"DIM1;DIM2;...\" with empty DIM signifying to iterate over all values.";
  }
}
