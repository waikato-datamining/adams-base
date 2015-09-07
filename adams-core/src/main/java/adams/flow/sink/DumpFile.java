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
 * DumpFile.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.BufferSupporter;
import adams.core.MultiAttemptWithWaitSupporter;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.FileEncodingSupporter;
import adams.core.io.FileUtils;
import adams.flow.core.Unknown;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Actor that just dumps any input token into a separate line of the specified output file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: DumpFile
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The name of the output file.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-append &lt;boolean&gt; (property: append)
 * &nbsp;&nbsp;&nbsp;If set to true, file gets only appended.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when writing to the file, use empty string for 
 * &nbsp;&nbsp;&nbsp;default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-num-attempts &lt;int&gt; (property: numAttempts)
 * &nbsp;&nbsp;&nbsp;The number of attempts for writing the data.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-attempt-interval &lt;int&gt; (property: attemptInterval)
 * &nbsp;&nbsp;&nbsp;The time in msec to wait before the next attempt.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-buffer-size &lt;int&gt; (property: bufferSize)
 * &nbsp;&nbsp;&nbsp;The number of lines to buffer before writing to disk, in order to improve 
 * &nbsp;&nbsp;&nbsp;I&#47;O performance.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DumpFile
  extends AbstractAppendableFileWriter 
  implements FileEncodingSupporter, MultiAttemptWithWaitSupporter, BufferSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -366362262032858011L;

  /** the key for storing the buffer in the backup. */
  public final static String BACKUP_BUFFER = "buffer";

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** the number of tries for writing the data. */
  protected int m_NumAttempts;

  /** the interval between attempts. */
  protected int m_AttemptInterval;

  /** the size of the buffer. */
  protected int m_BufferSize;

  /** the buffer. */
  protected List<String> m_Buffer;

  /** the time in msec to wait between attempts. */

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor that just dumps any input token into a separate line of the "
      + "specified output file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "num-attempts", "numAttempts",
      1, 1, null);

    m_OptionManager.add(
      "attempt-interval", "attemptInterval",
      1000, 0, null);

    m_OptionManager.add(
      "buffer-size", "bufferSize",
      1, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Buffer = new ArrayList<>();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The name of the output file.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String appendTipText() {
    return "If set to true, file gets only appended.";
  }
  
  /**
   * Sets the encoding to use.
   * 
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }
  
  /**
   * Returns the encoding to use.
   * 
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when writing to the file, use empty string for default.";
  }

  /**
   * Sets the number of attempts.
   *
   * @param value	the number of attempts
   */
  @Override
  public void setNumAttempts(int value) {
    if (value >= 1) {
      m_NumAttempts = value;
      reset();
    }
    else {
      getLogger().warning("Number of attempts must at least 1, provided: " + value);
    }
  }

  /**
   * Returns the number of attempts.
   *
   * @return		the number of attempts
   */
  @Override
  public int getNumAttempts() {
    return m_NumAttempts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String numAttemptsTipText() {
    return "The number of attempts for writing the data.";
  }

  /**
   * Sets the time to wait between attempts in msec.
   *
   * @param value	the time in msec
   */
  @Override
  public void setAttemptInterval(int value) {
    if (getOptionManager().isValid("numAttempts", value)) {
      m_AttemptInterval = value;
      reset();
    }
  }

  /**
   * Returns the time to wait between attempts in msec.
   *
   * @return		the time in msec
   */
  @Override
  public int getAttemptInterval() {
    return m_AttemptInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String attemptIntervalTipText() {
    return "The time in msec to wait before the next attempt.";
  }

  /**
   * Sets the number of lines to buffer before writing them to disk.
   *
   * @param value	the number of lines to buffer
   */
  public void setBufferSize(int value) {
    if (getOptionManager().isValid("bufferSize", value)) {
      m_BufferSize = value;
      reset();
    }
  }

  /**
   * Returns the number of lines to buffer before writing them to disk.
   *
   * @return		the number of lines to buffer
   */
  public int getBufferSize() {
    return m_BufferSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bufferSizeTipText() {
    return
        "The number of lines to buffer before writing to disk, in order to "
	+ "improve I/O performance.";
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

    result = super.getQuickInfo();

    if (result != null) {
      value = QuickInfoHelper.toString(this, "bufferSize", (m_BufferSize > 1 ? m_BufferSize : null), ", buffering: ");
      if (value != null)
        result += value;
    }

    return result;
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_BUFFER, m_Buffer);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_BUFFER)) {
      m_Buffer = (List<String>) state.get(BACKUP_BUFFER);
      state.remove(BACKUP_BUFFER);
    }

    super.restoreState(state);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Writes the content of the buffer to disk.
   *
   * @return		error message is something went wrong, null otherwise
   */
  protected String writeToDisk() {
    String    result;
    int       attempt;
    boolean   finished;

    result   = null;
    finished = false;
    attempt  = 0;
    while (!finished) {
      attempt++;
      result = FileUtils.writeToFileMsg(
	m_OutputFile.getAbsolutePath(),
	Utils.flatten(m_Buffer, System.getProperty("line.separator")),
	m_Append,
	m_Encoding.charsetValue().name());
      finished = (attempt == m_NumAttempts) || (result == null);
      if (!finished && (result != null)) {
	if (isLoggingEnabled())
	  getLogger().info("Attempt " + attempt + "/" + m_NumAttempts + " failed, retrying...");
	if (m_AttemptInterval > 0)
	  Utils.wait(this, this, m_AttemptInterval, 100);
      }
    }

    m_Buffer.clear();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String    result;

    result = null;
    m_Buffer.add("" + m_InputToken.getPayload());
    if ((m_Buffer.size() >= m_BufferSize) || !m_Append)
      result = writeToDisk();

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    // write any left over data to disk
    if (m_Buffer.size() > 0)
      writeToDisk();

    super.wrapUp();
  }
}
