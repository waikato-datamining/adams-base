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
 * HorizontalIndicatorPaintlet.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;
import java.awt.Graphics;

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.plot.Axis;

/**
 * Paintlet for painting a horizontal indicator line.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HorizontalIndicatorPaintlet
  extends AbstractStrokePaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 1590879510204857918L;

  /** the color of the indicator. */
  protected Color m_Color;
  
  /** the value where to paint the indicator. */
  protected double m_Value;
  
  /** whether the value represents a percentage (0-1). */
  protected boolean m_Percentage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints a horizontal indicator line at a specified y value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "color", "color",
	    Color.BLUE);

    m_OptionManager.add(
	    "value", "value",
	    1.0, null, null);

    m_OptionManager.add(
	    "percentage", "percentage",
	    false);
  }

  /**
   * Sets the color for the indicator.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    memberChanged();
  }

  /**
   * Returns the indicator for the indicator.
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
    return "The color of the indicator.";
  }

  /**
   * Sets the y-value the indicator paints.
   *
   * @param value	the y-value
   */
  public void setValue(double value) {
    m_Value = value;
    memberChanged();
  }

  /**
   * Returns the y-value the indicator to paint on.
   *
   * @return		the y-value
   */
  public double getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The y-value for the line.";
  }

  /**
   * Sets whether the y-value represents a percentage (0-1) instead of
   * an absolute value.
   *
   * @param value	true if percentage
   */
  public void setPercentage(boolean value) {
    m_Percentage = value;
    memberChanged();
  }

  /**
   * Returns whether the y-value represents a percentage (0-1) instead of
   * an absolute value.
   *
   * @return		true if percentage
   */
  public boolean getPercentage() {
    return m_Percentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentageTipText() {
    return 
	"If enabled, the y-value gets interpreted as percentage (0-1) "
	+ "instead of absolute value; this is based on the min/max of the y axis.";
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.GRID;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    AxisPanel	axis;
    double	pos;
    
    axis = getPanel().getPlot().getAxis(Axis.LEFT);
    
    g.setColor(m_Color);
    if (m_Percentage) {
      pos = (axis.getMaximum() - axis.getMinimum()) * m_Value + axis.getMinimum();
      g.drawLine(
	  0,
	  axis.valueToPos(pos),
	  getPlot().getWidth() - 1,
	  axis.valueToPos(pos));
    }
    else {
      g.drawLine(
	  0,
	  axis.valueToPos(m_Value),
	  getPlot().getWidth() - 1,
	  axis.valueToPos(m_Value));
    }
  }
}
