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
 * ExternalCommand.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.StoppableWithFeedback;
import adams.core.command.output.OutputFormatter;
import adams.core.command.stderr.StdErrProcessor;
import adams.core.command.stdout.StdOutProcessor;
import adams.core.option.OptionHandler;
import adams.flow.core.FlowContextHandler;

/**
 * Interface for classes that execute external commands.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ExternalCommand
  extends OptionHandler, FlowContextHandler, QuickInfoSupporter, StoppableWithFeedback, CleanUpHandler {

  /**
   * Sets what output to forward.
   *
   * @param value	the type of output
   */
  public void setOutputType(OutputType value);

  /**
   * Returns what output to forward.
   *
   * @return		the type of output
   */
  public OutputType getOutputType();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText();

  /**
   * Sets the handler for processing the output received on stderr.
   *
   * @param value	the handler
   */
  public void setStdErrProcessor(StdErrProcessor value);

  /**
   * Returns the handler for processing the output received on stderr.
   *
   * @return		the handler
   */
  public StdErrProcessor getStdErrProcessor();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stdErrProcessorTipText();

  /**
   * Sets the handler for processing the output received on stdout.
   *
   * @param value	the handler
   */
  public void setStdOutProcessor(StdOutProcessor value);

  /**
   * Returns the handler for processing the output received on stdout.
   *
   * @return		the handler
   */
  public StdOutProcessor getStdOutProcessor();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stdOutProcessorTipText();

  /**
   * Sets the formatter to use for the output that is being forwarded.
   *
   * @param value	the formatter
   */
  public void setOutputFormatter(OutputFormatter value);

  /**
   * Returns the formatter to use for the output that is being forwarded.
   *
   * @return		the formatter
   */
  public OutputFormatter getOutputFormatter();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFormatterTipText();

  /**
   * Whether the command is used in a blocking or async fashion.
   *
   * @return		true if blocking, false if async
   */
  public boolean isUsingBlocking();

  /**
   * Executes the command.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute();

  /**
   * Checks whether a command has been executed (and recorded).
   *
   * @return		true if executed/recorded
   */
  public boolean hasLastCommand();

  /**
   * Returns the last command that was executed.
   *
   * @return		the last command, null if not available
   */
  public String[] getLastCommand();

  /**
   * Returns whether the command was executed.
   *
   * @return		true if executed
   */
  public boolean isExecuted();

  /**
   * Returns whether the command is currently running.
   *
   * @return		true if running
   */
  public boolean isRunning();

  /**
   * Returns whether the command finished.
   *
   * @return		true if finished
   */
  public boolean isFinished();

  /**
   * Whether there is any pending output.
   *
   * @return		true if output pending
   */
  public boolean hasOutput();

  /**
   * Returns the next line in the output.
   *
   * @return		the line, null if none available
   */
  public Object output();

  /**
   * Gets called by the output formatter class.
   *
   * @param output	the formatted output to collect
   */
  public void addFormattedOutput(Object output);

  /**
   * Returns what output type the command generates via its output formatter.
   *
   * @return		the type
   */
  public Class generates();

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp();
}
