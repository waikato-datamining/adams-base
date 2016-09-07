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
 * MultiStoppingCriterion.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.opt.genetic.stopping;

import adams.opt.genetic.AbstractGeneticAlgorithm;

/**
 <!-- globalinfo-start -->
 * Applies its sub-criteria, one after the other.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-criterion &lt;adams.genetic.stopping.AbstractStoppingCriterion&gt; [-criterion ...] (property: criteria)
 * &nbsp;&nbsp;&nbsp;The criteria to apply, one after the other.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiStoppingCriterion
  extends AbstractStoppingCriterion {

  private static final long serialVersionUID = -725187280232195524L;

  /** the criteria to use. */
  protected AbstractStoppingCriterion[] m_Criteria;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies its sub-criteria, one after the other.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "criterion", "criteria",
      new AbstractStoppingCriterion[0]);
  }

  /**
   * Sets the criteria to use.
   *
   * @param value	the criteria
   */
  public void setCriteria(AbstractStoppingCriterion[] value) {
    m_Criteria = value;
    reset();
  }

  /**
   * Returns the criteria in use.
   *
   * @return		the criteria
   */
  public AbstractStoppingCriterion[] getCriteria() {
    return m_Criteria;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String criteriaTipText() {
    return "The criteria to apply, one after the other.";
  }

  /**
   * Gets called when the genetic algorithm starts.
   */
  @Override
  public void start() {
    for (AbstractStoppingCriterion criterion: m_Criteria)
      criterion.start();
  }

  /**
   * Performs the actual check of the stopping criterion.
   *
   * @param genetic	the algorithm
   * @return		true if to stop
   */
  @Override
  protected boolean doCheckStopping(AbstractGeneticAlgorithm genetic) {
    int		i;

    for (i = 0; i < m_Criteria.length; i++) {
      if (m_Criteria[i].checkStopping(genetic)) {
        if (isLoggingEnabled())
          getLogger().info("checkStopping #" + (i+1) + ": true");
        return true;
      }
    }
    return false;
  }
}
