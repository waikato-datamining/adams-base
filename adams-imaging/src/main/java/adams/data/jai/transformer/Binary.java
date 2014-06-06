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
 * Binary.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import java.awt.image.BufferedImage;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

/**
 <!-- globalinfo-start -->
 * Turns an image into a binary image, using a grayscale threshold to determine which pixels are black (below) and which are white (equal to or above).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-threshold &lt;int&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold that determines whether a grayscale pixel will become black 
 * &nbsp;&nbsp;&nbsp;(below) or white (equal to or above).
 * &nbsp;&nbsp;&nbsp;default: 128
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7706 $
 */
public class Binary
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the (grayscale) threshold to use. */
  protected int m_Threshold;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Turns an image into a binary image, using a grayscale threshold to "
	+ "determine which pixels are black (below) and which are white (equal to or above).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"threshold", "threshold",
	128, 0, 255);
  }

  /**
   * Sets the threshold.
   *
   * @param value	the threshold
   */
  public void setThreshold(int value) {
    if ((value >= 0) && (value <= 255)) {
      m_Threshold = value;
      reset();
    }
    else {
      getLogger().severe("Threshold has to be 0 <= x <= 255, provided: " + value);
    }
  }

  /**
   * Returns the threshold.
   *
   * @return		the threshold
   */
  public int getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String thresholdTipText() {
    return "The threshold that determines whether a grayscale pixel will become black (below) or white (equal to or above).";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "threshold", m_Threshold, "threshold: ");
  }
  
  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    int 			width;
    int 			height;
    BufferedImage		image;
    int				x;
    int				y;
    int[]			channels;
    int				i;

    width     = img.getWidth();
    height    = img.getHeight();
    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    image     = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_BYTE_GRAY);

    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
	channels = BufferedImageHelper.split(image.getRGB(x, y));
	for (i = 0; i <= 3; i++) {
	  if (channels[i] < m_Threshold)
	    channels[i] = 0;
	  else
	    channels[i] = 255;
	}
	image.setRGB(x, y, BufferedImageHelper.combine(channels));
      }
    }

    result[0].setImage(image);

    return result;
  }
}
