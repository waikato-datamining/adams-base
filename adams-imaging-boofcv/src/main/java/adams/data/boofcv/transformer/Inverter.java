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
 * Inverter.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.struct.image.GrayU8;

/**
 <!-- globalinfo-start -->
 * Takes a binary image in the BoofCV Unsigned Int 8 format and inverts each pixel. If the image is notbinary behaviour is undefined.
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
 * @author sjb90
 * @version $Revision$
 */
public class Inverter
  extends AbstractBoofCVTransformer {

  private static final long serialVersionUID = 8829671646134900700L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Takes a binary image in the BoofCV Unsigned Int 8 format and inverts each pixel. If the image is not" +
      "binary behaviour is undefined.";
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img the image to transform (can be modified, since it is a copy)
   * @return the generated image(s)
   */
  @Override
  protected BoofCVImageContainer[] doTransform(BoofCVImageContainer img) {
    GrayU8 image = (GrayU8) BoofCVHelper.toBoofCVImage(img, BoofCVImageType.UNSIGNED_INT_8);
    BoofCVImageContainer[] result = new BoofCVImageContainer[1];
    byte[] data = image.getData();
    for (int i = 0; i < data.length; i++) {
	data[i] = data[i] == (byte)1 ? (byte)-1 : (byte)1;
    }
    image.setData(data);
    result[0] = new BoofCVImageContainer();
    result[0].setImage(image);
    return result;
  }
}
