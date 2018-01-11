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
 * AbstractClassAttributeHeuristic.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.classattribute;

import adams.core.option.AbstractOptionHandler;
import weka.core.Instances;

/**
 * Ancestor for heuristics that determine the class attribute for a dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClassAttributeHeuristic
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 3567877912866412434L;

  /**
   * Determines the class attribute index for the given dataset.
   *
   * @param data	the dataset to inspect
   * @return		the index, -1 if failed to determine
   */
  public abstract int determineClassAttribute(Instances data);
}
