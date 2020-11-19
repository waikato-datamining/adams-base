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
 * ImageUtils.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Helper class for image related operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageUtils {

  /**
   * Transfers all the non-black pixels from the source image into the target one.
   *
   * @param source	the source image
   * @param target	the target image
   */
  public static void combineImages(BufferedImage source, BufferedImage target) {
    int[] 	pixSource;
    int[] 	pixTarget;
    int		i;
    int		black;
    boolean	modified;

    if (source.getWidth() != target.getWidth())
      throw new IllegalArgumentException("Images differ in width: " + source.getWidth() + " != " + target.getWidth());
    if (source.getHeight() != target.getHeight())
      throw new IllegalArgumentException("Images differ in height: " + source.getHeight() + " != " + target.getHeight());

    pixSource = source.getRGB(0, 0, source.getWidth(), source.getHeight(), null, 0, source.getWidth());
    pixTarget = target.getRGB(0, 0, target.getWidth(), target.getHeight(), null, 0, target.getWidth());
    black     = Color.BLACK.getRGB();
    modified  = false;
    for (i = 0; i < pixSource.length; i++) {
      if (pixSource[i] != black) {
        if (pixSource[i] != pixTarget[i]) {
	  pixTarget[i] = pixSource[i];
	  modified     = true;
	}
      }
    }
    if (modified)
      target.setRGB(0, 0, target.getWidth(), target.getHeight(), pixTarget, 0, target.getWidth());
  }

  /**
   * Replaces the old color with the new one.
   *
   * @param image 	the image to process
   * @param oldColor 	the old color to replace
   * @param newColor 	the replacement color
   */
  public static void replaceColor(BufferedImage image, Color oldColor, Color newColor) {
    int		oldC;
    int		newC;
    int[]	pixels;
    int		i;
    boolean	modified;

    oldC     = oldColor.getRGB();
    newC     = newColor.getRGB();
    pixels   = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    modified = false;
    for (i = 0; i < pixels.length; i++) {
      if (pixels[i] == oldC) {
	pixels[i] = newC;
	modified  = true;
      }
    }
    if (modified)
      image.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
  }

  /**
   * Replaces any non-black pixels with the specified color.
   *
   * @param image 	the image to process
   * @param color 	the new color for non-black pixels
   */
  public static void initImage(BufferedImage image, Color color) {
    int 	black;
    int 	rgb;
    int[]	pixels;
    int		i;
    boolean	modified;

    black    = Color.BLACK.getRGB();
    rgb      = color.getRGB();
    pixels   = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    modified = false;
    for (i = 0; i < pixels.length; i++) {
      if (pixels[i] != black) {
	pixels[i] = rgb;
	modified  = true;
      }
    }
    if (modified)
      image.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
  }

  /**
   * Creates a new image with just black pixels.
   *
   * @param width 	the width of the image
   * @param height 	the height of the image
   */
  public static BufferedImage newImage(int width, int height) {
    BufferedImage 	result;
    int 		black;
    int[]		pixels;
    int			i;

    black  = Color.BLACK.getRGB();
    pixels = new int[width*height];
    for (i = 0; i < pixels.length; i++)
      pixels[i] = black;

    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    result.setRGB(0, 0, result.getWidth(), result.getHeight(), pixels, 0, result.getWidth());

    return result;
  }

  /**
   * Checks whether the pixel requires replacement.
   * If target is not null, then the pixel must match that RGB value.
   * If target is null, then the pixel must be difference from the replacement RGB value.
   *
   * @param pixel	the pixel to check
   * @param target	the target RGB to replace, can be null
   * @param replacement	the replacement RGB to use
   * @return		true if  pixel requires replacing
   */
  protected static boolean requiresReplacement(int pixel, Integer target, int replacement) {
    if (target == null)
      return (pixel != replacement);
    else
      return (pixel == target);
  }

  /**
   * Performs flood-fill on the provided image.
   * Based on pseudo code from here:
   * https://en.wikipedia.org/wiki/Flood_fill#Alternative_implementations
   *
   * @param image		the image to update
   * @param n			the starting point
   * @param targetColor		the color to replace, can be null to apply to any color
   * @param replacementColor	the replacement color
   */
  public static void fill(BufferedImage image, Point n, Color targetColor, Color replacementColor) {
    Integer 		target;
    int			replacement;
    LinkedList<Point> queue;
    int			x;
    int			y;
    int			w;
    int			h;

    if (targetColor == null)
      target = null;
    else
      target = targetColor.getRGB();
    replacement = replacementColor.getRGB();
    if ((target != null) && (target == replacement))
      return;

    x = (int) n.getX();
    y = (int) n.getY();
    if ((target != null) && image.getRGB(x, y) != target)
      return;

    image.setRGB(x, y, replacement);
    w     = image.getWidth();
    h     = image.getHeight();
    queue = new LinkedList<>();
    queue.add(n);

    while (!queue.isEmpty()) {
      n = queue.removeFirst();
      x = (int) n.getX();
      y = (int) n.getY();
      // west
      if ((x > 0) && (requiresReplacement(image.getRGB(x - 1, y), target, replacement))) {
	image.setRGB(x - 1, y, replacement);
	queue.add(new Point(x - 1, y));
      }
      // east
      if ((x < w - 1) && (requiresReplacement(image.getRGB(x + 1, y), target, replacement))) {
	image.setRGB(x + 1, y, replacement);
	queue.add(new Point(x + 1, y));
      }
      // north
      if ((y > 0) && (requiresReplacement(image.getRGB(x, y - 1), target, replacement))) {
	image.setRGB(x, y - 1, replacement);
	queue.add(new Point(x, y - 1));
      }
      // south
      if ((y < h - 1) && (requiresReplacement(image.getRGB(x, y + 1), target, replacement))) {
	image.setRGB(x, y + 1, replacement);
	queue.add(new Point(x, y + 1));
      }
    }
  }
}
