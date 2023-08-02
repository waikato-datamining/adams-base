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
 * Copyright (C) 2009-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.logging.LoggingObject;
import adams.core.management.EnvVar;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.PlaceholdersDefinition;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class for accessing the system-wide defined placeholders.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Placeholders
  extends LoggingObject {

  private static final long serialVersionUID = 2057418242713301148L;

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

  /** the list of placeholders only to expand, but not collapse. */
  public static final String NO_COLLAPSE = "no_collapse";

  /** the environment variable to obtain placeholders from (semi-colon separated placeholder=path pairs). */
  public final static String ADAMS_PLACEHOLDERS = "ADAMS_PLACEHOLDERS";

  /** the singleton. */
  protected static Placeholders m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** the placeholders. */
  protected Map<String,String> m_Placeholders;

  /** the placeholders that are not to be collapsed. */
  protected Set<String> m_NoCollapse;

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
    Enumeration<String>	enm;
    String		key;
    String		env;
    String[]		pairs;
    String[]		parts;

    if (m_Properties == null) {
      try {
	m_Properties = Environment.getInstance().read(PlaceholdersDefinition.KEY);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }

      m_NoCollapse   = new HashSet<>();
      if (m_Properties.hasKey(NO_COLLAPSE) && !m_Properties.getProperty(NO_COLLAPSE).trim().isEmpty())
	m_NoCollapse.addAll(Arrays.asList(m_Properties.getProperty(NO_COLLAPSE).replace(" ", "").split(",")));

      m_Placeholders = new HashMap<>();
      enm            = (Enumeration<String>) m_Properties.propertyNames();
      while (enm.hasMoreElements()) {
        key = enm.nextElement();
        if (key.equals(NO_COLLAPSE))
          continue;
        m_Placeholders.put(key, m_Properties.getPath(key));
      }

      // environment variable
      env = EnvVar.get(ADAMS_PLACEHOLDERS);
      if (env != null) {
        System.out.println("Applying " + ADAMS_PLACEHOLDERS + "...");
        pairs = env.split(";");
        for (String pair: pairs) {
          parts = pair.split("=");
          if (parts.length == 2)
            m_Placeholders.put(parts[0], parts[1]);
          else
            System.err.println("Invalid placeholder=path pair from environment variable: " + pair);
	}
      }

      if (isLoggingEnabled())
        getLogger().info("Placeholders: " + m_Placeholders);
    }
  }

  /**
   * Returns all stored placeholder keys (local +  global).
   *
   * @return		the placeholder keys (local + global)
   */
  public Set<String> placeholders() {
    return m_Placeholders.keySet();
  }

  /**
   * Returns the placeholders that are excluded from collapsing.
   *
   * @return		the placeholder keys
   */
  public Set<String> noCollapse() {
    return m_NoCollapse;
  }

  /**
   * Checks whether the placeholder exists.
   *
   * @param key		the (uppercase) name of the placeholder to check
   * @return		true if available
   */
  public boolean has(String key) {
    return m_Placeholders.containsKey(key);
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

    result = m_Placeholders.get(key);
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
    m_Placeholders.put(key, value);
    return (String) m_Properties.setProperty(key, value);
  }

  /**
   * Removes the placeholder value under the specified key.
   *
   * @param key		the placeholder key (uppercase!) to remove
   * @return		any previously stored value for the placeholder, null
   * 			if none previously stored
   */
  public synchronized String remove(String key) {
    m_Placeholders.remove(key);
    return (String) m_Properties.remove(key);
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

    // the GUI seems to generate these sometimes...
    while (s.startsWith("${CWD}/${CWD}"))
      s = s.substring("${CWD}/".length());
    while (s.startsWith("${CWD}\\${CWD}"))
      s = s.substring("${CWD}\\".length());
    while (s.startsWith("${CWD}/${HOME}"))
      s = s.substring("${CWD}/".length());
    while (s.startsWith("${CWD}\\${HOME}"))
      s = s.substring("${CWD}\\".length());

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
	    result.append(PLACEHOLDER_START).append(key).append(PLACEHOLDER_END);
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
    String		value;
    String		bestKey;
    int			bestLeft;
    int			bestKeyLen;
    int			currLeft;
    int			currKeyLen;
    boolean		valid;

    if (s == null)
      return null;
    
    result  = s;
    bestKey = null;
    if ((result.length() > 0) && !result.equals(".")) {
      bestLeft   = result.length();
      bestKeyLen = result.length();
      for (String key: placeholders()) {
        if (noCollapse().contains(key))
          continue;
	value = get(key);
	if ((value != null) && (result.indexOf(value) == 0)) {
	  valid = false;
          if (result.length() == value.length())
	    valid = true;
	  else if ((result.length() > value.length()) && ((result.charAt(value.length()) == '/') || (result.charAt(value.length()) == '\\')))
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
    return m_Properties.toStringSimple();
  }

  /**
   * Turns the global placeholders into a properties object.
   *
   * @return		the generated properties
   */
  public Properties toProperties() {
    Properties		result;

    result = new Properties();

    for (String key: placeholders())
      result.setProperty(key, get(key));

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
    for (String key: getSingleton().placeholders()) {
      String placeholder = PLACEHOLDER_START + key + PLACEHOLDER_END;
      System.out.println(placeholder + " -> " + getSingleton().expand(placeholder));
    }

    for (String arg: args) {
      System.out.println("--> " + arg);
      System.out.println("  collapsed: " + getSingleton().collapse(arg));
      System.out.println("  expanded: " + getSingleton().expand(arg));
    }
  }
}
