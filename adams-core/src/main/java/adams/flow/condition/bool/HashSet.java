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
 * HashSet.java
 * Copyright (C) 2013-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.conversion.Conversion;
import adams.data.conversion.ObjectToObject;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.transformer.HashSetInit;

/**
 <!-- globalinfo-start -->
 * Evaluates to true if the payload of the current token or the specified string (if non-empty) is present in the specified hashset.<br>
 * The value gets transformed using the specified conversion.<br>
 * <br>
 * See also:<br>
 * adams.flow.standalone.HashSetInit<br>
 * adams.flow.transformer.HashSetInit
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the hashset in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: hashset
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value (if non-empty) to look for in the hashset, takes precedence of
 * &nbsp;&nbsp;&nbsp;the token passing through.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-conversion &lt;adams.data.conversion.Conversion&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The type of conversion to perform.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.ObjectToObject
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HashSet
  extends AbstractBooleanCondition
  implements ClassCrossReference {
  
  /** for serialization. */
  private static final long serialVersionUID = -1349114354556041598L;
  
  /** the name of the lookup table in the internal storage. */
  protected StorageName m_StorageName;

  /** the value to check. */
  protected String m_Value;

  /** the type of conversion. */
  protected Conversion m_Conversion;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Evaluates to true if the payload of the current token or the specified "
	+ "string (if non-empty) is present in the specified hashset.\n"
	+ "The value gets transformed using the specified conversion.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{adams.flow.standalone.HashSetInit.class, HashSetInit.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName("hashset"));

    m_OptionManager.add(
	    "value", "value",
	    "");

    m_OptionManager.add(
	    "conversion", "conversion",
	    new ObjectToObject());
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "value", (m_Value.isEmpty() ? "-from token-" : m_Value), ", value: ");
    result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", conversion: ");

    return result;
  }

  /**
   * Sets the name for the hashset in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the hashset in the internal storage.
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
    return "The name of the hashset in the internal storage.";
  }

  /**
   * Sets the (optional) value to look for in the hashset, takes precedence
   * over the token passing through.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the (optional) value to look for in the hashset, takes precedence
   * over the token passing through.
   *
   * @return		the value
   */
  public String getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The value (if non-empty) to look for in the hashset, takes precedence of the token passing through.";
  }

  /**
   * Sets the type of conversion to perform.
   *
   * @param value	the type of conversion
   */
  public void setConversion(Conversion value) {
    m_Conversion = value;
    m_Conversion.setOwner(this);
    reset();
  }

  /**
   * Returns the type of conversion to perform.
   *
   * @return		the type of conversion
   */
  public Conversion getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The type of conversion to perform.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean		result;
    java.util.HashSet	hashset;
    Object		value;
    String		msg;

    if (owner == null) {
      getLogger().warning("No owning actor provided, cannot evaluate!");
      return false;
    }

    result = false;

    if (!owner.getStorageHandler().getStorage().has(m_StorageName)) {
      getLogger().severe("Hashset '" + m_StorageName + "' not available! Not initialized with " + Utils.classesToString(getClassCrossReferences()) + "?");
    }
    else {
      hashset = (java.util.HashSet) owner.getStorageHandler().getStorage().get(m_StorageName);
      if (m_Value.isEmpty())
	value = token.getPayload();
      else
        value = m_Value;
      m_Conversion.setInput(value);
      msg = m_Conversion.convert();
      if (msg != null)
	msg = owner.getFullName() + ": " + msg;
      if ((msg == null) && (m_Conversion.getOutput() != null)) {
        value  = m_Conversion.getOutput();
	result = hashset.contains(value);
      }
    }

    return result;
  }
}
