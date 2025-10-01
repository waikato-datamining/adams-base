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
 * Brightness.java
 * Copyright (C) 2014-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import adams.data.image.BufferedImageContainer;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 <!-- globalinfo-start -->
 * Brightens or darkens an image using the specified factor and offset.<br>
 * factor: &lt;0=darken image, &gt;0=brighten image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-factor &lt;float&gt; (property: factor)
 * &nbsp;&nbsp;&nbsp;The factor to use for brightening&#47;darkening.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-4
 * </pre>
 * 
 * <pre>-offset &lt;float&gt; (property: offset)
 * &nbsp;&nbsp;&nbsp;The offset to use for brightening&#47;darkening.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7706 $
 */
public class Brightness
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the factor to use. */
  protected float m_Factor;

  /** the offset to use. */
  protected float m_Offset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Brightens or darkens an image using the specified factor and offset.\n"
	+ "factor: <0=darken image, >0=brighten image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"factor", "factor",
	1.0f, 0.0001f, null);

    m_OptionManager.add(
	"offset", "offset",
	0.0f);
  }

  /**
   * Sets the factor.
   *
   * @param value	the factor
   */
  public void setFactor(float value) {
    if (value > 0.0) {
      m_Factor = value;
      reset();
    }
    else {
      getLogger().severe("Factor has to be >0, provided: " + value);
    }
  }

  /**
   * Returns the factor.
   *
   * @return		the factor
   */
  public float getFactor() {
    return m_Factor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String factorTipText() {
    return "The factor to use for brightening/darkening.";
  }

  /**
   * Sets the offset.
   *
   * @param value	the offset
   */
  public void setOffset(float value) {
    m_Offset = value;
    reset();
  }

  /**
   * Returns the offset.
   *
   * @return		the offset
   */
  public float getOffset() {
    return m_Offset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String offsetTipText() {
    return "The offset to use for brightening/darkening.";
  }

  /**
   * Optional checks of the image.
   *
   * @param img		the image to check
   */
  @Override
  protected void checkImage(BufferedImageContainer img) {
    super.checkImage(img);
    if (img.getImage().getType() == BufferedImage.TYPE_BYTE_INDEXED)
      throw new IllegalStateException("Brightness operation cannot be applied to indexed image!");
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
    BufferedImage		image;
    BufferedImage		filtered;
    RescaleOp 			op;

    image     = img.toBufferedImage();
    op        = new RescaleOp(m_Factor, m_Offset, null);
    filtered  = op.filter(image, null);
    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    result[0].setImage(filtered);

    return result;
  }
}
