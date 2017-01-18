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
 * SpreadSheetToNumeric.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetHelper;

import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet into a purely numeric one.<br>
 * Missing cells can get replaced with a specified value or skipped.<br>
 * Booleans gets turned into 0&#47;1 (false&#47;true).<br>
 * Date&#47;time types get turned into numeric ones by using their Java epoch.<br>
 * Strings (per column) get a 0-based index assigned in the order they appear.<br>
 * Any other cell type get flagged as missing or, if provided, set to the unhandled value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-replace-missing-cells &lt;boolean&gt; (property: replaceMissingCells)
 * &nbsp;&nbsp;&nbsp;If enabled, missing cells get replaced with the specified value.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-missing-value &lt;double&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The writer setup to use for generating the string.
 * &nbsp;&nbsp;&nbsp;default: NaN
 * </pre>
 * 
 * <pre>-unhandled &lt;double&gt; (property: unhandled)
 * &nbsp;&nbsp;&nbsp;The replacement value for unhandled cell types.
 * &nbsp;&nbsp;&nbsp;default: NaN
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToNumeric
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4890225060389916155L;

  /** whether to replace missing cells. */
  protected boolean m_ReplaceMissingCells;

  /** the replacement value for missing values. */
  protected double m_MissingValue;

  /** the replacement value for unhandled values. */
  protected double m_Unhandled;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Turns a spreadsheet into a purely numeric one.\n"
        + "Missing cells can get replaced with a specified value or skipped.\n"
        + "Booleans gets turned into 0/1 (false/true).\n"
        + "Date/time types get turned into numeric ones by using their Java epoch.\n"
        + "Strings (per column) get a 0-based index assigned in the order they appear.\n"
        + "Any other cell type get flagged as missing or, if provided, set to the unhandled value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "replace-missing-cells", "replaceMissingCells",
      false);

    m_OptionManager.add(
      "missing-value", "missingValue",
      Double.NaN);

    m_OptionManager.add(
      "unhandled", "unhandled",
      Double.NaN);
  }

  /**
   * Sets whether to replace missing cells.
   *
   * @param value	true if to replace
   */
  public void setReplaceMissingCells(boolean value) {
    m_ReplaceMissingCells = value;
    reset();
  }

  /**
   * Returns whether to replace missing cells.
   *
   * @return		true if to replace
   */
  public boolean getReplaceMissingCells() {
    return m_ReplaceMissingCells;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceMissingCellsTipText() {
    return "If enabled, missing cells get replaced with the specified value.";
  }

  /**
   * Sets the replacement value for missing cells.
   *
   * @param value	the missing value
   */
  public void setMissingValue(double value) {
    m_MissingValue = value;
    reset();
  }

  /**
   * Returns the replacement value for missing cells.
   *
   * @return		the missing value
   */
  public double getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValueTipText() {
    return "The writer setup to use for generating the string.";
  }

  /**
   * Sets the replacement value for unhandled cell types.
   *
   * @param value	the unhandled value
   */
  public void setUnhandled(double value) {
    m_Unhandled = value;
    reset();
  }

  /**
   * Returns the replacement value for unhandled cell types.
   *
   * @return		the unhandled value
   */
  public double getUnhandled() {
    return m_Unhandled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String unhandledTipText() {
    return "The replacement value for unhandled cell types.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "unhandled", m_Unhandled, "unhandled: ");
    if (m_ReplaceMissingCells)
      result += QuickInfoHelper.toString(this, "missingValue", m_MissingValue, ", missing:");

    return result;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return SpreadSheetHelper.convertToNumeric(
      (SpreadSheet) m_Input,
      new HashMap<>(),
      m_ReplaceMissingCells ? m_MissingValue : null,
      m_Unhandled);
  }
}
