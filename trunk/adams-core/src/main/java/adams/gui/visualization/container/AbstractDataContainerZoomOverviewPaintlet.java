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
 * AbstractDataContainerZoomOverviewPaintlet.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.container;

import java.awt.Color;
import java.awt.Graphics;

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractPaintlet;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 * Highlights the current zoom in the data container panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <P> the type of DataContainerPanel to use
 */
public abstract class AbstractDataContainerZoomOverviewPaintlet<P extends DataContainerPanel>
  extends AbstractPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -3979473621483079352L;
  
  /** the background color. */
  protected Color m_HighlightColor;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Highlights the section that is currently zoomed in.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "highlight-color", "highlightColor",
	    Color.LIGHT_GRAY);
  }

  /**
   * Sets the color for the zoom highlight.
   *
   * @param value	the color
   */
  public void setHighlightColor(Color value) {
    m_HighlightColor = value;
    memberChanged();
  }

  /**
   * Returns the color for the zoom highlight.
   *
   * @return		the color
   */
  public Color getHighlightColor() {
    return m_HighlightColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundColorTipText() {
    return "The color for the zoom highlight.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  public PaintMoment getPaintMoment() {
    return PaintMoment.BACKGROUND;
  }

  /**
   * Returns the panel to obtain plot and containers from.
   * 
   * @return		the panel
   */
  protected abstract P getContainerPanel();
  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  public void performPaint(Graphics g, PaintMoment moment) {
    P		panel;
    AxisPanel	axis;
    double	min;
    double	max;
    
    panel = getContainerPanel();
    if (panel == null)
      return;
    
    // get min/max of current zoom
    axis = panel.getPlot().getAxis(Axis.BOTTOM);
    if (!axis.isZoomed())
      return;
    min  = axis.getAxisModel().getActualMinimum();
    max  = axis.getAxisModel().getActualMaximum();
    
    // plot min/max in unzoomed panel
    axis = getPlot().getAxis(Axis.BOTTOM);
    g.setColor(m_HighlightColor);
    g.fillRect(
	axis.valueToPos(min), 
	0, 
	axis.valueToPos(max) - axis.valueToPos(min) + 1, 
	getPanel().getHeight());
  }
}
