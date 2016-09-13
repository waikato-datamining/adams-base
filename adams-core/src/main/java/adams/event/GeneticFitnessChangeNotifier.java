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
 * FitnessChangeNotifier.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.event;


/**
 * Interface for genetic algorithms that notify other objects about
 * changes of their fitness.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface GeneticFitnessChangeNotifier {

  /**
   * Returns the best currently best fitness.
   * 
   * @return		the fitness
   */
  public double getCurrentFitness();
  
  /**
   * Adds the given listener to its internal list of listeners.
   * 
   * @param l		the listener to add
   */
  public void addFitnessChangeListener(GeneticFitnessChangeListener l);

  /**
   * Removes the given listener from its internal list of listeners.
   * 
   * @param l		the listener to remove
   */
  public void removeFitnessChangeListener(GeneticFitnessChangeListener l);
}
