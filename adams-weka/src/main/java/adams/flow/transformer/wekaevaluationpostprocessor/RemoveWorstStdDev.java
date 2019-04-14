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
 * RemoveWorstStdDev.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaevaluationpostprocessor;

import adams.core.QuickInfoHelper;
import adams.data.statistics.StatUtils;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes the worst predictions, which are considered outliers that
 * detract from the actual model performance. Uses a standard deviation
 * based approach (threshold: mean + multiplier*stdev).
 *
 * Only works on numeric predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemoveWorstStdDev
  extends AbstractNumericClassPostProcessor {

  private static final long serialVersionUID = -8126062783012759418L;

  /** the multiplier for the standard deviation. */
  protected double m_Multiplier;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes the worst predictions, which are considered outliers that "
      + "detract from the actual model performance. All errors that are larger than "
      + "'mean + multiplier*stdev' are considered outliers. Mean and stdev are "
      + "calculated on the actual class values.\n"
      + "Only works on numeric predictions.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "multiplier", "multiplier",
      3.0, 0.0, null);
  }

  /**
   * Sets the multiplier for the stdev.
   *
   * @param value	the multiplier
   */
  public void setMultiplier(double value) {
    if (getOptionManager().isValid("multiplier", value)) {
      m_Multiplier = value;
      reset();
    }
  }

  /**
   * Returns the multiplier for the stdev..
   *
   * @return		the multiplier
   */
  public double getMultiplier() {
    return m_Multiplier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String multiplierTipText() {
    return "The multiplier for the standard deviation (mean + multiplier*stdev = threshold for outliers).";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "multiplier", m_Multiplier, "multiplier: ");
  }

  /**
   * Post-processes the evaluation.
   *
   * @param eval	the Evaluation to post-process
   * @return		the generated evaluations
   */
  @Override
  protected List<Evaluation> doPostProcess(Evaluation eval) {
    List<Evaluation>	result;
    double		threshold;
    TIntList 		indices;
    int			i;
    TDoubleList 	errors;
    double		mean;
    double		stdev;

    result  = new ArrayList<>();

    // calculate mean/stdev
    errors = new TDoubleArrayList();
    for (Prediction pred : eval.predictions())
      errors.add(Math.abs(pred.actual() - pred.predicted()));
    mean      = StatUtils.mean(errors.toArray());
    stdev     = StatUtils.stddev(errors.toArray(), true);
    threshold = mean + stdev * m_Multiplier;
    if (isLoggingEnabled()) {
      getLogger().info("mean: " + mean);
      getLogger().info("stdev: " + stdev);
      getLogger().info("threshold: " + threshold);
    }

    // determine predictions to keep
    indices = new TIntArrayList();
    for (i = 0; i < errors.size(); i++) {
      if (errors.get(i) < threshold)
        indices.add(i);
    }

    result.add(newEvaluation("-removed_worststdev_" + m_Multiplier, eval, indices));

    return result;
  }
}
