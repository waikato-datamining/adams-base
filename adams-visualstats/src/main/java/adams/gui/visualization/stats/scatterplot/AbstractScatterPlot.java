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
 * AbstractScatterPlot.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.plot.AbstractHitDetector;
import adams.gui.visualization.core.plot.HitDetectorSupporter;
import adams.gui.visualization.core.plot.TipTextCustomizer;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Date;

/**
 * Abstract class for displaying a single scatterplotpanel on a paintable panel.
 *
 * @author msf8
 * @version $Revision$
 */
public abstract class AbstractScatterPlot
  extends PaintablePanel
  implements TipTextCustomizer {

  /** for serialization */
  private static final long serialVersionUID = -3526702766287841051L;

  /**Scatter plot panel to display */
  protected ScatterPlotPanel m_Plot;

  /**Instances to be plotted */
  protected SpreadSheet m_Data;

  /**Index of attribute to be displayed on x axis */
  protected int m_XIndex = 0;

  /**Index of attribute to be displayed on y axis */
  protected  int m_YIndex = 0;

  /**Paintlet to display the data points on the scatter plot */
  protected AbstractScatterPlotPaintlet m_Paintlet;

  /** Array of overlay options */
  protected AbstractScatterPlotOverlay[] m_Overlays;

  public PlotPanel getPlot() {
    return m_Plot;
  }

  /**
   * Set the paintlet for dawing the data
   * @param val			Paintlet for plotting
   */
  public void setPaintlet(AbstractScatterPlotPaintlet val) {
    removePaintlet(m_Paintlet);
    m_Paintlet = val;
  }

  protected boolean canPaint(Graphics g) {
    if(m_Plot != null && m_Data != null)
      return true;
    else
      return false;
  }

  /**
   * Sets the instances to be plotted
   * @param data	Instances to be plotted
   */
  public void setData(SpreadSheet data) {
    m_Data = data;
  }

  /**
   * Get the instances being plotted
   * @return		Instances being plotted
   */
  public SpreadSheet getData() {
    return m_Data;
  }

  /**
   * gets the index of the x attribute
   * @return		index of x attribute
   */
  public int getX_Index() {
    return m_XIndex;
  }

  /**
   * Sets the index of the y attribute
   * @return		index of y attribute
   */
  public int getY_Index() {
    return m_YIndex;
  }

  /**
   * Removes all overlays from the scatterplot. Removes the paintlets that
   * do the drawing as well as resetting the array
   */
  public void removeOverlays() {
    if(m_Overlays != null) {
      for(int i = 0; i< m_Overlays.length; i++) {
	removePaintlet(m_Overlays[i].getPaintlet());
      }
      m_Overlays = null;
    }
  }

  /**
   * Set the index of the attribute to be displayed on
   * the x axis
   * @param x		0-based index of attribute
   */
  public void setX(int x) {
    m_XIndex = x;
    update();
  }

  /**
   * Set the index of the attribute to be displayed on
   * the y axis
   * @param y		0-based index of attribute
   */
  public void setY(int y) {
    m_YIndex = y;
    update();
  }

  /**
   * Processes the given tip text. Among the current mouse position, the
   * panel that initiated the call are also provided.
   *
   * @param panel	the content panel that initiated this call
   * @param mouse	the mouse position
   * @param tiptext	the tiptext so far
   * @return		the processed tiptext
   */
  public String processTipText(PlotPanel panel, Point mouse, String tiptext) {
    String			result;
    MouseEvent 			event;
    String			hit;
    AbstractHitDetector 	detector;

    result = tiptext;
    event  = new MouseEvent(
      getPlot().getContent(),
      MouseEvent.MOUSE_MOVED,
      new Date().getTime(),
      0,
      (int) mouse.getX(),
      (int) mouse.getY(),
      0,
      false);

    detector = ((HitDetectorSupporter) m_Paintlet).getHitDetector();
    if (detector != null) {
      hit = (String) detector.detect(event);
      if (hit != null) {
	if (result == null)
	  result = hit;
	else
	  result = " (" + hit + ")";
      }
    }

    return result;
  }
}