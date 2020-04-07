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
 * BufferedImagesToBufferedImageBitmaskContainer.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.Utils;
import adams.data.image.BufferedImageBitmaskContainer;
import adams.data.image.BufferedImageContainer;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Turns the BufferedImage containers into a container with image (index=0) and bitmasks (index&gt;=1).<br>
 * Only the report from the first container is transferred.
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
public class BufferedImagesToBufferedImageBitmaskContainer
  extends AbstractConversion {

  private static final long serialVersionUID = 2072620446932941644L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the BufferedImage containers into a container with "
      + "image (index=0) and bitmasks (index>=1).\n"
      + "Only the report from the first container is transferred.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return BufferedImageContainer[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return BufferedImageBitmaskContainer.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    BufferedImageBitmaskContainer	result;
    BufferedImageContainer[]		conts;
    BufferedImage[]			bitmasks;
    int					i;

    conts = (BufferedImageContainer[]) m_Input;
    if (conts.length < 2)
      throw new IllegalStateException("Expected array of length >=2 of " + Utils.classToString(BufferedImageContainer.class) + " objects, but received: " + conts.length);

    result = new BufferedImageBitmaskContainer();
    result.setContent(conts[0].getContent());
    result.setReport(conts[0].getReport().getClone());
    bitmasks = new BufferedImage[conts.length - 1];
    for (i = 1; i < conts.length; i++)
      bitmasks[i - 1] = conts[i].getImage();
    result.setBitmasks(bitmasks);

    return result;
  }
}
