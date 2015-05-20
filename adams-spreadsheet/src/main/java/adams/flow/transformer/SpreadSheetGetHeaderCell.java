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
 * SpreadSheetGetHeaderCell.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Extracts a single value from a spreadsheet's header.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetGetHeaderCell
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-col &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column of the header cell to retrieve; An index is a number starting 
 * &nbsp;&nbsp;&nbsp;with 1; apart from column names (case-sensitive), the following placeholders 
 * &nbsp;&nbsp;&nbsp;can be used as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-empty &lt;java.lang.String&gt; (property: empty)
 * &nbsp;&nbsp;&nbsp;The value to return in case the header cell is empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetGetHeaderCell
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 6877755289168511940L;

  /** the column of the cell to obtain. */
  protected SpreadSheetColumnIndex m_Column;

  /** the value to return if cell is empty. */
  protected String m_Empty;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Extracts a single value from a spreadsheet's header.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "col", "column",
	    new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
	    "empty", "empty",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Column = new SpreadSheetColumnIndex();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "col", m_Column, "col: ");
    value = QuickInfoHelper.toString(this, "empty", (m_Empty.length() > 0 ? m_Empty : null), ", empty: ");
    if (value != null)
      result += null;
    
    return result;
  }

  /**
   * Sets the column of the cell.
   *
   * @param value	the column
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column of the cell.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column of the header cell to retrieve; " + m_Column.getExample();
  }

  /**
   * Sets the value to return if cell is empty.
   *
   * @param value	the empty value
   */
  public void setEmpty(String value) {
    m_Empty = value;
    reset();
  }

  /**
   * Returns the value to return if cell is empty.
   *
   * @return		the empty value
   */
  public String getEmpty() {
    return m_Empty;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String emptyTipText() {
    return "The value to return in case the header cell is empty.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.spreadsheet.SpreadSheet.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    SpreadSheet	sheet;
    String	value;
    Cell	cell;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    m_Column.setSpreadSheet(sheet);

    if (m_Column.getIntIndex() == -1) {
      result = "Illegal column index ('" + m_Column.getIndex() + "')?";
    }
    else {
      cell = sheet.getHeaderRow().getCell(m_Column.getIntIndex());
      if (cell == null)
	value = m_Empty;
      else if (cell.isMissing())
	value = SpreadSheet.MISSING_VALUE;
      else
	value = cell.getContent();
      m_OutputToken = new Token(value);
    }

    return result;
  }
}
