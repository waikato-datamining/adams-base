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
 * SpreadSheetRowStatistic.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.rowstatistic.AbstractRowStatistic;
import adams.data.spreadsheet.rowstatistic.Mean;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates statistics for a chosen row.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetRowStatistic
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-row &lt;adams.core.Index&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The row to generate the statistics for.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-statistic &lt;adams.data.spreadsheet.rowstatistic.AbstractRowStatistic&gt; (property: statistic)
 * &nbsp;&nbsp;&nbsp;The statistic to generate for the specified row.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.rowstatistic.Mean
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class SpreadSheetRowStatistic
  extends AbstractSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4527040722924866539L;
  
  /** the column to generate the statistic for. */
  protected Index m_Row;  

  /** the statistic to generate. */
  protected AbstractRowStatistic m_Statistic;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates statistics for a chosen row.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "row", "row",
	    new Index(Index.FIRST));

    m_OptionManager.add(
	    "statistic", "statistic",
	    new Mean());
  }

  /**
   * Sets the row to generate the statistic for.
   *
   * @param value	the row
   */
  public void setRow(Index value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the row to generate the statistics for.
   *
   * @return		the row
   */
  public Index getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The row to generate the statistics for.";
  }

  /**
   * Sets the statistic to generate.
   *
   * @param value	the statistic
   */
  public void setStatistic(AbstractRowStatistic value) {
    m_Statistic = value;
    reset();
  }

  /**
   * Returns the statistic to generate
   *
   * @return		the statistic
   */
  public AbstractRowStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticTipText() {
    return "The statistic to generate for the specified row.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "row", m_Row, "row: ");
    result += QuickInfoHelper.toString(this, "statistic", m_Statistic, ", stat: ");
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		input;
    int			row;
    SpreadSheet		output;

    result  = null;
    input   = (SpreadSheet) m_InputToken.getPayload();
    m_Row.setMax(input.getRowCount());
    row = m_Row.getIntIndex();
    if (row == -1) {
      result = "Failed to locate row: " + m_Row;
    }
    else {
      output        = m_Statistic.generate(input, row);
      m_OutputToken = new Token(output);
    }
    
    return result;
  }
}
