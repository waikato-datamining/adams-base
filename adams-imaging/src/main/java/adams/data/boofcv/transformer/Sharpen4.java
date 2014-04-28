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
 * Sharpen4.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.data.boofcv.BoofCVImageContainer;
import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;

/**
 <!-- globalinfo-start -->
 * Applies a Laplacian-4 based sharpen filter to the image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class Sharpen4
  extends AbstractBoofCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -465068613851000709L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies a Laplacian-4 based sharpen filter to the image.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BoofCVImageContainer[] doTransform(BoofCVImageContainer img) {
    BoofCVImageContainer[]	result;
    ImageFloat32		input;
    ImageFloat32		output;
    
    input  = ConvertBufferedImage.convertFromSingle(img.toBufferedImage(), null, ImageFloat32.class);
    output = new ImageFloat32(input.width, input.height);

    EnhanceImageOps.sharpen4(input, output);

    result    = new BoofCVImageContainer[1];
    result[0] = (BoofCVImageContainer) img.getHeader();
    result[0].setImage(output);
    
    return result;
  }
}
