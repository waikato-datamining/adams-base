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
 * OptionProducer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.util.HashSet;

import adams.core.CleanUpHandler;
import adams.core.logging.LoggingLevel;

/**
 * Interface for classes that generate output from visiting the options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <O> the type of output data that gets generated
 * @param <I> the internal type used while nesting
 */
public interface OptionProducer<O,I>
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
   * @param value 	the level
   */
  public void setLoggingLevel(LoggingLevel value);

  /**
   * Returns the logging level.
   *
   * @return 		true if logging output is on
   */
  public LoggingLevel getLoggingLevel();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggingLevelTipText();

  /**
   * Sets whether to output the values of options instead of variable
   * placeholders.
   *
   * @param value	if true then the values are output instead of variable placeholders
   */
  public void setOutputVariableValues(boolean value);

  /**
   * Returns whether the values of options are output instead of variable
   * placeholders.
   *
   * @return		true if values are output instead of variable placeholders
   */
  public boolean getOutputVariableValues();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputVariableValuesTipText();

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
   * Returns whether property names are used or just the command-line flags.
   *
   * @return		true if property names are used
   */
  public boolean getUsePropertyNames();

  /**
   * Returns the visited top-level object.
   *
   * @return		the visited object
   */
  public OptionHandler getInput();

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output
   * @see		#initOutput()t
   */
  public O getOutput();

  /**
   * Visits a boolean option.
   *
   * @param option	the boolean option
   * @return		the last internal data structure that was generated
   */
  public I processOption(BooleanOption option);

  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		the last internal data structure that was generated
   */
  public I processOption(ClassOption option);

  /**
   * Visits an argument option.
   *
   * @param option	the argument option
   * @return		the last internal data structure that was generated
   */
  public I processOption(AbstractArgumentOption option);

  /**
   * Visits the option and obtains information from it.
   *
   * @param option	the current option
   * @return		the last internal data structure that was generated
   */
  public I doProduce(AbstractOption option);

  /**
   * Visits the option and obtains information from it.
   *
   * @param visitedObject	the option handler to visit
   * @return			the generated output
   */
  public O produce(OptionHandler visitedObject);

  /**
   * Returns the output generated from the visit.
   *
   * @return		the output, null in case of an error
   */
  @Override
  public String toString();

  /**
   * Writes the generated content to the specified file.
   *
   * @param filename	the file to write to
   * @return		true if successfully written
   */
  public boolean write(String filename);

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp();
}
