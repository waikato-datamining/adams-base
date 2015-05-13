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
 * TextOverlayPaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core;

import adams.core.VariableName;
import adams.flow.core.Actor;
import adams.gui.event.PaintEvent.PaintMoment;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Simply paints the specified text at a certain location.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextOverlayPaintlet
  extends AbstractPaintlet
  implements FlowAwarePaintlet {

  private static final long serialVersionUID = 7923819857566247771L;

  /** the actor that this paintlet belongs to. */
  protected Actor m_Actor;

  /** the text to paint. */
  protected String m_Text;

  /** the variable with the text to paint. */
  protected VariableName m_TextVariable;

  /** the X position. */
  protected int m_X;

  /** the Y position. */
  protected int m_Y;

  /** the color for the text. */
  protected Color m_Color;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply paints the specified text at a certain location.";
  }
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "text", "text",
      "");

    m_OptionManager.add(
      "text-variable", "textVariable",
      new VariableName());

    m_OptionManager.add(
      "x", "X",
      5, 1, null);

    m_OptionManager.add(
      "y", "Y",
      20, 1, null);

    m_OptionManager.add(
      "color", "color",
      Color.BLACK);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actor = null;
  }

  /**
   * Sets the owning actor.
   *
   * @param actor	the actor this paintlet belongs to
   */
  public void setActor(Actor actor) {
    m_Actor = actor;
    memberChanged();
  }

  /**
   * Returns the owning actor.
   *
   * @return		the actor this paintlet belongs to, null if none set
   */
  public Actor getActor() {
    return m_Actor;
  }

  /**
   * Sets the text to paint.
   *
   * @param value	the text
   */
  public void setText(String value) {
    m_Text = value;
    memberChanged();
  }

  /**
   * Returns the text to paint.
   *
   * @return		the text
   */
  public String getText() {
    return m_Text;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String textTipText() {
    return "The text to paint.";
  }

  /**
   * Sets the variable containing the text to paint.
   *
   * @param value	the variable
   */
  public void setTextVariable(VariableName value) {
    m_TextVariable = value;
    memberChanged();
  }

  /**
   * Returns the variable containing the text to paint.
   *
   * @return		the variable
   */
  public VariableName getTextVariable() {
    return m_TextVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String textVariableTipText() {
    return "The variable containing the text to paint.";
  }

  /**
   * Sets the X position in pixels.
   *
   * @param value	the position
   */
  public void setX(int value) {
    m_X = value;
    memberChanged();
  }

  /**
   * Returns the X position in pixels.
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
    return "The X position in pixels.";
  }

  /**
   * Sets the Y position in pixels.
   *
   * @param value	the position
   */
  public void setY(int value) {
    m_Y = value;
    memberChanged();
  }

  /**
   * Returns the Y position in pixels.
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
    return "The Y position in pixels.";
  }

  /**
   * Set the stroke color for the text.
   *
   * @param value	color of the text
   */
  public void setColor(Color value) {
    m_Color = value;
    memberChanged();
  }

  /**
   * Get the stroke color for the text.
   *
   * @return		color of the text
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
    return "The color for the text.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.POST_PAINT;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    String	text;

    text = "";

    if (m_Actor != null) {
      if (m_Actor.getVariables().has(m_TextVariable.getValue()))
	text = m_Actor.getVariables().get(m_TextVariable.getValue());
    }

    if (text.isEmpty())
      text = m_Text;

    g.setColor(m_Color);
    g.drawString(text, m_X, m_Y);
  }
}
