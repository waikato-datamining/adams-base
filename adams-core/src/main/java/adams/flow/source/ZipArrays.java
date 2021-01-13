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
 * ZipArrays.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.transformer.ArrayToSequence;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Aligns the corresponding elements of the storage arrays into row-based arrays. The adams.flow.transformer.ArrayToSequence actor can then iterate through these rows then.<br>
 * The arrays must have the same length.
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
 * &nbsp;&nbsp;&nbsp;default: ZipArrays
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ZipArrays
  extends AbstractSimpleSource
  implements StorageUser {

  /** for serialization. */
  private static final long serialVersionUID = 8955342876774562591L;

  /** the names of the stored values. */
  protected StorageName[] m_StorageNames;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Aligns the corresponding elements of the storage arrays "
	+ "into row-based arrays. The " + Utils.classToString(ArrayToSequence.class)
	+ " actor can then iterate through these rows then.\n"
	+ "The arrays must have the same length.";
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
    Set<Class>	classes;
    Class	cls;
    Object[]	values;
    int 	length;
    Object	all;
    Object	inner;
    
    result = null;

    // get storage items
    values  = new Object[m_StorageNames.length];
    length  = -1;
    classes = new HashSet<>();
    for (i = 0; i < m_StorageNames.length; i++) {
      if (getStorageHandler().getStorage().has(m_StorageNames[i])) {
	values[i] = getStorageHandler().getStorage().get(m_StorageNames[i]);
	if (!values[i].getClass().isArray())
	  result = "Storage item #" + (i + 1) + " (" + m_StorageNames[i] + ") is not an array!";
	else {
	  if (length == -1)
	    length = Array.getLength(values[i]);
	  else if (length != Array.getLength(values[i]))
	    result = "Storage item #" + (i + 1) + " (" + m_StorageNames[i] + ") has a different length from first one: " + Array.getLength(values[i]) + " != " + Array.getLength(values[0]);
	}
	if (result == null)
	  classes.add(values[i].getClass().getComponentType());
      }
      else {
	result = "Storage item #" + (i + 1) + " (" + m_StorageNames[i] + ") not found!";
      }
      if (result != null)
	break;
    }
    
    if (result == null) {
      try {
        if (classes.size() == 1)
	  cls = values[0].getClass().getComponentType();
        else
	  cls = Object.class;
	inner = Array.newInstance(cls, values.length);
	all   = Array.newInstance(inner.getClass(), length);
	for (n = 0; n < length; n++) {
	  inner = Array.newInstance(cls, values.length);
	  Array.set(all, n, inner);
	  for (i = 0; i < values.length; i++) {
	    Array.set(inner, i, Array.get(values[i], n));
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
