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
 * ClassLister.java
 * Copyright (C) 2007-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.base.BaseRegExp;
import adams.core.logging.ConsoleLoggingObject;
import adams.core.option.OptionUtils;
import adams.env.ClassListerBlacklistDefinition;
import adams.env.ClassListerDefinition;
import adams.env.Environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Determines the classnames of superclasses that are to be displayed in
 * the GUI for instance.
 * <br><br>
 * <b>IMPORTANT NOTE:</b> Due to <a href="http://geekexplains.blogspot.com/2008/07/what-is-reentrant-synchronization-in.html" target="_blank">reentrant threads</a>,
 * the <code>getSingleton()</code> method is not allowed to be called from
 * <code>static {...}</code> blocks in classes that are managed by the
 * ClassLister class (and therefore the ClassLocator class). Since the
 * ClassLocator class loads classes into the JVM, the <code>static {...}</code>
 * block of these classes gets executed and the ClassLister gets initialized
 * once again. In this case, the previous singleton will most likely get
 * overwritten.
 * <br><br>
 * Calling the main method of this class allows listing of classes for
 * all or a specific superclass. Examples:
 * <pre>
 * - List all:
 *   adams.core.ClassLister -allow-empty
 * - List all actors:
 *   adams.core.ClassLister -super adams.flow.core.AbstractActor
 * - List only transformers:
 *   adams.core.ClassLister -super adams.flow.core.AbstractActor -match ".*\.transformer\..*"
 * </pre>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #main(String[])
 */
