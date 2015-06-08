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
 * ActorPath.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Breaks up a string denoting a full name of an actor into the individual
 * path elements.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorPath
  implements Comparable<ActorPath>, Serializable {

  private static final long serialVersionUID = -5734626103557090578L;

  /** the elements of the path. */
  protected String[] m_Parts;

  /** the full path. */
  protected String m_FullPath;

  /**
   * Initializes the path.
   *
   * @param path	the path to break up
   */
  public ActorPath(String path) {
    int		i;

    m_FullPath = null;
    if ((path == null) || (path.length() == 0)) {
      m_Parts = new String[0];
    }
    else {
      // mask escaped "." in names
      path = path.replace("\\.", "\t");
      // remove surrounding "[]"
      if (path.startsWith("[") && path.endsWith("]"))
	path = path.substring(1, path.length() - 1);
      // remove the directors from the path
      if (path.matches(".*\\.[\\w]*Director$"))
	path = path.replaceAll("\\.[\\w]*Director$", "");
      // remove any trailing text after "/" (incl)
      if (path.indexOf('/') > -1)
	path = path.replaceAll("\\/[0-9]+-(OUT|ERR|DEBUG).*", "");
      // remove any trailing white spaces
      path = path.replaceAll("\\s*$", "");

      m_Parts = path.split("\\.");
      for (i = 0; i < m_Parts.length; i++)
	m_Parts[i] = m_Parts[i].replace("\t", ".");
    }
  }

  /**
   * Initializes the path with the specified elements.
   *
   * @param path	the path elements to use
   */
  public ActorPath(String[] path) {
    m_Parts    = path.clone();
    m_FullPath = null;
  }

  /**
   * Returns a clone of the path elements.
   *
   * @return		the elements of the path
   */
  public String[] getPath() {
    return m_Parts.clone();
  }

  /**
   * Returns the number of path elements this path is made of.
   *
   * @return		the number of path elements
   */
  public int getPathCount() {
    return m_Parts.length;
  }

  /**
   * Returns the specified element of the path.
   *
   * @param element	the index of the element to retrieve
   * @return		the specified path element
   */
  public String getPathComponent(int element) {
    return m_Parts[element];
  }

  /**
   * Returns the path without the last element.
   *
   * @return		the new path
   */
  public ActorPath getParentPath() {
    String[]	parts;
    int		i;

    if (m_Parts.length >= 1) {
      parts = new String[m_Parts.length - 1];
      for (i = 0; i < parts.length; i++)
	parts[i] = new String(m_Parts[i]);
    }
    else {
      parts = new String[0];
    }

    return new ActorPath(parts);
  }

  /**
   * Returns the path without the first element.
   *
   * @return		the new path
   */
  public ActorPath getChildPath() {
    String[]	parts;
    int		i;

    if (m_Parts.length >= 1) {
      parts = new String[m_Parts.length - 1];
      for (i = 1; i < m_Parts.length; i++)
	parts[i - 1] = new String(m_Parts[i]);
    }
    else {
      parts = new String[0];
    }

    return new ActorPath(parts);
  }

  /**
   * Returns the last path component, if available.
   *
   * @return		the last component, null if no path elements stored
   */
  public String getLastPathComponent() {
    if (m_Parts.length > 0)
      return new String(m_Parts[m_Parts.length - 1]);
    else
      return null;
  }

  /**
   * Returns the first path component, if available.
   *
   * @return		the first component, null if no path elements stored
   */
  public String getFirstPathComponent() {
    if (m_Parts.length > 0)
      return new String(m_Parts[0]);
    else
      return null;
  }

  /**
   * Checks whether the specified actor path is a descendant of this
   * actor path object. A path is always a descendant of itself.
   * [a, b, c] is a descendant of [a, b] and so is [a, b], but not [a].
   *
   * @param actorPath	the path to check whether it is a descendant
   * @return		true if a descendant
   */
  public boolean isDescendant(ActorPath actorPath) {
    boolean	result;
    int		i;

    result = (actorPath.getPathCount() >= getPathCount());
    if (result) {
      for (i = 0; i < getPathCount(); i++) {
	if (!getPathComponent(i).equals(actorPath.getPathComponent(i))) {
	  result = false;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Returns the path that denotes the common ancestor of this and the
   * specified actor path.
   *
   * @param actorPath	the actor path to get the common ancestor for
   * @return		the common ancestor (can have length 0!)
   */
  public ActorPath getCommonAncestor(ActorPath actorPath) {
    ArrayList<String>	parts;
    int			i;

    parts = new ArrayList<String>();

    for (i = 0; (i < getPathCount()) && (i < actorPath.getPathCount()); i++) {
      if (getPathComponent(i).equals(actorPath.getPathComponent(i)))
	parts.add(new String(getPathComponent(i)));
      else
	break;
    }

    return new ActorPath(parts.toArray(new String[parts.size()]));
  }
  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(ActorPath o) {
    int		result;
    int		i;

    // special case if one of the paths has no elements
    if ((getPathCount() == 0) || (o.getPathCount() == 0))
      return new Integer(getPathCount()).compareTo(o.getPathCount());

    result = 0;
    for (i = 0; (i < getPathCount()) && (i < o.getPathCount()); i++) {
      result = getPathComponent(i).compareTo(o.getPathComponent(i));
      if ((result == 0) && ((i == getPathCount() - 1) || (i == o.getPathCount() - 1)))
	result = new Integer(getPathCount()).compareTo(o.getPathCount());
      if (result != 0)
	break;
    }

    return result;
  }

  /**
   * Checks whether the provided object is the same as this one.
   *
   * @return		true if the object is an ActorPath and represents
   * 			the same path elements
   */
  public boolean equals(Object o) {
    if (o instanceof ActorPath)
      return (compareTo((ActorPath) o) == 0);
    else
      return false;
  }

  /**
   * Returns the hashcode of the underlying array.
   *
   * @return		the hashcode
   */
  public int hashCode() {
    return m_Parts.hashCode();
  }

  /**
   * Returns the path as a single string.
   *
   * @return		the complete path
   */
  public String toString() {
    StringBuilder path;

    if (m_FullPath == null) {
      path = new StringBuilder();

      for (String part : m_Parts) {
	if (path.length() > 0)
	  path.append(".");
	path.append(part.replace(".", "\\."));
      }
      m_FullPath = path.toString();
    }

    return m_FullPath;
  }
}
