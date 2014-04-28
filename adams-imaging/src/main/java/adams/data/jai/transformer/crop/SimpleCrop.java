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

/**
 * SimpleCrop.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jai.transformer.crop;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Simple cropping algorithm that uses a fixed window. The user either specifies a window height&#47;width (if both non-zero) or the bottom-right corner coordinates apart from the coordinates of the top-left corner.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-top &lt;int&gt; (property: top)
 * &nbsp;&nbsp;&nbsp;The y position of the top-left corner.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-left &lt;int&gt; (property: left)
 * &nbsp;&nbsp;&nbsp;The x position of the top-left corner.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-bottom &lt;int&gt; (property: bottom)
 * &nbsp;&nbsp;&nbsp;The y position of the bottom-right corner; use -1 to use image height.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-right &lt;int&gt; (property: right)
 * &nbsp;&nbsp;&nbsp;The x position of the bottom-right corner; use -1 to use image width.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the crop rectangle; ignored if less than 1.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the crop rectangle, ignored if less than 1.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8487 $
 */
public class SimpleCrop
  extends AbstractCropAlgorithm {

  /** for serialization. */
  private static final long serialVersionUID = -696539737461589970L;

  /** the y of the top-left corner. */
  protected int m_Top;

  /** the x of the top-left corner. */
  protected int m_Left;

  /** the y of the bottom-right corner. */
  protected int m_Bottom;

  /** the x of the bottom-right corner. */
  protected int m_Right;

  /** the height of the window. */
  protected int m_Height;

  /** the width of the window. */
  protected int m_Width;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Simple cropping algorithm that uses a fixed window. The user either "
	+ "specifies a window height/width (if both non-zero) or the bottom-right corner "
	+ "coordinates apart from the coordinates of the top-left corner.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "top", "top",
	    0, 0, null);

    m_OptionManager.add(
	    "left", "left",
	    0, 0, null);

    m_OptionManager.add(
	    "bottom", "bottom",
	    -1, -1, null);

    m_OptionManager.add(
	    "right", "right",
	    -1, -1, null);

    m_OptionManager.add(
	    "height", "height",
	    0, 0, null);

    m_OptionManager.add(
	    "width", "width",
	    0, 0, null);
  }

  /**
   * Sets the y of the top-left corner.
   *
   * @param value 	the y
   */
  public void setTop(int value) {
    if (value >= 0) {
      m_Top = value;
      reset();
    }
    else {
      getLogger().warning("Top must be >= 0, provided: " + value);
    }
  }

  /**
   * Returns the y of the top-left corner.
   *
   * @return 		the y
   */
  public int getTop() {
    return m_Top;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String topTipText() {
    return "The y position of the top-left corner.";
  }

  /**
   * Sets the x of the top-left corner.
   *
   * @param value 	the x
   */
  public void setLeft(int value) {
    if (value >= 0) {
      m_Left = value;
      reset();
    }
    else {
      getLogger().warning("Left must be >= 0, provided: " + value);
    }
  }

  /**
   * Returns the x of the top-left corner.
   *
   * @return 		the x
   */
  public int getLeft() {
    return m_Left;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String leftTipText() {
    return "The x position of the top-left corner.";
  }

  /**
   * Sets the y of the bottom-right corner.
   *
   * @param value 	the y
   */
  public void setBottom(int value) {
    if ((value > 0) || (value == -1)) {
      m_Bottom = value;
      reset();
    }
    else {
      getLogger().warning("Bottom must be > 0 or -1 (for image height), provided: " + value);
    }
  }

  /**
   * Returns the y of the bottom-right corner.
   *
   * @return 		the y
   */
  public int getBottom() {
    return m_Bottom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bottomTipText() {
    return "The y position of the bottom-right corner; use -1 to use image height.";
  }

  /**
   * Sets the x of the bottom-right corner.
   *
   * @param value 	the x
   */
  public void setRight(int value) {
    if ((value > 0) || (value == -1)) {
      m_Right = value;
      reset();
    }
    else {
      getLogger().warning("Right must be > 0 or -1 (for image width), provided: " + value);
    }
  }

  /**
   * Returns the x of the bottom-right corner.
   *
   * @return 		the x
   */
  public int getRight() {
    return m_Right;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rightTipText() {
    return "The x position of the bottom-right corner; use -1 to use image width.";
  }

  /**
   * Sets the height of the crop rectangle.
   *
   * @param value 	the height, ignored if less than 1
   */
  public void setHeight(int value) {
    if (value >= 0) {
      m_Height = value;
      reset();
    }
    else {
      getLogger().warning("Height must be >= 0, provided: " + value);
    }
  }

  /**
   * Returns the height of the crop rectangle.
   *
   * @return 		the height, ignored if less than 1
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the crop rectangle; ignored if less than 1.";
  }

  /**
   * Sets the width of the crop rectangle.
   *
   * @param value 	the width, ignored if less than 1
   */
  public void setWidth(int value) {
    if (value >= 0) {
      m_Width = value;
      reset();
    }
    else {
      getLogger().warning("Width must be >= 0, provided: " + value);
    }
  }

  /**
   * Returns the width of the window.
   *
   * @return 		the width, ignored if less than 1
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the crop rectangle, ignored if less than 1.";
  }

  /**
   * Performs the actual cropping.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  @Override
  protected BufferedImage doCrop(BufferedImage img) {
    BufferedImage	image;
    int			width;
    int			height;
    int			right;
    int			bottom;
    int			x;
    int			y;
    
    if ((m_Width > 0) && (m_Height > 0)) {
      if (m_Width == -1)
	width = img.getWidth();
      else
	width = m_Width;
      if (m_Height == -1)
	height = img.getHeight();
      else
	height = m_Height;
    }
    else {
      if (m_Right == -1)
	right = img.getWidth();
      else
	right = m_Right;
      if (m_Bottom == -1)
	bottom = img.getHeight();
      else
	bottom = m_Bottom;
      width  = right  - m_Left;
      height = bottom - m_Top;
    }
    
    if (isLoggingEnabled())
      getLogger().info("left=" + m_Left + ", top=" + m_Top + ", width=" + width + ", height=" + height);

    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
	image.setRGB(x, y, img.getRGB(m_Left + x, m_Top + y));
      }
    }
    
    return image;
  }
}
