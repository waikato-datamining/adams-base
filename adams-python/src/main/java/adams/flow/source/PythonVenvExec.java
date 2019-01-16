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
 * PythonVenvExec.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseText;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Executes the specified executable in a Python virtual environment with the specified options and broadcasts the generated output (stdout and&#47;or stderr) continuously.<br>
 * Fails if the specified environment does not contain any 'activate' scripts typically found in such directories.
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
 * &nbsp;&nbsp;&nbsp;default: PythonVenvExec
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
 * <pre>-time-out &lt;int&gt; (property: timeOut)
 * &nbsp;&nbsp;&nbsp;The maximum time in seconds for the process to run before getting killed,
 * &nbsp;&nbsp;&nbsp; ignored if less than 1.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-executable &lt;java.lang.String&gt; (property: executable)
 * &nbsp;&nbsp;&nbsp;The executable from the virtual environment to run (no path).
 * &nbsp;&nbsp;&nbsp;default: python
 * </pre>
 *
 * <pre>-executable-options &lt;adams.core.base.BaseText&gt; (property: executableOptions)
 * &nbsp;&nbsp;&nbsp;The command-line options for the executable.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-working-directory &lt;java.lang.String&gt; (property: workingDirectory)
 * &nbsp;&nbsp;&nbsp;The current working directory for the command.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-placeholder &lt;boolean&gt; (property: optionsContainPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion for the option
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-variable &lt;boolean&gt; (property: optionsContainVariable)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic variable expansion for the option string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PythonVenvExec
  extends AbstractPythonExecution {

  /** for serialization. */
  private static final long serialVersionUID = -132045002653940359L;

  /** the executable to run. */
  protected String m_Executable;

  /** the options for the executable. */
  protected BaseText m_ExecutableOptions;

  /** the current working directory. */
  protected String m_WorkingDirectory;

  /** whether the options contain placeholders, which need to be
   * expanded first. */
  protected boolean m_OptionsContainPlaceholder;

  /** whether the options contain variables, which need to be
   * expanded first. */
  protected boolean m_OptionsContainVariable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Executes the specified executable in a Python virtual environment with the specified options and "
	+ "broadcasts the generated output (stdout and/or stderr) continuously.\n"
	+ "Fails if the specified environment does not contain any 'activate' scripts "
	+ "typically found in such directories.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "executable", "executable",
      "python");

    m_OptionManager.add(
      "executable-options", "executableOptions",
      new BaseText(""));

    m_OptionManager.add(
      "working-directory", "workingDirectory",
      "");

    m_OptionManager.add(
      "placeholder", "optionsContainPlaceholder",
      false);

    m_OptionManager.add(
      "variable", "optionsContainVariable",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "executable", m_Executable);
    value = QuickInfoHelper.toString(this, "executableOptions", (m_ExecutableOptions.isEmpty() ? "-none-" : m_ExecutableOptions), ", options: ");
    if ((value != null) && !value.isEmpty())
      result += value;

    return result;
  }

  /**
   * Sets the executable from the virtualenv to run.
   *
   * @param value	the executable
   */
  public void setExecutable(String value) {
    m_Executable = value;
    reset();
  }

  /**
   * Returns the executable from the virtualenv to run.
   *
   * @return 		the executable
   */
  public String getExecutable() {
    return m_Executable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String executableTipText() {
    return "The executable from the virtual environment to run (no path).";
  }

  /**
   * Sets the command-line options for the executable.
   *
   * @param value	the options
   */
  public void setExecutableOptions(BaseText value) {
    m_ExecutableOptions = value;
    reset();
  }

  /**
   * Returns the command-line options for the executable.
   *
   * @return 		the options
   */
  public BaseText getExecutableOptions() {
    return m_ExecutableOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String executableOptionsTipText() {
    return "The command-line options for the executable.";
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
   * Sets whether the option string contains placeholders which need to be
   * expanded first.
   *
   * @param value	true if option string contains placeholders
   */
  public void setOptionsContainPlaceholder(boolean value) {
    m_OptionsContainPlaceholder = value;
    reset();
  }

  /**
   * Returns whether the option string contains placeholders which need to be
   * expanded first.
   *
   * @return		true if command string contains placeholders
   */
  public boolean getOptionsContainPlaceholder() {
    return m_OptionsContainPlaceholder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionsContainPlaceholderTipText() {
    return "Set this to true to enable automatic placeholder expansion for the option string.";
  }

  /**
   * Sets whether the option string contains variables which need to be
   * expanded first.
   *
   * @param value	true if option string contains variables
   */
  public void setOptionsContainVariable(boolean value) {
    m_OptionsContainVariable = value;
    reset();
  }

  /**
   * Returns whether the option string contains variables which need to be
   * expanded first.
   *
   * @return		true if option string contains variables
   */
  public boolean getOptionsContainVariable() {
    return m_OptionsContainVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionsContainVariableTipText() {
    return "Set this to true to enable automatic variable expansion for the option string.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String 		options;
    String		envDir;
    List<String>	cmd;

    result = null;

    // check whether really a virtualenv
    envDir = m_Environment.getActualBinDir();
    if (!(FileUtils.fileExists(envDir + File.separator + "activate")
      || FileUtils.fileExists(envDir + File.separator + "activate.csh")
      || FileUtils.fileExists(envDir + File.separator + "activate.bat"))) {
      result = "This doesn't look like a typical virtual environment (no 'activate' scripts): " + envDir;
    }

    // preprocess options
    options = "";
    if (m_ExecutableOptions.length() > 0) {
      options = m_ExecutableOptions.getValue();
      if (m_OptionsContainVariable)
	options = getVariables().expand(options);
      if (m_OptionsContainPlaceholder)
	options = Placeholders.getSingleton().expand(options).replace("\\", "/");
    }

    cmd = new ArrayList<>();
    cmd.add(envDir + File.separator + FileUtils.fixExecutable(m_Executable));
    if (m_ExecutableOptions.length() > 0) {
      try {
	cmd.addAll(Arrays.asList(OptionUtils.splitOptions(options)));
      }
      catch (Exception e) {
	result = handleException("Failed to parse options: " + options, e);
      }
    }

    if (result == null)
      result = launch(cmd, m_WorkingDirectory);

    return result;
  }
}
