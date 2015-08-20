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

/*
 * FitnessChangeEvent.java
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.event;

import adams.genetic.AbstractGeneticAlgorithm;

import java.util.EventObject;

/**
 * Event that gets sent whenever the fitness of a genetic algorithm 
 * changed.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FitnessChangeEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 4900409199763866247L;

  /** the fitness that triggered this event. */
  protected double m_Fitness;

  /** the associated setup, if any. */
  protected Object m_Setup;

  /** the associated weights. */
  protected int[] m_Weights;

  /**
   * Initializes the event.
   * 
   * @param source	the algorithm that triggered the event
   * @param fitness	the fitness that triggered this event
   * @param setup 	the setup
   * @param weights	the weights/bits
   */
  public FitnessChangeEvent(AbstractGeneticAlgorithm source, double fitness, Object setup, int[] weights) {
    super(source);
    
    m_Fitness = fitness;
    m_Setup   = setup;
    m_Weights = weights.clone();
  }
  
  /**
   * Returns the genetic algorithm that triggered the event.
   * 
   * @return		the genetic algorithm
   */
  public adams.genetic.AbstractGeneticAlgorithm getGeneticAlgorithm() {
    return (adams.genetic.AbstractGeneticAlgorithm) getSource();
  }
  
  /**
   * Returns the fitness that triggered this event.
   * 
   * @return		the fitness
   */
  public double getFitness() {
    return m_Fitness;
  }

  /**
   * Returns the associated setup.
   *
   * @return		the setup
   */
  public Object getSetup() {
    return m_Setup;
  }

  /**
   * Returns the associated weights/bits.
   *
   * @return		the weights
   */
  public int[] getWeights() {
    return m_Weights;
  }
}
