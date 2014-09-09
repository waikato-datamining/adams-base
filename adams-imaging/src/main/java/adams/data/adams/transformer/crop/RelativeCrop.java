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
 * RelativeCrop.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.adams.transformer.crop;

import java.awt.Point;
import java.awt.image.BufferedImage;

import adams.data.image.ImageAnchor;

/**
 <!-- globalinfo-start -->
 * Crops the image to specified width and height. Where the crop rectangle starts is defined by the X and Y position and the anchor.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-x &lt;double&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The horizontal pixel position (0-1: percent; &gt;1: pixels).
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-y &lt;double&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The vertical pixel position (0-1: percent; &gt;1: pixels).
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-width &lt;double&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the crop rectangle (0-1: percent; &gt;1: pixels).
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-5
 * </pre>
 * 
 * <pre>-height &lt;double&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the crop rectangle (0-1: percent; &gt;1: pixels).
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-5
 * </pre>
 * 
 * <pre>-anchor &lt;TOP_LEFT|TOP_RIGHT|CENTER|BOTTOM_LEFT|BOTTOM_RIGHT&gt; (property: anchor)
 * &nbsp;&nbsp;&nbsp;Defines where to anchor the position on the crop rectangle.
 * &nbsp;&nbsp;&nbsp;default: TOP_LEFT
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8950 $
 */
public class RelativeCrop
  extends AbstractCropAlgorithm {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the X position of the crop rectangle. */
  protected double m_X;
  
  /** the Y position of the crop rectangle. */
  protected double m_Y;
  
  /** the width of the crop rectangle. */
  protected double m_Width;
  
  /** the height of the crop rectangle. */
  protected double m_Height;
  
  /** where to anchor the position on the rectangle. */
  protected ImageAnchor m_Anchor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Crops the image to specified width and height. Where the crop "
	+ "rectangle starts is defined by the X and Y position and the anchor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"x", "X",
	0.0, 0.0, null);

    m_OptionManager.add(
	"y", "Y",
	0.0, 0.0, null);

    m_OptionManager.add(
	"width", "width",
	1.0, 0.00001, null);

    m_OptionManager.add(
	"height", "height",
	1.0, 0.00001, null);

    m_OptionManager.add(
	"anchor", "anchor",
	ImageAnchor.TOP_LEFT);
  }

  /**
   * Sets the X position (0-1: percent; >1: pixels).
   *
   * @param value	the position
   */
  public void setX(double value) {
    if (value >= 0) {
      m_X = value;
      reset();
    }
    else {
      getLogger().severe("X has to be >=0, provided: " + value);
    }
  }

  /**
   * Returns the X position (0-1: percent; >1: pixels).
   *
   * @return		the position
   */
  public double getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String XTipText() {
    return "The horizontal pixel position (0-1: percent; >1: pixels).";
  }

  /**
   * Sets the Y position (0-1: percent; >1: pixels).
   *
   * @param value	the position
   */
  public void setY(double value) {
    if (value >= 0) {
      m_Y = value;
      reset();
    }
    else {
      getLogger().severe("Y has to be >=0, provided: " + value);
    }
  }

  /**
   * Returns the Y position (0-1: percent; >1: pixels).
   *
   * @return		the position
   */
  public double getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String YTipText() {
    return "The vertical pixel position (0-1: percent; >1: pixels).";
  }

  /**
   * Sets the width of the crop rectangle (0-1: percent; >1: pixels).
   *
   * @param value	the width
   */
  public void setWidth(double value) {
    if (value > 0) {
      m_Width = value;
      reset();
    }
    else {
      getLogger().severe("Width has to be >0, provided: " + value);
    }
  }

  /**
   * Returns the width of the crop rectangle (0-1: percent; >1: pixels).
   *
   * @return		the width
   */
  public double getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String widthTipText() {
    return "The width of the crop rectangle (0-1: percent; >1: pixels).";
  }

  /**
   * Sets the height of the crop rectangle (0-1: percent; >1: pixels).
   *
   * @param value	the height
   */
  public void setHeight(double value) {
    if (value > 0) {
      m_Height = value;
      reset();
    }
    else {
      getLogger().severe("Height has to be >0, provided: " + value);
    }
  }

  /**
   * Returns the height of the crop rectangle (0-1: percent; >1: pixels).
   *
   * @return		the height
   */
  public double getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String heightTipText() {
    return "The height of the crop rectangle (0-1: percent; >1: pixels).";
  }

  /**
   * Sets where to anchor the position on the rectangle.
   *
   * @param value	the anchor
   */
  public void setAnchor(ImageAnchor value) {
    m_Anchor = value;
    reset();
  }

  /**
   * Returns where to anchor the position on the rectangle.
   *
   * @return		the anchor
   */
  public ImageAnchor getAnchor() {
    return m_Anchor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String anchorTipText() {
    return "Defines where to anchor the position on the crop rectangle.";
  }

  /**
   * Performs the actual cropping.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  @Override
  protected BufferedImage doCrop(BufferedImage img) {
    BufferedImage	result;
    int			x;
    int			y;
    int			width;
    int			height;
    int			leftOrig;
    int			topOrig;
    int			heightOrig;
    int			widthOrig;
    int			xOrig;
    int			yOrig;
    
    // calculate absolute values
    if (m_Width <= 1.0)
      width = (int) Math.round(img.getWidth() * m_Width);
    else
      width = (int) m_Width;
    if (m_Height <= 1.0)
      height = (int) Math.round(img.getHeight() * m_Height);
    else
      height = (int) m_Height;
    if (m_X <= 1.0)
      x = (int) Math.round(img.getWidth() * m_X);
    else
      x = (int) m_X;
    if (m_Y <= 1.0)
      y = (int) Math.round(img.getHeight() * m_Y);
    else
      y = (int) m_Y;
    
    // generate cropped image
    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    switch (m_Anchor) {
      case TOP_LEFT:
	leftOrig = x - 1;
	topOrig  = y - 1;
	break;
      case TOP_RIGHT:
	leftOrig = x - 1 - width;
	topOrig  = y - 1;
	break;
      case BOTTOM_LEFT:
	leftOrig = x - 1;
	topOrig  = y - 1 - height;
	break;
      case BOTTOM_RIGHT:
	leftOrig = x - 1 - width;
	topOrig  = y - 1 - height;
	break;
      case CENTER:
	leftOrig = x - 1 - width / 2;
	topOrig  = y - 1 - height / 2;
	break;
      default:
	throw new IllegalStateException("Unhandled anchor: " + m_Anchor);
    }

    if (isLoggingEnabled()) {
      getLogger().info("x=" + (x - 1) + ", y=" + (y - 1) + ", width=" + width + ", height=" + height + ", anchor=" + m_Anchor);
      getLogger().info("  --> " + "leftOrig=" + leftOrig + ", topOrig=" + topOrig);
    }

    m_TopLeft     = new Point(leftOrig, topOrig);
    m_BottomRight = new Point(leftOrig + width - 1, topOrig + height - 1);

    heightOrig = img.getHeight();
    widthOrig  = img.getWidth();
    
    for (y = 0; y < height; y++) {
      yOrig = topOrig + y;
      if ((yOrig < 0) || (yOrig >= heightOrig))
	continue;
      for (x = 0; x < width; x++) {
	xOrig = leftOrig + x;
	if ((xOrig < 0) || (xOrig >= widthOrig))
	  continue;
	result.setRGB(x, y, img.getRGB(xOrig, yOrig));
      }
    }
    
    return result;
  }
}
