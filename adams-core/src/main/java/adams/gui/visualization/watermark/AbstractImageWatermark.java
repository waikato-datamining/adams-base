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
 * AbstractImageWatermark.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.watermark;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * Ancestor for image-based watermarks.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageWatermark
  extends AbstractWatermark {

  private static final long serialVersionUID = 2034216126819968344L;

  /** the location. */
  protected Location m_Location;

  /** the X position. */
  protected int m_X;

  /** the Y position. */
  protected int m_Y;

  /** the padding to use. */
  protected int m_Padding;

  /** the scale to use (1 = 100%). */
  protected double m_Scale;

  /** the alpha value to use for the overlay (0: transparent, 255: opaque). */
  protected int m_Alpha;

  /** whether the image was initialized. */
  protected transient boolean m_Initialized;

  /** the image. */
  protected transient Image m_Image;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "location", "location",
      getDefaultLocation());

    m_OptionManager.add(
      "x", "X",
      getDefaultX(), 1, null);

    m_OptionManager.add(
      "y", "Y",
      getDefaultY(), 1, null);

    m_OptionManager.add(
      "padding", "padding",
      getDefaultPadding(), 0, null);

    m_OptionManager.add(
      "scale", "scale",
      getDefaultScale(), 0.001, null);

    m_OptionManager.add(
      "alpha", "alpha",
      getDefaultAlpha(), 0, 255);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Initialized = false;
    m_Image       = null;
  }

  /**
   * Returns the default location.
   *
   * @return		the default
   */
  protected Location getDefaultLocation() {
    return Location.BOTTOM_RIGHT;
  }

  /**
   * Sets the location of the image.
   *
   * @param value	the location
   */
  public void setLocation(Location value) {
    m_Location = value;
    reset();
  }

  /**
   * Returns the location of the image.
   *
   * @return		the location
   */
  public Location getLocation() {
    return m_Location;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locationTipText() {
    return "Where to place the image.";
  }

  /**
   * Returns the default X position (1-based).
   *
   * @return		the default
   */
  protected int getDefaultX() {
    return 1;
  }

  /**
   * Sets the X position in pixels (1-based).
   *
   * @param value	the position
   */
  public void setX(int value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the X position in pixels (1-based).
   *
   * @return		the position
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The X position in pixels (1-based)";
  }

  /**
   * Returns the default Y position (1-based).
   *
   * @return		the default
   */
  protected int getDefaultY() {
    return 1;
  }

  /**
   * Sets the Y position in pixels (1-based).
   *
   * @param value	the position
   */
  public void setY(int value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the Y position in pixels (1-based).
   *
   * @return		the position
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The Y position in pixels (1-based).";
  }

  /**
   * Returns the default padding around the image.
   *
   * @return		the default
   */
  protected int getDefaultPadding() {
    return 0;
  }

  /**
   * Sets the padding around the image.
   *
   * @param value	the padding
   */
  public void setPadding(int value) {
    m_Padding = value;
    reset();
  }

  /**
   * Returns the padding around the image.
   *
   * @return		the padding
   */
  public int getPadding() {
    return m_Padding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paddingTipText() {
    return "The padding to use around the image.";
  }

  /**
   * Returns the default scale factor to use.
   *
   * @return		the default
   */
  protected double getDefaultScale() {
    return 1.0;
  }

  /**
   * Sets the scale factor to apply.
   *
   * @param value	the scale factor
   */
  public void setScale(double value) {
    m_Scale = value;
    reset();
  }

  /**
   * Returns the scale factor to apply.
   *
   * @return		the scale factor
   */
  public double getScale() {
    return m_Scale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scaleTipText() {
    return "The scale factor to apply to the image (1 = 100%).";
  }

  /**
   * Returns the default alpha value: 0=transparent, 255=opaque.
   *
   * @return		the default
   */
  protected int getDefaultAlpha() {
    return 255;
  }

  /**
   * Sets the alpha value to use for the overlay: 0=transparent, 255=opaque.
   *
   * @param value	the alpha value
   */
  public void setAlpha(int value) {
    if (getOptionManager().isValid("alpha", value)) {
      m_Alpha = value;
      reset();
    }
  }

  /**
   * Returns the alpha value to use for the overlay: 0=transparent, 255=opaque.
   *
   * @return		the alpha value
   */
  public int getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String alphaTipText() {
    return "The alpha value to use for the overlay: 0=transparent, 255=opaque.";
  }

  /**
   * Loads the image.
   *
   * @return		the image, null if failed to load
   */
  protected abstract Image loadImage();

  /**
   * Loads the image if necessary.
   */
  protected void initializeImage() {
    if (!m_Initialized) {
      m_Image = loadImage();

      if ((m_Image != null) && (m_Scale != 1.0)) {
	m_Image = m_Image.getScaledInstance(
	  (int) (m_Image.getWidth(null) * m_Scale),
	  (int) (m_Image.getHeight(null) * m_Scale),
	  Image.SCALE_SMOOTH);
      }

      if (isLoggingEnabled())
	getLogger().info("image width=" + m_Image.getWidth(null) + ", height=" + m_Image.getHeight(null));

      m_Initialized = true;
    }
  }

  /**
   * Returns whether the watermark can be applied.
   *
   * @param g		the graphics context
   * @param dimension 	the dimension of the drawing area
   * @return		true if it can be applied
   */
  @Override
  protected boolean canApplyWatermark(Graphics g, Dimension dimension) {
    initializeImage();
    return super.canApplyWatermark(g, dimension)
	     && (m_Image != null);
  }

  /**
   * Applies the watermark in the specified graphics context.
   *
   * @param g 		the graphics context
   * @param dimension 	the dimension of the drawing area
   */
  @Override
  protected void doApplyWatermark(Graphics g, Dimension dimension) {
    int		x;
    int		y;

    initializeImage();
    if (m_Image == null)
      return;

    switch (m_Location) {
      case ABSOLUTE:
	x = m_X - 1 + m_Padding;
	y = m_Y - 1 + m_Padding;
	break;

      case TOP_LEFT:
	x = m_Padding;
	y = m_Padding;
	break;

      case TOP_RIGHT:
	x = (int) dimension.getWidth() - m_Image.getWidth(null) - m_Padding;
	y = m_Padding;
	break;

      case BOTTOM_LEFT:
	x = m_Padding;
	y = (int) dimension.getHeight() - m_Image.getHeight(null) - m_Padding;
	break;

      case BOTTOM_RIGHT:
	x = (int) dimension.getWidth()  - m_Image.getWidth(null) - m_Padding;
	y = (int) dimension.getHeight() - m_Image.getHeight(null) - m_Padding;
	break;

      default:
	throw new IllegalStateException("Unhandled location: " + m_Location);
    }

    if (isLoggingEnabled())
      getLogger().info("x=" + x + ", y=" + y);

    if (m_Alpha < 255)
      ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) m_Alpha / 255));

    g.drawImage(m_Image, x, y, null);
  }
}
