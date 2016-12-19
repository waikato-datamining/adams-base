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
 * Expression.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control.plotprocessor;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.container.SequencePlotterContainer;
import adams.parser.MathematicalExpression;
import adams.parser.MathematicalExpressionText;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies a mathematical function to the data (separately for X and Y).<br>
 * Values can be accessed using 'xN' and 'yN' with 'N' being the 1-based index in the current window.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-plot-name-suffix &lt;java.lang.String&gt; (property: plotNameSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix for the plot name; if left empty, the plot container automatically 
 * &nbsp;&nbsp;&nbsp;becomes an OVERLAY.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-window-size &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;The window size to use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-x-expression &lt;adams.parser.MathematicalExpressionText&gt; (property: XExpression)
 * &nbsp;&nbsp;&nbsp;The expression to apply to the X data.
 * &nbsp;&nbsp;&nbsp;default: x1
 * </pre>
 * 
 * <pre>-y-expression &lt;adams.parser.MathematicalExpressionText&gt; (property: YExpression)
 * &nbsp;&nbsp;&nbsp;The expression to apply to the Y data.
 * &nbsp;&nbsp;&nbsp;default: y1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Expression
  extends AbstractPlotProcessor {
  
  /** for serialization. */
  private static final long serialVersionUID = 5171916489269022308L;
  
  /** Size of window size for calculating lowess. */
  protected int m_WindowSize;

  /** the expression for X. */
  protected MathematicalExpressionText m_XExpression;

  /** the expression for Y. */
  protected MathematicalExpressionText m_YExpression;

  /** for storing the plot data. */
  protected List<Point2D> m_Data;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies a mathematical function to the data (separately for X and Y).\n"
	+ "Values can be accessed using 'xN' and 'yN' with 'N' being the "
	+ "1-based index in the current window.";
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Data = new ArrayList<>();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Data.clear();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "window-size", "windowSize",
      1, 1, null);

    m_OptionManager.add(
      "x-expression", "XExpression",
      new MathematicalExpressionText("x1"));

    m_OptionManager.add(
      "y-expression", "YExpression",
      new MathematicalExpressionText("y1"));
  }

  /**
   * Sets the polynomial order.
   *
   * @param value 	the order
   */
  public void setWindowSize(int value) {
    if (getOptionManager().isValid("windowSize", value)) {
      m_WindowSize = value;
      reset();
    }
  }

  /**
   * Returns the polynominal order.
   *
   * @return 		the order
   */
  public int getWindowSize() {
    return m_WindowSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowSizeTipText() {
    return "The window size to use.";
  }

  /**
   * Sets the expression for the X values.
   *
   * @param value 	the expression
   */
  public void setXExpression(MathematicalExpressionText value) {
    m_XExpression = value;
    reset();
  }

  /**
   * Returns the expression for the X values.
   *
   * @return 		the expression
   */
  public MathematicalExpressionText getXExpression() {
    return m_XExpression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XExpressionTipText() {
    return "The expression to apply to the X data.";
  }

  /**
   * Sets the expression for the Y values.
   *
   * @param value 	the expression
   */
  public void setYExpression(MathematicalExpressionText value) {
    m_YExpression = value;
    reset();
  }

  /**
   * Returns the expression for the Y values.
   *
   * @return 		the expression
   */
  public MathematicalExpressionText getYExpression() {
    return m_YExpression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YExpressionTipText() {
    return "The expression to apply to the Y data.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "windowSize", m_WindowSize, ", window: ");
    result += QuickInfoHelper.toString(this, "XExpression", m_XExpression, ", x: ");
    result += QuickInfoHelper.toString(this, "YExpression", m_YExpression, ", y: ");

    return result;
  }
  
  /**
   * Processes the provided container. Generates new containers
   * if applicable.
   * 
   * @param cont	the container to process
   * @return		null if no new containers were produced
   */
  @Override
  protected List<SequencePlotterContainer> doProcess(SequencePlotterContainer cont) {
    List<SequencePlotterContainer>	result;
    Point2D				point;
    Comparable				x;
    Comparable				y;
    int					i;
    HashMap				symbols;
    
    result = null;

    x = (Comparable) cont.getValue(SequencePlotterContainer.VALUE_X);
    y = (Comparable) cont.getValue(SequencePlotterContainer.VALUE_Y);
    
    if ((x instanceof Number) && (y instanceof Number)) {
      point = new Point2D.Double(((Number) x).doubleValue(), ((Number) y).doubleValue());
      m_Data.add(point);
      while (m_Data.size() > m_WindowSize)
	m_Data.remove(0);
      if (m_Data.size() == m_WindowSize) {
	// X
	x       = null;
	symbols = new HashMap();
	for (i = 0; i < m_Data.size(); i++)
	  symbols.put("x" + (i+1), m_Data.get(i).getX());
	try {
	  x = MathematicalExpression.evaluate(m_XExpression.getValue(), symbols);
	}
	catch (Exception e) {
	  m_LastError = Utils.handleException(this, "Failed to evaluate x expression: " + m_XExpression + "\nusing: " + symbols, e);
	  return null;
	}
	// Y
	y       = null;
	symbols = new HashMap();
	for (i = 0; i < m_Data.size(); i++)
	  symbols.put("y" + (i+1), m_Data.get(i).getY());
	try {
	  y = MathematicalExpression.evaluate(m_YExpression.getValue(), symbols);
	}
	catch (Exception e) {
	  m_LastError = Utils.handleException(this, "Failed to evaluate y expression: " + m_YExpression + "\nusing: " + symbols, e);
	  return null;
	}
	result = new ArrayList<>();
	result.add(new SequencePlotterContainer(getPlotName(cont), ((Number) x).doubleValue(), ((Number) y).doubleValue(), getPlotType()));
      }
    }
    
    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    
    m_Data.clear();
  }
}
