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
 * OpenIMAJToBufferedImage.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.image.BufferedImageContainer;
import adams.data.openimaj.OpenIMAJImageContainer;

/**
 <!-- globalinfo-start -->
 * Turns an OpenIMAJ container into a BufferedImage one.
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11709 $
 */
public class OpenIMAJToBufferedImage
  extends AbstractConversion
  implements OtherFormatToBufferedImageConversion {

  /** for serialization. */
  private static final long serialVersionUID = -6909862341852136089L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns an OpenIMAJ container into a BufferedImage one.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return OpenIMAJImageContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return BufferedImageContainer.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    BufferedImageContainer	result;
    OpenIMAJImageContainer	input;

    input  = (OpenIMAJImageContainer) m_Input;
    result = new BufferedImageContainer();
    result.setReport(input.getReport().getClone());
    result.setNotes(input.getNotes().getClone());
    result.setImage(input.toBufferedImage());

    return result;
  }
}
