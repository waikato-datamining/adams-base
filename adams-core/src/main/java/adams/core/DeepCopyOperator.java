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
 * DeepCopyOperator.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.option.OptionHandler;

/**
 * Interface for option handlers that can perform optional deep copies.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface DeepCopyOperator
  extends OptionHandler {

  /**
   * Sets whether to perform a deep copy of each array element before transferring it into the target array.
   *
   * @param value	true if to copy
   */
  public void setDeepCopy(boolean value);

  /**
   * Returns whether to perform a deep copy of each array element before transferring it into the target array.
   *
   * @return		true if to copy
   */
  public boolean getDeepCopy();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String deepCopyTipText();
}
