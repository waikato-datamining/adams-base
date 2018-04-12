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
 * AbstractNumericSummaryStatistic.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

/**
 * Ancestor for summary statistics that work with numeric predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNumericSummaryStatistic
  extends AbstractSummaryStatistic
  implements NumericSummaryStatistic {

  private static final long serialVersionUID = -3184846086560157381L;

  /** the actual values. */
  protected double[] m_NumericActual;

  /** the predicted values. */
  protected double[] m_NumericPredicted;

  /**
   * Clears all input.
   */
  public void clear() {
    super.clear();
    m_NumericActual    = null;
    m_NumericPredicted = null;
  }

  /**
   * Sets the actual values.
   *
   * @param value	the actual
   */
  public void setNumericActual(double[] value) {
    m_NumericActual = value;
  }

  /**
   * Returns the actual values.
   *
   * @return		the actual
   */
  public double[] getNumericActual() {
    return m_NumericActual;
  }

  /**
   * Sets the predicted values.
   *
   * @param value	the predicted
   */
  public void setNumericPredicted(double[] value) {
    m_NumericPredicted = value;
  }

  /**
   * Returns the predicted values.
   *
   * @return		the predicted
   */
  public double[] getNumericPredicted() {
    return m_NumericPredicted;
  }

  /**
   * Hook method for performing checks before calculating statistic.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (m_NumericActual == null)
        result = "No actual values provided!";
      else if (m_NumericPredicted == null)
        result = "No predicted values provided!";
      else if (m_NumericActual.length != m_NumericPredicted.length)
        result = "Differing number of actual and predicted values: " + m_NumericActual.length + " != " + m_NumericPredicted.length;
    }

    return result;
  }
}
