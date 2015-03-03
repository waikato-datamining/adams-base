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
 * WekaAttributeRange.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka;

import java.util.Comparator;

import weka.core.Instances;
import adams.core.AbstractDataBackedRange;
import adams.core.Range;

/**
 * Extended {@link Range} class that also allows attribute names for specifying
 * attribute positions (names are case-insensitive, just like placeholders for 
 * 'first', 'second', etc). If attribute names contain "-" or "," then they
 * need to be surrounded by double-quotes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAttributeRange
  extends AbstractDataBackedRange<Instances> {

  /** for serialization. */
  private static final long serialVersionUID = 5215987200366396733L;

  /**
   * Initializes with no range.
   */
  public WekaAttributeRange() {
    super();
  }

  /**
   * Initializes with the given range, but no maximum.
   *
   * @param range	the range to use
   */
  public WekaAttributeRange(String range) {
    super(range);
  }

  /**
   * Initializes with the given range and maximum.
   *
   * @param range	the range to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public WekaAttributeRange(String range, int max) {
    super(range, max);
  }

  /**
   * Returns a new comparator to use for sorting the names.
   * 
   * @return		the comparator
   */
  @Override
  protected Comparator newComparator() {
    return new InvertedStringLengthComparator();
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public WekaAttributeRange getClone() {
    return (WekaAttributeRange) super.getClone();
  }

  /**
   * Returns the number of columns the dataset has.
   * 
   * @param data	the dataset to retrieve the number of columns
   */
  @Override
  protected int getNumColumns(Instances data) {
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
  protected String getColumnName(Instances data, int colIndex) {
    return data.attribute(colIndex).name();
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
      + "sub-ranges of indices ('start-end'); "
      + "'inv(...)' inverts the range '...'; apart from attribute names "
      + "(case-sensitive), the following placeholders can be used as well: "
      + FIRST + ", " + SECOND + ", " + THIRD + ", " + LAST_2 + ", " + LAST_1 + ", " + LAST;
  }
}
