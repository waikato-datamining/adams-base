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
 * OptionTraversalPath.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.util.Stack;

/**
 * Keeps track of the properties traversed so far.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionTraversalPath
  extends Stack<String> {

  /** for serialization. */
  private static final long serialVersionUID = 1386973529868246153L;

  /**
   * Returns the full property path.
   * 
   * @return		the path
   */
  public String getPath() {
    StringBuilder	result;
    int			i;
    
    result = new StringBuilder();
    
    for (i = 0; i < size(); i++) {
      if (i > 0)
	result.append(".");
      result.append(get(i).replace(".", "\\."));
    }
    
    return result.toString();
  }
}
