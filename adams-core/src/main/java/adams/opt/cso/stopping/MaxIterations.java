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
 * MaxIterations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso.stopping;

import adams.opt.cso.AbstractCatSwarmOptimization;

/**
 <!-- globalinfo-start -->
 * Stops after the maximum number of iterations have been reached.
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
 * &nbsp;&nbsp;&nbsp;The number of iterations to perform.
 * &nbsp;&nbsp;&nbsp;default: 10000000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MaxIterations
  extends AbstractStoppingCriterion {

  private static final long serialVersionUID = -725187280232195524L;

  /** number of iterations to perform. */
  protected int m_NumIterations;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stops after the maximum number of iterations have been reached.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-iter", "numIterations",
      10000000, 1, null);
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
    return "The number of iterations to perform.";
  }

  /**
   * Gets called when the genetic algorithm starts.
   */
  @Override
  public void start() {
    // nothing
  }

  /**
   * Performs the actual check of the stopping criterion.
   *
   * @param swarm	the algorithm
   * @return		true if to stop
   */
  @Override
  protected boolean doCheckStopping(AbstractCatSwarmOptimization swarm) {
    return swarm.getCurrentIteration() >= getNumIterations();
  }
}
