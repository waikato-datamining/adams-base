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
 * LookUpAdd.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.LookUpHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.control.StorageName;

import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Adds key-value pairs to the specified lookup table.<br>
 * The input can either an array or a spreadsheet.<br>
 * If the input is an array, it must have length 2, with the first element the key and the second one the value. In case of a spreadsheet, the pairs are loaded using the specified columns.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: LookUpAdd
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name for the lookup table in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: lookup
 * </pre>
 * 
 * <pre>-key-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: keyColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column in the spreadsheet to use as key; An index is a 
 * &nbsp;&nbsp;&nbsp;number starting with 1; column names (case-sensitive) as well as the following 
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-value-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: valueColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column in the spreadsheet to use as value; An index is 
 * &nbsp;&nbsp;&nbsp;a number starting with 1; column names (case-sensitive) as well as the following 
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-use-native &lt;boolean&gt; (property: useNative)
 * &nbsp;&nbsp;&nbsp;If enabled, native objects are used as value rather than strings.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LookUpAdd
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7602201480653131469L;
  
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
      "Adds key-value pairs to the specified lookup table.\n"
	+ "The input can either an array or a spreadsheet.\n"
	+ "If the input is an array, it must have length 2, with the first "
	+ "element the key and the second one the value. In case of "
	+ "a spreadsheet, the pairs are loaded using the specified columns.";
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "keyColumn", m_KeyColumn, ", key: ");
    result += QuickInfoHelper.toString(this, "valueColumn", m_ValueColumn, ", value: ");
    value   = QuickInfoHelper.toString(this, "useNative", m_UseNative, ", native");
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
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    if (m_UseNative)
      return new Class[]{Object[].class, SpreadSheet.class};
    else
      return new Class[]{String[].class, SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    if (m_UseNative)
      return new Class[]{Object[].class, SpreadSheet.class};
    else
      return new Class[]{String[].class, SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    HashMap<String,Object>	lookup;
    HashMap<String,Object>	lookupAdd;
    Object[]			pair;
    String			key;
    Object			value;
    SpreadSheet			sheet;
    StringBuilder		error;

    result = null;
    
    if (!getStorageHandler().getStorage().has(m_StorageName)) {
      result = "Lookup table '" + m_StorageName + "' not available! Not initialized with " + LookUpInit.class.getName() + "?";
    }
    else {
      lookup = (HashMap<String,Object>) getStorageHandler().getStorage().get(m_StorageName);
      if (m_InputToken.getPayload() instanceof SpreadSheet) {
	sheet     = (SpreadSheet) m_InputToken.getPayload();
	error     = new StringBuilder();
	lookupAdd = LookUpHelper.load(sheet, m_KeyColumn.getIndex(), m_ValueColumn.getIndex(), m_UseNative, error);
	if (lookupAdd == null) {
	  result = error.toString();
	}
	else {
	  lookup.putAll(lookupAdd);
	  getStorageHandler().getStorage().put(m_StorageName, lookup);
	}
      }
      else {
	pair = (Object[]) m_InputToken.getPayload();
	if (pair.length != 2) {
	  result = "Array must have length 2, provided: " + pair.length;
	}
	else {
	  key = pair[0].toString();
	  value = pair[1];
	  if (isLoggingEnabled()) {
	    if (lookup.containsKey(key))
	      getLogger().info("Replacing: '" + key + "' -> '" + value + "'");
	    else
	      getLogger().info("Adding: '" + key + "' -> '" + value + "'");
	  }
	  lookup.put(key, value);
	}
      }
    }

    if (result == null)
      m_OutputToken = m_InputToken;

    return result;
  }
}
