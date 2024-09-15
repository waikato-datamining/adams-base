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
 * FileComplete.java
 * Copyright (C) 2023-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.core.io.filecomplete.AbstractFileCompleteCheck;
import adams.core.io.filecomplete.NoCheck;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if the file (from option or token) is considered complete.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The file to check; overrides the String&#47;File object in the token.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-check &lt;adams.core.io.filecomplete.AbstractFileCompleteCheck&gt; (property: check)
 * &nbsp;&nbsp;&nbsp;The check scheme to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.filecomplete.NoCheck
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileComplete
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = -6986050060604039765L;

  /** the file to look for. */
  protected PlaceholderFile m_File;

  /** the check scheme to use. */
  protected AbstractFileCompleteCheck m_Check;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Evaluates to 'true' if the file (from option or token) is considered complete.";
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
      "check", "check",
      new NoCheck());
  }

  /**
   * Sets the file to check.
   *
   * @param value	the file
   */
  public void setFile(PlaceholderFile value) {
    m_File = value;
    reset();
  }

  /**
   * Returns the file to check.
   *
   * @return		the file
   */
  public PlaceholderFile getFile() {
    return m_File;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileTipText() {
    return "The file to check; overrides the String/File object in the token.";
  }

  /**
   * Sets the 'in use' check scheme.
   *
   * @param value	the check scheme
   */
  public void setCheck(AbstractFileCompleteCheck value) {
    m_Check = value;
    reset();
  }

  /**
   * Returns the 'in use' check scheme.
   *
   * @return		the check scheme
   */
  public AbstractFileCompleteCheck getCheck() {
    return m_Check;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkTipText() {
    return "The check scheme to use.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "file", m_File, "file: ");
    result += QuickInfoHelper.toString(this, "check", m_Check, ", check: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if (m_File == null)
	result = "No file provided!";
    }

    return result;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean		result;
    PlaceholderFile	file;

    result = false;

    if (m_File.exists() && !m_File.isDirectory()) {
      result = m_Check.isComplete(m_File);
      if (isLoggingEnabled())
	getLogger().info(m_File + " -> " + result);
    }
    else if ((token.hasPayload(String.class)) || (token.hasPayload(File.class))) {
      file = new PlaceholderFile("" + token.getPayload());
      if (file.exists() && !file.isDirectory()) {
	result = m_Check.isComplete(file);
	if (isLoggingEnabled())
	  getLogger().info(m_File + " -> " + result);
      }
    }

    return result;
  }
}
