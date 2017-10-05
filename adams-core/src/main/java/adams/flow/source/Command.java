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
 * Command.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.ClassCrossReference;
import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderDirectory;
import adams.flow.core.RunnableWithLogging;
import adams.flow.core.Token;
import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Runs an external system command and broadcasts the generated output (stdout and&#47;or stderr) continuously, opposed to adams.flow.source.Exec which waits for the command to finish.<br>
 * <br>
 * See also:<br>
 * adams.flow.source.Exec
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Command
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-cmd &lt;adams.core.base.BaseText&gt; (property: command)
 * &nbsp;&nbsp;&nbsp;The external command to run.
 * &nbsp;&nbsp;&nbsp;default: ls -l .
 * </pre>
 *
 * <pre>-working-directory &lt;java.lang.String&gt; (property: workingDirectory)
 * &nbsp;&nbsp;&nbsp;The current working directory for the command.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-placeholder &lt;boolean&gt; (property: commandContainsPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion for the command
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-variable &lt;boolean&gt; (property: commandContainsVariable)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic variable expansion for the command
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-output-type &lt;STDOUT|STDERR|BOTH&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;Determines the output type; if BOTH is selected then an array is output
 * &nbsp;&nbsp;&nbsp;with stdout as first element and stderr as second
 * &nbsp;&nbsp;&nbsp;default: STDOUT
 * </pre>
 *
 * <pre>-prefix-stdout &lt;java.lang.String&gt; (property: prefixStdOut)
 * &nbsp;&nbsp;&nbsp;The (optional) prefix to use for output from stdout.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-prefix-stderr &lt;java.lang.String&gt; (property: prefixStdErr)
 * &nbsp;&nbsp;&nbsp;The (optional) prefix to use for output from stderr.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Command
  extends AbstractSource
  implements ClassCrossReference, StreamingProcessOwner {

  /** for serialization. */
  private static final long serialVersionUID = -132045002653940359L;

  /** the command to run. */
  protected BaseText m_Command;

  /** the current working directory. */
  protected String m_WorkingDirectory;

  /** whether the replace string contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_CommandContainsPlaceholder;

  /** whether the replace string contains a variable, which needs to be
   * expanded first. */
  protected boolean m_CommandContainsVariable;

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

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Runs an external system command and broadcasts the generated output "
	+ "(stdout and/or stderr) continuously, opposed to " + Exec.class.getName()
	+ " which waits for the command to finish.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{Exec.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "cmd", "command",
      new BaseText("ls -l ."));

    m_OptionManager.add(
      "working-directory", "workingDirectory",
      "");

    m_OptionManager.add(
      "placeholder", "commandContainsPlaceholder",
      false);

    m_OptionManager.add(
      "variable", "commandContainsVariable",
      false);

    m_OptionManager.add(
      "output-type", "outputType",
      StreamingProcessOutputType.STDOUT);

    m_OptionManager.add(
      "prefix-stdout", "prefixStdOut",
      "");

    m_OptionManager.add(
      "prefix-stderr", "prefixStdErr",
      "");
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "command", m_Command);
  }

  /**
   * Sets the command to run.
   *
   * @param value	the command
   */
  public void setCommand(BaseText value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to run.
   *
   * @return 		the command
   */
  public BaseText getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String commandTipText() {
    return "The external command to run.";
  }

  /**
   * Sets the current working directory for the command.
   *
   * @param value	the directory, ignored if empty
   */
  public void setWorkingDirectory(String value) {
    m_WorkingDirectory = value;
    reset();
  }

  /**
   * Returns the current working directory for the command.
   *
   * @return 		the directory, ignored if empty
   */
  public String getWorkingDirectory() {
    return m_WorkingDirectory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String workingDirectoryTipText() {
    return "The current working directory for the command.";
  }

  /**
   * Sets whether the command string contains a placeholder which needs to be
   * expanded first.
   *
   * @param value	true if command string contains a placeholder
   */
  public void setCommandContainsPlaceholder(boolean value) {
    m_CommandContainsPlaceholder = value;
    reset();
  }

  /**
   * Returns whether the command string contains a placeholder which needs to be
   * expanded first.
   *
   * @return		true if command string contains a placeholder
   */
  public boolean getCommandContainsPlaceholder() {
    return m_CommandContainsPlaceholder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandContainsPlaceholderTipText() {
    return "Set this to true to enable automatic placeholder expansion for the command string.";
  }

  /**
   * Sets whether the command string contains a variable which needs to be
   * expanded first.
   *
   * @param value	true if command string contains a variable
   */
  public void setCommandContainsVariable(boolean value) {
    m_CommandContainsVariable = value;
    reset();
  }

  /**
   * Returns whether the command string contains a variable which needs to be
   * expanded first.
   *
   * @return		true if command string contains a variable
   */
  public boolean getCommandContainsVariable() {
    return m_CommandContainsVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandContainsVariableTipText() {
    return "Set this to true to enable automatic variable expansion for the command string.";
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
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		cmd;
    final String	fCmd;

    m_Output.clear();

    // preprocess command
    cmd = m_Command.getValue();
    if (m_CommandContainsVariable)
      cmd = getVariables().expand(cmd);
    if (m_CommandContainsPlaceholder)
      cmd = Placeholders.getSingleton().expand(cmd).replace("\\", "/");
    fCmd = cmd;
    if (isLoggingEnabled()) {
      getLogger().info("Command: " + cmd);
      if (!m_WorkingDirectory.isEmpty())
	getLogger().info("Working dir: " + m_WorkingDirectory);
    }

    // setup thread
    m_ExecutionFailure = null;
    m_ProcessOutput = new StreamingProcessOutput(this);
    m_Monitor = new RunnableWithLogging() {
      private static final long serialVersionUID = -4475355379511760429L;
      @Override
      protected void doRun() {
        try {
          ProcessBuilder builder = new ProcessBuilder(fCmd);
          if (!m_WorkingDirectory.isEmpty())
            builder.directory(new PlaceholderDirectory(m_WorkingDirectory).getAbsoluteFile());
	  m_ProcessOutput.monitor(builder);
	}
	catch (Exception e) {
          m_ExecutionFailure = new IllegalStateException("Failed to execute: " + fCmd, e);
	}
	m_Monitor       = null;
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

    return null;
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
    Token	result;

    result = null;

    while ((m_Output.size() == 0) && !isStopped() && (m_Monitor != null)) {
      Utils.wait(this, this, 1000, 100);
    }

    if (m_ExecutionFailure != null)
      throw m_ExecutionFailure;

    if (!isStopped() && (m_Monitor != null)) {
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
