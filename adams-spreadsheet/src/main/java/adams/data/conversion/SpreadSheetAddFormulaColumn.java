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
 * SpreadSheetAddFormulaColumn.java
 * Copyright (C) 2013-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Range;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.parser.SpreadSheetFormulaText;

/**
 <!-- globalinfo-start -->
 * Adds a column with a user-supploed formula for the specified rows.
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
 * <pre>-header &lt;java.lang.String&gt; (property: header)
 * &nbsp;&nbsp;&nbsp;The title of the formula column.
 * &nbsp;&nbsp;&nbsp;default: Sum
 * </pre>
 *
 * <pre>-formula &lt;adams.parser.SpreadSheetFormulaText&gt; (property: formula)
 * &nbsp;&nbsp;&nbsp;The formula to add (incl '='); use '&#64;' as placeholder for the current row
 * &nbsp;&nbsp;&nbsp;and '#' for the last column.
 * &nbsp;&nbsp;&nbsp;default: =sum(A&#64;:#&#64;)
 * </pre>
 *
 * <pre>-rows &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The range of rows to add the formula for.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
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
public class SpreadSheetAddFormulaColumn
  extends AbstractInPlaceSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = 3333030701857606514L;

  /** the placeholder for the row. */
  public final static String PLACEHOLDER_ROW = "@";

  /** the placeholder for the last column. */
  public final static String PLACEHOLDER_LAST_COL = "#";

  /** the column header. */
  protected String m_Header;

  /** the formula to add. */
  protected SpreadSheetFormulaText m_Formula;
  
  /** the rows to add the formula to. */
  protected Range m_Rows;

  /** whether to expand variables. */
  protected boolean m_ExpandVariables;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds a column with a user-supploed formula for the specified rows.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "header", "header",
      "Sum");

    m_OptionManager.add(
      "formula", "formula",
      new SpreadSheetFormulaText("=sum(A@:#@)"));

    m_OptionManager.add(
      "rows", "rows",
      new Range(Range.ALL));

    m_OptionManager.add(
      "expand-variables", "expandVariables",
      false);
  }

  /**
   * Sets the column header to use.
   *
   * @param value	the header
   */
  public void setHeader(String value) {
    m_Header = value;
    reset();
  }

  /**
   * Returns the column header in use.
   *
   * @return		the header
   */
  public String getHeader() {
    return m_Header;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headerTipText() {
    return "The title of the formula column.";
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
    return "The formula to add (incl '='); use '@' as placeholder for the current row and '#' for the last column.";
  }

  /**
   * Sets the range of rows to add the sum for.
   *
   * @param value	the range
   */
  public void setRows(Range value) {
    m_Rows = value;
    reset();
  }

  /**
   * Returns the range of rows to add the sum for.
   *
   * @return		true range
   */
  public Range getRows() {
    return m_Rows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsTipText() {
    return "The range of rows to add the formula for.";
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
    int[]	rows;
    String	formula;
    
    if (m_NoCopy)
      result = input;
    else
      result = input.getClone();
    
    m_Rows.setMax(result.getRowCount());
    rows = m_Rows.getIntIndices();
    result.insertColumn(result.getColumnCount(), m_Header);
    
    for (i = 0; i < rows.length; i++) {
      formula = m_Formula.getValue();
      if (m_ExpandVariables)
        formula = getOptionManager().getVariables().expand(formula);
      formula = formula.replace(PLACEHOLDER_ROW, Integer.toString(rows[i] + 2));
      formula = formula.replace(PLACEHOLDER_LAST_COL, SpreadSheetUtils.getColumnPosition(result.getColumnCount() - 2));
      row     = result.getRow(rows[i]);
      row.addCell(result.getColumnCount() - 1).setFormula(formula);
    }
    
    return result;
  }
}
