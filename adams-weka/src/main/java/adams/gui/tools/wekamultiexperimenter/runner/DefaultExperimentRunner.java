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
 * DefaultExperimentRunner.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.runner;

import adams.core.DateUtils;
import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;

import java.io.File;
import java.util.Date;

/**
 * A class that handles running a copy of the experiment
 * in a separate thread.
 * 
 * @see weka.gui.experiment.RunPanel.ExperimentRunner
 */
public class DefaultExperimentRunner
  extends AbstractExperimentRunner {
  
  /** for serialization. */
  private static final long serialVersionUID = -5499408120296699079L;

  /**
   * Initializes the thread.
   * 
   * @param owner		the experimenter this runner belongs to
   * @throws Exception	if experiment is null or cannot be copied via serialization
   */
  public DefaultExperimentRunner(ExperimenterPanel owner) throws Exception {
    super(owner);
  }

  /**
   * Performs the actual running of the experiment.
   * 
   * @throws Exception	fails due to some error
   */
  @Override
  protected void doRun() throws Exception {
    int errors = 0;
    showStatus("Iterating...");
    while (m_Running && m_Exp.hasMoreIterations()) {
      try {
	String current = "Iteration:";
	if (m_Exp.getUsePropertyIterator()) {
	  int cnum = m_Exp.getCurrentPropertyNumber();
	  String ctype = m_Exp.getPropertyArray().getClass().getComponentType().getName();
	  int lastDot = ctype.lastIndexOf('.');
	  if (lastDot != -1)
	    ctype = ctype.substring(lastDot + 1);
	  String cname = " " + ctype + "=" + (cnum + 1) + ":" + m_Exp.getPropertyArrayValue(cnum).getClass().getName();
	  current += cname;
	}
	String dname = ((File) m_Exp.getDatasets().elementAt(m_Exp.getCurrentDatasetNumber())).getName();
	current += " Dataset=" + dname + " Run=" + (m_Exp.getCurrentRunNumber());
	showStatus(current);
	m_Exp.nextIteration();
      } 
      catch (Exception ex) {
	errors++;
	logMessage(ex);
	ex.printStackTrace();
	boolean continueAfterError = false;
	if (continueAfterError)
	  m_Exp.advanceCounters(); // Try to keep plowing through
	else
	  m_Running = false;
      }
    }
    showStatus("Postprocessing...");
    m_Exp.postProcess();
    if (!m_Running)
      logMessage("Interrupted");
    else
      logMessage("Finished");
    if (errors == 1)
      logMessage("There was " + errors + " error");
    else
      logMessage("There were " + errors + " errors");
    showStatus(NOT_RUNNING);
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
    m_Owner.finishExecution();
    update();
    m_Running = false;
    logMessage("Done!");
    logMessage("--> END: " + DateUtils.getTimestampFormatter().format(new Date()));
  }
}