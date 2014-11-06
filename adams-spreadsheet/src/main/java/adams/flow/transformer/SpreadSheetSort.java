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
 * SpreadSheetSort.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseBoolean;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Sorts the rows of the spreadsheet according to the selected column indices and sort order (ascending&#47;descending).
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetSort
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-sort-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; [-sort-column ...] (property: sortColumn)
 * &nbsp;&nbsp;&nbsp;The columns to use for sorting.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-sort-order &lt;adams.core.base.BaseBoolean&gt; [-sort-order ...] (property: sortOrder)
 * &nbsp;&nbsp;&nbsp;The order of sorting for the columns (true = ascending, false = descending
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-unique &lt;boolean&gt; (property: unique)
 * &nbsp;&nbsp;&nbsp;If enabled, all duplicate rows (according to the sort setup) get dropped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetSort
  extends AbstractInPlaceSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8621434041912535939L;

  /** the indices of the columns to use for sorting. */
  protected SpreadSheetColumnIndex[] m_SortColumn;

  /** whether to use ascending or descending sort order. */
  protected BaseBoolean[] m_SortOrder;
  
  /** whether to perform a unique sort. */
  protected boolean m_Unique;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Sorts the rows of the spreadsheet according to the selected column "
	+ "indices and sort order (ascending/descending).";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "sort-column", "sortColumn",
	    new SpreadSheetColumnIndex[]{new SpreadSheetColumnIndex("first")});

    m_OptionManager.add(
	    "sort-order", "sortOrder",
	    new BaseBoolean[]{new BaseBoolean(true)});

    m_OptionManager.add(
	    "unique", "unique",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_SortColumn = new SpreadSheetColumnIndex[0];
    m_SortOrder  = new BaseBoolean[0];
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    int		i;
    
    if (QuickInfoHelper.hasVariable(this, "sortColumn") && QuickInfoHelper.hasVariable(this, "sortOrder"))
      result = QuickInfoHelper.getVariable(this, "sortColumn") + " and " + QuickInfoHelper.getVariable(this, "sortOrder");
    else {
      result = "";
      for (i = 0; i < m_SortColumn.length; i++) {
	if (i > 0)
	  result += " and ";
	result += m_SortColumn[i].getIndex();
	if (!m_SortOrder[i].booleanValue())
	  result += " (desc)";
      }
    }
    
    result += QuickInfoHelper.toString(this, "unique", m_Unique, "unique", ", ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy", ", ");

    return result;
  }

  /**
   * Sets the indices of the columns to use for sorting.
   *
   * @param value	the columns
   */
  public void setSortColumn(SpreadSheetColumnIndex[] value) {
    m_SortColumn = value;
    if (m_SortColumn.length != m_SortOrder.length)
      m_SortOrder = (BaseBoolean[]) Utils.adjustArray(m_SortOrder, m_SortColumn.length, new BaseBoolean(true));
    reset();
  }

  /**
   * Returns the indices of the columns to use for sorting.
   *
   * @return		the columns
   */
  public SpreadSheetColumnIndex[] getSortColumn() {
    return m_SortColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortColumnTipText() {
    return "The columns to use for sorting.";
  }

  /**
   * Sets the sorting order for the columns (ascending/descending).
   *
   * @param value	the order (true=asc)
   */
  public void setSortOrder(BaseBoolean[] value) {
    m_SortOrder = value;
    if (m_SortColumn.length != m_SortOrder.length)
      m_SortColumn = (SpreadSheetColumnIndex[]) Utils.adjustArray(m_SortColumn, m_SortOrder.length, new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));
    reset();
  }

  /**
   * Returns the sorting order for the columns (ascending/descending).
   *
   * @return		the order (true=asc)
   */
  public BaseBoolean[] getSortOrder() {
    return m_SortOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortOrderTipText() {
    return "The order of sorting for the columns (true = ascending, false = descending).";
  }

  /**
   * Sets whether to drop duplicate rows during sort.
   *
   * @param value	true if to drop duplicate rows
   */
  public void setUnique(boolean value) {
    m_Unique = value;
    reset();
  }

  /**
   * Returns whether to drop duplicate rows during sort.
   *
   * @return		true if duplicate rows dropped
   */
  public boolean getUnique() {
    return m_Unique;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String uniqueTipText() {
    return "If enabled, all duplicate rows (according to the sort setup) get dropped.";
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
    RowComparator	comp;
    int[]		indices;
    boolean[]		order;
    int			i;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    if (!m_NoCopy)
      sheet = sheet.getClone();
    
    // columns
    indices = new int[m_SortColumn.length];
    for (i = 0; i < m_SortColumn.length; i++) {
      m_SortColumn[i].setSpreadSheet(sheet);
      indices[i] = m_SortColumn[i].getIntIndex();
    }
    
    // order
    order = new boolean[m_SortOrder.length];
    for (i = 0; i < m_SortOrder.length; i++)
      order[i] = m_SortOrder[i].booleanValue();

    // set up comparator
    comp = new RowComparator(indices, order);

    sheet.sort(comp, m_Unique);

    m_OutputToken = new Token(sheet);

    return result;
  }
}
