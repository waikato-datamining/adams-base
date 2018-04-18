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
 * AbstractGeneralSummaryStatistic.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.summarystatistics;

/**
 * Ancestor for statistics that can be applied to numeric and categorical
 * predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractGeneralSummaryStatistic
  extends AbstractSummaryStatistic
  implements NumericSummaryStatistic, CategoricalSummaryStatistic {

  private static final long serialVersionUID = -3507256835214071742L;

  /** the actual values. */
  protected double[] m_NumericActual;

  /** the predicted values. */
  protected double[] m_NumericPredicted;

  /** the actual values. */
  protected String[] m_CategoricalActual;

  /** the predicted values. */
  protected String[] m_CategoricalPredicted;

  /** the probabilities (optional). */
  protected double[] m_CategoricalProbabilities;

  /** the class distributions (optional). */
  protected double[][] m_CategoricalClassDistributions;

  /** the class distribution labels (required if distributions provided). */
  protected String[] m_CategoricalClassDistributionLabels;

  /**
   * Clears all input.
   */
  public void clear() {
    super.clear();
    m_NumericActual                      = null;
    m_NumericPredicted                   = null;
    m_CategoricalActual                  = null;
    m_CategoricalPredicted               = null;
    m_CategoricalProbabilities           = null;
    m_CategoricalClassDistributions      = null;
    m_CategoricalClassDistributionLabels = null;
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
   * Sets the actual values.
   *
   * @param value	the actual
   */
  public void setCategoricalActual(String[] value) {
    m_CategoricalActual = value;
  }

  /**
   * Returns the actual values.
   *
   * @return		the actual
   */
  public String[] getCategoricalActual() {
    return m_CategoricalActual;
  }

  /**
   * Sets the predicted values.
   *
   * @param value	the predicted
   */
  public void setCategoricalPredicted(String[] value) {
    m_CategoricalPredicted = value;
  }

  /**
   * Returns the predicted values.
   *
   * @return		the predicted
   */
  public String[] getCategoricalPredicted() {
    return m_CategoricalPredicted;
  }

  /**
   * Sets the probabilities.
   *
   * @param value	the probabilities
   */
  public void setCategoricalProbabilities(double[] value) {
    m_CategoricalProbabilities = value;
  }

  /**
   * Returns the probabilities.
   *
   * @return		the probabilities
   */
  public double[] getCategoricalProbabilities() {
    return m_CategoricalProbabilities;
  }

  /**
   * Sets the class distributions.
   *
   * @param value	the class distributions
   */
  public void setCategoricalClassDistributions(double[][] value) {
    m_CategoricalClassDistributions = value;
  }

  /**
   * Returns the class distributions values.
   *
   * @return		the class distributions
   */
  public double[][] getCategoricalClassDistributions() {
    return m_CategoricalClassDistributions;
  }

  /**
   * Sets the class labels (order in the class distributions).
   *
   * @param value	the class distribution labels
   */
  public void setCategoricalClassDistributionLabels(String[] value) {
    m_CategoricalClassDistributionLabels = value;
  }

  /**
   * Returns the class distributions values.
   *
   * @return		the class distribution labels
   */
  public String[] getCategoricalClassDistributionLabels() {
    return m_CategoricalClassDistributionLabels;
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
      if ((m_NumericActual != null) || (m_NumericPredicted != null)) {
	if (m_NumericActual == null)
	  result = "No actual values provided!";
	else if (m_NumericPredicted == null)
	  result = "No predicted values provided!";
	else if (m_NumericActual.length != m_NumericPredicted.length)
	  result = "Differing number of actual and predicted values: " + m_NumericActual.length + " != " + m_NumericPredicted.length;
      }
    }

    if (result == null) {
      if ((m_CategoricalActual != null) || (m_CategoricalPredicted != null)) {
	if (m_CategoricalActual == null)
	  result = "No actual values provided!";
	else if (m_CategoricalPredicted == null)
	  result = "No predicted values provided!";
	else if (m_CategoricalActual.length != m_CategoricalPredicted.length)
	  result = "Differing number of actual and predicted values: " + m_CategoricalActual.length + " != " + m_CategoricalPredicted.length;
	if ((result == null) && (m_CategoricalProbabilities != null)) {
	  if (m_CategoricalPredicted.length != m_CategoricalProbabilities.length)
	    result = "Differing number of predicted values and probabilities: " + m_CategoricalPredicted.length + " != " + m_CategoricalProbabilities.length;
	}
	if ((result == null) && (m_CategoricalClassDistributions != null)) {
	  if (m_CategoricalPredicted.length != m_CategoricalClassDistributions.length)
	    result = "Differing number of predicted values and class distributions: " + m_CategoricalPredicted.length + " != " + m_CategoricalClassDistributions.length;
	  else if (m_CategoricalClassDistributionLabels == null)
	    result = "No class distribution labels provided!";
	  else if (m_CategoricalClassDistributionLabels.length != m_CategoricalClassDistributions[0].length)
	    result = "Differing number of predicted values and class distributions: " + m_CategoricalPredicted.length + " != " + m_CategoricalClassDistributions.length;
	}
      }
    }

    return result;
  }

  /**
   * Checks whether numeric predictions are available.
   *
   * @return		true if numeric predictions available
   */
  public boolean isNumeric() {
    return (m_NumericActual != null) && (m_NumericPredicted != null);
  }
}
