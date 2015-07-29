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
 * WaitForFile.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MultiAttemptWithWaitSupporter;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Waits for the file passing through to become available, i.e., not in use by another process.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
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
 * &nbsp;&nbsp;&nbsp;default: WaitForFile
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-max-attempts &lt;int&gt; (property: numAttempts)
 * &nbsp;&nbsp;&nbsp;The maximum number of intervals to wait.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-attempt-nterval &lt;int&gt; (property: attemptInterval)
 * &nbsp;&nbsp;&nbsp;The interval in milli-seconds to wait before continuing with the execution.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-generate-error &lt;boolean&gt; (property: generateError)
 * &nbsp;&nbsp;&nbsp;If enabled, will generate an error in case the maximum number of waits has 
 * &nbsp;&nbsp;&nbsp;been reached and the file is in use.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WaitForFile
  extends AbstractTransformer
  implements MultiAttemptWithWaitSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3383735680425581504L;

  /** the maximum number of interval to wait. */
  protected int m_NumAttempts;

  /** the interval in milli-seconds to wait. */
  protected int m_AttemptInterval;

  /** whether to generate an error, in case the file is still not available after the maximum wait. */
  protected boolean m_GenerateError;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Waits for the file passing through to become available, i.e., not in use by another process.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-attempts", "numAttempts",
      10, 1, null);

    m_OptionManager.add(
      "attempt-nterval", "attemptInterval",
      100, 1, null);

    m_OptionManager.add(
      "generate-error", "generateError",
      false);
  }

  /**
   * Sets the maximum number of intervals to wait.
   *
   * @param value	the maximum
   */
  public void setNumAttempts(int value) {
    if (getOptionManager().isValid("numAttempts", value)) {
      m_NumAttempts = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of intervals to wait.
   *
   * @return		the maximum
   */
  public int getNumAttempts() {
    return m_NumAttempts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numAttemptsTipText() {
    return "The maximum number of intervals to wait.";
  }

  /**
   * Sets the interval in milli-seconds to wait.
   *
   * @param value	the interval
   */
  public void setAttemptInterval(int value) {
    if (getOptionManager().isValid("attemptInterval", value)) {
      m_AttemptInterval = value;
      reset();
    }
  }

  /**
   * Returns the interval to wait in milli-seconds.
   *
   * @return		the interval
   */
  public int getAttemptInterval() {
    return m_AttemptInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attemptIntervalTipText() {
    return "The interval in milli-seconds to wait before continuing with the execution.";
  }

  /**
   * Sets whether to generate an error if the maximum number of waits has
   * been reached, but the file is still in use.
   *
   * @param value	true if to generate error
   */
  public void setGenerateError(boolean value) {
    m_GenerateError = value;
    reset();
  }

  /**
   * Returns whether to generate an error if the maximum number of waits has
   * been reached, but the file is still in use.
   *
   * @return		true if to generate error
   */
  public boolean getGenerateError() {
    return m_GenerateError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generateErrorTipText() {
    return
      "If enabled, will generate an error in case the maximum number of waits "
	+ "has been reached and the file is in use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "numAttempts", m_NumAttempts);
    result += " * ";
    result += QuickInfoHelper.toString(this, "attemptInterval", m_AttemptInterval) + "ms";

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class, java.io.File.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    int		count;
    File	file;
    String	msg;
    boolean	inUse;

    result = null;

    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else
      file = (File) m_InputToken.getPayload();

    // wait
    count = 0;
    while ((count < m_NumAttempts) && !isStopped()) {
      if (!FileUtils.isOpen(file))
	break;
      count++;
      Utils.wait(this, m_AttemptInterval, Math.min(100, m_AttemptInterval));
    }

    if (!isStopped()) {
      inUse = FileUtils.isOpen(file);
      if (isLoggingEnabled())
	getLogger().info("count=" + count + ", inUse=" + inUse + ", file=" + file);

      // still open?
      if ((count == m_NumAttempts) && inUse) {
	msg = "File '" + file + "' is still in use after " + m_NumAttempts + " * " + m_AttemptInterval + "msec!";
	if (m_GenerateError)
	  result = msg;
	else
	  getLogger().warning(msg);
      }

      m_OutputToken = m_InputToken;
    }

    return result;
  }
}
