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
 * MaxIterationsWithoutImprovement.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso.stopping;

import adams.opt.cso.AbstractCatSwarmOptimization;

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
 * <pre>-num-iter &lt;int&gt; (property: numIterations)
 * &nbsp;&nbsp;&nbsp;The maximum number of iterations to perform without improvement.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MaxIterationsWithoutImprovement
  extends AbstractStoppingCriterion {

  private static final long serialVersionUID = -725187280232195524L;

  /** number of iterations to perform. */
  protected int m_NumIterations;

  /** the minimum required improvement (percent: 0-1). */
  protected double m_MinimumImprovement;

  /** the number of iterations without improvement. */
  protected int m_NoImprovement;

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
      "num-iter", "numIterations",
      10, 1, null);

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
   * Sets the number of iterations to perform.
   *
   * @param value	the number
   */
  public void setNumIterations(int value) {
    m_NumIterations = value;
    reset();
  }

  /**
   * Returns the number of iterations to perform.
   *
   * @return		the number
   */
  public int getNumIterations() {
    return m_NumIterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numIterationsTipText() {
    return "The maximum number of iterations to perform without improvement.";
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
  }

  /**
   * Records fitness, resets counter.
   *
   * @param genetic	the algorithm
   */
  protected void record(AbstractCatSwarmOptimization genetic) {
    m_LastFitness   = genetic.getCurrentFitness();
    m_NoImprovement = 0;
  }

  /**
   * Performs the actual check of the stopping criterion.
   *
   * @param swarm	the algorithm
   * @return		true if to stop
   */
  @Override
  protected boolean doCheckStopping(AbstractCatSwarmOptimization swarm) {
    double	improvement;

    if (m_LastFitness == null) {
      record(swarm);
      return false;
    }

    if (m_LastFitness != 0) {
      if (isLoggingEnabled())
	getLogger().info("(" + m_LastFitness + " - " + swarm.getCurrentFitness() + ") / " + m_LastFitness);
      improvement = Math.abs(m_LastFitness - swarm.getCurrentFitness()) / m_LastFitness;
      if (isLoggingEnabled())
	getLogger().info("--> Improvement: " + improvement);
    }
    else {
      getLogger().warning("Last fitness is 0. To avoid divByZero using 0 as improvement!");
      improvement = 0.0;
    }

    if ((improvement >= m_MinimumImprovement) || (Double.isInfinite(m_LastFitness))) {
      record(swarm);
      return false;
    }

    m_NoImprovement++;
    if (isLoggingEnabled())
      getLogger().info("No improvement of at least " + m_MinimumImprovement + ": " + m_NoImprovement);

    return (m_NoImprovement > m_NumIterations);
  }
}
