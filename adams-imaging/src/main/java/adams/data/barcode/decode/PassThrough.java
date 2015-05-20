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
 * PassThrough.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.barcode.decode;

import adams.data.image.AbstractImageContainer;
import adams.data.text.TextContainer;

/**
 <!-- globalinfo-start -->
 * Dummy decoder, does nothing.
 * <br><br>
 <!-- globalinfo-end -->
 * <br><br>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class PassThrough
  extends AbstractBarcodeDecoder {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = 6149942254926179607L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy decoder, does nothing.";
  }

  /**
   * Performs the actual decoding.
   *
   * @param image the image to extract the barcode from
   * @return a TextContainer with the decoded barcode text and (optional) meta-data
   */
  protected TextContainer doDecode(AbstractImageContainer image) {
    return new TextContainer();
  }
}
