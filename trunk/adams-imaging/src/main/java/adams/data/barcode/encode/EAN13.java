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
 * EAN13.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.barcode.encode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- globalinfo-start -->
 * * Draws an EAN13 barcode at a specified location and size.Digits must be 13 characters long. A valid checksum is enforced.
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
 * * <pre>-digits &lt;java.lang.String&gt; (property: digits)
 * * &nbsp;&nbsp;&nbsp;Digits to be encoded.
 * * &nbsp;&nbsp;&nbsp;default: 0123456789012
 * * </pre>
 * * 
 * <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class EAN13 extends AbstractBarcodeEncoder {

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
    return "Draws an EAN13 barcode at a specified location and size." +
        "Digits must be 13 characters long. A valid checksum is enforced.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "digits", "digits",
      "0123456789012");
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
   * Checks the given 13 digit code.
   * See also <a href="https://en.wikipedia.org/wiki/International_Article_Number_%28EAN%29#Calculation"
   * target="_blank">International Article Number (EAN)</a>.
   *
   * @param ean     the code to check
   * @return        null if valid, otherwise error message
   */
  protected String isValidCode(String ean) {
    int       sum;
    int       i;
    int       c;
    int       check;

    if ((ean == null) || ean.isEmpty())
      return "Digits must not be null or empty.";
    if (!ean.matches("\\d+"))
      return "Value must not contain non-numeric characters, provided: " + ean;
    if (ean.length() != 13)
      return "13 digits must be present: " + ean + " (=" + ean.length() + ")";

    sum = 0;
    for (i = 0; i < 12; i++) {
      c = Integer.parseInt(ean.substring(i, i+1));
      sum += c * ((i % 2 == 0) ? 1 : 3);
    }
    check = sum;
    while (check > 0)
      check -= 10;
    check = Math.abs(check);
    if (check != Integer.parseInt(ean.substring(12, 13)))
      return "Checksum digits differ: expected=" + check + ", found=" + ean.substring(12, 13);

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
      EAN13Writer writer = new EAN13Writer();
      Map<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.MARGIN, m_Margin);
      BitMatrix matrix = writer.encode(m_Digits, BarcodeFormat.EAN_13, m_Width, m_Height, hints);

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
