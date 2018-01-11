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
 * WekaLabelIndex.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka;

import adams.core.AbstractDataBackedIndex;
import adams.core.Index;
import weka.core.Attribute;

/**
 * Extended {@link Index} class that can use a label name to determine an
 * index of a label as well.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaLabelIndex
  extends AbstractDataBackedIndex<Attribute> {

  /** for serialization. */
  private static final long serialVersionUID = -4358263779315198808L;

  /**
   * Initializes with no index.
   */
  public WekaLabelIndex() {
    super();
  }

  /**
   * Initializes with the given index, but no maximum.
   *
   * @param index	the index to use
   */
  public WekaLabelIndex(String index) {
    super(index);
  }

  /**
   * Initializes with the given index and maximum.
   *
   * @param index	the index to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public WekaLabelIndex(String index, int max) {
    super(index, max);
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public WekaLabelIndex getClone() {
    return (WekaLabelIndex) super.getClone();
  }

  /**
   * Returns the number of labels that the attribute has.
   * 
   * @param data	the attribute to retrieve the number of labels
   */
  @Override
  protected int getNumNames(Attribute data) {
    return data.numValues();
  }
  
  /**
   * Returns the label name at the specified index.
   * 
   * @param data	the dataset to use
   * @param colIndex	the label index
   * @return		the label name
   */
  @Override
  protected String getName(Attribute data, int colIndex) {
    return data.value(colIndex);
  }

  /**
   * Returns the example.
   *
   * @return		the example
   */
  @Override
  public String getExample() {
    return
        "An index is a number starting with 1; apart from label names "
      + "(case-sensitive), the following placeholders can be used as well: "
      + FIRST + ", " + SECOND + ", " + THIRD + ", " + LAST_2 + ", " + LAST_1 + ", " + LAST + "; "
      + "numeric indices can be enforced by preceding them with '#' (eg '#12'); "
      + "label names can be surrounded by double quotes.";
  }
}
