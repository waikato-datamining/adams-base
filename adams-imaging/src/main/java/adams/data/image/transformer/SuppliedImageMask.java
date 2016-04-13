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
 * SuppliedImageMask.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.image.XScreenMaskHelper;
import adams.flow.control.StorageName;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Masks out regions on the image using a supplied image from internal storage, making them transparent.
 * <br><br>
 <!-- globalinfo-end -->
 <br><br>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-mask &lt;adams.flow.control.StorageName&gt; (property: mask)
 * &nbsp;&nbsp;&nbsp;The name of the storage name (image) to use as mask.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 * <pre>-down &lt;boolean&gt; (property: down)
 * &nbsp;&nbsp;&nbsp;If true, then pixels &lt;= threshold are not masked and the others' alpha channel 
 * &nbsp;&nbsp;&nbsp;are set to 0 (made transparent).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-threshold &lt;int&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;Threshold value used for binarization, specify -1 to automatically determine 
 * &nbsp;&nbsp;&nbsp;a threshold.
 * &nbsp;&nbsp;&nbsp;default: 127
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SuppliedImageMask
  extends AbstractBufferedImageTransformer {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = -922292531841315436L;

  /** the image in internal storage. */
  protected StorageName m_Mask;

  /**
   * If true, then pixels <= threshold are not masked and the others' alpha channel are set to 0 (made transparent).
   */
  protected boolean m_Down;

  /**
   * Threshold value used for binarization, specify -1 to automatically determine a threshold.
   */
  protected int m_Threshold;

  /** the mask image. */
  protected int[][] m_MaskMatrix;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Masks out regions on the image using a supplied image from internal "
	+ "storage, making them transparent.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("mask", "mask", new StorageName());
    m_OptionManager.add("down", "down", true);
    m_OptionManager.add("threshold", "threshold", 127, -1, 255);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_MaskMatrix = null;
  }

  /**
   * Gets the storage name of the mask image.
   *
   * @return the storage name
   */
  public StorageName getMask() {
    return m_Mask;
  }

  /**
   * Sets storage name of the mask image.
   *
   * @param value the storage name
   */
  public void setMask(StorageName value) {
    m_Mask = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String maskTipText() {
    return "The name of the storage name (image) to use as mask.";
  }

  /**
   * Get whether mask is applied below or above the threshold.
   *
   * @return threshold direction
   */
  public boolean getDown() {
    return m_Down;
  }

  /**
   * Set whether mask is applied below or above the threshold.
   *
   * @param value threshold direction
   */
  public void setDown(boolean value) {
    m_Down = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String downTipText() {
    return "If true, then pixels <= threshold are not masked and the others' alpha channel are set to 0 (made transparent).";
  }

  /**
   * Get the manual threshold value.
   *
   * @return manual threshold value
   */
  public int getThreshold() {
    return m_Threshold;
  }

  /**
   * Set the manual threshold value.
   *
   * @param value manual threshold value
   */
  public void setThreshold(int value) {
    if (value >= -1 && value <= 255) {
      m_Threshold = value;
      reset();
    } else
      getLogger().severe("Threshold must be 0 >= value <= 255, or -1 for auto-thresholding, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "Threshold value used for binarization, specify -1 to automatically determine a threshold.";
  }

  /**
   * Initializes the mask if necessary.
   */
  protected void initMask() {
    BufferedImage 	maskImg;
    Object		obj;
    float[][] 		mask;
    float 		min;
    float 		max;
    int			x;
    int			y;
    float 		ratio;

    if (this.m_MaskMatrix != null)
      return;

    if (getFlowContext() == null)
      throw new IllegalStateException("No flow context set!");

    obj = getFlowContext().getStorageHandler().getStorage().get(m_Mask);
    if (obj == null)
      throw new IllegalStateException("Mask not available from storage: " + m_Mask);

    if (obj instanceof BufferedImage)
      maskImg = (BufferedImage) obj;
    else if (obj instanceof AbstractImageContainer)
      maskImg = ((AbstractImageContainer) obj).toBufferedImage();
    else
      throw new IllegalStateException(
	"Mask must be either a " + BufferedImage.class.getName()
	  + " or derived from " + AbstractImageContainer.class.getName()
	  + ", found: " + obj.getClass().getName());

    mask = new float[maskImg.getHeight()][maskImg.getWidth()];
    min = Float.POSITIVE_INFINITY;
    max = Float.NEGATIVE_INFINITY;
    for (y = 0; y < maskImg.getHeight(); y++) {
      for (x = 0; x < maskImg.getWidth(); x++) {
        mask[y][x] = maskImg.getRGB(x, y);
	max        = Math.max(max, mask[y][x]);
	min        = Math.min(min, mask[y][x]);
      }
    }

    m_MaskMatrix = new int[maskImg.getHeight()][maskImg.getWidth()];
    ratio = 255 / (max - min);
    for (y = 0; y < mask.length; y++) {
      for (x = 0; x < mask[0].length; x++) {
	m_MaskMatrix[y][x] = Math.round((mask[y][x] - min) * ratio);
      }
    }

    XScreenMaskHelper.binarizeMask(m_MaskMatrix, m_Threshold, m_Down, getLogger());
  }

  /**
   * Masks the image.
   *
   * @param container the image to be processed
   * @return the masked image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer container) {
    BufferedImage 	image;

    initMask();

    image = container.getImage();
    image = XScreenMaskHelper.applyMask(image, m_MaskMatrix, m_Threshold, m_Down, getLogger());
    container.setImage(image);
    return new BufferedImageContainer[]{container};
  }
}
