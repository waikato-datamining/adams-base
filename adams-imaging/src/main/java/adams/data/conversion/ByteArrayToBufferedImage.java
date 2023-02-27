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
 * ByteArrayToBufferedImage.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.MessageCollection;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 <!-- globalinfo-start -->
 * Converts the byte array representating a binary image (e.g., JPG or PNG) into a BufferedImage container.
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
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ByteArrayToBufferedImage
  extends AbstractConversion {

  private static final long serialVersionUID = -5360462559635715854L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts the byte array representating a binary image (e.g., JPG or PNG) into a BufferedImage container.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return byte[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return BufferedImageContainer.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    BufferedImageContainer	result;
    MessageCollection		errors;
    BufferedImage 		image;

    errors = new MessageCollection();
    image  = BufferedImageHelper.fromBytes((byte[]) m_Input, errors);
    if (image == null) {
      if (errors.isEmpty())
	throw new IOException("Failed to convert bytes to BufferedImage!");
      else
	throw new IOException("Failed to convert bytes to BufferedImage: " + errors);
    }
    else {
      result = new BufferedImageContainer();
      result.setImage(image);
      return result;
    }
  }
}
