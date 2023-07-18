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
 * FileIncomplete.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io.fileuse;

import adams.core.io.filecomplete.AbstractFileCompleteCheck;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Assumes the file to be in use when not complete, i.e., still being written.
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
 * <pre>-check &lt;adams.core.io.filecomplete.AbstractFileCompleteCheck&gt; (property: check)
 * &nbsp;&nbsp;&nbsp;The check scheme to use for checking the 'in use' state (incomplete -&gt; in
 * &nbsp;&nbsp;&nbsp;use).
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.filecomplete.NoCheck
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileIncomplete
  extends AbstractFileUseCheck {

  private static final long serialVersionUID = 8298169274868083275L;

  /** the check to use. */
  protected AbstractFileCompleteCheck m_Check;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Assumes the file to be in use when not complete, i.e., still being written.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "check", "check",
      new adams.core.io.filecomplete.NoCheck());
  }

  /**
   * Sets the file check to use for checking the completeness.
   *
   * @param value	the check
   */
  public void setCheck(AbstractFileCompleteCheck value) {
    m_Check = value;
    reset();
  }

  /**
   * Returns the file check to use for checking the completeness.
   *
   * @return		the check
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
    return "The check scheme to use for checking the 'in use' state (incomplete -> in use).";
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file the file to check
   * @return true if in use
   */
  @Override
  public boolean isInUse(File file) {
    return !m_Check.isComplete(file);
  }
}
