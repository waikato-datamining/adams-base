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
 * BufferedImageBitmaskContainerToBufferedImages.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.data.image.BufferedImageBitmaskContainer;
import adams.data.image.BufferedImageContainer;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Turns the bitmask container back into an array of containers for BufferedImage objects, with the image being the first and the bitmasks the other elements.<br>
 * The incoming report gets cloned into all of the outgoing containers.
 * <br><br>
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BufferedImageBitmaskContainerToBufferedImages
  extends AbstractConversion {

  private static final long serialVersionUID = 8416640937812264746L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the bitmask container back into an array of containers "
      + "for BufferedImage objects, with the image being the first and the "
      + "bitmasks the other elements.\n"
      + "The incoming report gets cloned into all of the outgoing containers.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return BufferedImageBitmaskContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return BufferedImageContainer[].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    BufferedImageContainer[]		result;
    BufferedImageBitmaskContainer 	input;
    BufferedImage[]			bitmasks;
    int					i;

    input     = (BufferedImageBitmaskContainer) m_Input;
    bitmasks  = input.getBitmasks();
    result    = new BufferedImageContainer[1 + input.getNumBitmasks()];
    result[0] = new BufferedImageContainer();
    result[0].setImage(input.getImage());
    result[0].setReport(input.getReport().getClone());
    for (i = 0; i < input.getNumBitmasks(); i++) {
      result[i + 1] = new BufferedImageContainer();
      result[i + 1].setImage(bitmasks[i]);
      result[i + 1].setReport(input.getReport().getClone());
    }

    return result;
  }
}
