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
 * SpreadSheetAnonymize.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Hashtable;

import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.core.base.BaseRegExp;
import adams.data.AbstractAnonymizer;
import adams.data.DoubleAnonymizer;
import adams.data.StringAnonymizer;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Anonymizes a range of columns in a spreadsheet.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.core.io.SpreadSheet<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.core.io.SpreadSheet<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetAnonymize
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
 * <pre>-col-regexp &lt;adams.core.base.BaseRegExp&gt; (property: columnsRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression applied to the column names to locate the columns 
 * &nbsp;&nbsp;&nbsp;to anonymize.
 * &nbsp;&nbsp;&nbsp;default: ^ID$
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the anonymizers.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetAnonymize
  extends AbstractSpreadSheetTransformer 
  implements Randomizable {

  /** for serialization. */
  private static final long serialVersionUID = -2767861909141864017L;

  /** the key for storing the mapping of column names/anonymizers. */
  public final static String BACKUP_MAPPING = "mapping";

  /** the columns to anonymize. */
  protected BaseRegExp m_ColumnsRegExp;

  /** the seed value. */
  protected long m_Seed;

  /** the column/anonymizer mapping. */
  protected Hashtable<String,AbstractAnonymizer> m_Mapping;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Anonymizes a range of columns in a spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "col-regexp", "columnsRegExp",
	    new BaseRegExp("^ID$"));

    m_OptionManager.add(
	    "seed", "seed",
	    1L);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Mapping = new Hashtable<String,AbstractAnonymizer>();
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_MAPPING);
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

    if (m_Mapping != null)
      result.put(BACKUP_MAPPING, m_Mapping);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_MAPPING)) {
      m_Mapping = (Hashtable<String,AbstractAnonymizer>) state.get(BACKUP_MAPPING);
      state.remove(BACKUP_MAPPING);
    }

    super.restoreState(state);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "columnsRegExp", m_ColumnsRegExp, "cols: ");
    result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");

    return result;
  }

  /**
   * Sets the regular expression for the column names of columns to anonymize.
   *
   * @param value	the regular expression
   */
  public void setColumnsRegExp(BaseRegExp value) {
    m_ColumnsRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for the column names of columns to anonymize.
   *
   * @return		the regular expression
   */
  public BaseRegExp getColumnsRegExp() {
    return m_ColumnsRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsRegExpTipText() {
    return "The regular expression applied to the column names to locate the columns to anonymize.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for the anonymizers.";
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
    Row			row;
    int			i;
    int			n;
    String		colName;
    ArrayList<Boolean>	numeric;
    ArrayList<String>	names;
    ArrayList<String>	id;
    Cell		cell;
    Object		anon;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    
    if (sheet.getRowCount() > 0) {
      // set up anonymizers
      row     = sheet.getHeaderRow();
      numeric = new ArrayList<Boolean>();
      names   = new ArrayList<String>();
      id      = new ArrayList<String>();
      for (i = 0; i < row.getCellCount(); i++) {
	colName = row.getCell(i).getContent();
	if (m_ColumnsRegExp.isMatch(colName)) {
	  names.add(colName);
	  numeric.add(sheet.isNumeric(i, true));
	  id.add(row.getCellKey(i));
	  if (!m_Mapping.containsKey(colName)) {
	    if (numeric.get(numeric.size() - 1))
	      m_Mapping.put(colName, new DoubleAnonymizer(colName, m_Seed, sheet.getRowCount()));
	    else
	      m_Mapping.put(colName, new StringAnonymizer(colName, m_Seed, sheet.getRowCount()));
	  }
	}
      }

      if (names.size() > 0) {
	sheet = sheet.getClone();
	for (n = 0; n < sheet.getRowCount(); n++) {
	  row = sheet.getRow(n);
	  for (i = 0; i < names.size(); i++) {
	    if (row.getCell(id.get(i)).isMissing())
	      continue;
	    cell = row.getCell(id.get(i));
	    if (numeric.get(i))
	      anon = m_Mapping.get(names.get(i)).anonymize(cell.toDouble());
	    else
	      anon = m_Mapping.get(names.get(i)).anonymize(cell.toString());
	    if (anon instanceof Double)
	      cell.setContent((Double) anon);
	    else
	      cell.setContent((String) anon);
	  }
	}
      }
    }

    m_OutputToken = new Token(sheet);

    return result;
  }
}
