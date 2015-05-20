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
 * Variables.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import adams.core.base.BaseRegExp;
import adams.core.logging.LoggingObject;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.event.VariableChangeListener;

/**
 * A container for storing variables and their values. Values are
 * stored as string representations. A variable placeholder string is of the
 * following form: <br>
 * <pre>
 * @{name}
 * </pre>
 * With "name" consisting of word characters: 0-9a-zA-Z_-
 * <br><br>
 * Environment variables can be accessed by prefixing them with "env."
 * ({@link Variables#ENVIRONMENT_VARIABLE_PREFIX}) and system properties
 * by prefixing with "system." ({@link Variables#SYSTEM_PROPERTY_PREFIX}).
 * Examples: system.os.name, env.PATH
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #ENVIRONMENT_VARIABLE_PREFIX
 * @see #SYSTEM_PROPERTY_PREFIX
 */
public class Variables
  extends LoggingObject
  implements CleanUpHandler, CloneHandler<Variables> {

  /** for serialization. */
  private static final long serialVersionUID = -1654017967835758598L;

  /** the start of a variable. */
  public final static String START = "@{";

  /** the end of a variable. */
  public final static String END = "}";

  /** allowed characters. */
  public final static String CHARS = "abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ-0123456789:.";

  /** the prefix for environment variables. */
  public final static String ENVIRONMENT_VARIABLE_PREFIX = "env.";

  /** the prefix for system properties. */
  public final static String SYSTEM_PROPERTY_PREFIX = "system.";

  /** the variable &lt;-&gt; value relation. */
  protected Hashtable<String,String> m_Variables;

  /** the environment variables &lt;-&gt; value relation. */
  protected Hashtable<String,String> m_EnvironmentVariables;

  /** the system properties &lt;-&gt; value relation. */
  protected Hashtable<String,String> m_SystemProperties;

  /** the listeners. */
  protected WrapperHashSet<VariableChangeListener> m_VariableChangeListeners;

  /**
   * Initializes the container.
   */
  public Variables() {
    super();

    m_Variables               = new Hashtable<String,String>();
    m_VariableChangeListeners = new WrapperHashSet<VariableChangeListener>();

    // environment variables
    m_EnvironmentVariables = new Hashtable<String,String>();
    Map<String,String> env = System.getenv();
    for (String key: env.keySet())
      m_EnvironmentVariables.put(ENVIRONMENT_VARIABLE_PREFIX + key, env.get(key));

    // system properties
    m_SystemProperties = new Hashtable<String,String>();
    java.util.Properties props = System.getProperties();
    Enumeration enm = props.propertyNames();
    while (enm.hasMoreElements()) {
      String key = (String) enm.nextElement();
      m_SystemProperties.put(SYSTEM_PROPERTY_PREFIX + key, props.getProperty(key));
    }
  }

  /**
   * Removes all currently stored variables.
   */
  public void clear() {
    m_Variables.clear();
  }

  /**
   * Returns an enumeration over the names of the variables.
   *
   * @return		the names
   */
  public Enumeration<String> names() {
    return m_Variables.keys();
  }

  /**
   * Returns a set containing the names of the variables.
   *
   * @return		the names
   */
  public Set<String> nameSet() {
    return m_Variables.keySet();
  }

  /**
   * Stores the value for the variable.
   *
   * @param name	the name (or placeholder string) of the variable
   * @param value	the value of the variable
   */
  public void set(String name, String value) {
    Type	type;
    String	strippedName;

    if (!isValidName(name))
      throw new IllegalArgumentException("Invalid variable name: " + name);

    strippedName = extractName(name);
    if (strippedName.startsWith(ENVIRONMENT_VARIABLE_PREFIX))
      return;
    if (strippedName.startsWith(SYSTEM_PROPERTY_PREFIX))
      return;

    if (has(strippedName))
      type = Type.MODIFIED;
    else
      type = Type.ADDED;

    if (isLoggingEnabled())
      getLogger().log(Level.INFO, "set: name=" + name + ", value=" + value);

    m_Variables.put(strippedName, value);
    notifyVariableChangeListeners(new VariableChangeEvent(this, type, name));
  }

  /**
   * Checks whether a variable is stored or not.
   *
   * @param name	the name (or placeholder string) of the variable
   * @return		true if the variable is stored
   */
  public boolean has(String name) {
    name = extractName(name);
    if (m_EnvironmentVariables.containsKey(name))
      return true;
    else if (m_SystemProperties.containsKey(name))
      return true;
    else
      return m_Variables.containsKey(name);
  }

  /**
   * Returns the stored value if present, otherwise null.
   *
   * @param name	the name (or placeholder string) of the variable
   * @return		the associated value or null if not found
   */
  public String get(String name) {
    return get(name, null);
  }

  /**
   * Removes the variable.
   *
   * @param name	the name (or placeholder string) of the variable
   * @return		the previously stored value
   */
  public String remove(String name) {
    String	result;

    result = m_Variables.remove(extractName(name));
    notifyVariableChangeListeners(new VariableChangeEvent(this, Type.REMOVED, name));

    if (isLoggingEnabled())
      getLogger().log(Level.INFO, "remove: name=" + name + ", value=" + result);

    return result;
  }

  /**
   * Removes variables that match a regular expressions.
   *
   * @param regExp	the regular expression to match the name of the variables against
   * @return		true if at least one was removed
   */
  public boolean remove(BaseRegExp regExp) {
    boolean		result;
    List<String>	names;

    result = false;

    names = new ArrayList<String>();
    synchronized(m_Variables) {
      // find matches
      for (String name: m_Variables.keySet()) {
	if (regExp.isMatch(name))
	  names.add(name);
      }

      // remove variables
      for (String name: names)
	remove(name);
    }

    return result;
  }

  /**
   * Returns the stored value if present, otherwise the default value.
   *
   * @param name	the name (or placeholder string) of the variable
   * @param defValue	the default value, in case the variable is not stored
   * @return		the associated value
   */
  public String get(String name, String defValue) {
    name = extractName(name);
    if (m_EnvironmentVariables.containsKey(name))
      return m_EnvironmentVariables.get(name);
    else if (m_SystemProperties.containsKey(name))
      return m_SystemProperties.get(name);
    else if (m_Variables.containsKey(name))
      return m_Variables.get(name);
    else
      return defValue;
  }

  /**
   * Returns whether the stored value is present as non-string object.
   * <br><br>
   * Default implementation always returns false.
   *
   * @return		true if the value is stored as non-string
   * @see		#getObject(String)
   */
  public boolean isObject(String name) {
    return false;
  }

  /**
   * Returns the store value.
   *
   * @param name	the name of the value
   * @return		the value referenced by the name, null if not available
   * @see		#getObject(String,Object)
   */
  public Object getObject(String name) {
    return getObject(name, null);
  }

  /**
   * Returns the store value.
   * <br><br>
   * Default implementation simply returns {@link #get(String,String)}.
   *
   * @param name	the name of the value
   * @param defValue	the default value to use if value not present
   * @return		the value referenced by the name, defValue if not available
   */
  public Object getObject(String name, Object defValue) {
    if (defValue == null)
      return get(name, null);
    else
      return get(name, defValue.toString());
  }

  /**
   * Returns the number of variables currently stored.
   *
   * @return		the number of variables
   */
  public int size() {
    return m_Variables.size();
  }

  /**
   * Adds the listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addVariableChangeListener(VariableChangeListener l) {
    m_VariableChangeListeners.add(l);
  }

  /**
   * Removes the listener from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeVariableChangeListener(VariableChangeListener l) {
    m_VariableChangeListeners.remove(l);
  }

  /**
   * Removes all listeners from the internal list.
   */
  public void removeVariableChangeListeners() {
    m_VariableChangeListeners.clear();
  }

  /**
   * Notifies all listeners with the specified event.
   *
   * @param e		the event to send
   */
  protected void notifyVariableChangeListeners(VariableChangeEvent e) {
    for (VariableChangeListener l: m_VariableChangeListeners)
      l.variableChanged(e);
  }

  /**
   * Extracts the name of a variable from the placeholder string, i.e., it
   * strips the "@{" and "}" from the string. Does nothing if already stripped.
   *
   * @param variable	the variable placeholder
   * @return		the extracted name
   * @see		#START
   * @see		#END
   */
  public static String extractName(String variable) {
    String	result;

    result = variable;

    if (result.startsWith(START) && result.endsWith(END))
      result = result.substring(2, result.length() - 1);

    return result;
  }
  
  /**
   * Extracts all the variables from the given expression.
   * 
   * @param expr	the expression to parse for variable names
   * @return		the variable names located in the expression
   */
  public static String[] extractNames(String expr) {
    List<String>	result;
    int			start;
    int			end;
    
    result = new ArrayList<String>();
    
    while ((start = expr.indexOf(START)) > -1) {
      end = expr.indexOf(END, start);
      if (end > -1) {
	result.add(expr.substring(start + START.length(), end + END.length() - 1));
	expr = expr.substring(end + END.length(), expr.length());
      }
    }
    
    return result.toArray(new String[result.size()]);
  }

  /**
   * Surrounds the variable name with "@{" and "}", if necessary.
   *
   * @param name	the name to process
   * @return		the padded name
   * @see		#START
   * @see		#END
   */
  public static String padName(String name) {
    String	result;

    result = name;
    if (!(result.startsWith(START) && result.endsWith(END)))
      result = START + result + END;

    return result;
  }

  /**
   * Checks whether the string represents a variable placeholder, i.e., it
   * starts with "@{" and ends with "}".
   *
   * @param s		the string to check
   * @return		true if the string represents a variable placeholder
   * 			string
   */
  public static boolean isPlaceholder(String s) {
    boolean	result;
    
    if (s == null)
      return false;

    result = (s.startsWith(START) && s.endsWith(END));
    if (result)
      result = isValidName(extractName(s));

    return result;
  }

  /**
   * Checks whether the string represents a valid name (without the "@{" and "}").
   *
   * @param s		the name to check
   * @return		true if valid
   */
  public static boolean isValidName(String s) {
    boolean	result;
    String	name;
    int		i;
    char	c;
    
    if (s == null)
      return false;

    name   = extractName(s);
    result = (name.length() > 0);
    if (result) {
      for (i = 0; i < name.length(); i++) {
	c = name.charAt(i);
	if (CHARS.indexOf(c) == -1) {
	  result = false;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Creates a valid variable name.
   *
   * @param s		the name to turn into a valid variable name
   * @return		the processed name
   */
  public static String createValidName(String s) {
    StringBuilder	result;
    int			i;
    char		c;

    result = new StringBuilder();
    s      = extractName(s);
    for (i = 0; i < s.length(); i++) {
      c = s.charAt(i);
      if (CHARS.indexOf(c) == -1)
	result.append("_");
      else
	result.append(c);
    }

    return result.toString();
  }

  /**
   * Turns any string (with surrounding @{...}) into a valid variable name, 
   * by replacing invalid characters with underscores ("_").
   * 
   * @param s		the string to convert into a valid variable name
   * @return		the (potentially) fixed name
   */
  public static String toValidName(String s) {
    return toValidName(s, "_");
  }

  /**
   * Turns any string (with surrounding @{...}) into a valid variable name, 
   * by replacing invalid characters with the specified replacement string.
   * 
   * @param s		the string to convert into a valid variable name
   * @param replace	the replacement string for invalid chars
   * @return		the (potentially) fixed name
   */
  public static String toValidName(String s, String replace) {
    StringBuilder	result;
    int			i;
    char		chr;
    
    result = new StringBuilder();
    
    for (i = 0; i < s.length(); i++) {
      chr = s.charAt(i);
      if ((chr >= '0') && (chr <= '9'))
	result.append(chr);
      else if ((chr >= 'a') && (chr <= 'z'))
	result.append(chr);
      else if ((chr >= 'A') && (chr <= 'Z'))
	result.append(chr);
      else if (chr == '_')
	result.append(chr);
      else if (chr == '-')
	result.append(chr);
      else if (chr == ':')
	result.append(chr);
      else if (chr == '.')
	result.append(chr);
      else
	result.append(replace);
    }
    
    return result.toString();
  }
  
  /**
   * Replaces all variables in the string with the currently stored values.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  public String expand(String s) {
    return expand(s, (s.indexOf(START + START) > -1));
  }

  /**
   * Expands environment variables.
   *
   * @param s		the string to expand
   * @return		the potentially expanded string
   */
  protected String doExpandEnv(String s) {
    String		result;
    String		part;
    Enumeration<String>	enm;
    String		name;

    result = s;
    part   = START + ENVIRONMENT_VARIABLE_PREFIX;
    if (result.indexOf(part) > -1) {
      enm = m_EnvironmentVariables.keys();
      while (enm.hasMoreElements() && (result.indexOf(part) > -1)) {
	name   = enm.nextElement();
	result = result.replace(START + name + END, get(name));
      }
    }

    return result;
  }

  /**
   * Expands system properties.
   *
   * @param s		the string to expand
   * @return		the potentially expanded string
   */
  protected String doExpandSystemProps(String s) {
    String		result;
    String		part;
    Enumeration<String>	enm;
    String		name;

    result = s;
    part   = START + SYSTEM_PROPERTY_PREFIX;
    if (result.indexOf(part) > -1) {
      enm = m_SystemProperties.keys();
      while (enm.hasMoreElements() && (result.indexOf(part) > -1)) {
	name   = enm.nextElement();
	result = result.replace(START + name + END, get(name));
      }
    }

    return result;
  }

  /**
   * Expands regular variables.
   *
   * @param s		the string to expand
   * @return		the potentially expanded string
   */
  protected String doExpandRegular(String s) {
    String		result;
    String		part;
    Enumeration<String>	enm;
    String		name;

    result = s;
    part   = START;
    if (result.indexOf(part) > -1) {
      enm = names();
      while (enm.hasMoreElements() && (result.indexOf(part) > -1)) {
	name   = enm.nextElement();
	result = result.replace(START + name + END, get(name));
      }
    }

    return result;
  }

  /**
   * Performs all expansions.
   *
   * @param s		the string to expand
   * @return		the potentially expanded string
   */
  protected String doExpand(String s) {
    String		result;

    result = s;
    result = doExpandEnv(result);
    result = doExpandSystemProps(result);
    result = doExpandRegular(result);

    return result;
  }

  /**
   * Replaces all variables in the string with the currently stored values.
   *
   * @param s		the string to process
   * @param recurse	whether to recurse, i.e., replacing "@{@{"
   * @return		the processed string
   */
  protected String expand(String s, boolean recurse) {
    String		result;

    result = doExpand(s);
    if (recurse)
      result = expand(result);

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    removeVariableChangeListeners();
    clear();
  }

  /**
   * Returns a clone of the object (but without the listeners).
   *
   * @return		the clone, null if failed
   */
  @Override
  public Variables getClone() {
    return getClone(null);
  }

  /**
   * Returns a clone of the object (but without the listeners).
   *
   * @param filter	the regular expression that the variable names must 
   * 			match, null to ignore
   * @return		the clone, null if failed
   */
  public Variables getClone(BaseRegExp filter) {
    Variables	result;

    try {
      result = getClass().newInstance();
      result.assign(this, filter);
    }
    catch (Exception e) {
      result = null;
      System.err.println("Failed to clone variables:");
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Adds all the variables from the other Variables object (overwrites
   * any existing ones).
   *
   * @param other	the Variables to copy
   */
  public void assign(Variables other) {
    assign(other, null);
  }

  /**
   * Adds all the variables from the other Variables object (overwrites
   * any existing ones).
   *
   * @param other	the Variables to copy
   * @param filter	the regular expression that the variable names must 
   * 			match, null to ignore
   */
  public void assign(Variables other, BaseRegExp filter) {
    Enumeration<String>		names;
    String			name;

    names = other.names();
    while (names.hasMoreElements()) {
      name = names.nextElement();
      if ((filter == null) || ((filter != null) && (filter.isMatch(name))))
	set(name, other.get(name));
    }
  }

  /**
   * Simply returns the internal hashtable of variable/value pairs.
   *
   * @return		the stored variables
   */
  @Override
  public String toString() {
    return m_Variables.toString();
  }
}
