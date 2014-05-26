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
 * BinaryCrop.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jai.transformer.crop;

import java.awt.image.BufferedImage;

import adams.data.image.BufferedImageHelper;

/**
 <!-- globalinfo-start -->
 * Turns image into binary (ie black and white) image and determines largest rectangle in the middle to crop to.
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
public class BinaryCrop
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
	"Turns image into binary (ie black and white) image and determines "
	+ "largest rectangle in the middle to crop to.";
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
    BufferedImage	binary;
    int			width;
    int			height;
    int			i;
    int			xMiddle;
    int			yMiddle;
    int			top;
    int			bottom;
    int			left;
    int			right;

    binary    = BufferedImageHelper.convert(img, BufferedImage.TYPE_BYTE_BINARY);
    width     = img.getWidth();
    height    = img.getHeight();
    xMiddle   = width / 2;
    yMiddle   = height / 2;
    
    // from top
    top = 0;
    for (i = 0; i < yMiddle; i++) {
      if (((binary.getRGB(xMiddle, i) >> 0) & 0xFF) > 0) {
	top = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("top: " + top);
    
    // from bottom
    bottom = height - 1;
    for (i = height - 1; i >= yMiddle; i--) {
      if (((binary.getRGB(xMiddle, i) >> 0) & 0xFF) > 0) {
	bottom = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("bottom: " + bottom);
    
    // from left
    left = 0;
    for (i = 0; i < xMiddle; i++) {
      if (((binary.getRGB(i, yMiddle) >> 0) & 0xFF) > 0) {
	left = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("left: " + left);
    
    // from right
    right = width - 1;
    for (i = width - 1; i >= xMiddle; i--) {
      if (((binary.getRGB(i, yMiddle) >> 0) & 0xFF) > 0) {
	right = i;
	break;
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("right: " + right);
    
    // crop original
    image = img.getSubimage(left, top, right - left + 1, bottom - top + 1);

    return image;
  }
}
