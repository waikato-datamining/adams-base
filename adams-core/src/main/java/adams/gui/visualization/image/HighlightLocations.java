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
 * HighlightLocations.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image;

import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Overlay for highlighting points on an image.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HighlightLocations
  extends AbstractImageOverlay {

  private static final long serialVersionUID = -3086529802660906811L;

  /** the list of locations to highlight. */
  protected List<Point> m_Locations;

  /** the thickness of the stroke. */
  protected float m_StrokeThickness;

  /** the diameter of the circle. */
  protected int m_Diameter;

  /** the color of the circle. */
  protected Color m_Color;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Highlights the specified locations in the image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "stroke-thickness", "strokeThickness",
      1.0f, 0.01f, null);

    m_OptionManager.add(
      "diameter", "diameter",
      5, 1, null);

    m_OptionManager.add(
      "color", "color",
      Color.RED);
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
   * Returns the current stroke thickness.
   *
   * @return		the thickness
   */
  public float getStrokeThickness() {
    return m_StrokeThickness;
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
   * Sets the diameter of the circles.
   *
   * @param value	the diameter in pixels
   */
  public void setDiameter(int value) {
    m_Diameter = value;
    reset();
  }

  /**
   * Returns the diameter of the circles.
   *
   * @return		the diameter in pixels
   */
  public int getDiameter() {
    return m_Diameter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diameterTipText() {
    return "The diameter of the circles in pixels.";
  }

  /**
   * Sets the color of the circles.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color of the circles.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color of the circles.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Locations = new ArrayList<>();
  }

  /**
   * Sets the locations to highlight.
   *
   * @param value	the locations
   */
  public void setLocations(List<Point> value) {
    if (value == null)
      m_Locations = new ArrayList<>();
    else
      m_Locations = new ArrayList<>(value);
  }

  /**
   * Returns the current locations that are being highlighted.
   *
   * @return		the locations
   */
  public List<Point> getLocations() {
    return m_Locations;
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected void doImageChanged(PaintPanel panel) {
  }

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected void doPaintOverlay(PaintPanel panel, Graphics g) {
    Graphics2D 	g2d;
    float	width;

    if (m_Locations.size() == 0)
      return;

    g2d   = null;
    width = 1.0f;

    // set width
    if (g instanceof Graphics2D) {
      g2d = (Graphics2D) g;
      if (g2d.getStroke() instanceof BasicStroke)
	width = ((BasicStroke) g2d.getStroke()).getLineWidth();
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
    }

    g.setColor(m_Color);
    for (Point loc: m_Locations) {
      g.drawOval(
	(int) loc.getX() - m_Diameter / 2,
	(int) loc.getY() - m_Diameter / 2,
	m_Diameter,
	m_Diameter);
    }

    // restore width
    if (g2d != null)
      g2d.setStroke(new BasicStroke(width));
  }
}
