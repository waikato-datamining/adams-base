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
 * HasRows.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Checks whether the spreadsheet passing through has the required number of rows.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-num-rows &lt;int&gt; (property: numRows)
 * &nbsp;&nbsp;&nbsp;The minimum number of rows that the spreadsheet needs to have, no lower
 * &nbsp;&nbsp;&nbsp;bound if -1.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-max-rows &lt;int&gt; (property: maxRows)
 * &nbsp;&nbsp;&nbsp;The maximum number of rows that the spreadsheet can have, no upper bound
 * &nbsp;&nbsp;&nbsp;if -1.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HasRows
    extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 2973832676958171541L;

  /** the minimum number of rows. */
  protected int m_NumRows;

  /** the maximum number of rows. */
  protected int m_MaxRows;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the spreadsheet passing through has the required number of rows.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"num-rows", "numRows",
	1, -1, null);

    m_OptionManager.add(
	"max-rows", "maxRows",
	-1, -1, null);
  }

  /**
   * Sets the minimum number of rows the spreadsheet has to have.
   *
   * @param value	the number of rows (-1: no lower bound)
   */
  public void setNumRows(int value) {
    if (getOptionManager().isValid("numRows", value)) {
      m_NumRows = value;
      reset();
    }
  }

  /**
   * Returns the minimum number of rows the spreadsheet has to have
   *
   * @return		the number of rows (-1: no lower bound)
   */
  public int getNumRows() {
    return m_NumRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numRowsTipText() {
    return "The minimum number of rows that the spreadsheet needs to have, no lower bound if -1.";
  }

  /**
   * Sets the maximum number of rows the spreadsheet can have.
   *
   * @param value	the number of rows (-1: no upper bound)
   */
  public void setMaxRows(int value) {
    if (getOptionManager().isValid("maxRows", value)) {
      m_MaxRows = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of rows the spreadsheet can have.
   *
   * @return		the number of rows (-1: no upper bound)
   */
  public int getMaxRows() {
    return m_MaxRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxRowsTipText() {
    return "The maximum number of rows that the spreadsheet can have, no upper bound if -1.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "numRows", (m_NumRows == -1 ? "-any-" : "" + m_NumRows), "min rows: ");
    result += QuickInfoHelper.toString(this, "maxRows", (m_MaxRows == -1 ? "-any-" : "" + m_MaxRows), ", max rows: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean	result;
    SpreadSheet	sheet;

    result = (token.getPayload() instanceof SpreadSheet);

    if (result) {
      sheet  = (SpreadSheet) token.getPayload();

      if (m_NumRows > -1)
	result = (sheet.getRowCount() >= m_NumRows);

      if (m_MaxRows > -1)
	result = (sheet.getRowCount() <= m_MaxRows);
    }

    return result;
  }
}
