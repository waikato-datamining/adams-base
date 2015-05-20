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
 * SpreadSheetColumnsByName.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Creates a new spreadsheet with the columns that matched the regular expression. It is possible to invert the matching sense as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.core.io.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.core.io.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetColumnsByName
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
 * <pre>-reg-exp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to match the column names against.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-invert-matching (property: invertMatching)
 * &nbsp;&nbsp;&nbsp;If enabled, the matching sense is inverted, ie, only non-matching columns
 * &nbsp;&nbsp;&nbsp;get output.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetColumnsByName
  extends AbstractSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -253714973019682939L;

  /** the regular expression to match the column names against. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching. */
  protected boolean m_InvertMatching;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Creates a new spreadsheet with the columns that matched the regular "
     + "expression. It is possible to invert the matching sense as well.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reg-exp", "regExp",
	    new BaseRegExp());

    m_OptionManager.add(
	    "invert-matching", "invertMatching",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = "";
    if (m_InvertMatching && !QuickInfoHelper.hasVariable(this, "invertMatching"))
      result += "not ";
    result += "matching: ";

    result += QuickInfoHelper.toString(this, "regExp", m_RegExp);

    return result;
  }

  /**
   * Sets the regular expression to use for matching column names.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to use for matching column names.
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
    return "The regular expression to match the column names against.";
  }

  /**
   * Sets the whether to invert the matching.
   *
   * @param value	if true the matching is inverted
   */
  public void setInvertMatching(boolean value) {
    m_InvertMatching = value;
    reset();
  }

  /**
   * Returns whether to invert the matching.
   *
   * @return		true if the matching is inverted
   */
  public boolean getInvertMatching() {
    return m_InvertMatching;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertMatchingTipText() {
    return "If enabled, the matching sense is inverted, ie, only non-matching columns get output.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheet;
    SpreadSheet		subset;
    Row			row;
    Row			subrow;
    List<Integer>	indices;
    int			i;
    int			n;
    boolean		add;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();

    // determine columns for output
    indices = new ArrayList<Integer>();
    for (i = 0; i < sheet.getColumnCount(); i++) {
      if (m_InvertMatching)
	add = !m_RegExp.isMatch(sheet.getHeaderRow().getCell(i).getContent());
      else
	add = m_RegExp.isMatch(sheet.getHeaderRow().getCell(i).getContent());
      if (add)
	indices.add(i);
    }

    if (indices.size() > 0) {
      if (isLoggingEnabled())
	getLogger().info("Output columns: " + indices);

      subset = sheet.newInstance();
      // header
      for (i = 0; i < indices.size(); i++) {
	subset.getHeaderRow().addCell("" + (i+1)).setContent(
	    sheet.getHeaderRow().getCell(indices.get(i)).getContent());
      }
      // data
      for (n = 0; n < sheet.getRowCount(); n++) {
	row    = sheet.getRow(n);
	subrow = subset.addRow("" + (subset.getRowCount()));
	for (i = 0; i < indices.size(); i++)
	  subrow.addCell("" + (i+1)).assign(row.getCell(indices.get(i)));
      }

      m_OutputToken = new Token(subset);
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("No columns for output!");
    }

    return result;
  }
}
