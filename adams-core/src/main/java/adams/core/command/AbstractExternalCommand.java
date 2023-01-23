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
 * AbstractExternalCommand.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command;

import adams.core.Utils;
import adams.core.command.output.OutputFormatter;
import adams.core.command.output.PassThrough;
import adams.core.command.stderr.StdErrProcessor;
import adams.core.command.stdout.StdOutProcessor;
import adams.core.logging.LoggingHelper;
import adams.core.management.CommandResult;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for external commands.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractExternalCommand
  extends AbstractOptionHandler
  implements ExternalCommand {

  private static final long serialVersionUID = -3060945925413859934L;

  /** the type of output to forward. */
  protected OutputType m_OutputType;

  /** the handler for processing output on stderr. */
  protected StdErrProcessor m_StdErrProcessor;

  /** the handler for processing output on stderr. */
  protected StdOutProcessor m_StdOutProcessor;

  /** the output formatter in use. */
  protected OutputFormatter m_OutputFormatter;

  /** the command was executed. */
  protected boolean m_Executed;

  /** whether the command is still running. */
  protected boolean m_Running;

  /** whether the execution was stopped. */
  protected boolean m_Stopped;

  /** for buffering output. */
  protected List m_Output;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the last command that was executed. */
  protected transient String[] m_LastCommand;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-type", "outputType",
      getDefaultOutputType());

    m_OptionManager.add(
      "output-formatter", "outputFormatter",
      getDefaultOutputFormatter());

    m_OptionManager.add(
      "stdout-processor", "stdOutProcessor",
      getDefaultStdOutProcessor());

    m_OptionManager.add(
      "stderr-processor", "stdErrProcessor",
      getDefaultStdErrProcessor());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Output = new ArrayList();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Output.clear();
    m_LastCommand = null;
  }

  /**
   * Returns the default output type.
   *
   * @return		the default
   */
  protected OutputType getDefaultOutputType() {
    return OutputType.STDOUT;
  }

  /**
   * Sets the type of output to forward.
   *
   * @param value	the type
   */
  @Override
  public void setOutputType(OutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns the type of output to forward.
   *
   * @return		the type
   */
  @Override
  public OutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputTypeTipText() {
    return "The type of output to forward.";
  }

  /**
   * Returns the default output formatter.
   *
   * @return		the default
   */
  protected OutputFormatter getDefaultOutputFormatter() {
    return new PassThrough();
  }

  /**
   * Sets the formatter for the output that is being forwarded.
   *
   * @param value	the formatter
   */
  @Override
  public void setOutputFormatter(OutputFormatter value) {
    m_OutputFormatter = value;
    reset();
  }

  /**
   * Returns the formatter for the output that is being forwarded.
   *
   * @return		the formatter
   */
  @Override
  public OutputFormatter getOutputFormatter() {
    return m_OutputFormatter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFormatterTipText() {
    return "The formatter to apply to the data being forwarded.";
  }

  /**
   * Returns the default handler for processing output on stdout.
   *
   * @return		the handler
   */
  protected StdOutProcessor getDefaultStdOutProcessor() {
    return new adams.core.command.stdout.Null();
  }

  /**
   * Sets the handler for processing the output received on stdout.
   *
   * @param value	the handler
   */
  @Override
  public void setStdOutProcessor(StdOutProcessor value) {
    m_StdOutProcessor = value;
    reset();
  }

  /**
   * Returns the handler for processing the output received on stdout.
   *
   * @return		the handler
   */
  @Override
  public StdOutProcessor getStdOutProcessor() {
    return m_StdOutProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String stdOutProcessorTipText() {
    return "The handler for processing output received from the external command on stdout.";
  }

  /**
   * Returns the default handler for processing output on stderr.
   *
   * @return		the handler
   */
  protected StdErrProcessor getDefaultStdErrProcessor() {
    return new adams.core.command.stderr.Null();
  }

  /**
   * Sets the handler for processing the output received on stderr.
   *
   * @param value	the handler
   */
  @Override
  public void setStdErrProcessor(StdErrProcessor value) {
    m_StdErrProcessor = value;
    reset();
  }

  /**
   * Returns the handler for processing the output received on stderr.
   *
   * @return		the handler
   */
  @Override
  public StdErrProcessor getStdErrProcessor() {
    return m_StdErrProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String stdErrProcessorTipText() {
    return "The handler for processing output received from the external command on stderr.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Sets the flow context.
   *
   * @param value the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Hook method for performing checks before executing the command.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    String	result;

    result = null;

    if (m_FlowContext == null)
      result = "No flow context set!";

    if (result == null)
      result = m_StdErrProcessor.setUp(this);

    if (result == null)
      result = m_StdOutProcessor.setUp(this);

    return result;
  }

  /**
   * Logs the command for execution.
   *
   * @param cmd		the command
   */
  protected void log(String[] cmd) {
    log(Arrays.asList(cmd));
  }

  /**
   * Logs the command for execution.
   *
   * @param cmd		the command
   */
  protected void log(List<String> cmd) {
    if (isLoggingEnabled())
      getLogger().info("Command: " + Utils.flatten(cmd, " "));
  }

  /**
   * For logging the result of a command.
   *
   * @param res		the result to log
   */
  protected void log(CommandResult res) {
    if (isLoggingEnabled()) {
      getLogger().info("Command: " + Utils.flatten(res.command, " "));
      getLogger().info("Exit code: " + res.exitCode);
      if (res.stdout != null)
	getLogger().info("Stdout: " + res.stdout);
      if (res.stderr != null)
	getLogger().info("Stderr: " + res.stderr);
    }
  }

  /**
   * Generates an error message from the command result.
   *
   * @param res		the result to turn into an error message
   * @return		the error message
   */
  protected String commandResultToError(CommandResult res) {
    String	result;

    result = "Failed to execute: " + Utils.flatten(res.command, " ") + "\n"
      + "Exit code: " + res.exitCode;
    if (res.stdout != null)
      result += "\nStdout: " + res.stdout;
    if (res.stderr != null)
      result += "\nStderr: " + res.stderr;

    return result;
  }

  /**
   * Assembles the command to run.
   *
   * @return		the command
   */
  protected List<String> buildCommand() {
    return new ArrayList<>();
  }

  /**
   * Executes the command in asynchronous fashion.
   * Async commands must set the {@link #m_Running} flag to false themselves.
   *
   * @return		the result of the command, either a CommandResult or a String object (= error message)
   */
  protected Object doAsyncExecute() {
    return null;
  }

  /**
   * Executes the command in blocking fashion (ie waits till it finishes).
   * The {@link #m_Running} flag is set to false automatically.
   *
   * @return		the result of the command, either a CommandResult or a String object (= error message)
   */
  protected Object doBlockingExecute() {
    return null;
  }

  /**
   * Executes the command.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String execute() {
    String		result;
    Object		res;
    CommandResult 	cmdResult;

    m_Running     = false;
    m_Executed    = false;
    m_Stopped     = false;
    m_LastCommand = null;
    m_Output.clear();

    result = check();

    if (result == null) {
      try {
	m_Running = true;
	if (isUsingBlocking()) {
	  res       = doBlockingExecute();
	  m_Running = false;
	}
	else {
	  res = doAsyncExecute();
	}
	if (res != null) {
	  if (res instanceof CommandResult) {
	    cmdResult = (CommandResult) res;
	    if ((cmdResult.stderr != null) && !cmdResult.stderr.isEmpty())
	      m_StdErrProcessor.processBlocking(cmdResult.stderr);
	    if ((cmdResult.stdout != null) && !cmdResult.stdout.isEmpty())
	      m_StdOutProcessor.processBlocking(cmdResult.stdout);
	    log(cmdResult);
	    switch (m_OutputType) {
	      case NONE:
		// nothing to do
		break;
	      case STDOUT:
		if ((cmdResult.stdout != null) && !cmdResult.stdout.isEmpty())
		  m_OutputFormatter.formatOutput(this, true, isUsingBlocking(), cmdResult.stdout);
		break;
	      case STDERR:
		if ((cmdResult.stderr != null) && !cmdResult.stderr.isEmpty())
		  m_OutputFormatter.formatOutput(this, false, isUsingBlocking(), cmdResult.stderr);
		break;
	      case BOTH:
		if ((cmdResult.stdout != null) && !cmdResult.stdout.isEmpty())
		  m_OutputFormatter.formatOutput(this, true, isUsingBlocking(), cmdResult.stdout);
		if ((cmdResult.stderr != null) && !cmdResult.stderr.isEmpty())
		  m_OutputFormatter.formatOutput(this, false, isUsingBlocking(), cmdResult.stderr);
		break;
	      default:
		throw new IllegalStateException("Unhandled output type: " + m_OutputType);
	    }
	  }
	  else if (res instanceof String) {
	    result = (String) res;
	  }
	  else {
	    throw new IllegalStateException(
	      "Received an object of type " + Utils.classToString(res) + " instead of "
		+ "a String or " + Utils.classToString(CommandResult.class) + " one!");
	  }
	}
      }
      catch (Exception e) {
	m_Running = false;
	result    = LoggingHelper.handleException(this, "Failed to execute command!", e);
      }
    }

    m_Executed = true;

    return result;
  }

  /**
   * Gets called by the output formatter class.
   *
   * @param output	the formatted output to collect
   */
  public void addFormattedOutput(Object output) {
    m_Output.add(output);
  }

  /**
   * Checks whether a command has been executed (and recorded).
   *
   * @return		true if executed/recorded
   */
  @Override
  public boolean hasLastCommand() {
    return (m_LastCommand != null);
  }

  /**
   * Returns the last command that was executed.
   *
   * @return		the last command, null if not available
   */
  @Override
  public String[] getLastCommand() {
    return m_LastCommand;
  }

  /**
   * Returns whether the command was executed.
   *
   * @return		true if executed
   */
  @Override
  public boolean isExecuted() {
    return m_Executed;
  }

  /**
   * Returns whether the command is currently running.
   *
   * @return		true if running
   */
  @Override
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Returns whether the command finished.
   *
   * @return		true if finished
   */
  @Override
  public boolean isFinished() {
    return isExecuted() && !isRunning();
  }

  /**
   * Whether there is any pending output.
   *
   * @return		true if output pending
   */
  @Override
  public boolean hasOutput() {
    return isRunning() || (m_Output.size() > 0);
  }

  /**
   * Returns the next line in the output.
   *
   * @return		the line, null if none available
   */
  @Override
  public Object output() {
    Object  	result;
    int 	i;

    result = null;

    if (m_Output.size() > 0)
      result = m_Output.remove(0);

    // wait a bit for more data to come through before giving up
    if ((result == null) && !isFinished() && !isUsingBlocking()) {
      for (i = 0; i < 10; i++) {
	Utils.wait(this, this, 100, 50);
	if (m_Output.size() > 0) {
	  result = m_Output.remove(0);
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns what output type the command generates via its output formatter.
   *
   * @return		the type
   */
  public Class generates() {
    return m_OutputFormatter.generates(isUsingBlocking());
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_FlowContext = null;
    m_OutputFormatter.cleanUp();
    m_StdOutProcessor.cleanUp();
    m_StdErrProcessor.cleanUp();
  }
}
