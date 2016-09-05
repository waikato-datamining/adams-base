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
 * AbstractAdamsExperimentRunner.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.runner;

import adams.core.DateUtils;
import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;
import weka.core.Instances;

import java.util.Date;

/**
 * Ancestor for classes that handle running a copy of the experiment
 * in a separate thread.
 */
public abstract class AbstractAdamsExperimentRunner<T extends AbstractExperiment>
  extends AbstractExperimentRunner<T> {

  /** for serialization */
  private static final long serialVersionUID = -5591889874714150118L;

  /**
   * Initializes the thread.
   *
   * @param owner		the experimenter this runner belongs to
   * @throws Exception	if experiment is null or cannot be copied via serialization
   */
  public AbstractAdamsExperimentRunner(ExperimenterPanel owner) throws Exception {
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
   * Examines the supplied experiment to determine the results destination and
   * attempts to load the results.
   */
  protected void loadResults() {
    Instances	data;

    logMessage("Attempting to load results...");

    data = m_Exp.toInstances();

    if (data != null) {
      m_Owner.getAnalysisPanel().setResults(data);
      logMessage("Successfully loaded results!");
    }
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
  protected void postRun(boolean success) {
    if (success)
      loadResults();
    m_Owner.finishExecution();
    update();
    m_Running = false;
    logMessage("Done!");
    logMessage("--> END: " + DateUtils.getTimestampFormatter().format(new Date()));
    m_Exp.setStatusMessageHandler(null);
  }

  /**
   * Aborts the experiment.
   */
  public void abortExperiment() {
    super.abortExperiment();
    m_Exp.stopExecution();
  }
}