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

/**
 * MultiCheck.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileuse;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Applies the specified checks sequentially, stops as soon one of them returns that the file is 'in use'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-check &lt;adams.core.io.fileuse.AbstractFileUseCheck&gt; [-check ...] (property: checks)
 * &nbsp;&nbsp;&nbsp;The schemes to use for checking the 'in use' state.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiCheck
  extends AbstractFileUseCheck {

  private static final long serialVersionUID = -3766862011655514895L;

  /** the checks to use. */
  protected AbstractFileUseCheck[] m_Checks;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies the specified checks sequentially, stops as soon one of them "
        + "returns that the file is 'in use'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "check", "checks",
      new AbstractFileUseCheck[0]);
  }

  /**
   * Sets the check schemes to use.
   *
   * @param value	the checks
   */
  public void setChecks(AbstractFileUseCheck[] value) {
    m_Checks = value;
    reset();
  }

  /**
   * Returns the check schemes to use.
   *
   * @return		the checks
   */
  public AbstractFileUseCheck[] getChecks() {
    return m_Checks;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checksTipText() {
    return "The schemes to use for checking the 'in use' state.";
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file	the file to check
   * @return		true if in use
   */
  @Override
  public boolean isInUse(File file) {
    boolean	result;

    result = false;

    if (m_Checks.length == 0)
      getLogger().warning("No check schemes defined!");

    for (AbstractFileUseCheck check: m_Checks) {
      result = check.isInUse(file);
      if (isLoggingEnabled())
	getLogger().info(check.getClass().getName() + " on " + file + ": " + result);
      if (result)
	break;
    }

    return result;
  }
}
