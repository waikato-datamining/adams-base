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
 * SimpleLinearRegressionIntervalEstimator.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.functions;

import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import weka.classifiers.IntervalEstimator;
import weka.core.Instance;
import weka.core.RevisionUtils;
import weka.core.WekaException;

/**
 <!-- globalinfo-start -->
 * Learns a simple linear regression model. Picks the attribute that results in the lowest squared error. Can only deal with numeric attributes.<br/>
 * Makes standard errors available.<br/>
 * <br/>
 * Provides confidence intervals as well. For more information see:<br/>
 * http://stattrek.com/regression/slope-confidence-interval.aspx
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -additional-stats
 *  Output additional statistics.</pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleLinearRegressionIntervalEstimator
  extends SimpleLinearRegressionWithAccess
  implements IntervalEstimator {

  private static final long serialVersionUID = 2148259814445498954L;

  /**
   * Returns a string describing this classifier
   *
   * @return a description of the classifier suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return super.globalInfo() + "\n\n"
      + "Provides confidence intervals as well. For more information see:\n"
      + "http://stattrek.com/regression/slope-confidence-interval.aspx";
  }

  /**
   * Returns an N * 2 array, where N is the number of prediction
   * intervals. In each row, the first element contains the lower
   * boundary of the corresponding prediction interval and the second
   * element the upper boundary.
   *
   * @param inst the instance to make the prediction for.
   * @param confidenceLevel the percentage of cases that the interval should cover.
   * @return an array of prediction intervals
   * @throws Exception if the intervals can't be computed
   */
  @Override
  public double[][] predictIntervals(Instance inst, double confidenceLevel) throws Exception {
    double 		alpha;
    double		critProb;
    TDistribution 	td;
    double		critValue;
    double		marginError;

    if (m_df < 1)
      throw new WekaException("No degrees of freedom!");

    alpha       = 1 - confidenceLevel;
    critProb    = 1 - alpha/2;
    td          = new TDistributionImpl(m_df);
    critValue   = td.inverseCumulativeProbability(critProb);
    marginError = critValue * getSlopeSE();

    return new double[][]{{getSlope() - marginError, getSlope() + marginError}};
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 11128 $");
  }

  /**
   * Generates a linear regression function predictor.
   *
   * @param argv the options
   */
  public static void main(String argv[]) {
    runClassifier(new SimpleLinearRegressionIntervalEstimator(), argv);
  }
}
