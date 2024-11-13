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
 * StoppableEvaluation.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers;

import adams.core.StoppableWithFeedback;
import weka.core.Instances;

/**
 * Extended Evaluation class that can stop its evaluation processes better.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class StoppableEvaluation
  extends Evaluation
  implements StoppableWithFeedback {

  private static final long serialVersionUID = 7965365760743873046L;

  /**
   * Initializes all the counters for the evaluation. Use
   * <code>useNoPriors()</code> if the dataset is the test set and you can't
   * initialize with the priors from the training set via
   * <code>setPriors(Instances)</code>.
   *
   * @param data set of training instances, to get some header information and
   *             prior class distribution information
   * @throws Exception if the class is not defined
   * @see #useNoPriors()
   * @see #setPriors(Instances)
   */
  public StoppableEvaluation(Instances data) throws Exception {
    super(data);
    m_delegate = new weka.classifiers.evaluation.StoppableEvaluation(data);
  }

  /**
   * Initializes all the counters for the evaluation and also takes a cost
   * matrix as parameter. Use <code>useNoPriors()</code> if the dataset is the
   * test set and you can't initialize with the priors from the training set via
   * <code>setPriors(Instances)</code>.
   *
   * @param data       set of training instances, to get some header information and
   *                   prior class distribution information
   * @param costMatrix the cost matrix---if null, default costs will be used
   * @throws Exception if cost matrix is not compatible with data, the class is
   *                   not defined or the class is numeric
   * @see #useNoPriors()
   * @see #setPriors(Instances)
   */
  public StoppableEvaluation(Instances data, CostMatrix costMatrix) throws Exception {
    super(data, costMatrix);
    m_delegate = new weka.classifiers.evaluation.StoppableEvaluation(data, costMatrix);
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    ((StoppableWithFeedback) m_delegate).stopExecution();
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return ((StoppableWithFeedback) m_delegate).isStopped();
  }

}
