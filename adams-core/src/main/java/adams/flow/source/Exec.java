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
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.util.ArrayList;
import java.util.List;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.management.OS;
import adams.core.management.ProcessUtils;
import adams.core.management.ProcessUtils.ProcessResult;
import adams.core.option.OptionUtils;
import adams.data.conversion.ConversionFromString;
import adams.data.conversion.StringToString;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Runs an external system command and broadcasts the generated output (stdout or stderr).
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
 * Valid options are: <br><br>
 * 
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
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-cmd &lt;java.lang.String&gt; (property: command)
 * &nbsp;&nbsp;&nbsp;The external command to run.
 * &nbsp;&nbsp;&nbsp;default: ls -l .
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
 * <pre>-stderr &lt;boolean&gt; (property: outputStdErr)
 * &nbsp;&nbsp;&nbsp;If set to true, then stderr is output instead of stdout.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-split-output &lt;boolean&gt; (property: splitOutput)
 * &nbsp;&nbsp;&nbsp;If set to true, then the output gets split on newline.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.ConversionFromString&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion scheme to apply to the output.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.StringToString
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Exec
  extends AbstractSource {

  /** for serialization. */
  private static final long serialVersionUID = -132045002653940359L;

  /** the command to run. */
  protected String m_Command;

  /** whether the replace string contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_CommandContainsPlaceholder;

  /** whether the replace string contains a variable, which needs to be
   * expanded first. */
  protected boolean m_CommandContainsVariable;

  /** whether to output stderr instead of stdout. */
  protected boolean m_OutputStdErr;
  
  /** whether to split the string output on the new line before converting. */
  protected boolean m_SplitOutput;

  /** the conversion scheme to process the output with. */
  protected ConversionFromString m_Conversion;
  
  /** the tokens to forward. */
  protected List m_Output;

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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cmd", "command",
	    "ls -l .");

    m_OptionManager.add(
	    "placeholder", "commandContainsPlaceholder",
	    false);

    m_OptionManager.add(
	    "variable", "commandContainsVariable",
	    false);

    m_OptionManager.add(
	    "stderr", "outputStdErr",
	    false);

    m_OptionManager.add(
	    "split-output", "splitOutput",
	    false);

    m_OptionManager.add(
	    "conversion", "conversion",
	    new StringToString());
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
  public void setCommand(String value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to run.
   *
   * @return 		the command
   */
  public String getCommand() {
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
   * Sets whether to output stderr instead of stdout.
   *
   * @param value	if true then stderr is output instead of stdout
   */
  public void setOutputStdErr(boolean value) {
    m_OutputStdErr = value;
    reset();
  }

  /**
   * Returns whether stderr instead of stdout is output.
   *
   * @return 		true if stderr is output instead of stdout
   */
  public boolean getOutputStdErr() {
    return m_OutputStdErr;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String outputStdErrTipText() {
    return "If set to true, then stderr is output instead of stdout.";
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
    return "If set to true, then the output gets split on " + (OS.isWindows() ? "CRLF" : "newline") + ".";
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
    return "The conversion scheme to apply to the output.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
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
    ProcessResult	proc;
    String[]		items;
    String		msg;
    String		output;

    result = null;

    m_Output.clear();
    
    // preprocess command
    cmd = m_Command;
    if (m_CommandContainsVariable)
      cmd = getVariables().expand(cmd);
    if (m_CommandContainsPlaceholder)
      cmd = Placeholders.getSingleton().expand(cmd).replace("\\", "/");
    
    try {
      proc = ProcessUtils.execute(OptionUtils.splitOptions(cmd));
      if (!proc.hasSucceeded()) {
	result = proc.toErrorOutput();
      }
      else {
	if (m_OutputStdErr)
	  output = proc.getStdErr();
	else
	  output = proc.getStdOut();
	if (m_SplitOutput) {
	  if (OS.isWindows())
	    items = output.split("\r\n");
	  else
	    items = output.split("\n");
	}
	else {
	  items = new String[]{output};
	}
	for (String item: items) {
	  m_Conversion.setInput(item);
	  msg = m_Conversion.convert();
	  if (msg == null)
	    m_Output.add(m_Conversion.getOutput());
	  else
	    getLogger().severe("Failed to convert '" + item + "'!");
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
