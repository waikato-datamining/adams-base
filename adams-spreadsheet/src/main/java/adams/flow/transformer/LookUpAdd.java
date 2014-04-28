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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.HashMap;

import adams.core.QuickInfoHelper;
import adams.flow.control.StorageName;

/**
 <!-- globalinfo-start -->
 * Adds a key-value pair to the specified lookup table. <br/>
 * The input for the actor a string array of length 2, with the first element the key and the second one the value.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
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
 * &nbsp;&nbsp;&nbsp;default: LookUpAdd
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
	"Adds a key-value pair to the specified lookup table. \n"
	+ "The input for the actor a string array of length 2, with the first "
	+ "element the key and the second one the value.";
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
      return new Class[]{Object[].class};
    else
      return new Class[]{String[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    if (m_UseNative)
      return new Class[]{Object.class};
    else
      return new Class[]{String[].class};
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
    Object[]			pair;
    String			key;
    Object			value;
    
    result = null;
    
    if (!getStorageHandler().getStorage().has(m_StorageName)) {
      result = "Lookup table '" + m_StorageName + "' not available! Not initialized with " + LookUpInit.class.getName() + "?";
    }
    else {
      lookup = (HashMap<String,Object>) getStorageHandler().getStorage().get(m_StorageName);
      pair   = (Object[]) m_InputToken.getPayload();
      if (pair.length != 2) {
	result = "String array must have length 2, provided: " + pair.length;
      }
      else {
	key   = pair[0].toString();
	value = pair[1];
	if (isLoggingEnabled()) {
	  if (lookup.containsKey(key))
	    getLogger().info("Replacing: '" + key + "' -> '" + value + "'");
	  else
	    getLogger().info("Adding: '" + key + "' -> '" + value + "'");
	}
	lookup.put(key, value);
	m_OutputToken = m_InputToken;
      }
    }
    
    return result;
  }
}
