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
 * WekaUnorderedAttributeRange.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka;

import adams.core.AbstractDataBackedUnorderedRange;
import adams.core.UnorderedRange;
import weka.core.Instances;

/**
 * Extended {@link UnorderedRange} class that also allows attribute names for specifying
 * attribute positions (names are case-insensitive, just like placeholders for 
 * 'first', 'second', etc). If attribute names contain "-" or "," then they
 * need to be surrounded by double-quotes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaUnorderedAttributeRange
  extends AbstractDataBackedUnorderedRange<Instances> {

  /** for serialization. */
  private static final long serialVersionUID = 5215987200366396733L;

  /**
   * Initializes with no range.
   */
  public WekaUnorderedAttributeRange() {
    super();
  }

  /**
   * Initializes with the given range, but no maximum.
   *
   * @param range	the range to use
   */
  public WekaUnorderedAttributeRange(String range) {
    super(range);
  }

  /**
   * Initializes with the given range and maximum.
   *
   * @param range	the range to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public WekaUnorderedAttributeRange(String range, int max) {
    super(range, max);
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public WekaUnorderedAttributeRange getClone() {
    return (WekaUnorderedAttributeRange) super.getClone();
  }

  /**
   * Returns the number of columns the dataset has.
   * 
   * @param data	the dataset to retrieve the number of columns
   */
  @Override
  protected int getNumNames(Instances data) {
    return data.numAttributes();
  }
  
  /**
   * Returns the column name at the specified index.
   * 
   * @param data	the dataset to use
   * @param colIndex	the column index
   * @return		the column name
   */
  @Override
  protected String getName(Instances data, int colIndex) {
    return data.attribute(colIndex).name();
  }

  /**
   * Parses the subrange.
   *
   * @param subrange	the subrange
   * @return		the indices
   */
  protected int[] parseSubRange(String subrange) {
    WekaAttributeRange range;

    range = new WekaAttributeRange(subrange);
    if (m_Data == null)
      range.setMax(m_Max);
    else
      range.setData(m_Data);

    return range.getIntIndices();
  }

  /**
   * Returns the example.
   *
   * @return		the example
   */
  @Override
  public String getExample() {
    return
        "A range is a comma-separated list of single 1-based indices or "
      + "sub-ranges of indices ('start-end'); apart from attribute names "
      + "(case-sensitive), the following placeholders can be used as well: "
      + FIRST + ", " + SECOND + ", " + THIRD + ", " + LAST_2 + ", " + LAST_1 + ", " + LAST + "; "
      + "numeric indices can be enforced by preceding them with '#' (eg '#12'); "
      + "attribute names can be surrounded by double quotes.";
  }
}
