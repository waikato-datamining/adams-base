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
 * AbstractExperiment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.experiment;

import adams.core.StatusMessageHandler;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import weka.classifiers.Classifier;

import java.util.logging.Level;

/**
 * Ancestor for simple experiments.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractExperiment
  extends AbstractOptionHandler
  implements Stoppable {

  private static final long serialVersionUID = -345521029095304309L;

  /** the classifiers to evaluate. */
  protected Classifier[] m_Classifiers;

  /** the datasets to evaluate. */
  protected PlaceholderFile[] m_Datasets;

  /** the number of runs. */
  protected int m_Runs;

  /** for notifications. */
  protected StatusMessageHandler m_StatusMessageHandler;

  /** whether the experiment is running. */
  protected boolean m_Running;

  /** whether the experiment was stopped. */
  protected boolean m_Stopped;

  /** the generated results. */
  protected SpreadSheet m_Results;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "classifier", "classifiers",
      new Classifier[0]);

    m_OptionManager.add(
      "dataset", "datasets",
      new PlaceholderFile[0]);

    m_OptionManager.add(
      "runs", "runs",
      10, 1, null);
  }

  /**
   * Sets the classifiers to use.
   *
   * @param value	the classifiers
   */
  public void setClassifiers(Classifier[] value) {
    m_Classifiers = value;
    reset();
  }

  /**
   * Returns the classifiers.
   *
   * @return		the classifiers
   */
  public Classifier[] getClassifiers() {
    return m_Classifiers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifiersTipText() {
    return "The classifiers to evaluate.";
  }

  /**
   * Sets the datasets to use.
   *
   * @param value	the datasets
   */
  public void setDatasets(PlaceholderFile[] value) {
    m_Datasets = value;
    reset();
  }

  /**
   * The datasets in use.
   *
   * @return		the datasets
   */
  public PlaceholderFile[] getDatasets() {
    return m_Datasets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetsTipText() {
    return "The datasets to use";
  }

  /**
   * Sets the number of runs.
   *
   * @param value	the runs
   */
  public void setRuns(int value) {
    m_Runs = value;
    reset();
  }

  /**
   * Returns the number of runs.
   *
   * @return		the runs
   */
  public int getRuns() {
    return m_Runs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String runsTipText() {
    return "The number of runs to perform.";
  }

  /**
   * Hook method just before the experiment is run (after initialization).
   *
   * @return		null if successful, otherwise error message
   */
  protected String preRun() {
    return null;
  }

  /**
   * Returns the collected results.
   *
   * @return		the results
   */
  public SpreadSheet getResults() {
    return m_Results;
  }

  /**
   * Displays the message.
   *
   * @param msg		the message to display
   */
  protected void log(String msg) {
    if (m_StatusMessageHandler != null)
      m_StatusMessageHandler.showStatus(msg);
    else
      getLogger().info(msg);
  }

  /**
   * Displays the error.
   *
   * @param msg		the message to display
   * @param t		the exception
   */
  protected void log(String msg, Throwable t) {
    if (m_StatusMessageHandler != null)
      m_StatusMessageHandler.showStatus(msg + "\n" + Utils.throwableToString(t));
    else
      getLogger().log(Level.SEVERE, msg, t);
  }

  /**
   * Initializes the experiment.
   */
  protected String initRun() {
    m_Stopped = false;
    m_Running = true;
    m_Results = new DefaultSpreadSheet();
    return null;
  }

  /**
   * Runs the actual experiment.
   *
   * @return		null if successful, otherwise error message
   */
  protected abstract String doRun();

  /**
   * Hook method just after the experiment was run.
   *
   * @param success	true if successfully run
   */
  protected void postRun(boolean success) {
  }

  /**
   * Executes the experiment.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String	result;

    log("Initializing...");
    result = initRun();
    if (result == null) {
      log("Pre-Run...");
      result = preRun();
    }
    if (result == null) {
      log("Run...");
      result = doRun();
      log("Post-Run...");
      postRun((result == null) && !m_Stopped);
    }

    if (result != null)
      log(result);

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }
}
