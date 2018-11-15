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
 * AbstractPythonExecution.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.Utils;
import adams.core.io.PlaceholderDirectory;
import adams.core.option.OptionUtils;
import adams.flow.core.ActorUtils;
import adams.flow.core.RunnableWithLogging;
import adams.flow.core.Token;
import adams.flow.standalone.PythonEnvironment;
import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for sources that execute a Python executable.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPythonExecution
  extends AbstractSource
  implements StreamingProcessOwner {

  /** for serialization. */
  private static final long serialVersionUID = -132045002653940359L;

  /** whether to output stderr instead of stdout or both. */
  protected StreamingProcessOutputType m_OutputType;

  /** the stdout prefix. */
  protected String m_PrefixStdOut;

  /** the stderr prefix. */
  protected String m_PrefixStdErr;

  /** the tokens to forward. */
  protected List m_Output;

  /** the process monitor. */
  protected transient StreamingProcessOutput m_ProcessOutput;

  /** the runnable executing the command. */
  protected RunnableWithLogging m_Monitor;

  /** in case an exception occurred executing the command (gets rethrown). */
  protected IllegalStateException m_ExecutionFailure;

  /** the time out in seconds. */
  protected int m_TimeOut;

  /** the environment in use. */
  protected PythonEnvironment m_Environment;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-type", "outputType",
      StreamingProcessOutputType.STDOUT);

    m_OptionManager.add(
      "prefix-stdout", "prefixStdOut",
      "");

    m_OptionManager.add(
      "prefix-stderr", "prefixStdErr",
      "");

    m_OptionManager.add(
      "time-out", "timeOut",
      -1, -1, null);
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
  }

  /**
   * Sets what output from the process to forward.
   *
   * @param value	the output type
   */
  public void setOutputType(StreamingProcessOutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns what output from the process to forward.
   *
   * @return 		the output type
   */
  public StreamingProcessOutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return
      "Determines the output type; if " + StreamingProcessOutputType.BOTH + " is selected "
	+ "then an array is output with stdout as first element and stderr as "
	+ "second";
  }

  /**
   * Sets the (optional) prefix to use for output from stdout.
   *
   * @param value	the prefix
   */
  public void setPrefixStdOut(String value) {
    m_PrefixStdOut = value;
    reset();
  }

  /**
   * Returns the (optional) prefix to use for output from stdout.
   *
   * @return 		the prefix
   */
  public String getPrefixStdOut() {
    return m_PrefixStdOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String prefixStdOutTipText() {
    return "The (optional) prefix to use for output from stdout.";
  }

  /**
   * Sets the (optional) prefix to use for output from stderr.
   *
   * @param value	the prefix
   */
  public void setPrefixStdErr(String value) {
    m_PrefixStdErr = value;
    reset();
  }

  /**
   * Returns the (optional) prefix to use for output from stderr.
   *
   * @return 		the prefix
   */
  public String getPrefixStdErr() {
    return m_PrefixStdErr;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String prefixStdErrTipText() {
    return "The (optional) prefix to use for output from stderr.";
  }

  /**
   * Sets the time out for the process.
   *
   * @param value	the time out in seconds
   */
  public void setTimeOut(int value) {
    if (getOptionManager().isValid("timeOut", value)) {
      m_TimeOut = value;
      reset();
    }
  }

  /**
   * Returns the time out for the process.
   *
   * @return 		the time out in seconds
   */
  public int getTimeOut() {
    return m_TimeOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String timeOutTipText() {
    return "The maximum time in seconds for the process to run before getting killed, ignored if less than 1.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String 	result;

    result = super.setUp();

    if (result == null) {
      m_Environment = (PythonEnvironment) ActorUtils.findClosestType(this, PythonEnvironment.class, true);
      if (m_Environment == null)
        result = "Failed to locate a " + Utils.classToString(PythonEnvironment.class) + " actor!";
    }

    return result;
  }

  /**
   * Launches the command.
   *
   * @param cmd 	the command and its options
   * @param workingDir	the working directory, ignored if empty
   * @return		null if everything is fine, otherwise error message
   */
  protected String launch(final List<String> cmd, final String workingDir) {
    String		result;

    result = null;
    m_Output.clear();

    if (result == null) {
      if (isLoggingEnabled()) {
	getLogger().info("Command: " + OptionUtils.joinOptions(cmd.toArray(new String[cmd.size()])));
	if (!workingDir.isEmpty())
	  getLogger().info("Working dir: " + workingDir);
      }

      // setup thread
      m_ExecutionFailure = null;
      m_ProcessOutput = new StreamingProcessOutput(this);
      m_ProcessOutput.setTimeOut(m_TimeOut);
      m_Monitor = new RunnableWithLogging() {
	private static final long serialVersionUID = -4475355379511760429L;

	@Override
	protected void doRun() {
	  try {
	    ProcessBuilder builder = new ProcessBuilder(cmd);
	    if (!workingDir.isEmpty())
	      builder.directory(new PlaceholderDirectory(workingDir).getAbsoluteFile());
            m_Environment.updatePythonPath(builder.environment());
	    m_ProcessOutput.monitor(builder);
	  }
	  catch (Exception e) {
	    m_ExecutionFailure = new IllegalStateException("Failed to execute: " + OptionUtils.joinOptions(cmd.toArray(new String[cmd.size()])), e);
	  }
	  m_Monitor = null;
	  m_ProcessOutput = null;
	}

	@Override
	public void stopExecution() {
	  if (m_ProcessOutput != null)
	    m_ProcessOutput.destroy();
	  super.stopExecution();
	}
      };
      new Thread(m_Monitor).start();
    }

    return result;
  }

  /**
   * Adds the line from the output to the internal list of lines to output.
   *
   * @param line	the line to add
   * @param stdout	whether stdout or stderr
   */
  public void processOutput(String line, boolean stdout) {
    if (stdout)
      m_Output.add(m_PrefixStdOut + line);
    else
      m_Output.add(m_PrefixStdErr + line);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token			result;
    IllegalStateException	exc;

    result = null;

    while ((m_Output.size() == 0) && !isStopped() && (m_Monitor != null)) {
      Utils.wait(this, this, 1000, 100);
    }

    if (m_ExecutionFailure != null) {
      exc                = m_ExecutionFailure;
      m_ExecutionFailure = null;
      throw exc;
    }

    if (!isStopped() && (m_Output.size() > 0)) {
      result = new Token(m_Output.get(0));
      m_Output.remove(0);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_Output.size() > 0) || (m_Monitor != null);
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Monitor != null)
      m_Monitor.stopExecution();
    super.stopExecution();
  }
}
