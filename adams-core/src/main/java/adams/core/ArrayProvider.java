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
 * ArrayProvider.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

/**
 * For classes that can generate data either as an array or one-by-one.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ArrayProvider {

  /**
   * Sets whether to generate data as array or as single objects.
   *
   * @param value	true if output is an array
   */
  public void setOutputArray(boolean value);

  /**
   * Returns whether to generate the as array or as single objects.
   *
   * @return		true if output is an array
   */
  public boolean getOutputArray();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputArrayTipText();
}
