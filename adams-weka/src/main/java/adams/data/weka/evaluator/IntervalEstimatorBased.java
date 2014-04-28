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
 * IntervalEstimatorBased.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.evaluator;

import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;

import weka.classifiers.Classifier;
import weka.classifiers.IntervalEstimator;
import weka.classifiers.functions.GaussianProcesses;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Uses a classifier that produces confidence intervals. ???
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-threshold &lt;double&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold percentage to use (0-1).
 * &nbsp;&nbsp;&nbsp;default: 0.75
 * </pre>
 *
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use for cross-validation; cross-validation gets turned
 * &nbsp;&nbsp;&nbsp;off below a value of 2.
 * &nbsp;&nbsp;&nbsp;default: 2
 * </pre>
 *
 * <pre>-seed &lt;int&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for randomizing the data for cross-validation.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-classifier &lt;weka.classifiers.Classifier [options]&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The classifier to use (must implement weka.classifiers.IntervalEstimator
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.functions.GaussianProcesses -L 1.0 -N 0 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"
 * </pre>
 *
 * <pre>-level &lt;double&gt; (property: confidenceLevel)
 * &nbsp;&nbsp;&nbsp;The confidence level to use when generating the confidence intervals (0-
 * &nbsp;&nbsp;&nbsp;1).
 * &nbsp;&nbsp;&nbsp;default: 0.95
 * </pre>
 *
 * <pre>-relative (property: relativeWidths)
 * &nbsp;&nbsp;&nbsp;If set to true, then the calculated widths will be relative ones, as they
 * &nbsp;&nbsp;&nbsp;will get divided by the class value of the Instance.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IntervalEstimatorBased
  extends AbstractCrossvalidatedInstanceEvaluator<IntervalEstimatorBased.SortedInterval> {

  /** for serialization. */
  private static final long serialVersionUID = -7760097633698319552L;

  /**
   * Helper class for sorting the confidence intervals.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SortedInterval
    extends AbstractCrossvalidatedInstanceEvaluator.EvaluationContainer {

    /** the intervals. */
    protected double[][] m_Intervals;

    /** the average width. */
    protected double m_AverageWidth;

    /** whether the widths are relative. */
    protected boolean m_RelativeWidths;

    /**
     * Initializes the intervals.
     *
     * @param inst	the Instance this container is for
     * @param intervals	the intervals
     * @param relative	whether to use relative widths
     */
    public SortedInterval(Instance inst, double[][] intervals, boolean relative) {
      super(inst);

      m_Intervals      = intervals.clone();
      m_AverageWidth   = calcAverageWidth(m_Intervals);
      m_RelativeWidths = relative;
      if (m_RelativeWidths) {
	if (m_Instance.classValue() == 0)
	  m_AverageWidth = Double.MAX_VALUE;
	else
	  m_AverageWidth /= m_Instance.classValue();
      }
    }

    /**
     * Returns the stored intervals.
     *
     * @return		the intervals
     */
    public double[][] getIntervals() {
      return m_Intervals;
    }

    /**
     * Returns the average width of the stored intervals.
     *
     * @return		the average width
     */
    public double getAverageWidth() {
      return m_AverageWidth;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p/>
     * Only compares the commandlines of the two objects.
     *
     * @param o 	the object to be compared.
     * @return  	a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException 	if the specified object's type prevents it
     *         				from being compared to this object.
     */
    @Override
    public int compareTo(Object o) {
      int		result;
      SortedInterval	other;
      int		i;
      double		width;
      double		widthOther;

      if (o == null)
        return 1;

      other = (SortedInterval) o;

      result = new Integer(getIntervals().length).compareTo(new Integer(other.getIntervals().length));

      if (result == 0) {
	for (i = 0; i < m_Intervals.length; i++) {
	  width      = calcWidth(getIntervals()[i]);
	  widthOther = calcWidth(other.getIntervals()[i]);
	  result     = new Double(width).compareTo(new Double(widthOther));
	  if (result != 0)
	    break;
	}
      }

      return result;
    }

    /**
     * Returns the intervals as string.
     *
     * @return		the intervals
     */
    @Override
    public String toString() {
      return "intervals=" + Utils.arrayToString(m_Intervals) + ", avg width=" + m_AverageWidth;
    }
  }

  /** the IntervalEstimator to use. */
  protected Classifier m_Classifier;

  /** the confidence level. */
  protected double m_ConfidenceLevel;

  /** whether to divide the calculated widths by the class value. */
  protected boolean m_RelativeWidths;

  /** the maximum width allowed. */
  protected double m_MaxWidth;

  /** the minimum width encountered. */
  protected double m_MinWidth;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a classifier that produces confidence intervals. ???";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    new GaussianProcesses());

    m_OptionManager.add(
	    "level", "confidenceLevel",
	    0.95);

    m_OptionManager.add(
	    "relative", "relativeWidths",
	    false);
  }

  /**
   * Sets the classifier to use, must implement weka.classifiers.IntervalEstimator.
   *
   * @param value 	the classifier
   */
  public void setClassifier(Classifier value) {
    if (value instanceof IntervalEstimator) {
      m_Classifier = value;
      reset();
    }
    else {
      getLogger().severe("Classifier must implement " + IntervalEstimator.class.getName());
    }
  }

  /**
   * Returns the classifier.
   *
   * @return 		the classifier
   */
  public Classifier getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The classifier to use (must implement " + IntervalEstimator.class.getName() + ").";
  }

  /**
   * Sets the confidence level.
   *
   * @param value 	the confidence level (0-1)
   */
  public void setConfidenceLevel(double value) {
    m_ConfidenceLevel = value;
    reset();
  }

  /**
   * Returns the confidence level.
   *
   * @return 		the confidence level (0-1)
   */
  public double getConfidenceLevel() {
    return m_ConfidenceLevel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String confidenceLevelTipText() {
    return "The confidence level to use when generating the confidence intervals (0-1).";
  }

  /**
   * Sets whether to divide the calculated widths by the class value.
   *
   * @param value 	if true then the widths will get divided by the class
   * 			value (= relative)
   */
  public void setRelativeWidths(boolean value) {
    m_RelativeWidths = value;
    reset();
  }

  /**
   * Returns whether the calculated widths are divided by the class value.
   *
   * @return 		trye if the widths are divided by the class value
   */
  public boolean getRelativeWidths() {
    return m_RelativeWidths;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String relativeWidthsTipText() {
    return
        "If set to true, then the calculated widths will be relative ones, "
      + "as they will get divided by the class value of the Instance.";
  }

  /**
   * Finds the threshold based on the collected data.
   *
   * @param evals	the collected evaluation containers
   * @return		null if everything OK, error message otherwise
   */
  @Override
  protected String findThreshold(Vector<SortedInterval> evals) {
    int		location;

    if (evals.size() == 0)
      return "No intervals collected!";

    Collections.sort(evals);

    // get minimum width
    m_MinWidth = evals.firstElement().getAverageWidth();

    // get maximum allowed width
    location   = (int) Math.round((double) evals.size() * m_Threshold);
    m_MaxWidth = evals.get(location).getAverageWidth();
    if (isLoggingEnabled())
      getLogger().info("Computed thresholds: min=" + m_MinWidth + ", max=" + m_MaxWidth);

    return null;
  }

  /**
   * Performs an evaluation on the given train and test set.
   *
   * @param train	the training set
   * @param test	the test set
   * @return		the generated evaluation container
   */
  @Override
  protected Vector<SortedInterval> evaluate(Instances train, Instances test) {
    Vector<SortedInterval>	result;
    int				i;

    result = new Vector<SortedInterval>();

    try {
      // build classifier
      if (isLoggingEnabled())
	getLogger().info("Building classifier...");
      m_Classifier.buildClassifier(train);

      // obtain intervals
      if (isLoggingEnabled())
	getLogger().info("Obtaining intervals...");
      for (i = 0; i < test.numInstances(); i++) {
	try {
	  result.add(
	      new SortedInterval(
		  test.instance(i),
		  ((IntervalEstimator) m_Classifier).predictIntervals(
		      test.instance(i), m_ConfidenceLevel),
		  m_RelativeWidths));
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Error obtaining intervals for test instance #" + (i+1) + ": " + test.instance(i), e);
	}
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to evaluate", e);
    }

    return result;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param inst	the instance to evaluate
   * @return		evaluation range, between 0 and 1 (0 = bad, 1 = good, -1 = if unable to evaluate)
   */
  @Override
  protected double doEvaluate(Instance inst) {
    double	result;
    double	width;
    double[][]	intervals;

    try {
      intervals = ((IntervalEstimator) m_Classifier).predictIntervals(inst, m_ConfidenceLevel);
      width     = calcAverageWidth(intervals);

      if (m_RelativeWidths) {
	if (inst.classValue() == 0)
	  width = Double.MAX_VALUE;
	else
	  width /= inst.classValue();
      }

      if (width < m_MinWidth) {
	result = 1.0;
      }
      else if (width > m_MaxWidth) {
	// width  = max   => 0.5
	// width >= 2*max => 1.0
	width -= m_MaxWidth;
	if (width > m_MaxWidth)
	  result = 0.0;
	else
	  result = 0.5 - width / m_MaxWidth / 2;
      }
      else {
	// width = min => 1.0
	// width = max => 0.5
	result = 1 - (width - m_MinWidth) / (m_MaxWidth - m_MinWidth) / 2;
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to evaluate", e);
      result = -1;
    }

    return result;
  }

  /**
   * Calculates the width of the interval.
   *
   * @param array	the lower and upper bound
   * @return		the width
   */
  protected static double calcWidth(double[] array) {
    return array[1] - array[0];
  }

  /**
   * Calculates the average width of the intervals.
   *
   * @param array	the arrayw with the lower and upper bounds
   * @return		the average width
   */
  protected static double calcAverageWidth(double[][] array) {
    double	result;
    int		i;

    result = 0;
    for (i = 0; i < array.length; i++)
      result += calcWidth(array[i]);
    if (array.length > 0)
      result /= array.length;

    return result;
  }
}
