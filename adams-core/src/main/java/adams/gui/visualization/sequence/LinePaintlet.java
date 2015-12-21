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
 * LinePaintlet.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequenceUtils;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

/**
 * A paintlet for painting a line plot of a sequence.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LinePaintlet
  extends AbstractXYSequencePaintlet
  implements AntiAliasingSupporter, PaintletWithCustomDataSupport {

  /** for serialization. */
  private static final long serialVersionUID = 8242948176244747138L;

  /**
   * Enum for the marker shape to plot around the data points.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum MarkerShape {
    /** nothing. */
    NONE,
    /** a square box. */
    BOX,
    /** a circle. */
    CIRCLE,
    /** a triangle. */
    TRIANGLE;
  }

  /** the maximum width/height of the shape to plot around the points (= data
   * point marker), if there's enough space. */
  protected int m_MarkerExtent;

  /** indicates whether marker shapes are painted or not. */
  protected boolean m_MarkersEnabled;

  /** whether to paint all the data points (no optimization). */
  protected boolean m_PaintAll;

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
	    GUIHelper.getInteger(getClass(), "markersExtent", 7), 0, null);

    m_OptionManager.add(
	    "markers-disabled", "markersDisabled",
	    !GUIHelper.getBoolean(getClass(), "markersEnabled", true));

    m_OptionManager.add(
	    "anti-aliasing-enabled", "antiAliasingEnabled",
	    GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));

    m_OptionManager.add(
	    "paint-all", "paintAll",
	    false);
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return new LineHitDetector(this);
  }

  /**
   * Returns whether marker shapes are disabled.
   *
   * @return		true if marker shapes are disabled
   */
  public boolean isMarkersDisabled() {
    return !m_MarkersEnabled;
  }

  /**
   * Sets whether to draw markers or not.
   *
   * @param value	if true then marker shapes won't be drawn
   */
  public void setMarkersDisabled(boolean value) {
    m_MarkersEnabled = !value;
    memberChanged();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String markersDisabledTipText() {
    return "If set to true, the markers are disabled.";
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
   * Returns whether marker shapes are disabled.
   *
   * @return		true if marker shapes are disabled
   */
  public boolean getPaintAll() {
    return m_PaintAll;
  }

  /**
   * Sets whether to draw markers or not.
   *
   * @param value	if true then marker shapes won't be drawn
   */
  public void setPaintAll(boolean value) {
    m_PaintAll = value;
    memberChanged();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintAllTipText() {
    return "If set to true, all data points will be painted, regardless whether they are visible or not.";
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
   * Draws the custom data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   */
  public void drawCustomData(Graphics g, PaintMoment moment, XYSequence data, Color color) {
    drawData(g, moment, data, color, MarkerShape.NONE);
  }

  /**
   * Draws the data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   * @param marker	the type of marker to draw
   */
  protected void drawData(Graphics g, PaintMoment moment, XYSequence data, Color color, MarkerShape marker) {
    List<XYSequencePoint>	points;
    XYSequencePoint		curr;
    int				currX;
    int				currY;
    int				prevX;
    int				prevY;
    int				prevMarkerX;
    int				prevMarkerY;
    AxisPanel			axisX;
    AxisPanel			axisY;
    int				i;
    int				start;
    int				end;

    points = data.toList();
    axisX  = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY  = getPanel().getPlot().getAxis(Axis.LEFT);

    g.setColor(color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    // find the start and end points for painting
    if (m_PaintAll) {
      start = 0;
      end   = data.size() - 1;
    }
    else {
      start = XYSequenceUtils.findClosestX(points, Math.floor(axisX.getMinimum()));
      if (start > 0)
	start--;
      end = XYSequenceUtils.findClosestX(points, Math.ceil(axisX.getMaximum()));
      if (end < data.size() - 1)
	end++;
    }

    currX       = Integer.MIN_VALUE;
    currY       = Integer.MIN_VALUE;
    prevX       = axisX.valueToPos(points.get(start).getX());
    prevY       = axisY.valueToPos(points.get(start).getY());
    prevMarkerX = 0;
    prevMarkerY = 0;

    for (i = start; i <= end; i++) {
      curr = (XYSequencePoint) points.get(i);

      // determine coordinates
      currX = axisX.valueToPos(XYSequencePoint.toDouble(curr.getX()));
      if (!m_PaintAll) {
	if ((i != start) && (i != end) && (currX == prevX))
	  continue;
      }
      currY = axisY.valueToPos(XYSequencePoint.toDouble(curr.getY()));

      // draw line
      g.drawLine(prevX, prevY, currX, currY);
      if (marker != MarkerShape.NONE) {
	if (Math.sqrt(Math.pow(currX - prevMarkerX, 2) + Math.pow(currY - prevMarkerY, 2)) > m_MarkerExtent * 2) {
	  if (marker == MarkerShape.BOX) {
	    g.drawRect(
		currX - (m_MarkerExtent / 2),
		currY - (m_MarkerExtent / 2),
		m_MarkerExtent - 1,
		m_MarkerExtent - 1);
	  }
	  else if (marker == MarkerShape.CIRCLE) {
	    g.drawArc(
		currX - (m_MarkerExtent / 2),
		currY - (m_MarkerExtent / 2),
		m_MarkerExtent - 1,
		m_MarkerExtent - 1,
		0,
		360);
	  }
	  else if (marker == MarkerShape.TRIANGLE) {
	    int[] x = new int[3];
	    int[] y = new int[3];
	    x[0] = currX - (m_MarkerExtent / 2);
	    y[0] = currY + (m_MarkerExtent / 2);
	    x[1] = x[0] + m_MarkerExtent;
	    y[1] = y[0];
	    x[2] = currX;
	    y[2] = y[0] - m_MarkerExtent;
	    g.drawPolygon(x, y, 3);
	  }

	  prevMarkerX = currX;
	  prevMarkerY = currY;
	}
      }

      prevX = currX;
      prevY = currY;
    }
  }

  /**
   * Determines the shape to paint around the data points, based on the index
   * of the data.
   *
   * @param index	the index of the spectrum
   * @return		the marker shape
   */
  protected MarkerShape getMarkerShape(int index) {
    MarkerShape		result;
    MarkerShape[]	shapes;

    result = MarkerShape.NONE;

    if (m_MarkersEnabled && (m_MarkerExtent > 0) && getPlot().isZoomed()) {
      shapes = MarkerShape.values();
      result = shapes[(index % (shapes.length - 1)) + 1];
    }

    return result;
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
	  drawData(g, moment, data, getColor(i), getMarkerShape(i));
	}
      }
    }
  }
}
