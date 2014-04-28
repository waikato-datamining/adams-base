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
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.event;

import java.util.EventObject;

import adams.genetic.MTAbstractGeneticAlgorithm;
import adams.optimise.genetic.AbstractGeneticAlgorithm;

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
  
  /**
   * Initializes the event.
   * 
   * @param source	the algorithm that triggered the event
   * @param fitness	the fitness that triggered this event
   */
  public FitnessChangeEvent(MTAbstractGeneticAlgorithm source, double fitness) {
    super(source);
    
    m_Fitness = fitness;
  }
  
  /**
   * Returns the genetic algorithm that triggered the event.
   * 
   * @return		the genetic algorithm
   */
  public AbstractGeneticAlgorithm getGeneticAlgorithm() {
    return (AbstractGeneticAlgorithm) getSource();
  }
  
  /**
   * Returns the fitness that triggered this event.
   * 
   * @return		the fitness
   */
  public double getFitness() {
    return m_Fitness;
  }
}
