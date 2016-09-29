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

  /** for serialization */
  private static final long serialVersionUID = 7526127735639196077L;

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

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "line-thickness", "thickness",
      getDefaultThickness(), 0.0f, null);

    m_OptionManager.add(
      "color", "color",
      getDefaultColor());
  }

  /**
   * Returns the default thickness.
   *
   * @return		the default
   */
  protected float getDefaultThickness() {
    return 2.0f;
  }

  /**
   * Set the thickness of the overlay.
   *
   * @param value			Thickness in pixels
   */
  public void setThickness(float value) {
    m_Thickness = value;
    reset();
  }

  /**
   * Get the thickness of the overlay.
   *
   * @return			Thickness in pixels
   */
  public float getThickness() {
    return m_Thickness;
  }

  /**
   * Return a tip text for the thickness property.
   *
   * @return		Tip text string
   */
  public String thicknessTipText() {
    return "Thickness of the overlay line";
  }

  /**
   * Returns the default color.
   *
   * @return		the default
   */
  protected Color getDefaultColor() {
    return Color.BLUE;
  }

  /**
   * Set the color for this overlay.
   *
   * @param value			Color of overlay
   */
  public void setColor(Color value) {
    m_Color = value;
    if (m_Paintlet != null)
      m_Paintlet.setColor(value);
    reset();
  }

  /**
   * Get the color for this overlay.
   *
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

  /**
   * set up the overlay and its paintlet.
   */
  public abstract void setUp();

  /**
   * Return the scatter plot parent the overlay is being drawn on.
   *
   * @return		scatter plot
   */
  protected AbstractScatterPlot getParent() {
    return m_Parent;
  }

  /**
   * Set the scatterplot that this overlay is being drawn on.
   */
  public void setParent(AbstractScatterPlot val) {
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
  public AbstractOverlayPaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns a string to display info on the overlay
   */
  public String name() {
    return toString();
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
}