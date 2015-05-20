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
 * BreakUpString.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Breaks up a string into multiple lines if wider than the specified number of columns. Uses word-boundaries.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-columns &lt;int&gt; (property: numColumns)
 * &nbsp;&nbsp;&nbsp;The maximum number of columns (ie characters) of the generated string.
 * &nbsp;&nbsp;&nbsp;default: 72
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BreakUpString
  extends AbstractStringConversion {

  /** for serialization. */
  private static final long serialVersionUID = 6233539723999989507L;
  
  /** the number of columns. */
  protected int m_NumColumns;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Breaks up a string into multiple lines if wider than the specified "
	+ "number of columns. Uses word-boundaries.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-columns", "numColumns",
	    72, 1, null);
  }

  /**
   * Sets the maximum number of columns/chars per line.
   *
   * @param value	the number of columns
   */
  public void setNumColumns(int value) {
    m_NumColumns = value;
    reset();
  }

  /**
   * Returns the maximum number of columns/chars per line.
   *
   * @return		the number of columns
   */
  public int getNumColumns() {
    return m_NumColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numColumnsTipText() {
    return "The maximum number of columns (ie characters) of the generated string.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "numColumns", m_NumColumns);
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return Utils.flatten(Utils.breakUp((String) m_Input, m_NumColumns), "\n");
  }
}
