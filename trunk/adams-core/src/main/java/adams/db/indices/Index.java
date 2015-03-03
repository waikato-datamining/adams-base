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
 * Index.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db.indices;

import java.util.Vector;

/**
 * An Index is an ordered collection of IndexColumns
 * 
 * @author dale
 * @version $Revision$
 */
public class Index extends Vector<IndexColumn>{

  private static final long serialVersionUID = -6266605625222865131L;

  /**
   * Constructor
   *
   */
  public Index() {
    super();
  }

  /**
   * Add IndexColumn to this index. Return index count
   * @param ic	IndexColumn to add
   * @return	current number of indices
   */
  public int addIndexColumn(IndexColumn ic) {
    add(ic);    
    return(size());
  }


  /**
   * Does given Index match this one?
   * @param index	Index to compare
   * @return	equals?
   */
  public boolean equals(Index index) {
    if (index.size() != size()) {
      return(false); 
    }
    for (int i=0;i<index.size();i++) {
      if (!presentAt(i,index.get(i))) {
	return(false);
      }
    }
    return(true);
  }
  
  /**
   * Is given IndexColumn present at given position in collection?
   * @param pos		position
   * @param incol	IndexColumn
   * @return	present?
   */
  public boolean presentAt(int pos,IndexColumn incol) {    
    IndexColumn ic=get(pos);
    if (incol.equals(ic)) {
      return(true);
    }     

    return(false);
  }  
}
