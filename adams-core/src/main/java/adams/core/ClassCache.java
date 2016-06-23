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

import adams.core.logging.LoggingObject;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

/**
 * A singleton that stores all classes on the classpath.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassCache 
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -2973185784363491578L;

  /**
   * For filtering classes.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ClassFileFilter
    implements FileFilter {

    /**
     * Checks whether the file is a class.
     *
     * @param pathname	the file to check
     * @return		true if a class file
     */
    public boolean accept(File pathname) {
      return pathname.getName().endsWith(".class");
    }
  }

  /**
   * For filtering classes.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DirectoryFilter
    implements FileFilter {

    /**
     * Checks whether the file is a directory.
     *
     * @param pathname	the file to check
     * @return		true if a directory
     */
    public boolean accept(File pathname) {
      return pathname.isDirectory();
    }
  }

  /** the key for the default package. */
  public final static String DEFAULT_PACKAGE = "DEFAULT";

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
   * Fixes the classname, turns "/" and "\" into "." and removes ".class".
   *
   * @param classname	the classname to process
   * @return		the processed classname
   */
  protected String cleanUp(String classname) {
    String	result;

    result = classname;

    if (result.contains("/"))
      result = result.replace("/", ".");
    if (result.contains("\\"))
      result = result.replace("\\", ".");
    if (result.endsWith(".class"))
      result = result.substring(0, result.length() - 6);

    return result;
  }

  /**
   * Extracts the package name from the (clean) classname.
   *
   * @param classname	the classname to extract the package from
   * @return		the package name
   */
  protected String extractPackage(String classname) {
    if (classname.contains("."))
      return classname.substring(0, classname.lastIndexOf("."));
    else
      return DEFAULT_PACKAGE;
  }

  /**
   * Adds the classname to the cache.
   *
   * @param classname	the classname, automatically removes ".class" and
   * 			turns "/" or "\" into "."
   * @return		true if adding changed the cache
   */
  public boolean add(String classname) {
    String		pkgname;
    HashSet<String>	names;

    // classname and package
    classname = cleanUp(classname);
    pkgname   = extractPackage(classname);
    
    // add to cache
    if (!m_NameCache.containsKey(pkgname))
      m_NameCache.put(pkgname, new HashSet<>());
    if (!m_ClassCache.containsKey(pkgname))
      m_ClassCache.put(pkgname, new HashSet<>());
    names = m_NameCache.get(pkgname);

    return names.add(classname);
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

    classname = cleanUp(classname);
    pkgname   = extractPackage(classname);
    names     = m_NameCache.get(pkgname);
    if (names != null)
      return names.remove(classname);
    else
      return false;
  }

  /**
   * Fills the class cache with classes in the specified directory.
   *
   * @param prefix	the package prefix so far, null for default package
   * @param dir		the directory to search
   */
  protected void initFromDir(String prefix, File dir) {
    File[]	files;

    // check classes
    files = dir.listFiles(new ClassFileFilter());
    for (File file: files) {
      if (prefix == null)
	add(file.getName());
      else
	add(prefix + "." + file.getName());
    }

    // descend in directories
    files = dir.listFiles(new DirectoryFilter());
    for (File file: files) {
      if (prefix == null)
	initFromDir(file.getName(), file);
      else
	initFromDir(prefix + "." + file.getName(), file);
    }
  }

  /**
   * Fills the class cache with classes in the specified directory.
   *
   * @param dir		the directory to search
   */
  protected void initFromDir(File dir) {
    if (isLoggingEnabled())
      getLogger().log(Level.INFO, "Analyzing directory: " + dir);
    initFromDir(null, dir);
  }

  /**
   * Analyzes the MANIFEST.MF file of a jar whether additional jars are
   * listed in the "Class-Path" key.
   * 
   * @param manifest	the manifest to analyze
   */
  protected void initFromManifest(Manifest manifest) {
    Attributes	atts;
    String	cp;
    String[]	parts;

    if (manifest == null)
      return;

    atts = manifest.getMainAttributes();
    cp   = atts.getValue("Class-Path");
    if (cp == null)
      return;
    
    parts = cp.split(" ");
    for (String part: parts) {
      if (part.trim().length() == 0)
	return;
      if (part.toLowerCase().endsWith(".jar") || !part.equals("."))
        initFromClasspathPart(part);
    }
  }
  
  /**
   * Fills the class cache with classes from the specified jar.
   *
   * @param file		the jar to inspect
   */
  protected void initFromJar(File file) {
    JarFile		jar;
    JarEntry		entry;
    Enumeration		enm;

    if (isLoggingEnabled())
      getLogger().log(Level.INFO, "Analyzing jar: " + file);

    if (!file.exists()) {
      getLogger().log(Level.WARNING, "Jar does not exist: " + file);
      return;
    }
    
    try {
      jar = new JarFile(file);
      enm = jar.entries();
      while (enm.hasMoreElements()) {
        entry = (JarEntry) enm.nextElement();
        if (entry.getName().endsWith(".class"))
          add(entry.getName());
      }
      initFromManifest(jar.getManifest());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to inspect: " + file, e);
    }
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
   * Analyzes a part of the classpath.
   * 
   * @param part	the part to analyze
   */
  protected void initFromClasspathPart(String part) {
    File		file;

    file = null;
    if (part.startsWith("file:")) {
      part = part.replace(" ", "%20");
      try {
	file = new File(new java.net.URI(part));
      }
      catch (URISyntaxException e) {
	getLogger().log(Level.SEVERE, "Failed to generate URI: " + part, e);
      }
    }
    else {
      file = new File(part);
    }
    if (file == null) {
      if (isLoggingEnabled())
	getLogger().log(Level.INFO, "Skipping: " + part);
      return;
    }

    // find classes
    if (file.isDirectory())
      initFromDir(file);
    else if (file.exists())
      initFromJar(file);
  }
  
  /**
   * Initializes the cache.
   */
  protected void initialize() {
    String		part;
    URLClassLoader 	sysLoader;
    URL[] 		urls;

    m_NameCache  = new HashMap<>();
    m_ClassCache = new HashMap<>();
    sysLoader = (URLClassLoader) getClass().getClassLoader();
    urls      = sysLoader.getURLs();
    for (URL url: urls) {
      if (isLoggingEnabled())
	getLogger().log(Level.INFO, "Classpath URL: " + url);
      part = url.toString();
      initFromClasspathPart(part);
    }
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    ClassCache cache = new ClassCache();
    Iterator<String> packages = cache.packages();
    while (packages.hasNext()) {
      String key = packages.next();
      System.out.println(key + ": " + cache.getClassnames(key).size());
    }
  }
}
