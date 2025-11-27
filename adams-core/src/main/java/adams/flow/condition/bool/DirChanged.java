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
 * DirChanged.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.dirchanged.DirChangeMonitor;
import adams.core.io.dirchanged.NoChange;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if the dir change monitor detects a change.
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
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: dir)
 * &nbsp;&nbsp;&nbsp;The directory to check.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-check &lt;adams.core.io.dirchanged.DirChangeMonitor&gt; (property: check)
 * &nbsp;&nbsp;&nbsp;The dir changed monitor to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.dirchanged.NoChange
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DirChanged
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = -6986050060604039765L;

  /** the file to look for. */
  protected PlaceholderDirectory m_Dir;

  /** the check scheme to use. */
  protected DirChangeMonitor m_Check;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Evaluates to 'true' if the dir change monitor detects a change.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "dir", "dir",
      new PlaceholderDirectory("."));

    m_OptionManager.add(
      "check", "check",
      new NoChange());
  }

  /**
   * Sets the file to check.
   *
   * @param value	the file
   */
  public void setDir(PlaceholderDirectory value) {
    m_Dir = value;
    reset();
  }

  /**
   * Returns the dir to check.
   *
   * @return		the dir
   */
  public PlaceholderDirectory getDir() {
    return m_Dir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dirTipText() {
    return "The directory to check.";
  }

  /**
   * Sets the 'dir changed' check scheme.
   *
   * @param value	the check scheme
   */
  public void setCheck(DirChangeMonitor value) {
    m_Check = value;
    reset();
  }

  /**
   * Returns the 'dir changed' check scheme.
   *
   * @return		the check scheme
   */
  public DirChangeMonitor getCheck() {
    return m_Check;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkTipText() {
    return "The dir changed monitor to use.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "dir", m_Dir, "dir: ");
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
      if (m_Dir == null)
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

    result = false;

    if (m_Dir.exists() && m_Dir.isDirectory()) {
      if (!m_Check.isInitialized(m_Dir))
	m_Check.initialize(m_Dir);
      result = m_Check.hasChanged(m_Dir);
      m_Check.update(m_Dir);
    }

    return result;
  }
}
