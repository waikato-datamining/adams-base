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
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUnorderedColumnRange;
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
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetReorderColumns
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-order &lt;adams.data.spreadsheet.SpreadSheetUnorderedColumnRange&gt; (property: order)
 * &nbsp;&nbsp;&nbsp;The new order for the columns
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.SpreadSheetUnorderedColumnRange
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetReorderColumns
  extends AbstractSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6140158624456525670L;
  
  /** the new order of the columns. */
  protected SpreadSheetUnorderedColumnRange m_Order;
  
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
	    new SpreadSheetUnorderedColumnRange(SpreadSheetUnorderedColumnRange.ALL));
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
  public void setOrder(SpreadSheetUnorderedColumnRange value) {
    m_Order = value;
    reset();
  }

  /**
   * Returns the regular expression for the column names of columns to anonymize.
   *
   * @return		the regular expression
   */
  public SpreadSheetUnorderedColumnRange getOrder() {
    return m_Order;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String orderTipText() {
    return "The new order for the columns";
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
    int[]			indices;

    result = null;

    if (m_Order.isEmpty())
      result = "No new column order provided!";
    
    if (result == null) {
      input  = (SpreadSheet) m_InputToken.getPayload();
      output = input.newInstance();
      output.setDataRowClass(input.getDataRowClass());
      for (String comment: input.getComments())
	output.addComment(comment);

      // determine indices
      m_Order.setData(input);
      indices = m_Order.getIntIndices();

      // header
      rowOld = input.getHeaderRow();
      rowNew = output.getHeaderRow();
      for (i = 0; i < indices.length; i++)
	rowNew.addCell("" + i).setContentAsString(rowOld.getContent(indices[i]));

      // data
      for (n = 0; n < input.getRowCount(); n++) {
	rowOld = input.getRow(n);
	rowNew = output.addRow();
	for (i = 0; i < indices.length; i++) {
	  if (rowOld.hasCell(indices[i]))
	    rowNew.addCell("" + i).assign(rowOld.getCell(indices[i]));
	}
      }

      m_OutputToken = new Token(output);
    }

    return result;
  }
}
