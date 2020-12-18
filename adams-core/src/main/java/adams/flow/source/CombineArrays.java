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
 * CombineArrays.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CombineArrays
  extends AbstractSimpleSource
  implements StorageUser {

  /** for serialization. */
  private static final long serialVersionUID = 8955342876774562591L;

  /** the names of the stored values. */
  protected StorageName[] m_StorageNames;

  /** the class for the array. */
  protected String m_ArrayClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Combines the storage items representing arrays into a single array.\n"
      + "When not supplying an array class the type of the first array is used.";
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

    result = QuickInfoHelper.toString(this, "storageNames", Utils.flatten(m_StorageNames, ", "), "names: ");
    if (result == null)
      result = "-no names specified-";
    result += QuickInfoHelper.toString(this, "arrayClass", (m_ArrayClass.length() != 0) ? m_ArrayClass : "-from 1st storage item-", ", Class: ");

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
    String	result;
    int		i;
    int		n;
    Object[]	values;
    int		total;
    Object	all;
    
    result = null;

    // get storage items
    values = new Object[m_StorageNames.length];
    total  = 0;
    for (i = 0; i < m_StorageNames.length; i++) {
      if (getStorageHandler().getStorage().has(m_StorageNames[i])) {
	values[i] = getStorageHandler().getStorage().get(m_StorageNames[i]);
	if (!values[i].getClass().isArray())
	  result = "Storage item #" + (i + 1) + " (" + m_StorageNames[i] + ") is not an array!";
	else
	  total += Array.getLength(values[i]);
      }
      else {
	result = "Storage item #" + (i + 1) + " (" + m_StorageNames[i] + ") not found!";
      }
      if (result != null)
	break;
    }
    
    if (result == null) {
      try {
	if (m_ArrayClass.trim().length() == 0)
	  all = Array.newInstance(values[0].getClass().getComponentType(), total);
	else
	  all = Utils.newArray(m_ArrayClass, total);
	n = 0;
        for (Object value: values) {
          for (i = 0; i < Array.getLength(value); i++) {
            Array.set(all, n, Array.get(value, i));
            n++;
	  }
        }
        m_OutputToken = new Token(all);
      }
      catch (Exception e) {
	result = handleException("Failed to generate array:", e);
      }
    }

    return result;
  }
}
