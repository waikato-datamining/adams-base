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
 * LookUp.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.HashMap;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.rowfinder.MissingValue;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.MissingLookUpKey;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Forwards the value associated with the key that the actor gets as input, using the specified lookup table from internal storage.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: LookUp
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
 * &nbsp;&nbsp;&nbsp;The name for the lookup table in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: lookup
 * </pre>
 * 
 * <pre>-missing-key &lt;NO_OUTPUT|OUTPUT_MISSING_VALUE|OUTPUT_KEY|CAUSE_ERROR&gt; (property: missingKey)
 * &nbsp;&nbsp;&nbsp;The behavior in case a lookup key is missing (ie not found in the lookup 
 * &nbsp;&nbsp;&nbsp;table).
 * &nbsp;&nbsp;&nbsp;default: NO_OUTPUT
 * </pre>
 * 
 * <pre>-missing-value &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The value to forward if the missing key behavior is OUTPUT_MISSING_VALUE.
 * &nbsp;&nbsp;&nbsp;default: ???
 * </pre>
 * 
 * <pre>-use-native &lt;boolean&gt; (property: useNative)
 * &nbsp;&nbsp;&nbsp;If enabled, native objects are output rather than strings.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-suppress-missing-key-warnings &lt;boolean&gt; (property: suppressMissingKeyWarnings)
 * &nbsp;&nbsp;&nbsp;If enabled, warnings about missing keys are suppressed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LookUp
  extends AbstractTransformer
  implements StorageUser {

  /** for serialization. */
  private static final long serialVersionUID = 444466366407383727L;
  
  /** the name of the lookup table in the internal storage. */
  protected StorageName m_StorageName;

  /** the behavior for missing keys. */
  protected MissingLookUpKey m_MissingKey;

  /** the missing value (only used for {@link MissingValue#OUTPUT_MISSING_VALUE}). */
  protected String m_MissingValue;
  
  /** whether to output native objects rather than strings. */
  protected boolean m_UseNative;
  
  /** whether to suppress warnings when key is not present. */
  protected boolean m_SuppressMissingKeyWarnings;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Forwards the value associated with the key that the actor gets as "
	+ "input, using the specified lookup table from internal storage.";
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
	    "missing-key", "missingKey",
	    MissingLookUpKey.NO_OUTPUT);

    m_OptionManager.add(
	    "missing-value", "missingValue",
	    "???");

    m_OptionManager.add(
	    "use-native", "useNative",
	    false);

    m_OptionManager.add(
	    "suppress-missing-key-warnings", "suppressMissingKeyWarnings",
	    false);
  }

  /**
   * Returns whether storage items are being used.
   * 
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
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
    result += QuickInfoHelper.toString(this, "missingKey", m_MissingKey, ", missing: ");
    result += QuickInfoHelper.toString(this, "missingValue", m_MissingValue, ", value: ");
    value = QuickInfoHelper.toString(this, "useNative", m_UseNative, ", native");
    if (value != null)
      result += value;
    value = QuickInfoHelper.toString(this, "suppressMissingKeyWarnings", m_SuppressMissingKeyWarnings, ", no missing key warnings");
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
   * Sets the behavior for missing keys.
   *
   * @param value	the behavior
   */
  public void setMissingKey(MissingLookUpKey value) {
    m_MissingKey = value;
    reset();
  }

  /**
   * Returns the behavior for missing keys.
   *
   * @return		the behavior
   */
  public MissingLookUpKey getMissingKey() {
    return m_MissingKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingKeyTipText() {
    return "The behavior in case a lookup key is missing (ie not found in the lookup table).";
  }

  /**
   * Sets the value to be used if behavior is {@link MissingLookUpKey#OUTPUT_MISSING_VALUE}.
   *
   * @param value	the value to use
   * @see		MissingLookUpKey#OUTPUT_MISSING_VALUE
   */
  public void setMissingValue(String value) {
    m_MissingValue = value;
    reset();
  }

  /**
   * Returns the value used if behavior is {@link MissingLookUpKey#OUTPUT_MISSING_VALUE}.
   *
   * @return		the value in use
   * @see		MissingLookUpKey#OUTPUT_MISSING_VALUE
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
    return "The value to forward if the missing key behavior is " + MissingLookUpKey.OUTPUT_MISSING_VALUE + ".";
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
   * Sets whether to suppress warnings about missing keys.
   *
   * @param value	true if to suppress warnings
   */
  public void setSuppressMissingKeyWarnings(boolean value) {
    m_SuppressMissingKeyWarnings = value;
    reset();
  }

  /**
   * Returns whether to suppress warnings about missing keys.
   *
   * @return		true if warnings suppressed
   */
  public boolean getSuppressMissingKeyWarnings() {
    return m_SuppressMissingKeyWarnings;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppressMissingKeyWarningsTipText() {
    return "If enabled, warnings about missing keys are suppressed.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
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
      return new Class[]{String.class};
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
    String			key;
    Object			value;
    
    result = null;
    
    if (!getStorageHandler().getStorage().has(m_StorageName)) {
      result = "Lookup table '" + m_StorageName + "' not available! Not initialized with " + LookUpInit.class.getName() + "?";
    }
    else {
      lookup = (HashMap<String,Object>) getStorageHandler().getStorage().get(m_StorageName);
      key    = (String) m_InputToken.getPayload();
      if (lookup.containsKey(key)) {
	value = lookup.get(key);
	if (isLoggingEnabled())
	  getLogger().info("Lookup: '" + key + "' -> '" + value + "'");
	m_OutputToken = new Token(value);
      }
      else {
	switch (m_MissingKey) {
	  case NO_OUTPUT:
	    if (!m_SuppressMissingKeyWarnings)
	    getLogger().severe("Key '" + key + "' not available from lookup table '" + m_StorageName + "'!");
	    break;
	  case CAUSE_ERROR:
	    if (!m_SuppressMissingKeyWarnings)
	      getLogger().severe("Key '" + key + "' not available from lookup table '" + m_StorageName + "'!");
	    result = "Key '" + key + "' not available from lookup table '" + m_StorageName + "'!";
	    break;
	  case OUTPUT_KEY:
	    m_OutputToken = new Token(key);
	    if (!m_SuppressMissingKeyWarnings)
	      getLogger().warning("Key '" + key + "' not available from lookup table '" + m_StorageName + "', using key!");
	    break;
	  case OUTPUT_MISSING_VALUE:
	    m_OutputToken = new Token(m_MissingValue);
	    if (!m_SuppressMissingKeyWarnings)
	      getLogger().warning("Key '" + key + "' not available from lookup table '" + m_StorageName + "', using missing value: " + m_MissingValue);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled missing key behavior: " + m_MissingKey);
	}
      }
    }
    
    return result;
  }
}
