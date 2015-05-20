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
 * LookUpInit.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.HashMap;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.LookUpHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;

/**
 <!-- globalinfo-start -->
 * Creates a lookup table from a spreadsheet, using one column as key and another one as value. The lookup table itself gets stored in the internal storage under the specified name.
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
 * &nbsp;&nbsp;&nbsp;default: LookUpInit
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name for the lookup table in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: lookup
 * </pre>
 * 
 * <pre>-key-column &lt;adams.core.Index&gt; (property: keyColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column in the spreadsheet to use as key; An index is a 
 * &nbsp;&nbsp;&nbsp;number starting with 1; the following placeholders can be used as well: 
 * &nbsp;&nbsp;&nbsp;first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-value-column &lt;adams.core.Index&gt; (property: valueColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column in the spreadsheet to use as value; An index is 
 * &nbsp;&nbsp;&nbsp;a number starting with 1; the following placeholders can be used as well:
 * &nbsp;&nbsp;&nbsp; first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 2
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LookUpInit
  extends AbstractSpreadSheetTransformer
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 4182914190162129217L;

  /** the name of the lookup table in the internal storage. */
  protected StorageName m_StorageName;

  /** the index of the column to use as key. */
  protected SpreadSheetColumnIndex m_KeyColumn;

  /** the index of the column to use as value. */
  protected SpreadSheetColumnIndex m_ValueColumn;
  
  /** whether to output native objects rather than strings. */
  protected boolean m_UseNative;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Creates a lookup table from a spreadsheet, using one column as key "
	+ "and another one as value. The lookup table itself gets stored in "
	+ "the internal storage under the specified name.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName("lookup"));

    m_OptionManager.add(
	    "key-column", "keyColumn",
	    new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
	    "value-column", "valueColumn",
	    new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
	    "use-native", "useNative",
	    false);
  }

  /**
   * Returns whether storage items are being updated.
   * 
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return !getSkip();
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

    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "keyColumn", m_KeyColumn, ", key: ");
    result += QuickInfoHelper.toString(this, "valueColumn", m_ValueColumn, ", value: ");
    value = QuickInfoHelper.toString(this, "useNative", m_UseNative, ", native");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Sets the name for the lookup table in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the lookup table in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name for the lookup table in the internal storage.";
  }

  /**
   * Sets the index of the column to act as key in the lookup table.
   *
   * @param value	the index
   */
  public void setKeyColumn(SpreadSheetColumnIndex value) {
    m_KeyColumn = value;
    reset();
  }

  /**
   * Returns the index of the column to act as key in the lookup table.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getKeyColumn() {
    return m_KeyColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyColumnTipText() {
    return "The index of the column in the spreadsheet to use as key; " + m_KeyColumn.getExample();
  }

  /**
   * Sets the index of the column to act as value in the lookup table.
   *
   * @param value	the index
   */
  public void setValueColumn(SpreadSheetColumnIndex value) {
    m_ValueColumn = value;
    reset();
  }

  /**
   * Returns the index of the column to act as value in the lookup table.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getValueColumn() {
    return m_ValueColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueColumnTipText() {
    return "The index of the column in the spreadsheet to use as value; " + m_ValueColumn.getExample();
  }

  /**
   * Sets whether to output native objects rather than strings.
   *
   * @param value	true if to output native objects
   */
  public void setUseNative(boolean value) {
    m_UseNative = value;
    reset();
  }

  /**
   * Returns whether native objects are output rather than strings.
   *
   * @return		true if native objects are used
   */
  public boolean getUseNative() {
    return m_UseNative;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useNativeTipText() {
    return "If enabled, native objects are used as value rather than strings.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			sheet;
    HashMap<String,Object>	lookup;
    StringBuilder		error;
    
    result = null;
    
    sheet  = (SpreadSheet) m_InputToken.getPayload();
    error  = new StringBuilder();
    lookup = LookUpHelper.load(sheet, m_KeyColumn.getIndex(), m_ValueColumn.getIndex(), m_UseNative, error);
    if (lookup == null)
      result = error.toString();
    else
      getStorageHandler().getStorage().put(m_StorageName, lookup);
    
    if (result == null)
      m_OutputToken = m_InputToken;
    
    return result;
  }
}
