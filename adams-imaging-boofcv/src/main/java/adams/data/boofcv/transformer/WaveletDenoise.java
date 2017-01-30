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
 * WaveletDenoise.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.abst.denoise.FactoryImageDenoise;
import boofcv.abst.denoise.WaveletDenoiseFilter;
import boofcv.struct.image.ImageFloat32;

/**
 <!-- globalinfo-start -->
 * Removes noise using wavelet transformation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-levels &lt;int&gt; (property: numLevels)
 * &nbsp;&nbsp;&nbsp;The number of levels to use.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    author = "Peter Abeles",
    license = License.APACHE2,
    url = "http://boofcv.org/index.php?title=Example_Wavelet_Noise_Removal",
    note = "Example code taken from this URL"
)
public class WaveletDenoise
  extends AbstractBoofCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -465068613851000709L;
  
  /** the number of levels. */
  protected int m_NumLevels;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes noise using wavelet transformation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-levels", "numLevels",
	    3, 1, null);
  }

  /**
   * Sets the number of levels to use.
   *
   * @param value	the levels
   */
  public void setNumLevels(int value) {
    m_NumLevels = value;
    reset();
  }

  /**
   * Returns the number of levels to use.
   *
   * @return		the levels
   */
  public int getNumLevels() {
    return m_NumLevels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numLevelsTipText() {
    return "The number of levels to use.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BoofCVImageContainer[] doTransform(BoofCVImageContainer img) {
    BoofCVImageContainer[]		result;
    ImageFloat32 			input;
    ImageFloat32 			denoised;
    WaveletDenoiseFilter<ImageFloat32> 	denoiser;
    
    input    = (ImageFloat32) BoofCVHelper.toBoofCVImage(img.getImage(), BoofCVImageType.FLOAT_32);
    denoised = new ImageFloat32(input.width,input.height);
    denoiser = FactoryImageDenoise.waveletBayes(ImageFloat32.class, m_NumLevels, 0, 255);

    denoiser.process(input, denoised);

    result    = new BoofCVImageContainer[1];
    result[0] = (BoofCVImageContainer) img.getHeader();
    result[0].setImage(denoised);
    
    return result;
  }
}
