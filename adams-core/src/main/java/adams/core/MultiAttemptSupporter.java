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
 * MultiAttemptSupporter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core;

/**
 * Interface for classes that support multiple attempts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MultiAttemptSupporter {

  /**
   * Sets the number of attempts.
   *
   * @param value	the number of attempts
   */
  public void setNumAttempts(int value);

  /**
   * Returns the number of attempts.
   *
   * @return		the number of attempts
   */
  public int getNumAttempts();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numAttemptsTipText();
}
