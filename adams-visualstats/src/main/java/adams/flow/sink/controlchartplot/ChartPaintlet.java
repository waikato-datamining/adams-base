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
 * ChartPaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.controlchartplot;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequenceUtils;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.HitDetectorSupporter;
import adams.gui.visualization.sequence.AbstractXYSequencePointHitDetector;
import adams.gui.visualization.sequence.LineHitDetector;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;

/**
 * A paintlet for painting a line plot of a sequence.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9308 $
 */
public class ChartPaintlet
  extends AbstractControlChartPaintlet
  implements AntiAliasingSupporter, HitDetectorSupporter<AbstractXYSequencePointHitDetector> {

  /** for serialization. */
  private static final long serialVersionUID = 8242948176244747138L;

  /** the maximum width/height of the shape to plot around the points (= data
   * point marker), if there's enough space. */
  protected int m_MarkerExtent;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paintlet for generating a line plot for X-Y sequences.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "markers-extent", "markerExtent",
	    7, 0, null);

    m_OptionManager.add(
	    "anti-aliasing-enabled", "antiAliasingEnabled",
	    GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
  }

  /**
   * Sets the extent (width and height of the shape around the plotted point).
   * 0 turns the plotting off. Should be an odd number for centering the shape.
   *
   * @param value	the new extent
   */
  public void setMarkerExtent(int value) {
    if (value >= 0) {
      m_MarkerExtent = value;
      memberChanged();
    }
    else {
      System.err.println("Marker extent must be >= 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the current marker extent (which is the width and height of the
   * shape).
   *
   * @return		the current extent
   */
  public int getMarkerExtent() {
    return m_MarkerExtent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String markerExtentTipText() {
    return "The size of the markers in pixels.";
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    memberChanged();
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
    return "If enabled, uses anti-aliasing for drawing lines.";
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return new LineHitDetector();
  }

  /**
   * Draws the data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   */
  protected void drawData(Graphics g, PaintMoment moment, XYSequence data, Color color) {
    List<XYSequencePoint>	points;
    XYSequencePoint		curr;
    int				currX;
    int				currY;
    int				prevX;
    int				prevY;
    AxisPanel			axisX;
    AxisPanel			axisY;
    int				i;
    int				start;
    int				end;
    HashMap<String,Object>	meta;

    points = data.toList();
    axisX  = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY  = getPanel().getPlot().getAxis(Axis.LEFT);

    g.setColor(color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    start = XYSequenceUtils.findClosestX(points, Math.floor(axisX.getMinimum()));
    if (start > 0)
      start--;
    end = XYSequenceUtils.findClosestX(points, Math.ceil(axisX.getMaximum()));
    if (end < data.size() - 1)
      end++;

    prevX = axisX.valueToPos(points.get(start).getX());
    prevY = axisY.valueToPos(points.get(start).getY());

    for (i = start; i <= end; i++) {
      curr = points.get(i);
      meta = null;
      if (curr instanceof SequencePlotPoint)
	meta = ((SequencePlotPoint) curr).getMetaData();

      // determine coordinates
      currX = axisX.valueToPos(XYSequencePoint.toDouble(curr.getX()));
      currY = axisY.valueToPos(XYSequencePoint.toDouble(curr.getY()));

      // draw line
      g.drawLine(prevX, prevY, currX, currY);

      // violation?
      if ((meta != null) && ((Boolean) meta.get("violation")))
	g.setColor(Color.RED);
      else
        g.setColor(color);

      // marker
      g.drawRect(
	currX - (m_MarkerExtent / 2),
	currY - (m_MarkerExtent / 2),
	m_MarkerExtent - 1,
	m_MarkerExtent - 1);

      prevX = currX;
      prevY = currY;
    }
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    int		i;
    XYSequence 	data;

    // paint all points
    synchronized(getActualContainerManager()) {
      for (i = 0; i < getActualContainerManager().count(); i++) {
	if (!getActualContainerManager().isVisible(i))
	  continue;
        if (getActualContainerManager().isFiltered() && !getActualContainerManager().isFiltered(i))
          continue;
	data = getActualContainerManager().get(i).getData();
	if (data.size() == 0)
	  continue;
	synchronized(data) {
	  drawData(g, moment, data, getColor(i));
	}
      }
    }
  }
}
