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
 * Rotate.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Rotates an image by a defined number of degrees.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-angle &lt;int&gt; (property: angle)
 * &nbsp;&nbsp;&nbsp;The angle, in degrees, to rotate the image by.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 360
 * </pre>
 *
 * <pre>-background &lt;java.awt.Color&gt; (property: background)
 * &nbsp;&nbsp;&nbsp;The color to use for the background of the rotated image.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Rotate
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the angle to rotate the image by. */
  protected int m_Angle;

  /** the background color. */
  protected Color m_Background;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Rotates an image by a defined number of degrees.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "angle", "angle",
      0, 0, 360);

    m_OptionManager.add(
      "background", "background",
      Color.BLACK);
  }

  /**
   * Sets the rotation angle.
   *
   * @param value	the angle (0-360)
   */
  public void setAngle(int value) {
    if (getOptionManager().isValid("angle", value)) {
      m_Angle = value;
      reset();
    }
  }

  /**
   * Returns the rotation angle.
   *
   * @return		the angle
   */
  public int getAngle() {
    return m_Angle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String angleTipText() {
    return "The angle, in degrees, to rotate the image by.";
  }

  /**
   * Sets the color to use for the background.
   *
   * @param value	the color
   */
  public void setBackground(Color value) {
    m_Background = value;
    reset();
  }

  /**
   * Returns the color in use for the background.
   *
   * @return		the color
   */
  public Color getBackground() {
    return m_Background;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String backgroundTipText() {
    return "The color to use for the background of the rotated image.";
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

    result = new BufferedImageContainer[1];
    result[0]  = (BufferedImageContainer) img.getHeader();
    result[0].setImage(BufferedImageHelper.rotate(img.toBufferedImage(), m_Angle, m_Background));

    return result;
  }
}
