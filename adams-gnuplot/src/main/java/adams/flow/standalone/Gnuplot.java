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
 * Gnuplot.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.core.management.ProcessUtils;
import adams.core.management.ProcessUtils.ProcessResult;

/**
 <!-- globalinfo-start -->
 * Executes Gnuplot with the specified script file.<br>
 * <br>
 * NB: Add the absolute path to the binary, if gnuplot is not on the system's path.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Gnuplot
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
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-binary &lt;java.lang.String&gt; (property: binary)
 * &nbsp;&nbsp;&nbsp;The Gnuplot binary to execute; use absolute path if not on system's path.
 * &nbsp;&nbsp;&nbsp;default: gnuplot
 * </pre>
 * 
 * <pre>-script-file &lt;adams.core.io.PlaceholderFile&gt; (property: scriptFile)
 * &nbsp;&nbsp;&nbsp;The script file to execute.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Gnuplot
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1977229579470423891L;

  /** the binary to execute. */
  protected String m_Binary;
  
  /** the script file to execute. */
  protected PlaceholderFile m_ScriptFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Executes Gnuplot with the specified script file.\n\n"
	+ "NB: Add the absolute path to the binary, if gnuplot is not on the system's path.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "binary", "binary",
	    "gnuplot");

    m_OptionManager.add(
	    "script-file", "scriptFile",
	    new PlaceholderFile("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "binary", m_Binary);
    result += QuickInfoHelper.toString(this, "scriptFile", m_ScriptFile, " ");

    return result;
  }

  /**
   * Sets the gnuplot binary to execute.
   *
   * @param value	the binary
   */
  public void setBinary(String value) {
    m_Binary = value;
    reset();
  }

  /**
   * Returns the gnuplot binary to execute.
   *
   * @return		the binary
   */
  public String getBinary() {
    return m_Binary;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binaryTipText() {
    return "The Gnuplot binary to execute; use absolute path if not on system's path.";
  }

  /**
   * Sets the script file to execute.
   *
   * @param value	the script file
   */
  public void setScriptFile(PlaceholderFile value) {
    m_ScriptFile = value;
    reset();
  }

  /**
   * Returns the script file to execute.
   *
   * @return		the script file
   */
  public PlaceholderFile getScriptFile() {
    return m_ScriptFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scriptFileTipText() {
    return "The script file to execute.";
  }
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String[]		cmd;
    ProcessResult	proc;

    result = null;

    if (isHeadless())
      return result;
    
    if (!m_ScriptFile.exists()) {
      result = "Script file '" + m_ScriptFile + "' does not exist!";
      return result;
    }
    
    cmd = new String[]{
	m_Binary,
	m_ScriptFile.getAbsolutePath()
    };
    
    try {
      proc = ProcessUtils.execute(cmd);
      if (!proc.hasSucceeded())
	result = proc.toErrorOutput();
      else
	getLogger().severe(proc.getStdErr());
    }
    catch (Exception e) {
      result = handleException("Failed to run Gnuplot with script '" + m_ScriptFile + "':", e);
    }

    return result;
  }
}
