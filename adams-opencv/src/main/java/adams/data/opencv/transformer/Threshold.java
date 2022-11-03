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
 * Threshold.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.transformer;

import adams.data.opencv.ColorConversionCode;
import adams.data.opencv.OpenCVImageContainer;
import adams.data.opencv.ThresholdType;
import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.threshold;

/**
 <!-- globalinfo-start -->
 * Generates a binary image using the specified image. Automatically converts the image to grayscale.<br>
 * For more information see:<br>
 * https:&#47;&#47;docs.opencv.org&#47;4.6.0&#47;d7&#47;d1b&#47;group__imgproc__misc.html#gaa9e58d2860d4afa658ef70a9b1115576
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-threshold &lt;int&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold to apply to the gray pixel values.
 * &nbsp;&nbsp;&nbsp;default: 127
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 *
 * <pre>-max-value &lt;int&gt; (property: maxValue)
 * &nbsp;&nbsp;&nbsp;The max value to use.
 * &nbsp;&nbsp;&nbsp;default: 255
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 *
 * <pre>-type &lt;THRESH_BINARY|THRESH_BINARY_INV|THRESH_TRUNC|THRESH_TOZERO|THRESH_TOZERO_INV|THRESH_MASK|THRESH_OTSU|THRESH_TRIANGLE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of thresholding to apply.
 * &nbsp;&nbsp;&nbsp;default: THRESH_BINARY
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Threshold
    extends AbstractOpenCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the threshold to use. */
  protected int m_Threshold;

  /** the max value. */
  protected int m_MaxValue;

  /** the threshold type. */
  protected ThresholdType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a binary image using the specified image. Automatically converts the image to grayscale.\n"
        + "For more information see:\n"
        + "https://docs.opencv.org/4.6.0/d7/d1b/group__imgproc__misc.html#gaa9e58d2860d4afa658ef70a9b1115576";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "threshold", "threshold",
        127, 0, 255);

    m_OptionManager.add(
        "max-value", "maxValue",
        255, 0, 255);

    m_OptionManager.add(
        "type", "type",
        ThresholdType.THRESH_BINARY);
  }

  /**
   * Sets the threshold.
   *
   * @param value	the threshold
   */
  public void setThreshold(int value) {
    if (getOptionManager().isValid("threshold", value)) {
      m_Threshold = value;
      reset();
    }
  }

  /**
   * Returns the threshold.
   *
   * @return		the threshold
   */
  public int getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String thresholdTipText() {
    return "The threshold to apply to the gray pixel values.";
  }

  /**
   * Sets the max value.
   *
   * @param value	the max value
   */
  public void setMaxValue(int value) {
    if (getOptionManager().isValid("maxValue", value)) {
      m_MaxValue = value;
      reset();
    }
  }

  /**
   * Returns the max value.
   *
   * @return		the max value
   */
  public int getMaxValue() {
    return m_MaxValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxValueTipText() {
    return "The max value to use.";
  }

  /**
   * Sets the type.
   *
   * @param value	the type
   */
  public void setType(ThresholdType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type.
   *
   * @return		the type
   */
  public ThresholdType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String typeTipText() {
    return "The type of thresholding to apply.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected OpenCVImageContainer[] doTransform(OpenCVImageContainer img) {
    OpenCVImageContainer[]	result;
    Mat				mat;
    double			computed;
    ColorConversionCode 	conversion;

    mat = img.getContent().clone();

    conversion = null;
    if (mat.channels() == 3)
      conversion = ColorConversionCode.COLOR_BGR2GRAY;
    else if (mat.channels() == 4)
      conversion = ColorConversionCode.COLOR_BGRA2GRAY;

    if (conversion != null) {
      getLogger().warning("Image not in grayscale, attempting conversion: " + conversion);
      cvtColor(mat, mat, conversion.getCode());
    }

    computed = threshold(mat, mat, m_Threshold, m_MaxValue, m_Type.getType());

    result = new OpenCVImageContainer[1];
    result[0] = (OpenCVImageContainer) img.getHeader();
    result[0].setContent(mat);
    result[0].getReport().setNumericValue("Threshold", computed);

    return result;
  }
}
