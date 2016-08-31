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
 * AdamsExperimentRunner.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.runner;

import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;

/**
 * Ancestor for classes that handle running a copy of the experiment
 * in a separate thread.
 */
public class AdamsExperimentRunner<T extends AbstractExperiment>
  extends AbstractAdamsExperimentRunner<T> {

  /** for serialization */
  private static final long serialVersionUID = -5591889874714150118L;

  /**
   * Initializes the thread.
   *
   * @param owner		the experimenter this runner belongs to
   * @throws Exception	if experiment is null or cannot be copied via serialization
   */
  public AdamsExperimentRunner(ExperimenterPanel owner) throws Exception {
    super(owner);
  }

  /**
   * Initializes the experiment.
   *
   * @throws Exception	fails due to some error
   */
  protected void doInitialize() throws Exception {
    m_Exp.setStatusMessageHandler(m_Owner);
  }

  /**
   * Performs the actual running of the experiment.
   *
   * @throws Exception	fails due to some error
   */
  @Override
  protected void doRun() throws Exception {
    String	result;

    result = m_Exp.execute();
    if (result != null)
      throw new Exception(result);
  }

  /**
   * Hook method that gets executed after the experiment has finished
   * (successfully or not).
   *
   * @param success	whether successfully finished (neither error, nor aborted)
   */
  @Override
  protected void postRun(boolean success) {
    super.postRun(success);
    m_Exp.setStatusMessageHandler(null);
  }
}