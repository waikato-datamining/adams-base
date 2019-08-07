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
 * Null.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.data.idextraction;

/**
 * Does nothing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Null
  extends AbstractIDExtractor {

  private static final long serialVersionUID = 6130414784797102811L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Does nothing.";
  }

  /**
   * Checks whether the data type is handled.
   *
   * @param obj		the object to check
   * @return		always false
   */
  public boolean handles(Object obj) {
    return false;
  }

  /**
   * Extracts the ID from the object.
   *
   * @param obj		the object to process
   * @return		always null
   */
  @Override
  protected String doExtractID(Object obj) {
    return null;
  }
}
