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
 * StdDevOverlayPaintlet.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

import gnu.trove.list.array.TDoubleArrayList;

import java.awt.Color;
import java.awt.Graphics;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.statistics.StatUtils;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Draws the standard deviation as straight line.
 * <p/>
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
 * <pre>-factor &lt;double&gt; (property: factor)
 * &nbsp;&nbsp;&nbsp;The factor to multiple the standard deviation, eg to display +&#47;- 2 stdev.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 * <pre>-is-sample &lt;boolean&gt; (property: isSample)
 * &nbsp;&nbsp;&nbsp;If enabled, the data are treated as samples and not as populations.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StdDevOverlayPaintlet
  extends AbstractXYSequencePaintlet
  implements PaintletWithCustomDataSupport {

  /** for serialization. */
  private static final long serialVersionUID = 6292059403058224856L;

  /** the factor to multiply the standard deviation with. */
  protected double m_Factor;
  
  /** whether the arrays are samples or populations. */
  protected boolean m_IsSample;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws the standard deviation as straight line.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "factor", "factor",
	    1.0);

    m_OptionManager.add(
	    "is-sample", "isSample",
	    true);
  }

  /**
   * Sets the factor to multiply the standard deviation with.
   *
   * @param value	the factor
   */
  public void setFactor(double value) {
    m_Factor = value;
    reset();
  }

  /**
   * Returns the factor to multiply the standard deviation with.
   *
   * @return		the factor
   */
  public double getFactor() {
    return m_Factor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String factorTipText() {
    return "The factor to multiple the standard deviation, eg to display +/- 2 stdev.";
  }

  /**
   * Sets whether the data represent samples instead of populations.
   *
   * @param value	true if data are samples and not populations
   */
  public void setIsSample(boolean value) {
    m_IsSample = value;
    reset();
  }

  /**
   * Returns whether the data represent samples instead of populations.
   *
   * @return		true if data are samples and not populations
   */
  public boolean getIsSample() {
    return m_IsSample;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String isSampleTipText() {
    return "If enabled, the data are treated as samples and not as populations.";
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
  public void drawCustomData(Graphics g, PaintMoment moment, XYSequence data, Color color) {
    AxisPanel		xAxis;
    AxisPanel		yAxis;
    TDoubleArrayList	stdevList;
    double		stdev;
    
    xAxis = getPlot().getAxis(Axis.BOTTOM);
    yAxis = getPlot().getAxis(Axis.LEFT);

    // calculate stdev
    stdevList = new TDoubleArrayList();
    for (XYSequencePoint point: data.toList())
      stdevList.add(point.getY());
    stdev = StatUtils.stddev(stdevList.toArray(), m_IsSample) * m_Factor;
    
    g.setColor(color);
    g.drawLine(
	xAxis.valueToPos(xAxis.getActualMinimum()), 
	yAxis.valueToPos(stdev), 
	xAxis.valueToPos(xAxis.getActualMaximum()), 
	yAxis.valueToPos(stdev));
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    int			i;
    XYSequence		data;

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
