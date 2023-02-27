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
 * BufferedImageToByteArray.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Turns a BufferedImage object into a byte array.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-format &lt;java.lang.String&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The image format to use, e.g., JPG or PNG.
 * &nbsp;&nbsp;&nbsp;default: JPG
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BufferedImageToByteArray
  extends AbstractImageToByteArray {

  private static final long serialVersionUID = 4452018959514672473L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a BufferedImage object into a byte array.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return BufferedImage.class;
  }

  /**
   * Turns the input into a BufferedImage.
   *
   * @param input	the input to convert
   * @return		the buffered image
   */
  protected BufferedImage toBufferedImage(Object input) {
    return (BufferedImage) input;
  }
}
