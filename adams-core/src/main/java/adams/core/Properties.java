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
 * Properties.java
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.base.BasePassword;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.env.Environment;
import adams.gui.core.ColorHelper;
import adams.gui.core.GUIHelper;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Enhanced java.util.Properties class.
 * <p>
 * <b>Date types</b> are stores as follows:
 * <ul>
 *   <li>time: {@link #FORMAT_TIME}</li>
 *   <li>date: {@link #FORMAT_DATE}</li>
 *   <li>date/time: {@link #FORMAT_DATETIME}</li>
 * </ul>
 * <p>
 * <b>Colors</b> are stored as hex string, i.e., "#RRGGBB" with all three
 * values as hex values. The following string representations of colors
 * are supported as well (case-insensitive):
 * <ul>
 *   <li>black</li>
 *   <li>blue</li>
 *   <li>cyan</li>
 *   <li>darkgray</li>
 *   <li>darkgrey</li>
 *   <li>gray</li>
 *   <li>grey</li>
 *   <li>green</li>
 *   <li>lightgray</li>
 *   <li>lightgrey</li>
 *   <li>magenta</li>
 *   <li>orange</li>
 *   <li>pink</li>
 *   <li>red</li>
 *   <li>white</li>
 *   <li>yellow</li>
 * </ul>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Properties
  extends java.util.Properties
  implements Comparable, Mergeable<Properties>, CloneHandler<Properties> {

  /** for serialization. */
  private static final long serialVersionUID = -4908679921070087606L;

  /** the comment. */
  public final static String COMMENT = "# ";

  /** the time format. */
  public final static String FORMAT_TIME = "HH:mm:ss.SSSZ";

  /** the date format. */
  public final static String FORMAT_DATE = "yyyy-MM-dd";

  /** the date/time format. */
  public final static String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  
  /** the debugging level. */
  private final static Logger LOGGER = LoggingHelper.getConsoleLogger(Properties.class);

  /**
   * default constructor.
   */
  public Properties() {
    super();

    initialize();
  }

  /**
   * Creates an empty property list with the specified defaults.
   *
   * @param defaults	the default properties
   */
  public Properties(java.util.Properties defaults) {
    super();

    Properties defaultsNew = new Properties();
    Enumeration<String> enm = (Enumeration<String>) defaults.propertyNames();
    while (enm.hasMoreElements()) {
      String key = enm.nextElement();
      defaultsNew.setProperty(key, defaults.getProperty(key));
    }
    setDefaults(defaultsNew);

    initialize();
  }

  /**
   * Creates an empty property list with the specified defaults.
   *
   * @param defaults	the default properties
   */
  public Properties(Properties defaults) {
    super(defaults);

    initialize();
  }

  /**
   * Initializes member variables.
   */
  protected void initialize() {
  }

  /**
   * Sets the default properties.
   *
   * @param value	the default properties
   */
  public void setDefaults(Properties value) {
    defaults = value;
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public Properties getClone() {
    return (Properties) clone();
  }

  /**
   * Reads properties that inherit from several locations. Properties
   * are first defined in the system resource location (i.e. in the
   * CLASSPATH).  These default properties must exist.
   * Not to be confused with <code>load(String)</code>.
   *
   * @param name 	the location of the resource that should be
   * 			loaded.  e.g.: "adams/gui/Something.props".
   * @return 		the Properties
   * @throws Exception 	if no default properties are defined, or if
   * 			an error occurs reading the properties files.
   * @see		#load(String)
   */
  public static Properties read(String name) throws Exception {
    List<String>	dirs;

    dirs = new ArrayList<String>();
    dirs.add(name.replaceAll("\\/[^\\/]*$", ""));
    dirs.add(Environment.getInstance().getHome());
    if (!dirs.contains(System.getProperty("user.dir")))
      dirs.add(System.getProperty("user.dir"));

    return read(name, dirs);
  }

  /**
   * Reads properties that inherit from several locations. Properties
   * are first defined in the system resource location (i.e. in the
   * CLASSPATH).  These default properties must exist.
   * Not to be confused with <code>load(String)</code>.
   *
   * @param name 	the location of the resource that should be
   * 			loaded.  e.g.: "adams/gui/Something.props".
   * @param dirs	the directories to search for this properties file.
   * @return 		the Properties
   * @throws Exception 	if no default properties are defined, or if
   * 			an error occurs reading the properties files.
   * @see		#load(String)
   */
  public static Properties read(String name, List<String> dirs) throws Exception {
    Properties 		result;
    Properties		props;
    int			i;
    Enumeration<URL>	urls;
    URL			url;
    boolean		first;

    name   = name.replaceAll(".*\\/", "");
    result = new Properties();
    first  = true;

    for (i = 0; i < dirs.size(); i++) {
      LOGGER.log(Level.INFO, "name=" + name + ", dir/" + (i+1) + "=" + dirs.get(i));

      if (i == 0) {
	try {
	  urls = ClassLoader.getSystemResources(dirs.get(i) + "/" + name);
	  while (urls.hasMoreElements()) {
	    url = urls.nextElement();
	    LOGGER.log(Level.FINE, "url=" + url);
	    if (first) {
	      result.load(url.openStream());
	      first = false;
	      LOGGER.log(Level.FINER, "props=" + result.toStringSimple());
	    }
	    else {
	      props = new Properties();
	      props.load(url.openStream());
	      LOGGER.log(Level.FINER, "props=" + props.toStringSimple());
	      result.mergeWith(props);
	    }
	  }
	}
	catch (Exception ex) {
	  System.err.println("Warning, unable to load properties file from system resource: " + dirs.get(i) + "/" + name);
	}
      }
      else {
	props = new Properties(result);
	LOGGER.log(Level.FINE, "file=" + dirs.get(i) + "/" + name);
	props.load(dirs.get(i) + "/" + name);
	result = props;
	LOGGER.log(Level.FINER, "props=" + result.toStringSimple());
      }
    }

    return result;
  }

  /**
   * Expands the placeholders in the string. Supported placeholders:
   * <pre>
   * %t - the temp directory
   * %h - the user's home directory (also $HOME and %USERHOME%)
   * %p - the project's home directory (if such is available)
   * %c - the current directory
   * %% - gets replaced by a single percentage sign
   * </pre>
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String expandPlaceHolders(String s) {
    String	result;

    result = s;

    if ((s.indexOf('%') > -1) || (s.indexOf('$') > -1)) {
      result = result.replace("%t", System.getProperty("java.io.tmpdir"));
      result = result.replace("%p", Environment.getInstance().getHome());
      result = result.replace("%h", System.getProperty("user.home"));
      result = result.replace("$HOME", System.getProperty("user.home"));
      result = result.replace("%USERHOME%", System.getProperty("user.home"));
      result = result.replace("%c", System.getProperty("user.dir"));
      result = result.replace("%%", "%");
    }

    return result;
  }

  /**
   * Ensures that the directory ends with a separator, e.g. "/".
   * 
   * @param dir		the directory to fix
   * @return		the fixed directory
   */
  protected String fixDir(String dir) {
    if (!dir.endsWith(File.separator))
      return dir + File.separator;
    else
      return dir;
  }
  
  /**
   * Collapses the paths to placeholders in the string. Supported placeholders
   * (from highest precedence to lowest):
   * <pre>
   * %c - the current directory
   * %p - the project's home directory (if such is available)
   * %h - the user's home directory
   * %t - the temp directory
   * </pre>
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String collapsePlaceHolders(String s) {
    String	result;
    String[]	find;
    String[]	replace;
    int		i;

    result = s;

    find = new String[]{
	System.getProperty("user.dir"),
	Environment.getInstance().getHome(),
	System.getProperty("user.home"),
	System.getProperty("java.io.tmpdir")
    };
    replace = new String[]{
	"%c",
	"%p",
	"%h",
	"%t"
    };
    
    for (i = 0; i < find.length; i++) {
      find[i]    = fixDir(find[i]);
      replace[i] = fixDir(replace[i]);
      if (result.startsWith(find[i]))
	result = replace[i] + result.substring(find[i].length());
    }

    return result;
  }

  /**
   * Returns a set of all the keys.
   *
   * @return		a set over all the keys
   */
  public Set<String> keySetAll() {
    HashSet<String>	result;
    Enumeration<String>	enm;

    result = new HashSet<String>();
    enm    = (Enumeration<String>) propertyNames();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    return result;
  }

  /**
   * Returns a set of all the keys that match the specified regular expression.
   *
   * @param regexp	the expression to match the keys against
   * @return		a set over all the keys
   */
  public Set<String> keySetAll(BaseRegExp regexp) {
    HashSet<String>	result;
    Enumeration<String>	enm;
    String		key;

    result = new HashSet<String>();
    enm    = (Enumeration<String>) propertyNames();
    while (enm.hasMoreElements()) {
      key = enm.nextElement();
      if (regexp.isMatch(key))
	result.add(key);
    }

    return result;
  }

  /**
   * Returns an enumeration of all the keys in this property list,
   * including distinct keys in the default property list if a key
   * of the same name has not already been found from the main
   * properties list, as long as the key matches the provided prefix.
   *
   * @param regExp	the regular expression that the property keys must match
   * @return  		an enumeration of all the keys in this property list, including
   *          		the keys in the default property list, that match the prefix.
   */
  public Enumeration<String> propertyNames(String regExp) {
    Vector<String>	result;
    Enumeration<String>	enm;
    String		key;
    Pattern		pattern;

    if ((regExp == null) || (regExp.length() == 0))
      regExp = ".*";

    pattern = Pattern.compile(regExp);
    result  = new Vector<String>();
    enm     = (Enumeration<String>) propertyNames();
    while (enm.hasMoreElements()) {
      key = enm.nextElement();
      if (pattern.matcher(key).matches())
	result.add(key);
    }

    return result.elements();
  }

  /**
   * Loads the properties from the given file - not to be confused with
   * <code>read(String)</code>.
   *
   * @param filename	the file to load the properties from
   * @return		true if successfully loaded
   * @see		#read(String)
   */
  public boolean load(String filename) {
    boolean		result;
    BufferedInputStream	stream;
    File		file;

    result = true;

    try {
      clear();
      file = new File(filename);
      if (file.exists()) {
	stream = new BufferedInputStream(new FileInputStream(filename));
	load(stream);
	stream.close();
      }
      else {
	LOGGER.log(Level.FINE, "file '" + filename + "' does not exist - skipped!");
      }
    }
    catch (Exception e) {
      clear();
      result = false;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Saves the properties to the given file.
   *
   * @param filename	the file to save the properties to
   * @return		true if successfully written
   */
  public boolean save(String filename) {
    return save(filename, null);
  }

  /**
   * Saves the properties to the given file.
   *
   * @param filename	the file to save the properties to
   * @param comment	the comment to use, can be null
   * @return		true if successfully written
   */
  public boolean save(String filename, String comment) {
    boolean			result;
    BufferedOutputStream	stream;
    FileOutputStream		fos;

    result = true;

    fos    = null;
    stream = null;
    try {
      stream = new BufferedOutputStream(new FileOutputStream(filename));
      collapse().store(stream, comment);
      stream.flush();
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
    }
    finally {
      FileUtils.closeQuietly(stream);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }

  /**
   * Outputs a single line representation of the full properties stored in
   * the Properties object (and its defaults).
   *
   * @return		the single-line representation
   */
  public String toStringSimple() {
    Hashtable<String,String>	values;
    Enumeration<String>		names;
    String			name;

    values = new Hashtable<String,String>();
    names  = (Enumeration<String>) propertyNames();
    while (names.hasMoreElements()) {
      name = names.nextElement();
      values.put(name, getProperty(name));
    }

    return values.toString();
  }

  /**
   * Outputs the properties as they would be written to a file.
   *
   * @return		the generated output or null in case of an error
   */
  @Override
  public String toString() {
    return toString(null);
  }

  /**
   * Outputs the properties as they would be written to a file.
   *
   * @param comment	the comment to output
   * @return		the generated output or null in case of an error
   */
  public String toString(String comment) {
    String		result;
    StringWriter	writer;

    result = null;

    try {
      writer = new StringWriter();
      collapse().store(writer, comment);
      writer.flush();
      writer.close();
      result = writer.toString();
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Outputs the properties as they would be written to a file.
   * In addition, all lines will be output as comment:
   * <pre>
   * # key1=value1
   * # key2=value2
   * # ...
   * </pre>
   *
   * @return		the generated output or null in case of an error
   * @see		#COMMENT
   */
  public String toComment() {
    return Utils.commentOut(toString(), COMMENT);
  }

  /**
   * Adds the key-value pairs stored in the specified properties to itself.
   * The property names are not prefixed. Already existing properties will be
   * overwritten.
   *
   * @param props	the properties to add
   */
  public void add(Properties props) {
    add(props, null);
  }

  /**
   * Adds the key-value pairs stored in the specified properties to itself.
   * Already existing properties will be overwritten.
   *
   * @param props	the properties to add
   * @param prefix	the prefix for the property names, use null to ignore
   */
  public void add(Properties props, String prefix) {
    Enumeration	enm;
    String	key;

    enm = props.propertyNames();
    while (enm.hasMoreElements()) {
      key = (String) enm.nextElement();
      if (prefix != null)
	setProperty(prefix + key, props.getProperty(key));
      else
	setProperty(key, props.getProperty(key));
    }
  }

  /**
   * Removes the entry with the specified key, if available.
   *
   * @param key		the key to remove
   */
  public void removeKey(String key) {
    remove(key);
  }

  /**
   * Removes the entry with the specified key, if available. Processes the
   * defaults recursively.
   *
   * @param key		the key to remove
   */
  public void removeKeyRecursive(String key) {
    removeKey(key);
    if (defaults != null)
      ((Properties) defaults).removeKeyRecursive(key);
  }

  /**
   * Removes the entries that match the regular expression.
   *
   * @param regexp	the regular expression that the keys must match
   */
  public void removeKeys(String regexp) {
    Enumeration		keys;
    String		key;
    List<String>	toRemove;
    int			i;
    Pattern		pattern;

    keys     = keys();
    toRemove = new ArrayList<String>();
    pattern  = Pattern.compile(regexp);
    while (keys.hasMoreElements()) {
      key = keys.nextElement().toString();
      if (pattern.matcher(key).matches())
	toRemove.add(key);
    }

    for (i = 0; i < toRemove.size(); i++)
      remove(toRemove.get(i));
  }

  /**
   * Removes the entries that match the specified regular expression. Processes the
   * defaults recursively.
   *
   * @param regexp	the regular expression that the keys must match
   */
  public void removeKeysRecursive(String regexp) {
    removeKeys(regexp);
    if (defaults != null)
      ((Properties) defaults).removeKeysRecursive(regexp);
  }

  /**
   * Checks whether the given property exists.
   *
   * @param key		the property to check
   * @return		true if the property exists
   */
  public boolean hasKey(String key) {
    return (getProperty(key) != null);
  }

  /**
   * Sets the integer value.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setInteger(String key, Integer value) {
    setProperty(key, "" + value);
  }

  /**
   * Returns the integer value associated with the key, or null if not found
   * or not parseable.
   *
   * @param key		the property to look for
   * @return		the value for the key
   */
  public Integer getInteger(String key) {
    return getInteger(key, null);
  }

  /**
   * Returns the integer value associated with the key, or the default value
   * if not found or not parseable.
   *
   * @param key		the property to look for
   * @param defValue	the default value in case key is not found or value
   * 			cannot be parsed
   * @return		the value for the key
   */
  public Integer getInteger(String key, Integer defValue) {
    Integer	result;
    String	value;

    result = defValue;

    try {
      if (hasKey(key)) {
	value = getProperty(key);
	if (value != null)
	  result = new Integer(value);
      }
    }
    catch (Exception e) {
      result = defValue;
      System.err.println(
	  "Cannot parse integer value of property '" + key + " (" + getProperty(key) + ")'! "
	  + "Using default: " + defValue);
    }

    return result;
  }

  /**
   * Sets the long value.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setLong(String key, Long value) {
    setProperty(key, "" + value);
  }

  /**
   * Returns the long value associated with the key, or null if not found
   * or not parseable.
   *
   * @param key		the property to look for
   * @return		the value for the key
   */
  public Long getLong(String key) {
    return getLong(key, null);
  }

  /**
   * Returns the long value associated with the key, or the default value
   * if not found or not parseable.
   *
   * @param key		the property to look for
   * @param defValue	the default value in case key is not found or value
   * 			cannot be parsed
   * @return		the value for the key
   */
  public Long getLong(String key, Long defValue) {
    Long	result;
    String	value;

    result = defValue;

    try {
      if (hasKey(key)) {
	value = getProperty(key);
	if (value != null)
	  result = new Long(value);
      }
    }
    catch (Exception e) {
      result = defValue;
      System.err.println(
	  "Cannot parse long value of property '" + key + " (" + getProperty(key) + ")'! "
	  + "Using default: " + defValue);
    }

    return result;
  }

  /**
   * Sets the double value.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setDouble(String key, Double value) {
    setProperty(key, "" + value);
  }

  /**
   * Returns the double value associated with the key, or null if not found
   * or not parseable.
   *
   * @param key		the property to look for
   * @return		the value for the key
   */
  public Double getDouble(String key) {
    return getDouble(key, null);
  }

  /**
   * Returns the double value associated with the key, or the default value
   * if not found or not parseable.
   *
   * @param key		the property to look for
   * @param defValue	the default value in case key is not found or value
   * 			cannot be parsed
   * @return		the value for the key
   */
  public Double getDouble(String key, Double defValue) {
    Double	result;
    String	value;

    result = defValue;

    try {
      if (hasKey(key)) {
	value = "" + getProperty(key);
	if (value != null)
	  result = new Double(value);
      }
    }
    catch (Exception e) {
      result = defValue;
      System.err.println(
	  "Cannot parse double value of property '" + key + "'! "
	  + "Using default: " + defValue);
    }

    return result;
  }

  /**
   * Sets the boolean value.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setBoolean(String key, Boolean value) {
    setProperty(key, "" + value);
  }

  /**
   * Returns the boolean value associated with the key, or null if not found
   * or not parseable.
   *
   * @param key		the property to look for
   * @return		the value for the key
   */
  public Boolean getBoolean(String key) {
    return getBoolean(key, null);
  }

  /**
   * Returns the boolean value associated with the key, or the default value
   * if not found or not parseable.
   *
   * @param key		the property to look for
   * @param defValue	the default value in case key is not found or value
   * 			cannot be parsed
   * @return		the value for the key
   */
  public Boolean getBoolean(String key, Boolean defValue) {
    Boolean	result;
    String	value;

    result = defValue;

    try {
      if (hasKey(key)) {
	value = getProperty(key);
	if (value != null)
	  result = new Boolean(value);
      }
    }
    catch (Exception e) {
      result = defValue;
      System.err.println(
	  "Cannot parse boolean value of property '" + key + "'! "
	  + "Using default: " + defValue);
    }

    return result;
  }

  /**
   * Sets the time value.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setTime(String key, Time value) {
    if (value != null)
      setProperty(key, new DateFormat(FORMAT_TIME).format(value));
  }

  /**
   * Returns the time value associated with the key, or null if not found
   * or not parseable.
   *
   * @param key		the property to look for
   * @return		the value for the key
   */
  public Time getTime(String key) {
    return getTime(key, null);
  }

  /**
   * Returns the time value associated with the key, or the default value
   * if not found or not parseable.
   *
   * @param key		the property to look for
   * @param defValue	the default value in case key is not found or value
   * 			cannot be parsed
   * @return		the value for the key
   */
  public Time getTime(String key, Time defValue) {
    Time	result;
    String	value;

    result = defValue;

    try {
      if (hasKey(key)) {
	value = getProperty(key);
	if (value != null)
	  result = new Time(new DateFormat(FORMAT_TIME).parse(value));
      }
    }
    catch (Exception e) {
      result = defValue;
      System.err.println(
	  "Cannot parse time value of property '" + key + "'! "
	  + "Using default: " + defValue);
    }

    return result;
  }

  /**
   * Sets the date value.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setDate(String key, Date value) {
    if (value != null)
      setProperty(key, new DateFormat(FORMAT_DATE).format(value));
  }

  /**
   * Returns the date value associated with the key, or null if not found
   * or not parseable.
   *
   * @param key		the property to look for
   * @return		the value for the key
   */
  public Date getDate(String key) {
    return getDate(key, null);
  }

  /**
   * Returns the date value associated with the key, or the default value
   * if not found or not parseable.
   *
   * @param key		the property to look for
   * @param defValue	the default value in case key is not found or value
   * 			cannot be parsed
   * @return		the value for the key
   */
  public Date getDate(String key, Date defValue) {
    Date	result;
    String	value;

    result = defValue;

    try {
      if (hasKey(key)) {
	value = getProperty(key);
	if (value != null)
	  result = new DateFormat(FORMAT_DATE).parse(value);
      }
    }
    catch (Exception e) {
      result = defValue;
      System.err.println(
	  "Cannot parse date value of property '" + key + "'! "
	  + "Using default: " + defValue);
    }

    return result;
  }

  /**
   * Sets the date/time value.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   */
  public void setDateTime(String key, DateTime value) {
    if (value != null)
      setProperty(key, new DateFormat(FORMAT_DATETIME).format(value));
  }

  /**
   * Returns the date/time value associated with the key, or null if not found
   * or not parseable.
   *
   * @param key		the property to look for
   * @return		the value for the key
   */
  public DateTime getDateTime(String key) {
    return getDateTime(key, null);
  }

  /**
   * Returns the date/time value associated with the key, or the default value
   * if not found or not parseable.
   *
   * @param key		the property to look for
   * @param defValue	the default value in case key is not found or value
   * 			cannot be parsed
   * @return		the value for the key
   */
  public DateTime getDateTime(String key, DateTime defValue) {
    DateTime	result;
    String	value;

    result = defValue;

    try {
      if (hasKey(key)) {
	value = getProperty(key);
	if (value != null)
	  result = new DateTime(new DateFormat(FORMAT_DATETIME).parse(value));
      }
    }
    catch (Exception e) {
      result = defValue;
      System.err.println(
	  "Cannot parse date/time value of property '" + key + "'! "
	  + "Using default: " + defValue);
    }

    return result;
  }

  /**
   * Sets the path value.
   *
   * @param key		the property to store the value under
   * @param value	the value to store
   * @see		#collapsePlaceHolders(String)
   */
  public void setPath(String key, String value) {
    setProperty(key, collapsePlaceHolders(value));
  }

  /**
   * Returns the path value associated with the key, or null if not found.
   *
   * @param key		the property to look for
   * @return		the value for the key
   * @see		#expandPlaceHolders(String)
   */
  public String getPath(String key) {
    return getPath(key, null);
  }

  /**
   * Returns the path value associated with the key, or the default value
   * if not found.
   *
   * @param key		the property to look for
   * @param defValue	the default value in case key is not found
   * @return		the value for the key
   * @see		#expandPlaceHolders(String)
   */
  public String getPath(String key, String defValue) {
    String	result;
    String	value;

    result = defValue;
    if (result != null)
      result = expandPlaceHolders(result);

    if (hasKey(key)) {
      value = getProperty(key);
      if (value != null)
	result = expandPlaceHolders(value);
    }

    return result;
  }

  /**
   * Returns the color associated with the string.
   *
   * @param key		the property to look for
   * @return		the value or null if not found
   */
  public Color getColor(String key) {
    return getColor(key, null);
  }

  /**
   * Returns the color associated with the string.
   *
   * @param key		the property to look for
   * @param defValue	the default value
   * @return		the value or defValue if not found
   */
  public Color getColor(String key, Color defValue) {
    Color	result;

    result = defValue;

    if (hasKey(key))
      result = ColorHelper.valueOf(getProperty(key));

    return result;
  }

  /**
   * Stores the color as hex string under the specified property key.
   *
   * @param key		the property to store the color under
   * @param value	the color to store
   */
  public void setColor(String key, Color value) {
    setProperty(key, ColorHelper.toHex(value));
  }

  /**
   * Returns the password associated with the string.
   *
   * @param key		the property to look for
   * @return		the value or null if not found
   */
  public BasePassword getPassword(String key) {
    return getPassword(key, null);
  }

  /**
   * Returns the password associated with the string.
   *
   * @param key		the property to look for
   * @param defValue	the default value
   * @return		the value or defValue if not found
   */
  public BasePassword getPassword(String key, BasePassword defValue) {
    BasePassword	result;

    result = defValue;

    if (hasKey(key))
      result = new BasePassword(getProperty(key));

    return result;
  }

  /**
   * Stores the password as 64-bit encoded string under the specified 
   * property key.
   *
   * @param key		the property to store the password under
   * @param value	the password to store
   */
  public void setPassword(String key, BasePassword value) {
    setProperty(key, value.stringValue());
  }

  /**
   * Returns the font associated with the string.
   *
   * @param key		the property to look for
   * @return		the value or null if not found
   */
  public Font getFont(String key) {
    return getFont(key, null);
  }

  /**
   * Returns the font associated with the string.
   *
   * @param key		the property to look for
   * @param defValue	the default value
   * @return		the value or defValue if not found
   */
  public Font getFont(String key, Font defValue) {
    Font	result;

    result = defValue;

    if (hasKey(key))
      result = Font.decode(getProperty(key));

    return result;
  }

  /**
   * Stores the font under the specified property key.
   *
   * @param key		the property to store the font under
   * @param value	the font to store
   */
  public void setFont(String key, Font value) {
    setProperty(key, GUIHelper.encodeFont(value));
  }

  /**
   * Merges its own data with the one provided by the specified object.
   *
   * @param other		the object to merge with
   */
  public void mergeWith(Properties other) {
    HashSet<String>	keys;
    String		key;
    Iterator<String>	iter;
    String		thisValue;
    String		otherValue;

    keys = new HashSet<String>();
    keys.addAll(stringPropertyNames());
    keys.addAll(other.stringPropertyNames());

    iter = keys.iterator();
    while (iter.hasNext()) {
      key = iter.next();

      // merge
      if (hasKey(key) && other.hasKey(key)) {
	thisValue  = getProperty(key);
	otherValue = other.getProperty(key);
	if (!thisValue.equals(otherValue)) {
	  if (thisValue.length() == 0)
	    setProperty(key, otherValue);
	  else if (otherValue.length() == 0)
	    setProperty(key, thisValue);
	  else
	    setProperty(key, thisValue + "," + otherValue);
	}
      }
      // add
      else if (other.hasKey(key)) {
	setProperty(key, other.getProperty(key));
      }
    }
  }

  /**
   * Removes all properties that share this prefix.
   *
   * @param prefix	the prefix to look for
   */
  public void removeWithPrefix(String prefix) {
    Enumeration<String>	enm;
    String		key;

    enm = (Enumeration<String>) propertyNames();
    while (enm.hasMoreElements()) {
      key = enm.nextElement();
      if (key.startsWith(prefix))
	remove(key);
    }
  }

  /**
   * Removes all properties that share this suffix.
   *
   * @param suffix	the suffix to look for
   */
  public void removeWithSuffix(String suffix) {
    Enumeration<String>	enm;
    String		key;

    enm = (Enumeration<String>) propertyNames();
    while (enm.hasMoreElements()) {
      key = enm.nextElement();
      if (key.endsWith(suffix))
	remove(key);
    }
  }

  /**
   * Collapses all the inherited and current properties into a single Properties
   * object and returns it.
   *
   * @return		the collapsed version of this Properties object
   */
  public Properties collapse() {
    Properties		result;
    Enumeration<String>	keys;
    String		key;

    result = new Properties();
    keys   = (Enumeration<String>) propertyNames();
    while (keys.hasMoreElements()) {
      key = keys.nextElement();
      result.setProperty(key, getProperty(key));
    }

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(Object o) {
    int			result;
    Properties		props;
    Enumeration<String>	enm;
    String		key;

    if (o == null)
      return 1;

    if (!(o instanceof Properties))
      return -1;

    props = (Properties) o;

    if (!keySetAll().containsAll(props.keySetAll()))
      result = -1;
    else if (!props.keySetAll().containsAll(keySetAll()))
      result = +1;
    else
      result = 0;

    if (result == 0) {
      enm = (Enumeration<String>) propertyNames();
      while (enm.hasMoreElements()) {
	key    = enm.nextElement();
	result = getProperty(key).compareTo(props.getProperty(key));
	if (result != 0)
	  break;
      }
    }

    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return (compareTo(obj) == 0);
  }

  /**
   * Re-generates a properties object that was commented out.
   *
   * @param comments	the commented out properties content
   * @return		the properties object, or null in case of an error
   * @see		#toComment()
   */
  public static Properties fromComment(String comments) {
    Properties		result;
    StringReader	reader;

    result = new Properties();
    reader = new StringReader(Utils.unComment(comments.toString(), Properties.COMMENT));
    try {
      result.load(reader);
    }
    catch (Exception e) {
      System.err.println("Error obtaining properties from comments:");
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Only for testing. Exepects the name of a props file as first parameter,
   * tries to load it and then dumps the content to stdout.
   *
   * @param args	the commandline arguments
   * @throws Exception	if an error occurs
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    Properties props = new Properties();
    if (args.length > 0)
      props = read(args[0], Environment.getInstance().getDirectories(args[0]));
    Enumeration<String> names = (Enumeration<String>) props.propertyNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      System.out.println(name + ": " + props.getProperty(name));
    }

    Properties clone = (Properties) props.clone();
    Properties empty = new Properties();
    System.out.println("original == clone? " + props.equals(clone));
    System.out.println("original == empty? " + props.equals(empty));
  }
}
