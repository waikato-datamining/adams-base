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
 * ClassLocator.java
 * Copyright (C) 2005-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.core;

import java.awt.HeadlessException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import adams.core.logging.ConsoleLoggingObject;

/**
 * This class is used for discovering classes that implement a certain
 * interface or a derived from a certain class. Based on the
 * <code>weka.core.ClassDiscovery</code> class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see StringCompare
 * @see weka.core.ClassDiscovery
 */
public class ClassLocator 
  extends ConsoleLoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = 6443115424919701746L;

  /** for caching queries (classname-packagename &lt;-&gt; Vector with classnames). */
  protected Hashtable<String,List<String>> m_Cache;

  /** for caching failed instantiations (classnames). */
  protected HashSet<String> m_BlackListed;

  /** the overall class cache. */
  protected ClassCache m_ClassCache;

  /** the singleton. */
  protected static ClassLocator m_Singleton;

  /**
   * Initializes the class locator.
   */
  protected ClassLocator() {
    super();
    initCache();
  }

  /**
   * Checks the given packages for classes that inherited from the given class,
   * in case it's a class, or implement this class, in case it's an interface.
   *
   * @param classname       the class/interface to look for
   * @param pkgnames        the packages to search in
   * @return                a list with all the found classnames
   */
  public List<String> find(String classname, String[] pkgnames) {
    List<String>	result;
    Class		cls;

    result = new ArrayList<String>();

    try {
      cls    = Class.forName(classname);
      result = find(cls, pkgnames);
    }
    catch (Throwable t) {
      getLogger().log(Level.SEVERE, "Failed to instantiate '" + classname + "'/" + Utils.arrayToString(pkgnames) + " (find):", t);
    }

    return result;
  }

  /**
   * Checks the given packages for classes that inherited from the given class,
   * in case it's a class, or implement this class, in case it's an interface.
   *
   * @param cls             the class/interface to look for
   * @param pkgnames        the packages to search in
   * @return                a list with all the found classnames
   */
  public List<String> find(Class cls, String[] pkgnames) {
    List<String>	result;
    int			i;
    HashSet<String>	names;

    result = new ArrayList<String>();

    names = new HashSet<String>();
    for (i = 0; i < pkgnames.length; i++)
      names.addAll(findInPackage(cls, pkgnames[i]));

    // sort result
    result.addAll(names);
    Collections.sort(result, new StringCompare());

    return result;
  }

  /**
   * Checks the given package for classes that inherited from the given class,
   * in case it's a class, or implement this class, in case it's an interface.
   *
   * @param classname       the class/interface to look for
   * @param pkgname         the package to search in
   * @return                a list with all the found classnames
   */
  public List<String> findInPackage(String classname, String pkgname) {
    List<String>	result;
    Class		cls;

    result = new ArrayList<String>();

    try {
      cls    = Class.forName(classname);
      result = findInPackage(cls, pkgname);
    }
    catch (Throwable t) {
      getLogger().log(Level.SEVERE, "Failed to instantiate '" + classname + "'/" + pkgname + " (findInPackage):", t);
    }

    return result;
  }

  /**
   * Checks the given package for classes that inherited from the given class,
   * in case it's a class, or implement this class, in case it's an interface.
   *
   * @param cls             the class/interface to look for
   * @param pkgname         the package to search in
   * @return                a list with all the found classnames
   */
  public List<String> findInPackage(Class cls, String pkgname) {
    List<String>	result;
    int			i;
    Class		clsNew;

    // already cached?
    result = getCache(cls, pkgname);

    if (result == null) {
      getLogger().info("Searching for '" + cls.getName() + "' in '" + pkgname + "':");

      result = new ArrayList<String>();
      if (m_ClassCache.getClassnames(pkgname) != null)
	result.addAll(m_ClassCache.getClassnames(pkgname));

      // check classes
      i = 0;
      while (i < result.size()) {
	try {
	  // no inner classes
	  if (result.get(i).indexOf('$') > -1) {
	    result.remove(i);
	    continue;
	  }
	  // blacklisted?
	  if (isBlacklisted(result.get(i))) {
	    result.remove(i);
	    continue;
	  }

	  clsNew = Class.forName(result.get(i));
	  // no abstract classes
	  if (Modifier.isAbstract(clsNew.getModifiers())) {
	    m_ClassCache.remove(result.get(i));
	    result.remove(i);
	  }
	  // must implement interface
	  else if ( (cls.isInterface()) && (!hasInterface(cls, clsNew)) ) {
	    result.remove(i);
	  }
	  // must be derived from class
	  else if ( (!cls.isInterface()) && (!isSubclass(cls, clsNew)) ) {
	    result.remove(i);
	  }
	  else {
	    i++;
	  }
	}
	catch (Throwable t) {
	  if ((t.getCause() != null) && (t.getCause() instanceof HeadlessException)) {
	    getLogger().warning("Cannot instantiate '" + result.get(i) + "' in headless environment - skipped.");
	  }
	  else {
	    getLogger().log(Level.SEVERE, "Failed to instantiate '" + result.get(i) + "' (find):", t);
	  }
	  blacklist(result.get(i));
	  result.remove(i);
	}
      }

      // sort result
      Collections.sort(result, new StringCompare());

      // add to cache
      addCache(cls, pkgname, result);
    }

    return result;
  }

  /**
   * Lists all packages it can find in the classpath.
   *
   * @return                a list with all the found packages
   */
  public List<String> findPackages() {
    List<String>	result;
    Enumeration<String>	packages;

    result   = new ArrayList<String>();
    packages = m_ClassCache.packages();
    while (packages.hasMoreElements())
      result.add(packages.nextElement());
    Collections.sort(result, new StringCompare());

    return result;
  }

  /**
   * initializes the cache for the classnames.
   */
  protected void initCache() {
    if (m_Cache == null)
      m_Cache = new Hashtable<String,List<String>>();
    if (m_BlackListed == null)
      m_BlackListed = new HashSet<String>();
    if (m_ClassCache == null)
      m_ClassCache = new ClassCache();
  }

  /**
   * adds the list of classnames to the cache.
   *
   * @param cls		the class to cache the classnames for
   * @param pkgname	the package name the classes were found in
   * @param classnames	the list of classnames to cache
   */
  protected void addCache(Class cls, String pkgname, List<String> classnames) {
    m_Cache.put(cls.getName() + "-" + pkgname, classnames);
  }

  /**
   * returns the list of classnames associated with this class and package, if
   * available, otherwise null.
   *
   * @param cls		the class to get the classnames for
   * @param pkgname	the package name for the classes
   * @return		the classnames if found, otherwise null
   */
  protected List<String> getCache(Class cls, String pkgname) {
    return m_Cache.get(cls.getName() + "-" + pkgname);
  }

  /**
   * Blacklists the given classname.
   *
   * @param classname	the classname to blacklist
   */
  protected void blacklist(String classname) {
    m_BlackListed.add(classname);
  }

  /**
   * Returns whether this classname has been blacklisted.
   *
   * @param classname	the classname to check
   * @return		true if blacklisted
   */
  public boolean isBlacklisted(String classname) {
    return m_BlackListed.contains(classname);
  }

  /**
   * Returns the singleton, instantiates it if necessary.
   * 
   * @return		the singleton
   */
  public static synchronized ClassLocator getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ClassLocator();
    
    return m_Singleton;
  }
  
  /**
   * Checks whether the "otherclass" is a subclass of the given "superclass".
   *
   * @param superclass      the superclass to check against
   * @param otherclass      this class is checked whether it is a subclass
   *                        of the the superclass
   * @return                TRUE if "otherclass" is a true subclass
   */
  public static boolean isSubclass(String superclass, String otherclass) {
    try {
      return isSubclass(Class.forName(superclass), Class.forName(otherclass));
    }
    catch (Throwable t) {
      return false;
    }
  }

  /**
   * Checks whether the "otherclass" is a subclass of the given "superclass".
   *
   * @param superclass      the superclass to check against
   * @param otherclass      this class is checked whether it is a subclass
   *                        of the the superclass
   * @return                TRUE if "otherclass" is a true subclass
   */
  public static boolean isSubclass(Class superclass, Class otherclass) {
    Class       currentclass;
    boolean     result;

    result       = false;
    currentclass = otherclass;
    do {
      result = currentclass.equals(superclass);

      // topmost class reached?
      if (currentclass.equals(Object.class) || (currentclass.getSuperclass() == null))
        break;

      if (!result)
        currentclass = currentclass.getSuperclass();
    }
    while (!result);

    return result;
  }

  /**
   * Checks whether the given class implements the given interface.
   *
   * @param intf      the interface to look for in the given class
   * @param cls       the class to check for the interface
   * @return          TRUE if the class contains the interface
   */
  public static boolean hasInterface(String intf, String cls) {
    try {
      return hasInterface(Class.forName(intf), Class.forName(cls));
    }
    catch (Throwable t) {
      return false;
    }
  }

  /**
   * Checks whether the given class implements the given interface.
   *
   * @param intf      the interface to look for in the given class
   * @param cls       the class to check for the interface
   * @return          TRUE if the class contains the interface
   */
  public static boolean hasInterface(Class intf, Class cls) {
    return intf.isAssignableFrom(cls);
  }
  
  /**
   * Possible calls:
   * <ul>
   *    <li>
   *      adams.core.ClassLocator &lt;packages&gt;<br/>
   *      Prints all the packages in the current classpath
   *    </li>
   *    <li>
   *      adams.core.ClassLocator &lt;classname&gt; &lt;packagename(s)&gt;<br/>
   *      Prints the classes it found.
   *    </li>
   * </ul>
   *
   * @param args	the commandline arguments
   */
  public static void main(String[] args) {
    List<String>	list;
    List<String>	packages;
    int         	i;
    StringTokenizer	tok;

    if ((args.length == 1) && (args[0].equals("packages"))) {
      list = getSingleton().findPackages();
      for (i = 0; i < list.size(); i++)
	System.out.println(list.get(i));
    }
    else if (args.length == 2) {
      // packages
      packages = new ArrayList<String>();
      tok = new StringTokenizer(args[1], ",");
      while (tok.hasMoreTokens())
        packages.add(tok.nextToken());

      // search
      list = getSingleton().find(
  		args[0],
  		packages.toArray(new String[packages.size()]));

      // print result, if any
      System.out.println(
          "Searching for '" + args[0] + "' in '" + args[1] + "':\n"
          + "  " + list.size() + " found.");
      for (i = 0; i < list.size(); i++)
        System.out.println("  " + (i+1) + ". " + list.get(i));
    }
    else {
      System.out.println("\nUsage:");
      System.out.println(
	  ClassLocator.class.getName() + " packages");
      System.out.println("\tlists all packages in the classpath");
      System.out.println(
	  ClassLocator.class.getName() + " <classname> <packagename(s)>");
      System.out.println("\tlists classes derived from/implementing 'classname' that");
      System.out.println("\tcan be found in 'packagename(s)' (comma-separated list)");
      System.out.println();
      System.exit(1);
    }
  }
}
