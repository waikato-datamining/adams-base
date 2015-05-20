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
 * AbstractBarcodeDecoder.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.barcode.decode;

import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;
import adams.data.text.TextContainer;

/**
 * Ancestor for Barcode decoders.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBarcodeDecoder
  extends AbstractOptionHandler {

  /**
   * Performs checks on the image.
   * <br><br>
   * Default implementation just ensures that an image is present.
   *
   * @param image the image to check
   */
  protected void check(AbstractImageContainer image) {
    if (image == null)
      throw new IllegalStateException("No image container provided!");
    if (image.getImage() == null)
      throw new IllegalStateException("No image provided!");
  }

  /**
   * Performs the actual decoding.
   *
   * @param image the image to extract the barcode from
   * @return a TextContainer with the decoded barcode text and (optional) meta-data
   */
  protected abstract TextContainer doDecode(AbstractImageContainer image);

  /**
   * Attempts to decode the barcode in the image.
   *
   * @param image the image to extract the barcode from
   * @return a TextContainer with the decoded barcode text and (optional) meta-data
   */
  public TextContainer decode(AbstractImageContainer image) {
    check(image);
    return doDecode(image);
  }
}
