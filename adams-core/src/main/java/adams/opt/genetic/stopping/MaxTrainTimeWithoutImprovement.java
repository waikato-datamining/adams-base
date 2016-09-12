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
 * MaxTrainTimeWithoutImprovement.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.opt.genetic.stopping;

import adams.opt.genetic.AbstractGeneticAlgorithm;

/**
 <!-- globalinfo-start -->
 * Stops after the maximum number of seconds since last improvement has been reached.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-max-train &lt;int&gt; (property: maxTrainTime)
 * &nbsp;&nbsp;&nbsp;The maximum number of seconds of training time to wait for improvement (
 * &nbsp;&nbsp;&nbsp;0 = unlimited time).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-min-improvement &lt;double&gt; (property: minimumImprovement)
 * &nbsp;&nbsp;&nbsp;The minimum improvement in percent (0-1) to achieve in the alotted time.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MaxTrainTimeWithoutImprovement
  extends AbstractStoppingCriterion {

  private static final long serialVersionUID = -725187280232195524L;

  /** the maximum number of seconds to train. */
  protected int m_MaxTrainTime;

  /** the minimum required improvement (percent: 0-1). */
  protected double m_MinimumImprovement;

  /** the time of last improvement. */
  protected long m_TrainStart;

  /** the last fitness. */
  protected Double m_LastFitness;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stops after the maximum number of seconds since last improvement has been reached.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-train", "maxTrainTime",
      0, 0, null);

    m_OptionManager.add(
      "min-improvement", "minimumImprovement",
      0.0, 0.0, 1.0);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_LastFitness = null;
  }

  /**
   * Sets the maximum number of seconds to wait for improvement.
   *
   * @param value	the number of seconds
   */
  public void setMaxTrainTime(int value) {
    if (getOptionManager().isValid("maxTrainTime", value)) {
      m_MaxTrainTime = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of seconds to wait for improvement.
   *
   * @return		the number of seconds
   */
  public int getMaxTrainTime() {
    return m_MaxTrainTime;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTrainTimeTipText() {
    return "The maximum number of seconds of training time to wait for improvement (0 = unlimited time).";
  }

  /**
   * Sets the minimum improvement in percent (0-1) to achieve in the alotted time.
   *
   * @param value	the minimum
   */
  public void setMinimumImprovement(double value) {
    if (getOptionManager().isValid("minimumImprovement", value)) {
      m_MinimumImprovement = value;
      reset();
    }
  }

  /**
   * Returns the minimum improvement in percent (0-1) to achieve in the alotted time.
   *
   * @return		the minimum
   */
  public double getMinimumImprovement() {
    return m_MinimumImprovement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumImprovementTipText() {
    return "The minimum improvement in percent (0-1) to achieve in the alotted time.";
  }

  /**
   * Gets called when the genetic algorithm starts.
   */
  @Override
  public void start() {
    m_LastFitness = null;
    m_TrainStart  = System.currentTimeMillis();
  }

  /**
   * Records time and fitness.
   *
   * @param genetic	the algorithm
   */
  protected void record(AbstractGeneticAlgorithm genetic) {
    m_LastFitness = genetic.getCurrentFitness();
    m_TrainStart  = System.currentTimeMillis();
  }

  /**
   * Performs the actual check of the stopping criterion.
   *
   * @param genetic	the algorithm
   * @return		true if to stop
   */
  @Override
  protected boolean doCheckStopping(AbstractGeneticAlgorithm genetic) {
    double	improvement;

    if (m_MaxTrainTime == 0)
      return false;

    if (m_LastFitness == null) {
      record(genetic);
      return false;
    }

    if (m_LastFitness != 0) {
      improvement = Math.abs(m_LastFitness - genetic.getCurrentFitness()) / m_LastFitness;
    }
    else {
      getLogger().warning("Last fitness is 0. To avoid divByZero using 0 as improvement!");
      improvement = 0.0;
    }

    if (isLoggingEnabled())
      getLogger().info("Improvement: " + improvement);

    if (improvement >= m_MinimumImprovement) {
      record(genetic);
      return false;
    }

    return ((double) (System.currentTimeMillis() - m_TrainStart) / 1000.0 >= m_MaxTrainTime);
  }
}
