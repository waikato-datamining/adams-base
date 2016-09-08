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
 * AbstractSimpleCatSwarmOptimization.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso;

import org.jblas.DoubleMatrix;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Ancestor for simple CSO algorithms that just use .
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSimpleCatSwarmOptimization
  extends AbstractCatSwarmOptimization {

  private static final long serialVersionUID = 8721543186280693305L;

  /** whether to evaluate in parallel. */
  protected boolean m_EvalParallel;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "eval-parallel", "evalParallel",
      false);
  }

  /**
   * Sets whether to evaluate in parallel.
   *
   * @param value	true if in parallel
   */
  public void setEvalParallel(boolean value) {
    m_EvalParallel = value;
    reset();
  }

  /**
   * Returns  whether to evaluate in parallel.
   *
   * @return		true if in parallel
   */
  public boolean getEvalParallel() {
    return m_EvalParallel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String evalParallelTipText() {
    return "If enabled, the evaluation happens in parallel.";
  }

  /**
   * Problem-specific fitness function
   * -- expects a one-dimensional matrix
   * -- returns a non-negative value where lower is better
   * -- should be implemented for different problems
   */
  public abstract double particleFitness(DoubleMatrix particle);

  /**
   * Helper methods to evaluate all or part of the swarm,
   * either in serial or parallel, used by run()
   *
   */
  protected void evalSwarm(int[] indices) {
    IntStream indicesStream = Arrays.stream(indices);
    if (m_EvalParallel)
      indicesStream = indicesStream.parallel();
    indicesStream.forEach((index)-> updateFitness(index, particleFitness(m_Positions.getRow(index))));
  }
}
