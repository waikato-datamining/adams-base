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
 * CatSwarmOptimization.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso;

import adams.core.Pausable;
import adams.core.Randomizable;
import adams.core.StoppableWithFeedback;
import adams.core.TechnicalInformationHandler;
import adams.core.option.OptionHandler;
import adams.event.CatSwarmOptimizationFitnessChangeListener;
import adams.opt.cso.stopping.AbstractStoppingCriterion;
import org.jblas.DoubleMatrix;

/**
 * Interface for Cat Swarm Optimizations (CSO).
 *
 * @author Mike Mayo (mmayo at waikato dot ac dot nz) - original code
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface CatSwarmOptimization
  extends OptionHandler, Randomizable, StoppableWithFeedback, Pausable, TechnicalInformationHandler {

  /**
   * Sets the swarm size to use.
   *
   * @param value	the size
   */
  public void setSwarmSize(int value);

  /**
   * Returns the swarm size in use.
   *
   * @return		the size
   */
  public int getSwarmSize();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String swarmSizeTipText();

  /**
   * Sets the phi parameter.
   *
   * @param value	phi
   */
  public void setPhi(double value);

  /**
   * Returns the phi parameter.
   *
   * @return		phi
   */
  public double getPhi();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String phiTipText();

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value);

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText();

  /**
   * Sets the stopping criterion.
   *
   * @param value	the criterion
   */
  public void setStopping(AbstractStoppingCriterion value);

  /**
   * Returns the stopping criterion.
   *
   * @return		the criterion
   */
  public AbstractStoppingCriterion getStopping();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stoppingTipText();

  /**
   * Problem-specific random particle generator
   * -- should return a one dimensional matrix of fixed length
   *
   */
  public abstract DoubleMatrix randomParticle();

  /**
   * Returns the current iteration.
   *
   * @return		the iteration
   */
  public int getCurrentIteration();

  /**
   * Stringifier
   * -- extend this method to add additional problem-specific
   *    information
   *
   */
  public String toString();

  /**
   * Method to get the index of the best particle in the swarm
   *
   */
  public int getBestIndex();

  /**
   * Method to get the best particle in the swarm
   *
   */
  public DoubleMatrix getBest();

  /**
   * Methods to get information about the swarm as a string
   * -- extend to add additional statistics
   *
   */
  public String reportStringHeader();

  /**
   * Returns the current fitness.
   *
   * @return		the fitness
   */
  public double getCurrentFitness();

  /**
   * Generates a simple report string.
   *
   * @return		the generated string
   */
  public String reportString();

  /**
   * Updates the fitness of the specified swarm member.
   *
   * @param index	the index of the member
   * @param fitness	the calculated fitness
   */
  public void updateFitness(int index, double fitness);

  /**
   * Run method
   * -- performs the main loop of CSO
   * -- returns best solution found
   *
   */
  public DoubleMatrix run();

  /**
   * Stops the execution.
   */
  public void stopExecution();

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped();

  /**
   * Pauses the execution.
   */
  public void pauseExecution();

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused();

  /**
   * Resumes the execution.
   */
  public void resumeExecution();

  /**
   * Adds the listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addFitnessChangeListener(CatSwarmOptimizationFitnessChangeListener l);

  /**
   * Removes the listener from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeFitnessChangeListener(CatSwarmOptimizationFitnessChangeListener l);
}
