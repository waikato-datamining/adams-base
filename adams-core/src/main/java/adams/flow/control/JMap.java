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
 * JMap.java
 * Copyright (C) 2009-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.management.ProcessUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Runs jmap whenever a token gets passed through. The generated output gets tee-ed off.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 * Conditional equivalent:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.control.ConditionalTee
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
 * &nbsp;&nbsp;&nbsp;default: JMap
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
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stopping-timeout &lt;int&gt; (property: stoppingTimeout)
 * &nbsp;&nbsp;&nbsp;The timeout in milliseconds when waiting for actors to finish (&lt;= 0 for
 * &nbsp;&nbsp;&nbsp;infinity; see 'finishBeforeStopping').
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-asynchronous &lt;boolean&gt; (property: asynchronous)
 * &nbsp;&nbsp;&nbsp;If enabled, the sub-actors get executed asynchronously rather than the flow
 * &nbsp;&nbsp;&nbsp;waiting for them to finish before proceeding with execution.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-tee &lt;adams.flow.core.Actor&gt; [-tee ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-executable &lt;adams.core.io.PlaceholderFile&gt; (property: executable)
 * &nbsp;&nbsp;&nbsp;The full path to the jmap executable.
 * </pre>
 *
 * <pre>-additional &lt;java.lang.String&gt; (property: additionalOptions)
 * &nbsp;&nbsp;&nbsp;Additional options for the jmap execution, supports inline variables.
 * &nbsp;&nbsp;&nbsp;default: -histo:live
 * </pre>
 *
 * <pre>-pid &lt;long&gt; (property: PID)
 * &nbsp;&nbsp;&nbsp;The PID to monitor: use -1 to ignore monitoring or -999 to automatically
 * &nbsp;&nbsp;&nbsp;determine the PID of the current JVM.
 * &nbsp;&nbsp;&nbsp;default: -999
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JMap
  extends Tee {

  /** for serialization. */
  private static final long serialVersionUID = -4497496140953116320L;

  /** the jmap executable. */
  protected PlaceholderFile m_Executable;

  /** additional options for jmap. */
  protected String m_AdditionalOptions;

  /** the process ID to run jmap against (-1 is ignored). */
  protected long m_PID;

  /** the actual PID to monitor. */
  protected long m_ActualPID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Runs jmap whenever a token gets passed through. The generated "
      + "output gets tee-ed off.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "executable", "executable",
      new PlaceholderFile(getJMapExecutablePath()), false);

    m_OptionManager.add(
      "additional", "additionalOptions",
      "-histo:live");

    m_OptionManager.add(
      "pid", "PID",
      ProcessUtils.AUTO_PID);
  }

  /**
   * Returns the full path of the JMap executable, if possible.
   *
   * @return		the full path of the executable if possible, otherwise
   * 			just the executable
   */
  protected String getJMapExecutablePath() {
    return Utils.unDoubleQuote(adams.core.management.JMap.getExecutablePath());
  }

  /**
   * Sets the jmap executable.
   *
   * @param value	the executable
   */
  public void setExecutable(PlaceholderFile value) {
    m_Executable = value;
    reset();
  }

  /**
   * Returns the jmap executable.
   *
   * @return		the executable
   */
  public PlaceholderFile getExecutable() {
    return m_Executable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String executableTipText() {
    return "The full path to the jmap executable.";
  }

  /**
   * Sets the additional options for jmap.
   *
   * @param value	the additional options
   */
  public void setAdditionalOptions(String value) {
    m_AdditionalOptions = value;
    reset();
  }

  /**
   * Returns the additional options for jmap.
   *
   * @return		the additional options
   */
  public String getAdditionalOptions() {
    return m_AdditionalOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalOptionsTipText() {
    return "Additional options for the jmap execution, supports inline variables.";
  }

  /**
   * Sets the PID to monitor.
   *
   * @param value	the PID
   */
  public void setPID(long value) {
    m_PID = value;
    reset();
  }

  /**
   * Returns the PID to monitor.
   *
   * @return		the PID
   */
  public long getPID() {
    return m_PID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String PIDTipText() {
    return
        "The PID to monitor: use " + ProcessUtils.NO_PID + " to ignore monitoring or " + ProcessUtils.AUTO_PID
      + " to automatically determine the PID of the current JVM.";
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_ActualPID = m_PID;
      if (m_ActualPID == ProcessUtils.AUTO_PID)
	m_ActualPID = ProcessUtils.getVirtualMachinePID();
    }

    return result;
  }

  /**
   * Returns whether jmap can be run.
   *
   * @return		true if jmap can be executed
   */
  protected boolean canRunJMap() {
    return (m_ActualPID != ProcessUtils.NO_PID);
  }

  /**
   * Returns whether the token can be processed in the tee actor.
   *
   * @param token	the token to process
   * @return		true if token can be processed
   */
  @Override
  protected boolean canProcessInput(Token token) {
    return (super.canProcessInput(token) && canRunJMap());
  }

  /**
   * Creates the token to tee-off.
   *
   * @param token	the input token
   * @return		the token to tee-off
   */
  @Override
  protected Token createTeeToken(Token token) {
    Token	result;
    String	outputStr;
    String	additional;

    additional = getVariables().expand(m_AdditionalOptions);
    outputStr = adams.core.management.JMap.execute(
	  m_Executable.getAbsolutePath(), additional, m_ActualPID);
    if (isLoggingEnabled())
      getLogger().info("output: " + outputStr);

    result = new Token(outputStr);

    return result;
  }
}
