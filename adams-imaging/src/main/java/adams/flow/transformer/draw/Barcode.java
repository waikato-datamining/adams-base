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
 * Barcode.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.draw;

import adams.data.barcode.encode.AbstractBarcodeEncoder;
import adams.data.barcode.encode.PassThrough;

import java.awt.image.BufferedImage;

/**
 * <!-- globalinfo-start -->
 * * Draws a barcode with the specified color and dimensions at the given location.
 * * <br><br>
 * <!-- globalinfo-end -->
 * <br><br>
 * <!-- options-start -->
 * * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * * &nbsp;&nbsp;&nbsp;default: WARNING
 * * </pre>
 * * 
 * * <pre>-encoder &lt;adams.data.barcode.encode.AbstractBarcodeEncoder&gt; (property: encoder)
 * * &nbsp;&nbsp;&nbsp;Type of barcode to be drawn.
 * * &nbsp;&nbsp;&nbsp;default: adams.data.barcode.encode.PassThrough
 * * </pre>
 * * 
 * <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class Barcode extends AbstractDrawOperation {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = 5622962107167118784L;

  /**
   * Barcode type to be drawn.
   */
  protected AbstractBarcodeEncoder m_Encoder;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws a barcode with the specified color and dimensions at the given location.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("encoder", "encoder", new PassThrough());
  }

  /**
   * Sets the encoder type.
   *
   * @return encoder type
   */
  public AbstractBarcodeEncoder getEncoder() {
    return m_Encoder;
  }

  /**
   * Gets the encoder type.
   *
   * @param value encoder type
   */
  public void setEncoder(AbstractBarcodeEncoder value) {
    m_Encoder = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String encoderTipText() {
    return "Type of barcode to be drawn.";
  }

  /**
   * Performs the actual draw operation.
   *
   * @param image the image to draw on
   */
  @Override
  protected String doDraw(BufferedImage image) {
    return m_Encoder.doDraw(image);
  }
}
