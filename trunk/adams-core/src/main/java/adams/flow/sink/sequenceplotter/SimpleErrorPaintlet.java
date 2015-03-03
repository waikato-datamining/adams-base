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
 * SimpleErrorPaintlet.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import java.awt.Color;
import java.awt.Graphics;

import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 * Simple error plots: line, errorbar, box.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see PlotType
 */
public class SimpleErrorPaintlet
  extends AbstractErrorPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -713940308371660030L;

  /** how to paint the errors. */
  public enum PlotType {
    /** simple line. */
    LINE,
    /** bars (with markers at end). */
    BAR,
    /** boxes (if x and y error available). */
    BOX
  }
  
  /** the plot type. */
  protected PlotType m_PlotType;
  
  /** the length of the markers in case of {@link PlotType#BAR}. */
  protected int m_MarkerLength;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints simple error bars for X and Y (if available).";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "plot-type", "plotType",
	    PlotType.BAR);
    
    m_OptionManager.add(
	    "marker-length", "markerLength",
	    5, 1, null);
  }

  /**
   * Sets the type of plot.
   *
   * @param value 	the type
   */
  public void setPlotType(PlotType value) {
    m_PlotType = value;
    reset();
  }

  /**
   * Returns the type of plot.
   *
   * @return 		the type
   */
  public PlotType getPlotType() {
    return m_PlotType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotTypeTipText() {
    return 
	"The type of plot to use for displaying the errors; " 
	+ PlotType.BOX + " requires X and Y errors to be present, "
	+ "otherwise it falls back to " + PlotType.LINE + ".";
  }

  /**
   * Sets the length of the markers in case of {@link PlotType#BAR}.
   *
   * @param value 	the length in pixels
   */
  public void setMarkerLength(int value) {
    m_MarkerLength = value;
    reset();
  }

  /**
   * Returns the length of the markers in case of {@link PlotType#BAR}.
   *
   * @return 		the length in pixels
   */
  public int getMarkerLength() {
    return m_MarkerLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String markerLengthTipText() {
    return "The length in pixels for the markers in case of " + PlotType.BAR + ".";
  }

  /**
   * Draws a simple line representing the errors.
   * 
   * @param g		the graphics context
   * @param axisX	the X axis
   * @param axisY	the Y axis
   * @param point	the point to draw
   */
  protected void drawErrorLine(Graphics g, AxisPanel axisX, AxisPanel axisY, SequencePlotPoint point) {
    Double[]	error;
    double	from;
    double	to;
    
    // horizontal
    if (point.hasErrorX()) {
      error = point.getErrorX();
      if (error.length == 1) {
	from = point.getX() - error[0];
	to   = point.getX() + error[0];
      }
      else {
	from = error[0];
	to   = error[1];
      }
      g.drawLine(
	  axisX.valueToPos(from), 
	  axisY.valueToPos(point.getY()), 
	  axisX.valueToPos(to), 
	  axisY.valueToPos(point.getY()));
    }

    // vertical
    if (point.hasErrorY()) {
      error = point.getErrorY();
      if (error.length == 1) {
	from = point.getY() - error[0];
	to   = point.getY() + error[0];
      }
      else {
	from = error[0];
	to   = error[1];
      }
      g.drawLine(
	  axisX.valueToPos(point.getX()), 
	  axisY.valueToPos(from), 
	  axisX.valueToPos(point.getX()),
	  axisY.valueToPos(to));
    }
  }

  /**
   * Draws a bars representing the errors.
   * 
   * @param g		the graphics context
   * @param axisX	the X axis
   * @param axisY	the Y axis
   * @param point	the point to draw
   */
  protected void drawErrorBar(Graphics g, AxisPanel axisX, AxisPanel axisY, SequencePlotPoint point) {
    Double[]	error;
    double	from;
    double	to;
    int		length;
    
    length = (int) (((float) m_MarkerLength - m_StrokeThickness) / 2.0);
    
    // horizontal
    if (point.hasErrorX()) {
      error = point.getErrorX();
      if (error.length == 1) {
	from = point.getX() - error[0];
	to   = point.getX() + error[0];
      }
      else {
	from = error[0];
	to   = error[1];
      }
      g.drawLine(
	  axisX.valueToPos(from), 
	  axisY.valueToPos(point.getY()), 
	  axisX.valueToPos(to), 
	  axisY.valueToPos(point.getY()));
      // left
      g.drawLine(
	  axisX.valueToPos(from), 
	  axisY.valueToPos(point.getY()) - length, 
	  axisX.valueToPos(from), 
	  axisY.valueToPos(point.getY()) + length);
      // right
      g.drawLine(
	  axisX.valueToPos(to), 
	  axisY.valueToPos(point.getY()) - length, 
	  axisX.valueToPos(to), 
	  axisY.valueToPos(point.getY()) + length);
    }
    
    // vertical
    if (point.hasErrorY()) {
      error = point.getErrorY();
      if (error.length == 1) {
	from = point.getY() - error[0];
	to   = point.getY() + error[0];
      }
      else {
	from = error[0];
	to   = error[1];
      }
      g.drawLine(
	  axisX.valueToPos(point.getX()), 
	  axisY.valueToPos(from), 
	  axisX.valueToPos(point.getX()),
	  axisY.valueToPos(to));
      // top
      g.drawLine(
	  axisX.valueToPos(point.getX()) - length, 
	  axisY.valueToPos(from), 
	  axisX.valueToPos(point.getX()) + length,
	  axisY.valueToPos(from));
      // bottom
      g.drawLine(
	  axisX.valueToPos(point.getX()) - length, 
	  axisY.valueToPos(to), 
	  axisX.valueToPos(point.getX()) + length,
	  axisY.valueToPos(to));
    }
  }

  /**
   * Draws a box representing the errors.
   * 
   * @param g		the graphics context
   * @param axisX	the X axis
   * @param axisY	the Y axis
   * @param point	the point to draw
   */
  protected void drawErrorBox(Graphics g, AxisPanel axisX, AxisPanel axisY, SequencePlotPoint point) {
    Double[]	error;
    double	fromX;
    double	toX;
    double	fromY;
    double	toY;

    // X
    error = point.getErrorX();
    if (error.length == 1) {
      fromX = point.getX() - error[0];
      toX   = point.getX() + error[0];
    }
    else {
      fromX = error[0];
      toX   = error[1];
    }
    
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

    g.drawRect(
	axisX.valueToPos(fromX), 
	axisY.valueToPos(toY), 
	axisX.valueToPos(toX) - axisX.valueToPos(fromX) + 1, 
	axisY.valueToPos(fromY) - axisY.valueToPos(toY) + 1);
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
    PlotType	type;
    
    g.setColor(color);

    axisX = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY = getPanel().getPlot().getAxis(Axis.LEFT);

    for (Object o: data.toList()) {
      if (o instanceof SequencePlotPoint) {
	point = (SequencePlotPoint) o;
	type  = m_PlotType;
	
	// all data available?
	if (type == PlotType.BOX) {
	  if (!(point.hasErrorX() && point.hasErrorY()))
	    type = PlotType.LINE;
	}

	switch (type) {
	  case LINE:
	    drawErrorLine(g, axisX, axisY, point);
	    break;
	  case BAR:
	    drawErrorBar(g, axisX, axisY, point);
	    break;
	  case BOX:
	    drawErrorBox(g, axisX, axisY, point);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled error bar type: " + type);
	}
      }
    }
  }
}
