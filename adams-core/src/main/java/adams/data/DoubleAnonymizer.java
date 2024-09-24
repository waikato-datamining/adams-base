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
 * IntegerAnonymizer.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

/**
 * Anonymizes Double objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DoubleAnonymizer
  extends AbstractAnonymizer<Double> {

  /** for serialization. */
  private static final long serialVersionUID = 1155523733572722431L;

  /**
   * Default constructor. Uses a random ID and buffer size 100.
   */
  public DoubleAnonymizer() {
    super();
  }

  /**
   * Initializes the anonymizer with a random seed value.
   * 
   * @param id		the ID of the anonymizer
   * @param bufferSize	the size of the buffer for unused IDs
   */
  public DoubleAnonymizer(String id, int bufferSize) {
    super(id, bufferSize);
  }

  /**
   * Initializes the anonymizer.
   * 
   * @param id		the ID of the anonymizer
   * @param seed	the seed value for the random number generator
   * @param bufferSize	the size of the buffer for unused IDs
   */
  public DoubleAnonymizer(String id, long seed, int bufferSize) {
    super(id, seed, bufferSize);
  }

  /**
   * Turns the anonymous integer ID into the appropriate data type.
   * 
   * @param id		the ID to convert
   * @return		the final result
   */
  @Override
  protected Double toAnonymized(Integer id) {
    return id.doubleValue();
  }
}
