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
 * HistogramBasedIntensityAdjustmentGray8.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.core.BoofCVHelper;
import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Histogram adjustment algorithms aim to spread out pixel intensity values (of gray-scale image) uniformly across the allowed range.
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
@MixedCopyright(
    author = "Peter Abeles",
    license = License.APACHE2,
    url = "http://boofcv.org/index.php?title=Example_Image_Enhancement",
    note = "Example code taken from this URL"
)
public class HistogramBasedIntensityAdjustmentGray8
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
    return 
	"Histogram adjustment algorithms aim to spread out pixel intensity "
	+ "values (of gray-scale image) uniformly across the allowed range.";
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
    ImageUInt8			gray;
    ImageUInt8			adjusted;
    int[] 			histogram;
    int[] 			transform;

    gray     = (ImageUInt8) BoofCVHelper.toBoofCVImage(img.getImage(), BoofCVImageType.UNSIGNED_INT_8);
    adjusted = new ImageUInt8(gray.width, gray.height);

    histogram = new int[256];
    transform = new int[256];

    ImageStatistics.histogram(gray, histogram);
    EnhanceImageOps.equalize(histogram, transform);
    EnhanceImageOps.applyTransform(gray, transform, adjusted);    

    result    = new BoofCVImageContainer[1];
    result[0] = (BoofCVImageContainer) img.getHeader();
    result[0].setImage(adjusted);
    
    return result;
  }
}
