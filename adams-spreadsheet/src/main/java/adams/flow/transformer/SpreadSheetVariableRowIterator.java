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
 * SpreadSheetVariableRowIterator.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Hashtable;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.Utils;
import adams.core.Variables;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Iterates through a defined range of rows. In each iteration the cell values of the defined column range are mapped to variables.<br>
 * By default the (cleaned up) header names of the columns are used as variable names. To avoid name clashes, a prefix can be chosen for the variable names.<br>
 * The subset of columns of the row in the current iteration can be output as spreadsheet well (computationally expensive). By default, the complete spreadsheet is forwarded as is.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetVariableRowIterator
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
 * <pre>-rows &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The rows to retrieve the values from; A range is a comma-separated list 
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; column names (case-sensitive) as well as the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used: first, second, third, last_2, last_1,
 * &nbsp;&nbsp;&nbsp; last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns to retrieve the values from; A range is a comma-separated list 
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; column names (case-sensitive) as well as the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used: first, second, third, last_2, last_1,
 * &nbsp;&nbsp;&nbsp; last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-variable-prefix &lt;java.lang.String&gt; (property: variablePrefix)
 * &nbsp;&nbsp;&nbsp;The prefix to prepend the header names with to make up the variable name.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-missing-value &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The value to use as variable value in case of missing cells.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-output-modified &lt;boolean&gt; (property: outputModified)
 * &nbsp;&nbsp;&nbsp;If enabled, the modified spreadsheet (current row with subset of columns
 * &nbsp;&nbsp;&nbsp;) is output instead of the full dataset (computationally expensive).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-lenient &lt;boolean&gt; (property: lenient)
 * &nbsp;&nbsp;&nbsp;If enabled, then no error message is generated if no rows are found.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetVariableRowIterator
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1117931423508873847L;

  /** the key for storing the queue of rows to process. */
  public final static String BACKUP_QUEUE = "queue";

  /** the underlying spreadsheet. */
  public final static String BACKUP_SHEET = "sheet";

  /** the column indices. */
  public final static String BACKUP_COLUMNS = "columns";

  /** the range of columns to use. */
  protected SpreadSheetColumnRange m_Columns;
  
  /** the range of rows to use. */
  protected Range m_Rows;
  
  /** the prefix for the variables. */
  protected String m_VariablePrefix;
  
  /** the value to use for missing cells. */
  protected String m_MissingValue;
  
  /** the rows to iterate. */
  protected ArrayList<Integer> m_Queue;
  
  /** the underlying sheet. */
  protected SpreadSheet m_Sheet;
  
  /** the column indices. */
  protected int[] m_ColumnIndices;
  
  /** whether to output the modified spreadsheet. */
  protected boolean m_OutputModified;

  /** whether to suppress the error message if no rows selected. */
  protected boolean m_Lenient;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Iterates through a defined range of rows. In each iteration the "
	+ "cell values of the defined column range are mapped to variables.\n"
	+ "By default the (cleaned up) header names of the columns are used as "
	+ "variable names. To avoid name clashes, a prefix can be chosen for "
	+ "the variable names.\n"
	+ "The subset of columns of the row in the current iteration can be "
	+ "output as spreadsheet well (computationally expensive). By default, "
	+ "the complete spreadsheet is forwarded as is.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "rows", "rows",
	    new Range(Range.ALL));

    m_OptionManager.add(
	    "columns", "columns",
	    new SpreadSheetColumnRange(Range.ALL));

    m_OptionManager.add(
	    "variable-prefix", "variablePrefix",
	    "");

    m_OptionManager.add(
	    "missing-value", "missingValue",
	    "");

    m_OptionManager.add(
	    "output-modified", "outputModified",
	    false);

    m_OptionManager.add(
	    "lenient", "lenient",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Queue         = new ArrayList<Integer>();
    m_Sheet         = null;
    m_ColumnIndices = null;
  }
  
  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Queue.clear();
    m_Sheet         = null;
    m_ColumnIndices = null;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    
    pruneBackup(BACKUP_QUEUE);
    pruneBackup(BACKUP_SHEET);
    pruneBackup(BACKUP_COLUMNS);
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

    if (m_Queue != null)
      result.put(BACKUP_QUEUE, m_Queue);
    if (m_Sheet != null)
      result.put(BACKUP_SHEET, m_Sheet);
    if (m_ColumnIndices != null)
      result.put(BACKUP_COLUMNS, m_ColumnIndices);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_QUEUE)) {
      m_Queue = (ArrayList<Integer>) state.get(BACKUP_QUEUE);
      state.remove(BACKUP_QUEUE);
    }

    if (state.containsKey(BACKUP_SHEET)) {
      m_Sheet = (SpreadSheet) state.get(BACKUP_SHEET);
      state.remove(BACKUP_SHEET);
    }

    if (state.containsKey(BACKUP_COLUMNS)) {
      m_ColumnIndices = (int[]) state.get(BACKUP_COLUMNS);
      state.remove(BACKUP_COLUMNS);
    }

    super.restoreState(state);
  }

  /**
   * Sets the columns to retrieve the values from.
   *
   * @param value	the columns
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the columns to retrieve the values from.
   *
   * @return		the columns
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
    return "The columns to retrieve the values from; " + m_Columns.getExample();
  }

  /**
   * Sets the rows to retrieve the values from.
   *
   * @param value	the rows
   */
  public void setRows(Range value) {
    m_Rows = value;
    reset();
  }

  /**
   * Returns the rows to retrieve the values from.
   *
   * @return		the rows
   */
  public Range getRows() {
    return m_Rows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsTipText() {
    return "The rows to retrieve the values from; " + m_Columns.getExample();
  }

  /**
   * Sets the prefix for the variables (prefix + header).
   *
   * @param value	the prefix
   */
  public void setVariablePrefix(String value) {
    m_VariablePrefix = value;
    reset();
  }

  /**
   * Returns the prefix for the variables (prefix + header).
   *
   * @return		the prefix
   */
  public String getVariablePrefix() {
    return m_VariablePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variablePrefixTipText() {
    return "The prefix to prepend the header names with to make up the variable name.";
  }

  /**
   * Sets the value to use for missing cells.
   *
   * @param value	the value for missing cells
   */
  public void setMissingValue(String value) {
    m_MissingValue = value;
    reset();
  }

  /**
   * Returns the value to use for missing cells.
   *
   * @return		the value for missing cells
   */
  public String getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValueTipText() {
    return "The value to use as variable value in case of missing cells.";
  }

  /**
   * Sets whether to output the modified spreadsheet (current row, subset 
   * of columns) instead of the full one.
   *
   * @param value	true if to output modified spreadsheet
   */
  public void setOutputModified(boolean value) {
    m_OutputModified = value;
    reset();
  }

  /**
   * Returns whether to output the modified spreadsheet (current row, subset 
   * of columns) instead of the full one.
   *
   * @return		true if to output modified spreadsheet
   */
  public boolean getOutputModified() {
    return m_OutputModified;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputModifiedTipText() {
    return 
	"If enabled, the modified spreadsheet (current row with subset of "
	+ "columns) is output instead of the full dataset (computationally "
	+ "expensive).";
  }

  /**
   * Sets whether to suppress error message if no rows found.
   *
   * @param value	true if to suppress
   */
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether to suppress error message if no rows found.
   *
   * @return		true if to suppress
   */
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lenientTipText() {
    return "If enabled, then no error message is generated if no rows are found.";
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

    result  = QuickInfoHelper.toString(this, "rows", m_Rows, "rows: ");
    result += QuickInfoHelper.toString(this, "columns", m_Columns, ", cols: ");
    value = QuickInfoHelper.toString(this, "variablePrefix", (m_VariablePrefix.length() > 0 ? m_VariablePrefix : null), ", prefix: ");
    if (value != null)
      result += value;
    value = QuickInfoHelper.toString(this, "missingValue", (m_MissingValue.length() > 0 ? m_MissingValue : null), ", missing: ");
    if (value != null)
      result += value;
    value = QuickInfoHelper.toString(this, "outputModified", m_OutputModified, ", output modified");
    if (value != null)
      result += value;
    value = QuickInfoHelper.toString(this, "lenient", m_Lenient, ", lenient");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    
    result = null;
    
    m_Queue.clear();
    m_Sheet = (SpreadSheet) m_InputToken.getPayload();
    
    m_Columns.setSpreadSheet(m_Sheet);
    if (m_Columns.getIntIndices().length == 0)
      result = "No columns available with range '" + m_Columns.getRange() + "'?";
    
    if (result == null) {
      m_Rows.setMax(m_Sheet.getRowCount());
      if ((m_Rows.getIntIndices().length == 0) && !m_Lenient)
	result = "No rows available with range '" + m_Rows.getRange() + "'?";
    }
    
    if (result == null) {
      m_ColumnIndices = m_Columns.getIntIndices();
      m_Queue.addAll(Utils.toList(m_Rows.getIntIndices()));
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
    return (m_Queue != null) && !m_Queue.isEmpty();
  }
  
  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token			result;
    SpreadSheet			mod;
    int				rowIndex;
    int				i;
    int				col;
    Row				row;
    Row				header;
    String			name;
    SpreadSheetRemoveColumn	remove;
    SpreadSheetColumnRange	range;
    String			msg;
    
    result   = null;
    rowIndex = m_Queue.get(0);
    m_Queue.remove(0);

    if (m_OutputModified) {
      mod = m_Sheet.getHeader();
      mod.addRow().assign(m_Sheet.getRow(rowIndex));
      if (!m_Columns.isAllRange()) {
	range  = new SpreadSheetColumnRange(m_Columns.getRange());
	range.setInverted(!range.isInverted());
	remove = new SpreadSheetRemoveColumn();
	remove.setPosition(range);
	remove.input(new Token(m_Sheet));
	msg = remove.execute();
	if (msg == null) {
	  if (remove.hasPendingOutput()) {
	    mod = (SpreadSheet) remove.output().getPayload();
	  }
	  else {
	    getLogger().severe("Failed to generate modified spreadsheet (no output)!");
	    mod = null;
	  }
	}
	else {
	  getLogger().severe("Failed to generate modified spreadsheet: " + msg);
	  mod = null;
	}
      }
      if (mod != null)
	result = new Token(mod);
    }
    else {
      result = new Token(m_Sheet);
    }
    
    header = m_Sheet.getHeaderRow();
    row    = m_Sheet.getRow(rowIndex);
    for (i = 0; i < m_ColumnIndices.length; i++) {
      col  = m_ColumnIndices[i];
      name = Variables.toValidName(m_VariablePrefix + header.getCell(col).getContent());
      if (!row.hasCell(col))
	getVariables().set(name, m_MissingValue);
      else
	getVariables().set(name, row.getCell(col).getContent());
    }
    
    return result;
  }
  
  /**
   * Frees up memory.
   */
  @Override
  public void wrapUp() {
    if (m_Queue != null)
      m_Queue.clear();
    
    m_Sheet = null;
    
    super.wrapUp();
  }
  
  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    m_Queue = null;
    
    super.cleanUp();
  }
}
