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
public class OptionTraversalPath {

  /** for serialization. */
  private static final long serialVersionUID = 1386973529868246153L;

  /** the path stack. */
  protected Stack<String> m_Paths;

  /** the object stack. */
  protected Stack<Object> m_Objects;

  public OptionTraversalPath() {
    m_Paths   = new Stack<>();
    m_Objects = new Stack<>();
  }

  /**
   * Pushes the path and the associated object onto the stack.
   *
   * @param path	the path to add
   * @param obj		the associated object
   */
  public synchronized void push(String path, Object obj) {
    m_Paths.push(path);
    m_Objects.push(obj);
  }

  /**
   * Pops current path and object of the stack.
   */
  public synchronized void pop() {
    m_Paths.pop();
    m_Objects.pop();
  }

  /**
   * Returns the size of the stack.
   *
   * @return		the number of elements on the stack
   */
  public synchronized int size() {
    return m_Paths.size();
  }

  /**
   * Returns whether the stack is empty.
   *
   * @return		true if empty
   */
  public synchronized boolean empty() {
    return m_Paths.empty();
  }

  /**
   * Returns the path at the specified index.
   *
   * @param index	the index of the path element
   * @return		the path element
   */
  public synchronized String getPath(int index) {
    return m_Paths.get(index);
  }

  /**
   * Returns the object at the specified index.
   *
   * @param index	the index of the object element
   * @return		the object element
   */
  public synchronized Object getObject(int index) {
    return m_Objects.get(index);
  }

  /**
   * Returns the path at the top.
   *
   * @return		the path element
   */
  public synchronized String peekPath() {
    return m_Paths.peek();
  }

  /**
   * Returns the object at the top.
   *
   * @return		the object element
   */
  public synchronized Object peekObject() {
    return m_Objects.peek();
  }

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
      result.append(getPath(i).replace(".", "\\."));
    }
    
    return result.toString();
  }

  /**
   * Just returns the path.
   *
   * @return		the path
   */
  public String toString() {
    return getPath();
  }
}
