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
 * AbstractScriptlet.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for scriplets that generate Gnuplot scripts (or parts of it).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptlet
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8269710957096517396L;

  /** the character for comments in Gnuplot scripts. */
  public final static String COMMENT = "#";

  /** the data file to use. */
  protected PlaceholderFile m_DataFile;

  /** stores the error message if the check failed. */
  protected String m_LastError;
  
  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "data-file", "dataFile",
	    new PlaceholderFile("."));
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
   * Checks whether an error was encountered during the last generation.
   *
   * @return		true if an error was encountered
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Returns the error that occurred during the last generation.
   *
   * @return		the error, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
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

  /**
   * Generates the actual script code.
   *
   * @return		the script code, null in case of an error
   */
  protected abstract String doGenerate();
  
  /**
   * Returns the generated script-code string.
   *
   * @return		the script-code, null in case of an error
   */
  public String generate() {
    m_LastError = check();
    if (hasLastError())
      return null;
    return doGenerate();
  }
}
