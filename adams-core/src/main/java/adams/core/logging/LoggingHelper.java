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
 * LoggingHelper.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.option.OptionUtils;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Helper class for logging related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LoggingHelper {

  /** the environment variable suffix of the log level to look for. */
  public final static String LOGLEVEL_SUFFIX = ".LOGLEVEL";

  /** for comparing levels. */
  protected final static LevelComparator m_LevelComparator = new LevelComparator();

  /** the global logging handler. */
  protected static Handler m_DefaultHandler = new SimpleConsoleHandler();

  /** the formatter for the timestamp. */
  protected static DateFormat m_DateFormat = new DateFormat("yyyyMMdd-HHmmss.SSS");

  /**
   * Returns the log level for the specified class. E.g., for the class
   * "hello.world.App" the environment variable "hello.world.App.LOGLEVEL"
   * is inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}" 
   * returned. Default is WARNING.
   *
   * @param cls		the class to return the debug level for
   * @return		the logging level
   */
  public static Level getLevel(Class cls) {
    Level	result;

    result = Level.WARNING;

    if (System.getenv(cls.getName() + LOGLEVEL_SUFFIX) != null) {
      try {
	result = LoggingLevel.valueOf(System.getenv(cls.getName() + LOGLEVEL_SUFFIX)).getLevel();
      }
      catch (Exception e) {
	result = Level.WARNING;
      }
    }

    return result;
  }

  /**
   * Returns the logging level for the specified class. E.g., for the class
   * "hello.world.App" the environment variable "hello.world.App.LOGLEVEL"
   * is inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}" 
   * returned. Default is WARNING.
   *
   * @param cls		the class to return the debug level for
   * @return		the logging level
   */
  public static LoggingLevel getLoggingLevel(Class cls) {
    LoggingLevel	result;
    Level		level;

    result = LoggingLevel.WARNING;
    level  = getLevel(cls);
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
   * E.g., for the class "hello.world.App" the environment variable "hello.world.App.LOGLEVEL"
   * is inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}" 
   * returned.
   *
   * @param cls		the class to return the logger for
   * @return		the logger
   */
  public static Logger getLogger(Class cls) {
    Logger	result;

    result = Logger.getLogger(cls.getName());
    result.setLevel(getLevel(cls));
    result.removeHandler(m_DefaultHandler);
    result.addHandler(m_DefaultHandler);
    result.setUseParentHandlers(false);

    return result;
  }

  /**
   * Returns the a logger with the log level for the specified class. 
   * E.g., for the class "hello.world.App" the environment variable "hello.world.App.LOGLEVEL"
   * is inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}" 
   * returned.
   *
   * @param name	the name of the class to return the logger for
   * @return		the logger
   */
  public static Logger getLogger(String name) {
    Logger	result;

    result = Logger.getLogger(name);
    result.setLevel(Level.WARNING);
    result.removeHandler(m_DefaultHandler);
    result.addHandler(m_DefaultHandler);
    result.setUseParentHandlers(false);

    return result;
  }

  /**
   * Returns a console logger with the log level for the specified class. 
   * E.g., for the class "hello.world.App" the environment variable "hello.world.App.LOGLEVEL"
   * is inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}" 
   * returned.
   *
   * @param cls		the class to return the logger for
   * @return		the logger
   */
  public static Logger getConsoleLogger(Class cls) {
    Logger	result;

    result = Logger.getLogger(cls.getName());
    result.setLevel(getLevel(cls));
    result.addHandler(new SimpleConsoleHandler());
    result.setUseParentHandlers(false);

    return result;
  }

  /**
   * Returns a console logger with the log level for the specified class. 
   * E.g., for the class "hello.world.App" the environment variable "hello.world.App.LOGLEVEL"
   * is inspected and "{OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST}" 
   * returned.
   *
   * @param name	the name of the class to return the logger for
   * @return		the logger
   */
  public static Logger getConsoleLogger(String name) {
    Logger	result;

    result = Logger.getLogger(name);
    result.setLevel(Level.WARNING);
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
  public static void setDefaultHandler(Handler value) {
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
  public static Handler getDefaultHandler() {
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
      multi = (MultiHandler) LoggingHelper.getDefaultHandler();
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
	handler = (Handler) Class.forName(classname).newInstance();
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
      msg += Utils.throwableToString(record.getThrown()) + "\n";

    result = new StringBuilder();
    lines  = msg.split("\n");
    suffix = "-" + LoggingLevel.valueOf(record.getLevel());
    prefix = record.getLoggerName();

    // any prefix to print?
    if ((prefix != null) && prefix.length() > 0)
      actualPrefix = "[" + prefix + suffix + "/" + m_DateFormat.format(new Date()) + "] ";
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
}
