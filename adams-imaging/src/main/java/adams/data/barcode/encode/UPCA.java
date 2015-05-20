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
 * UPCA.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.barcode.encode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.UPCAWriter;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Draws a UPCA barcode at a specified location and size.Digits must be 11 (checksum gets calculated) or 12 (incl. checksum)
 * <br><br>
 <!-- globalinfo-end -->
 * <br><br>
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
 * <pre>-digits &lt;java.lang.String&gt; (property: digits)
 * &nbsp;&nbsp;&nbsp;Digits to be encoded.
 * &nbsp;&nbsp;&nbsp;default: 012345678905
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class UPCA extends AbstractBarcodeEncoder {

  /**
   * For serialization
   */
  private static final long serialVersionUID = -5065848369849665652L;

  /**
   * Digits to be encoded.
   */
  protected String m_Digits;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Draws a UPCA barcode at a specified location and size."
        + "Digits must be 11 (checksum gets calculated) or 12 (incl. checksum)";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "digits", "digits",
      "012345678905");
  }

  /**
   * Returns the digits to be encoded in the QR code.
   *
   * @return the digits
   */
  public String getDigits() {
    return m_Digits;
  }

  /**
   * Checks the given 12 digit code.
   * See also <a href="https://en.wikipedia.org/wiki/Universal_Product_Code#Check_digits"
   * target="_blank">Universal Product Code</a>.
   *
   * @param code     the code to check
   * @return        null if valid, otherwise error message
   */
  protected String isValidCode(String code) {
    int       sum;
    int       i;
    int       c;
    int       check;

    if ((code == null) || code.isEmpty())
      return "Digits must not be null or empty.";
    if (!code.matches("\\d+"))
      return "Value must not contain non-numeric characters, provided: " + code;
    // do we compute checksum automatically?
    if (code.length() == 11)
      return null;
    if (code.length() != 12)
      return "11 or 12 digits must be present: " + code + " (=" + code.length() + ")";

    sum = 0;
    for (i = 0; i < 11; i++) {
      c = Integer.parseInt(code.substring(i, i+1));
      sum += c * ((i % 2 == 0) ? 1 : 3);
    }
    check = 10 - (sum % 10);
    if (check != Integer.parseInt(code.substring(11, 12)))
      return "Checksum digits differ: expected=" + check + ", found=" + code.substring(11, 12);

    return null;
  }

  /**
   * Sets the digits to be encoded in the QR code.
   *
   * @param value the digits
   */
  public void setDigits(String value) {
    String      check;

    check = isValidCode(value);

    if (check == null) {
      m_Digits = value;
      reset();
    }
    else {
      getLogger().severe(check);
    }
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String digitsTipText() {
    return "Digits to be encoded.";
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
      UPCAWriter writer = new UPCAWriter();
      Map<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.MARGIN, m_Margin);
      BitMatrix matrix = writer.encode(m_Digits, BarcodeFormat.UPC_A, m_Width, m_Height, hints);

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
