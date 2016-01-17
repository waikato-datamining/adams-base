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
 * CombineChannels.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.multiimageoperation;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.Channel;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Combines the channels into a single image. The channel images are expected to be gray scale images.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-channel &lt;ALPHA|RED|GREEN|BLUE&gt; [-channel ...] (property: channels)
 * &nbsp;&nbsp;&nbsp;The channels to get from the incoming image.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CombineChannels
  extends AbstractBufferedImageMultiImageOperation {

  private static final long serialVersionUID = 1888786897723421704L;

  /** the channels to combine. */
  protected Channel[] m_Channels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Combines the channels into a single image. The channel images are expected to be gray scale images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "channel", "channels",
	    new Channel[0]);
  }

  /**
   * Sets the channels.
   *
   * @param value	the channels
   */
  public void setChannels(Channel[] value) {
    m_Channels = value;
    reset();
  }

  /**
   * Returns the channels.
   *
   * @return		the channels
   */
  public Channel[] getChannels() {
    return m_Channels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String channelsTipText() {
    return "The channels to get from the incoming image.";
  }

  /**
   * Returns the minimum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumImagesRequired() {
    return Math.max(m_Channels.length, 2);
  }

  /**
   * Returns the maximum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no upper limit
   */
  public int maxNumImagesRequired() {
    return Math.max(m_Channels.length, 2);
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
    BufferedImage 		newImg;
    int                         i;
    int				x;
    int				y;
    int[]                       pixels;

    result    = new BufferedImageContainer[1];
    newImg    = new BufferedImage(images[0].getWidth(), images[0].getHeight(), BufferedImage.TYPE_INT_ARGB);
    result[0] = (BufferedImageContainer) images[0].getHeader();
    result[0].setImage(newImg);

    for (i = 0; i < images.length; i++)
      images[i].setImage(BufferedImageHelper.convert(images[i].getImage(), BufferedImage.TYPE_BYTE_GRAY));

    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
        pixels = new int[4];
        for (i = 0; i < m_Channels.length; i++) {
          switch (m_Channels[i]) {
            case RED:
              pixels[0] = images[i].getImage().getRGB(x, y);
              break;
            case GREEN:
              pixels[1] = images[i].getImage().getRGB(x, y);
              break;
            case BLUE:
              pixels[2] = images[i].getImage().getRGB(x, y);
              break;
            case ALPHA:
              pixels[3] = images[i].getImage().getRGB(x, y);
              break;
            default:
              throw new IllegalStateException("Unhandled channel: " + m_Channels[i]);
          }
        }
        result[0].getImage().setRGB(x, y, BufferedImageHelper.combine(pixels));
      }
    }

    return result;
  }
}
