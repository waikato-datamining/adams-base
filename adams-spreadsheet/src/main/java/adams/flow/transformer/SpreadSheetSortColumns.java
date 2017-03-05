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
 * SpreadSheetSortColumns.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.DefaultCompare;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reorders a user-defined subset of columns by name using the specified comparator.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetSortColumns
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
 * <pre>-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The subset of columns to perform the sorting on.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-comparator &lt;java.util.Comparator&gt; (property: comparator)
 * &nbsp;&nbsp;&nbsp;The comparator to use; must implement java.util.Comparator and java.io.Serializable
 * &nbsp;&nbsp;&nbsp;default: adams.core.DefaultCompare
 * </pre>
 * 
 * <pre>-reverse &lt;boolean&gt; (property: reverse)
 * &nbsp;&nbsp;&nbsp;If enabled, the sorting order gets reversed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetSortColumns
  extends AbstractSpreadSheetTransformer {

  private static final long serialVersionUID = 6177865465885016861L;

  /**
   * For sorting columns.
   */
  public static class SortContainer {

    /** the name of the column. */
    protected String m_Name;

    /** the original index of the column. */
    protected int m_Index;

    /**
     * Initializes the container.
     *
     * @param name	the name of the column
     * @param index	the index of the column
     */
    public SortContainer(String name, int index) {
      m_Name  = name;
      m_Index = index;
    }

    /**
     * Returns the name of the column.
     *
     * @return		the name
     */
    public String getName() {
      return m_Name;
    }

    /**
     * Returns the index of the column.
     *
     * @return		the index
     */
    public int getIndex() {
      return m_Index;
    }
  }

  /**
   * Comparator wrapper for the containers, uses the provided comparator
   * to compare the names.
   */
  public static class ContainerComparator
    implements Comparator<SortContainer> {

    /** the actual comparator to use. */
    protected Comparator m_Comparator;

    /**
     * Initializes the comparator wrapper.
     *
     * @param comp	the comparator to use for comparing the names
     */
    public ContainerComparator(Comparator comp) {
      m_Comparator = comp;
    }

    /**
     * Compares the two containers.
     *
     * @param o1	the first container
     * @param o2	the second container
     * @return		the comparison
     */
    @Override
    public int compare(SortContainer o1, SortContainer o2) {
      return m_Comparator.compare(o1.getName(), o2.getName());
    }
  }

  /** the range of columns to sort. */
  protected SpreadSheetColumnRange m_Columns;

  /** the comparator to use. */
  protected Comparator m_Comparator;

  /** whether to reverse the sorting. */
  protected boolean m_Reverse;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reorders a user-defined subset of columns by name using the specified comparator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "columns", "columns",
      new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));

    m_OptionManager.add(
      "comparator", "comparator",
      new DefaultCompare());

    m_OptionManager.add(
      "reverse", "reverse",
      false);
  }

  /**
   * Sets the subset of columns to sort.
   *
   * @param value	the columns
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the subset of columns to sort.
   *
   * @return		the subset
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The subset of columns to perform the sorting on.";
  }

  /**
   * Sets the comparator to use.
   *
   * @param value	the comparator
   */
  public void setComparator(Comparator value) {
    m_Comparator = value;
    reset();
  }

  /**
   * Returns the comparator to use.
   *
   * @return		the comparator
   */
  public Comparator getComparator() {
    return m_Comparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String comparatorTipText() {
    return "The comparator to use; must implement " + Comparator.class.getName() + " and " + Serializable.class.getName();
  }

  /**
   * Sets whether to reverse the sorting.
   *
   * @param value	true if to reverse
   */
  public void setReverse(boolean value) {
    m_Reverse = value;
    reset();
  }

  /**
   * Returns whether to reverse the sorting.
   *
   * @return		true if to reverse
   */
  public boolean getReverse() {
    return m_Reverse;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reverseTipText() {
    return "If enabled, the sorting order gets reversed.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "columns", m_Columns, "cols: ");
    result += QuickInfoHelper.toString(this, "comparator", m_Comparator.getClass(), ", comp: ");
    result += QuickInfoHelper.toString(this, "reverse", m_Reverse, "reversed", ", ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    List<SortContainer>		conts;
    SpreadSheet			sheet;
    TIntHashSet			subset;
    TIntArrayList		newOrder;
    int				i;
    boolean			first;
    StringBuilder		order;
    SpreadSheetReorderColumns	reorder;

    result = null;
    sheet  = (SpreadSheet) m_InputToken.getPayload();
    conts  = new ArrayList<>();
    subset = new TIntHashSet();
    m_Columns.setData(sheet);
    for (int index: m_Columns.getIntIndices()) {
      conts.add(new SortContainer(sheet.getColumnName(index), index));
      subset.add(index);
    }
    if (conts.size() == 0)
      result = "No columns selected?";

    if (result == null) {
      conts.sort(new ContainerComparator(m_Comparator));
      if (m_Reverse)
	Collections.reverse(conts);
      newOrder = new TIntArrayList();
      first    = true;
      // old: ("not-in-subset")? ("subset") ("not-in-subset")?
      // new: ("not-in-subset")? ("sorted subset") ("not-in-subset")?
      for (i = 0; i < sheet.getColumnCount(); i++) {
	if (first) {
	  if (subset.contains(i)) {
	    first = false;
	    for (SortContainer cont: conts)
	      newOrder.add(cont.getIndex());
	  }
	  else {
	    newOrder.add(i);
	  }
	}
	else {
	  if (!subset.contains(i))
	    newOrder.add(i);
	}
      }
      // generate reorder string
      order = new StringBuilder();
      for (i = 0; i < newOrder.size(); i++) {
	if (i > 0)
	  order.append(",");
	order.append("" + (newOrder.get(i) + 1));
      }
      // reorder
      reorder = new SpreadSheetReorderColumns();
      reorder.setOrder(order.toString());
      reorder.input(new Token(sheet));
      result = reorder.execute();
      if (result == null)
	m_OutputToken = reorder.output();
      reorder.cleanUp();
    }

    return result;
  }
}
