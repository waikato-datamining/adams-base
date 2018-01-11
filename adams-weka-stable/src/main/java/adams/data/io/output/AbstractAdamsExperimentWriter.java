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
 * AbstractAdamsExperimentWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.ClassLister;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.io.input.AbstractAdamsExperimentReader;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;

/**
 * Ancestor for ADAMS Experiment writers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAdamsExperimentWriter
  extends AbstractOptionHandler
  implements FileFormatHandler {

  private static final long serialVersionUID = -6244722208500006381L;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  public abstract AbstractAdamsExperimentReader getCorrespondingReader();

  /**
   * Returns whether the writer is actually available.
   *
   * @return		true if available and ready to use
   */
  public boolean isAvailable() {
    return true;
  }

  /**
   * Performs checks.
   *
   * @param exp	the experiment to check
   */
  protected void check(AbstractExperiment exp) {
    if (exp == null)
      throw new IllegalStateException("No experiment provided!");
  }

  /**
   * Performs the actual writing of the experiment file.
   *
   * @param file	the file to write to
   * @param exp	        the experiment to write
   * @return		null if successfully written, otherwise error message
   */
  protected abstract String doWrite(PlaceholderFile file, AbstractExperiment exp);

  /**
   * Writes the experiment file.
   *
   * @param file	the file to write to
   * @param exp	        the experiment to write
   * @return		null if successfully written, otherwise error message
   */
  public String write(PlaceholderFile file, AbstractExperiment exp) {
    check(exp);
    return doWrite(file, exp);
  }

  /**
   * Returns a list with classes of writers.
   *
   * @return		the writer classes
   */
  public static Class[] getWriters() {
    return ClassLister.getSingleton().getClasses(AbstractAdamsExperimentWriter.class);
  }
}
