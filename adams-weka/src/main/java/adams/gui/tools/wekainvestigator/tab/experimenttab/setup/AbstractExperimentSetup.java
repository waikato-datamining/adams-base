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
 * AbstractExperimentSetup.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.experimenttab.setup;

import adams.core.ClassLister;
import adams.core.StoppableWithFeedback;
import adams.gui.tools.wekainvestigator.evaluation.AbstractEvaluation;
import adams.gui.tools.wekainvestigator.tab.ExperimentTab;
import adams.gui.tools.wekainvestigator.tab.experimenttab.ResultItem;
import org.apache.commons.lang3.time.StopWatch;
import weka.classifiers.Classifier;

/**
 * Ancestor for experiment setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractExperimentSetup
  extends AbstractEvaluation<ExperimentTab, ResultItem>
  implements StoppableWithFeedback {

  private static final long serialVersionUID = -5847790432092994409L;

  /** whether the experiment got stopped. */
  protected boolean m_Stopped;

  /**
   * Tests whether the experiment setup can be executed for the classifier.
   *
   * @param classifier 	the classifier to check
   * @return		null if successful, otherwise error message
   */
  public abstract String canExecute(Classifier classifier);

  /**
   * Initializes the result item.
   *
   * @param classifier	the current classifier
   * @return		the initialized history item
   * @throws Exception	if initialization fails
   */
  public abstract ResultItem init(Classifier classifier) throws Exception;

  /**
   * Executes the experiment setup for the classifier and updates the result item.
   *
   * @param classifier	the current classifier
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  protected abstract void doExecute(Classifier classifier, ResultItem item) throws Exception;

  /**
   * Hook method for after executing the experiment, e.g., cleaning up temp files.
   *
   * @param classifier	the current classifier
   * @param item	the item to update
   */
  protected abstract void postExecute(Classifier classifier, ResultItem item);

  /**
   * Executes the experiment setup for the classifier and updates the result item.
   *
   * @param classifier	the current classifier
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  public void execute(Classifier classifier, ResultItem item) throws Exception {
    StopWatch 	watch;

    m_Stopped = false;

    watch = new StopWatch();
    watch.start();
    doExecute(classifier, item);
    watch.stop();
    if (item.hasRunInformation())
      item.getRunInformation().add("Total time", (watch.getTime() / 1000.0) + "s");
    postExecute(classifier, item);
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns the available actions.
   *
   * @return		the action classnames
   */
  public static Class[] getSetups() {
    return ClassLister.getSingleton().getClasses(AbstractExperimentSetup.class);
  }
}
