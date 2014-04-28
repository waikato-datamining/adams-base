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
 * NullToken.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

/**
 * A dummy token without any payload.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NullToken
  extends Token {

  /** for serialization. */
  private static final long serialVersionUID = -2164551823465416849L;
  
  /**
   * Ignores any payload.
   * 
   * @param value	ignored
   */
  public void setPayload(Object value) {
  }
  
  /**
   * Returns a string representation of the payload.
   * 
   * @return		the string representation
   */
  public String toString() {
    return "Token #" + hashCode();
  }
}
