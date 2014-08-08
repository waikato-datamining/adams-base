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
 * Performance.java
 * Copyright (C) 2008-2014 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta.multisearch;

import java.io.Serializable;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.MultiSearch;
import weka.core.SelectedTag;
import weka.core.setupgenerator.Point;

/**
 * A helper class for storing the performance of values in the parameter
 * space. Can be sorted with the PerformanceComparator class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see PerformanceComparator
 */
public class Performance
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -4374706475277588755L;

  /** evaluation via: Correlation coefficient. */
  public static final int EVALUATION_CC = 0;

  /** evaluation via: Root mean squared error. */
  public static final int EVALUATION_RMSE = 1;

  /** evaluation via: Root relative squared error. */
  public static final int EVALUATION_RRSE = 2;

  /** evaluation via: Mean absolute error. */
  public static final int EVALUATION_MAE = 3;

  /** evaluation via: Relative absolute error. */
  public static final int EVALUATION_RAE = 4;

  /** evaluation via: Combined = (1-CC) + RRSE + RAE. */
  public static final int EVALUATION_COMBINED = 5;

  /** evaluation via: Accuracy. */
  public static final int EVALUATION_ACC = 6;

  /** evaluation via: Kappa statistic. */
  public static final int EVALUATION_KAPPA = 7;

  /** the values the filter/classifier were built with. */
  protected Point<Object> m_Values;

  /** the evaluation type. */
  protected int m_Evaluation;

  /** the Correlation coefficient. */
  protected double m_CC;

  /** the Root mean squared error. */
  protected double m_RMSE;

  /** the Root relative squared error. */
  protected double m_RRSE;

  /** the Mean absolute error. */
  protected double m_MAE;

  /** the Relative absolute error. */
  protected double m_RAE;

  /** the Accuracy. */
  protected double m_ACC;

  /** the Kappa statistic. */
  protected double m_Kappa;

  /**
   * Initializes the performance container. If the Evaluation object is null,
   * then the worst possible values for the measures are assumed (in order to
   * assure a low ranking).
   *
   * @param values		the values
   * @param evaluation	the evaluation to extract the performance
   * 				measures from, can be null
   * @param evalType		the type of evaluation
   * @throws Exception	if retrieving of measures fails
   */
  public Performance(Point<Object> values, Evaluation evaluation, int evalType) throws Exception {
    super();

    m_Values     = values;
    m_Evaluation = evalType;

    if (evaluation != null) {
      m_RMSE = evaluation.rootMeanSquaredError();
      m_RRSE = evaluation.rootRelativeSquaredError();
      m_MAE  = evaluation.meanAbsoluteError();
      m_RAE  = evaluation.relativeAbsoluteError();

      try {
        m_CC = evaluation.correlationCoefficient();
      }
      catch (Exception e) {
        m_CC = Double.NaN;
      }
      try {
        m_ACC = evaluation.pctCorrect();
      }
      catch (Exception e) {
        m_ACC = Double.NaN;
      }
      try {
        m_Kappa = evaluation.kappa();
      }
      catch (Exception e) {
        m_Kappa = Double.NaN;
      }
    }
    else {
      m_RMSE  = Double.MAX_VALUE;
      m_RRSE  = Double.MAX_VALUE;
      m_MAE   = Double.MAX_VALUE;
      m_RAE   = Double.MAX_VALUE;
      m_CC    = Double.MIN_VALUE;
      m_ACC   = Double.MIN_VALUE;
      m_Kappa = Double.MIN_VALUE;
    }
  }

  /**
   * Returns the evaluation type.
   *
   * @return		the type of evaluation
   * @see		MultiSearch#TAGS_EVALUATION
   */
  public int getEvaluation() {
    return m_Evaluation;
  }

  /**
   * returns the performance measure.
   *
   * @return 			the performance measure
   */
  public double getPerformance() {
    return getPerformance(m_Evaluation);
  }

  /**
   * returns the performance measure.
   *
   * @param evaluation	the type of evaluation to return
   * @param value 	the performance measure
   */
  public void setPerformance(int evaluation, double value) {
    switch (evaluation) {
      case EVALUATION_CC:
        m_CC = value;
        break;
      case EVALUATION_RMSE:
        m_RMSE = value;
        break;
      case EVALUATION_RRSE:
        m_RRSE = value;
        break;
      case EVALUATION_MAE:
        m_MAE = value;
        break;
      case EVALUATION_RAE:
        m_RAE = value;
        break;
      case EVALUATION_COMBINED:
        break;
      case EVALUATION_ACC:
        m_ACC = value;
        break;
      case EVALUATION_KAPPA:
        m_Kappa = value;
        break;
      default:
        throw new IllegalArgumentException("Evaluation type '" + evaluation + "' not supported!");
    }
  }

  /**
   * returns the performance measure.
   *
   * @param evaluation	the type of evaluation to return
   * @return 			the performance measure
   */
  public double getPerformance(int evaluation) {
    double	result;

    result = Double.NaN;

    switch (evaluation) {
      case EVALUATION_CC:
        result = m_CC;
        break;
      case EVALUATION_RMSE:
        result = m_RMSE;
        break;
      case EVALUATION_RRSE:
        result = m_RRSE;
        break;
      case EVALUATION_MAE:
        result = m_MAE;
        break;
      case EVALUATION_RAE:
        result = m_RAE;
        break;
      case EVALUATION_COMBINED:
        result = (1 - StrictMath.abs(m_CC)) + m_RRSE + m_RAE;
        break;
      case EVALUATION_ACC:
        result = m_ACC;
        break;
      case EVALUATION_KAPPA:
        result = m_Kappa;
        break;
      default:
        throw new IllegalArgumentException("Evaluation type '" + evaluation + "' not supported!");
    }

    return result;
  }

  /**
   * returns the values for this performance.
   *
   * @return the values
   */
  public Point<Object> getValues() {
    return m_Values;
  }

  /**
   * returns a string representation of this performance object.
   *
   * @return a string representation
   */
  @Override
  public String toString() {
    String	result;

    result = "Performance (" + getValues() + "): ";
    result +=   getPerformance()
              + " (" + new SelectedTag(m_Evaluation, MultiSearch.TAGS_EVALUATION) + ")";

    if (m_Evaluation == Performance.EVALUATION_COMBINED) {
      result +=   ", " + getPerformance(Performance.EVALUATION_CC)
                + " (" + new SelectedTag(Performance.EVALUATION_CC, MultiSearch.TAGS_EVALUATION) + ")";
      result +=   ", " + getPerformance(Performance.EVALUATION_RRSE)
                + " (" + new SelectedTag(Performance.EVALUATION_RRSE, MultiSearch.TAGS_EVALUATION) + ")";
      result +=   ", " + getPerformance(Performance.EVALUATION_RAE)
                + " (" + new SelectedTag(Performance.EVALUATION_RAE, MultiSearch.TAGS_EVALUATION) + ")";
    }

    return result;
  }
}