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
 * EnvVar.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.management;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for environment variables. Uses {@link System#getenv(String)} under the hood.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class EnvVar {

  /** the default list separator (;). */
  public final static String SEPARATOR_LIST = ";";

  /** the default pair separator (=). */
  public final static String SEPARATOR_PAIR = "=";

  /**
   * Returns the current mapping of environment variables.
   *
   * @return		the mapping
   * @see		System#getenv()
   */
  public static Map<String,String> get() {
    return System.getenv();
  }

  /**
   * Returns whether the specified environment variable is present.
   *
   * @param name	the env var to check
   * @return		true if present
   * @see		System#getenv(String)
   */
  public static boolean has(String name) {
    return (name != null) && (System.getenv(name) != null);
  }

  /**
   * Generates the actual environment variable name.
   *
   * @param name		the input name
   * @param dotsToUnderscores 	whether to convert underscores with dots
   * @param checkUppercase 	whether to convert to uppercase
   * @return			the converted name
   */
  public static String convert(String name, boolean dotsToUnderscores, boolean checkUppercase) {
    String	result;

    if (dotsToUnderscores && checkUppercase)
      result = name.replace(".", "_").toUpperCase();
    else if (dotsToUnderscores)
      result = name.replace(".", "_");
    else if (checkUppercase)
      result = name.toUpperCase();
    else
      result = name;

    return result;
  }

  /**
   * Locates the actual environment variable.
   *
   * @param name	the env var to retrieve
   * @param dotsToUnderscores 	whether to check presence of name with underscores replacing dots
   * @param checkUppercase 	whether to check presence of uppercased name
   * @return		the actual variable name, null if not present
   * @see		System#getenv(String)
   */
  public static String find(String name, boolean dotsToUnderscores, boolean checkUppercase) {
    String	result;
    String	newName;

    result = null;

    if (has(name))
      result = name;

    if ((result == null) && dotsToUnderscores && checkUppercase) {
      newName = convert(name, true, true);
      if (has(newName))
	result = newName;
    }

    if ((result == null) && dotsToUnderscores && !checkUppercase) {
      newName = convert(name, true, false);
      if (has(newName))
	result = newName;
    }

    if ((result == null) && !dotsToUnderscores && checkUppercase) {
      newName = convert(name, false, true);
      if (has(newName))
	result = newName;
    }

    return result;
  }

  /**
   * Returns the value of the specified environment variable.
   *
   * @param name	the env var to retrieve
   * @return		the associated value or null if not present
   * @see		System#getenv(String)
   */
  public static String get(String name) {
    return get(name, null);
  }

  /**
   * Returns the value of the specified environment variable.
   *
   * @param name	the env var to retrieve
   * @param defValue 	the default value to use if not set
   * @return		the associated value or the default value if not present
   * @see		System#getenv(String)
   */
  public static String get(String name, String defValue) {
    return get(name, defValue, false, false);
  }

  /**
   * Returns the value of the specified environment variable.
   *
   * @param name	the env var to retrieve
   * @param defValue 	the default value to use if not set
   * @param dotsToUnderscores 	whether to check presence of name with underscores replacing dots
   * @param checkUppercase 	whether to check presence of uppercased name
   * @return		the associated value or the default value if not present
   * @see		System#getenv(String)
   */
  public static String get(String name, String defValue, boolean dotsToUnderscores, boolean checkUppercase) {
    String	result;

    name = find(name, dotsToUnderscores, checkUppercase);
    if (name == null)
      result = defValue;
    else
      result = System.getenv(name);

    return result;
  }

  /**
   * Obtains key=value pairs from the specified environment variable.
   * Uses semi-colon (;) as list separator and equals (=) as pair separator.
   *
   * @param name	the environment variable to check
   * @return		the generated mapping of key=value pairs
   */
  public static Map<String,String> getKeyValuePairs(String name) {
    return getKeyValuePairs(name, SEPARATOR_LIST, SEPARATOR_PAIR, true);
  }

  /**
   * Obtains key=value pairs from the specified environment variable.
   *
   * @param name	the environment variable to check
   * @param dotsToUnderscores 	whether to convert check presence of name with underscores replacing dots
   * @param checkUppercase 	whether to convert check presence of uppercased name
   * @return		the generated mapping of key=value pairs
   */
  public static Map<String,String> getKeyValuePairs(String name, boolean dotsToUnderscores, boolean checkUppercase) {
    return getKeyValuePairs(name, dotsToUnderscores, checkUppercase, SEPARATOR_LIST, SEPARATOR_PAIR, true);
  }

  /**
   * Obtains key=value pairs from the specified environment variable.
   *
   * @param name	the environment variable to check
   * @param listSep 	the separator to use for splitting the list of pairs
   * @param pairSep	the separator to use for splitting a pair into key and value
   * @param info 	whether to output some info in the console on stdout
   * @return		the generated mapping of key=value pairs
   */
  public static Map<String,String> getKeyValuePairs(String name, String listSep, String pairSep, boolean info) {
    return getKeyValuePairs(name, false, false, listSep, pairSep, info);
  }

  /**
   * Obtains key=value pairs from the specified environment variable.
   *
   * @param name	the environment variable to check
   * @param listSep 	the separator to use for splitting the list of pairs
   * @param pairSep	the separator to use for splitting a pair into key and value
   * @param info 	whether to output some info in the console on stdout
   * @param dotsToUnderscores 	whether to convert check presence of name with underscores replacing dots
   * @param checkUppercase 	whether to convert check presence of uppercased name
   * @return		the generated mapping of key=value pairs
   */
  public static Map<String,String> getKeyValuePairs(String name, boolean dotsToUnderscores, boolean checkUppercase, String listSep, String pairSep, boolean info) {
    Map<String,String>	result;
    String		newName;
    String		env;
    String[]		pairs;
    String[]		parts;

    result  = new HashMap<>();
    newName = find(name, dotsToUnderscores, checkUppercase);
    env     = get(newName, null);
    if (env != null) {
      if (info)
	System.out.println("Applying " + newName + ": " + env);
      pairs = env.split(listSep);
      for (String pair: pairs) {
	parts = pair.split(pairSep);
	if (parts.length == 2)
	  result.put(parts[0], parts[1]);
	else
	  System.err.println(
	    "Invalid key-value pair in environment variable '" + newName + "' (list separator is '" + listSep + "' "
	      + "and pair separator is '" + pairSep + "'): " + pair);
      }
    }

    return result;
  }
}
