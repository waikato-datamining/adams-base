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
 * SplitChannels.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.Channel;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Splits the image into the specified separate channels. With the output channels all converted to grayscale.
 * <p/>
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
public class SplitChannels
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the channels to extract. */
  protected Channel[] m_Channels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Splits the image into the specified separate channels. "
      + "With the output channels all converted to grayscale.";
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "channels", m_Channels);
  }

  /**
   * Optional checks of the image.
   *
   * @param img		the image to check
   */
  @Override
  protected void checkImage(BufferedImageContainer img) {
    super.checkImage(img);

    if (m_Channels.length == 0)
      throw new IllegalStateException("No channel(s) selected!");
  }

  /**
   * Generates the channels.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the channels
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    BufferedImage               oldImg;
    BufferedImage               newImg;
    int                         i;
    int                         x;
    int                         y;
    int[]                       pixel;
    int                         gray;

    result = new BufferedImageContainer[m_Channels.length];
    oldImg = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_INT_ARGB);
    for (i = 0; i < m_Channels.length; i++) {
      newImg    = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      result[i] = (BufferedImageContainer) img.getHeader();
      result[i].setImage(newImg);
    }
    for (y = 0; y < oldImg.getHeight(); y++) {
      for (x = 0; x < oldImg.getWidth(); x++) {
        pixel = BufferedImageHelper.split(oldImg.getRGB(x, y));
        for (i = 0; i < m_Channels.length; i++) {
          switch (m_Channels[i]) {
            case RED:
              gray = pixel[0];
              break;
            case GREEN:
              gray = pixel[1];
              break;
            case BLUE:
              gray = pixel[2];
              break;
            case ALPHA:
              gray = pixel[3];
              break;
            default:
              throw new IllegalStateException("Unhandled channel: " + m_Channels[i]);
          }
          result[i].getImage().setRGB(x, y, BufferedImageHelper.combine(gray, gray, gray, 0));
        }
      }
    }

    return result;
  }
}
