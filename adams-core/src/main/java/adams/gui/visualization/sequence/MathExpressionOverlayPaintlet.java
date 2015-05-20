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
 * MathExpressionOverlayPaintlet.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

import java.awt.Color;
import java.awt.Graphics;
import java.util.logging.Level;

import adams.core.base.BaseString;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.parser.MathematicalExpression;
import adams.parser.MathematicalExpressionText;

/**
 <!-- globalinfo-start -->
 * Calculates data points using the provided mathematical expression and paints them using the specified paintlet.<br>
 * If the expression generates a NaN ('not a number') the x&#47;y pair gets ignored.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 * 
 * <pre>-expression &lt;adams.parser.MathematicalExpressionText&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The mathematical expression to use for generating the Y values; use 'X' 
 * &nbsp;&nbsp;&nbsp;as the current data point on the X axis in your expression.
 * &nbsp;&nbsp;&nbsp;default: X
 * </pre>
 * 
 * <pre>-num-points &lt;int&gt; (property: numPoints)
 * &nbsp;&nbsp;&nbsp;The number of data points to generate for the overlay.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-paintlet &lt;adams.gui.visualization.sequence.PaintletWithCustomDataSupport&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for painting the generated data points.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.LinePaintlet
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color for the line.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MathExpressionOverlayPaintlet
  extends AbstractXYSequencePaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 6292059403058224856L;

  /** the expression to evaluate. */
  protected MathematicalExpressionText m_Expression;
  
  /** the number of data points to generate. */
  protected int m_NumPoints;

  /** the paintlet to use for painting the data points. */
  protected PaintletWithCustomDataSupport m_Paintlet;
  
  /** the color for the overlay. */
  protected Color m_Color;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Calculates data points using the provided mathematical expression "
	+ "and paints them using the specified paintlet.\n"
	+ "If the expression generates a NaN ('not a number') the x/y pair "
	+ "gets ignored.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"expression", "expression",
	new MathematicalExpressionText("X"));

    m_OptionManager.add(
	"num-points", "numPoints",
	100, 1, null);

    m_OptionManager.add(
	"paintlet", "paintlet",
	new LinePaintlet());
    
    m_OptionManager.add(
	"color", "color", 
	Color.BLACK);
  }

  /**
   * Sets the expression to use for generating the Y values.
   *
   * @param value	the expression
   */
  public void setExpression(MathematicalExpressionText value) {
    m_Expression = value;
    reset();
  }

  /**
   * Returns the expression to use for generating the Y values.
   *
   * @return		the expression
   */
  public MathematicalExpressionText getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return 
	"The mathematical expression to use for generating the Y values; "
	+ "use 'X' as the current data point on the X axis in your expression.";
  }

  /**
   * Sets the number of points to generate.
   *
   * @param value	the number of points
   */
  public void setNumPoints(int value) {
    m_NumPoints = value;
    reset();
  }

  /**
   * Returns the number of points to generate.
   *
   * @return		the number of points
   */
  public int getNumPoints() {
    return m_NumPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsTipText() {
    return "The number of data points to generate for the overlay.";
  }

  /**
   * Sets the paintlet to use for painting the generated data points.
   *
   * @param value	the paintlet
   */
  public void setPaintlet(PaintletWithCustomDataSupport value) {
    m_Paintlet = value;
    if (m_Paintlet != null)
      m_Paintlet.setPanel(null);
    reset();
  }

  /**
   * Returns the paintlet to use for painting the generated data points.
   *
   * @return		the paintlet
   */
  public PaintletWithCustomDataSupport getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText() {
    return "The paintlet to use for painting the generated data points.";
  }

  /**
   * Set the stroke color for the paintlet.
   * 
   * @param value	color of the stroke
   */
  public void setColor(Color value) {
    m_Color = value;
    memberChanged();
  }

  /**
   * Get the stroke color for the paintlet.
   * 
   * @return		color of the stroke
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
    return "The color for the line.";
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
   * Returns a new instance of the hit detector to use.
   *
   * @return		always null
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return null;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    AxisPanel			xAxis;
    XYSequence			data;
    double			xMin;
    double			xMax;
    double			x;
    double			y;
    int				i;
    MathematicalExpression	expr;
    
    xAxis = getPlot().getAxis(Axis.BOTTOM);
    xMin  = xAxis.getActualMinimum();
    xMax  = xAxis.getActualMaximum();
    data  = new XYSequence();
    expr  = new MathematicalExpression();
    expr.setExpression(m_Expression.getValue());
    
    // generate data
    for (i = 0; i < m_NumPoints; i++) {
      x = xMin + (xMax - xMin) / (m_NumPoints - 1) * i;
      expr.setSymbols(new BaseString[]{new BaseString("X=" + x)});
      try {
	y = expr.evaluate();
	if (!Double.isNaN(y))
	  data.add(new XYSequencePoint(x, y));
	if (isLoggingEnabled())
	  getLogger().info("f(" + x + ") = " + y + (Double.isNaN(y) ? " (skipped)" : ""));
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to evaluate '" + m_Expression + "' using x=" + x, e);
      }
    }
    
    // paint data
    m_Paintlet.setPanel(getPanel(), false);
    m_Paintlet.drawCustomData(g, moment, data, m_Color);
    m_Paintlet.setPanel(null);
  }
}
