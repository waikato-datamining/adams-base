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
 * Binary.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.core.BoofCVHelper;
import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Creates a binary image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-use-mean-threshold &lt;boolean&gt; (property: useMeanThreshold)
 * &nbsp;&nbsp;&nbsp;If enabled, the mean is used rather than the fixed threshold value.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-threshold &lt;float&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold to use.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-remove-small-blobs &lt;boolean&gt; (property: removeSmallBlobs)
 * &nbsp;&nbsp;&nbsp;If enabled, small blobs are removed using erode8&#47;dilate8.
 * &nbsp;&nbsp;&nbsp;default: false
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
    url = "http://boofcv.org/index.php?title=Tutorial_Binary_Image",
    note = "Example code taken from this URL"
)
public class Binary
  extends AbstractBoofCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -465068613851000709L;

  /** whether to use the mean as threshold. */
  protected boolean m_UseMeanThreshold;
  
  /** the threshold to use. */
  protected float m_Threshold;
  
  /** whether to remove small blobs. */
  protected boolean m_RemoveSmallBlobs;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a binary image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-mean-threshold", "useMeanThreshold",
	    false);

    m_OptionManager.add(
	    "threshold", "threshold",
	    0.0f);

    m_OptionManager.add(
	    "remove-small-blobs", "removeSmallBlobs",
	    false);
  }

  /**
   * Sets whether to use the mean as threshold rather than fixed value.
   *
   * @param value	true if to use mean
   */
  public void setUseMeanThreshold(boolean value) {
    m_UseMeanThreshold = value;
    reset();
  }

  /**
   * Returns whether to use the mean as threshold rather than fixed value.
   *
   * @return		true if mean is used
   */
  public boolean getUseMeanThreshold() {
    return m_UseMeanThreshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String useMeanThresholdTipText() {
    return "If enabled, the mean is used rather than the fixed threshold value.";
  }

  /**
   * Sets the threshold to use.
   *
   * @param value	the threshold to use
   */
  public void setThreshold(float value) {
    m_Threshold = value;
    reset();
  }

  /**
   * Returns the threshold to use.
   *
   * @return		the threshold in use
   */
  public float getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String thresholdTipText() {
    return "The threshold to use.";
  }

  /**
   * Sets whether to remove small blobs using erode8/dilate8.
   *
   * @param value	true if to remove blobs
   */
  public void setRemoveSmallBlobs(boolean value) {
    m_RemoveSmallBlobs = value;
    reset();
  }

  /**
   * Returns whether to remove small blobs using erode8/dilate8.
   *
   * @return		true if blobs removed
   */
  public boolean getRemoveSmallBlobs() {
    return m_RemoveSmallBlobs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String removeSmallBlobsTipText() {
    return "If enabled, small blobs are removed using erode8/dilate8.";
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
    ImageFloat32 		input;
    ImageUInt8 			binary;
    double 			threshold;
    ImageUInt8 			filtered;
    
    input  = (ImageFloat32) BoofCVHelper.toBoofCVImage(img.getImage(), BoofCVImageType.FLOAT_32);
    binary = new ImageUInt8(input.width,input.height);

    if (m_UseMeanThreshold) {
      threshold = ImageStatistics.mean(input);
      getLogger().info("mean: " + threshold);
    }
    else {
      threshold = m_Threshold;
    }

    ThresholdImageOps.threshold(input, binary, (float) threshold, true);

    if (m_RemoveSmallBlobs) {
      filtered = BinaryImageOps.erode8(binary,null);
      filtered = BinaryImageOps.dilate8(filtered, null);
    }
    else {
      filtered = binary;
    }

    result    = new BoofCVImageContainer[1];
    result[0] = (BoofCVImageContainer) img.getHeader();
    result[0].setImage(filtered);
    
    return result;
  }
}
