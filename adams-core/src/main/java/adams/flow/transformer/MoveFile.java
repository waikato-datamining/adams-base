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
 * MoveFile.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.AtomicMoveSupporter;
import adams.core.MultiAttemptWithWaitSupporter;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Moves a file to a different location (or just renames it).<br>
 * Source and target can be swapped as well.<br>
 * Simply forwards the input token if moving was successful.
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
 * &nbsp;&nbsp;&nbsp;default: MoveFile
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
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The target file or directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-input-is-target &lt;boolean&gt; (property: inputIsTarget)
 * &nbsp;&nbsp;&nbsp;If true, then the input token will be used as target and the file parameter 
 * &nbsp;&nbsp;&nbsp;as source.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-atomic-move &lt;boolean&gt; (property: atomicMove)
 * &nbsp;&nbsp;&nbsp;If true, then an atomic move operation will be attempted (NB: not supported 
 * &nbsp;&nbsp;&nbsp;by all operating systems).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-num-attempts &lt;int&gt; (property: numAttempts)
 * &nbsp;&nbsp;&nbsp;The number of attempts for moving.
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
 * <pre>-output-moved-file &lt;boolean&gt; (property: outputMovedFile)
 * &nbsp;&nbsp;&nbsp;If true, then the moved file rather than the input file gets forwarded.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MoveFile
  extends AbstractTransformer
  implements MultiAttemptWithWaitSupporter, AtomicMoveSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -1725398133887399010L;

  /** the target. */
  protected PlaceholderFile m_File;

  /** whether the input token is the target instead. */
  protected boolean m_InputIsTarget;

  /** whether to perform an atomic move. */
  protected boolean m_AtomicMove;

  /** the number of tries for writing the data. */
  protected int m_NumAttempts;

  /** the interval between attempts. */
  protected int m_AttemptInterval;

  /** whether to output the moved file rather than the input. */
  protected boolean m_OutputMovedFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Moves a file to a different location (or just renames it).\n"
        + "Source and target can be swapped as well.\n"
        + "Simply forwards the input token if moving was successful.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "file", "file",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "input-is-target", "inputIsTarget",
      false);

    m_OptionManager.add(
      "atomic-move", "atomicMove",
      false);

    m_OptionManager.add(
      "num-attempts", "numAttempts",
      1, 1, null);

    m_OptionManager.add(
      "attempt-interval", "attemptInterval",
      1000, 0, null);

    m_OptionManager.add(
      "output-moved-file", "outputMovedFile",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "file", m_File);

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "inputIsTarget", m_InputIsTarget, "input is target"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "atomicMove", m_AtomicMove, "atomic move"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputMovedFile", m_OutputMovedFile, "output moved file"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the target file.
   *
   * @param value	the file
   */
  public void setFile(PlaceholderFile value) {
    m_File = value;
    reset();
  }

  /**
   * Returns the target file.
   *
   * @return 		the file
   */
  public PlaceholderFile getFile() {
    return m_File;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileTipText() {
    return "The target file or directory.";
  }

  /**
   * Sets whether the input is the actual target, not the file.
   *
   * @param value	if true then the input will be used as target
   * 			and the file as source
   */
  public void setInputIsTarget(boolean value) {
    m_InputIsTarget = value;
    reset();
  }

  /**
   * Returns whether the input is the actual target, not the file.
   *
   * @return 		true if the input will be used as target and the
   * 			file as source
   */
  public boolean getInputIsTarget() {
    return m_InputIsTarget;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputIsTargetTipText() {
    return
        "If true, then the input token will be used as target and the file "
      + "parameter as source.";
  }

  /**
   * Sets whether to attempt atomic move operation.
   *
   * @param value	if true then attempt atomic move operation
   */
  public void setAtomicMove(boolean value) {
    m_AtomicMove = value;
    reset();
  }

  /**
   * Returns whether to attempt atomic move operation.
   *
   * @return 		true if to attempt atomic move operation
   */
  public boolean getAtomicMove() {
    return m_AtomicMove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String atomicMoveTipText() {
    return
        "If true, then an atomic move operation will be attempted "
	  + "(NB: not supported by all operating systems).";
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
    return "The number of attempts for moving.";
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
   * Sets whether to output the moved file rather than the input file.
   *
   * @param value	if true then the moved file is output
   */
  public void setOutputMovedFile(boolean value) {
    m_OutputMovedFile = value;
    reset();
  }

  /**
   * Returns whether to output the moved file rather than the input file.
   *
   * @return 		true if to output the moved file
   */
  public boolean getOutputMovedFile() {
    return m_OutputMovedFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputMovedFileTipText() {
    return "If true, then the moved file rather than the input file gets forwarded.";
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
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
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
    String	result;
    File	file;
    File	source;
    File	target;
    int       	attempt;
    boolean   	finished;

    result = null;

    if (m_InputToken.getPayload() instanceof File)
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    else
      file = new PlaceholderFile((String) m_InputToken.getPayload());

    if (m_InputIsTarget) {
      target = file;
      source = m_File;
    }
    else {
      target = m_File;
      source = file;
    }

    getLogger().info("Source '" + source + "' exists: " + source.exists());
    getLogger().info("Target '" + target + "' exists: " + target.exists());

    attempt  = 0;
    finished = false;
    while (!finished) {
      attempt++;
      result = null;
      try {
	if (!FileUtils.move(source.getAbsoluteFile(), target.getAbsoluteFile(), m_AtomicMove))
	  result = "Failed to move file" + (m_AtomicMove ? "(atomic)" : "") + ": " + source + " -> " + target;
      }
      catch (Exception e) {
	result = handleException("Failed to move file" + (m_AtomicMove ? "(atomic)" : "") + ": " + source + " -> " + target, e);
      }
      finished = (attempt == m_NumAttempts) || (result == null);
      if (!finished && (result != null)) {
	if (isLoggingEnabled())
	  getLogger().info("Attempt " + attempt + "/" + m_NumAttempts + " failed, retrying...");
	if (m_AttemptInterval > 0)
	  Utils.wait(this, this, m_AttemptInterval, 100);
      }
    }

    if (result == null) {
      if (m_OutputMovedFile) {
	if (target.isDirectory())
	  file = new PlaceholderFile(target.getAbsolutePath() + File.separator + source.getName());
	else
	  file = new PlaceholderFile(target.getAbsolutePath());
	if (m_InputToken.getPayload() instanceof File)
	  m_OutputToken = new Token(file);
	else
	  m_OutputToken = new Token(file.getAbsolutePath());
      }
      else {
	m_OutputToken = new Token(m_InputToken.getPayload());
      }
    }

    return result;
  }
}
