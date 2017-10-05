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
 * Exec.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.ClassCrossReference;
import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderDirectory;
import adams.core.management.OS;
import adams.core.management.ProcessUtils;
import adams.core.option.OptionUtils;
import adams.data.conversion.ConversionFromString;
import adams.data.conversion.StringToString;
import adams.flow.core.Token;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Runs an external system command and broadcasts the generated output (stdout or stderr).<br>
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
 * &nbsp;&nbsp;&nbsp;default: Exec
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
 * <pre>-split-output &lt;boolean&gt; (property: splitOutput)
 * &nbsp;&nbsp;&nbsp;If set to true, then the output gets split on newline; does not apply when 
 * &nbsp;&nbsp;&nbsp;outputting stdout and stderr together.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.ConversionFromString&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion scheme to apply to the output; does not apply when outputting 
 * &nbsp;&nbsp;&nbsp;stdout and stderr together.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.StringToString
 * </pre>
 * 
 * <pre>-fail-on-process-error &lt;boolean&gt; (property: failOnProcessError)
 * &nbsp;&nbsp;&nbsp;If enabled, the actor will fail as well if the process failed.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Exec
  extends AbstractSource
  implements ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = -132045002653940359L;

  /**
   * What to output.
   */
  public enum OutputType {
    STDOUT,
    STDERR,
    BOTH,
  }

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

  /** whether to output stderr instead of stdout. */
  protected OutputType m_OutputType;
  
  /** whether to split the string output on the new line before converting. */
  protected boolean m_SplitOutput;

  /** the conversion scheme to process the output with. */
  protected ConversionFromString m_Conversion;
  
  /** the tokens to forward. */
  protected List m_Output;

  /** whether to fail on process error. */
  protected boolean m_FailOnProcessError;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Runs an external system command and broadcasts the generated output "
      + "(stdout or stderr).";
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
	    OutputType.STDOUT);

    m_OptionManager.add(
	    "split-output", "splitOutput",
	    false);

    m_OptionManager.add(
	    "conversion", "conversion",
	    new StringToString());

    m_OptionManager.add(
	    "fail-on-process-error", "failOnProcessError",
	    true);
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
  public void setOutputType(OutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns what output from the process to forward.
   *
   * @return 		the output type
   */
  public OutputType getOutputType() {
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
      "Determines the output type; if " + OutputType.BOTH + " is selected "
	+ "then an array is output with stdout as first element and stderr as "
	+ "second";
  }

  /**
   * Sets whether to split the output on the newline or crlf.
   *
   * @param value	if true then output is split
   */
  public void setSplitOutput(boolean value) {
    m_SplitOutput = value;
    reset();
  }

  /**
   * Returns whether to split the output on newline or crlf.
   *
   * @return 		true if output is split
   */
  public boolean getSplitOutput() {
    return m_SplitOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String splitOutputTipText() {
    return "If set to true, then the output gets split on " + (OS.isWindows() ? "CRLF" : "newline") + "; does not apply when outputting stdout and stderr together.";
  }

  /**
   * Sets the conversion to apply to the output.
   *
   * @param value	the conversion
   */
  public void setConversion(ConversionFromString value) {
    m_Conversion = value;
    reset();
  }

  /**
   * Returns the conversion to apply to the output.
   *
   * @return 		the conversion
   */
  public ConversionFromString getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The conversion scheme to apply to the output; does not apply when outputting stdout and stderr together.";
  }

  /**
   * Sets whether to fail as well if the process failed.
   *
   * @param value	true if to fail as well
   */
  public void setFailOnProcessError(boolean value) {
    m_FailOnProcessError = value;
    reset();
  }

  /**
   * Returns whether to fail as well if the process failed.
   *
   * @return		true if to fail as well
   */
  public boolean getFailOnProcessError() {
    return m_FailOnProcessError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String failOnProcessErrorTipText() {
    return "If enabled, the actor will fail as well if the process failed.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    if (m_OutputType == OutputType.BOTH)
      return new Class[]{String[].class};
    else
      return new Class[]{m_Conversion.generates()};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String		cmd;
    CollectingProcessOutput proc;
    String[]		items;
    String		msg;
    String[]		output;
    int			i;

    result = null;

    m_Output.clear();
    
    // preprocess command
    cmd = m_Command.getValue();
    if (m_CommandContainsVariable)
      cmd = getVariables().expand(cmd);
    if (m_CommandContainsPlaceholder)
      cmd = Placeholders.getSingleton().expand(cmd).replace("\\", "/");
    if (isLoggingEnabled()) {
      getLogger().info("Command: " + cmd);
      if (!m_WorkingDirectory.isEmpty())
	getLogger().info("Working dir: " + m_WorkingDirectory);
    }
    
    try {
      if (m_WorkingDirectory.isEmpty())
	proc = ProcessUtils.execute(OptionUtils.splitOptions(cmd));
      else
	proc = ProcessUtils.execute(OptionUtils.splitOptions(cmd), new PlaceholderDirectory(m_WorkingDirectory));
      if (!proc.hasSucceeded() && m_FailOnProcessError) {
	result = ProcessUtils.toErrorOutput(proc);
      }
      else {
	switch (m_OutputType) {
	  case STDOUT:
	    output = new String[]{proc.getStdOut()};
	    break;
	  case STDERR:
	    output = new String[]{proc.getStdErr()};
	    break;
	  case BOTH:
	    output = new String[]{proc.getStdOut(), proc.getStdErr()};
	    break;
	  default:
	    throw new IllegalStateException("Unhandled output type: " + m_OutputType);
	}
	if (m_OutputType != OutputType.BOTH) {
	  if (m_SplitOutput) {
	    if (OS.isWindows())
	      items = output[0].split("\r\n");
	    else
	      items = output[0].split("\n");
	  }
	  else {
	    items = new String[]{output[0]};
	  }
	  for (i = 0; i < items.length; i++) {
	    for (String item : items) {
	      m_Conversion.setInput(item);
	      msg = m_Conversion.convert();
	      if (msg == null)
		m_Output.add(m_Conversion.getOutput());
	      else
		getLogger().severe("Failed to convert '" + item + "'!");
	    }
	  }
	}
	else {
	  m_Output.add(output);
	}
      }
    }
    catch (Exception e) {
      result = handleException("Failed to execute command: " + cmd, e);
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result = new Token(m_Output.get(0));
    m_Output.remove(0);

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_Output.size() > 0);
  }
}
