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
 * AbstractRecursiveDirectoryLister.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io.lister;

/**
 * Ancestor for recursive directory listers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRecursiveDirectoryLister
  extends AbstractDirectoryLister
  implements RecursiveDirectoryLister {

  /** for serialization. */
  private static final long serialVersionUID = -1846677500660003814L;

  /** whether to look for files/dirs recursively. */
  protected boolean m_Recursive;

  /** the maximum depth to look recursively (0 = only watch dir, -1 = infinite). */
  protected int m_MaxDepth;

  /**
   * Initializes the object.
   */
  public AbstractRecursiveDirectoryLister() {
    super();

    m_Recursive = false;
    m_MaxDepth  = -1;
  }

  /**
   * Sets whether to search recursively.
   *
   * @param value 	true if to search recursively
   */
  public void setRecursive(boolean value) {
    m_Recursive = value;
  }

  /**
   * Returns whether to search recursively.
   *
   * @return 		true if search is recursively
   */
  public boolean getRecursive() {
    return m_Recursive;
  }

  /**
   * Sets the maximum depth to search (1 = only watch dir, -1 = infinite).
   *
   * @param value 	the maximum depth
   */
  public void setMaxDepth(int value) {
    m_MaxDepth = value;
  }

  /**
   * Returns the maximum depth to search (1 = only watch dir, -1 = infinite).
   *
   * @return 		the maximum depth
   */
  public int getMaxDepth() {
    return m_MaxDepth;
  }

  /**
   * A string representation of the object.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    String	result;

    result  = super.toString();
    result += ", ";
    result += "Recursive=" + m_Recursive + ", ";
    result += "MaxDepth=" + m_MaxDepth;

    return result;
  }
}
