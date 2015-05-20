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
 * AbstractScriptletWithDataFile.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

import adams.core.io.PlaceholderFile;

/**
 * Ancestor for scriplets that generate Gnuplot scripts (or parts of it).
 * Specifies the data file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptletWithDataFile
  extends AbstractScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = 8269710957096517396L;

  /** the data file to use. */
  protected PlaceholderFile m_DataFile;

  /** whether to use absolute path for data file. */
  protected boolean m_UseAbsolutePath;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "data-file", "dataFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "use-absolute-path", "useAbsolutePath",
      true);
  }

  /**
   * Resets the scriptlet.
   */
  protected void reset() {
    super.reset();

    m_LastError = null;
  }

  /**
   * Sets the data file to use.
   *
   * @param value	the data file
   */
  public void setDataFile(PlaceholderFile value) {
    m_DataFile = value;
    reset();
  }

  /**
   * Returns the data file in use.
   *
   * @return		the data file
   */
  public PlaceholderFile getDataFile() {
    return m_DataFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String dataFileTipText() {
    return "The data file to use as basis for the plot.";
  }

  /**
   * Sets whether to use the data file's absolute path.
   *
   * @param value	true if to use absolute path
   */
  public void setUseAbsolutePath(boolean value) {
    m_UseAbsolutePath = value;
    reset();
  }

  /**
   * Returns whether to use the data file's absolute path.
   *
   * @return		true if to use absolute path
   */
  public boolean getUseAbsolutePath() {
    return m_UseAbsolutePath;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String useAbsolutePathTipText() {
    return "If enabled, the absolute path of the data file is used, otherwise just its name.";
  }

  /**
   * Returns the actual data file to use in the scripts.
   *
   * @return		the actual filename
   */
  public String getActualDataFile() {
    if (m_UseAbsolutePath)
      return getDataFile().getAbsolutePath();
    else
      return getDataFile().getName();
  }

  /**
   * Hook method for performing checks.
   * <p/>
   * Default implementation only checks whether the data file is available.
   *
   * @return		null if all checks passed, otherwise error message
   */
  public String check() {
    if (!m_DataFile.exists())
      return "Data file '" + m_DataFile + "' does not exist!";

    if (!m_DataFile.isFile())
      return "'" + m_DataFile + "' is not a file?";

    return null;
  }
}
