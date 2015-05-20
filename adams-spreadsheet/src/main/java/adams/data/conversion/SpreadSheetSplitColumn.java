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
 * SpreadSheetSplitColumn.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.transformer.StringSplit.Delimiter;

/**
 <!-- globalinfo-start -->
 * Splits the string representation of the cells of a column into multiple columns using a regular expression.
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
 * <pre>-column &lt;adams.core.Index&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column to split.
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-expression &lt;java.lang.String&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The regular expression used for splitting the column; \t\n\r\b\f get automatically 
 * &nbsp;&nbsp;&nbsp;converted into their character counterparts.
 * &nbsp;&nbsp;&nbsp;default: \\t
 * </pre>
 * 
 * <pre>-delimiter &lt;DISCARD|APPEND|PREPEND&gt; (property: delimiter)
 * &nbsp;&nbsp;&nbsp;Defines what to do with the delimiters (= expression).
 * &nbsp;&nbsp;&nbsp;default: DISCARD
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetSplitColumn
  extends AbstractSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = 6146479838753681459L;

  /** the column to process. */
  protected SpreadSheetColumnIndex m_Column;

  /** the regular expression to use for splitting the string. */
  protected BaseRegExp m_Expression;

  /** what to do with the delimiters. */
  protected Delimiter m_Delimiter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Splits the string representation of the cells of a column into "
	+ "multiple columns using a regular expression.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "column", "column",
	    new SpreadSheetColumnIndex(Index.FIRST));

    m_OptionManager.add(
	    "expression", "expression",
	    new BaseRegExp("\\t"));

    m_OptionManager.add(
	    "delimiter", "delimiter",
	    Delimiter.DISCARD);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "column", m_Column.getIndex(), "col: ");
    result += QuickInfoHelper.toString(this, "expression", m_Expression, "-none-", ", expr: ");
    result += "(" + QuickInfoHelper.toString(this, "delimiter", m_Delimiter) + ")";

    return result;
  }

  /**
   * Sets the column to split.
   *
   * @param value	the index
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column to split.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column to split.";
  }

  /**
   * Sets the regular expression used for splitting the string.
   *
   * @param value	the expression
   */
  public void setExpression(BaseRegExp value) {
    m_Expression = value;
    reset();
  }

  /**
   * Returns the regular expression for splitting the string.
   *
   * @return		the expression
   */
  public BaseRegExp getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return
        "The regular expression used for splitting the column; \\t\\n\\r\\b\\f get "
      + "automatically converted into their character counterparts.";
  }

  /**
   * Sets what to do with the delimiter (= expression).
   *
   * @param value	the action
   */
  public void setDelimiter(Delimiter value) {
    m_Delimiter = value;
    reset();
  }

  /**
   * Returns what to do with the delimiter (= expression).
   *
   * @return		the action
   */
  public Delimiter getDelimiter() {
    return m_Delimiter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String delimiterTipText() {
    return "Defines what to do with the delimiters (= expression).";
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet		result;
    int			i;
    int			n;
    int			r;
    int			cindex;
    String[][]		split;
    Row			rowIn;
    Row			rowOut;
    int			col;
    Cell		cell;
    String		str;
    int			num;
    
    m_Column.setSpreadSheet(input);
    col = m_Column.getIntIndex();
    
    // split data
    num   = 0;
    split = new String[input.getRowCount()][];
    for (r = 0; r < split.length; r++) {
      cell = input.getCell(r, col);
      if ((cell == null) || (cell.isMissing()))
	continue;
      str = cell.getContent();
      if (str == null)
	continue;
      split[r] = str.split(m_Expression.getValue());
      if (split[r].length > num)
	num = split[r].length;
    }
    
    // assemble header
    result = input.newInstance();
    result.setDataRowClass(input.getDataRowClass());
    rowIn  = input.getHeaderRow();
    rowOut = result.getHeaderRow();
    for (i = 0; i < input.getColumnCount(); i++) {
      if (i == col) {
	for (n = 0; n < num; n++)
	  rowOut.addCell("" + rowOut.getCellCount()).setContent(
	      rowIn.getCell(col).getContent() + "-" + (n+1));
      }
      else {
	rowOut.addCell("" + rowOut.getCellCount()).assign(rowIn.getCell(i));
      }
    }
    
    // add data
    for (r = 0; r < input.getRowCount(); r++) {
      rowIn  = input.getRow(r);
      rowOut = result.addRow();
      cindex = 0;
      for (i = 0; i < input.getColumnCount(); i++) {
	if (!rowIn.hasCell(i) || rowIn.getCell(i).isMissing())
	  continue;
	if (i == col) {
	  if ((split[r] == null) || (split[r].length == 0))
	    continue;
	  for (n = 0; n < split[r].length; n++)
	    rowOut.addCell(cindex + n).setContent(split[r][n]);
	  cindex += num;
	}
	else {
	  rowOut.addCell(cindex).assign(rowIn.getCell(i));
	  cindex++;
	}
      }
    }
    
    split = null;
  
    return result;
  }
}
