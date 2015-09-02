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
 * SpreadSheetCollapse.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.RowIdentifier;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;
import gnu.trove.set.hash.TIntHashSet;

import java.util.HashSet;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the specified key columns to identify groups of rows. It then collapses these rows into a single one, concatenating the content of the cells. Optionally, duplicate cell values can be omitted.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetCollapse
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-key-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: keyColumns)
 * &nbsp;&nbsp;&nbsp;The columns to use as keys for identifying rows in the spreadsheets; if 
 * &nbsp;&nbsp;&nbsp;left empty, all rows are used.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator to use when joining cell values.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-no-duplicates &lt;boolean&gt; (property: noDuplicates)
 * &nbsp;&nbsp;&nbsp;If enabled, duplicate cell values get omitted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8336 $
 */
public class SpreadSheetCollapse
  extends AbstractSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 444466366407383727L;

  /** the range of column indices to use as key for identifying a row. */
  protected SpreadSheetColumnRange m_KeyColumns;

  /** the separator for the collapsed cells. */
  protected String m_Separator;
  
  /** whether to omit duplicate cell values. */
  protected boolean m_NoDuplicates;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses the specified key columns to identify groups of rows. "
        + "It then collapses these rows into a single one, concatenating the "
        + "content of the cells. Optionally, duplicate cell values can be omitted.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key-columns", "keyColumns",
      new SpreadSheetColumnRange(SpreadSheetColumnRange.FIRST));

    m_OptionManager.add(
      "separator", "separator",
      "");

    m_OptionManager.add(
      "no-duplicates", "noDuplicates",
      false);
  }

  /**
   * Sets the colums that identify a row.
   *
   * @param value	the range
   */
  public void setKeyColumns(SpreadSheetColumnRange value) {
    m_KeyColumns = value;
    reset();
  }

  /**
   * Returns the colums that identify a rowx
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getKeyColumns() {
    return m_KeyColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyColumnsTipText() {
    return
        "The columns to use as keys for identifying rows in the spreadsheets; "
	+ "if left empty, all rows are used.";
  }

  /**
   * Sets the separator to use when joining cell values.
   *
   * @param value	the separator
   */
  public void setSeparator(String value) {
    m_Separator = value;
    reset();
  }

  /**
   * Returns the separator to use when joining cell values.
   *
   * @return		the separator
   */
  public String getSeparator() {
    return m_Separator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorTipText() {
    return "The separator to use when joining cell values.";
  }

  /**
   * Sets whether to omit duplicates.
   *
   * @param value	true if to omit duplicates
   */
  public void setNoDuplicates(boolean value) {
    m_NoDuplicates = value;
    reset();
  }

  /**
   * Returns whether to omit duplicates.
   *
   * @return		true if to omit duplicates
   */
  public boolean getNoDuplicates() {
    return m_NoDuplicates;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noDuplicatesTipText() {
    return "If enabled, duplicate cell values get omitted.";
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

    result  = QuickInfoHelper.toString(this, "keyColumns", m_KeyColumns, "key: ");
    result += QuickInfoHelper.toString(this, "separator", (m_Separator.isEmpty() ? "-none-" : m_Separator), ", sep: ");
    value   = QuickInfoHelper.toString(this, "noDuplicates", m_NoDuplicates, "no-dup", ", ");
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
    SpreadSheet 	output;
    TIntHashSet 	keys;
    RowIdentifier	rows;
    List<Integer>	subset;
    Row			rowOld;
    Row			rowNew;
    int			i;
    HashSet<String>	dups;
    String		content;
    StringBuilder	collapsed;

    result     = null;
    input      = (SpreadSheet) m_InputToken.getPayload();
    output = null;
    
    // columns to use as key
    m_KeyColumns.setSpreadSheet(input);
    keys = new TIntHashSet(m_KeyColumns.getIntIndices());
    if (keys.size() == 0)
      result = "No key columns defined!";
    rows = new RowIdentifier(m_KeyColumns);

    if (result == null) {
      // create output
      rows.identify(input);
      output = input.getHeader();

      // data
      if (input.getRowCount() > 0) {
	dups = new HashSet<>();
	for (String key : rows.getKeys()) {
	  if (isStopped())
	    return null;
	  rowNew = output.addRow();
	  subset = rows.getRows(key);
	  // keys
	  for (int index : keys.toArray()) {
	    rowNew.addCell(index).setContent(
	      input.getRow(subset.get(0)).getCell(index).getContent());
	  }
	  // collapse cells
	  for (i = 0; i < input.getColumnCount(); i++) {
	    if (keys.contains(i))
	      continue;
	    if (m_NoDuplicates)
	      dups.clear();
	    collapsed = null;
	    for (int index: subset) {
	      rowOld  = input.getRow(index);
	      content = rowOld.getContent(i);
	      if (m_NoDuplicates && dups.contains(content))
		continue;
	      if (m_NoDuplicates)
		dups.add(content);
	      if (collapsed == null)
		collapsed = new StringBuilder();
	      else
	        collapsed.append(m_Separator);
	      collapsed.append(content);
	    }
	    if (collapsed != null)
	      rowNew.addCell(i).setContentAsString(collapsed.toString());
	    else
	      rowNew.addCell(i).setMissing();
	  }
	}
      }
    }

    if (output != null)
      m_OutputToken = new Token(output);
    
    return result;
  }
}
