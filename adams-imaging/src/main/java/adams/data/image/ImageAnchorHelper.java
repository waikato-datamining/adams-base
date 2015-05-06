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
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
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
   * @param width	the width to use as basis
   * @return		the actual X
   */
  public static int calculateX(int width, double x) {
    int		result;

    if (x <= 1.0)
      result = (int) Math.round(width * x);
    else
      result = (int) x;

    return result;
  }

  /**
   * Calculates the actual Y value.
   *
   * @param y		the y (0-1: percent of original image width, >1 absolute pixels)
   * @param height	the height to use as basis
   * @return		the actual Y
   */
  public static int calculateY(int height, double y) {
    int		result;

    if (y <= 1.0)
      result = (int) Math.round(height * y);
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
   * @param useAnchorAtPos  whether to use the anchor at the position rather than the image
   * @return		the top-left and bottom-right coordinates
   */
  public static Point[] calculateCorners(BufferedImage img, ImageAnchor anchor, double x, double y, double width, double height, boolean useAnchorAtPos) {
    return calculateCorners(img.getWidth(), img.getHeight(), anchor, x, y, width, height, useAnchorAtPos);
  }

  /**
   * Calculates the actual top-left (index 0) and bottom-right coordinates (index 1).
   *
   * @param x		the x (0-1: percent of original image width, >1 absolute pixels)
   * @param y		the y (0-1: percent of original image width, >1 absolute pixels)
   * @param width	the width (0-1: percent of original image width, >1 absolute pixels)
   * @param height	the height (0-1: percent of original image width, >1 absolute pixels)
   * @param anchor	the anchor to use
   * @param imgWidth	the image width to use as basis
   * @param imgHeight	the image height to use as basis
   * @param useAnchorAtPos  whether to use the anchor at the position rather than the image
   * @return		the top-left and bottom-right coordinates
   */
  public static Point[] calculateCorners(int imgWidth, int imgHeight, ImageAnchor anchor, double x, double y, double width, double height, boolean useAnchorAtPos) {
    Point[]		result;
    int			xAct;
    int			yAct;
    int			widthAct;
    int			heightAct;
    int			leftOrig;
    int			topOrig;
    int			top;
    int			middle;
    int			bottom;
    int			left;
    int			center;
    int			right;
    
    xAct      = calculateX(imgWidth, x);
    yAct      = calculateY(imgHeight, y);
    widthAct  = calculateX(imgWidth, width);
    heightAct = calculateY(imgHeight, height);

    if (useAnchorAtPos) {
      // Y
      top    = yAct;
      middle = yAct - heightAct / 2;
      bottom = yAct + heightAct - 1;
      // X
      left   = xAct;
      center = xAct - widthAct / 2;
      right  = xAct + widthAct - 1;
    }
    else {
      // Y
      top    = yAct;
      middle = imgHeight / 2 - (heightAct / 2) - yAct / 2;
      bottom = imgHeight - heightAct - yAct;
      // X
      left   = xAct;
      center = imgWidth / 2 - (widthAct / 2) - xAct / 2;
      right  = imgWidth - widthAct - xAct;
    }
    
    switch (anchor) {
      case TOP_LEFT:
	leftOrig = left;
	topOrig  = top;
	break;
      case TOP_CENTER:
	leftOrig = center;
	topOrig  = top;
	break;
      case TOP_RIGHT:
	leftOrig = right;
	topOrig  = top;
	break;
      case MIDDLE_LEFT:
	leftOrig = left;
	topOrig  = middle;
	break;
      case MIDDLE_CENTER:
	leftOrig = center;
	topOrig  = middle;
	break;
      case MIDDLE_RIGHT:
	leftOrig = right;
	topOrig  = middle;
	break;
      case BOTTOM_LEFT:
	leftOrig = left;
	topOrig  = bottom;
	break;
      case BOTTOM_CENTER:
	leftOrig = center;
	topOrig  = bottom;
	break;
      case BOTTOM_RIGHT:
	leftOrig = right;
	topOrig  = bottom;
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
