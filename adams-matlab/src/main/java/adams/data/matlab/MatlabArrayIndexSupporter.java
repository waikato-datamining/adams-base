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
 * MatlabArrayIndexSupporter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.matlab;

import adams.core.base.Mat5ArrayElementIndex;
import adams.core.option.OptionHandler;

/**
 * Interface for classes that use an array index.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface MatlabArrayIndexSupporter
  extends OptionHandler {

  /**
   * Sets the index to obtain.
   *
   * @param value	the index
   */
  public void setIndex(Mat5ArrayElementIndex value);

  /**
   * Returns the index to obtain.
   *
   * @return		the index
   */
  public Mat5ArrayElementIndex getIndex();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText();

  /**
   * Sets whether the index is 0-based or 1-based.
   *
   * @param value	true if 0-based
   */
  public void setZeroBasedIndex(boolean value);

  /**
   * Returns whether the index is 0-based or 1-based.
   *
   * @return		true if 0-based
   */
  public boolean getZeroBasedIndex();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zeroBasedIndexTipText();
}
