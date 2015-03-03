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
 * ErrorBandPaintlet.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import gnu.trove.list.array.TIntArrayList;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 * Plots a band around the values, using the Y errors to define the width of
 * the band.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7793 $
 */
public class ErrorBandPaintlet
  extends AbstractErrorPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -713940308371660030L;
  
  /** the alpha value to use for the band. */
  protected int m_Alpha;
  
  /** the color to use for the band. */
  protected HashMap<Color,Color> m_BandColors;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints a 'band' (or polygon) using the errors as the corners.";
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_BandColors = new HashMap<Color,Color>();
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_BandColors.clear();;
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	    "alpha", "alpha",
	    63, 0, 255);
  }

  /**
   * Sets the alpha value for the band (0-255).
   *
   * @param value 	the alpha value
   */
  public void setAlpha(int value) {
    if ((value >= 0) && (value <= 255)) {
      m_Alpha = value;
      reset();
    }
    else {
      getLogger().warning("Alpha must satisfy 0 <= x <= 255, provided: " + value);
    }
  }

  /**
   * Returns the the alpha value for the band (0-255).
   *
   * @return 		the alpha value
   */
  public int getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alphaTipText() {
    return "The alpha value to use for the band.";
  }

  /**
   * Draws the error data with the given color.
   *
   * @param g		the graphics context
   * @param data	the error data to draw
   * @param color	the color to draw in
   */
  @Override
  protected void drawData(Graphics g, SequencePlotSequence data, Color color) {
    SequencePlotPoint	point;
    AxisPanel		axisX;
    AxisPanel		axisY;
    TIntArrayList	x;
    TIntArrayList	y;
    Double[]		error;
    double		fromY;
    double		toY;
    
    if (!m_BandColors.containsKey(color))
      m_BandColors.put(color, new Color(color.getRed(), color.getGreen(), color.getBlue(), m_Alpha));
    
    g.setColor(m_BandColors.get(color));

    axisX = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY = getPanel().getPlot().getAxis(Axis.LEFT);

    x = new TIntArrayList();
    y = new TIntArrayList();
    for (Object o: data.toList()) {
      if (o instanceof SequencePlotPoint) {
	point = (SequencePlotPoint) o;

	// Y
	error = point.getErrorY();
	if (error.length == 1) {
	  fromY = point.getY() - error[0];
	  toY   = point.getY() + error[0];
	}
	else {
	  fromY = error[0];
	  toY   = error[1];
	}

	x.add(axisX.valueToPos(point.getX())); 
	y.add(axisY.valueToPos(fromY));
	x.insert(0, axisX.valueToPos(point.getX()));
	y.insert(0, axisY.valueToPos(toY));
      }
    }
    
    g.fillPolygon(x.toArray(), y.toArray(), x.size());
  }
}
