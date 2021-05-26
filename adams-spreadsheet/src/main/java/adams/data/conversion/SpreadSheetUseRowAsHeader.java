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
 * SpreadSheetUseRowAsHeader.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetRowIndex;

/**
 <!-- globalinfo-start -->
 * Uses the values of the specified data row for the header.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-row &lt;adams.data.spreadsheet.SpreadSheetRowIndex&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The row to use as the new header.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-force-string &lt;boolean&gt; (property: forceString)
 * &nbsp;&nbsp;&nbsp;If enabled, the value is set as string, even if it resembles a number.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-delete &lt;boolean&gt; (property: delete)
 * &nbsp;&nbsp;&nbsp;If enabled, the row gets deleted after updating the header.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetUseRowAsHeader
  extends AbstractInPlaceSpreadSheetConversion {

  private static final long serialVersionUID = 2431670205967099683L;

  /** the row to use as header. */
  protected SpreadSheetRowIndex m_Row;

  /** whether to set value as string. */
  protected boolean m_ForceString;

  /** whether to delete the row after updating the header. */
  protected boolean m_Delete;
  
  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the values of the specified data row for the header.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "row", "row",
      new SpreadSheetRowIndex("1"));

    m_OptionManager.add(
      "force-string", "forceString",
      false);

    m_OptionManager.add(
      "delete", "delete",
      false);
  }

  /**
   * Sets the row to use as new header.
   *
   * @param value	the row
   */
  public void setRow(SpreadSheetRowIndex value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the row to use as new header.
   *
   * @return		the row
   */
  public SpreadSheetRowIndex getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The row to use as the new header.";
  }

  /**
   * Sets whether to force setting the value as string even if it resembles
   * a number.
   *
   * @param value	true if to force string
   */
  public void setForceString(boolean value) {
    m_ForceString = value;
    reset();
  }

  /**
   * Returns whether to force setting the value as string even if it resembles
   * a number.
   *
   * @return		true if string type is enforced
   */
  public boolean getForceString() {
    return m_ForceString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forceStringTipText() {
    return "If enabled, the value is set as string, even if it resembles a number.";
  }

  /**
   * Sets whether to delete the row after updating the header.
   *
   * @param value	true if to delete the afterwards
   */
  public void setDelete(boolean value) {
    m_Delete = value;
    reset();
  }

  /**
   * Returns whether to delete the row after updating the header.
   *
   * @return		true if to delete the row afterwards
   */
  public boolean getDelete() {
    return m_Delete;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String deleteTipText() {
    return "If enabled, the row gets deleted after updating the header.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result = QuickInfoHelper.toString(this, "row", m_Row, "row: ");
    result += QuickInfoHelper.toString(this, "forceString", m_ForceString, "force string", ", ");
    result += QuickInfoHelper.toString(this, "delete", m_Delete, "delete afterwards", ", ");
    
    return result;
  }

  /**
   * Generates the new spreadsheet from the input.
   *
   * @param input the incoming spreadsheet
   * @throws Exception if conversion fails for some reason
   * @return the generated spreadsheet
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet		result;
    int			index;
    Row			row;
    Row			header;
    int			i;

    result = input;
    if (!m_NoCopy)
      result = result.getClone();

    m_Row.setSpreadSheet(input);
    index = m_Row.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("Row not found: " + m_Row.getIndex());

    header = result.getHeaderRow();
    row    = result.getRow(index);
    for (i = 0; i < result.getColumnCount(); i++) {
      if (!row.hasCell(i) || row.getCell(i).isMissing()) {
	header.getCell(i).setMissing();
      }
      else {
        if (m_ForceString)
	  header.getCell(i).setContentAsString(row.getCell(i).getContent());
        else
	  header.getCell(i).setContent(row.getCell(i).getContent());
      }
    }

    if (m_Delete)
      result.removeRow(index);

    return result;
  }
}
