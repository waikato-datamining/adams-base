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
 * Otsu.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.features;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.opencv.ColorConversionCode;
import adams.data.opencv.OpenCVImageContainer;
import adams.data.opencv.ThresholdType;
import adams.data.report.DataType;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.threshold;

/**
 <!-- globalinfo-start -->
 * Computes the threshold using Otsu's method
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The (optional) prefix to use for the feature names.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-max-value &lt;int&gt; (property: maxValue)
 * &nbsp;&nbsp;&nbsp;The max value to use.
 * &nbsp;&nbsp;&nbsp;default: 255
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Otsu
    extends AbstractOpenCVFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** the max value. */
  protected int m_MaxValue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes the threshold using Otsu's method";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "max-value", "maxValue",
        255, 0, 255);
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
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(OpenCVImageContainer img) {
    HeaderDefinition		result;

    result = new HeaderDefinition();
    result.add("Otsu", DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(OpenCVImageContainer img) {
    List<Object>[]	result;
    Mat 		mat;
    double		computed;
    ColorConversionCode conversion;

    result    = new List[1];
    result[0] = new ArrayList<>();

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

    computed = threshold(mat, mat, 0, m_MaxValue, ThresholdType.THRESH_OTSU.getType());
    result[0].add(computed);

    return result;
  }
}
