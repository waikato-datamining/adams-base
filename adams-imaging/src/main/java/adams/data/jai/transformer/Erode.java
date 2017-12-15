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
 * Erode.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import adams.data.image.BufferedImageContainer;

import javax.media.jai.KernelJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ErodeDescriptor;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Erode
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the kernel (blank separated list of floats). */
  protected float[] m_Kernel;

  /** the kernel height. */
  protected int m_KernelHeight;

  /** the kernel width. */
  protected int m_KernelWidth;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Performs erosion.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "kernel", "kernel",
      "0 1 0 1 1 1 0 1 0");

    m_OptionManager.add(
      "kernel-width", "kernelWidth",
      3, 1, null);

    m_OptionManager.add(
      "kernel-height", "kernelHeight",
      3, 1, null);
  }

  /**
   * Sets the kernel (blank-separated list of floats, row-wise).
   *
   * @param value	the kernel
   */
  public void setKernel(String value) {
    String[]	values;
    int		i;

    if (value.trim().isEmpty()) {
      m_Kernel = new float[0];
    }
    else {
      values = value.split(" ");
      m_Kernel = new float[values.length];
      for (i = 0; i < values.length; i++)
        m_Kernel[i] = Float.parseFloat(values[i]);
    }
    reset();
  }

  /**
   * Returns the kernel (blank-separated list of floats, row-wise).
   *
   * @return		the kernel
   */
  public String getKernel() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < m_Kernel.length; i++) {
      if (i > 0)
        result.append(" ");
      result.append(Float.toString(m_Kernel[i]));
    }

    return result.toString();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String kernelTipText() {
    return "The kernel to use; blank-separated floats; row-wise.";
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
   * Returns the width of the kernel.
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
    return "The width of the kernel.";
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
   * Returns the height of the kernel.
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
    return "The height of the kernel.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    KernelJAI 			kernel;
    RenderedOp 			op;

    result    = new BufferedImageContainer[1];
    kernel    = new KernelJAI(m_KernelWidth, m_KernelHeight, m_Kernel);
    op        = ErodeDescriptor.create(img.getImage(), kernel, null);
    result[0] = (BufferedImageContainer) img.getHeader();
    result[0].setImage(op.getAsBufferedImage());

    return result;
  }
}
