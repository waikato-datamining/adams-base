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
 * SpreadSheetSubsetByValue.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.core.Token;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Generates subsets from a spreadsheet, grouped by the same string value in the specified column.<br>
 * For instance, if a spreadsheet has 3 unique values (A, B, C) in column 2, then 3 subsheets will generated, each containing the rows that have the value A, B or C.
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
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetSubsetByValue
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
 * <pre>-col &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column which unique string values identify the subsets; An index is 
 * &nbsp;&nbsp;&nbsp;a number starting with 1; apart from column names (case-sensitive), the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used as well: first, second, third, last_2,
 * &nbsp;&nbsp;&nbsp; last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetSubsetByValue
  extends AbstractSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -253714973019682939L;

  /** the key for storing the sorted sheet in the backup. */
  public final static String BACKUP_SORTED = "sorted";

  /** the key for storing the row index in the backup. */
  public final static String BACKUP_ROWINDEX = "row index";

  /** the key for storing the col index in the backup. */
  public final static String BACKUP_COLINDEX = "col index";

  /** the string values of this column are used to split the sheet into subsets. */
  protected SpreadSheetColumnIndex m_Column;

  /** the sorted spreadsheet. */
  protected SpreadSheet m_Sorted;
  
  /** the current row index. */
  protected int m_RowIndex;
  
  /** the column index. */
  protected int m_ColIndex;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates subsets from a spreadsheet, grouped by the same string "
	+ "value in the specified column.\n"
	+ "For instance, if a spreadsheet has 3 unique values (A, B, C) in column 2, "
	+ "then 3 subsheets will generated, each containing the rows that have the "
	+ "value A, B or C.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "col", "column",
	    new SpreadSheetColumnIndex(Range.FIRST));
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
   * Resets the actor. 
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Sorted   = null;
    m_RowIndex = -1;
    m_ColIndex = -1;
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "column", m_Column, "col: ");
  }

  /**
   * Sets the column that identifies the subsets.
   *
   * @param value	the column
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column that identifies the subsets.
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
    return "The column which unique string values identify the subsets; " + m_Column.getExample();
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_SORTED);
    pruneBackup(BACKUP_ROWINDEX);
    pruneBackup(BACKUP_COLINDEX);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_Sorted != null)
      result.put(BACKUP_SORTED, m_Sorted);
    if (m_RowIndex != -1)
      result.put(BACKUP_ROWINDEX, m_RowIndex);
    if (m_ColIndex != -1)
      result.put(BACKUP_COLINDEX, m_ColIndex);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_SORTED)) {
      m_Sorted = (SpreadSheet) state.get(BACKUP_SORTED);
      state.remove(BACKUP_SORTED);
    }
    if (state.containsKey(BACKUP_ROWINDEX)) {
      m_RowIndex = (Integer) state.get(BACKUP_ROWINDEX);
      state.remove(BACKUP_ROWINDEX);
    }
    if (state.containsKey(BACKUP_COLINDEX)) {
      m_ColIndex = (Integer) state.get(BACKUP_COLINDEX);
      state.remove(BACKUP_COLINDEX);
    }

    super.restoreState(state);
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

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    m_Column.setSpreadSheet(sheet);

    m_ColIndex = m_Column.getIntIndex();
    if (m_ColIndex == -1) {
      result = "No column selected!";
    }
    else {
      m_Sorted = sheet.getClone();
      m_Sorted.sort(m_ColIndex, true);
      m_RowIndex = 0;
    }

    return result;
  }
  
  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Sorted != null) && (m_Sorted.getRowCount() > 0);
  }
  
  /**
   * Returns the cell value at the specified column.
   * 
   * @param row		the row to get the cell value from
   * @param col		the column of the cell
   * @return		the cell value, null if missing
   */
  protected String getCellValue(Row row, int col) {
    if (!row.hasCell(col) || row.getCell(col).isMissing())
      return null;
    else
      return row.getCell(col).getContent();
  }
  
  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    SpreadSheet	sheet;
    String	current;
    String	old;
    Row		row;
    boolean	finished;
    boolean	first;
    
    result   = null;
    current  = "";
    old      = "";
    sheet    = null;
    finished = false;
    first    = true;
    do {
      row = m_Sorted.getRow(m_RowIndex);
      old = current;
      current = getCellValue(row, m_ColIndex);
      if (old.equals(current) || first) {
	if (sheet == null) {
	  sheet = m_Sorted.getHeader();
	  first = false;
	}
	sheet.addRow().assign(row);
	m_RowIndex++;
      }
      else {
	finished = true;
      }
    }
    while (!finished && !isStopped() && (m_RowIndex < m_Sorted.getRowCount()));
    
    if (sheet != null)
      result = new Token(sheet);
    
    // all rows processed?
    if (m_RowIndex == m_Sorted.getRowCount())
      m_Sorted = null;
    
    return result;
  }
}
