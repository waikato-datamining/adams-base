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
 * AbstractSavitzkyGolay.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.NotesHandler;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;

/**
 * Abstract ancestor for Savitzky-Golay filters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public abstract class AbstractSavitzkyGolay<T extends DataContainer>
  extends AbstractFilter<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7714239052976065971L;

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

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A filter that applies Savitzky-Golay smoothing.\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Coefficients = null;
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
   * Returns the X-value of the DataPoint.
   *
   * @param point	the point to get the X-Value from
   * @return		the X-value
   */
  protected abstract double getValue(DataPoint point);

  /**
   * Creates a new DataPoint based on the old one and the new X value.
   *
   * @param oldPoint	the old DataPoint
   * @param x		the new X value
   * @return		the new DataPoint
   */
  protected abstract DataPoint newDataPoint(DataPoint oldPoint, double x);

  /**
   * Optional post-processing.
   * <p/>
   * Default implementation does nothing.
   *
   * @param oldPoint	the original DataPoint
   * @param newPoint	the new DataPoint
   */
  protected void postProcess(DataPoint oldPoint, DataPoint newPoint) {
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T			result;
    int			i;
    int			n;
    int			width;
    List<DataPoint>	points;
    DataPoint		newPoint;
    double		value;

    if (m_Coefficients == null) {
      if (data.size() < m_NumPointsLeft + m_NumPointsRight + 1) {
	getLogger().severe("Not enough data points: #" + data);
	result = (T) data.getClone();
	if (result instanceof NotesHandler)
	  ((NotesHandler) result).getNotes().addWarning(this.getClass(), "Not enough data points!");
	return result;
      }
      m_Coefficients = adams.data.utils.SavitzkyGolay.determineCoefficients(
	  m_NumPointsLeft, m_NumPointsRight, m_PolynomialOrder, m_DerivativeOrder, isLoggingEnabled());
    }

    result = (T) data.getHeader();
    points = data.toList();
    width  = m_NumPointsLeft + m_NumPointsRight + 1;
    for (i = 0; i <= points.size() - width; i++) {
      // apply coefficients to window
      value = 0;
      for (n = 0; n < width; n++)
	value += m_Coefficients[n] * getValue(points.get(i + n));

      newPoint = newDataPoint(points.get(i + m_NumPointsLeft), value);
      postProcess(points.get(i + m_NumPointsLeft), newPoint);

      result.add(newPoint);
    }

    return result;
  }
}
