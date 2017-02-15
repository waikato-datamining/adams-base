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
 * WekaExperimentContainer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for Weka experiment results.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExperimentContainer
  extends AbstractContainer {

  private static final long serialVersionUID = 4355446529330404212L;

  /** the identifier for the experiment. */
  public final static String VALUE_EXPERIMENT = "Experiment";

  /** the identifier for the results (instances). */
  public final static String VALUE_INSTANCES = "Instances";

  /** the identifier for the results (spreadsheet). */
  public final static String VALUE_SPREADSHEET = "Spreadsheet";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaExperimentContainer() {
    this(null, null, null);
  }

  /**
   * Initializes the container with just the Instances.
   *
   * @param inst	the experiment results
   */
  public WekaExperimentContainer(Instances inst) {
    this(null, inst, null);
  }

  /**
   * Initializes the container.
   *
   * @param exp		the experiment (can be null)
   * @param inst	the experiment results
   * @param sheet	the experiment results (can be null)
   */
  public WekaExperimentContainer(AbstractExperiment exp, Instances inst, SpreadSheet sheet) {
    super();

    store(VALUE_EXPERIMENT,  exp);
    store(VALUE_INSTANCES,   inst);
    store(VALUE_SPREADSHEET, sheet);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_EXPERIMENT, "experiment object " + AbstractExperiment.class.getName());
    addHelp(VALUE_INSTANCES, "results (instances); " + Instances.class.getName());
    addHelp(VALUE_SPREADSHEET, "result (spreadsheet); " + SpreadSheet.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<>();

    result.add(VALUE_EXPERIMENT);
    result.add(VALUE_INSTANCES);
    result.add(VALUE_SPREADSHEET);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_INSTANCES);
  }
}
