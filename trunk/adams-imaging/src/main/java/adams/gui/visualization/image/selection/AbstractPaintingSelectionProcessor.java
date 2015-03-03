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
 * AbstractPaintingSelectionProcessor.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.selection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Ancestor for processors that paint with a certain color and stroke thickness.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPaintingSelectionProcessor
  extends AbstractSelectionProcessor {
  
  /** for serialization. */
  private static final long serialVersionUID = 5301544099367524209L;

  /** the color to use for painting. */
  protected Color m_Color;
  
  /** the thickness of the stroke. */
  protected float m_StrokeThickness;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "color", "color",
	    getDefaultColor());

    m_OptionManager.add(
	    "stroke-thickness", "strokeThickness",
	    getDefaultStrokeThickness(), 0.01f, null);
  }

  /**
   * Returns the default color to use.
   * 
   * @return		the color
   */
  protected Color getDefaultColor() {
    return Color.BLUE;
  }
  
  /**
   * Sets the color to use.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the current color for painting.
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
    return "The color to use for painting.";
  }
  
  /**
   * Returns the default stroke thickness.
   * 
   * @return		the thickness
   */
  protected float getDefaultStrokeThickness() {
    return 1.0f;
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
   * Applies the specified stroke thickness.
   * 
   * @param g		the graphics context
   * @param thickness	the thickness to set
   * @return		the previous thickness
   */
  protected float applyStroke(Graphics g, float thickness) {
    float	result;
    Graphics2D 	g2d;
        
    result = 1.0f;
    
    if (g instanceof Graphics2D) {
      g2d = (Graphics2D) g;
      if (g2d.getStroke() instanceof BasicStroke)
	result = ((BasicStroke) g2d.getStroke()).getLineWidth();
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
    }
    
    return result;
  }
}
