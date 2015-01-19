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
 * Dummy.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.barcode.encode;

import java.awt.image.BufferedImage;

/**
 * <!-- globalinfo-start -->
 * * Dummy barcode type, draws nothing.
 * * <p/>
 * <!-- globalinfo-end -->
 * <p/>
 * <!-- options-start -->
 * * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * * &nbsp;&nbsp;&nbsp;default: WARNING
 * * </pre>
 * * 
 * * <pre>-x &lt;int&gt; (property: x)
 * * &nbsp;&nbsp;&nbsp;X position of the top-left corner.
 * * &nbsp;&nbsp;&nbsp;default: 1
 * * &nbsp;&nbsp;&nbsp;minimum: 1
 * * </pre>
 * * 
 * * <pre>-y &lt;int&gt; (property: y)
 * * &nbsp;&nbsp;&nbsp;Y position of the top-left corner.
 * * &nbsp;&nbsp;&nbsp;default: 1
 * * &nbsp;&nbsp;&nbsp;minimum: 1
 * * </pre>
 * * 
 * * <pre>-width &lt;int&gt; (property: width)
 * * &nbsp;&nbsp;&nbsp;Width of the barcode in pixels.
 * * &nbsp;&nbsp;&nbsp;default: 100
 * * &nbsp;&nbsp;&nbsp;minimum: 1
 * * </pre>
 * * 
 * * <pre>-height &lt;int&gt; (property: height)
 * * &nbsp;&nbsp;&nbsp;Height of the barcode in pixels.
 * * &nbsp;&nbsp;&nbsp;default: 100
 * * &nbsp;&nbsp;&nbsp;minimum: 1
 * * </pre>
 * * 
 * * <pre>-margin &lt;int&gt; (property: margin)
 * * &nbsp;&nbsp;&nbsp;White margin surrounding the barcode.
 * * &nbsp;&nbsp;&nbsp;default: 5
 * * &nbsp;&nbsp;&nbsp;minimum: 0
 * * </pre>
 * * 
 * <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class PassThrough extends AbstractBarcodeEncoder {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = 2986332203089616095L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy barcode type, draws nothing.";
  }

  /**
   * Dummy barcode encoder, does no encoding.
   *
   * @param image the image to draw on
   * @return the image to draw on
   */
  @Override
  protected String doDraw(BufferedImage image) {
    return null;
  }
}
