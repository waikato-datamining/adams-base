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
 * MetaDataText.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image;

import adams.data.report.Field;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Overlay for text from the image's meta-data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see ImagePanel#getAllProperties()
 */
public class MetaDataText
  extends AbstractImageOverlay {

  private static final long serialVersionUID = -3086529802660906811L;

  /** the X for the text. */
  protected int m_X;

  /** the Y for the text. */
  protected int m_Y;

  /** the font for the text. */
  protected Font m_Font;

  /** the color of the text. */
  protected Color m_Color;

  /** the prefix to use. */
  protected String m_Prefix;

  /** the meta-data field to display. */
  protected Field m_Field;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the meta-data information at the specified location.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "x", "X",
      10, 1, null);

    m_OptionManager.add(
      "y", "Y",
      20, 1, null);

    m_OptionManager.add(
      "font", "font",
      UIManager.getDefaults().getFont("TextField.font"));

    m_OptionManager.add(
      "color", "color",
      Color.BLACK);

    m_OptionManager.add(
      "prefix", "prefix",
      "");

    m_OptionManager.add(
      "field", "field",
      new Field());
  }

  /**
   * Sets the X of the text.
   *
   * @param value	the X
   */
  public void setX(int value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the X of the text.
   *
   * @return		the X
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
    return "The X for the text.";
  }

  /**
   * Sets the Y of the text.
   *
   * @param value	the Y
   */
  public void setY(int value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the Y of the text.
   *
   * @return		the Y
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
    return "The Y for the text.";
  }

  /**
   * Sets the font of the text.
   *
   * @param value	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the font of the text.
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
    return "The font of the text.";
  }

  /**
   * Sets the color of the text.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color of the text.
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
    return "The color of the text.";
  }

  /**
   * Sets the optional prefix for the text.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the optional prefix for the text.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The (optional) prefix for the text.";
  }

  /**
   * Sets the meta-data field to display.
   *
   * @param value	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the meta-data field to display.
   *
   * @return		the field
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
    return "The meta-data field to display.";
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected void doImageChanged(PaintPanel panel) {
  }

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected void doPaintOverlay(PaintPanel panel, Graphics g) {
    String	text;
    Font	actualFont;

    if (!panel.getOwner().getAllProperties().hasValue(m_Field))
      return;

    if (m_Prefix.isEmpty())
      text = "" + panel.getOwner().getAllProperties().getValue(m_Field);
    else
      text = m_Prefix + panel.getOwner().getAllProperties().getValue(m_Field);

    actualFont = m_Font.deriveFont((float) ((double) m_Font.getSize() / panel.getScale()));
    g.setColor(m_Color);
    g.setFont(actualFont);
    g.drawString(text, m_X, m_Y);
  }
}
