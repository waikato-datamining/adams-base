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
 * NestedAdamsExperimentWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.PlaceholderFile;
import adams.core.option.NestedProducer;
import adams.data.io.input.AbstractAdamsExperimentReader;
import adams.data.io.input.NestedAdamsExperimentReader;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;

/**
 * Writes ADAMS experiments in nested format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NestedAdamsExperimentWriter
  extends AbstractAdamsExperimentWriter {

  private static final long serialVersionUID = 6314312820017136318L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes ADAMS Experiments in nested format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new NestedAdamsExperimentReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new NestedAdamsExperimentReader().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public AbstractAdamsExperimentReader getCorrespondingReader() {
    return new NestedAdamsExperimentReader();
  }

  /**
   * Performs the actual writing of the experiment file.
   *
   * @param file	the file to write to
   * @param exp	        the experiment to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, AbstractExperiment exp) {
    String		result;
    NestedProducer 	producer;

    result = null;

    producer = new NestedProducer();
    producer.setOutputClasspath(false);
    producer.produce(exp);
    if (!producer.write(file.getAbsolutePath()))
      result = "Failed to write experiment to: " + file;

    return result;
  }
}
