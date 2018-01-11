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
 * TransformNNSearch.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package weka.core.neighboursearch;

import weka.core.Instance;
import weka.core.Instances;

public abstract class TransformNNSearch extends NewNNSearch {
  
  /** Transformed instances. */
  protected Instances m_myInstances;
  
  public TransformNNSearch() {
    // TODO Auto-generated constructor stub
  }
  
  public Instances getTransformedInstances() {
    return(m_myInstances);
  }
  
  public TransformNNSearch(Instances insts) {
    super(insts);
    // TODO Auto-generated constructor stub
  }

  public abstract Instance transformInstance(Instance in) throws Exception;
  
}
 