public class ClassLister
  extends ConsoleLoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = 8482163084925911272L;

  /** the name of the props file. */
  public final static String FILENAME = "ClassLister.props";

  /** the name of the props file. */
  public final static String BLACKLIST = "ClassLister.blacklist";

  /** the properties (superclass/packages). */
  protected Properties m_Packages;

  /** the cache (superclass/classnames). */
  protected HashMap<String,HashSet<String>> m_CacheNames;

  /** the list (superclass/classnames). */
  protected HashMap<String,List<String>> m_ListNames;

  /** the cache (superclass/classes). */
  protected HashMap<String,HashSet<Class>> m_CacheClasses;

  /** the list (superclass/classes). */
  protected HashMap<String,List<Class>> m_ListClasses;

  /** the singleton. */
  protected static ClassLister m_Singleton;

  /**
   * Initializes the classlister.
   */
  private ClassLister() {
    super();
    initialize();
  }

  /**
   * Adds a class hierarchy.
   *
   * @param superclass	the superclass
   * @param packages	the packages
   */
  public void addHierarchy(String superclass, String[] packages) {
    List<String> 	names;
    List<Class>		classes;
    Properties		blacklist;
    String[]		patterns;
    int			i;
    Pattern		p;

    blacklist  = Environment.getInstance().read(ClassListerBlacklistDefinition.KEY);
    names      = ClassLocator.getSingleton().findNames(superclass, packages);
    classes    = ClassLocator.getSingleton().findClasses(superclass, packages);
    // remove blacklisted classes
    if (blacklist.hasKey(superclass)) {
      try {
        patterns = blacklist.getProperty(superclass).replaceAll(" ", "").split(",");
        for (String pattern: patterns) {
          p = Pattern.compile(pattern);
          // names
          i = 0;
          while (i < names.size()) {
            if (p.matcher(names.get(i)).matches())
              names.remove(i);
            else
              i++;
          }
          // classes
          i = 0;
          while (i < classes.size()) {
            if (p.matcher(classes.get(i).getName()).matches())
              classes.remove(i);
            else
              i++;
          }
        }
      }
      catch (Exception ex) {
        getLogger().log(Level.SEVERE, "Failed to blacklist classes for superclass '" +  superclass + "':", ex);
      }
    }
    // create class list
    m_CacheNames.put(superclass, new HashSet<>(names));
    m_ListNames.put(superclass, new ArrayList<>(names));
    m_CacheClasses.put(superclass, new HashSet<>(classes));
    m_ListClasses.put(superclass, new ArrayList<>(classes));
  }

  /**
   * loads the props file and interpretes it.
   */
  protected void initialize() {
    Enumeration		enm;
    String		superclass;
    String[]		packages;

    try {
      m_Packages     = Environment.getInstance().read(ClassListerDefinition.KEY);
      m_CacheNames   = new HashMap<>();
      m_ListNames    = new HashMap<>();
      m_CacheClasses = new HashMap<>();
      m_ListClasses  = new HashMap<>();

      enm = m_Packages.propertyNames();
      while (enm.hasMoreElements()) {
        superclass = (String) enm.nextElement();
        packages   = m_Packages.getProperty(superclass).replaceAll(" ", "").split(",");
        addHierarchy(superclass, packages);
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to determine packages/classes:", e);
      m_Packages = new Properties();
    }
  }

  /**
   * Returns all the classnames that were found for this superclass.
   *
   * @param superclass	the superclass to return the derived classes for
   * @return		the classnames of the derived classes
   */
  public String[] getClassnames(Class superclass) {
    List<String>	list;

    list = m_ListNames.get(superclass.getName());
    if (list == null)
      return new String[0];
    else
      return list.toArray(new String[list.size()]);
  }

  /**
   * Returns all the classs that were found for this superclass.
   *
   * @param superclass	the superclass to return the derived classes for
   * @return		the classes of the derived classes
   */
  public Class[] getClasses(Class superclass) {
    return getClasses(superclass.getName());
  }

  /**
   * Returns all the classes that were found for this superclass.
   *
   * @param superclass	the superclass to return the derived classes for
   * @return		the classes of the derived classes
   */
  public Class[] getClasses(String superclass) {
    List<Class>	list;

    list = m_ListClasses.get(superclass);
    if (list == null)
      return new Class[0];
    else
      return list.toArray(new Class[list.size()]);
  }

  /**
   * Returns the superclasses that the specified classes was listed under.
   *
   * @param cls		the class to look up its superclasses
   * @return		the superclass(es)
   */
  public String[] getSuperclasses(Class cls) {
    return getSuperclasses(cls.getName());
  }

  /**
   * Returns the superclasses that the specified classes was listed under.
   *
   * @param cls		the class to look up its superclasses
   * @return		the superclass(es)
   */
  public String[] getSuperclasses(String cls) {
    List<String>	result;

    result = new ArrayList<>();

    for (String superclass: m_CacheNames.keySet()) {
      if (m_CacheNames.get(superclass).contains(cls))
        result.add(superclass);
    }

    if (result.size() > 1)
      Collections.sort(result);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the all superclasses that define class hierarchies.
   *
   * @return		the superclasses
   */
  public String[] getSuperclasses() {
    List<String>	result;

    result = new ArrayList<>(m_CacheNames.keySet());
    Collections.sort(result);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns all the packages that were found for this superclass.
   *
   * @param superclass	the superclass to return the packages for
   * @return		the packages
   */
  public String[] getPackages(Class superclass) {
    return getPackages(superclass.getName());
  }

  /**
   * Returns all the packages that were found for this superclass.
   *
   * @param superclass	the superclass to return the packages for
   * @return		the packages
   */
  public String[] getPackages(String superclass) {
    String	packages;

    packages = m_Packages.getProperty(superclass);
    if ((packages == null) || (packages.length() == 0))
      return new String[0];
    else
      return packages.split(",");
  }

  /**
   * Returns the superclass-packages relation.
   *
   * @return		the properties object listing the packages
   */
  public Properties getPackages() {
    return m_Packages;
  }

  /**
   * Only prints the generated props file with all the classnames, based on
   * the package names for the individual packages.
   *
   * @return		the props file with the classnames
   */
  @Override
  public String toString() {
    StringBuilder	result;
    List<String>	keys;

    result = new StringBuilder();

    keys = new ArrayList<>(m_ListNames.keySet());
    Collections.sort(keys);
    for (String key: keys) {
      result.append(key).append("\n");
      result.append(Utils.flatten(m_ListNames.get(key), ",")).append("\n\n");
    }

    return result.toString();
  }

  /**
   * Returns the singleton instance of the class lister.
   *
   * @return		the singleton
   */
  public static synchronized ClassLister getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ClassLister();

    return m_Singleton;
  }

  /**
   * Outputs a list of available conversions.
   *
   * @param args	the commandline options: [-env classname] [-super classname] [-match regexp]
   * @throws Exception	if invalid environment class or invalid regular expression
   */
  public static void main(String[] args) throws Exception {
    if (OptionUtils.helpRequested(args)) {
      System.out.println();
      System.out.println("Usage: " + ClassLister.class.getName() + " [-env <classname>] [-super <classname>] [-match <regexp>] [-allow-empty]");
      System.out.println();
      return;
    }

    // environment
    String env = OptionUtils.getOption(args, "-env");
    if (env == null)
      env = Environment.class.getName();
    Class cls = Class.forName(env);
    Environment.setEnvironmentClass(cls);

    // match
    String match = OptionUtils.getOption(args, "-match");
    if (match == null)
      match = BaseRegExp.MATCH_ALL;
    BaseRegExp regexp = new BaseRegExp(match);

    // allow empty class hierarchies?
    boolean allowEmpty = OptionUtils.hasFlag(args, "-allow-empty");

    // superclass
    String[] superclasses;
    String sclass = OptionUtils.getOption(args, "-super");
    if (sclass == null)
      superclasses = getSingleton().getSuperclasses();
    else
      superclasses = new String[]{sclass};

    // list them
    for (String superclass: superclasses) {
      cls = Class.forName(superclass);
      Class[] classes = getSingleton().getClasses(cls);
      if ((classes.length > 0) || allowEmpty) {
        System.out.println("--> " + superclass);
        for (Class c: classes) {
          if (regexp.isMatch(c.getName()))
            System.out.println(c.getName());
        }
        System.out.println();
      }
    }
  }
}
