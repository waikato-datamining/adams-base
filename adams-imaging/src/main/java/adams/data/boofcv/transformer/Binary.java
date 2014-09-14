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
 * <pre>-threshold-type &lt;MANUAL|MEAN&gt; (property: thresholdType)
 * &nbsp;&nbsp;&nbsp;The type of threshold to apply.
 * &nbsp;&nbsp;&nbsp;default: MANUAL
 * </pre>
 *
 * <pre>-threshold &lt;float&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The manual threshold to use.
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

  /**
   * The treshold type to apply.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ThresholdType {
    /** manually supplied threshold. */
    MANUAL,
    /** using the mean. */
    MEAN,
    /** using adaptive gaussian. */
    //ADAPTIVE_GAUSSIAN  TODO upgrade BoofCV
  }

  /** the type of threshold to apply. */
  protected ThresholdType m_ThresholdType;

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
	    "threshold-type", "thresholdType",
	    ThresholdType.MANUAL);

    m_OptionManager.add(
	    "threshold", "threshold",
	    0.0f);

    m_OptionManager.add(
	    "remove-small-blobs", "removeSmallBlobs",
	    false);
  }

  /**
   * Sets the type of threshold to apply.
   *
   * @param value	the type
   */
  public void setThresholdType(ThresholdType value) {
    m_ThresholdType = value;
    reset();
  }

  /**
   * Returns the type of threshold to apply.
   *
   * @return		the type
   */
  public ThresholdType getThresholdType() {
    return m_ThresholdType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String thresholdTypeTipText() {
    return "The type of threshold to apply.";
  }

  /**
   * Sets the manual threshold to use.
   *
   * @param value	the threshold to use
   */
  public void setThreshold(float value) {
    m_Threshold = value;
    reset();
  }

  /**
   * Returns the manual threshold to use.
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
    return "The manual threshold to use.";
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

    switch (m_ThresholdType) {
      case MANUAL:
	ThresholdImageOps.threshold(input, binary, m_Threshold, true);
	break;
      case MEAN:
	threshold = ImageStatistics.mean(input);
	getLogger().info("mean: " + threshold);
	ThresholdImageOps.threshold(input, binary, (float) threshold, true);
	break;
      default:
	throw new IllegalStateException("Unhandled threshold type: " + m_ThresholdType);
    }

    if (m_RemoveSmallBlobs) {
      filtered = BinaryImageOps.erode8(binary, 1, null);
      filtered = BinaryImageOps.dilate8(filtered, 1, null);
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
