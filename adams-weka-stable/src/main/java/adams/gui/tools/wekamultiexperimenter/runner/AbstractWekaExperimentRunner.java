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
 * AbstractWekaExperimentRunner.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.runner;

import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.experiment.CSVResultListener;
import weka.experiment.DatabaseResultListener;
import weka.experiment.Experiment;
import weka.experiment.InstanceQuery;

import java.io.File;

/**
 * Ancestor for classes that handle running a copy of the experiment
 * in a separate thread.
 *
 * @see weka.gui.experiment.RunPanel.ExperimentRunner
 */
public abstract class AbstractWekaExperimentRunner<T extends Experiment>
  extends AbstractExperimentRunner<T> {

  /** for serialization */
  private static final long serialVersionUID = -5591889874714150118L;

  /**
   * Initializes the thread.
   *
   * @param owner		the experimenter this runner belongs to
   * @throws Exception	if experiment is null or cannot be copied via serialization
   */
  public AbstractWekaExperimentRunner(ExperimenterPanel owner) throws Exception {
    super(owner);
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
}