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
 * StorageValuesArray.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.data.conversion.Conversion;
import adams.data.conversion.UnknownToUnknown;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 * Outputs the values associated with the specified names from temporary storage as an array.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: StorageValuesArray
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; [-storage-name ...] (property: storageNames)
 * &nbsp;&nbsp;&nbsp;The names of the stored values to retrieve as array.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-array-class &lt;java.lang.String&gt; (property: arrayClass)
 * &nbsp;&nbsp;&nbsp;The class to use for the array; if none is specified, the class of the first 
 * &nbsp;&nbsp;&nbsp;storage item is used.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.Conversion&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The type of conversion to perform.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.UnknownToUnknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class StorageValuesArray
  extends AbstractSource
  implements StorageUser {

  /** for serialization. */
  private static final long serialVersionUID = 8955342876774562591L;

  /** the names of the stored values. */
  protected StorageName[] m_StorageNames;

  /** the class for the array. */
  protected String m_ArrayClass;

  /** the stored value. */
  protected Object m_StoredValue;

  /** the type of conversion. */
  protected Conversion m_Conversion;

  /**
   * Default constructor.
   */
  public StorageValuesArray() {
    super();
  }

  /**
   * Initializes with the specified storage names.
   *
   * @param storageNames    the names to use
   */
  public StorageValuesArray(StorageName[] storageNames) {
    this();
    setStorageNames(storageNames);
  }

  /**
   * Initializes with the specified storage names.
   *
   * @param storageNames    the names to use
   */
  public StorageValuesArray(String[] storageNames) {
    this();
    setStorageNames(storageNames);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Outputs the values associated with the specified names from "
      + "temporary storage as an array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageNames",
	    new StorageName[0]);

    m_OptionManager.add(
	    "array-class", "arrayClass",
	    "");

    m_OptionManager.add(
	    "conversion", "conversion",
	    new UnknownToUnknown());
  }

  /**
   * Adds the storage name.
   *
   * @param value	the name
   */
  public void addStorageName(StorageName value) {
    m_StorageNames = (StorageName[]) Utils.adjustArray(m_StorageNames, m_StorageNames.length + 1, value);
    reset();
  }

  /**
   * Sets the names of the stored values.
   *
   * @param value	the names
   */
  public void setStorageNames(String[] value) {
    setStorageNames((StorageName[]) BaseObject.toObjectArray(value, StorageName.class));
  }

  /**
   * Sets the names of the stored values.
   *
   * @param value	the names
   */
  public void setStorageNames(StorageName[] value) {
    m_StorageNames = value;
    reset();
  }

  /**
   * Returns the names of the stored values.
   *
   * @return		the names
   */
  public StorageName[] getStorageNames() {
    return m_StorageNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNamesTipText() {
    return "The names of the stored values to retrieve as array.";
  }

  /**
   * Sets the class for the array.
   *
   * @param value	the classname, use empty string to use class of first
   * 			element
   */
  public void setArrayClass(String value) {
    m_ArrayClass = value;
    reset();
  }

  /**
   * Returns the class for the array.
   *
   * @return		the classname, empty string if class of first element
   * 			is used
   */
  public String getArrayClass() {
    return m_ArrayClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String arrayClassTipText() {
    return
        "The class to use for the array; if none is specified, the class of "
      + "the first storage item is used.";
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
   * Returns whether storage items are being used.
   * 
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    return !getSkip() && (m_StorageNames.length > 0);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "storageNames", Utils.flatten(m_StorageNames, ", "), "Names: ");
    if (result == null)
      result = "-no names specified-";
    result += QuickInfoHelper.toString(this, "arrayClass", (m_ArrayClass.length() != 0) ? m_ArrayClass : "-from 1st storage item-", ", Class: ");
    result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", conversion: ");

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_StoredValue = null;
  }

  /**
   * Hook for performing setup checks -- used in setUp() and preExecute().
   *
   * @param fromSetUp	whether the method has been called from within setUp()
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String performSetUpChecks(boolean fromSetUp) {
    String	result;

    result = super.performSetUpChecks(fromSetUp);

    if (result == null) {
      if (canPerformSetUpCheck(fromSetUp, "storageNames")) {
	if ((m_StorageNames == null) || (m_StorageNames.length == 0))
	  result = "No names specified for storage values!";
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    MessageCollection	errors;
    int			i;
    Object[]		values;
    
    result = null;
    errors = new MessageCollection();

    // get storage items
    values = new Object[m_StorageNames.length];
    for (i = 0; i < m_StorageNames.length; i++) {
      if (getStorageHandler().getStorage().has(m_StorageNames[i]))
	values[i] = getStorageHandler().getStorage().get(m_StorageNames[i]);
      else
	errors.add("Storage item #" + (i+1) + " (" + m_StorageNames[i] + ") not found!");
      if (!errors.isEmpty())
	break;
    }
    
    if (errors.isEmpty()) {
      try {
	if (m_ArrayClass.trim().isEmpty())
	  m_StoredValue = Array.newInstance(values[0].getClass(), values.length);
	else
	  m_StoredValue = Utils.newArray(m_ArrayClass, values.length);
        for (i = 0; i < values.length; i++) {
          m_Conversion.setInput(values[i]);
          result = m_Conversion.convert();
          if (result != null)
            errors.add(getFullName() + ": " + result);
          if ((result == null) && (m_Conversion.getOutput() != null))
            Array.set(m_StoredValue, i, m_Conversion.getOutput());
          m_Conversion.cleanUp();
        }
      }
      catch (Exception e) {
	errors.add("Failed to generate array:", e);
      }
    }

    if (errors.isEmpty())
      return null;
    else
      return errors.toString();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result          = new Token(m_StoredValue);
    m_StoredValue = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_StoredValue != null);
  }
}
