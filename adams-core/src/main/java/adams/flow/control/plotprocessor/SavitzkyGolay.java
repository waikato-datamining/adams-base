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
 * SavitzkyGolay.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control.plotprocessor;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.flow.container.SequencePlotterContainer;

/**
 <!-- globalinfo-start -->
 * A processor that applies SavitzkyGolay smoothing.<br>
 * <br>
 * For more information see:<br>
 * <br>
 * A. Savitzky, Marcel J.E. Golay (1964). Smoothing and Differentiation of Data by Simplified Least Squares Procedures. Analytical Chemistry. 36:1627-1639.<br>
 * <br>
 * William H. Press, Saul A. Teukolsky, William T. Vetterling, Brian P. Flannery (1992). Savitzky-Golay Smoothing Filters.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;article{Savitzky1964,
 *    author = {A. Savitzky and Marcel J.E. Golay},
 *    journal = {Analytical Chemistry},
 *    pages = {1627-1639},
 *    title = {Smoothing and Differentiation of Data by Simplified Least Squares Procedures},
 *    volume = {36},
 *    year = {1964},
 *    HTTP = {http:&#47;&#47;dx.doi.org&#47;10.1021&#47;ac60214a047}
 * }
 * 
 * &#64;inbook{Press1992,
 *    author = {William H. Press and Saul A. Teukolsky and William T. Vetterling and Brian P. Flannery},
 *    chapter = {14.8},
 *    edition = {Second},
 *    pages = {650-655},
 *    publisher = {Cambridge University Press},
 *    series = {Numerical Recipes in C},
 *    title = {Savitzky-Golay Smoothing Filters},
 *    year = {1992},
 *    PDF = {http:&#47;&#47;www.nrbook.com&#47;a&#47;bookcpdf&#47;c14-8.pdf}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
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
 * <pre>-polynomial &lt;int&gt; (property: polynomialOrder)
 * &nbsp;&nbsp;&nbsp;The polynomial order to use, must be at least 2.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 2
 * </pre>
 * 
 * <pre>-derivative &lt;int&gt; (property: derivativeOrder)
 * &nbsp;&nbsp;&nbsp;The order of the derivative to use, &gt;= 0.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-left &lt;int&gt; (property: numPointsLeft)
 * &nbsp;&nbsp;&nbsp;The number of points left of a data point, &gt;= 0.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-right &lt;int&gt; (property: numPointsRight)
 * &nbsp;&nbsp;&nbsp;The number of points right of a data point, &gt;= 0.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SavitzkyGolay
  extends AbstractPlotProcessor
  implements TechnicalInformationHandler {
  
  /** for serialization. */
  private static final long serialVersionUID = 5171916489269022308L;

  /** the polynomial order. */
  protected int m_PolynomialOrder;

  /** the order of the derivative. */
  protected int m_DerivativeOrder;

  /** the number of points to the left of a data point. */
  protected int m_NumPointsLeft;

  /** the number of points to the right of a data point. */
  protected int m_NumPointsRight;

  /** the calculated coefficients. */
  protected double[] m_Coefficients;

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
        "A processor that applies SavitzkyGolay smoothing.\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Data         = new ArrayList<Point2D>();
    m_Coefficients = null;
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Data.clear();
    m_Coefficients = null;
  }
  
  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;
    TechnicalInformation 	additional;

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "A. Savitzky and Marcel J.E. Golay");
    result.setValue(Field.TITLE, "Smoothing and Differentiation of Data by Simplified Least Squares Procedures");
    result.setValue(Field.JOURNAL, "Analytical Chemistry");
    result.setValue(Field.VOLUME, "36");
    result.setValue(Field.PAGES, "1627-1639");
    result.setValue(Field.YEAR, "1964");
    result.setValue(Field.HTTP, "http://dx.doi.org/10.1021/ac60214a047");

    additional = result.add(Type.INBOOK);
    additional.setValue(Field.AUTHOR, "William H. Press and Saul A. Teukolsky and William T. Vetterling and Brian P. Flannery");
    additional.setValue(Field.SERIES, "Numerical Recipes in C");
    additional.setValue(Field.EDITION, "Second");
    additional.setValue(Field.TITLE, "Savitzky-Golay Smoothing Filters");
    additional.setValue(Field.CHAPTER, "14.8");
    additional.setValue(Field.PAGES, "650-655");
    additional.setValue(Field.YEAR, "1992");
    additional.setValue(Field.PUBLISHER, "Cambridge University Press");
    additional.setValue(Field.PDF, "http://www.nrbook.com/a/bookcpdf/c14-8.pdf");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "polynomial", "polynomialOrder",
	    2, 2, null);

    m_OptionManager.add(
	    "derivative", "derivativeOrder",
	    1, 0, null);

    m_OptionManager.add(
	    "left", "numPointsLeft",
	    3, 0, null);

    m_OptionManager.add(
	    "right", "numPointsRight",
	    3, 0, null);
  }

  /**
   * Resets the coefficients.
   */
  public void resetCoefficients() {
    m_Coefficients = null;
  }

  /**
   * Sets the polynomial order.
   *
   * @param value 	the order
   */
  public void setPolynomialOrder(int value) {
    if (value >= 2) {
      m_PolynomialOrder = value;
      reset();
      resetCoefficients();
    }
    else {
      getLogger().severe(
	  "The polynomial order must be at least 2 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the polynominal order.
   *
   * @return 		the order
   */
  public int getPolynomialOrder() {
    return m_PolynomialOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String polynomialOrderTipText() {
    return "The polynomial order to use, must be at least 2.";
  }

  /**
   * Sets the order of the derivative.
   *
   * @param value 	the order
   */
  public void setDerivativeOrder(int value) {
    if (value >= 0) {
      m_DerivativeOrder = value;
      reset();
      resetCoefficients();
    }
    else {
      getLogger().severe(
	  "The order of the derivative must be at least 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the order of the derivative.
   *
   * @return 		the order
   */
  public int getDerivativeOrder() {
    return m_DerivativeOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String derivativeOrderTipText() {
    return "The order of the derivative to use, >= 0.";
  }

  /**
   * Sets the number of points to the left of a data point.
   *
   * @param value 	the number of points
   */
  public void setNumPointsLeft(int value) {
    if (value >= 0) {
      m_NumPointsLeft = value;
      reset();
      resetCoefficients();
    }
    else {
      getLogger().severe(
	  "The number of points to the left must be at least 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the number of points to the left of a data point.
   *
   * @return 		the number of points
   */
  public int getNumPointsLeft() {
    return m_NumPointsLeft;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsLeftTipText() {
    return "The number of points left of a data point, >= 0.";
  }

  /**
   * Sets the number of points to the right of a data point.
   *
   * @param value 	the number of points
   */
  public void setNumPointsRight(int value) {
    if (value >= 0) {
      m_NumPointsRight = value;
      reset();
      resetCoefficients();
    }
    else {
      getLogger().severe(
	  "The number of points to the right must be at least 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the number of points to the right of a data point.
   *
   * @return 		the number of points
   */
  public int getNumPointsRight() {
    return m_NumPointsRight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsRightTipText() {
    return "The number of points right of a data point, >= 0.";
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
    result += QuickInfoHelper.toString(this, "polynomialOrder", m_PolynomialOrder, ", PO: ");
    result += QuickInfoHelper.toString(this, "derivativeOrder", m_DerivativeOrder, ", DO: ");
    result += QuickInfoHelper.toString(this, "numPointsLeft", m_NumPointsLeft, ", L: ");
    result += QuickInfoHelper.toString(this, "numPointsRight", m_NumPointsLeft, ", R: ");
    
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
    int					width;
    int					i;
    int					n;
    double				value;

    result = null;
    
    x = (Comparable) cont.getValue(SequencePlotterContainer.VALUE_X);
    y = (Comparable) cont.getValue(SequencePlotterContainer.VALUE_Y);
    
    if ((x instanceof Number) && (y instanceof Number)) {
      point = new Point2D.Double(((Number) x).doubleValue(), ((Number) y).doubleValue());
      m_Data.add(point);
      while (m_Data.size() > m_NumPointsLeft + m_NumPointsRight + 1)
	m_Data.remove(0);
      if (m_Data.size() == m_NumPointsLeft + m_NumPointsRight + 1) {
	if (m_Coefficients == null) {
	  m_Coefficients = adams.data.utils.SavitzkyGolay.determineCoefficients(
	      m_NumPointsLeft, m_NumPointsRight, m_PolynomialOrder, m_DerivativeOrder, isLoggingEnabled());
	}
	result = new ArrayList<SequencePlotterContainer>();
	width  = m_NumPointsLeft + m_NumPointsRight + 1;
	for (i = 0; i <= m_Data.size() - width; i++) {
	  // apply coefficients to window
	  value = 0;
	  for (n = 0; n < width; n++)
	    value += m_Coefficients[n] * m_Data.get(i + n).getY();

	  result.add(new SequencePlotterContainer(getPlotName(cont), m_Data.get(i + m_NumPointsLeft).getX(), value, getPlotType()));
	}
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
