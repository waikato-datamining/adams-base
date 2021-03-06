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
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer.crop;

import adams.data.image.ImageAnchor;
import adams.data.image.ImageAnchorHelper;

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Crops the image to specified width and height. Where the crop rectangle starts is defined by the X and Y position and the anchor.
 * <br><br>
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
 * <pre>-anchor &lt;TOP_LEFT|TOP_CENTER|TOP_RIGHT|MIDDLE_LEFT|MIDDLE_CENTER|MIDDLE_RIGHT|BOTTOM_LEFT|BOTTOM_CENTER|BOTTOM_RIGHT&gt; (property: anchor)
 * &nbsp;&nbsp;&nbsp;Defines where to anchor the position on the crop rectangle.
 * &nbsp;&nbsp;&nbsp;default: TOP_LEFT
 * </pre>
 * 
 * <pre>-use-anchor-at-pos &lt;boolean&gt; (property: useAnchorAtPos)
 * &nbsp;&nbsp;&nbsp;If enabled, the anchor gets placed at the x&#47;y positiion and the corners 
 * &nbsp;&nbsp;&nbsp;of the crop are calculated accordingly; eg 'MIDDLE_CENTER' then refers to 
 * &nbsp;&nbsp;&nbsp;the center of the crop rectangle, with the center located at x&#47;y.
 * &nbsp;&nbsp;&nbsp;default: false
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

  /** whether to use the anchor at the specified to position rather than using image. */
  protected boolean m_UseAnchorAtPos;

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

    m_OptionManager.add(
	"use-anchor-at-pos", "useAnchorAtPos",
	false);
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
   * Sets whether to place the anchor at the position or use it relative to image.
   *
   * @param value	true if anchor at pos
   */
  public void setUseAnchorAtPos(boolean value) {
    m_UseAnchorAtPos = value;
    reset();
  }

  /**
   * Returns whether to place the anchor at the position or use it relative to image.
   *
   * @return		true if anchor at pos
   */
  public boolean getUseAnchorAtPos() {
    return m_UseAnchorAtPos;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String useAnchorAtPosTipText() {
    return
      "If enabled, the anchor gets placed at the x/y positiion and the "
        + "corners of the crop are calculated accordingly; "
        + "eg '" + ImageAnchor.MIDDLE_CENTER + "' then refers to the center "
        + "of the crop rectangle, with the center located at x/y.";
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
    Point[]		corners;
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

    corners       = ImageAnchorHelper.calculateCorners(img, m_Anchor, m_X, m_Y, m_Width, m_Height, m_UseAnchorAtPos);
    m_TopLeft     = corners[0];
    m_BottomRight = corners[1];
    leftOrig      = (int) m_TopLeft.getX();
    topOrig       = (int) m_TopLeft.getY();
    width         = (int) (m_BottomRight.getX() - m_TopLeft.getX() + 1);
    height        = (int) (m_BottomRight.getY() - m_TopLeft.getY() + 1);

    if (isLoggingEnabled()) {
      getLogger().info("x=" + m_X + ", y=" + m_Y + ", width=" + m_Width + ", height=" + m_Height + ", anchor=" + m_Anchor);
      getLogger().info("  --> " + "top-left=" + m_TopLeft + ", bottom-right=" + m_BottomRight);
    }

    heightOrig = img.getHeight();
    widthOrig  = img.getWidth();

    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
