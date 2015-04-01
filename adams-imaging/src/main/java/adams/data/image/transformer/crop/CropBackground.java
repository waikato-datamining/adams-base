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
 * CropBackground.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.crop;

import adams.data.image.ImageAnchor;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Picks the background at the specified anchor position and crops to the smallest possible rectangle.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-anchor &lt;TOP_LEFT|TOP_CENTER|TOP_RIGHT|MIDDLE_LEFT|MIDDLE_CENTER|MIDDLE_RIGHT|BOTTOM_LEFT|BOTTOM_CENTER|BOTTOM_RIGHT&gt; (property: anchor)
 * &nbsp;&nbsp;&nbsp;Defines where to pick the background color.
 * &nbsp;&nbsp;&nbsp;default: TOP_LEFT
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6652 $
 */
public class CropBackground
  extends AbstractCropAlgorithm {

  /** for serialization. */
  private static final long serialVersionUID = -696539737461589970L;

  /** where to pick the background color. */
  protected ImageAnchor m_Anchor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Picks the background at the specified anchor position and crops to the smallest possible rectangle.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"anchor", "anchor",
	ImageAnchor.TOP_LEFT);
  }

  /**
   * Sets where to pick the background color.
   *
   * @param value	the anchor
   */
  public void setAnchor(ImageAnchor value) {
    m_Anchor = value;
    reset();
  }

  /**
   * Returns where to pick the background color.
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
    return "Defines where to pick the background color.";
  }

  /**
   * Scans the images to find the closest x/y to the center that has the same
   * color as the background. Automatically detects if to scan backwards.
   *
   * @param img         the image to scan
   * @param from        the first index (incl)
   * @param to          the second index (incl)
   * @param horizontal  whether to scan row by row or column by column
   * @param background  the background color to match
   * @return            the x or y; -1 denotes that no closer position was located
   */
  protected int scan(BufferedImage img, int from, int to, boolean horizontal, int background) {
    int     result;
    int       inc;
    int       x;
    int       y;
    int       color;

    result = -1;

    if (from < to)
      inc = 1;
    else
      inc = -1;

    if (horizontal) {
      y = from;
      while (true) {
        for (x = 0; x < img.getWidth(); x++) {
          color = img.getRGB(x, y) & 0x00FFFFFF;
          if (color != background) {
            result = y;
            return result;
          }
        }
        y += inc;
        if (y == to + inc)
          break;
      }
    }
    else {
      x = from;
      while (true) {
        for (y = 0; y < img.getHeight(); y++) {
          color = img.getRGB(x, y) & 0x00FFFFFF;
          if (color != background) {
            result = x;
            return result;
          }
        }
        x += inc;
        if (x == to + inc)
          break;
      }
    }

    return result;
  }

  /**
   * Simply returns the original image.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  @Override
  protected BufferedImage doCrop(BufferedImage img) {
    BufferedImage   result;
    int             x;
    int             y;
    int             background;
    int             top;
    int             left;
    int             bottom;
    int             right;
    int             pos;

    // interpret anchor
    switch (m_Anchor) {
      case TOP_LEFT:
	x = 0;
	y = 0;
	break;
      case TOP_CENTER:
	x = img.getWidth() / 2 - 1;
	y = 0;
	break;
      case TOP_RIGHT:
	x = img.getWidth() - 1;
	y = 0;
	break;
      case MIDDLE_LEFT:
	x = 0;
	y = img.getHeight() / 2 - 1;
	break;
      case MIDDLE_CENTER:
	x = img.getWidth() / 2 - 1;
	y = img.getHeight() / 2 - 1;
	break;
      case MIDDLE_RIGHT:
	x = img.getWidth() - 1;
	y = img.getHeight() / 2 - 1;
	break;
      case BOTTOM_LEFT:
	x = 0;
	y = img.getHeight() - 1;
	break;
      case BOTTOM_CENTER:
	x = img.getWidth() / 2 - 1;
	y = img.getHeight() - 1;
	break;
      case BOTTOM_RIGHT:
	x = img.getWidth() - 1;
	y = img.getHeight() - 1;
	break;
      default:
	throw new IllegalStateException("Unhandled anchor: " + m_Anchor);
    }

    // get color
    background = img.getRGB(x, y) & 0x00FFFFFF;
    if (isLoggingEnabled())
      getLogger().info("Background: " + background + " (" + new Color(background) + ")");

    // top->bottom
    pos = scan(img, 0, img.getHeight() - 1, true, background);
    if (pos > -1)
      top = pos;
    else
      top = 0;

    // bottom->top
    pos = scan(img, img.getHeight() - 1, 0, true, background);
    if (pos > -1)
      bottom = pos;
    else
      bottom = img.getHeight() - 1;

    // left->right
    pos = scan(img, 0, img.getWidth() - 1, false, background);
    if (pos > -1)
      left = pos;
    else
      left = 0;

    // right->left
    pos = scan(img, img.getWidth() - 1, 0, false, background);
    if (pos > -1)
      right = pos;
    else
      right = img.getWidth() - 1;
    if (isLoggingEnabled())
      getLogger().info("top=" + top + ", left=" + left + ", bottom=" + bottom + ", right=" + right);

    result = img.getSubimage(left, top, right - left + 1, bottom - top + 1);

    return result;
  }
}
