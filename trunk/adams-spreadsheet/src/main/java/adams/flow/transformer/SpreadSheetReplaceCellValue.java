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
 * SpreadSheetReplaceCellValue.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Hashtable;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Replaces cell values that match a regular expression with a predefined value.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetReplaceCellValue
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
 * <pre>-find &lt;adams.core.base.BaseRegExp&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The string to find (a regular expression).
 * &nbsp;&nbsp;&nbsp;default: find
 * </pre>
 * 
 * <pre>-replace &lt;java.lang.String&gt; (property: replace)
 * &nbsp;&nbsp;&nbsp;The string to replace the occurrences with.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-all &lt;boolean&gt; (property: replaceAll)
 * &nbsp;&nbsp;&nbsp;If set to true, then all occurrences will be replaced; otherwise only the 
 * &nbsp;&nbsp;&nbsp;first.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-placeholder &lt;boolean&gt; (property: replaceContainsPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion for the replacement 
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-variable &lt;boolean&gt; (property: replaceContainsVariable)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic variable expansion for the replacement 
 * &nbsp;&nbsp;&nbsp;string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-scope &lt;DATA_ONLY|HEADER_ONLY|HEADER_AND_DATA&gt; (property: scope)
 * &nbsp;&nbsp;&nbsp;Determines the scope of the find&#47;replace action.
 * &nbsp;&nbsp;&nbsp;default: DATA_ONLY
 * </pre>
 * 
 * <pre>-position &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position of the columns to process.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetReplaceCellValue
  extends AbstractInPlaceSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2639196406457405487L;

  /** the key for storing the current actual replace in the backup. */
  public final static String BACKUP_ACTUALREPLACE = "actual replace";

  /**
   * Defines the scope of the replace.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Scope {
    /** only data. */
    DATA_ONLY,
    /** only header. */
    HEADER_ONLY,
    /** all. */
    HEADER_AND_DATA
  }

  /** the string to find. */
  protected BaseRegExp m_Find;

  /** the replacement string. */
  protected String m_Replace;

  /** the actual replacement string (after optional placeholder expansion). */
  protected String m_ActualReplace;

  /** whether to replace all or only the first occurrence. */
  protected boolean m_ReplaceAll;

  /** whether the replace string contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_ReplaceContainsPlaceholder;

  /** whether the replace string contains a variable, which needs to be
   * expanded first. */
  protected boolean m_ReplaceContainsVariable;

  /** the scope of the replace. */
  protected Scope m_Scope;
  
  /** the position of the columns to process. */
  protected SpreadSheetColumnRange m_Position;  

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Replaces cell values that match a regular expression with a "
      + "predefined value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "find", "find",
	    new BaseRegExp("find"));

    m_OptionManager.add(
	    "replace", "replace",
	    "");

    m_OptionManager.add(
	    "all", "replaceAll",
	    false);

    m_OptionManager.add(
	    "placeholder", "replaceContainsPlaceholder",
	    false);

    m_OptionManager.add(
	    "variable", "replaceContainsVariable",
	    false);

    m_OptionManager.add(
	    "scope", "scope",
	    Scope.DATA_ONLY);

    m_OptionManager.add(
	    "position", "position",
	    new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	replace;
    String	find;

    replace = QuickInfoHelper.toString(this, "replace", m_Replace);
    if (replace == null)
      replace = "";
    find    = QuickInfoHelper.toString(this, "find", m_Find);
    if (find == null)
      find = "";

    if (replace.length() > 0)
      result = "replace ";
    else
      result = "remove ";
    result += "'" + find + "'";

    if (replace.length() > 0)
      result += " with '" + replace + "'";

    result += " (" + QuickInfoHelper.toString(this, "scope", m_Scope) + ")";
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy", ", ");
    result += QuickInfoHelper.toString(this, "position", m_Position, ", cols: ");

    return result;
  }

  /**
   * Sets the string to find (regular expression).
   *
   * @param value	the string
   */
  public void setFind(BaseRegExp value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the string to find (regular expression).
   *
   * @return		the string
   */
  public BaseRegExp getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The string to find (a regular expression).";
  }

  /**
   * Sets the string to replace the occurrences with.
   *
   * @param value	the string
   */
  public void setReplace(String value) {
    m_Replace = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the string to replace the occurences with.
   *
   * @return		the string
   */
  public String getReplace() {
    return Utils.backQuoteChars(m_Replace);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return "The string to replace the occurrences with.";
  }

  /**
   * Sets whether all occurrences are replaced or only the first.
   *
   * @param value	true if all are to be replaced, false if only the first
   */
  public void setReplaceAll(boolean value) {
    m_ReplaceAll = value;
    reset();
  }

  /**
   * Returns whether all occurrences are replaced or only the first one.
   *
   * @return		true if all are to be replaced, false if only the first
   */
  public boolean getReplaceAll() {
    return m_ReplaceAll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceAllTipText() {
    return "If set to true, then all occurrences will be replaced; otherwise only the first.";
  }

  /**
   * Sets whether the replace string contains a placeholder which needs to be
   * expanded first.
   *
   * @param value	true if replace string contains a placeholder
   */
  public void setReplaceContainsPlaceholder(boolean value) {
    m_ReplaceContainsPlaceholder = value;
    reset();
  }

  /**
   * Returns whether the replace string contains a placeholder which needs to be
   * expanded first.
   *
   * @return		true if replace string contains a placeholder
   */
  public boolean getReplaceContainsPlaceholder() {
    return m_ReplaceContainsPlaceholder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceContainsPlaceholderTipText() {
    return "Set this to true to enable automatic placeholder expansion for the replacement string.";
  }

  /**
   * Sets whether the replace string contains a variable which needs to be
   * expanded first.
   *
   * @param value	true if replace string contains a variable
   */
  public void setReplaceContainsVariable(boolean value) {
    m_ReplaceContainsVariable = value;
    reset();
  }

  /**
   * Returns whether the replace string contains a variable which needs to be
   * expanded first.
   *
   * @return		true if replace string contains a variable
   */
  public boolean getReplaceContainsVariable() {
    return m_ReplaceContainsVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceContainsVariableTipText() {
    return "Set this to true to enable automatic variable expansion for the replacement string.";
  }

  /**
   * Sets the scope of the replace.
   *
   * @param value	the scope
   */
  public void setScope(Scope value) {
    m_Scope = value;
    reset();
  }

  /**
   * Returns the scope of the replace.
   *
   * @return		the scope
   */
  public Scope getScope() {
    return m_Scope;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeTipText() {
    return "Determines the scope of the find/replace action.";
  }

  /**
   * Sets the position of the columns to process.
   *
   * @param value	the position
   */
  public void setPosition(SpreadSheetColumnRange value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the position of the columns to process.
   *
   * @return		the position
   */
  public SpreadSheetColumnRange getPosition() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String positionTipText() {
    return "The position of the columns to process.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_ACTUALREPLACE);
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

    result.put(BACKUP_ACTUALREPLACE, m_ActualReplace);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_ACTUALREPLACE)) {
      m_ActualReplace = (String) state.get(BACKUP_ACTUALREPLACE);
      state.remove(BACKUP_ACTUALREPLACE);
    }

    super.restoreState(state);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_ActualReplace = m_Replace;

      // do we need to expand placeholders?
      if (m_ReplaceContainsPlaceholder)
	m_ActualReplace = Placeholders.getSingleton().expand(m_ActualReplace).replace("\\", "/");
    }

    return result;
  }

  /**
   * Processes the string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String process(String s) {
    String	replace;

    // do we need to replace variables?
    replace = m_ActualReplace;
    if (m_ReplaceContainsVariable)
      replace = getVariables().expand(replace);

    if (m_ReplaceAll)
      return s.replaceAll(m_Find.getValue(), replace);
    else
      return s.replaceFirst(m_Find.getValue(), replace);
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
    SpreadSheet		updated;
    Row			row;
    int			i;
    String		curValue;
    String		newValue;
    boolean		modified;
    int[]		cols;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    if (m_NoCopy)
      updated = sheet;
    else
      updated  = sheet.getClone();
    modified = false;
    
    m_Position.setData(updated);
    cols = m_Position.getIntIndices();
    if (cols.length == 0)
      result = "No columns found to process: " + m_Position.getRange();

    if (result == null) {
      // header
      if ((m_Scope == Scope.HEADER_AND_DATA) || (m_Scope == Scope.HEADER_ONLY)) {
	row = updated.getHeaderRow();
	for (int n: cols) {
	  if (!row.hasCell(n) || row.getCell(n).isMissing())
	    continue;
	  curValue = row.getCell(n).getContent();
	  newValue = process(curValue);
	  if (!newValue.equals(curValue)) {
	    row.getCell(n).setContent(newValue);
	    modified = true;
	  }
	}
      }

      // data
      if ((m_Scope == Scope.HEADER_AND_DATA) || (m_Scope == Scope.DATA_ONLY)) {
	for (i = 0; i < updated.getRowCount(); i++) {
	  row = updated.getRow(i);
	  for (int n: cols) {
	    if (!row.hasCell(n) || row.getCell(n).isMissing())
	      continue;
	    curValue = row.getCell(n).getContent();
	    newValue = process(curValue);
	    if (!newValue.equals(curValue)) {
	      row.getCell(n).setContent(newValue);
	      modified = true;
	    }
	  }
	}
      }

      if (isLoggingEnabled())
	getLogger().info("Spreadsheet modified: " + modified);

      m_OutputToken = new Token(updated);
    }

    return result;
  }
}
