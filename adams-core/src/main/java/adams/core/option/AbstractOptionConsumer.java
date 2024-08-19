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
 * AbstractOptionConsumer.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.annotation.AnnotationHelper;
import adams.core.classmanager.ClassManager;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.core.management.CharsetHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

/**
 * Sets the option values based on the input data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <C> the type of data to consume
 * @param <V> the type of data used for values
 */
public abstract class AbstractOptionConsumer<C,V>
  extends LoggingObject
  implements OptionConsumer<C,V> {

  /** for serialization. */
  private static final long serialVersionUID = -6229518298821665902L;

  /** the input data. */
  protected C m_Input;

  /** whether to use command-line flags or property names. */
  protected boolean m_UsePropertyNames;

  /** the generated object. */
  protected OptionHandler m_Output;

  /** for storing errors that occurred while consuming the options. */
  protected MessageCollection m_Errors;

  /** for storing warnings that occurred while consuming the options. */
  protected MessageCollection m_Warnings;

  /** top-level properties to skip. */
  protected HashSet<String> m_SkippedProperties;

  /** for caching classname and constructor. */
  protected static Map<String,Constructor> m_ClassnameCache;

  /**
   * Initializes the visitor.
   */
  public AbstractOptionConsumer() {
    super();
    initialize();
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public abstract String globalInfo();

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_LoggingLevel      = LoggingLevel.OFF;
    m_Input             = null;
    m_UsePropertyNames  = false;
    m_Errors            = new MessageCollection();
    m_Warnings          = new MessageCollection();
    m_SkippedProperties = new HashSet<>();
    if (m_ClassnameCache == null)
      m_ClassnameCache = new HashMap<>();
  }

  /**
   * Resets the members.
   */
  protected void reset() {
    m_Output = null;
    m_Errors.clear();
    m_Warnings.clear();
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    m_Logger       = null;
  }

  /**
   * Generates a debug string, e.g., based on the method name.
   * <br><br>
   * Default implementation merely returns the string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String generateLoggingString(String s) {
    return s;
  }

  /**
   * Adds the specified error message to the internal error log.
   *
   * @param msg		the error message to log
   * @see		#hasErrors()
   * @see		#getErrors()
   */
  protected void logError(String msg) {
    m_Errors.add(msg);
  }

  /**
   * Checks whether errors were encountered while consuming the options.
   *
   * @return		true if errors were encountered
   * @see		#getErrors()
   */
  public boolean hasErrors() {
    return !m_Errors.isEmpty();
  }

  /**
   * Returns the error log.
   *
   * @return		the error log, can be empty
   * @see		#logError(String)
   * @see		#hasErrors()
   */
  public MessageCollection getErrors() {
    return m_Errors;
  }

  /**
   * Adds the specified warning message to the internal warning log.
   *
   * @param msg		the warning message to log
   * @see		#hasWarnings()
   * @see		#getWarnings()
   */
  protected void logWarning(String msg) {
    m_Warnings.add(msg);
  }

  /**
   * Checks whether warnings were encountered while consuming the options.
   *
   * @return		true if warnings were encountered
   * @see		#getWarnings()
   */
  public boolean hasWarnings() {
    return !m_Warnings.isEmpty();
  }

  /**
   * Returns the warning log.
   *
   * @return		the warning log, can be empty
   * @see		#logWarning(String)
   * @see		#hasWarnings()
   */
  public MessageCollection getWarnings() {
    return m_Warnings;
  }

  /**
   * Checks the deprecation of the object and logs a warning if that's the
   * case.
   *
   * @param object	the object to check
   */
  protected void checkDeprecation(Object object) {
    if (object != null)
      checkDeprecation(object.getClass());
  }

  /**
   * Checks the deprecation of the object and logs a warning if that's the
   * case.
   *
   * @param cls		the class to check
   */
  protected void checkDeprecation(Class cls) {
    String	msg;

    msg = AnnotationHelper.getDeprecationWarning(cls);
    if (msg != null)
      logWarning(msg);
  }

  /**
   * Sets whether console output is suppressed or not.
   *
   * @param value	if true then console output is suppressed (out/err)
   */
  public void setQuiet(boolean value) {
    getLogger().setLevel(Level.OFF);
  }

  /**
   * Returns whether console output is suppressed or not.
   *
   * @return		true if console output is suppressed
   */
  public boolean isQuiet() {
    return (getLogger().getLevel() == Level.OFF);
  }

  /**
   * Sets the top-level properties to skip.
   *
   * @param value	the properties
   */
  public void setSkippedProperties(HashSet<String> value) {
    m_SkippedProperties = value;
    reset();
  }

  /**
   * Returns the skipped top-level properties.
   *
   * @return		the properties
   */
  public HashSet<String> getSkippedProperties() {
    return m_SkippedProperties;
  }

  /**
   * Returns whether property names are used or just the command-line flags.
   *
   * @return		true if property names are used
   */
  protected boolean getUsePropertyNames() {
    return m_UsePropertyNames;
  }

  /**
   * Returns either the property name or the commandline flag, depending
   * on whether property names are to be used or not.
   *
   * @param option	the option to return the identifier for
   * @return		the identifier
   * @see		#getUsePropertyNames()
   */
  protected String getOptionIdentifier(AbstractOption option) {
    if (getUsePropertyNames())
      return option.getProperty();
    else
      return "-" + option.getCommandline();
  }

  /**
   * Returns the visited top-level object.
   *
   * @return		the visited object
   */
  public OptionHandler getOutput() {
    return m_Output;
  }

  /**
   * Sets the input data to use.
   *
   * @param input	the data to use
   */
  public void setInput(C input) {
    m_Input = input;
  }

  /**
   * Returns the currently set input data.
   *
   * @return		the data in use
   */
  public C getInput() {
    return m_Input;
  }

  /**
   * Converts the input string into the internal format.
   *
   * @param s		the string to process
     * @return		the internal format, null in case of an error
   */
  protected abstract C convertToInput(String s);

  /**
   * Turns the classname into a string.
   *
   * @param classname	the classname
   * @return		the class
   */
  protected Constructor forName(String classname) throws Exception {
    Constructor	result;

    if (m_ClassnameCache.containsKey(classname)) {
      result = m_ClassnameCache.get(classname);
    }
    else {
      result = ClassManager.getSingleton().forName(Conversion.getSingleton().rename(classname)).getConstructor();
      m_ClassnameCache.put(classname, result);
    }

    return result;
  }

  /**
   * Creates the empty option handler from the internal data structure and
   * returns it. This option handler will then be "visited".
   *
   * @return		the generated option handler, null in case of an error
   */
  protected abstract OptionHandler initOutput();

  /**
   * Attempts to return the write method of the option. Outputs an error
   * message in the console if not available.
   *
   * @param option	the option to get the write method for
   * @return		the method, null if not available
   */
  protected Method getWriteMethod(AbstractOption option) {
    Method	result;

    result = option.getWriteMethod();
    if (result == null)
      getLogger().severe("No write method for option '" + getOptionIdentifier(option) + "'!");

    return result;
  }

  /**
   * Processes the specified boolean option.
   *
   * @param option	the boolean option to process
   * @param values	the value for the boolean option
   * @throws Exception	if something goes wrong
   */
  protected abstract void processOption(BooleanOption option, V values) throws Exception;

  /**
   * Processes the specified class option.
   *
   * @param option	the class option to process
   * @param values	the value for the class option
   * @throws Exception	if something goes wrong
   */
  protected abstract void processOption(ClassOption option, V values) throws Exception;

  /**
   * Processes the specified argument option.
   *
   * @param option	the argument option to process
   * @param values	the value for the argument option
   * @throws Exception	if something goes wrong
   */
  protected abstract void processOption(AbstractArgumentOption option, V values) throws Exception;

  /**
   * Processes the specified argument option.
   *
   * @param option	the argument option to process
   * @param values	the value for the argument option
   * @throws Exception	if something goes wrong
   */
  protected void processOption(AbstractOption option, V values) throws Exception {
    // skipped property?
    if (m_SkippedProperties.contains(option.getProperty()))
      return;

    if (option instanceof BooleanOption) {
      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("processOption/boolean") + ": " + getOptionIdentifier(option));
      processOption((BooleanOption) option, values);
    }
    else if (option instanceof ClassOption) {
      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("processOption/class") + ": " + getOptionIdentifier(option));
      processOption((ClassOption) option, values);
    }
    else if (option instanceof AbstractArgumentOption) {
      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("processOption/argument") + ": " + getOptionIdentifier(option));
      processOption((AbstractArgumentOption) option, values);
    }
    else {
      throw new IllegalStateException("Unhandled type of option: " + option.getClass().getName());
    }
  }

  /**
   * Visits the options.
   *
   * @param manager	the manager to visit
   * @param input	the input data to use
   */
  protected abstract void doConsume(OptionManager manager, C input);

  /**
   * Consumes the current input. The generated option handler can be retrieved
   * via getOutput() as well.
   *
   * @return		the created object
   * @see		#getOutput()
   */
  public OptionHandler consume() {
    reset();

    m_Output = initOutput();
    if (isLoggingEnabled())
      getLogger().info(generateLoggingString("consume/initOutput") + ": " + (m_Output == null ? "null" : m_Output.getClass().getName()));

    if (m_Output != null) {
      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("consume/doConsume") + ": " + m_Output.getClass().getName());
      doConsume(m_Output.getOptionManager(), m_Input);
    }

    return getOutput();
  }

  /**
   * Consumes the provided input and updates the provided option handler.
   * The option handler can be retrieved via getOutput() as well.
   *
   * @param output	the option handler to update
   * @param input	the data to use for updating
   * @return		the updated object
   * @see		#getOutput()
   */
  public OptionHandler consume(OptionHandler output, C input) {
    reset();

    if (output == null)
      throw new IllegalArgumentException("OptionHandler cannot be null!");

    m_Output = output;
    if (isLoggingEnabled())
      getLogger().info(generateLoggingString("consume/createObject") + ": " + (m_Output == null ? "null" : m_Output.getClass().getName()));

    if (m_Output != null) {
      if (isLoggingEnabled())
	getLogger().info(generateLoggingString("consume/doConsume") + ": " + m_Output.getClass().getName());
      doConsume(m_Output.getOptionManager(), input);
    }

    return getOutput();
  }

  /**
   * Returns the charset to use. Checks whether consumer implements
   * {@link EncodingSupporter}.
   *
   * @return		the character set
   */
  protected Charset determineCharset() {
    Charset 	result;

    result = null;

    if (this instanceof EncodingSupporter)
      result = ((EncodingSupporter) this).getEncoding().charsetValue();
    if (result == null)
      result = CharsetHelper.getSingleton().getCharset();

    return result;
  }

  /**
   * Processes the specified string.
   *
   * @param s		the string to process
   * @return		the created object
   * @see		#getOutput()
   */
  public OptionHandler fromString(String s) {
    C		content;

    reset();
    content = convertToInput(s);
    if (content != null) {
      setInput(content);
      consume();
    }

    return getOutput();
  }

  /**
   * Processes the specified file.
   *
   * @param file	the file to process
   * @return		the created object, null in case content of file couldn't be loaded
   */
  public OptionHandler fromFile(File file) {
    OptionHandler	result;
    List<String>	lines;
    String		content;

    result = null;

    lines = FileUtils.loadFromFile(file, determineCharset().name());
    if (lines != null) {
      content = Utils.flatten(lines, "\n");
      result  = fromString(content);
    }

    return result;
  }

  /**
   * Reads the option handler from the specified file.
   *
   * @param filename	the file to read from
   * @return		the option handler if successful, null otherwise
   */
  public OptionHandler read(String filename) {
    OptionHandler	result;
    BufferedReader	reader;
    FileInputStream     fis;
    StringBuilder	content;
    String		line;
    String		msg;
    Charset		charset;

    result = null;

    charset = determineCharset();
    reader  = null;
    fis     = null;
    try {
      content = new StringBuilder();
      fis = new FileInputStream(filename);
      if (filename.toLowerCase().endsWith(".gz"))
	reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(fis), charset));
      else
	reader = new BufferedReader(new InputStreamReader(fis, charset));
      while ((line = reader.readLine()) != null) {
	if (content.length() > 0)
	  content.append("\n");
	content.append(line);
      }
      result = fromString(content.toString());
    }
    catch (Exception e) {
      msg = "Failed to read file '" + filename + "': ";
      logError(msg + e);
      getLogger().log(Level.SEVERE, msg, e);
      result = null;
    }
    finally {
      FileUtils.closeQuietly(reader);
      FileUtils.closeQuietly(fis);
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Output = null;
    m_Input         = null;
  }

  /**
   * Uses the specified consumer to parse the given string and return the
   * option handler.
   *
   * @param cls		the consumer class to use
   * @param s		the string to parse
   * @return		the option handler, null in case of an error
   */
  public static OptionHandler fromString(Class<? extends OptionConsumer> cls, String s) {
    return fromString(cls, s, null);
  }

  /**
   * Uses the specified consumer to parse the given string and return the
   * option handler.
   *
   * @param cls		the consumer class to use
   * @param s		the string to parse
   * @param errors 	for storing errors, can be null
   * @return		the option handler, null in case of an error
   */
  public static OptionHandler fromString(Class<? extends OptionConsumer> cls, String s, MessageCollection errors) {
    OptionHandler	result;
    OptionConsumer	consumer;

    result = null;

    try {
      consumer = cls.getDeclaredConstructor().newInstance();
      result   = consumer.fromString(s);
      if (consumer.hasErrors() && (errors != null))
	errors.addAll(consumer.getErrors());
      consumer.cleanUp();
    }
    catch (Exception e) {
      if (errors != null)
	errors.add("Failed to process string: " + s, e);
      System.err.println("Failed to process string: " + s);
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Uses the specified consumer to parse the content of the specified file and
   * return the option handler.
   *
   * @param cls		the consumer class to use
   * @param file	the file to load and parse
   * @return		the option handler, null in case of an error
   */
  public static OptionHandler fromFile(Class<? extends OptionConsumer> cls, File file) {
    OptionHandler	result;
    OptionConsumer	consumer;

    result = null;

    try {
      consumer = cls.getDeclaredConstructor().newInstance();
      result   = consumer.fromFile(file);
      consumer.cleanUp();
    }
    catch (Exception e) {
      System.err.println("Failed to process file: " + file);
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Uses the specified consumer to process the given object and return the
   * option handler.
   *
   * @param cls		the consumer class to use
   * @param input	the data structure to process
   * @return		the option handler, null in case of an error
   */
  public static OptionHandler consume(Class<? extends OptionConsumer> cls, Object input) {
    OptionHandler		result;
    OptionConsumer	consumer;

    result = null;

    try {
      consumer = cls.getDeclaredConstructor().newInstance();
      consumer.setInput(input);
      result   = consumer.consume();
      consumer.cleanUp();
    }
    catch (Exception e) {
      System.err.println("Failed to process object: " + input);
      e.printStackTrace();
    }

    return result;
  }
}
