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
 * SpreadSheetReorderColumns.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Reorders the columns in a spreadsheet according to a user-supplied order. Columns can be supplied either by name or index (1-based). Depending on whether you use a column multiple times or omit it, you effectively duplicate it or remove it from the final spreadsheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetReorderColumns
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
 * <pre>-order &lt;java.lang.String&gt; (property: order)
 * &nbsp;&nbsp;&nbsp;The new order for the columns; A range is a comma-separated list of single 
 * &nbsp;&nbsp;&nbsp;1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts 
 * &nbsp;&nbsp;&nbsp;the range '...'; the following placeholders can be used as well: first, 
 * &nbsp;&nbsp;&nbsp;second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetReorderColumns
  extends AbstractSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6140158624456525670L;
  
  /** the new order of the columns. */
  protected String m_Order;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Reorders the columns in a spreadsheet according to a user-supplied "
	+ "order. Columns can be supplied either by name or index (1-based). "
	+ "Depending on whether you use a column multiple times or omit it, "
	+ "you effectively duplicate it or remove it from the final spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "order", "order",
	    Range.ALL);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "order", m_Order);
  }

  /**
   * Sets the regular expression for the column names of columns to anonymize.
   *
   * @param value	the regular expression
   */
  public void setOrder(String value) {
    m_Order = value;
    reset();
  }

  /**
   * Returns the regular expression for the column names of columns to anonymize.
   *
   * @return		the regular expression
   */
  public String getOrder() {
    return m_Order;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String orderTipText() {
    return "The new order for the columns; " + new SpreadSheetColumnRange().getExample();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			input;
    SpreadSheet			output;
    Row				rowOld;
    Row				rowNew;
    int				i;
    int				n;
    List<Integer>		indices;
    String[]			ranges;
    SpreadSheetColumnRange	range;

    result = null;

    if (m_Order.trim().length() == 0)
      result = "No new column order provided!";
    
    if (result == null) {
      input  = (SpreadSheet) m_InputToken.getPayload();
      output = input.newInstance();
      output.setDataRowClass(input.getDataRowClass());
      for (String comment: input.getComments())
	output.addComment(comment);

      // determine indices
      indices = new ArrayList<Integer>();
      ranges  = SpreadSheetUtils.split(m_Order, ',');
      for (String r: ranges) {
	range = new SpreadSheetColumnRange(r);
	range.setSpreadSheet(input);
	indices.addAll(Utils.toList(range.getIntIndices()));
      }
      
      // header
      rowOld = input.getHeaderRow();
      rowNew = output.getHeaderRow();
      for (i = 0; i < indices.size(); i++)
	rowNew.addCell("" + i).setContent(rowOld.getContent(indices.get(i)));
      
      // data
      for (n = 0; n < input.getRowCount(); n++) {
	rowOld = input.getRow(n);
	rowNew = output.addRow();
	for (i = 0; i < indices.size(); i++) {
	  if (rowOld.hasCell(indices.get(i)))
	    rowNew.addCell("" + i).setContent(rowOld.getContent(indices.get(i)));
	}
      }

      m_OutputToken = new Token(output);
    }

    return result;
  }
}
