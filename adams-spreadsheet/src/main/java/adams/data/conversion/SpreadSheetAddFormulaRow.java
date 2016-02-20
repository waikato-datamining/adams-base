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
 * SpreadSheetAddFormulaRow.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-no-copy (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * </pre>
 * 
 * <pre>-formula &lt;java.lang.String&gt; (property: formula)
 * &nbsp;&nbsp;&nbsp;The formula to add (incl '='); use '&#64;' as placeholder for the current column.
 * &nbsp;&nbsp;&nbsp;default: =sum(&#64;1:&#64;10)
 * </pre>
 * 
 * <pre>-columns &lt;adams.core.Range&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The range of columns to add the formula for; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; the following placeholders can be used 
 * &nbsp;&nbsp;&nbsp;as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
      formula = formula.replace(PLACEHOLDER_COLUMN, SpreadSheetUtils.getColumnPosition(cols[i]));
      formula = formula.replace(PLACEHOLDER_LAST_ROW, Integer.toString(result.getRowCount()));
      row.addCell(cols[i]).setFormula(formula);
    }
    
    return result;
  }
}
