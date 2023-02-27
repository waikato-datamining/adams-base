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
 * AbstractImageToByteArray.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.MessageCollection;
import adams.data.image.BufferedImageHelper;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Converts an image into a byte array.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageToByteArray
  extends AbstractConversion {

  private static final long serialVersionUID = 4452018959514672473L;

  /** the format to use. */
  protected String m_Format;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "format", "format",
      "JPG");
  }

  /**
   * Sets the image format to use.
   *
   * @param value	the format
   */
  public void setFormat(String value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the image format to use.
   *
   * @return 		the format
   */
  public String getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The image format to use, e.g., JPG or PNG.";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return byte[].class;
  }

  /**
   * Turns the input into a BufferedImage.
   *
   * @param input	the input to convert
   * @return		the buffered image
   */
  protected abstract BufferedImage toBufferedImage(Object input);

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    byte[]		result;
    MessageCollection	errors;

    errors = new MessageCollection();
    result = BufferedImageHelper.toBytes(toBufferedImage(m_Input), m_Format, errors);
    if (result == null) {
      if (errors.isEmpty())
	throw new IOException("Failed to convert image to byte array!");
      else
	throw new IOException("Failed to convert image to byte array: " + errors);
    }
    else {
      return result;
    }
  }
}
