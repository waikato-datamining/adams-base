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
 * StringAnonymizer.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import adams.data.spreadsheet.SpreadSheetUtils;

/**
 * Anonymizes Integer objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringAnonymizer
  extends AbstractAnonymizer<String> {

  /** for serialization. */
  private static final long serialVersionUID = 1155523733572722431L;

  /**
   * Default constructor. Uses a random ID and buffer size 100.
   */
  public StringAnonymizer() {
    super();
  }

  /**
   * Initializes the anonymizer with a random seed value.
   * 
   * @param id		the ID of the anonymizer
   * @param bufferSize	the size of the buffer for unused IDs
   */
  public StringAnonymizer(String id, int bufferSize) {
    super(id, bufferSize);
  }

  /**
   * Initializes the anonymizer.
   * 
   * @param id		the ID of the anonymizer
   * @param seed	the seed value for the random number generator
   * @param bufferSize	the size of the buffer for unused IDs
   */
  public StringAnonymizer(String id, long seed, int bufferSize) {
    super(id, seed, bufferSize);
  }

  /**
   * Turns the anonymous integer ID into the appropriate data type.
   * 
   * @param id		the ID to convert
   * @return		the final result
   */
  @Override
  protected String toAnonymized(Integer id) {
    return SpreadSheetUtils.getColumnPosition(id);
  }
}
