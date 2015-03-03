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
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import adams.core.io.PlaceholderFile;
import adams.core.management.ProcessUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Runs jmap whenever a token gets passed through. The generated output gets tee-ed off.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: JMap
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-progress (property: showProgress)
 * &nbsp;&nbsp;&nbsp;If set to true, progress information will be output to stdout ('.').
 * </pre>
 *
 * <pre>-stop-on-errors (property: stopOnErrors)
 * &nbsp;&nbsp;&nbsp;If set to true, errors (like exceptions) will stop the flow execution; otherwise
 * &nbsp;&nbsp;&nbsp;it is attempted to continue.
 * </pre>
 *
 * <pre>-tee &lt;adams.flow.core.AbstractActor [options]&gt; (property: teeActor)
 * &nbsp;&nbsp;&nbsp;The actor to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.Null
 * </pre>
 *
 * <pre>-executable &lt;adams.core.io.PlaceholderFile&gt; (property: executable)
 * &nbsp;&nbsp;&nbsp;The full path to the jmap executable.
 * </pre>
 *
 * <pre>-additional &lt;java.lang.String&gt; (property: additionalOptions)
 * &nbsp;&nbsp;&nbsp;Additional options for the jmap execution.
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
 * @version $Revision$
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
   * Returns the filename of the jmap executable.
   *
   * @return		the executable
   */
  protected String getJMapExecutable() {
    String	result;
    String	os;

    os = System.getProperty("os.name").toLowerCase();
    if (os.indexOf("windows") > -1)
      result = "jmap.exe";
    else
      result = "jmap";

    return result;
  }

  /**
   * Returns the full path of the JMap executable, if possible.
   *
   * @return		the full path of the executable if possible, otherwise
   * 			just the executable
   */
  protected String getJMapExecutablePath() {
    String	result;
    File	jvmPath;

    jvmPath = new File(System.getProperty("java.home"));
    if (jvmPath.getAbsolutePath().endsWith(File.separator + "jre"))
      jvmPath = jvmPath.getParentFile();
    jvmPath = new File(jvmPath.getAbsolutePath() + File.separator + "bin");
    if (jvmPath.exists())
      result = jvmPath.getAbsolutePath() + File.separator + getJMapExecutable();
    else
      result = getJMapExecutable();

    return result;
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
    return "Additional options for the jmap execution.";
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

    outputStr = adams.core.management.JMap.execute(
	  m_Executable.getAbsolutePath(), m_AdditionalOptions, m_ActualPID);
    if (isLoggingEnabled())
      getLogger().info("output: " + outputStr);

    result = new Token(outputStr);

    return result;
  }
}
