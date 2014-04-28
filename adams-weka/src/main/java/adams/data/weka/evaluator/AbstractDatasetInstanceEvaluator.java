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
 * AbstractTrainingSetInstanceEvaluator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.evaluator;

import weka.core.Instances;

/**
 * Ancestor for evaluators that need a data set for initialization.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDatasetInstanceEvaluator
  extends AbstractInstanceEvaluator {

  /** for serialization. */
  private static final long serialVersionUID = 7443189522006925538L;

  /** the data set to use for training and other bits. */
  protected Instances m_Data;

  /** the percentage to the threshold. */
  protected double m_Threshold;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "threshold", "threshold",
	    0.75);
  }

  /**
   * Resets the scheme.
   */
  protected void reset() {
    super.reset();

    m_Initialized = false;
  }

  /**
   * Sets the data set to use for training and so forth.
   *
   * @param value 	the data set
   */
  public void setData(Instances value) {
    m_Data = value;
    reset();
  }

  /**
   * Returns the data to use for training and so forth.
   *
   * @return 		the data set, can be null if not yet set
   */
  public Instances getData() {
    return m_Data;
  }

  /**
   * Sets the threshold.
   *
   * @param value 	the threshold (0-1)
   */
  public void setThreshold(double value) {
    m_Threshold = value;
    reset();
  }

  /**
   * Returns the threshold.
   *
   * @return 		the threshold (0-1)
   */
  public double getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "The threshold percentage to use (0-1).";
  }

  /**
   * Splits the dataset into two separate ones, according to the specified
   * percentage (0-1).
   *
   * @param data	the data to split
   * @param percentage	the percentage of the split (0-1)
   * @return		the array with the two datasets generated
   */
  protected Instances[] split(Instances data, double percentage) {
    Instances[]	result;
    int		max;

    result    = new Instances[2];
    max       = (int) Math.round((double) data.numInstances() * percentage);
    result[0] = new Instances(data, 0, max);
    result[1] = new Instances(data, max, data.numInstances() - max);

    return result;
  }

  /**
   * Finds the user-defined threshold and sets other internal variables
   * accordingly.
   *
   * @return		null if everything OK, error message otherwise
   */
  protected abstract String findThreshold();

  /**
   * Performs necessary initializations before being able to evaluate.
   *
   * @return		null if everything fine, error message otherwise
   */
  protected String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_Data == null)
	result = "No data for training available!";
      else if (m_Data.classIndex() == -1)
	result = "No class attribute set!";
      else
	result = findThreshold();
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   *
   * @see 		#m_Data
   */
  public void cleanUp() {
    super.cleanUp();

    m_Data = null;
  }
}
