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
 * BarPaintlet.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequenceUtils;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Paintlet for painting a bar plot for a sequence.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
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
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the bar in pixel.
 * &nbsp;&nbsp;&nbsp;default: 20
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-paint-all (property: paintAll)
 * &nbsp;&nbsp;&nbsp;If set to true, all data points will be painted, regardless whether they 
 * &nbsp;&nbsp;&nbsp;are visible or not.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BarPaintlet
  extends AbstractXYSequencePaintlet
  implements PaintletWithCustomDataSupport {

  /** for serialization. */
  private static final long serialVersionUID = 8968797530613834056L;

  /** whether to paint all the data points (no optimization). */
  protected boolean m_PaintAll;

  /** the width of the bar. */
  protected int m_Width;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paintlet for painting a bar plot for a sequence.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "width", "width",
	    20, 1, null);

    m_OptionManager.add(
	    "paint-all", "paintAll",
	    false);
  }

  /**
   * Sets the width of the bar.
   *
   * @param value	width in pixel
   */
  public void setWidth(int value) {
    m_Width = value;
    memberChanged();
  }

  /**
   * Returns the width of the bar.
   *
   * @return		width in pixel
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the bar in pixel.";
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
   * Returns whether marker shapes are disabled.
   *
   * @return		true if marker shapes are disabled
   */
  public boolean getPaintAll() {
    return m_PaintAll;
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
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    BarHitDetector	result;
    
    result = new BarHitDetector(getSequencePanel());
    result.setWidth(m_Width);
    
    return result;
  }
  
  /**
   * Updates the settings of the hit detector.
   */
  @Override
  protected void updateHitDetector() {
    ((BarHitDetector) m_HitDetector).setWidth(m_Width);
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
    List<XYSequencePoint>	points;
    XYSequencePoint		curr;
    int				currX;
    int				currY;
    int				prevX;
    AxisPanel			axisX;
    AxisPanel			axisY;
    int				i;
    int				start;
    int				end;

    points = data.toList();
    axisX  = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY  = getPanel().getPlot().getAxis(Axis.LEFT);

    // paint all points
    g.setColor(color);

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

    currX  = Integer.MIN_VALUE;
    currY  = Integer.MIN_VALUE;
    prevX  = axisX.valueToPos(points.get(start).getX());

    for (i = start; i <= end; i++) {
      curr = (XYSequencePoint) points.get(i);

      // determine coordinates
      currX = axisX.valueToPos(XYSequencePoint.toDouble(curr.getX()));
      if (!m_PaintAll) {
	if ((i != start) && (i != end) && (currX + m_Width < prevX))
	  continue;
      }
      currY = axisY.valueToPos(XYSequencePoint.toDouble(curr.getY()));

      // draw rectangle
      g.fillRect(currX - (m_Width / 2), Math.min(axisY.valueToPos(0), currY), m_Width, Math.abs(axisY.valueToPos(0) - currY));

      prevX = currX;
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
    XYSequence	data;

    // paint all points
    synchronized(getActualContainerManager()) {
      for (i = 0; i < getActualContainerManager().count(); i++) {
	if (!getActualContainerManager().isVisible(i))
	  continue;
	data = getActualContainerManager().get(i).getData();
	if (data.size() == 0)
	  continue;
	synchronized(data) {
	  drawCustomData(g, moment, data, getColor(i));
	}
      }
    }
  }
}
