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
 * WekaAttributeIndex.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka;

import weka.core.Instances;
import adams.core.AbstractDataBackedIndex;
import adams.core.Index;

/**
 * Extended {@link Index} class that can use an attribute name to determine an
 * index of a attribute as well.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAttributeIndex
  extends AbstractDataBackedIndex<Instances> {

  /** for serialization. */
  private static final long serialVersionUID = -4358263779315198808L;
  
  /**
   * Initializes with no index.
   */
  public WekaAttributeIndex() {
    super();
  }

  /**
   * Initializes with the given index, but no maximum.
   *
   * @param index	the index to use
   */
  public WekaAttributeIndex(String index) {
    super(index);
  }

  /**
   * Initializes with the given index and maximum.
   *
   * @param index	the index to use
   * @param max		the maximum of the 1-based index (e.g., use "10" to
   * 			allow "1-10" or -1 for uninitialized)
   */
  public WekaAttributeIndex(String index, int max) {
    super(index, max);
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public WekaAttributeIndex getClone() {
    return (WekaAttributeIndex) super.getClone();
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
   * Returns the example.
   *
   * @return		the example
   */
  @Override
  public String getExample() {
    return
        "An index is a number starting with 1; apart from attribute names "
      + "(case-sensitive), the following placeholders can be used as well: "
      + FIRST + ", " + SECOND + ", " + THIRD + ", " + LAST_2 + ", " + LAST_1 + ", " + LAST;
  }
}
