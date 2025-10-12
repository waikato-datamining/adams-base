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
 * Text.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.watermark;

import adams.core.Utils;
import adams.core.base.BaseText;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Displays the supplied text.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Text
  extends AbstractWatermark {

  private static final long serialVersionUID = 340768234363054038L;

  /** the location. */
  protected Location m_Location;

  /** the X position of the text (1-based). */
  protected int m_X;

  /** the Y position of the text (1-based). */
  protected int m_Y;

  /** the padding to use. */
  protected int m_Padding;

  /** the Y increment when outputting multiple lines. */
  protected int m_YIncrement;

  /** the color of the pixel. */
  protected Color m_Color;

  /** the font to use. */
  protected Font m_Font;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /** the alpha value to use for the overlay (0: transparent, 255: opaque). */
  protected int m_Alpha;

  /** the text to draw. */
  protected BaseText m_Text;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the supplied text.";
  }

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
      1, 1, null);

    m_OptionManager.add(
      "y", "Y",
      16, 1, null);

    m_OptionManager.add(
      "padding", "padding",
      getDefaultPadding(), 0, null);

    m_OptionManager.add(
      "y-increment", "YIncrement",
      16, -1, null);

    m_OptionManager.add(
      "color", "color",
      Color.BLACK);

    m_OptionManager.add(
      "font", "font",
      Fonts.getMonospacedFont());

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      true);

    m_OptionManager.add(
      "alpha", "alpha",
      255, 0, 255);

    m_OptionManager.add(
      "text", "text",
      new BaseText("ADAMS"));
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
   * Sets the X position of the text (top-left corner).
   *
   * @param value	the position, 1-based
   */
  public void setX(int value) {
    if (getOptionManager().isValid("X", value)) {
      m_X = value;
      reset();
    }
  }

  /**
   * Returns the X position of the text (top-left corner).
   *
   * @return		the position, 1-based
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
    return "The X position of the top-left corner of the text (1-based).";
  }

  /**
   * Sets the Y position of the text (top-left corner).
   *
   * @param value	the position, 1-based
   */
  public void setY(int value) {
    if (getOptionManager().isValid("Y", value)) {
      m_Y = value;
      reset();
    }
  }

  /**
   * Returns the Y position of the text (top-left corner).
   *
   * @return		the position, 1-based
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
    return "The Y position of the top-left corner of the text (1-based).";
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
   * Sets the Y increment when outputting multiple lines.
   *
   * @param value	the increment, -1 for autodetection based on font
   */
  public void setYIncrement(int value) {
    if (getOptionManager().isValid("YIncrement", value)) {
      m_YIncrement = value;
      reset();
    }
  }

  /**
   * Returns the Y increment when outputting multiple lines.
   *
   * @return		the increment, -1 for autodetection based on font
   */
  public int getYIncrement() {
    return m_YIncrement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YIncrementTipText() {
    return "The Y increment when outputting multiple lines of text, -1 for auto-detection based on font.";
  }

  /**
   * Sets the color of the pixel.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color of the pixel.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color of the pixel.";
  }

  /**
   * Sets the font to use.
   *
   * @param value	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the font in use.
   *
   * @return		the font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font to use for the text.";
  }

  /**
   * Sets the text to draw.
   *
   * @param value	the text
   */
  public void setText(BaseText value) {
    m_Text = value;
    reset();
  }

  /**
   * Returns the text to draw.
   *
   * @return		the text
   */
  public BaseText getText() {
    return m_Text;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String textTipText() {
    return "The text to use as watermark.";
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    reset();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "If enabled, uses anti-aliasing for drawing.";
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
   * Returns whether the watermark can be applied.
   *
   * @param g		the graphics context
   * @param dimension 	the dimension of the drawing area
   * @return		true if it can be applied
   */
  @Override
  protected boolean canApplyWatermark(Graphics g, Dimension dimension) {
    return super.canApplyWatermark(g, dimension)
	     && !m_Text.getValue().trim().isEmpty();
  }

  /**
   * Applies the watermark in the specified graphics context.
   *
   * @param g         the graphics context
   * @param dimension the dimension of the drawing area
   */
  @Override
  protected void doApplyWatermark(Graphics g, Dimension dimension) {
    String[]	lines;
    int		x;
    int		y;
    int		i;
    int		width;
    int		height;

    lines = Utils.split(m_Text.getValue(), "\n");

    g.setFont(m_Font);
    width = 0;
    for (i = 0; i < lines.length; i++)
      width = Math.max(width, g.getFontMetrics().stringWidth(lines[i]));
    if (m_YIncrement <= 0)
      height = (int) (g.getFontMetrics().getHeight() * 1.25);
    else
      height = m_YIncrement;

    switch (m_Location) {
      case ABSOLUTE:
	x = m_X - 1 + m_Padding;
	y = m_Y - 1 + m_Padding;
	break;

      case TOP_LEFT:
	x = m_Padding;
	y = height + m_Padding;
	break;

      case TOP_RIGHT:
	x = (int) dimension.getWidth() - width - m_Padding;
	y = height + m_Padding;
	break;

      case BOTTOM_LEFT:
	x = m_Padding;
	y = (int) dimension.getHeight() - (lines.length - 1) * height - m_Padding;
	break;

      case BOTTOM_RIGHT:
	x = (int) dimension.getWidth()  - width - m_Padding;
	y = (int) dimension.getHeight() - (lines.length - 1) * height - m_Padding;
	break;

      default:
	throw new IllegalStateException("Unhandled location: " + m_Location);
    }

    g.setColor(m_Color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    if (m_Alpha < 255)
      ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) m_Alpha / 255));
    for (i = 0; i < lines.length; i++)
      g.drawString(lines[i], x, y + i * height);

  }
}
