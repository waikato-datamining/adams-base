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
import adams.core.SerializedObject;
import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.experiment.CSVResultListener;
import weka.experiment.DatabaseResultListener;
import weka.experiment.Experiment;
import weka.experiment.InstanceQuery;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Ancestor for classes that handle running a copy of the experiment
 * in a separate thread.
 *
 * @see weka.gui.experiment.RunPanel.ExperimentRunner
 */
public abstract class AbstractExperimentRunner
  extends Thread
  implements Serializable {

  /** for serialization */
  private static final long serialVersionUID = -5591889874714150118L;

  /** The message displayed when no experiment is running */
  protected static final String NOT_RUNNING = "Not running";

  /** the experimenter this runner belongs to. */
  protected ExperimenterPanel m_Owner;

  /** the copy of the experiment. */
  protected Experiment m_Exp;

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
    Experiment exp = m_Owner.getExperiment();
    logMessage("--> START: " + DateUtils.getTimestampFormatter().format(new Date()));
    logMessage("Running experiment: " + exp.toString());
    logMessage("Writing experiment copy");
    SerializedObject so = new SerializedObject(exp);
    logMessage("Reading experiment copy");
    m_Exp = (Experiment) so.getObject();
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
  protected void doInitialize() throws Exception {
    m_Exp.initialize();
  }

  /**
   * Performs the actual running of the experiment.
   *
   * @throws Exception	fails due to some error
   */
  protected abstract void doRun() throws Exception;

  /**
   * Examines the supplied experiment to determine the results destination and
   * attempts to load the results.
   */
  protected void loadResults() {
    File 			resultFile;
    DatabaseResultListener	dblistener;
    InstanceQuery 		query;
    String 			tableName;
    Instances			data;
    AbstractFileLoader		loader;

    logMessage("Attempting to load results...");

    data = null;
    if ((m_Exp.getResultListener() instanceof CSVResultListener)) {
      resultFile = ((CSVResultListener) m_Exp.getResultListener()).getOutputFile();
      if ((resultFile == null)) {
	logMessage("No result file");
      }
      else {
	loader = ConverterUtils.getLoaderForFile(resultFile);
	if (loader == null) {
	  logMessage("Failed to determine loader for results file: " + resultFile);
	}
	else {
	  try {
	    loader.setFile(resultFile);
	    data = loader.getDataSet();
	  }
	  catch (Exception e) {
	    logError(e, "Problem reading result file");
	  }
	}
      }
    }
    else if (m_Exp.getResultListener() instanceof DatabaseResultListener) {
      dblistener = (DatabaseResultListener) m_Exp.getResultListener();
      try {
	query = new InstanceQuery();
	query.setDatabaseURL(dblistener.getDatabaseURL());
	query.setUsername(dblistener.getUsername());
	query.setPassword(dblistener.getPassword());
	query.connectToDatabase();
	tableName = query.getResultsTableName(m_Exp.getResultProducer());
	query.setQuery("SELECT * FROM " + tableName);
	data = query.retrieveInstances();
      }
      catch (Exception ex) {
	logError(ex, "Problem reading database");
      }
    }
    else {
      logMessage("Can't get results from experiment");
    }

    if (data != null) {
      m_Owner.getAnalysisPanel().setResults(data);
      logMessage("Successfully loaded results!");
    }
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