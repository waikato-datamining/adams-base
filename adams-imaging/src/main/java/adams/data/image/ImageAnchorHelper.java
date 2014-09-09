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
 * ImageAnchorHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * Helper class for dealing with the image anchor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageAnchorHelper {

  /**
   * Calculates the actual X value.
   *
   * @param x		the x (0-1: percent of original image width, >1 absolute pixels)
   * @param img		the image to use as basis
   * @return		the actual X
   */
  public static int calculateX(BufferedImage img, double x) {
    int		result;

    if (x <= 1.0)
      result = (int) Math.round(img.getWidth() * x);
    else
      result = (int) x;

    return result;
  }

  /**
   * Calculates the actual Y value.
   *
   * @param y		the y (0-1: percent of original image width, >1 absolute pixels)
   * @param img		the image to use as basis
   * @return		the actual Y
   */
  public static int calculateY(BufferedImage img, double y) {
    int		result;

    if (y <= 1.0)
      result = (int) Math.round(img.getHeight() * y);
    else
      result = (int) y;

    return result;
  }

  /**
   * Calculates the actual top-left (index 0) and bottom-right coordinates (index 1).
   *
   * @param x		the x (0-1: percent of original image width, >1 absolute pixels)
   * @param y		the y (0-1: percent of original image width, >1 absolute pixels)
   * @param width	the width (0-1: percent of original image width, >1 absolute pixels)
   * @param height	the height (0-1: percent of original image width, >1 absolute pixels)
   * @param anchor	the anchor to use
   * @param img		the image to use as basis
   * @return		the top-left and bottom-right coordinates
   */
  public static Point[] calculateCorners(BufferedImage img, ImageAnchor anchor, double x, double y, double width, double height) {
    Point[]		result;
    int			xAct;
    int			yAct;
    int			widthAct;
    int			heightAct;
    int			leftOrig;
    int			topOrig;
    
    xAct      = calculateX(img, x);
    yAct      = calculateY(img, y);
    widthAct  = calculateX(img, width);
    heightAct = calculateY(img, height);
    
    switch (anchor) {
      case TOP_LEFT:
	leftOrig = xAct - 1;
	topOrig  = yAct - 1;
	break;
      case TOP_RIGHT:
	leftOrig = img.getWidth() - widthAct - (xAct - 1);
	topOrig  = yAct - 1;
	break;
      case BOTTOM_LEFT:
	leftOrig = xAct - 1;
	topOrig  = img.getHeight() - heightAct - (yAct - 1);
	break;
      case BOTTOM_RIGHT:
	leftOrig = img.getWidth() - widthAct - (xAct - 1);
	topOrig  = img.getHeight() - heightAct - (yAct - 1);
	break;
      case CENTER:
	leftOrig = img.getWidth() / 2 - (widthAct / 2) - (xAct - 1) / 2;
	topOrig  = img.getHeight() / 2 - (heightAct / 2) - (yAct - 1) / 2;
	break;
      default:
	throw new IllegalStateException("Unhandled anchor: " + anchor);
    }

    result    = new Point[2];
    result[0] = new Point(leftOrig, topOrig);
    result[1] = new Point(leftOrig + widthAct - 1, topOrig + heightAct - 1);
    
    return result;
  }
}
