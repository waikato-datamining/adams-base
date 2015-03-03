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
 * Iteratable.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

/**
 * For classes that support iteration.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Iteratable {

  /**
   * Sets the number of iterations to perform.
   * 
   * @param value	the number of iterations
   */
  public void setNumIterations(int value);

  /**
   * Returns the number of iteratons.
   * 
   * @return  		the number of iterations
   */
  public int getNumIterations();

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numIterationsTipText();
}
