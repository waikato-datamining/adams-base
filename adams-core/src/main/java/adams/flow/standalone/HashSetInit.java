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
 * HashSetInit.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;

import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Creates an empty hashset in internal storage under the specified name.<br>
 * Initial (string) values can be supplied as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: HashSetInit
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the hashset in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: hashset
 * </pre>
 * 
 * <pre>-initial &lt;adams.core.base.BaseString&gt; [-initial ...] (property: initial)
 * &nbsp;&nbsp;&nbsp;The (optional) initial strings to populate the hashset with.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HashSetInit
  extends AbstractStandalone
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 4182914190162129217L;

  /** the name of the hashset in the internal storage. */
  protected StorageName m_StorageName;

  /** the initial strings to populate the hashset with. */
  protected BaseString[] m_Initial;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Creates an empty hashset in internal storage under the specified name.\n"
	+ "Initial (string) values can be supplied as well.";
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
	    "initial", "initial",
	    new BaseString[0]);
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

    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "initial", (m_Initial.length == 0 ? "-no initial values-" : m_Initial), ", initial: ");

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
   * Sets the (optional) initial values to populate the hashset with.
   *
   * @param value	the values
   */
  public void setInitial(BaseString[] value) {
    m_Initial = value;
    reset();
  }

  /**
   * Returns the (optional) initial values to populate the hashset with.
   *
   * @return		the values
   */
  public BaseString[] getInitial() {
    return m_Initial;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initialTipText() {
    return "The (optional) initial strings to populate the hashset with.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    HashSet	hashset;

    hashset = new HashSet();
    for (BaseString value: m_Initial)
      hashset.add(value.getValue());
    getStorageHandler().getStorage().put(m_StorageName, hashset);
    return null;
  }
}
