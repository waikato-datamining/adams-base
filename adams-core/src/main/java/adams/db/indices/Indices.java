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
 * Indices.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db.indices;

import java.util.Vector;

/**
 * Indices is a collection of Index objects for a table
 * 
 * @author dale
 * @version $Revision$
 */
public class Indices extends Vector<Index>{

  private static final long serialVersionUID = -6266605625222865131L;

  /**
   * Constructor
   *
   */
  public Indices() {
    super();
  }

  /**
   * Does the given Indices object match this?
   * @param ind
   * @return equals?
   */
  public boolean equals(Indices ind) {
    if (ind.size() != size()) {
      return(false); 
    }
    for (int i=0;i<ind.size();i++) {
      if (!present(ind.get(i))) {
	return(false);
      }
    }
    return(true);
  }
  
  /**
   * Is the Index present in this Indices object?
   * @param incol	Index
   * @return	present?
   */
  public boolean present(Index incol) {    
    for (int i=0;i<size();i++) {
      Index ic=get(i);
      if (ic.equals(incol)) {
	return(true);
      }     
    }
    return(false);
  }
}
