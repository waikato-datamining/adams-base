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
 * QRCode.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.barcode.encode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Draws a QR code at a specified location and size.
 * <p/>
 <!-- globalinfo-end -->
 * <p/>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;X position of the top-left corner.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;Y position of the top-left corner.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;Width of the barcode in pixels.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;Height of the barcode in pixels.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-margin &lt;int&gt; (property: margin)
 * &nbsp;&nbsp;&nbsp;White margin surrounding the barcode.
 * &nbsp;&nbsp;&nbsp;default: 5
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-text &lt;java.lang.String&gt; (property: text)
 * &nbsp;&nbsp;&nbsp;Text to be encoded.
 * &nbsp;&nbsp;&nbsp;default: foobar
 * </pre>
 * 
 * <pre>-errorCorrectionLevel &lt;L|M|Q|H&gt; (property: errorCorrectionLevel)
 * &nbsp;&nbsp;&nbsp;Error correction level.
 * &nbsp;&nbsp;&nbsp;default: L
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class QRCode extends AbstractBarcodeEncoder {

  /**
   * For serialization
   */
  private static final long serialVersionUID = -108663694502130066L;

  /**
   * Error correction level.
   */
  protected ErrorCorrectionLevel m_ErrorCorrectionLevel;

  /**
   * Text to be encoded.
   */
  protected String m_Text;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws a QR code at a specified location and size.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "text", "text",
      "foobar");

    m_OptionManager.add(
      "errorCorrectionLevel", "errorCorrectionLevel",
      ErrorCorrectionLevel.L);
  }

  /**
   * Returns the text to be encoded in the QR code.
   *
   * @return the text
   */
  public String getText() {
    return m_Text;
  }

  /**
   * Sets the text to be encoded in the QR code.
   *
   * @param value the text
   */
  public void setText(String value) {
    if (value != null && !value.isEmpty()) {
      m_Text = value;
      reset();
    }
    else {
      getLogger().severe("Text must not be null or empty.");
    }
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String textTipText() {
    return "Text to be encoded.";
  }

  /**
   * Returns the error correction level of the QR code.
   *
   * @param value the error correction level
   */
  public void setErrorCorrectionLevel(ErrorCorrectionLevel value) {
    m_ErrorCorrectionLevel = value;
    reset();
  }

  /**
   * Sets the error correction level of the QR code.
   *
   * @return the error correction level
   */
  public ErrorCorrectionLevel getErrorCorrectionLevel() {
    return m_ErrorCorrectionLevel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String errorCorrectionLevelTipText() {
    return "Error correction level.";
  }

  /**
   * Performs the actual draw operation.
   *
   * @param image the image to draw on
   */
  @Override
  protected String doDraw(BufferedImage image) {
    String result = null;

    try {
      QRCodeWriter writer = new QRCodeWriter();
      Map<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.ERROR_CORRECTION, m_ErrorCorrectionLevel);
      hints.put(EncodeHintType.MARGIN, m_Margin);
      BitMatrix matrix = writer.encode(m_Text, BarcodeFormat.QR_CODE, m_Width, m_Height, hints);

      for (int y = m_Y; y < m_Height; y++) {
        for (int x = m_X; x < m_Width; x++)
          image.setRGB(x, y, matrix.get(x, y) ? 0 : 0xFFFFFF);
      }
    }
    catch (WriterException e) {
      result = e.getMessage();
    }

    return result;
  }
}
