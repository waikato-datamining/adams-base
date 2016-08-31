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
 * AbstractAdamsExperimentIO.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.io;

import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.ObjectFileChooser;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;

/**
 * Ancestor for classes that handle loading/saving of experiments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of experiment
 */
public abstract class AbstractAdamsExperimentIO<T extends AbstractExperiment>
  extends AbstractExperimentIO<T> {

  /** for serialization. */
  private static final long serialVersionUID = -1358953690042787633L;

  /**
   * Creates and returns a file chooser for loading/saving experiments.
   * 
   * @return		the file chooser
   */
  protected BaseFileChooser createFileChooser() {
    return new ObjectFileChooser();
  }
}
