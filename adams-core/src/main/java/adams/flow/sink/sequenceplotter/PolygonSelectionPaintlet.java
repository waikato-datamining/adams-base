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
 * PolygonSelectionPaintlet.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.sequenceplotter;

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.AbstractXYSequencePaintlet;
import adams.gui.visualization.sequence.AbstractXYSequencePointHitDetector;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Paints the selected points as point/line/polygon.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PolygonSelectionPaintlet
  extends AbstractXYSequencePaintlet {

  private static final long serialVersionUID = -373146191991787658L;

  /** the color to use for painting. */
  protected Color m_Color;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints the selected points as point/line/polygon.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "color", Color.RED);
  }

  /**
   * Set the stroke color for the paintlet.
   *
   * @param value		Color of the stroke
   */
  public void setColor(Color value) {
    m_Color = value;
    memberChanged();
  }

  /**
   * Get the stroke color for the paintlet.
   *
   * @return		Color of the stroke
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Tip text for the stroke color property.
   *
   * @return		the tip text
   */
  public String colorTipText() {
    return "Stroke color for the paintlet";
  }

  /**
   * Checks whether the paintlet is supposed to paint for this
   * {@link PaintMoment}. Returns always true if
   * {@link PaintMoment#MULTIPLE}.
   *
   * @return		true if painting should occur
   */
  @Override
  public boolean canPaint(PaintMoment moment) {
    if (!(getPanel() instanceof SequencePlotterPanel))
      return false;
    else
      return super.canPaint(moment);
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return always null
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return null;
  }

  /**
   * The actual paint routine of the paintlet.
   *
   * @param g      the graphics context to use for painting
   * @param moment what {@link PaintMoment} is currently being painted
   */
  @Override
  protected void doPerformPaint(Graphics g, PaintMoment moment) {
    int[]			x;
    int[]			y;
    int				i;
    SequencePlotterPanel	parent;
    AxisPanel 			axisX;
    AxisPanel			axisY;

    parent = (SequencePlotterPanel) m_Panel;
    if (parent == null)
      return;
    if (parent.getSelection().isEmpty())
      return;

    g.setColor(m_Color);

    axisX = m_Panel.getPlot().getAxis(Axis.BOTTOM);
    axisY = m_Panel.getPlot().getAxis(Axis.LEFT);
    x = new int[parent.getSelection().size()];
    y = new int[x.length];
    for (i = 0; i < x.length; i++) {
      x[i] = axisX.valueToPos(parent.getSelection().get(i).getX());
      y[i] = axisY.valueToPos(parent.getSelection().get(i).getY());
    }

    if (x.length == 1)
      g.drawOval(x[0] - 1, y[0] - 1, 3, 3);
    else if (x.length == 2)
      g.drawLine(x[0], y[0], x[1], y[1]);
    else
      g.drawPolygon(x, y, x.length);
  }
}
