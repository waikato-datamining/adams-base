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
 * SpreadSheetAddFormulaRow.java
 * Copyright (C) 2013-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Range;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.parser.SpreadSheetFormulaText;

/**
 <!-- globalinfo-start -->
 * Adds a row with a user-supplied formula for the specified columns.
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
 * <pre>-formula &lt;adams.parser.SpreadSheetFormulaText&gt; (property: formula)
 * &nbsp;&nbsp;&nbsp;The formula to add (incl '='); use '&#64;' as placeholder for the current column
 * &nbsp;&nbsp;&nbsp;and '#' for the last row.
 * &nbsp;&nbsp;&nbsp;default: =sum(&#64;1:&#64;#)
 * </pre>
 *
 * <pre>-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The range of columns to add the formula for; A range is a comma-separated
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; column names (case-sensitive) as well
 * &nbsp;&nbsp;&nbsp;as the following placeholders can be used: first, second, third, last_2,
 * &nbsp;&nbsp;&nbsp;last_1, last; numeric indices can be enforced by preceding them with '#'
 * &nbsp;&nbsp;&nbsp;(eg '#12'); column names can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-expand-variables &lt;boolean&gt; (property: expandVariables)
 * &nbsp;&nbsp;&nbsp;If enabled, any variables in the formula get expanded first before adding
 * &nbsp;&nbsp;&nbsp;it to the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetAddFormulaRow
  extends AbstractInPlaceSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = 6046280641743329345L;

  /** the placeholder for the column. */
  public final static String PLACEHOLDER_COLUMN = "@";

  /** the placeholder for the last row. */
  public final static String PLACEHOLDER_LAST_ROW = "#";

  /** the formula to add. */
  protected SpreadSheetFormulaText m_Formula;
  
  /** the columns to add the sum to. */
  protected SpreadSheetColumnRange m_Columns;

  /** whether to expand variables. */
  protected boolean m_ExpandVariables;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds a row with a user-supplied formula for the specified columns.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "formula", "formula",
      new SpreadSheetFormulaText("=sum(@1:@#)"));

    m_OptionManager.add(
      "columns", "columns",
      new SpreadSheetColumnRange(Range.ALL));

    m_OptionManager.add(
      "expand-variables", "expandVariables",
      false);
  }

  /**
   * Sets the formula to use.
   *
   * @param value	the formula (incl. "=")
   */
  public void setFormula(SpreadSheetFormulaText value) {
    m_Formula = value;
    reset();
  }

  /**
   * Returns the formula in use.
   *
   * @return		the formula (incl. "=")
   */
  public SpreadSheetFormulaText getFormula() {
    return m_Formula;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formulaTipText() {
    return "The formula to add (incl '='); use '@' as placeholder for the current column and '#' for the last row.";
  }

  /**
   * Sets the range of columns to add the sum for.
   *
   * @param value	the range
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the range of columns to add the sum for.
   *
   * @return		true range
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The range of columns to add the formula for; " + m_Columns.getExample();
  }

  /**
   * Sets whether to expand any variable first before adding the formula
   * to the spreadsheet.
   *
   * @param value	true if to expand first
   */
  public void setExpandVariables(boolean value) {
    m_ExpandVariables = value;
    reset();
  }

  /**
   * Returns whether to expand any variable first before adding the formula
   * to the spreadsheet.
   *
   * @return		true if to expand first
   */
  public boolean getExpandVariables() {
    return m_ExpandVariables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expandVariablesTipText() {
    return "If enabled, any variables in the formula get expanded first before adding it to the spreadsheet.";
  }

  /**
   * Generates the new spreadsheet from the input.
   * 
   * @param input	the incoming spreadsheet
   * @return		the generated spreadsheet
   * @throws Exception	if conversion fails for some reason
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet	result;
    Row		row;
    int		i;
    int[]	cols;
    String	formula;
    
    if (m_NoCopy)
      result = input;
    else
      result = input.getClone();
    
    m_Columns.setSpreadSheet(result);
    cols = m_Columns.getIntIndices();
    row  = result.addRow();
    
    for (i = 0; i < cols.length; i++) {
      formula = m_Formula.getValue();
      if (m_ExpandVariables)
        formula = getOptionManager().getVariables().expand(formula);
      formula = formula.replace(PLACEHOLDER_COLUMN, SpreadSheetUtils.getColumnPosition(cols[i]));
      formula = formula.replace(PLACEHOLDER_LAST_ROW, Integer.toString(result.getRowCount()));
      row.addCell(cols[i]).setFormula(formula);
    }
    
    return result;
  }
}
