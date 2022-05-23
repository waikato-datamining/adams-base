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
 * ClassificationLabelTextOverlay.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.overlay;

import adams.data.report.DataType;
import adams.data.report.Field;
import adams.gui.core.Fonts;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Displays the classification label retrieved from the field in the report and overlays it as text on the image.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClassificationLabelTextOverlay
  extends AbstractOverlay
  implements OverlayWithCustomAlphaSupport {

  private static final long serialVersionUID = -4472215867236513662L;

  /** the field to store the classification label in. */
  protected Field m_Field;

  /** the color for the objects. */
  protected Color m_Color;

  /** the label font. */
  protected Font m_LabelFont;

  /** the x offset for the label. */
  protected int m_LabelOffsetX;

  /** the y offset for the label. */
  protected int m_LabelOffsetY;

  /** whether a custom alpha is in use. */
  protected boolean m_CustomAlphaEnabled;

  /** the custom alpha value to use. */
  protected int m_CustomAlpha;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the classification label retrieved from the field in the "
      + "report and overlays it as text on the image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field", "field",
      new Field("Classification", DataType.STRING));

    m_OptionManager.add(
      "color", "color",
      Color.RED);

    m_OptionManager.add(
      "label-font", "labelFont",
      Fonts.getSansFont(14));

    m_OptionManager.add(
      "label-offset-x", "labelOffsetX",
      10, 0, null);

    m_OptionManager.add(
      "label-offset-y", "labelOffsetY",
      20, 0, null);
  }

  /**
   * Sets the field to use.
   *
   * @param value 	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to use.
   *
   * @return 		the field
   */
  public Field getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The field to store the label in.";
  }

  /**
   * Sets the color to use for the label.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use for the label.
   *
   * @return 		the color
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
    return "The color to use for the label.";
  }

  /**
   * Sets the label font.
   *
   * @param value 	the label font
   */
  public void setLabelFont(Font value) {
    m_LabelFont = value;
    reset();
  }

  /**
   * Returns the label font.
   *
   * @return 		the label font
   */
  public Font getLabelFont() {
    return m_LabelFont;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFontTipText() {
    return "The font to use for the labels.";
  }

  /**
   * Sets the X offset for the label.
   *
   * @param value 	the X offset
   */
  public void setLabelOffsetX(int value) {
    if (getOptionManager().isValid("labelOffsetX", value)) {
      m_LabelOffsetX = value;
      reset();
    }
  }

  /**
   * Returns the X offset for the label.
   *
   * @return 		the X offset
   */
  public int getLabelOffsetX() {
    return m_LabelOffsetX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetXTipText() {
    return "The X offset for the label.";
  }

  /**
   * Sets the Y offset for the label.
   *
   * @param value 	the Y offset
   */
  public void setLabelOffsetY(int value) {
    if (getOptionManager().isValid("labelOffsetY", value)) {
      m_LabelOffsetY = value;
      reset();
    }
  }

  /**
   * Returns the Y offset for the label.
   *
   * @return 		the Y offset
   */
  public int getLabelOffsetY() {
    return m_LabelOffsetY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetYTipText() {
    return "The Y offset for the label.";
  }

  /**
   * Sets whether to use a custom alpha value for the overlay colors.
   *
   * @param value	true if to use custom alpha
   */
  @Override
  public void setCustomAlphaEnabled(boolean value) {
    m_CustomAlphaEnabled = value;
    annotationsChanged();
  }

  /**
   * Returns whether a custom alpha value is in use for the overlay colors.
   *
   * @return		true if custom alpha in use
   */
  @Override
  public boolean isCustomAlphaEnabled() {
    return m_CustomAlphaEnabled;
  }

  /**
   * Sets the custom alpha value (0: transparent, 255: opaque).
   *
   * @param value	the alpha value
   */
  @Override
  public void setCustomAlpha(int value) {
    m_CustomAlpha = value;
    annotationsChanged();
  }

  /**
   * Returns the custom alpha value (0: transparent, 255: opaque).
   *
   * @return		the alpha value
   */
  @Override
  public int getCustomAlpha() {
    return m_CustomAlpha;
  }

  /**
   * Applies the custom alpha value to the color if necessary.
   *
   * @param c		the color to update
   * @return		the (potentially) updated color
   */
  protected Color applyAlpha(Color c) {
    Color	result;

    result = c;

    if (m_CustomAlphaEnabled)
      result = new Color(c.getRed(), c.getGreen(), c.getBlue(), m_CustomAlpha);

    return result;
  }

  /**
   * Paints the overlay.
   *
   * @param panel 	the owning panel
   * @param g		the graphics context
   */
  @Override
  protected void doPaint(ObjectAnnotationPanel panel, Graphics g) {
    String	label;

    label = panel.getCurrentLabel();
    if (label != null) {
      g.setColor(applyAlpha(m_Color));
      g.setFont(getLabelFont());
      g.drawString(label, getLabelOffsetX(), getLabelOffsetY());
    }
  }
}
