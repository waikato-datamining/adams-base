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
 * CrossHairTracker.java
 * Copyright (C) 2012-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import adams.core.option.OptionHandler;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.ContentPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

/**
 <!-- globalinfo-start -->
 * A cross-hair mouse movement tracker.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the cross-hair.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CrossHairTracker
  extends AbstractStrokePaintlet
  implements MouseMovementTracker {

  /** for serialization. */
  private static final long serialVersionUID = -6562614432508466918L;
  
  /** the color of the cross-hair. */
  protected Color m_Color;

  /** whether to draw the coordinates as well. */
  protected boolean m_TextCoordinates;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "A cross-hair mouse movement tracker.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "color",
      Color.BLACK);

    m_OptionManager.add(
      "text-coordinates", "textCoordinates",
      false);
  }

  /**
   * Sets the color to use for the cross-hair.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color in use for the cross-hair.
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
    return "The color to use for the cross-hair.";
  }

  /**
   * Sets whether to display the coordinates as text as well.
   *
   * @param value	true if to display
   */
  public void setTextCoordinates(boolean value) {
    m_TextCoordinates = value;
    reset();
  }

  /**
   * Returns whether to display the coordinates as text as well.
   *
   * @return		true if to display
   */
  public boolean getTextCoordinates() {
    return m_TextCoordinates;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String textCoordinatesTipText() {
    return "If enabled, the coordinate values get displayed as text as well.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  public PaintMoment getPaintMoment() {
    return PaintMoment.POST_PAINT;
  }
  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  protected void doPerformPaint(Graphics g, PaintMoment moment) {
    ContentPanel	panel;
    Point		point;
    int			x;
    int			y;
    Graphics2D		g2d;
    AxisPanel		left;
    AxisPanel		bottom;
    String		label;
    TextLayout		textLayoutValue;
    Rectangle2D 	textBoundsValue;

    panel = getPlot().getContent();
    point = panel.getMousePosition();
    if (point == null)
      return;
    x = (int) point.getX();
    y = (int) point.getY();

    // draw cross-hair
    g.setColor(m_Color);
    // horizontal
    g.drawLine(0, y, panel.getWidth(), y);
    // vertical
    g.drawLine(x, 0, x, panel.getHeight());

    if (m_TextCoordinates) {
      left   = getPlot().getAxis(Axis.LEFT);
      bottom = getPlot().getAxis(Axis.BOTTOM);

      // x coordinate
      g.drawString(bottom.valueToDisplay(bottom.posToValue(x)), x + 10, 10);

      // y coordinate
      g2d             = (Graphics2D) g;
      label           = left.valueToDisplay(left.posToValue(y));
      textLayoutValue = new TextLayout(label, g.getFont(), g2d.getFontRenderContext());
      textBoundsValue = textLayoutValue.getBounds();
      g.drawString(label, (int) (panel.getWidth() - textBoundsValue.getWidth() - 10), y - 10);
    }
  }

  /**
   * Gets triggered when the mouse moved.
   * 
   * @param e		the mouse event that triggered the event
   */
  public void mouseMovementTracked(MouseEvent e) {
    getPanel().getPlot().getContent().repaint();
  }
  
  /**
   * Returns a shallow copy of the tracker. Doesn't expand variables in case
   * of {@link OptionHandler} objects.
   * 
   * @return		a shallow copy of the tracker
   */
  @Override
  public MouseMovementTracker shallowCopyTracker() {
    return (MouseMovementTracker) shallowCopy();
  }
  
  /**
   * Returns a shallow copy of the tracker.
   * 
   * @param expand	whether to expand variables to their actual value
   * @return		a shallow copy of the tracker
   */
  @Override
  public MouseMovementTracker shallowCopyTracker(boolean expand) {
    return (MouseMovementTracker) shallowCopy(expand);
  }
}
