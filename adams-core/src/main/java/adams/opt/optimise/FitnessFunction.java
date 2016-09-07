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
 * FitnessFunction.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.opt.optimise;

/**
 * Fitness function.
 * 
 * @author dale
 * @version $Revision$
 */
public interface FitnessFunction {
  
  /**
   * Get fitness given vars. Higher the better.
   * 
   * @param opd	vars
   * @return	fitness
   */
  public double evaluate(OptData opd);
  
  /**
   * Callback. Do something with new best.
   * @param opd	data for new best
   */
  public void newBest(double ff,OptData opd);
}
