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
 * OptionConsumer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import adams.core.CleanUpHandler;
import adams.core.logging.LoggingLevel;

/**
 * Interface for classes that set the option values based on the input data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <C> the type of data to consume
 * @param <V> the type of data used for values
 */
public interface OptionConsumer<C,V>
  extends CleanUpHandler {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public abstract String globalInfo();

  /**
   * Sets the logging level.
   *
   * @param value 	the logging level
   */
  public void setLoggingLevel(LoggingLevel value);

  /**
   * Returns the logging level.
   *
   * @return 		the logging level
   */
  public LoggingLevel getLoggingLevel();

  /**
   * Checks whether errors were encountered while consuming the options.
   *
   * @return		true if errors were encountered
   * @see		#getErrors()
   */
  public boolean hasErrors();

  /**
   * Returns the error log.
   *
   * @return		the error log, can be empty
   * @see		#hasErrors()
   */
  public List<String> getErrors();

  /**
   * Checks whether warnings were encountered while consuming the options.
   *
   * @return		true if warnings were encountered
   * @see		#getWarnings()
   */
  public boolean hasWarnings();

  /**
   * Returns the warning log.
   *
   * @return		the warning log, can be empty
   * @see		#hasWarnings()
   */
  public List<String> getWarnings();

  /**
   * Sets whether console output is suppressed or not.
   *
   * @param value	if true then console output is suppressed (out/err)
   */
  public void setQuiet(boolean value);

  /**
   * Returns whether console output is suppressed or not.
   *
   * @return		true if console output is suppressed
   */
  public boolean isQuiet();

  /**
   * Sets the top-level properties to skip.
   *
   * @param value	the properties
   */
  public void setSkippedProperties(HashSet<String> value);

  /**
   * Returns the skipped top-level properties.
   *
   * @return		the properties
   */
  public HashSet<String> getSkippedProperties();

  /**
   * Returns the visited top-level object.
   *
   * @return		the visited object
   */
  public OptionHandler getOutput();

  /**
   * Sets the input data to use.
   *
   * @param input	the data to use
   */
  public void setInput(C input);

  /**
   * Returns the currently set input data.
   *
   * @return		the data in use
   */
  public C getInput();

  /**
   * Consumes the current input. The generated option handler can be retrieved
   * via getOutput() as well.
   *
   * @return		the created object
   * @see		#getOutput()
   */
  public OptionHandler consume();

  /**
   * Consumes the provided input and updates the provided option handler.
   * The option handler can be retrieved via getOutput() as well.
   *
   * @param output	the option handler to update
   * @param input	the data to use for updating
   * @return		the updated object
   * @see		#getOutput()
   */
  public OptionHandler consume(OptionHandler output, C input);

  /**
   * Processes the specified string.
   *
   * @param s		the string to process
   * @return		the created object
   * @see		#getOutput()
   */
  public OptionHandler fromString(String s);

  /**
   * Processes the specified file.
   *
   * @param file	the file to process
   * @return		the created object, null in case content of file couldn't be loaded
   */
  public OptionHandler fromFile(File file);
  /**
   * Reads the option handler from the specified file.
   *
   * @param filename	the file to read from
   * @return		the option handler if successful, null otherwise
   */
  public OptionHandler read(String filename);

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp();
}
