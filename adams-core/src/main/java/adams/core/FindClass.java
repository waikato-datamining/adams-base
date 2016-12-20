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
 * FindClass.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.ClassCache.ClassFileFilter;
import adams.core.ClassCache.DirectoryFilter;
import adams.core.logging.LoggingObject;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

/**
 * For locating classes on the classpath.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FindClass
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -2973185784363491578L;

  /** the key for the default package. */
  public final static String DEFAULT_PACKAGE = "DEFAULT";

  /** the search string. */
  protected String m_Search;

  /** whether search is a regular expression. */
  protected boolean m_RegExp;

  /** the URLs that matched the search. */
  protected List<URL> m_Matches;

  /** the current URL. */
  protected URL m_CurrentURL;

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
   * Checks the classname against the search.
   *
   * @param classname	the classname, automatically removes ".class" and
   * 			turns "/" or "\" into "."
   */
  protected void check(String classname) {
    boolean	match;

    classname = cleanUp(classname);

    if (m_RegExp)
      match = (classname.matches(m_Search));
    else
      match = (classname.equals(m_Search));

    if (match) {
      if (!m_Matches.contains(m_CurrentURL))
	m_Matches.add(m_CurrentURL);
    }
  }

  /**
   * Fills the class cache with classes in the specified directory.
   *
   * @param prefix	the package prefix so far, null for default package
   * @param dir		the directory to search
   */
  protected void searchDir(String prefix, File dir) {
    File[]	files;

    // check classes
    files = dir.listFiles(new ClassFileFilter());
    for (File file: files) {
      if (prefix == null)
	check(file.getName());
      else
	check(prefix + "." + file.getName());
    }

    // descend in directories
    files = dir.listFiles(new DirectoryFilter());
    for (File file: files) {
      if (prefix == null)
	searchDir(file.getName(), file);
      else
	searchDir(prefix + "." + file.getName(), file);
    }
  }

  /**
   * Fills the class cache with classes in the specified directory.
   *
   * @param dir		the directory to search
   */
  protected void searchDir(File dir) {
    if (isLoggingEnabled())
      getLogger().log(Level.INFO, "Analyzing directory: " + dir);
    searchDir(null, dir);
  }

  /**
   * Analyzes the MANIFEST.MF file of a jar whether additional jars are
   * listed in the "Class-Path" key.
   *
   * @param manifest	the manifest to analyze
   */
  protected void searchManifest(Manifest manifest) {
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
	searchClasspathPart(part);
    }
  }

  /**
   * Fills the class cache with classes from the specified jar.
   *
   * @param file		the jar to inspect
   */
  protected void searchJar(File file) {
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
	  check(entry.getName());
      }
      searchManifest(jar.getManifest());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to inspect: " + file, e);
    }
  }

  /**
   * Analyzes a part of the classpath.
   *
   * @param part	the part to analyze
   */
  protected void searchClasspathPart(String part) {
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
      searchDir(file);
    else if (file.exists())
      searchJar(file);
  }

  /**
   * Searches for a classname.
   *
   * @param search 	the search string to find
   * @param regExp	true if the search string is a regular expression
   * @return		the matching URLs
   */
  public List<URL> search(String search, boolean regExp) {
    String		part;
    URLClassLoader 	sysLoader;
    URL[] 		urls;

    m_Matches = new ArrayList<>();
    m_Search  = search;
    m_RegExp  = regExp;
    sysLoader = (URLClassLoader) getClass().getClassLoader();
    urls      = sysLoader.getURLs();
    for (URL url: urls) {
      m_CurrentURL = url;
      if (isLoggingEnabled())
	getLogger().log(Level.INFO, "Classpath URL: " + url);
      part = url.toString();
      searchClasspathPart(part);
    }

    return m_Matches;
  }

  /**
   * For testing only.
   * <br>
   * Parameters: 'classname' 'regexp (true|false)'
   *
   * @param args	the commandline args
   */
  public static void main(String[] args) {
    FindClass find = new FindClass();
    List<URL> urls = find.search(args[0], Boolean.parseBoolean(args[1]));
    for (URL url: urls)
      System.out.println(url);
  }
}
