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
 * ClassCache.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.ClassPathTraversal.TraversalListener;
import adams.core.logging.LoggingObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * A class that stores all classes on the classpath.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassCache
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -2973185784363491578L;

  /**
   * For listening to the class traversal and populating the caches.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Listener
    implements TraversalListener {

    /** for caching all classes on the class path (package-name &lt;-&gt; HashSet with classnames). */
    protected HashMap<String,HashSet<String>> m_NameCache;

    /** for caching all classes on the class path (package-name &lt;-&gt; HashSet with classes). */
    protected HashMap<String,HashSet<Class>> m_ClassCache;

    /**
     * Initializes the listener.
     */
    public Listener() {
      m_NameCache  = new HashMap<>();
      m_ClassCache = new HashMap<>();
    }

    /**
     * Gets called when a class is being traversed.
     *
     * @param classname		the current classname
     * @param classPathPart	the current classpath part this classname is
     *                          located in
     */
    @Override
    public void traversing(String classname, URL classPathPart) {
      String		pkgname;
      HashSet<String>	names;

      // classname and package
      pkgname = ClassPathTraversal.extractPackage(classname);

      // add to cache
      if (!m_NameCache.containsKey(pkgname))
        m_NameCache.put(pkgname, new HashSet<>());
      if (!m_ClassCache.containsKey(pkgname))
        m_ClassCache.put(pkgname, new HashSet<>());
      names = m_NameCache.get(pkgname);

      names.add(classname);
    }

    /**
     * Returns the name cache.
     *
     * @return		the cache
     */
    public HashMap<String,HashSet<String>> getNameCache() {
      return m_NameCache;
    }

    /**
     * Returns the class cache.
     *
     * @return		the cache
     */
    public HashMap<String,HashSet<Class>> getClassCache() {
      return m_ClassCache;
    }
  }

  /** for caching all classes on the class path (package-name &lt;-&gt; HashSet with classnames). */
  protected HashMap<String,HashSet<String>> m_NameCache;

  /** for caching all classes on the class path (package-name &lt;-&gt; HashSet with classes). */
  protected HashMap<String,HashSet<Class>> m_ClassCache;

  /**
   * Initializes the cache.
   */
  public ClassCache() {
    super();
    initialize();
  }

  /**
   * Removes the classname from the cache.
   *
   * @param classname	the classname to remove
   * @return		true if the removal changed the cache
   */
  public boolean remove(String classname) {
    String		pkgname;
    HashSet<String>	names;

    classname = ClassPathTraversal.cleanUp(classname);
    pkgname   = ClassPathTraversal.extractPackage(classname);
    names     = m_NameCache.get(pkgname);
    if (names != null)
      return names.remove(classname);
    else
      return false;
  }

  /**
   * Returns all the stored packages.
   *
   * @return		the package names
   */
  public Iterator<String> packages() {
    return m_NameCache.keySet().iterator();
  }

  /**
   * Returns all the classes for the given package.
   *
   * @param pkgname	the package to get the classes for
   * @return		the classes (sorted by name)
   */
  public HashSet<String> getClassnames(String pkgname) {
    if (m_NameCache.containsKey(pkgname))
      return m_NameCache.get(pkgname);
    else
      return new HashSet<>();
  }

  /**
   * Returns all the classes for the given package.
   *
   * @param pkgname	the package to get the classes for
   * @return		the classes (sorted by name)
   */
  public HashSet<Class> getClasses(String pkgname) {
    if (m_ClassCache.containsKey(pkgname))
      return m_ClassCache.get(pkgname);
    else
      return new HashSet<>();
  }

  /**
   * Initializes the cache.
   */
  protected void initialize() {
    ClassPathTraversal	traversal;
    Listener 		listener;

    traversal = new ClassPathTraversal();
    listener  = new Listener();
    traversal.traverse(listener);

    m_NameCache  = listener.getNameCache();
    m_ClassCache = listener.getClassCache();
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    ClassCache cache = new ClassCache();
    Iterator<String> packages = cache.packages();
    List<String> sorted = new ArrayList<>();
    while (packages.hasNext())
      sorted.add(packages.next());
    Collections.sort(sorted);
    for (String key: sorted)
      System.out.println(key + ": " + cache.getClassnames(key).size());
  }
}
