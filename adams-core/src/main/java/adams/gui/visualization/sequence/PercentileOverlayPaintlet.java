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
 * PercentileOverlayPaintlet.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.statistics.Percentile;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

import java.awt.Color;
import java.awt.Graphics;

/**
 <!-- globalinfo-start -->
 * Draws the specified percentile as straight line.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 *
 * <pre>-percentile &lt;double&gt; (property: percentile)
 * &nbsp;&nbsp;&nbsp;The percentile to use (0-1).
 * &nbsp;&nbsp;&nbsp;default: 0.25
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PercentileOverlayPaintlet
  extends AbstractXYSequencePaintlet
  implements PaintletWithCustomDataSupport {

  /** for serialization. */
  private static final long serialVersionUID = 6292059403058224856L;

  /** the percentile. */
  protected double m_Percentile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws the specified percentile as straight line.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "percentile", "percentile",
      0.25, 0.0, 1.0);
  }

  /**
   * Sets the percentile to use.
   *
   * @param value	the percentile (0-1)
   */
  public void setPercentile(double value) {
    m_Percentile = value;
    memberChanged();
  }

  /**
   * Returns the percentile in use.
   *
   * @return		the percentile (0-1)
   */
  public double getPercentile() {
    return m_Percentile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentileTipText() {
    return "The percentile to use (0-1).";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.GRID;
  }
  
  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		always null
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return null;
  }
  /**
   * Draws the custom data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   */
  protected void doDrawCustomData(Graphics g, PaintMoment moment, XYSequence data, Color color) {
    AxisPanel		xAxis;
    AxisPanel		yAxis;
    Percentile<Double> 	perc;
    double 		value;
    
    xAxis = getPlot().getAxis(Axis.BOTTOM);
    yAxis = getPlot().getAxis(Axis.LEFT);

    // calculate percentile
    perc = new Percentile<>();
    for (XYSequencePoint point: data.toList())
      perc.add(point.getY());
    value = perc.getPercentile(m_Percentile);
    if (isLoggingEnabled())
      getLogger().info("Percentile (" + m_Percentile + "): " + value);
    
    g.setColor(color);
    g.drawLine(
	xAxis.valueToPos(xAxis.getActualMinimum()), 
	yAxis.valueToPos(value),
	xAxis.valueToPos(xAxis.getActualMaximum()), 
	yAxis.valueToPos(value));
  }

  /**
   * Draws the data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   */
  public void drawCustomData(Graphics g, PaintMoment moment, XYSequence data, Color color) {
    float	width;

    width = getStrokeWidth(g, 1.0f);
    applyStroke(g, m_StrokeThickness);
    doDrawCustomData(g, moment, data, color);
    applyStroke(g, width);
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  protected void doPerformPaint(Graphics g, PaintMoment moment) {
    int				i;
    XYSequence			data;
    XYSequenceContainerManager	manager;

    // paint all points
    manager = getSequencePanel().getContainerManager();
    synchronized(manager) {
      for (i = 0; i < manager.count(); i++) {
	if (!manager.isVisible(i))
	  continue;
        if (manager.isFiltered() && !manager.isFiltered(i))
          continue;
	data = manager.get(i).getData();
	if (data.size() == 0)
	  continue;
	synchronized(data) {
	  drawCustomData(g, moment, data, manager.get(i).getColor());
	}
      }
    }
 }
}
