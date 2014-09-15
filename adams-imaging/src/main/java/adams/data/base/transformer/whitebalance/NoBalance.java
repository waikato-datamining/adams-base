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
 * NoBalance.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.base.transformer.whitebalance;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Dummy white balance algorithm, performs no balancing at all.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6652 $
 */
public class NoBalance
  extends AbstractWhiteBalanceAlgorithm {

  /** for serialization. */
  private static final long serialVersionUID = -696539737461589970L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy white balance algorithm, performs no balancing at all.";
  }

  /**
   * Simply returns the original image.
   * 
   * @param img		the image to process
   * @return		the processed image
   */
  @Override
  protected BufferedImage doBalance(BufferedImage img) {
    return img;
  }
}
