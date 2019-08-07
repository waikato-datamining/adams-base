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
 * SpreadSheetCellRegExp.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.groupextraction;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Applies regular expression to the cell value of the spreadsheet row and
 * returns the specified expression group.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetCellRegExp
  extends AbstractGroupExtractor
  implements SpreadSheetRowGroupExtractorWithColumn {

  private static final long serialVersionUID = 6130414784797102811L;

  /** the column index. */
  protected SpreadSheetColumnIndex m_Column;

  /** the regular expression to match. */
  protected BaseRegExp m_RegExp;

  /** the group to extract. */
  protected String m_Group;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies regular expression to the cell value of the spreadsheet "
      + "row and returns the specified expression group.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "column", "column",
      new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp("(.*)"));

    m_OptionManager.add(
      "group", "group",
      "$1");
  }

  /**
   * Sets the column to get the group from.
   *
   * @param value	the column
   */
  @Override
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column to get the group from.
   *
   * @return		the column
   */
  @Override
  public SpreadSheetColumnIndex getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String columnTipText() {
    return "The column of the cell to get the group from.";
  }

  /**
   * Sets the regular expression to apply to the cell value.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to apply to the cell value.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to apply to the cell value.";
  }

  /**
   * Sets the group to extract.
   *
   * @param value	the group
   */
  public void setGroup(String value) {
    m_Group = value;
    reset();
  }

  /**
   * Returns the group to extract.
   *
   * @return		the group
   */
  public String getGroup() {
    return m_Group;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupTipText() {
    return "The group of the expression to extract.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "column", m_Column, "col:");
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");
    result += QuickInfoHelper.toString(this, "group", m_Group, ", group: ");

    return result;
  }

  /**
   * Checks whether the data type is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean handles(Object obj) {
    return (obj instanceof Row);
  }

  /**
   * Extracts the group from the object.
   *
   * @param obj		the object to process
   * @return		the extracted group, null if failed to extract or not handled
   */
  @Override
  protected String doExtractGroup(Object obj) {
    String	result;
    Row		row;
    Cell 	cell;
    String	value;

    result = null;

    row = (Row) obj;
    m_Column.setSpreadSheet(row.getOwner());
    if (m_Column.getIntIndex() == -1)
      throw new IllegalStateException("Failed to locate column: " + m_Column);

    value = null;
    cell = row.getCell(m_Column.getIntIndex());
    if ((cell != null) && !cell.isMissing())
      value = cell.getContent();

    if (value != null)
      result = value.replaceAll(m_RegExp.getValue(), m_Group);

    return result;
  }
}
