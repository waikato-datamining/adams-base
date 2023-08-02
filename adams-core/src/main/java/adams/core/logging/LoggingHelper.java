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
 * LoggingHelper.java
 * Copyright (C) 2013-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import adams.core.DateFormat;
import adams.core.management.EnvVar;
import adams.core.option.OptionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Helper class for logging related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LoggingHelper {

  /** the environment variable suffix of the log level to look for. */
  public final static String LOGLEVEL_SUFFIX = ".LOGLEVEL";

  /** the cache for the loglevels. */
  protected static Map<Class,Level> m_LogLevelCache = new HashMap<>();

  /** for comparing levels. */
  protected final static LevelComparator m_LevelComparator = new LevelComparator();

  /** the global logging handler. */
  protected static Handler m_DefaultHandler;

  /** the formatter for the timestamp. */
  protected static DateFormat m_DateFormat;

  /**
   * Gets the level for the specified environment variable. Checks:
   * - as is
   * - dots -> underscores
   * - uppercase
   * - dots -> underscores + uppercase
   *
   * @param env		the environment variable to get the level for, if possible
   * @return		the determined level or null if nothing found or parsable
   */
  protected static Level getLevel(String env) {
    Level	result;
    String	level;

    result = null;
    level  = EnvVar.get(env, null, true, true);
    if (level != null) {
      try {
	result = LoggingLevel.valueOf(level).getLevel();
      }
      catch (Exception e) {
        // ignored
      }
    }

    return result;
  }

  /**
   * Returns the log level for the specified class. E.g., for the class
   * "hello.world.App" the environment variables "hello.world.App.LOGLEVEL"
   * and "App.LOGLEVEL" are inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}"
   * returned. Default is WARNING.
   * Instead of dots, environment variables with underscores are inspected as well (and uppercase), i.e.,
   * "hello_world_App_LOGLEVEL", "App_LOGLEVEL", "HELLO_WORLD_APP_LOGLEVEL", "APP_LOGLEVEL".
   *
   * @param cls		the class to return the debug level for
   * @return		the logging level
   */
  public static Level getLevel(Class cls) {
    return getLevel(cls, Level.WARNING);
  }

  /**
   * Returns the log level for the specified class. E.g., for the class
   * "hello.world.App" the environment variables "hello.world.App.LOGLEVEL"
   * and "App.LOGLEVEL" are inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}"
   * returned.
   * Instead of dots, environment variables with underscores are inspected as well (and uppercase), i.e.,
   * "hello_world_App_LOGLEVEL", "App_LOGLEVEL", "HELLO_WORLD_APP_LOGLEVEL", "APP_LOGLEVEL".
   *
   * @param cls		the class to return the debug level for
   * @param defLevel	the default level to use
   * @return		the logging level
   */
  public static Level getLevel(Class cls, Level defLevel) {
    Level	result;

    if (m_LogLevelCache.containsKey(cls)) {
      result = m_LogLevelCache.get(cls);
    }
    else {
      result = getLevel(cls.getName() + LOGLEVEL_SUFFIX);
      if (result == null)
	result = getLevel(cls.getSimpleName() + LOGLEVEL_SUFFIX);
      if (result == null)
	result = defLevel;
      m_LogLevelCache.put(cls, result);
    }

    return result;
  }

  /**
   * Returns the logging level for the specified class. E.g., for the class
   * "hello.world.App" the environment variables "hello.world.App.LOGLEVEL"
   * and "App.LOGLEVEL" are inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}"
   * returned. Default is WARNING.
   *
   * @param cls		the class to return the debug level for
   * @return		the logging level
   */
  public static LoggingLevel getLoggingLevel(Class cls) {
    return getLoggingLevel(cls, LoggingLevel.WARNING);
  }

  /**
   * Returns the logging level for the specified class. E.g., for the class
   * "hello.world.App" the environment variables "hello.world.App.LOGLEVEL"
   * and "App.LOGLEVEL" are inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}"
   * returned.
   *
   * @param cls		the class to return the debug level for
   * @param defLevel 	the default level
   * @return		the logging level
   */
  public static LoggingLevel getLoggingLevel(Class cls, LoggingLevel defLevel) {
    LoggingLevel	result;
    Level		level;

    result = defLevel;
    level  = getLevel(cls, defLevel.getLevel());
    for (LoggingLevel l: LoggingLevel.values()) {
      if (l.getLevel() == level) {
	result = l;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the a logger with the log level for the specified class.
   * "hello.world.App" the environment variables "hello.world.App.LOGLEVEL"
   * and "App.LOGLEVEL" are inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}"
   * returned. Default level is WARNING.
   *
   * @param cls		the class to return the logger for
   * @return		the logger
   */
  public static Logger getLogger(Class cls) {
    return getLogger(cls, Level.WARNING);
  }

  /**
   * Returns the a logger with the log level for the specified class. 
   * "hello.world.App" the environment variables "hello.world.App.LOGLEVEL"
   * and "App.LOGLEVEL" are inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}"
   * returned.
   *
   * @param cls		the class to return the logger for
   * @param defLevel 	the default level
   * @return		the logger
   */
  public static Logger getLogger(Class cls, Level defLevel) {
    Logger	result;

    result = Logger.getLogger(cls.getName());
    result.setLevel(getLevel(cls, defLevel));
    result.removeHandler(getDefaultHandler());
    result.addHandler(getDefaultHandler());
    result.setUseParentHandlers(false);

    return result;
  }

  /**
   * Returns the a logger with the specified name.
   * Default level is WARNING.
   *
   * @param name	the name of the class to return the logger for
   * @return		the logger
   */
  public static Logger getLogger(String name) {
    return getLogger(name, Level.WARNING);
  }

  /**
   * Returns the a logger with the specified name.
   *
   * @param name	the name of the class to return the logger for
   * @param defLevel 	the default level
   * @return		the logger
   */
  public static Logger getLogger(String name, Level defLevel) {
    Logger	result;

    result = Logger.getLogger(name);
    result.setLevel(defLevel);
    result.removeHandler(getDefaultHandler());
    result.addHandler(getDefaultHandler());
    result.setUseParentHandlers(false);

    return result;
  }

  /**
   * Returns a console logger with the log level for the specified class.
   * E.g., for the class "hello.world.App" the environment variables "hello.world.App.LOGLEVEL"
   * and "App.LOGLEVEL" are inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}"
   * returned. Default level is WARNING.
   *
   * @param cls		the class to return the logger for
   * @return		the logger
   */
  public static Logger getConsoleLogger(Class cls) {
    return getConsoleLogger(cls, Level.WARNING);
  }

  /**
   * Returns a console logger with the log level for the specified class. 
   * E.g., for the class "hello.world.App" the environment variables "hello.world.App.LOGLEVEL"
   * and "App.LOGLEVEL" are inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}"
   * returned.
   *
   * @param cls		the class to return the logger for
   * @param defLevel 	the default level
   * @return		the logger
   */
  public static Logger getConsoleLogger(Class cls, Level defLevel) {
    Logger	result;

    result = Logger.getLogger(cls.getName());
    result.setLevel(getLevel(cls, defLevel));
    result.addHandler(new SimpleConsoleHandler());
    result.setUseParentHandlers(false);

    return result;
  }

  /**
   * Returns a console logger with the log level for the specified class.
   * Default level is WARNING.
   *
   * @param name	the name of the class to return the logger for
   * @return		the logger
   */
  public static Logger getConsoleLogger(String name) {
    return getConsoleLogger(name, Level.WARNING);
  }

  /**
   * Returns a console logger with the log level for the specified class.
   *
   * @param name	the name of the class to return the logger for
   * @param defLevel 	the default level
   * @return		the logger
   */
  public static Logger getConsoleLogger(String name, Level defLevel) {
    Logger	result;

    result = Logger.getLogger(name);
    result.setLevel(defLevel);
    result.addHandler(new SimpleConsoleHandler());
    result.setUseParentHandlers(false);

    return result;
  }

  /**
   * Checks whether the logger has at least the specified level set.
   *
   * @param logger	the logger to check
   * @param levelMin	the minimum level to meet
   * @return		if minimum logging level met
   */
  public static boolean isAtLeast(Logger logger, Level levelMin) {
    return isAtLeast(logger.getLevel(), levelMin);
  }

  /**
   * Checks whether the level meets the minimum.
   *
   * @param level	the level to check
   * @param levelMin	the minimum level to meet
   * @return		if minimum logging level met
   */
  public static boolean isAtLeast(Level level, Level levelMin) {
    return (m_LevelComparator.compare(level, levelMin) >= 0);
  }

  /**
   * Checks whether the logger has at most the specified level set.
   *
   * @param logger	the logger to check
   * @param levelMax	the maximum level to meet
   * @return		if maximum logging level met
   */
  public static boolean isAtMost(Logger logger, Level levelMax) {
    return isAtMost(logger.getLevel(), levelMax);
  }

  /**
   * Checks whether the level is at most the specified maximum level.
   *
   * @param level	the level to check
   * @param levelMax	the maximum level to meet
   * @return		if maximum logging level met
   */
  public static boolean isAtMost(Level level, Level levelMax) {
    return (m_LevelComparator.compare(level, levelMax) <= 0);
  }

  /**
   * Sets the handler to use for logging.
   *
   * @param value	the handler
   */
  public static synchronized void setDefaultHandler(Handler value) {
    Handler			old;
    LogManager			manager;
    Enumeration<String>		names;
    String			name;
    Logger			logger;
    HashSet<LoggingListener>	listeners;

    old       = m_DefaultHandler;
    listeners = new HashSet<>();
    if (old instanceof AbstractLogHandler)
      listeners.addAll(((AbstractLogHandler) old).loggingListeners());
    m_DefaultHandler = value;
    if (listeners.size() > 0) {
      if (m_DefaultHandler instanceof AbstractLogHandler) {
	for (LoggingListener l: listeners)
	  ((AbstractLogHandler) m_DefaultHandler).addLoggingListener(l);
      }
      ((AbstractLogHandler) old).removeLoggingListeners();
    }
    manager = LogManager.getLogManager();
    names   = manager.getLoggerNames();
    while (names.hasMoreElements()) {
      name   = names.nextElement();
      logger = manager.getLogger(name);
      if (logger == null)
	continue;
      logger.removeHandler(old);
      logger.removeHandler(m_DefaultHandler);
      logger.addHandler(m_DefaultHandler);
    }
  }

  /**
   * Returns the current log handler.
   *
   * @return		the handler
   */
  public static synchronized Handler getDefaultHandler() {
    if (m_DefaultHandler == null) {
      m_DefaultHandler = new MultiHandler();
      ((MultiHandler) m_DefaultHandler).addHandler(new SimpleConsoleHandler());
    }
    return m_DefaultHandler;
  }

  /**
   * Determines the index of the handler in the default handler.
   *
   * @param handler	the handler to look for
   * @return		the index, -1 if not found
   * @see		#getDefaultHandler()
   */
  public static int indexOfDefaultHandler(Handler handler) {
    int			result;
    MultiHandler	multi;
    Handler		h;
    int			i;

    result = -1;

    if (getDefaultHandler() instanceof MultiHandler) {
      multi = (MultiHandler) LoggingHelper.getDefaultHandler();
      for (i = 0; i < multi.getHandlers().length; i++) {
	h = multi.getHandlers()[i];
	if (h.equals(handler)) {
	  result = i;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Adds the handler to the default handler, but only if not already present.
   *
   * @return		null if successful, otherwise error message
   * @see		#getDefaultHandler()
   */
  public static String addToDefaultHandler(Handler handler) {
    String		result;
    MultiHandler	multi;

    result = null;

    if (getDefaultHandler() instanceof MultiHandler) {
      multi = (MultiHandler) getDefaultHandler();
      if (indexOfDefaultHandler(handler) == -1)
	multi.addHandler(handler);
    }
    else {
      result = "Default logging handler is not of type " + MultiHandler.class.getName() + " - failed to install " + handler.getClass().getName() + "!";
    }

    return result;
  }

  /**
   * Removes the handler from the default handler. Calls the 'close()' method
   * of the handler if found.
   *
   * @return		null if successful, otherwise error message
   * @see		#getDefaultHandler()
   */
  public static String removeFromDefaultHandler(Handler handler) {
    String		result;
    MultiHandler	multi;
    int			index;

    result = null;

    if (getDefaultHandler() instanceof MultiHandler) {
      multi = (MultiHandler) LoggingHelper.getDefaultHandler();
      index = indexOfDefaultHandler(handler);
      if (index > -1) {
	multi.getHandlers()[index].close();
	multi.removeHandler(index);
      }
    }
    else {
      result = "Default logging handler is not of type " + MultiHandler.class.getName() + " - failed to remove " + handler.getClass().getName() + "!";
    }

    return result;
  }

  /**
   * Wraps the default handler in the provided handler, but only if not already wrapped.
   *
   * From:
   * <pre>
   * MultiHandler
   * - handler1
   * - handler2
   * - ...
   * - handlerN
   * </pre>
   *
   * To:
   * <pre>
   * MultiHandler
   * - wrapper
   *    - MultiHandlerInner
   *      - handler1
   *      - handler2
   *      - ...
   *      - handlerN
   * </pre>
   *
   * @param wrapper	the handler to use for wrapping
   * @return		null if successful, otherwise error message
   * @see		#getDefaultHandler()
   */
  public static String wrapDefaultHandler(EnhancingSingleHandler wrapper) {
    String		result;
    MultiHandler	multi;
    MultiHandler 	multiInner;
    boolean		canWrap;

    result = null;

    if (getDefaultHandler() instanceof MultiHandler) {
      canWrap = false;
      multi = (MultiHandler) LoggingHelper.getDefaultHandler();
      if (multi.getHandlers().length != 1)
	canWrap = true;
      if ((multi.getHandlers().length == 1) && !multi.getHandlers()[0].equals(wrapper))
	canWrap = true;
      if (canWrap) {
	multiInner = new MultiHandler();
	multiInner.setHandlers(multi.getHandlers());
	wrapper.setHandler(multiInner);
	multi.setHandlers(new Handler[]{(Handler) wrapper});
      }
    }
    else {
      result = "Default logging handler is not of type " + MultiHandler.class.getName() + " - failed to wrap with " + wrapper.getClass().getName() + "!";
    }

    return result;
  }

  /**
   * Removes the layer introduced by the provided handler in the default handler, but only if wrapped.
   *
   * From:
   * <pre>
   * MultiHandler
   * - wrapper
   *    - MultiHandlerInner
   *      - handler1
   *      - handler2
   *      - ...
   *      - handlerN
   * </pre>
   *
   * To:
   * <pre>
   * MultiHandler
   * - handler1
   * - handler2
   * - ...
   * - handlerN
   * </pre>
   *
   * @param wrapper	the wrapper handler to remove
   * @return		null if successful, otherwise error message
   * @see		#getDefaultHandler()
   */
  public static String unwrapDefaultHandler(EnhancingSingleHandler wrapper) {
    String		result;
    MultiHandler	multi;
    MultiHandler 	multiInner;
    boolean 		canUnwrap;

    result = null;

    if (getDefaultHandler() instanceof MultiHandler) {
      canUnwrap = false;
      multi = (MultiHandler) LoggingHelper.getDefaultHandler();
      if ((multi.getHandlers().length == 1) && multi.getHandlers()[0].equals(wrapper))
	canUnwrap = true;
      if (canUnwrap) {
	wrapper = (EnhancingSingleHandler) multi.getHandlers()[0];
	multiInner = (MultiHandler) wrapper.getHandler();
	multi.setHandlers(multiInner.getHandlers());
      }
    }
    else {
      result = "Default logging handler is not of type " + MultiHandler.class.getName() + " - failed to unwrap from " + wrapper.getClass().getName() + "!";
    }

    return result;
  }

  /**
   * Interprets the "-logging-handler &lt;classname&gt;" option in the command-line
   * options and sets the logging handler accordingly.
   *
   * @param options	the command-line options
   * @return		true if handler updated
   */
  public static boolean useHandlerFromOptions(String[] options) {
    boolean		result;
    String		classname;
    Handler		handler;
    MultiHandler	multi;

    result = false;

    classname = OptionUtils.removeOption(options, "-logging-handler");
    if (classname != null) {
      try {
	handler = (Handler) Class.forName(classname).getDeclaredConstructor().newInstance();
	multi   = new MultiHandler();
	multi.setHandlers(new Handler[]{handler});
	setDefaultHandler(multi);
	result = true;
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate logging handler: " + classname);
	e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Outputs the handler option definition on {@link System#out}.
   */
  public static void outputHandlerOption() {
    System.out.println("-logging-handler <classname>");
    System.out.println("\t" + "logging handler to use");
    System.out.println("\t" + "default: " + ConsolePanelHandler.class.getName());
  }

  /**
   * Returns the formatter for the timestamps.
   *
   * @return		the formatter
   */
  protected static synchronized DateFormat getDateFormat() {
    if (m_DateFormat == null)
      m_DateFormat = new DateFormat("yyyyMMdd-HHmmss.SSS");
    return m_DateFormat;
  }

  /**
   * Publish a <tt>LogRecord</tt>.
   * <p>
   * The logging request was made initially to a <tt>Logger</tt> object,
   * which initialized the <tt>LogRecord</tt> and forwarded it here.
   * <p>
   * The <tt>Handler</tt>  is responsible for formatting the message, when and
   * if necessary.  The formatting should include localization.
   * <p>
   * "{}" placeholders in the message get replaced with the objects from
   * {@link LogRecord#getParameters()}.
   *
   * @param  record  description of the log event. A null record is
   *                 silently ignored and is not published
   */
  public static StringBuilder assembleMessage(LogRecord record) {
    StringBuilder	result;
    String[]		lines;
    int			i;
    String		suffix;
    String		actualPrefix;
    String		msg;
    String		prefix;

    msg = record.getMessage() + "\n";
    if (record.getParameters() != null) {
      for (Object obj: record.getParameters())
	msg = msg.replace("{}", obj.toString());
    }
    if (record.getThrown() != null)
      msg += throwableToString(record.getThrown()) + "\n";

    result = new StringBuilder();
    lines  = msg.split("\n");
    suffix = "-" + LoggingLevel.valueOf(record.getLevel());
    prefix = record.getLoggerName();

    // any prefix to print?
    if ((prefix != null) && prefix.length() > 0)
      actualPrefix = "[" + prefix + suffix + "/" + getDateFormat().format(new Date()) + "] ";
    else
      actualPrefix = "";

    for (i = 0; i < lines.length; i++) {
      if (i > 0)
	result.append("\n");
      result.append(actualPrefix);
      result.append(lines[i]);
    }

    return result;
  }

  /**
   * Returns the name of the method calling this method.
   *
   * @return		the generated string
   */
  public static String getMethodName() {
    StringBuilder	result;
    StackTraceElement[]	trace;
    StackTraceElement	element;

    result  = new StringBuilder();
    trace   = Thread.currentThread().getStackTrace();
    if (trace.length >= 2) {
      element = trace[2];
      result.append(element.getMethodName());
    }
    else {
      result.append("<unknown>");
    }

    return result.toString();
  }

  /**
   * Returns the name of the method calling this method.
   *
   * @param cls		whether to include the class name
   * @return		the generated string
   */
  public static String getMethodName(boolean cls) {
    StringBuilder	result;
    StackTraceElement[]	trace;
    StackTraceElement	element;

    result  = new StringBuilder();
    trace   = Thread.currentThread().getStackTrace();
    if (trace.length >= 2) {
      element = trace[2];
      if (cls)
	result.append(element.getClassName()).append(".");
      result.append(element.getMethodName());
    }
    else {
      result.append("<unknown>");
    }

    return result.toString();
  }

  /**
   * Returns the name of the method calling this method.
   *
   * @param cls		whether to include the class name
   * @param line	whether to include the line number
   * @return		the generated string
   */
  public static String getMethodName(boolean cls, boolean line) {
    StringBuilder	result;
    StackTraceElement[]	trace;
    StackTraceElement	element;

    result  = new StringBuilder();
    trace   = Thread.currentThread().getStackTrace();
    if (trace.length >= 2) {
      element = trace[2];
      if (cls)
	result.append(element.getClassName()).append(".");
      result.append(element.getMethodName());
      if (line)
	result.append("[").append(element.getLineNumber()).append("]");
    }
    else {
      result.append("<unknown>");
    }

    return result.toString();
  }

  /**
   * Returns the line number this method was called in.
   *
   * @return		the line number
   */
  public static int getLineNumber() {
    int			result;
    StackTraceElement[]	trace;

    result = -1;
    trace = Thread.currentThread().getStackTrace();
    if (trace.length >= 2)
      result = Thread.currentThread().getStackTrace()[2].getLineNumber();

    return result;
  }

  /**
   * Returns the stacktrace of the throwable as string.
   *
   * @param t		the throwable to get the stacktrace for
   * @return		the stacktrace
   */
  public static String throwableToString(Throwable t) {
    return throwableToString(t, -1);
  }

  /**
   * Returns the stacktrace of the throwable as string.
   *
   * @param t		the throwable to get the stacktrace for
   * @param maxLines	the maximum number of lines to print, <= 0 for all
   * @return		the stacktrace
   */
  public static String throwableToString(Throwable t, int maxLines) {
    StringWriter writer;
    StringBuilder	result;
    String[]		lines;
    int			i;

    writer = new StringWriter();
    t.printStackTrace(new PrintWriter(writer));

    if (maxLines > 0) {
      result = new StringBuilder();
      lines  = writer.toString().split("\n");
      for (i = 0; i < maxLines; i++) {
	if (i > 0)
	  result.append("\n");
	result.append(lines[i]);
      }
    }
    else {
      result = new StringBuilder(writer.toString());
    }

    return result.toString();
  }

  /**
   * Returns the current stack trace.
   *
   * @param maxDepth	the maximum depth of the stack trace, <= 0 for full trace
   * @return		the stack trace as string (multiple lines)
   */
  public static String getStackTrace(int maxDepth) {
    StringBuilder	result;
    Throwable		th;
    StackTraceElement[]	trace;
    int			i;

    result = new StringBuilder();
    th     = new Throwable();
    th.fillInStackTrace();
    trace  = th.getStackTrace();
    if (maxDepth <= 0)
      maxDepth = trace.length - 1;
    maxDepth++;  // we're starting at 1 not 0
    maxDepth = Math.min(maxDepth, trace.length);
    for (i = 1; i < maxDepth; i++) {
      if (i > 1)
	result.append("\n");
      result.append(trace[i]);
    }

    return result.toString();
  }

  /**
   * Outputs the stacktrace along with the message on stderr and returns a
   * combination of both of them as string.
   *
   * @param source	the object that generated the exception, can be null
   * @param msg		the message for the exception
   * @param t		the exception
   * @return		the full error message (message + stacktrace)
   */
  public static String handleException(LoggingSupporter source, String msg, Throwable t) {
    return handleException(source, msg, t, false);
  }

  /**
   * Generates a string from the stacktrace along with the message and returns
   * that. Depending on the silent flag, this string is also forwarded to the
   * source's logger.
   *
   * @param source	the object that generated the exception, can be null
   * @param msg		the message for the exception
   * @param t		the exception
   * @param silent	if true then the generated message is not forwarded
   * 			to the source's logger
   * @return		the full error message (message + stacktrace)
   */
  public static String handleException(LoggingSupporter source, String msg, Throwable t, boolean silent) {
    String	result;

    result = msg.trim() + "\n" + throwableToString(t);
    if (!silent) {
      if (source != null)
	source.getLogger().log(Level.SEVERE, msg, t);
      else
	System.err.println(result);
    }

    return result;
  }
}
