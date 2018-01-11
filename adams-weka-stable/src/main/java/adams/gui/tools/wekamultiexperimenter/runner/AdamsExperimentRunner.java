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
 * AdamsExperimentRunner.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.runner;

import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;

/**
 * Ancestor for classes that handle running a copy of the experiment
 * in a separate thread.
 */
public class AdamsExperimentRunner<T extends AbstractExperiment>
  extends AbstractAdamsExperimentRunner<T> {

  /** for serialization */
  private static final long serialVersionUID = -5591889874714150118L;

  /**
   * Initializes the thread.
   *
   * @param owner		the experimenter this runner belongs to
   * @throws Exception	if experiment is null or cannot be copied via serialization
   */
  public AdamsExperimentRunner(ExperimenterPanel owner) throws Exception {
    super(owner);
  }
}