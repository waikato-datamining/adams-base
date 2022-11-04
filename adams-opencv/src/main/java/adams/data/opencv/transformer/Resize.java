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
 * Resize.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.transformer;

import adams.core.QuickInfoHelper;
import adams.data.opencv.InterpolationType;
import adams.data.opencv.OpenCVImageContainer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;

/**
 <!-- globalinfo-start -->
 * Resizes the image, either using absolute width&#47;height or factors for x&#47;y.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-type &lt;ABSOLUTE|FACTORS&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The resize type.
 * &nbsp;&nbsp;&nbsp;default: FACTORS
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The absolute width to use, -1 uses input width.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The absolute height to use, -1 uses input height.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-factor-x &lt;double&gt; (property: factorX)
 * &nbsp;&nbsp;&nbsp;The factor to apply to the width.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-factor-y &lt;double&gt; (property: factorY)
 * &nbsp;&nbsp;&nbsp;The factor to apply to the height.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-interpolation &lt;CV_INTER_NN|CV_INTER_LINEAR|CV_INTER_CUBIC|CV_INTER_AREA|CV_INTER_LANCZOS4&gt; (property: interpolation)
 * &nbsp;&nbsp;&nbsp;The interpolation type.
 * &nbsp;&nbsp;&nbsp;default: CV_INTER_LINEAR
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Resize
    extends AbstractOpenCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /**
   * How to resize.
   */
  public enum ResizeType {
    ABSOLUTE,
    FACTORS,
  }

  /** the resize type. */
  protected ResizeType m_Type;

  /** the absolute width. */
  protected int m_Width;

  /** the absolute height. */
  protected int m_Height;

  /** the x factor. */
  protected double m_FactorX;

  /** the y factor. */
  protected double m_FactorY;

  /** the interpolation type. */
  protected InterpolationType m_Interpolation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Resizes the image, either using absolute width/height or factors for x/y.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"type", "type",
	ResizeType.FACTORS);

    m_OptionManager.add(
	"width", "width",
	-1, -1, null);

    m_OptionManager.add(
	"height", "height",
	-1, -1, null);

    m_OptionManager.add(
	"factor-x", "factorX",
	1.0, 0.0, null);

    m_OptionManager.add(
	"factor-y", "factorY",
	1.0, 0.0, null);

    m_OptionManager.add(
	"interpolation", "interpolation",
	InterpolationType.CV_INTER_LINEAR);
  }

  /**
   * Sets the resize type.
   *
   * @param value	the type
   */
  public void setType(ResizeType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the resize type.
   *
   * @return		the type
   */
  public ResizeType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String typeTipText() {
    return "The resize type.";
  }

  /**
   * Sets the absolute width.
   *
   * @param value	the width (-1 to use input width)
   */
  public void setWidth(int value) {
    if (getOptionManager().isValid("width", value)) {
      m_Width = value;
      reset();
    }
  }

  /**
   * Returns the absolute width.
   *
   * @return		the width (-1 to use input width)
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String widthTipText() {
    return "The absolute width to use, -1 uses input width.";
  }

  /**
   * Sets the absolute height.
   *
   * @param value	the height (-1 to use input height)
   */
  public void setHeight(int value) {
    if (getOptionManager().isValid("height", value)) {
      m_Height = value;
      reset();
    }
  }

  /**
   * Returns the absolute height.
   *
   * @return		the height (-1 to use input height)
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String heightTipText() {
    return "The absolute height to use, -1 uses input height.";
  }

  /**
   * Sets the X factor.
   *
   * @param value	the factor
   */
  public void setFactorX(double value) {
    if (getOptionManager().isValid("factorX", value)) {
      m_FactorX = value;
      reset();
    }
  }

  /**
   * Returns the X factor.
   *
   * @return		the factor
   */
  public double getFactorX() {
    return m_FactorX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String factorXTipText() {
    return "The factor to apply to the width.";
  }

  /**
   * Sets the Y factor.
   *
   * @param value	the factor
   */
  public void setFactorY(double value) {
    if (getOptionManager().isValid("factorY", value)) {
      m_FactorY = value;
      reset();
    }
  }

  /**
   * Returns the Y factor.
   *
   * @return		the factor
   */
  public double getFactorY() {
    return m_FactorY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String factorYTipText() {
    return "The factor to apply to the height.";
  }

  /**
   * Sets the interpolation type.
   *
   * @param value	the type
   */
  public void setInterpolation(InterpolationType value) {
    m_Interpolation = value;
    reset();
  }

  /**
   * Returns the interpolation type.
   *
   * @return		the type
   */
  public InterpolationType getInterpolation() {
    return m_Interpolation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String interpolationTipText() {
    return "The interpolation type.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = null;

    if (m_Type == ResizeType.ABSOLUTE) {
      result = QuickInfoHelper.toString(this, "width", m_Width, "width: ");
      result += QuickInfoHelper.toString(this, "height", m_Height, ", height: ");
    }
    else if (m_Type == ResizeType.FACTORS) {
      result = QuickInfoHelper.toString(this, "factorX", m_FactorX, "x: ");
      result += QuickInfoHelper.toString(this, "factorY", m_FactorY, ", y: ");
    }

    return result;
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
    int				width;
    int				height;

    mat = img.getContent().clone();
    switch (m_Type) {
      case ABSOLUTE:
	width  = (m_Width == -1 ? mat.cols() : m_Width);
	height = (m_Height == -1 ? mat.rows() : m_Height);
	resize(mat, mat, new Size(width, height), 0, 0, m_Interpolation.getType());
	break;

      case FACTORS:
	resize(mat, mat, new Size(), m_FactorX, m_FactorY, m_Interpolation.getType());
	break;

      default:
	throw new IllegalStateException("Unhandled resize type: " + m_Type);
    }

    result = new OpenCVImageContainer[1];
    result[0] = (OpenCVImageContainer) img.getHeader();
    result[0].setContent(mat);

    return result;
  }
}
