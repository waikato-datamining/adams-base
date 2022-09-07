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
 * Copyright (C) 2007-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.env.ClassListerBlacklistDefinition;
import adams.env.ClassListerDefinition;
import adams.env.ClasspathBlacklistDefinition;
import adams.env.Environment;
import adams.flow.core.Compatibility;
import adams.gui.goe.AbstractEditorRegistration;
import nz.ac.waikato.cms.locator.ClassCache;
import nz.ac.waikato.cms.locator.ClassLocator;
import nz.ac.waikato.cms.locator.ClassPathTraversal;
import nz.ac.waikato.cms.locator.ClassTraversalWithBlacklister;
import nz.ac.waikato.cms.locator.PropertiesBasedClassListTraversal;
import nz.ac.waikato.cms.locator.blacklisting.SimpleBlacklister;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
 *   adams.core.ClassLister -super adams.flow.core.Actor
 * - List only transformers:
 *   adams.core.ClassLister -super adams.flow.core.Actor -match ".*\.transformer\..*"
 * - List only actors from the adams-compress module:
 *   adams.core.ClassLister -super adams.flow.core.Actor -filter-by-module adams-compress"
 * </pre>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see #main(String[])
 */
public class ClassLister
  extends nz.ac.waikato.cms.locator.ClassLister {

  /** for serialization. */
  private static final long serialVersionUID = 8482163084925911272L;

  /** the name of the props file. */
  public final static String FILENAME = "ClassLister.props";

  /** the name of the props file. */
  public final static String BLACKLIST = "ClassLister.blacklist";

  /** the name of the classpath blacklist props file. */
  public final static String CLASSPATH_BLACKLIST = "ClasspathBlacklist.props";

  /** for statically listed classes (superclass -> comma-separated classnames). */
  public static final String CLASSLISTER_CLASSES = "ClassLister.classes";

  /** for statically listed packages (superclass -> comma-separated packages). */
  public static final String CLASSLISTER_PACKAGES = "ClassLister.packages";

  /** the singleton. */
  protected static ClassLister m_Singleton;

  /** whether static or dynamic discovery is used. */
  protected boolean m_Static = false;

  /**
   * Initializes the classlister.
   */
  protected ClassLister() {
    super(new ClassPathTraversal());

    long start = System.currentTimeMillis();

    // TODO
    if (m_ClassTraversal instanceof ClassTraversalWithBlacklister) {
      Properties props = Environment.getInstance().read(ClasspathBlacklistDefinition.KEY);
      SimpleBlacklister blacklister = new SimpleBlacklister();
      String[] parts;
      // dirs
      parts = props.getProperty("Directories", "").split(",");
      for (String part: parts)
        blacklister.blacklistDir(new File(part.trim()));
      // files
      parts = props.getProperty("Files", "").split(",");
      for (String part: parts)
        blacklister.blacklistFile(part.trim());
      // patterns
      parts = props.getProperty("FilePatterns", "").split(",");
      for (String part: parts)
        blacklister.blacklistFilePattern(part.trim());
      ((ClassTraversalWithBlacklister) m_ClassTraversal).setBlacklister(blacklister);
    }

    // static?
    InputStream classes = null;
    InputStream packages = null;
    try {
      classes = getClass().getClassLoader().getResourceAsStream(CLASSLISTER_CLASSES);
      packages = getClass().getClassLoader().getResourceAsStream(CLASSLISTER_PACKAGES);
      if ((classes != null) && (packages != null)) {
        Properties propsClasses = new Properties();
        propsClasses.load(classes);
        Properties propsPackages = new Properties();
        propsPackages.load(packages);
        if ((propsClasses.size() > 0) && (propsPackages.size() > 0)) {
	  m_ClassTraversal = new PropertiesBasedClassListTraversal(propsClasses);
	  setPackages(propsPackages);
	  setBlacklist(new Properties());
	  System.out.println(getClass().getName() + ": Using statically defined classes/packages");
	  m_Static = true;
	}
      }
    }
    catch (Exception e) {
      // ignored
    }
    finally {
      FileUtils.closeQuietly(classes);
      FileUtils.closeQuietly(packages);
    }

    if (!m_Static) {
      System.out.println(getClass().getName() + ": Using dynamic class discovery");
      setPackages(Environment.getInstance().read(ClassListerDefinition.KEY));
      setBlacklist(Environment.getInstance().read(ClassListerBlacklistDefinition.KEY));
    }

    initialize();

    long end = System.currentTimeMillis();
    System.out.println(getClass().getName() + ": Time taken to initialize classes " + (end - start) + " msec");
  }

  /**
   * Returns whether static or dynamic discivery is being used.
   *
   * @return		true if static
   */
  public boolean isStatic() {
    return m_Static;
  }

  /**
   * Returns all the classes of the specified superclass (abstract class or
   * interface), but restricts it further to the specified class.
   *
   * @param superclass	abstract class or interface to get classes for
   * @param restriction	the interface that the classes must implement
   * @return		the class subset
   */
  public Class[] getClasses(Class superclass, Class restriction) {
    return getClasses(superclass, new Class[]{restriction});
  }

  /**
   * Returns all the classes of the specified superclass (abstract class or
   * interface), but restricts it further to the specified classes.
   *
   * @param superclass	abstract class or interface to get classes for
   * @param restriction	the interfaces that the classes must implement
   * @return		the class subset
   */
  public Class[] getClasses(Class superclass, Class[] restriction) {
    List<Class> 	result;
    Class[]		classes;
    Compatibility	comp;

    result  = new ArrayList<>();
    classes = getClasses(superclass);
    comp    = new Compatibility();
    for (Class cls: classes) {
      if (comp.isCompatible(new Class[]{cls}, restriction))
        result.add(cls);
    }

    return result.toArray(new Class[0]);
  }

  /**
   * For returning a list of all classes.
   *
   * @param managed	whether to restrict to managed classes
   * @return		the list of class names
   */
  public List<String> getAllClassnames(boolean managed) {
    return getAllClassnames(managed, null);
  }

  /**
   * Matches the class against the filters (interfaces, superclasses).
   *
   * @param cls		the class to match
   * @param filters	the interfaces/superclasses to match, null if no filtering
   * @return		true if a match
   */
  protected boolean matches(String cls, Class[] filters) {
    if (filters == null)
      return true;
    for (Class filter: filters) {
      if (!ClassLocator.matches(filter.getName(), cls))
        return false;
    }
    return true;
  }

  /**
   * Matches the class against the filters (interfaces, superclasses).
   *
   * @param cls		the class to match
   * @param filters	the interfaces/superclasses to match, null if no filtering
   * @return		true if a match
   */
  protected boolean matches(Class cls, Class[] filters) {
    if (filters == null)
      return true;
    for (Class filter: filters) {
      if (!ClassLocator.matches(filter, cls))
        return false;
    }
    return true;
  }

  /**
   * For returning a list of all classes.
   *
   * @param managed	whether to restrict to managed classes
   * @param filters 	for filtering the classes (interfaces/superclasses to match again), null to ignore
   * @return		the list of class names
   */
  public List<String> getAllClassnames(boolean managed, Class[] filters) {
    List<String> 	result;
    int			i;
    String		name;
    String		pkg;
    Iterator<String> 	iter;
    ClassCache		cache;

    result = new ArrayList<>();
    if (!managed) {
      // all classes
      cache = ClassLocator.getSingleton().getCache();
      iter  = cache.packages();
      while (iter.hasNext()) {
        pkg = iter.next();
	for (String cls: cache.getClassnames(pkg)) {
	  if (cache.isAnonymous(cls))
	    continue;
	  if (matches(cls, filters))
	    result.add(cls);
	}
      }
    }
    else {
      // only managed classes
      for (String supercls : ClassLister.getSingleton().getSuperclasses()) {
	for (Class cls : ClassLister.getSingleton().getClasses(supercls)) {
	  if (matches(cls, filters))
	    result.add(cls.getName());
	}
      }
    }
    Collections.sort(result);
    i = 0;
    name = "";
    while (i < result.size()) {
      if (!name.equals(result.get(i))) {
	name = result.get(i);
	i++;
      }
      else {
	result.remove(i);
      }
    }

    return result;
  }

  /**
   * Filters the classes using the ADAMS module name, returning only ones
   * that are from this module.
   *
   * @param classes 	the classes to filter
   * @param module	the name of the ADAMS module
   * @return		the filtered classes
   */
  public Class[] filterByModule(Class[] classes, String module) {
    List<Class>		result;
    Iterator<URL> 	parts;
    Set<String>		all;

    result = new ArrayList<>();
    parts  = ClassLocator.getSingleton().getCache().classpathParts(".*\\W" + module + "\\W.*");
    all    = new HashSet<>();
    while (parts.hasNext())
      all.addAll(ClassLocator.getSingleton().getCache().getClassnames(parts.next()));
    for (Class cls: classes) {
      if (all.contains(cls.getName()))
        result.add(cls);
    }

    return result.toArray(new Class[0]);
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
      System.out.println("Usage: " + ClassLister.class.getName() + " [-env <classname>] [-action <search|classes|packages>]");
      System.out.println();
      System.out.println("'search' action (default):");
      System.out.println("allows searching for classes");
      System.out.println("[-super <classname>] [-match <regexp>] [-allow-empty] [-filter-by-module <module>]");
      System.out.println();
      System.out.println("'classes' action:");
      System.out.println("for outputting the class hierarchies as properties file");
      System.out.println("each key is a superclass, the corresponding value a comma-separated list of class names");
      System.out.println("-output <props_file>");
      System.out.println();
      System.out.println("'packages' action:");
      System.out.println("for outputting the packages of the class hierarchies as properties file");
      System.out.println("each key is a superclass, the corresponding value a comma-separated list of package names");
      System.out.println("-output <props_file>");
      System.out.println();
      return;
    }

    // environment
    String env = OptionUtils.getOption(args, "-env");
    if (env == null)
      env = Environment.class.getName();
    Class cls = Class.forName(env);
    Environment.setEnvironmentClass(cls);

    // register editors
    AbstractEditorRegistration.registerEditors();

    // action
    String action = OptionUtils.getOption(args, "-action");
    if (action == null)
      action = "search";
    switch (action) {
      case "search":
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

        // filter-by-module
        String module = OptionUtils.getOption(args, "-filter-by-module");

        // list them
        for (String superclass : superclasses) {
          cls = Class.forName(superclass);
          Class[] classes = getSingleton().getClasses(cls);
          if (module != null)
            classes = getSingleton().filterByModule(classes, module);
          if ((classes.length > 0) || allowEmpty) {
            System.out.println("--> " + superclass);
            for (Class c : classes) {
              if (regexp.isMatch(c.getName()))
                System.out.println(c.getName());
            }
            System.out.println();
          }
        }
        break;

      case "classes":
        PlaceholderFile class_props_file = new PlaceholderFile(OptionUtils.getOption(args, "-output"));
        File class_props_dir = class_props_file.getParentFile();
        if (!class_props_dir.exists()) {
          if (!class_props_dir.mkdirs()) {
            System.err.println("Failed to create directory for classes file: " + class_props_dir);
            System.exit(2);
          }
        }
        Properties class_props = new Properties(getSingleton().toProperties());
        if (!class_props.save(class_props_file.getAbsolutePath())) {
	  System.err.println("Failed to write properties with classes to: " + class_props_file);
	  System.exit(1);
	}
        break;

      case "packages":
        PlaceholderFile pkgs_props_file = new PlaceholderFile(OptionUtils.getOption(args, "-output"));
        File pkgs_props_dir = pkgs_props_file.getParentFile();
        if (!pkgs_props_dir.exists()) {
          if (!pkgs_props_dir.mkdirs()) {
            System.err.println("Failed to create directory for packages file: " + pkgs_props_dir);
            System.exit(2);
          }
        }
        Properties pkgs_props = new Properties(getSingleton().toPackages());
        if (!pkgs_props.save(pkgs_props_file.getAbsolutePath())) {
	  System.err.println("Failed to write properties with packages to: " + pkgs_props_file);
	  System.exit(1);
	}
        break;

      default:
        throw new IllegalArgumentException("Unknown action: " + action);
    }
  }
}
