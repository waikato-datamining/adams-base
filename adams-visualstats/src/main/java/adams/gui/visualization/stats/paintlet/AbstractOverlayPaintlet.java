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
 * AbstractOverlayPaintlet.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

import java.awt.Graphics;

/**
 * Abstract class for paintlets that draw the overlays.
 *
 * @author msf8
 * @version $Revision$
 */
public abstract class AbstractOverlayPaintlet
extends AbstractColorPaintlet{

  /** for serialization */
  private static final long serialVersionUID = -9116218505488800869L;

  /**Index of the attribute displayed on the x axis */
  protected int m_XInd;

  /**Index of the attribute displayed on the y axis */
  protected int m_YInd;

  /** Whether the overlay has been calculated */
  protected boolean m_Calculated = false;

  /** X axis of scatter plot */
  protected AxisPanel m_AxisBottom;

  /** y axis of scatter plot */
  protected AxisPanel m_AxisLeft;

  public PaintMoment getPaintMoment() {
    return PaintMoment.POST_PAINT;
  }

  /**
   * Pass the parameters required by the overlay paintlet
   * @param data		instances to plot
   * @param x			Index of attribute on x axis
   * @param y			Index of attribute on y axis
   */
  public void parameters(SpreadSheet data, int x, int y) {
    m_Data = data;
    m_XInd = x;
    m_YInd = y;
    m_Calculated = false;
  }

  /**
   * Set the window size, only applicable for the lowess overlay paintlet
   * @param val				int size of window
   */
  public void setWindowSize(int val) {}

  /**
   * Set whether an indicator should be shown on the sides
   * @param val			true if indicator shown
   */
  public void setIndicator(boolean val){}

  /**
   * Calculates the data for the paintlet, doesn't handle the drawing
   * of the overlay. Sometimes just initializes the axispanels
   */
  public void calculate() {
    m_AxisBottom = getPanel().getPlot().getAxis(Axis.BOTTOM);
    m_AxisLeft = getPanel().getPlot().getAxis(Axis.LEFT);
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

  /**Draw the overlay onto the scatter plot, only calculates the
   * value to position in this method
   * @param g		Graphics drawn on
   */
  protected abstract void drawData(Graphics g);

  /**
   * set whether the paintlet has been calculated
   * @param val			True if paintlet calculated
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