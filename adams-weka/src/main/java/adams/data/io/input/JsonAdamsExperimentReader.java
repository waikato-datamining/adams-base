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
 * JsonAdamsExperimentReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.PlaceholderFile;
import adams.core.option.JsonConsumer;
import adams.data.io.output.AbstractAdamsExperimentWriter;
import adams.data.io.output.JsonAdamsExperimentWriter;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;

import java.util.logging.Level;

/**
 * Reads ADAMS Experiments in JSON format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonAdamsExperimentReader
  extends AbstractAdamsExperimentReader {

  private static final long serialVersionUID = 7175000296488786947L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads ADAMS Experiments in JSON format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "JSON experiment";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"expjson"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public AbstractAdamsExperimentWriter getCorrespondingWriter() {
    return new JsonAdamsExperimentWriter();
  }

  /**
   * Performs the actual reading of the experiment file.
   *
   * @param file	the file to read
   * @return		the experiment, null if failed to read
   */
  @Override
  protected AbstractExperiment doRead(PlaceholderFile file) {
    try {
      return (AbstractExperiment) new JsonConsumer().fromFile(file);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load experiment from: " + file, e);
      return null;
    }
  }
}
