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
 * AbstractScatterPlotOverlay.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.visualization.stats.paintlet.AbstractOverlayPaintlet;

import java.awt.Color;

/**
 * Abstract superclass for overlays on the scatterplot graph.
 *
 * @author msf8
 * @version $Revision$
 */
public abstract class AbstractScatterPlotOverlay
extends AbstractOptionHandler {

  /** parent scatter plot to plot data on */
  protected AbstractScatterPlot m_Parent;

  /**Instances object containing data */
  protected SpreadSheet m_Data;

  /**Paintlet to do the drawing */
  protected AbstractOverlayPaintlet m_Paintlet;

  /**Thickness for overlay line */
  protected float m_Thickness;

  /**Color of the overlay */
  protected Color m_Color;

  /** for serialization */
  private static final long serialVersionUID = 7526127735639196077L;

  /**Returns a string to display info on the overlay */
  public String name() {
    return this.toString();
  }

  public void defineOptions() {
    super.defineOptions();
    //thickness of overlay line
    m_OptionManager.add(
	"line-thickness", "thickness",
	2.0f, 1.0f, 5.0f);
    //color of overlay line
    m_OptionManager.add(
	"color", "color", Color.BLUE);
  }

  /**
   * Set the thickness of the overlay
   * @param val			Thickness in pixels
   */
  public void setThickness(float val) {
    m_Thickness = val;
  }

  /**
   * Get the thickness of the overlay
   * @return			Thickness in pixels
   */
  public float getThickness() {
    return m_Thickness;
  }

  /**
   * Return a tip text for the thickness property
   * @return		Tip text string
   */
  public String thicknessTipText() {
    return "Thickness of the overlay line";
  }

  /**set up the overlay and it's paintlet*/
  public abstract void setUp();

  /**
   * Return the scatter plot parent the overlay is being drawn on
   * @return		scatter plot
   */
  protected AbstractScatterPlot getParent()
  {
    return m_Parent;
  }

  /**Set the scatterplot that this overlay is being drawn on */
  public void setParent(AbstractScatterPlot val)
  {
    m_Parent = val;
  }

  /**
   * Pass the instances to the overlay object
   * @param inst		Instances to be plotted
   */
  public void inst(SpreadSheet inst) {
    m_Data = inst;
  }

  /**
   * Get the paintlet that is doing the drawing
   * @return		paintlet doing the drawing
   */
  public AbstractOverlayPaintlet getPaintlet()
  {
    return m_Paintlet;
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractScatterPlotOverlay shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractScatterPlotOverlay shallowCopy(boolean expand) {
    return (AbstractScatterPlotOverlay) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Set the color for this overlay
   * @param val			Color of overlay
   */
  public void setColor(Color val) {
    m_Color = val;
    if(m_Paintlet != null)
      m_Paintlet.setColor(val);
    reset();
  }

  /**
   * Get the color for this overlay
   * @return		Color used for overlay
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Tip text for the color property
   * @return			String describing the property
   */
  public String colorTipText() {
    return "Color of the overlay line";
  }
}