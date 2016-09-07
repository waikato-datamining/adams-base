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
 * MaxTrainTime.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso.stopping;

import adams.opt.cso.AbstractCatSwarmOptimization;

/**
 <!-- globalinfo-start -->
 * Stops after the maximum number of seconds have been reached.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-max-train &lt;int&gt; (property: maxTrainTime)
 * &nbsp;&nbsp;&nbsp;The maximum number of seconds to training time (0 = unlimited time).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MaxTrainTime
  extends AbstractStoppingCriterion {

  private static final long serialVersionUID = -725187280232195524L;

  /** the maximum number of seconds to train. */
  protected int m_MaxTrainTime;

  /** the time when training commenced. */
  protected long m_TrainStart;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stops after the maximum number of seconds have been reached.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-train", "maxTrainTime",
      0, 0, null);
  }

  /**
   * Sets the maximum number of seconds to perform training.
   *
   * @param value	the number of seconds
   */
  public void setMaxTrainTime(int value) {
    if (getOptionManager().isValid("maxTrainTime", value)) {
      m_MaxTrainTime = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of seconds to perform training.
   *
   * @return		the number of seconds
   */
  public int getMaxTrainTime() {
    return m_MaxTrainTime;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTrainTimeTipText() {
    return "The maximum number of seconds to training time (0 = unlimited time).";
  }

  /**
   * Gets called when the genetic algorithm starts.
   */
  @Override
  public void start() {
    m_TrainStart = System.currentTimeMillis();
  }

  /**
   * Performs the actual check of the stopping criterion.
   *
   * @param swarm	the algorithm
   * @return		true if to stop
   */
  @Override
  protected boolean doCheckStopping(AbstractCatSwarmOptimization swarm) {
    if (m_MaxTrainTime == 0)
      return false;
    else
      return ((double) (System.currentTimeMillis() - m_TrainStart) / 1000.0 >= m_MaxTrainTime);
  }
}
