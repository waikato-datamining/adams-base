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
 * RemoteExperimentRunner.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.experiment.ext;

import weka.experiment.RemoteExperiment;
import weka.experiment.RemoteExperimentEvent;
import weka.experiment.RemoteExperimentListener;

/**
 * A class that handles running a copy of the experiment
 * in a separate thread.
 * 
 * @see weka.gui.experiment.RunPanel.ExperimentRunner
 */
public class RemoteExperimentRunner
  extends AbstractExperimentRunner {

  /** for serialization */
  private static final long serialVersionUID = -5591889874714150118L;
  
  /**
   * Initializes the thread.
   * 
   * @param owner		the experimenter this runner belongs to
   * @throws Exception	if experiment is null or cannot be copied via serialization
   */
  public RemoteExperimentRunner(ExperimenterPanel owner) throws Exception {
    super(owner);
  }

  /**
   * Aborts the experiment.
   */
  @Override
  public void abortExperiment() {
    super.abortExperiment();
    ((RemoteExperiment) m_Exp).abortExperiment();
    update();
  }

  /**
   * Hook method that gets executed before the experiment gets initialized.
   * 
   * @throws Exception	fails due to some error
   */
  @Override
  protected void preRun() throws Exception {
    super.preRun();
    // add a listener
    logMessage("Adding a listener");
    ((RemoteExperiment)m_Exp).addRemoteExperimentListener(new RemoteExperimentListener() {
      public void remoteExperimentStatus(RemoteExperimentEvent e) {
	if (e.m_statusMessage)
	  showStatus(e.m_messageString);
	if (e.m_logMessage)
	  logMessage(e.m_messageString);
	if (e.m_experimentFinished) {
	  m_Running = false;
	  showStatus(NOT_RUNNING);
	  update();
	}
      }
    });
  }
  
  /**
   * Performs the actual running of the experiment.
   * 
   * @throws Exception	fails due to some error
   */
  @Override
  protected void doRun() throws Exception {
    showStatus("Remote experiment running...");
    ((RemoteExperiment) m_Exp).runExperiment();
  }
}