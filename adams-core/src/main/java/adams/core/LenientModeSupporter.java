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
 * LenientModeSupporter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.core;

/**
 * Interface for classes that support a lenient or strict mode.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface LenientModeSupporter {

  /**
   * Sets whether to use lenient mode.
   *
   * @param value	true if to turn on lenient mode
   */
  public void setLenient(boolean value);

  /**
   * Returns whether to use lenient mode.
   *
   * @return		true if lenient mode on
   */
  public boolean getLenient();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lenientTipText();
}
