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
 * GaussianBlur.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.transformer;

import adams.core.QuickInfoHelper;
import adams.data.opencv.BorderType;
import adams.data.opencv.OpenCVImageContainer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;

/**
 <!-- globalinfo-start -->
 * Applies Gaussian blur to the image.<br>
 * For more information see:<br>
 * https:&#47;&#47;docs.opencv.org&#47;4.6.0&#47;d4&#47;d86&#47;group__imgproc__filter.html#gaabe8c836e97159a9193fb0b11ac52cf1
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-kernel-width &lt;int&gt; (property: kernelWidth)
 * &nbsp;&nbsp;&nbsp;The width to use for the kernel (positive and odd integer).
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-kernel-height &lt;int&gt; (property: kernelHeight)
 * &nbsp;&nbsp;&nbsp;The height to use for the kernel (positive and odd integer).
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-sigma-x &lt;double&gt; (property: sigmaX)
 * &nbsp;&nbsp;&nbsp;The standard deviation for X.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-sigma-y &lt;double&gt; (property: sigmaY)
 * &nbsp;&nbsp;&nbsp;The standard deviation for Y.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-border-type &lt;BORDER_CONSTANT|BORDER_REPLICATE|BORDER_REFLECT|BORDER_WRAP|BORDER_REFLECT_101|BORDER_TRANSPARENT|BORDER_REFLECT101|BORDER_DEFAULT|BORDER_ISOLATED&gt; (property: borderType)
 * &nbsp;&nbsp;&nbsp;The type of border to use.
 * &nbsp;&nbsp;&nbsp;default: BORDER_DEFAULT
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GaussianBlur
    extends AbstractOpenCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the kernel width. */
  protected int m_KernelWidth;

  /** the kernel height. */
  protected int m_KernelHeight;

  /** sigma X (standard deviation for X). */
  protected double m_SigmaX;

  /** sigma Y (standard deviation for X). */
  protected double m_SigmaY;

  /** the border type. */
  protected BorderType m_BorderType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies Gaussian blur to the image.\n"
	+ "For more information see:\n"
	+ "https://docs.opencv.org/4.6.0/d4/d86/group__imgproc__filter.html#gaabe8c836e97159a9193fb0b11ac52cf1";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"kernel-width", "kernelWidth",
	3, 1, null);

    m_OptionManager.add(
	"kernel-height", "kernelHeight",
	3, 1, null);

    m_OptionManager.add(
	"sigma-x", "sigmaX",
	0.0, 0.0, null);

    m_OptionManager.add(
	"sigma-y", "sigmaY",
	0.0, 0.0, null);

    m_OptionManager.add(
	"border-type", "borderType",
	BorderType.BORDER_DEFAULT);
  }

  /**
   * Sets the kernel width.
   *
   * @param value	the width
   */
  public void setKernelWidth(int value) {
    if (getOptionManager().isValid("kernelWidth", value)) {
      m_KernelWidth = value;
      reset();
    }
  }

  /**
   * Returns the kernel width.
   *
   * @return		the width
   */
  public int getKernelWidth() {
    return m_KernelWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String kernelWidthTipText() {
    return "The width to use for the kernel (positive and odd integer).";
  }

  /**
   * Sets the kernel height.
   *
   * @param value	the height
   */
  public void setKernelHeight(int value) {
    if (getOptionManager().isValid("kernelHeight", value)) {
      m_KernelHeight = value;
      reset();
    }
  }

  /**
   * Returns the kernel height.
   *
   * @return		the height
   */
  public int getKernelHeight() {
    return m_KernelHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String kernelHeightTipText() {
    return "The height to use for the kernel (positive and odd integer).";
  }

  /**
   * Sets the standard deviation for X.
   *
   * @param value	the standard deviation
   */
  public void setSigmaX(double value) {
    if (getOptionManager().isValid("sigmaX", value)) {
      m_SigmaX = value;
      reset();
    }
  }

  /**
   * Returns the standard deviation for X.
   *
   * @return		the standard deviation
   */
  public double getSigmaX() {
    return m_SigmaX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String sigmaXTipText() {
    return "The standard deviation for X.";
  }

  /**
   * Sets the standard deviation for Y.
   *
   * @param value	the standard deviation
   */
  public void setSigmaY(double value) {
    if (getOptionManager().isValid("sigmaY", value)) {
      m_SigmaY = value;
      reset();
    }
  }

  /**
   * Returns the standard deviation for Y.
   *
   * @return		the standard deviation
   */
  public double getSigmaY() {
    return m_SigmaY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String sigmaYTipText() {
    return "The standard deviation for Y.";
  }

  /**
   * Sets the type of border to use.
   *
   * @param value	the type
   */
  public void setBorderType(BorderType value) {
    m_BorderType = value;
    reset();
  }

  /**
   * Returns the type of border to use.
   *
   * @return		the type
   */
  public BorderType getBorderType() {
    return m_BorderType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String borderTypeTipText() {
    return "The type of border to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "width", m_KernelWidth, "w: ");
    result += QuickInfoHelper.toString(this, "height", m_KernelHeight, ", h: ");
    result += QuickInfoHelper.toString(this, "sigmaX", m_SigmaX, ", sx: ");
    result += QuickInfoHelper.toString(this, "sigmaY", m_SigmaY, ", sy: ");

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

    mat = img.getContent().clone();
    GaussianBlur(mat, mat, new Size(m_KernelWidth, m_KernelHeight), m_SigmaX, m_SigmaY, m_BorderType.getType());

    result = new OpenCVImageContainer[1];
    result[0] = (OpenCVImageContainer) img.getHeader();
    result[0].setContent(mat);

    return result;
  }
}
