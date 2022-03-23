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
 * AbstractStrokeOutlinePlotter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.outline;

import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Ancestor for plotters that define a stroke width.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractStrokeOutlinePlotter
  extends AbstractOutlinePlotter {

  private static final long serialVersionUID = 5687473202923757616L;

  /** the thickness of the stroke. */
  protected float m_StrokeThickness;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"stroke-thickness", "strokeThickness",
	1.0f, 0.01f, null);
  }

  /**
   * Returns the current stroke thickness.
   *
   * @return		the thickness
   */
  public float getStrokeThickness() {
    return m_StrokeThickness;
  }

  /**
   * Sets the stroke thickness to use.
   *
   * @param value	the thickness
   */
  public void setStrokeThickness(float value) {
    m_StrokeThickness = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String strokeThicknessTipText() {
    return "The thickness of the stroke.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  protected String generateQuickInfo() {
    return QuickInfoHelper.toString(this, "strokeThickness", m_StrokeThickness, "stroke: ");
  }

  /**
   * Returns the thickness of the stroke.
   *
   * @param g		graphics context to get the thickness from
   * @param defValue	the default value to return in case of failure
   * @return		the stroke, default value if failed to extract
   */
  protected float getStrokeWidth(Graphics g, float defValue) {
    Graphics2D 	g2d;

    if (g instanceof Graphics2D) {
      g2d = (Graphics2D) g;
      if (g2d.getStroke() instanceof BasicStroke)
	return ((BasicStroke) g2d.getStroke()).getLineWidth();
    }

    return defValue;
  }

  /**
   * Applies the stroke thickness.
   *
   * @param stroke	the thickness to apply
   */
  protected void applyStroke(Graphics g, float stroke) {
    Graphics2D 	g2d;

    if (g instanceof Graphics2D) {
      g2d = (Graphics2D) g;
      g2d.setStroke(new BasicStroke(stroke));
    }
  }

  /**
   * Plots the outline.
   *
   * @param object the object to plot
   * @param color  the color to use
   * @param g      the graphics context
   */
  protected abstract void doPlot(LocatedObject object, Color color, Graphics2D g);

  /**
   * Plots the outline.
   *
   * @param object the object to plot
   * @param color  the color to use
   * @param g      the graphics context
   */
  @Override
  protected void doPlotOutline(LocatedObject object, Color color, Graphics2D g) {
    float	width;

    width = getStrokeWidth(g, 1.0f);
    applyStroke(g, m_StrokeThickness);
    doPlot(object, color, g);
    applyStroke(g, width);
  }
}
