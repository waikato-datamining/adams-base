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
 * AbstractOptionTraverserWithResult.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Ancestor for option traversers that return a result.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of the result
 */
public abstract class AbstractOptionTraverserWithResult<T>
  extends AbstractOptionTraverser
  implements OptionTraverserWithResult<T> {

  /** for serialization. */
  private static final long serialVersionUID = 7516066249256588141L;
  
  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();
    
    resetResult();
  }
  
  /**
   * Resets the result before traversing.
   */
  @Override
  public abstract void resetResult();

  /**
   * Returns the result of the traversal.
   *
   * @return		the result
   */
  @Override
  public abstract T getResult();
}
