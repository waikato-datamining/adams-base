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
 * LimitPaintlet.java
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
public class LimitPaintlet
  extends AbstractControlChartPaintlet
  implements AntiAliasingSupporter, HitDetectorSupporter<AbstractXYSequencePointHitDetector> {

  /** for serialization. */
  private static final long serialVersionUID = 8242948176244747138L;

  /** the color for the lower limit. */
  protected Color m_ColorLower;

  /** the color for the center. */
  protected Color m_ColorCenter;

  /** the color for the upper limit. */
  protected Color m_ColorUpper;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Paintlet for generating a limits of a control chart.\n"
      + "";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color-lower", "colorLower",
      Color.RED.darker());

    m_OptionManager.add(
      "color-center", "colorCenter",
      Color.LIGHT_GRAY);

    m_OptionManager.add(
      "color-upper", "colorUpper",
      Color.RED.darker());

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
  }

  /**
   * Sets the color for the lower limit.
   *
   * @param value	the color
   */
  public void setColorLower(Color value) {
    m_ColorLower = value;
    memberChanged();
  }

  /**
   * Returns the color for the lower limit.
   *
   * @return		the color
   */
  public Color getColorLower() {
    return m_ColorLower;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorLowerTipText() {
    return "The color to use for drawing the lower limit.";
  }

  /**
   * Sets the color for the center.
   *
   * @param value	the color
   */
  public void setColorCenter(Color value) {
    m_ColorCenter = value;
    memberChanged();
  }

  /**
   * Returns the color for the center.
   *
   * @return		the color
   */
  public Color getColorCenter() {
    return m_ColorCenter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorCenterTipText() {
    return "The color to use for drawing the center.";
  }

  /**
   * Sets the color for the upper limit.
   *
   * @param value	the color
   */
  public void setColorUpper(Color value) {
    m_ColorUpper = value;
    memberChanged();
  }

  /**
   * Returns the color for the upper limit.
   *
   * @return		the color
   */
  public Color getColorUpper() {
    return m_ColorUpper;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorUpperTipText() {
    return "The color to use for drawing the upper limit.";
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
   * @return		null
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return null;
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.PRE_PAINT;
  }

  /**
   * Draws the limits.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   */
  protected void drawData(Graphics g, PaintMoment moment, XYSequence data) {
    List<XYSequencePoint>	points;
    XYSequencePoint		curr;
    XYSequencePoint		prev;
    int				currX;
    int				currY;
    int				prevX;
    int				prevY;
    AxisPanel			axisX;
    AxisPanel			axisY;
    int				i;
    int				start;
    int				end;
    HashMap<String,Object>	currMeta;
    HashMap<String,Object>	prevMeta;
    boolean			isStart;
    boolean			isEnd;

    points = data.toList();
    axisX  = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY  = getPanel().getPlot().getAxis(Axis.LEFT);

    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    start = XYSequenceUtils.findClosestX(points, Math.floor(axisX.getMinimum()));
    if (start > 0)
      start--;
    end = XYSequenceUtils.findClosestX(points, Math.ceil(axisX.getMaximum()));
    if (end < data.size() - 1)
      end++;

    for (i = start + 1; i <= end; i++) {
      // previous point
      prev = points.get(i - 1);
      if (prev instanceof SequencePlotPoint)
	prevMeta = ((SequencePlotPoint) prev).getMetaData();
      else
        continue;

      // current
      curr = points.get(i);
      if (curr instanceof SequencePlotPoint)
	currMeta = ((SequencePlotPoint) curr).getMetaData();
      else
        continue;

      isStart = (i == start + 1);
      isEnd   = (i == end);
      prevX   = axisX.valueToPos(XYSequencePoint.toDouble(prev.getX()));
      currX   = axisX.valueToPos(XYSequencePoint.toDouble(curr.getX()));

      // lower
      prevY = axisY.valueToPos(XYSequencePoint.toDouble(prevMeta.get("lower")));
      currY = axisY.valueToPos(XYSequencePoint.toDouble(currMeta.get("lower")));
      g.setColor(m_ColorLower);
      g.drawLine((isStart ? axisX.valueToPos(axisX.getActualMinimum()) : prevX), prevY, prevX + (currX - prevX) / 2, prevY);
      if (prevY != currY)
	g.drawLine(prevX + (currX - prevX) / 2, prevY, prevX + (currX - prevX) / 2, currY);
      g.drawLine(prevX + (currX - prevX) / 2, currY, (isEnd ? axisX.valueToPos(axisX.getActualMaximum()) : currX), currY);

      // center
      prevY = axisY.valueToPos(XYSequencePoint.toDouble(prevMeta.get("center")));
      currY = axisY.valueToPos(XYSequencePoint.toDouble(currMeta.get("center")));
      g.setColor(m_ColorCenter);
      g.drawLine((isStart ? axisX.valueToPos(axisX.getActualMinimum()) : prevX), prevY, prevX + (currX - prevX) / 2, prevY);
      if (prevY != currY)
	g.drawLine(prevX + (currX - prevX) / 2, prevY, prevX + (currX - prevX) / 2, currY);
      g.drawLine(prevX + (currX - prevX) / 2, currY, (isEnd ? axisX.valueToPos(axisX.getActualMaximum()) : currX), currY);

      // upper
      prevY = axisY.valueToPos(XYSequencePoint.toDouble(prevMeta.get("upper")));
      currY = axisY.valueToPos(XYSequencePoint.toDouble(currMeta.get("upper")));
      g.setColor(m_ColorUpper);
      g.drawLine((isStart ? axisX.valueToPos(axisX.getActualMinimum()) : prevX), prevY, prevX + (currX - prevX) / 2, prevY);
      if (prevY != currY)
	g.drawLine(prevX + (currX - prevX) / 2, prevY, prevX + (currX - prevX) / 2, currY);
      g.drawLine(prevX + (currX - prevX) / 2, currY, (isEnd ? axisX.valueToPos(axisX.getActualMaximum()) : currX), currY);
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
	data = getActualContainerManager().get(i).getData();
	if (data.size() == 0)
	  continue;
	synchronized(data) {
	  drawData(g, moment, data);
	}
      }
    }
  }
}
