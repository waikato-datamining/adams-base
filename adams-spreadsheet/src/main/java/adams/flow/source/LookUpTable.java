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
 * LookUpTable.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.transformer.LookUpInit;

/**
 <!-- globalinfo-start -->
 * Outputs the specified lookup table as spreadsheet, containing to columns: Key with the keys and Value with the associated values.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
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
 * &nbsp;&nbsp;&nbsp;default: LookUpTable
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the lookup table in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: lookup
 * </pre>
 * 
 * <pre>-use-native &lt;boolean&gt; (property: useNative)
 * &nbsp;&nbsp;&nbsp;If enabled, native objects are output rather than strings.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LookUpTable
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = -4888807180866059350L;

  /** the column of the spreadsheet containing the keys. */
  public final static String COLUMN_KEY = "Key";

  /** the column of the spreadsheet containing the values. */
  public final static String COLUMN_VALUE = "Value";

  /** the name of the lookup table in the internal storage. */
  protected StorageName m_StorageName;
  
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
	"Outputs the specified lookup table as spreadsheet, containing to columns: "
	+ COLUMN_KEY + " with the keys and " + COLUMN_VALUE + " with the associated values.";
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
    value = QuickInfoHelper.toString(this, "useNative", m_UseNative, ", native");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Sets the name of the lookup table in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the lookup table in the internal storage.
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
    return "The name of the lookup table in the internal storage.";
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
    return "If enabled, native objects are output rather than strings.";
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
    SpreadSheet			sheet;
    ArrayList<String>		keys;
    Row				row;
    
    result = null;
    
    if (!getStorageHandler().getStorage().has(m_StorageName)) {
      result = "Lookup table '" + m_StorageName + "' not available! Not initialized with " + LookUpInit.class.getName() + "?";
    }
    else {
      lookup = (HashMap<String,Object>) getStorageHandler().getStorage().get(m_StorageName);
      sheet  = new SpreadSheet();
      sheet.setName(m_StorageName.getValue());
      sheet.getHeaderRow().addCell("k").setContent(COLUMN_KEY);
      sheet.getHeaderRow().addCell("v").setContent(COLUMN_VALUE);
      keys = new ArrayList<String>(lookup.keySet());
      Collections.sort(keys);
      for (String key: keys) {
	row = sheet.addRow();
	row.addCell("k").setContent(key);
	if (m_UseNative)
	  row.addCell("v").setNative(lookup.get(key));
	else
	  row.addCell("v").setContent(lookup.get(key).toString());
      }
      m_OutputToken = new Token(sheet);
    }
    
    return result;
  }
}
