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
 * BinningPostProcessingUser.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.postprocessing;

/**
 * Interface for classes that apply post-processing to bins.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface BinPostProcessingUser {

  /**
   * Sets the post-processing scheme to apply to the bins.
   *
   * @param value 	the post-processing
   */
  public void setPostProcessing(BinPostProcessing value);

  /**
   * Returns the post-processing scheme to apply to the bins.
   *
   * @return 		the post-processing
   */
  public BinPostProcessing getPostProcessing();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessingTipText();
}
