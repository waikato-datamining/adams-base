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
 * Placeholders.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.PlaceholdersDefinition;

import java.io.File;
import java.util.Enumeration;

/**
 * A class for accessing the system-wide defined placeholders.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Placeholders {

  /** the filename. */
  public final static String FILENAME = "Placeholders.props";

  /** the placeholder for the current working directory. */
  public final static String CWD = "CWD";

  /** the placeholder for the project directory. */
  public final static String PROJECT = "PROJECT";

  /** the placeholder for the tmp directory. */
  public final static String TMP = "TMP";

  /** the placeholder for the home directory. */
  public final static String HOME = "HOME";

  /** the starting string of a placeholder. */
  public final static String PLACEHOLDER_START = "${";

  /** the ending string of a placeholder. */
  public final static String PLACEHOLDER_END = "}";

  /** the separator for key-value pairs. */
  public final static String SEPARATOR = "=";

  /** the singleton. */
  protected static Placeholders m_Singleton;

  /** the placeholders. */
  protected Properties m_Placeholders;

  /**
   * Initializes the classlister.
   */
  private Placeholders() {
    super();

    initialize();
  }

  /**
   * loads the props file and interpretes it.
   */
  protected void initialize() {
    if (m_Placeholders == null) {
      try {
	m_Placeholders = Environment.getInstance().read(PlaceholdersDefinition.KEY);
      }
      catch (Exception e) {
	m_Placeholders = new Properties();
      }
    }
  }

  /**
   * Returns all stored placeholder keys (local +  global).
   *
   * @return		the placeholder keys (local + global)
   */
  public Enumeration<String> placeholders() {
    return (Enumeration<String>) m_Placeholders.propertyNames();
  }

  /**
   * Checks whether the placeholder exists.
   *
   * @param key		the (uppercase) name of the placeholder to check
   * @return		true if available
   */
  public boolean has(String key) {
    return m_Placeholders.hasKey(key);
  }

  /**
   * Returns the placeholder for the given (uppercase!) key.
   * Trailing path element separators ("/" or "\") are always stripped.
   *
   * @param key		the (uppercase) name of the placeholder to retrieve
   * @return		the value, null if not found
   */
  public String get(String key) {
    String	result;

    result = m_Placeholders.getPath(key);
    if ((result != null) && result.endsWith(File.separator))
      result = result.substring(0, result.length() - 1);

    return result;
  }

  /**
   * Sets the placeholder value under the specified key.
   *
   * @param key		the placeholder key (uppercase!)
   * @param value	the value of the placeholder
   * @return		any previously stored value for the placeholder, null
   * 			if none previously stored
   */
  public synchronized String set(String key, String value) {
    return (String) m_Placeholders.setProperty(key, value);
  }

  /**
   * Removes the placeholder value under the specified key.
   *
   * @param key		the placeholder key (uppercase!) to remove
   * @return		any previously stored value for the placeholder, null
   * 			if none previously stored
   */
  public synchronized String remove(String key) {
    return (String) m_Placeholders.remove(key);
  }

  /**
   * Checks whether all placeholders in the given string can be expanded.
   *
   * @param s		the string to check
   * @return		true if all placeholders could be expanded
   */
  public boolean isValid(String s) {
    s = doExpand(s);
    return (!s.contains(PLACEHOLDER_START));
  }

  /**
   * Expands all placeholders in the given string.
   *
   * @param s		the string to expand
   * @return		the string with the placeholders replaced
   */
  public String expand(String s) {
    String	result;
    
    result = doExpand(s);
    if (result.contains(PLACEHOLDER_START))
      System.err.println("Failed to fully expand '" + s + "': " + result);
    
    return result;
  }

  /**
   * Expands all placeholders in the given string.
   *
   * @param s		the string to expand
   * @return		the string with the placeholders replaced
   */
  protected String doExpand(String s) {
    StringBuilder	result;
    String		curr;
    String		key;
    int			pos;
    boolean		finished;
    String		prevStr;
    String		prevKey;
    boolean		error;
    String		value;

    result  = new StringBuilder();
    curr    = s;
    prevStr = curr;
    prevKey = "";
    error   = false;

    do {
      while ((pos = curr.indexOf(PLACEHOLDER_START)) > -1) {
	result.append(curr.substring(0, pos));
	curr = curr.substring(pos + 2, curr.length());
	pos  = curr.indexOf(PLACEHOLDER_END);
	if (pos > -1) {
	  key = curr.substring(0, pos);
	  // recursive?
	  if (key.equals(prevKey)) {
	    System.err.println("Recursive placeholder '" + key + "' when evaluating '" + s + "'!");
	    result = new StringBuilder(s);
	    error  = true;
	    break;
	  }
	  curr  = curr.substring(pos + 1, curr.length());
	  value = get(key);
	  if (value != null) {
	    result.append(value);
	  }
	  else {
	    result.append(PLACEHOLDER_START + key + PLACEHOLDER_END);
	    System.err.println("Unknown placeholder: " + key);
	  }
	  prevKey = key;
	}
	else {
	  result.append(curr.substring(pos));
	  curr = curr.substring(pos + 1, curr.length());
	}
      }

      if (!error && (curr.length() > 0))
	result.append(curr);

      // any placeholders left?
      finished = (result.indexOf(PLACEHOLDER_START) == -1);
      if (!finished && !error) {
	curr = result.toString();
	// couldn't expand any further? -> exit loop
	if (prevStr.equals(curr)) {
	  result = new StringBuilder(s);
	  error  = true;
	  break;
	}
	result = new StringBuilder();
      }
    }
    while (!finished && !error);

    return result.toString();
  }

  /**
   * Matches all placeholders against the string. The one that shortens the
   * string most, will be used.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  public String collapse(String s) {
    String		result;
    Enumeration<String>	pholders;
    String		key;
    String		value;
    String		bestKey;
    int			bestLeft;
    int			bestKeyLen;
    int			currLeft;
    int			currKeyLen;
    boolean		valid;

    if (s == null)
      return s;
    
    result  = s;
    bestKey = null;
    if ((result.length() > 0) && !result.equals(".")) {
      bestLeft   = result.length();
      bestKeyLen = result.length();
      pholders   = placeholders();
      while (pholders.hasMoreElements()) {
	key   = pholders.nextElement();
	value = get(key);
	if ((value != null) && (result.indexOf(value) == 0)) {
	  valid = false;
          if (result.length() == value.length())
	    valid = true;
	  if ((result.length() > value.length()) && ((result.charAt(value.length()) == '/') || (result.charAt(value.length()) == '\\')))
	    valid = true;
	  if (valid) {
	    currLeft   = result.replace(value, "").length();
	    currKeyLen = key.length();
	    // favor shorter path
	    if (currLeft < bestLeft) {
	      bestKey    = key;
	      bestLeft   = currLeft;
	    }
	    else if (currLeft == bestLeft) {
	      // favor shorter key
	      if (currKeyLen < bestKeyLen) {
		bestKey = key;
		bestKeyLen = currKeyLen;
	      }
	    }
	  }
	}
      }
    }
    else if (result.equals(".")) {
      result = PLACEHOLDER_START + CWD + PLACEHOLDER_END;
    }

    if (bestKey != null)
      result = result.replace(get(bestKey), PLACEHOLDER_START + bestKey + PLACEHOLDER_END);

    return result;
  }

  /**
   * Returns the underlying properties storing the placeholders as simple string.
   *
   * @return		the properties as (simple) string
   */
  @Override
  public String toString() {
    return m_Placeholders.toStringSimple();
  }

  /**
   * Turns the global placeholders into a properties object.
   *
   * @return		the generated properties
   */
  public Properties toProperties() {
    Properties		result;
    Enumeration<String>	enm;
    String		key;

    result = new Properties();

    enm = placeholders();
    while (enm.hasMoreElements()) {
      key = enm.nextElement();
      result.setProperty(key, get(key));
    }

    return result;
  }

  /**
   * Returns the singleton instance of the Placeholders.
   *
   * @return		the singleton
   */
  public static synchronized Placeholders getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new Placeholders();

    return m_Singleton;
  }

  /**
   * Checks whether all placeholders can be expanded.
   *
   * @param s		the string to process
   * @return		true if all placeholders could be expanded
   */
  public static synchronized boolean isValidStr(String s) {
    return getSingleton().isValid(s);
  }

  /**
   * Expands the placeholders in the given string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  public static synchronized String expandStr(String s) {
    return getSingleton().expand(s);
  }

  /**
   * Adds placeholders to the given string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  public static synchronized String collapseStr(String s) {
    return getSingleton().collapse(s);
  }

  /**
   * For outputting placeholders.
   *
   * @param args	the commandline arguments: [-env <environment class>]
   */
  public static void main(String[] args) {
    // set Environment class
    String env = OptionUtils.getOption(args, "-env");
    if (env == null)
      env = Environment.class.getName();
    Class envClass;
    try {
      envClass = Class.forName(env);
    }
    catch (Exception e) {
      envClass = Environment.class;
    }
    Environment.setEnvironmentClass(envClass);

    // output placeholders
    Enumeration<String> enm = getSingleton().placeholders();
    while (enm.hasMoreElements()) {
      String placeholder = PLACEHOLDER_START + enm.nextElement() + PLACEHOLDER_END;
      System.out.println(placeholder + " -> " + getSingleton().expand(placeholder));
    }

    for (String arg: args) {
      System.out.println("--> " + arg);
      System.out.println("  collapsed: " + getSingleton().collapse(arg));
      System.out.println("  expanded: " + getSingleton().expand(arg));
    }
  }
}
