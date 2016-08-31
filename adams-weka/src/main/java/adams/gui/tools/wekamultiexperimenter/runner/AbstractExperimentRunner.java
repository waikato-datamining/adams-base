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
 * AbstractExperimentRunner.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.runner;

import adams.core.DateUtils;
import adams.core.ObjectCopyHelper;
import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;

import java.io.Serializable;
import java.util.Date;

/**
 * Ancestor for classes that handle running a copy of the experiment
 * in a separate thread.
 *
 * @see weka.gui.experiment.RunPanel.ExperimentRunner
 */
public abstract class AbstractExperimentRunner<T>
  extends Thread
  implements Serializable {

  /** for serialization */
  private static final long serialVersionUID = -5591889874714150118L;

  /** The message displayed when no experiment is running */
  protected static final String NOT_RUNNING = "Not running";

  /** the experimenter this runner belongs to. */
  protected ExperimenterPanel m_Owner;

  /** the copy of the experiment. */
  protected T m_Exp;

  /** whether the experiment is still running. */
  protected boolean m_Running;

  /** whether the user cancelled the experiment. */
  protected boolean m_Aborted;

  /**
   * Initializes the thread.
   *
   * @param owner		the experimenter this runner belongs to
   * @throws Exception	if experiment is null or cannot be copied via serialization
   */
  public AbstractExperimentRunner(ExperimenterPanel owner) throws Exception {
    super();

    m_Owner = owner;
    T exp = (T) m_Owner.getExperiment();
    logMessage("--> START: " + DateUtils.getTimestampFormatter().format(new Date()));
    logMessage("Running experiment: " + exp.toString());
    m_Exp = (T) ObjectCopyHelper.copyObject(exp);
    if (m_Exp == null)
      throw new IllegalStateException("Failed to create copy of experiment!");
    logMessage("Made experiment copy");
  }

  /**
   * Aborts the experiment.
   */
  public void abortExperiment() {
    m_Running = false;
    m_Aborted = true;
  }

  /**
   * Whether the experiment is still running.
   *
   * @return		true if still running
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Logs the exception with no dialog.
   *
   * @param t		the exception
   */
  public void logMessage(Throwable t) {
    m_Owner.logMessage(t);
  }

  /**
   * Logs the message.
   *
   * @param msg		the log message
   */
  public void logMessage(String msg) {
    m_Owner.logMessage(msg);
  }

  /**
   * Logs the exception and also displays an error dialog.
   *
   * @param t		the exception
   * @param title	the title for the dialog
   */
  public void logError(Throwable t, String title) {
    m_Owner.logError(t, title);
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  public void logError(String msg, String title) {
    m_Owner.logError(msg, title);
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_Owner.showStatus(msg);
    m_Owner.logMessage(msg);
  }

  /**
   * Updates the owner's state.
   */
  public void update() {
    m_Owner.update();
  }

  /**
   * Hook method that gets executed before the experiment gets initialized.
   *
   * @throws Exception	fails due to some error
   */
  protected void preRun() throws Exception {
  }

  /**
   * Initializes the experiment.
   *
   * @throws Exception	fails due to some error
   */
  protected abstract void doInitialize() throws Exception;

  /**
   * Performs the actual running of the experiment.
   *
   * @throws Exception	fails due to some error
   */
  protected abstract void doRun() throws Exception;

  /**
   * Hook method that gets executed after the experiment has finished
   * (successfully or not).
   *
   * @param success	whether successfully finished (neither error, nor aborted)
   */
  protected void postRun(boolean success) {
  }

  /**
   * Starts running the experiment.
   */
  @Override
  public void run() {
    m_Running = true;
    m_Aborted = false;
    update();
    try {
      logMessage("Started");
      preRun();
      showStatus("Initializing...");
      doInitialize();
      showStatus("Running...");
      doRun();
      showStatus("Finished!");
    }
    catch (Exception ex) {
      logError(ex, "Execution error");
      showStatus(ex.getMessage());
      m_Running = false;
    }
    finally {
      postRun(!m_Aborted && m_Running);
      m_Running = false;
    }
  }
}