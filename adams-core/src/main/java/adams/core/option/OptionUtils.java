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
 * OptionUtils.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.db.DatabaseConnectionEstablisher;
import adams.db.DatabaseConnectionHandler;
import adams.env.Environment;
import adams.env.OptionsDefinition;
import adams.gui.goe.CustomStringRepresentationHandler;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * A helper class for option-related things.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionUtils {

  /** the name of the props file. */
  public final static String FILENAME = "Option.props";

  /** the prefix for a regular option, instead of a hook. */
  public final static String OPTION_PREFIX = "option.";

  /** for caching the property descriptors. */
  protected static Hashtable<String,PropertyDescriptor> m_PropertyDescriptorCache;
  static {
    m_PropertyDescriptorCache = new Hashtable<String,PropertyDescriptor>();
  }

  /** the hooks for "valueOf". A hook takes a class and a string as parameter. */
  protected static Hashtable<Class,Method> m_HooksValueOf;

  /** the hooks for "toString". A hook takes a class and the object as
   * parameter. */
  protected static Hashtable<Class,Method> m_HooksToString;

  /** whether the hooks got already registered. */
  protected static boolean m_HooksRegistered;

  static {
    m_HooksValueOf    = new Hashtable<Class,Method>();
    m_HooksToString   = new Hashtable<Class,Method>();
    m_HooksRegistered = false;
  }

  /** the properties in use. */
  protected static Properties m_Properties;

  /** whether debugging is on. */
  protected static Boolean m_Debug;

  /** whether to suppress default options (toArray/toString). */
  protected static Boolean m_SuppressDefaultValues;

  /**
   * Adds the given hook for converting strings into objects (low-level).
   *
   * @param key			the superclass to store the hook method under
   * @param method		the hook method
   */
  public static void addValueOfHook(Class key, Method method) {
    m_HooksValueOf.put(key, method);
  }

  /**
   * Adds the given hook for converting strings into objects, automatically
   * adds the handling of arrays.
   *
   * @param key			the superclass to store the hook method under
   * @param hookCls		the class containing the the hook method
   * @param hookMethod		the name of the hook method
   */
  public static void addValueOfHook(Class key, Class hookCls, String hookMethod) {
    Class	keyArray;

    try {
      // base class
      addValueOfHook(
	  key,
	  hookCls.getMethod(hookMethod, new Class[]{AbstractOption.class, String.class}));

      // add array class
      keyArray = Array.newInstance(key, 1).getClass();
      addValueOfHook(
	  keyArray,
	  hookCls.getMethod(hookMethod, new Class[]{AbstractOption.class, String.class}));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the "valueOf" hook for the specified class.
   *
   * @param key		the class to get the hook for
   * @return		the hook, or null if none available
   */
  public static Method getValueOfHook(Class key) {
    return m_HooksValueOf.get(key);
  }

  /**
   * Adds the given hook for converting objects into strings (low-level).
   *
   * @param key			the superclass to store the hook method under
   * @param method		the hook method
   */
  public static void addToStringHook(Class key, Method method) {
    m_HooksToString.put(key, method);
  }

  /**
   * Adds the given hook for converting objects into strings (low-level).
   *
   * @param key			the superclass to store the hook method under
   * @param hookCls		the class containing the the hook method
   * @param hookMethod		the name of the hook method
   */
  public static void addToStringHook(Class key, Class hookCls, String hookMethod) {
    Class	keyArray;

    try {
      // base class
      addToStringHook(
	  key,
	  hookCls.getMethod(hookMethod, new Class[]{AbstractOption.class, Object.class}));

      // add array class
      keyArray = Array.newInstance(key, 1).getClass();
      addToStringHook(
	  keyArray,
	  hookCls.getMethod(hookMethod, new Class[]{AbstractOption.class, Object.class}));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the "toString" hook for the specified class.
   *
   * @param key		the class to get the hook for
   * @return		the hook, or null if none available
   */
  public static Method getToStringHook(Class key) {
    return m_HooksToString.get(key);
  }

  /**
   * Returns the properties file with the custom editors.
   *
   * @return		the props file
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(OptionsDefinition.KEY);

    return m_Properties;
  }

  /**
   * Returns whether default values should be suppressed in toArray/toNested.
   *
   * @return		true if default values suppressed
   */
  public static synchronized boolean getSuppressDefaultValues() {
    if (m_SuppressDefaultValues == null)
      m_SuppressDefaultValues = getProperties().getBoolean(OPTION_PREFIX + "SuppressDefaultValues", true);

    return m_SuppressDefaultValues;
  }

  /**
   * Returns whether to output debugging information.
   *
   * @return		true if debugging output is on
   */
  public static synchronized boolean getDebug() {
    if (m_Debug == null)
      m_Debug = getProperties().getBoolean(OPTION_PREFIX + "Debug", false);

    return m_Debug;
  }

  /**
   * Registers all hooks.
   */
  public static synchronized void registerCustomHooks() {
    Properties 		props;
    Enumeration		enm;
    String		classname;
    Class		cls;
    Class		clsHook;
    String		hookToString;
    String		hookValueOf;
    boolean		enabled;

    if (m_HooksRegistered)
      return;

    m_HooksRegistered = true;
    props             = getProperties();
    enm               = props.propertyNames();
    while (enm.hasMoreElements()) {
      classname = (String) enm.nextElement();
      if (classname.indexOf("#") > -1)
	continue;
      if (classname.startsWith(OPTION_PREFIX))
	continue;
      enabled = props.getBoolean(classname);
      if (!enabled)
	continue;

      // obtain class
      cls = null;
      try {
	cls = Class.forName(classname);
      }
      catch (Exception e) {
	e.printStackTrace();
	System.err.println("Cannot get class for '" + classname + "' - skipped!");
	continue;
      }

      // get hook methods
      hookToString = props.getProperty(classname + "#toString");
      if (hookToString == null) {
	System.err.println("No 'toString' hook method for '" + classname + "' - skipped!");
	continue;
      }

      hookValueOf = props.getProperty(classname + "#valueOf");
      if (hookValueOf == null) {
	System.err.println("No 'valueOf' hook method for '" + classname + "' - skipped!");
	continue;
      }

      // register hooks
      try {
	clsHook = Class.forName(hookToString.replaceAll("#.*", ""));
	addToStringHook(cls, clsHook, hookToString.replaceAll(".*#", ""));
      }
      catch (Exception e) {
	e.printStackTrace();
	System.err.println("Error registering hook '" + hookToString + "'!");
	continue;
      }

      try {
	clsHook = Class.forName(hookValueOf.replaceAll("#.*", ""));
	addValueOfHook(cls, clsHook, hookValueOf.replaceAll(".*#", ""));
      }
      catch (Exception e) {
	e.printStackTrace();
	System.err.println("Error registering hook '" + hookValueOf + "'!");
	continue;
      }
    }
  }

  /**
   * Split up a string containing options into an array of strings,
   * one for each option.
   *
   * @param 		quotedOptionString the string containing the options
   * @return 		the array of options
   * @throws Exception 	in case of an unterminated string, unknown character or
   * 			a parse error
   */
  public static String[] splitOptions(String quotedOptionString) throws Exception{
    List<String> 	result;
    StringBuilder 	str;
    int 		i;
    String 		optStr;

    result = new ArrayList<String>();
    str    = new StringBuilder(quotedOptionString);

    while (true) {
      // trimLeft
      i = 0;
      while ((i < str.length()) && (Character.isWhitespace(str.charAt(i))))
	i++;
      str = str.delete(0, i);

      // stop when str is empty
      if (str.length() == 0)
	break;

      // if str start with a double quote
      if (str.charAt(0) == '"') {
	// find the first not anti-slashed double quote
	i = 1;
	while (i < str.length()) {
	  if (str.charAt(i) == str.charAt(0))
	    break;
	  if (str.charAt(i) == '\\') {
	    i += 1;
	    if (i >= str.length())
	      throw new Exception("String should not finish with \\");
	  }
	  i += 1;
	}

	if (i >= str.length())
	  throw new Exception("Quote parse error.");

	// add the found string to the option vector (without quotes)
	optStr = str.substring(1, i);
	optStr = Utils.unbackQuoteChars(optStr);
	result.add(optStr);
	str = str.delete(0, i+1);
      }
      else {
	// find first whiteSpace
	i = 0;
	while ((i < str.length()) && (!Character.isWhitespace(str.charAt(i))))
	  i++;

	// add the found string to the option vector
	optStr = str.substring(0, i);
	result.add(optStr);
	str = str.delete(0, i);
      }
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Joins all the options in an option array into a single string,
   * as might be used on the command line.
   *
   * @param optionArray the array of options
   * @return the string containing all options.
   */
  public static String joinOptions(String[] optionArray) {
    boolean 		escape;
    StringBuilder 	optionString;
    int			i;
    int			n;

    optionString = new StringBuilder();
    for (i = 0; i < optionArray.length; i++) {
      escape = false;

      // an invalid option encountered?
      if (optionArray[i] == null)
	continue;

      if (optionArray[i].equals(""))
	escape = true;

      for (n = 0; n < optionArray[i].length(); n++) {
        if ( Character.isWhitespace(optionArray[i].charAt(n))
          || (optionArray[i].charAt(n) == '"')
          || (optionArray[i].charAt(n) == '\'') ) {
	  escape = true;
	  break;
	}
      }

      if (escape)
	optionString.append('"' + Utils.backQuoteChars(optionArray[i]) + '"');
      else
	optionString.append(optionArray[i]);

      optionString.append(" ");
    }
    return optionString.toString().trim();
  }

  /**
   * Returns the classname and, if the object is an option handler, the
   * options as a single string.
   *
   * @param obj		the handler to turn into a string
   * @return		the generated string
   * @see		#getShortCommandLine(Object)
   */
  public static String getCommandLine(Object obj) {
    String			result;
    AbstractCommandLineHandler	handler;

    handler = AbstractCommandLineHandler.getHandler(obj);
    result  = handler.toCommandLine(obj);
    result  = result.trim();

    return result;
  }

  /**
   * Returns the classname and, if the object is an option handler, the
   * options as a single string. Shortened version.
   *
   * @param obj		the handler to turn into a string
   * @return		the generated string
   * @see		#getCommandLine(Object)
   */
  public static String getShortCommandLine(Object obj) {
    String			result;
    AbstractCommandLineHandler	handler;

    handler = AbstractCommandLineHandler.getHandler(obj);
    result  = handler.toShortCommandLine(obj);
    result  = result.trim();

    return result;
  }

  /**
   * Returns the options as array if the object in an option handler.
   *
   * @param obj		the handler to get the options array for
   * @return		the generated string
   */
  public static String[] getOptions(Object obj) {
    return getOptions(obj, false);
  }

  /**
   * Returns the options as array if the object in an option handler.
   *
   * @param obj		the handler to get the options array for
   * @param addClass	whether to add the classname as well
   * @return		the generated string
   */
  public static String[] getOptions(Object obj, boolean addClass) {
    String[]			result;
    String[]			newArray;
    AbstractCommandLineHandler	handler;

    handler = AbstractCommandLineHandler.getHandler(obj);
    result  = handler.getOptions(obj);

    if (addClass) {
      newArray = new String[result.length + 1];
      System.arraycopy(result, 0, newArray, 1, result.length);
      newArray[0] = obj.getClass().getName();
      result      = newArray;
    }

    return result;
  }

  /**
   * Returns an object generated from the string representation. The string
   * representation can either be a commandline or a nested (multi-line)
   * representation.
   *
   * @param classType 	the class that the instantiated object should
   * 			be assignable to -- an exception is thrown if this
   * 			is not the case
   * @param str		the string to use for generating the object
   * @return 		the newly created object, ready for use.
   * @throws Exception 	if the class name is invalid, or if the
   * 			class is not assignable to the desired class type, or
   * 			the options supplied are not acceptable to the object
   */
  public static Object forString(Class classType, String str) throws Exception {
    Object		result;
    NestedConsumer	consumer;

    // multi-line
    if (str.startsWith(NestedProducer.COMMENT) || (str.trim().indexOf('\n') > -1)) {
      consumer = new NestedConsumer();
      consumer.setQuiet(true);
      result = consumer.fromString(str);
      consumer.cleanUp();
    }
    // simple commandline
    else {
      result = forAnyCommandLine(classType, str);
    }

    return result;
  }

  /**
   * Returns an object generated from the string representation loaded from
   * the specified file. The string representation can either be a commandline
   * or a nested (multi-line) representation.
   *
   * @param classType 	the class that the instantiated object should
   * 			be assignable to -- an exception is thrown if this
   * 			is not the case
   * @param file	the file to load the string representation from
   * @return 		the newly created object, ready for use.
   * @throws Exception 	if the class name is invalid, or if the
   * 			class is not assignable to the desired class type, or
   * 			the options supplied are not acceptable to the object,
   *			or failed to load representation from file
   * @see 		#forString(Class, String)
   */
  public static Object fromFile(Class classType, File file) throws Exception {
    List<String>  lines;

    if (!file.exists())
      throw new IllegalArgumentException("File does not exist: " + file);
    if (file.isDirectory())
      throw new IllegalArgumentException("File is pointing to a directory: " + file);

    lines = FileUtils.loadFromFile(file);
    if (lines == null)
      throw new IllegalArgumentException("Failed to read string representation from file: " + file);

    return forString(classType, Utils.flatten(lines, "\n"));
  }

  /**
   * Creates a new instance of an object given its command-line, including
   * class name and (optional) arguments to pass to its setOptions method.
   * If the object implements OptionHandler and the options parameter is
   * non-null, the object will have its options set.
   * NB: works for WEKA and ADAMS option handlers, but is also slower.
   *
   * @param classType 	the class that the instantiated object should
   * 			be assignable to -- an exception is thrown if this
   * 			is not the case
   * @param cmdline 	the fully qualified class name and the (optional)
   * 			options of the object
   * @return 		the newly created object, ready for use.
   * @throws Exception 	if the class name is invalid, or if the
   * 			class is not assignable to the desired class type, or
   * 			the options supplied are not acceptable to the object
   * @see		#forCommandLine(Class, String)
   */
  public static Object forAnyCommandLine(Class classType, String cmdline) throws Exception {
    String[]	options;
    String	classname;

    if (cmdline.trim().length() == 0)
      throw new IllegalArgumentException("Empty commandline supplied!");
    
    options    = splitOptions(cmdline);
    classname  = options[0];
    options[0] = "";

    return forName(classType, classname, options);
  }

  /**
   * Creates a new instance of an option handler given its command-line, including
   * class name and (optional) arguments to pass to its setOptions method.
   * NB: works only with ADAMS option handlers.
   *
   * @param classType 	the class that the instantiated object should
   * 			be assignable to -- an exception is thrown if this
   * 			is not the case
   * @param cmdline 	the fully qualified class name and the (optional)
   * 			options of the object
   * @return 		the newly created object, ready for use.
   * @throws Exception 	if the class name is invalid, or if the
   * 			class is not assignable to the desired class type, or
   * 			the options supplied are not acceptable to the object
   * @see		#forAnyCommandLine(Class, String)
   */
  public static OptionHandler forCommandLine(Class classType, String cmdline) throws Exception {
    OptionHandler	result;

    if (cmdline.trim().length() == 0)
      throw new IllegalArgumentException("Empty commandline supplied!");

    result = AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
    if (result == null)
      throw new Exception("Failed to instantiate object of type '" + classType.getName() + "' from '" + cmdline + "! Class not present?");

    if (!classType.isAssignableFrom(result.getClass()))
      throw new Exception(classType.getName() + " is not assignable from " + result.getClass().getName());

    return result;
  }

  /**
   * Creates a new instance of an object given its class name and
   * (optional) arguments to pass to its setOptions method. If the
   * object implements OptionHandler and the options parameter is
   * non-null, the object will have its options set.
   *
   * @param classType 	the class that the instantiated object should
   * 			be assignable to -- an exception is thrown if this
   * 			is not the case
   * @param className 	the fully qualified class name of the object
   * @param options 	an array of options suitable for passing to setOptions.
   * 			May be null.
   * @return 		the newly created object, ready for use.
   * @throws Exception 	if the class name is invalid, or if the
   * 			class is not assignable to the desired class type, or
   * 			the options supplied are not acceptable to the object
   */
  public static Object forName(Class classType, String className, String[] options) throws Exception {
    Class 			cls;
    Object 			result;
    AbstractCommandLineHandler	handler;

    cls = null;
    try {
      cls = Class.forName(className);
    }
    catch (Exception ex) {
      throw new Exception("Can't find class called: " + className);
    }
    if (!classType.isAssignableFrom(cls))
      throw new Exception(classType.getName() + " is not assignable from " + className);

    result  = cls.newInstance();
    if (options != null) {
      handler = AbstractCommandLineHandler.getHandler(result);
      handler.setOptions(result, options);
    }

    // establish database connection
    if (result instanceof DatabaseConnectionEstablisher)
      ((DatabaseConnectionEstablisher) result).establishDatabaseConnection();

    return result;
  }

  /**
   * Returns the string value converted into the appropriate class.
   *
   * @param cls		the required class
   * @return		the instantiated object
   * @throws Exception	if conversion goes wrong or object cannot be instantiated
   */
  public static Object valueOf(Class cls, String value) throws Exception {
    Object			result;
    PropertyEditor		editor;
    AbstractCommandLineHandler	handler;
    boolean isDouble;

    result   = null;
    isDouble = Utils.isDouble(value);

    if ((cls == Byte.class) || (cls == Byte.TYPE))
      result = isDouble ? new Double(value).byteValue() : Byte.valueOf(value);
    // short
    else if ((cls == Short.class) || (cls == Short.TYPE))
      result = isDouble ? new Double(value).shortValue() : Short.valueOf(value);
    // int
    else if ((cls == Integer.class) || (cls == Integer.TYPE))
      result = isDouble ? new Double(value).intValue() : Integer.valueOf(value);
    // long
    else if ((cls == Long.class) || (cls == Long.TYPE))
      result = isDouble ? new Double(value).longValue() : Long.valueOf(value);
    // float
    else if ((cls == Float.class) || (cls == Float.TYPE))
      result = Float.valueOf(value);
    // double
    else if ((cls == Double.class) || (cls == Double.TYPE))
      result = Double.valueOf(value);
    // boolean
    else if ((cls == Boolean.class) || (cls == Boolean.TYPE))
      result = Boolean.valueOf(value);
    // character
    else if ((cls == Character.class) || (cls == Character.TYPE))
      result = "" + value;
    // string
    else if (cls == String.class)
      result = new String(value);

    // custom editor registered (string representation)?
    if (result == null) {
      editor = PropertyEditorManager.findEditor(cls);
      if (editor instanceof CustomStringRepresentationHandler) {
	editor.setValue(cls.newInstance());
	result = ((CustomStringRepresentationHandler) editor).fromCustomStringRepresentation(value);
      }
    }

    // option handler?
    if (result == null) {
      try {
	handler = AbstractCommandLineHandler.getHandler(cls);
	result  = handler.fromCommandLine(value);
      }
      catch (Exception e) {
	result = null;
      }
    }

    // unhandled
    if (result == null)
      throw new IllegalStateException("Unhandled class: " + cls.getName());

    return result;
  }

  /**
   * Creates an OptionHandler object with the same options as the specified one.
   * Also transfers the database connection object, if the object implements
   * the DatabaseConnectionHandler interface.
   *
   * @param o		the template
   * @param expand	whether to expand variables to their current value
   * 			instead of using the placeholders
   * @return		the copy with the same options, null in case of an error
   * @see		DatabaseConnectionHandler
   */
  public static OptionHandler shallowCopy(OptionHandler o, boolean expand) {
    OptionHandler	result;
    NestedProducer	producer;
    NestedConsumer	consumer;

    try {
      producer = new NestedProducer();
      producer.setOutputVariableValues(expand);
      producer.produce(o);
      consumer = new NestedConsumer();
      consumer.setInput(producer.getOutput());
      result = consumer.consume();
      producer.cleanUp();
      consumer.cleanUp();

      // transfer DB connection
      if (o instanceof DatabaseConnectionHandler) {
	((DatabaseConnectionHandler) result).setDatabaseConnection(
	    ((DatabaseConnectionHandler) o).getDatabaseConnection());
      }

      // transfer variables
      if (!expand) {
        result.getOptionManager().setVariables(o.getOptionManager().getVariables());
        result.getOptionManager().updateVariablesInstance(o.getOptionManager().getVariables());
        result.getOptionManager().updateVariableValues(true);
      }
    }
    catch (Exception e) {
      System.err.println("Failed to create shallow copy for OptionHandler!");
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Attempts to create an object object with the same options as the specified one.
   * Works for objects implementing {@link OptionHandler} and other command-line
   * handling objects.
   *
   * @param o		the object
   * @return		the copy with the same options, null in case of an error
   * @see		#getCommandLine(Object)
   * @see		#forCommandLine(Class, String)
   * @see		#forAnyCommandLine(Class, String)
   */
  public static Object shallowCopy(Object o) {
    Object	result;
    String	cmdline;
    
    try {
      cmdline = getCommandLine(o);
      if (o instanceof OptionHandler)
        result = forCommandLine(Object.class, cmdline);
      else
        result = forAnyCommandLine(Object.class, cmdline);
    }
    catch (Exception e) {
      result = null;
      System.err.println("Failed to create shallow copy for object!");
      e.printStackTrace();
    }
    
    return result;
  }

  /**
   * Checks whether are any options left over and returns a string listing them.
   *
   * @param options	the options array to check for unparsed options
   * @return		null if no options found, otherwise listing the
   * 			unparsed options
   */
  public static String checkRemainingOptions(String[] options) {
    StringBuilder	result;
    int			i;
    String		tmp;

    result = new StringBuilder();

    for (i = 0; i < options.length; i++) {
      if (options[i].length() > 0) {
        if (result.length() > 0)
          result.append(" ");
        result.append(options[i]);
      }
    }

    // do we have left-over "\"
    // (from 'carry-overs' for single-line commands spread over multiple lines)
    if (result.length() > 0) {
      tmp = result.toString().replace("\\", "").replace(" ", "");
      if (tmp.length() == 0)
	result = new StringBuilder();
    }

    if (result.length() == 0)
      return null;
    else
      return "Unparsed options found:\n" + result.toString();
  }

  /**
   * Checks whether help was requested (either -h or -help).
   *
   * @param options	the options to check
   * @return		true if a help flag is among the options
   */
  public static boolean helpRequested(String[] options) {
    return helpRequested(new ArrayList<String>(Arrays.asList(options)));
  }

  /**
   * Checks whether help was requested (either -h or -help).
   *
   * @param options	the options to check
   * @return		true if a help flag is among the options
   */
  public static boolean helpRequested(List<String> options) {
    return OptionUtils.hasFlag(options, "-h") || OptionUtils.hasFlag(options, "-help");
  }

  /**
   * Checks whether the flag is in the option string.
   *
   * @param options	the options to check
   * @param flag	the flag to look for (incl. "-")
   * @return		true if a help flag is among the options
   */
  public static boolean hasFlag(String[] options, String flag) {
    return hasFlag(new ArrayList<String>(Arrays.asList(options)), flag);
  }

  /**
   * Checks whether the flag is in the option string.
   *
   * @param options	the options to check
   * @param flag	the flag to look for (incl. "-")
   * @return		true if a help flag is among the options
   */
  public static boolean hasFlag(List<String> options, String flag) {
    boolean	result;
    int		i;

    result = false;

    for (i = 0; i < options.size(); i++) {
      if (options.get(i).equals(flag)) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * Checks whether the flag is in the option string and removes the flag
   * if present.
   *
   * @param options	the options to check
   * @param flag	the flag to look for (incl. "-")
   * @return		true if a help flag is among the options
   */
  public static boolean removeFlag(String[] options, String flag) {
    boolean	result;
    int		i;

    result = false;

    for (i = 0; i < options.length; i++) {
      if (options[i].equals(flag)) {
	options[i] = "";
        result     = true;
        break;
      }
    }

    return result;
  }

  /**
   * Checks whether the flag is in the option string and removes the flag
   * if present.
   *
   * @param options	the options to check
   * @param flag	the flag to look for (incl. "-")
   * @return		true if a help flag is among the options
   */
  public static boolean removeFlag(List<String> options, String flag) {
    boolean	result;
    int		i;

    result = false;

    for (i = 0; i < options.size(); i++) {
      if (options.get(i).equals(flag)) {
	options.set(i, "");
        result     = true;
        break;
      }
    }

    return result;
  }

  /**
   * Returns the arguments of the specified option, if available.
   *
   * @param options	the options to check
   * @param option	the option to look for (incl. "-")
   * @return		the argument of null if not found
   */
  public static String getOption(String[] options, String option) {
    return getOption(new ArrayList<String>(Arrays.asList(options)), option);
  }

  /**
   * Returns the arguments of the specified option, if available.
   *
   * @param options	the options to check
   * @param option	the option to look for (incl. "-")
   * @return		the argument of null if not found
   */
  public static String getOption(List<String> options, String option) {
    String	result;
    int		i;

    result = null;

    for (i = 0; i < options.size(); i++) {
      if (options.get(i).equals(option)) {
        if (i < options.size() - 1)
          result = options.get(i + 1);
        break;
      }
    }

    return result;
  }

  /**
   * Returns the arguments of the specified option, if available, and removes
   * the option and argument from the array.
   *
   * @param options	the options to check
   * @param option	the option to look for (incl. "-")
   * @return		the argument of null if not found
   */
  public static String removeOption(String[] options, String option) {
    String	result;
    int		i;

    result = null;

    for (i = 0; i < options.length; i++) {
      if (options[i].equals(option)) {
        options[i] = "";
        if (i < options.length - 1) {
          result         = options[i + 1];
          options[i + 1] = "";
        }
        break;
      }
    }

    return result;
  }

  /**
   * Returns the arguments of the specified option, if available, and removes
   * the option and argument from the array.
   *
   * @param options	the options to check
   * @param option	the option to look for (incl. "-")
   * @return		the argument of null if not found
   */
  public static String removeOption(List<String> options, String option) {
    String	result;
    int		i;

    result = null;

    for (i = 0; i < options.size(); i++) {
      if (options.get(i).equals(option)) {
        options.set(i, "");
        if (i < options.size() - 1) {
          result = options.get(i + 1);
          options.set(i + 1, "");
        }
        break;
      }
    }

    return result;
  }

  /**
   * Returns a string with all the options listed.
   *
   * @param handler		the option handler which options to list
   * @return			the generated overview
   */
  public static String list(OptionHandler handler) {
    return (String) AbstractOptionProducer.produce(CommandlineHelpProducer.class, handler);
  }

  /**
   * Transfers the option from one {@link OptionHandler} to another.
   *
   * @param source	the object to get the options from
   * @param dest	the object to update with the retrieved options
   * @return		true if options successfully set
   */
  public static boolean transferOptions(OptionHandler source, OptionHandler dest) {
    boolean		result;
    String[]		options;

    try {
      options = ArrayProducer.getOptions(source);
      ArrayConsumer.setOptions(dest, options);
      result = true;
    }
    catch (Exception e) {
      System.err.println("Failed to transfer options from '" + source + "' to '" + dest + "':");
      e.printStackTrace();
      result = false;
    }

    return result;
  }

  /**
   * Checks whether the property descriptors are already available.
   *
   * @param cls		the class to look for
   * @param property	the name of the property to look for
   * @return		true if already cached
   */
  protected static synchronized boolean hasPropertyDescriptor(Class cls, String property) {
    return m_PropertyDescriptorCache.containsKey(cls.getName() + "-" + property);
  }

  /**
   * Returns the property descriptors for the given class/property combination.
   *
   * @param cls		the class to look for
   * @param property	the name of the property to look for
   * @return		the descriptor, null if not found
   */
  protected static synchronized PropertyDescriptor getPropertyDescriptor(Class cls, String property) {
    return m_PropertyDescriptorCache.get(cls.getName() + "-" + property);
  }

  /**
   * Adds the descriptor to the cache.
   *
   * @param cls		the class to add the descriptor for
   * @param property	the name of the property to add the descriptor for
   * @param descriptor	the descriptor to add
   */
  protected static synchronized void addPropertyDescriptor(Class cls, String property, PropertyDescriptor descriptor) {
    m_PropertyDescriptorCache.put(cls.getName() + "-" + property, descriptor);
  }

  /**
   * Returns the bean property descriptor for the get/set methods. Should
   * never be null, unless the property cannot be found in the owner.
   *
   * @param owner	the owner to get the descriptor for
   * @param property	the name of the Java Beans property
   * @return		the bean property descriptor
   */
  public static PropertyDescriptor getDescriptor(OptionHandler owner, String property) {
    PropertyDescriptor		result;
    BeanInfo 			info;
    PropertyDescriptor[]	propDescs;
    MethodDescriptor[]		methDescs;
    Method[]			meths;
    int				i;

    try {
      if (hasPropertyDescriptor(owner.getClass(), property)) {
	result = getPropertyDescriptor(owner.getClass(), property);
      }
      else {
	result = new PropertyDescriptor(property, owner.getClass());
	addPropertyDescriptor(owner.getClass(), property, result);
      }
    }
    catch (Exception e) {
      // can only happen if property name is incorrect
      System.err.println("Error obtaining the property descriptor (" + owner.getClass().getName() + "):");
      e.printStackTrace();
      result = null;

      // some debugging output
      if (getDebug()) {
	// output available properties
	try {
	  info = Introspector.getBeanInfo(owner.getClass());
	  // property descriptors
	  propDescs = info.getPropertyDescriptors();
	  System.err.println(
	      "Available bean properties for class '" + owner.getClass().getName() + "':");
	  for (i = 0; i < propDescs.length; i++)
	    System.err.println((i+1) + ". " + propDescs[i].getDisplayName());
	  // method descriptors
	  methDescs = info.getMethodDescriptors();
	  System.err.println(
	      "Available bean methods for class '" + owner.getClass().getName() + "':");
	  for (i = 0; i < methDescs.length; i++) {
	    System.err.println((i+1) + ". " + methDescs[i].getDisplayName());
	    if (methDescs[i].getDisplayName().equals("_getPyInstance")) {
	      Class cls = methDescs[i].getMethod().getReturnType();
	      Method[] methods = cls.getMethods();
	      for (int n = 0; n < methods.length; n++)
		System.err.println("    " + (n+1) + ". " + methods[n].getName());
	    }
	  }
	  // methods
	  meths = owner.getClass().getMethods();
	  System.err.println(
	      "Available methods for class '" + owner.getClass().getName() + "':");
	  for (i = 0; i < meths.length; i++)
	    System.err.println((i+1) + ". " + meths[i].getName());
	}
	catch (Exception ex) {
	  System.err.println(
	      "Failed to obtain bean info/property descriptors for class '"
	      + owner.getClass().getName() + "'");
	}
      }
    }

    return result;
  }
}
