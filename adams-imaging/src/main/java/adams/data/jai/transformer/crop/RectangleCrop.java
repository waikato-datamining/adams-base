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
 * RectangleCrop.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jai.transformer.crop;

import java.awt.image.BufferedImage;

import adams.data.image.BufferedImageContainer;
import adams.data.jai.transformer.IndexedColors;

/**
 <!-- globalinfo-start -->
 * Turns the image into one with only two indexed colors and then determines from all four sides the locations where the color changes from the one on the edge. The image is then cropped to this rectangle.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8487 $
 */
public class RectangleCrop
  extends AbstractCropAlgorithm {

  /** for serialization. */
  private static final long serialVersionUID = -696539737461589970L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns the image into one with only two indexed colors and then "
	+ "determines from all four sides the locations where the color "
	+ "changes from the one on the edge. The image is then cropped to "
	+ "this rectangle.";
  }

  /**
   * Performs the actual cropping.
   * 
   * @param img		the image to crop
   * @return		the (potentially) cropped image
   */
  @Override
  protected BufferedImage doCrop(BufferedImage img) {
    BufferedImage		image;
    IndexedColors		indexed;
    BufferedImageContainer	cont;
    int				x;
    int				y;
    int				left;
    int				top;
    int				right;
    int				bottom;
    int				color;
    int				colorNew;
    int				width;
    int				height;
    
    if (isLoggingEnabled())
      getLogger().info("orig.width=" + img.getWidth() + ", orig.height=" + img.getHeight());

    cont    = new BufferedImageContainer();
    cont.setImage(img);
    indexed = new IndexedColors();
    indexed.setNumColors(2);
    image   = indexed.transform(cont)[0].getImage();
    
    // left
    x     = 0;
    y     = image.getHeight() / 2;
    color = image.getRGB(x, y);
    do {
      x++;
      colorNew = image.getRGB(x, y);
      left     = x;
    }
    while ((color == colorNew) && (x < image.getWidth() - 1));
    
    // right
    x     = image.getWidth() - 1;
    y     = image.getHeight() / 2;
    color = image.getRGB(x, y);
    do {
      x--;
      colorNew = image.getRGB(x, y);
      right    = x;
    }
    while ((color == colorNew) && (x > 0));
    
    // top
    x     = image.getWidth() / 2;
    y     = 0;
    color = image.getRGB(x, y);
    do {
      y++;
      colorNew = image.getRGB(x, y);
      top      = y;
    }
    while ((color == colorNew) && (y < image.getHeight() - 1));
    
    // bottom
    x     = image.getWidth() / 2;
    y     = image.getHeight() - 1;
    color = image.getRGB(x, y);
    do {
      y--;
      colorNew = image.getRGB(x, y);
      bottom   = y;
    }
    while ((color == colorNew) && (y > 0));
    
    if (isLoggingEnabled())
      getLogger().info("left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);
    
    width  = right - left + 1;
    height = bottom - top + 1;
    
    if (isLoggingEnabled())
      getLogger().info("width=" + width + ", height=" + height);

    image  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
	image.setRGB(x, y, img.getRGB(left + x, top + y));
      }
    }
    
    return image;
  }
}
