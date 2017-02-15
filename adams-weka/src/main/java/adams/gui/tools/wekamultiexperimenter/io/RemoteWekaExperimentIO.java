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
 * RemoteWekaExperimentIO.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.io;

import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import adams.gui.tools.wekamultiexperimenter.runner.AbstractExperimentRunner;
import adams.gui.tools.wekamultiexperimenter.runner.RemoteWekaExperimentRunner;
import weka.experiment.Experiment;
import weka.experiment.RemoteExperiment;

import java.io.File;
import java.util.logging.Level;

/**
 * IO handler for remote experiments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteWekaExperimentIO
  extends AbstractWekaExperimentIO<RemoteExperiment> {

  /** for serialization. */
  private static final long serialVersionUID = -7678768486122004558L;

  /**
   * Creates a new experiment.
   * 
   * @return		the generated experiment, null if failed
   */
  @Override
  public RemoteExperiment create() {
    try {
      return new RemoteExperiment();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to instantiate new remote experiment!", e);
      return null;
    }
  }

  /**
   * Loads an experiment.
   * 
   * @param file	the file to load
   * @return		the experiment, null if failed to load
   */
  @Override
  public RemoteExperiment load(File file) {
    RemoteExperiment	result;
    Experiment		exp;
    try {
      exp = Experiment.read(file.getAbsolutePath());
      if (!(exp instanceof RemoteExperiment))
	result = new RemoteExperiment(exp);
      else
	result = (RemoteExperiment) exp;
      return result;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load remote experiment from " + file + "!", e);
      return null;
    }
  }

  /**
   * Saves an experiment.
   * 
   * @param exp		the experiment to save
   * @param file	the file to save to
   * @return		false if failed to save
   */
  @Override
  public boolean save(RemoteExperiment exp, File file) {
    try {
      Experiment.write(file.getAbsolutePath(), exp);
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write remote experiment to " + file + "!", e);
      return false;
    }
  }
  
  /**
   * Creates an experiment runner thread object.
   * 
   * @param owner	the owning experimenter
   * @return		the runner
   * @throws Exception	if failed to instantiate runner
   */
  @Override
  public AbstractExperimentRunner createRunner(ExperimenterPanel owner) throws Exception {
    return new RemoteWekaExperimentRunner(owner);
  }

  /**
   * Returns the experiment superclass/interface.
   *
   * @return		the super class/interface
   */
  public Class getExperimentClass() {
    return RemoteExperiment.class;
  }
}
