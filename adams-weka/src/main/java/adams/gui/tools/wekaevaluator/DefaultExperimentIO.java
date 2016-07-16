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
 * DefaultExperimentIO.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekaevaluator;

import java.io.File;
import java.util.logging.Level;

import weka.experiment.Experiment;
import weka.experiment.RemoteExperiment;

/**
 * Default IO handler for experiments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultExperimentIO
  extends AbstractExperimentIO<Experiment> {

  /** for serialization. */
  private static final long serialVersionUID = -7678768486122004558L;

  /**
   * Creates a new experiment.
   * 
   * @return		the generated experiment, null if failed
   */
  @Override
  public Experiment create() {
    return new Experiment();
  }

  /**
   * Loads an experiment.
   * 
   * @param file	the file to load
   * @return		the experiment, null if failed to load
   */
  @Override
  public Experiment load(File file) {
    Experiment	result;
    try {
      result = Experiment.read(file.getAbsolutePath());
      if (result instanceof RemoteExperiment)
	result = ((RemoteExperiment) result).getBaseExperiment();
      return result;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load experiment from " + file + "!", e);
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
  public boolean save(Experiment exp, File file) {
    try {
      Experiment.write(file.getAbsolutePath(), exp);
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write experiment to " + file + "!", e);
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
    return new DefaultExperimentRunner(owner);
  }
}
