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
 * SpreadSheetRowFinder.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetView;
import adams.data.spreadsheet.SpreadSheetViewCreator;
import adams.data.spreadsheet.rowfinder.AbstractRowFinder;
import adams.data.spreadsheet.rowfinder.RowFinder;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Filters spreadsheets using the specified row finder.<br>
 * The output contains all the rows that the specified finder selected.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetRowFilter
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
 * <pre>-finder &lt;adams.data.spreadsheet.rowfinder.RowFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The row finder to use for identifying rows for the output.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.rowfinder.NullFinder
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetRowFilter
  extends AbstractSpreadSheetTransformer
  implements SpreadSheetViewCreator {

  /** for serialization. */
  private static final long serialVersionUID = 3754073511732133649L;
  
  /** the filter to apply. */
  protected adams.data.spreadsheet.rowfinder.RowFinder m_Finder;

  /** whether to create a view only. */
  protected boolean m_CreateView;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Filters spreadsheets using the specified row finder.\n"
	+ "The output contains all the rows that the specified finder "
	+ "selected.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "finder", "finder",
      new adams.data.spreadsheet.rowfinder.NullFinder());

    m_OptionManager.add(
      "create-view", "createView",
      false);
  }

  /**
   * Sets the finder to use.
   *
   * @param value	the finder
   */
  public void setFinder(RowFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the finder in use.
   *
   * @return		the finder
   */
  public RowFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The row finder to use for identifying rows for the output.";
  }

  /**
   * Sets whether to create a view only.
   *
   * @param value	true if to create a view only
   */
  public void setCreateView(boolean value) {
    m_CreateView = value;
    reset();
  }

  /**
   * Returns whether to create only a view.
   *
   * @return		true if to create view only
   */
  public boolean getCreateView() {
    return m_CreateView;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String createViewTipText() {
    return "If enabled, then only a view of the row subset is created.";
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

    result = QuickInfoHelper.toString(this, "finder", m_Finder);
    value  = QuickInfoHelper.toString(this, "createView", m_CreateView, ", view only");
    if (value != null)
      result += value;

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
    SpreadSheet		output;

    result = null;
    input  = (SpreadSheet) m_InputToken.getPayload();
    if (m_CreateView)
      output = new SpreadSheetView(input, m_Finder.findRows(input), null);
    else
      output = AbstractRowFinder.filter(input, m_Finder);
    m_OutputToken = new Token(output);
    
    return result;
  }
}
