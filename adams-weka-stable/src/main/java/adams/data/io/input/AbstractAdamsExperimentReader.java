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
 * AbstractAdamsExperimentReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.ClassLister;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.io.output.AbstractAdamsExperimentWriter;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;

/**
 * Ancestor for readers for ADAMS Experiments.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAdamsExperimentReader
  extends AbstractOptionHandler
  implements FileFormatHandler {

  private static final long serialVersionUID = 4625713362723567006L;

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
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  public abstract AbstractAdamsExperimentWriter getCorrespondingWriter();

  /**
   * Returns whether the reader is actually available.
   *
   * @return		true if available and ready to use
   */
  public boolean isAvailable() {
    return true;
  }

  /**
   * Performs checks.
   *
   * @param file	the image file to check
   */
  protected void check(PlaceholderFile file) {
    if (file == null)
      throw new IllegalStateException("No file provided!");
    if (!file.exists())
      throw new IllegalStateException("File '" + file + "' does not exist!");
    if (file.isDirectory())
      throw new IllegalStateException("File '" + file + "' points to a directory!");
  }

  /**
   * Performs the actual reading of the experiment file.
   *
   * @param file	the file to read
   * @return		the experiment, null if failed to read
   */
  protected abstract AbstractExperiment doRead(PlaceholderFile file);

  /**
   * Reads the experiment file.
   *
   * @param file	the file to read
   * @return		the experiment, null if failed to read
   */
  public AbstractExperiment read(PlaceholderFile file) {
    check(file);
    return doRead(file);
  }

  /**
   * Returns a list with classes of readers.
   *
   * @return		the reader classes
   */
  public static Class[] getReaders() {
    return ClassLister.getSingleton().getClasses(AbstractAdamsExperimentReader.class);
  }
}
