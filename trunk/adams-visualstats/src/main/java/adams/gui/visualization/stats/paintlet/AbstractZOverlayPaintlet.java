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
 * AbstractZOverlayPaintlet.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.Graphics;

import weka.core.Instances;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 * abstract class for creating z score overlay paintlets.
 *
 * @author msf8
 * @version $Revision$
 */
public abstract class AbstractZOverlayPaintlet
extends AbstractColorPaintlet{

  /** for serialization */
  private static final long serialVersionUID = 7699839322609153847L;

  /** whether the overlay has been calcualated */
  protected boolean m_Calculated;

  /** index of the attribute being displayed */
  protected int m_Ind;

  /**y axis of plot */
  protected AxisPanel m_AxisLeft;

  /** x axis of plot */
  protected AxisPanel m_AxisBottom;

  public PaintMoment getPaintMoment() {
    return PaintMoment.POST_PAINT;
  }

  protected void initialize() {
    super.initialize();
    m_Calculated = false;
  }

  /**
   * set the number of standard deviations for the overlay
   * only relevant for the stddev overlay
   * @param val			number of std dev from mean
   */
  public abstract void setStd(double val);

  /**
   * Pass the paramters required by the overlay paintlet
   * @param inst				Instances to be plotted
   * @param ind				index of the attribute within the instacnes
   */
  public void parameters(Instances inst, int ind) {
    m_Instances = inst;
    m_Ind = ind;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  public void performPaint(Graphics g, PaintMoment moment) {
    drawData(g);
  }

  /**
   * Draw the overlay onto the z score plot, only calculates
   * the value to position in this method
   * @param g			graphics to draw on
   */
  protected  abstract void drawData(Graphics g);

  /**calculates the data for the paintlet, doesn't handle the
   * drawing of the data
   */
  public void calculate() {
    m_AxisBottom = getPanel().getPlot().getAxis(Axis.BOTTOM);
    m_AxisLeft = getPanel().getPlot().getAxis(Axis.LEFT);
  }

  /**
   * set whether the paintlet has been calculated
   * @param val			Truen if paintlet calculated
   */
  public void setCalculated(boolean val) {
    m_Calculated = val;
    memberChanged();
  }

  /**
   * Get whether the paintlet has been calculated
   * @return			True if paintlet calculated
   */
  public boolean getCalculated() {
    return m_Calculated;
  }
}