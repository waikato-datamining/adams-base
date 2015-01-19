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
 * AbstractBarcodeEncoder.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.barcode.encode;

import adams.flow.transformer.draw.AbstractDrawOperation;

import java.awt.image.BufferedImage;

/**
 * Ancestor for barcode encoders, i.e., classes that generated barcode images.
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBarcodeEncoder extends AbstractDrawOperation {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = 2030827401336384097L;

  /**
   * Top left x of the barcode.
   */
  protected int m_X;

  /**
   * Top left y of the barcode.
   */
  protected int m_Y;

  /**
   * Width of barcode.
   */
  protected int m_Width;

  /**
   * Height of barcode.
   */
  protected int m_Height;

  /**
   * Margin around the barcode.
   */
  protected int m_Margin;

  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("x", "x", 1, 1, null);
    m_OptionManager.add("y", "y", 1, 1, null);
    m_OptionManager.add("width", "width", 100, 1, null);
    m_OptionManager.add("height", "height", 100, 1, null);
    m_OptionManager.add("margin", "margin", 5, 0, null);
  }

  /**
   * Additional checks to the image.
   *
   * @param image the image to check
   * @return null if OK, otherwise error message
   */
  @Override
  protected String check(BufferedImage image) {
    String result = super.check(image);

    if (result == null) {
      if (m_X + m_Width - 1 > image.getWidth())
        result = "X + Width is larger than image width: " + (m_X + m_Width) + " > " + image.getWidth();
      else if (m_Y + m_Height - 1 > image.getHeight())
        result = "Y + Height is larger than image height: " + (m_Y + m_Height) + " > " + image.getHeight();
    }

    return result;
  }

  /**
   * Returns the X position of the pixel.
   *
   * @return the position
   */
  public int getX() {
    return m_X;
  }

  /**
   * Sets the X position of the pixel.
   *
   * @param value the position
   */
  public void setX(int value) {
    if (value >= 1) {
      m_X = value;
      reset();
    } else
      getLogger().severe("X must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String xTipText() {
    return "X position of the top-left corner.";
  }

  /**
   * Returns the Y position of the pixel.
   *
   * @return the position
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Sets the Y position of the pixel.
   *
   * @param value the position
   */
  public void setY(int value) {
    if (value >= 1) {
      m_Y = value;
      reset();
    } else
      getLogger().severe("Y must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String yTipText() {
    return "Y position of the top-left corner.";
  }

  /**
   * Returns the width of the barcode.
   *
   * @return the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Sets the width of the barcode.
   *
   * @param value the width
   */
  public void setWidth(int value) {
    if (value >= 1) {
      m_Width = value;
      reset();
    } else
      getLogger().severe("Width must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "Width of the barcode in pixels.";
  }

  /**
   * Returns the height of the barcode.
   *
   * @return the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Sets the height of the barcode.
   *
   * @param value the height
   */
  public void setHeight(int value) {
    if (value >= 1) {
      m_Height = value;
      reset();
    } else
      getLogger().severe("Height must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "Height of the barcode in pixels.";
  }

  /**
   * Returns the margin around the barcode.
   *
   * @return the margin
   */
  public int getMargin() {
    return m_Margin;
  }

  /**
   * Sets the margin around the barcode.
   *
   * @param value the margin
   */
  public void setMargin(int value) {
    if (value >= 0) {
      m_Margin = value;
      reset();
    } else
      getLogger().severe("Margin must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String marginTipText() {
    return "White margin surrounding the barcode.";
  }
}
