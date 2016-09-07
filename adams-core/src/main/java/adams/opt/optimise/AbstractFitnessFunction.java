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
 * AbstractFitnessFunction.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.opt.optimise;

import adams.core.option.AbstractOptionHandler;

/**
 * Abstract ancestor for fitness functions.
 * 
 * @author dale
 * @version $Revision$
 */
public abstract class AbstractFitnessFunction
  extends AbstractOptionHandler 
  implements FitnessFunction {

  /** for serialization. */
  private static final long serialVersionUID = -275374067735516573L;
  
  public void newBest(double val, OptData opd) {
  }

}
