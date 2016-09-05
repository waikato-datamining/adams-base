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
 * DefaultAdamsExperimentIO.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.io;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractAdamsExperimentReader;
import adams.data.io.output.AbstractAdamsExperimentWriter;
import adams.gui.chooser.AdamsExperimentFileChooser;
import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;
import adams.gui.tools.wekamultiexperimenter.experiment.CrossValidationExperiment;
import adams.gui.tools.wekamultiexperimenter.runner.AbstractExperimentRunner;
import adams.gui.tools.wekamultiexperimenter.runner.AdamsExperimentRunner;

import java.io.File;
import java.util.logging.Level;

/**
 * Default IO handler for experiments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultAdamsExperimentIO
  extends AbstractAdamsExperimentIO<AbstractExperiment> {

  /** for serialization. */
  private static final long serialVersionUID = -7678768486122004558L;

  /**
   * Creates a new experiment.
   * 
   * @return		the generated experiment, null if failed
   */
  @Override
  public AbstractExperiment create() {
    return new CrossValidationExperiment();
  }

  /**
   * Loads an experiment.
   * 
   * @param file	the file to load
   * @return		the experiment, null if failed to load
   */
  @Override
  public AbstractExperiment load(File file) {
    AbstractExperiment			result;
    AdamsExperimentFileChooser  	chooser;
    AbstractAdamsExperimentReader 	reader;

    chooser = (AdamsExperimentFileChooser) getFileChooser();
    try {
      reader = chooser.getReaderForFile(file);
      if (reader == null)
	throw new Exception("No reader found for: " + file);
      result = reader.read(new PlaceholderFile(file));
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
  public boolean save(AbstractExperiment exp, File file) {
    AdamsExperimentFileChooser		chooser;
    AbstractAdamsExperimentWriter 	writer;
    String				msg;

    chooser = (AdamsExperimentFileChooser) getFileChooser();
    try {
      writer = chooser.getWriterForFile(file);
      if (writer == null)
	throw new Exception("No writer found for: " + file);
      msg = writer.write(new PlaceholderFile(file), exp);
      if (msg != null)
	throw new Exception(msg);
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
    return new AdamsExperimentRunner(owner);
  }
}
