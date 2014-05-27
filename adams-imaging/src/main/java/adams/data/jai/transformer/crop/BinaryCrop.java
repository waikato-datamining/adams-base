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
import adams.data.statistics.StatUtils;

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
 * <pre>-num-check-points &lt;int&gt; (property: numCheckPoints)
 * &nbsp;&nbsp;&nbsp;The number of check points (evenly distributed across width&#47;height) to use 
 * &nbsp;&nbsp;&nbsp;for locating the smallest rectangle in the middle.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
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

  /** the number of checkpoints to use for determining minimum rectangle. */
  protected int m_NumCheckPoints;
  
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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"num-check-points", "numCheckPoints",
	1, 1, null);
  }

  /**
   * Sets the number of check points to use for determining smallest rectangle
   * in the middle.
   *
   * @param value	the number
   */
  public void setNumCheckPoints(int value) {
    if (value > 0) {
      m_NumCheckPoints = value;
      reset();
    }
    else {
      getLogger().severe("Number of check points has to be >0, provided: " + value);
    }
  }

  /**
   * Returns the number of check points to use for determining smallest rectangle
   * in the middle.
   *
   * @return		the number
   */
  public int getNumCheckPoints() {
    return m_NumCheckPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numCheckPointsTipText() {
    return 
	"The number of check points (evenly distributed across width/height) "
	+ "to use for locating the smallest rectangle in the middle.";
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
    int			n;
    int[]		xCheck;
    int[]		yCheck;
    int[]		top;
    int[]		bottom;
    int[]		left;
    int[]		right;
    int			atop;
    int			abottom;
    int			aleft;
    int			aright;

    binary = BufferedImageHelper.convert(img, BufferedImage.TYPE_BYTE_BINARY);
    width  = img.getWidth();
    height = img.getHeight();
    xCheck = new int[m_NumCheckPoints];
    yCheck = new int[m_NumCheckPoints];
    top    = new int[m_NumCheckPoints];
    bottom = new int[m_NumCheckPoints];
    left   = new int[m_NumCheckPoints];
    right  = new int[m_NumCheckPoints];
    
    for (n = 0; n < m_NumCheckPoints; n++) {
      xCheck[n] = width  / (m_NumCheckPoints+1) * (n+1);
      yCheck[n] = height / (m_NumCheckPoints+1) * (n+1);
    }
    
    for (n = 0; n < m_NumCheckPoints; n++) {
      // from top
      top[n] = 0;
      for (i = 0; i < yCheck[n]; i++) {
	if (((binary.getRGB(xCheck[n], i) >> 0) & 0xFF) > 0) {
	  top[n] = i;
	  break;
	}
      }
      if (isLoggingEnabled())
	getLogger().fine("top[" + n + "]: " + top);

      // from bottom
      bottom[n] = height - 1;
      for (i = height - 1; i >= yCheck[n]; i--) {
	if (((binary.getRGB(xCheck[n], i) >> 0) & 0xFF) > 0) {
	  bottom[n] = i;
	  break;
	}
      }
      if (isLoggingEnabled())
	getLogger().fine("bottom[" + n + "]: " + bottom);

      // from left
      left[n] = 0;
      for (i = 0; i < xCheck[n]; i++) {
	if (((binary.getRGB(i, yCheck[n]) >> 0) & 0xFF) > 0) {
	  left[n] = i;
	  break;
	}
      }
      if (isLoggingEnabled())
	getLogger().fine("left[" + n + "]: " + left);

      // from right
      right[n] = width - 1;
      for (i = width - 1; i >= xCheck[n]; i--) {
	if (((binary.getRGB(i, yCheck[n]) >> 0) & 0xFF) > 0) {
	  right[n] = i;
	  break;
	}
      }
      if (isLoggingEnabled())
	getLogger().fine("right[" + n + "]: " + right);
    }
    
    // determine actual top/left/bottom/right
    aleft   = StatUtils.max(left);
    aright  = StatUtils.min(right);
    atop    = StatUtils.max(top);
    abottom = StatUtils.min(bottom);
    
    // crop original
    image = img.getSubimage(aleft, atop, aright - aleft + 1, abottom - atop + 1);

    return image;
  }
}
