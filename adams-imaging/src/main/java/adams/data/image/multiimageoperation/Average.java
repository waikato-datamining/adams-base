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
 * Average.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.multiimageoperation;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.statistics.StatUtils;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Computes the average from the images (each channel separately).
 * <br><br>
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
 * @version $Revision$
 */
public class Average
  extends AbstractBufferedImageMultiImageOperation {

  private static final long serialVersionUID = 7381673951864996785L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Computes the average from the images (each channel separately).";
  }

  /**
   * Returns the minimum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumImagesRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no upper limit
   */
  public int maxNumImagesRequired() {
    return -1;
  }

  /**
   * Checks the images.
   *
   * @param images	the images to check
   */
  @Override
  protected void check(BufferedImageContainer[] images) {
    String	msg;

    super.check(images);

    msg = checkSameDimensions(images);
    if (msg != null)
      throw new IllegalStateException(msg);
  }

  /**
   * Performs the actual processing of the images.
   *
   * @param images	the images to process
   * @return		the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doProcess(BufferedImageContainer[] images) {
    BufferedImageContainer[]	result;
    BufferedImage		output;
    int				x;
    int				y;
    int				i;
    int				n;
    int[][] 			channels;
    int[]			val;
    int[]			split;

    result   = new BufferedImageContainer[1];
    output   = BufferedImageHelper.deepCopy(images[0].toBufferedImage());
    channels = new int[images.length][];
    val      = new int[images.length];
    split    = new int[4];
    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
	for (i = 0; i < images.length; i++)
	  channels[i] = BufferedImageHelper.split(images[i].toBufferedImage().getRGB(x, y));
	for (n = 0; n < 4; n++) {
	  for (i = 0; i < images.length; i++)
	    val[i] = channels[i][n];
	  split[n] = (int) StatUtils.mean(val);
	}
	output.setRGB(x, y, BufferedImageHelper.combine(split));
      }
    }
    result[0] = new BufferedImageContainer();
    result[0].setImage(output);

    return result;
  }
}
