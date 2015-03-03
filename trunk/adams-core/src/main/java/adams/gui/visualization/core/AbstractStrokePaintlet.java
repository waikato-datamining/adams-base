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
 * AbstractStrokePaintlet.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Ancestor for paintlets that paint lines of some sort.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractStrokePaintlet
  extends AbstractPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 1704075176011969771L;
  
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
   * Sets the stroke thickness to use.
   *
   * @param value	the thickness
   */
  public void setStrokeThickness(float value) {
    m_StrokeThickness = value;
    memberChanged();
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
   * Returns the current stroke thickness.
   *
   * @return		the thickness
   */
  public float getStrokeThickness() {
    return m_StrokeThickness;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @see		#isEnabled()
   */
  public void paint(Graphics g) {
    Graphics2D 	g2d;
    float	width;

    if (isEnabled() && hasPanel()) {
      g2d   = null;
      width = 1.0f;

      // set width
      if (g instanceof Graphics2D) {
	g2d = (Graphics2D) g;
	if (g2d.getStroke() instanceof BasicStroke)
	  width = ((BasicStroke) g2d.getStroke()).getLineWidth();
	g2d.setStroke(new BasicStroke(m_StrokeThickness));
      }

      // paint
      performPaint(g, getPaintMoment());

      // restore width
      if (g2d != null)
	g2d.setStroke(new BasicStroke(width));
    }
  }
}
