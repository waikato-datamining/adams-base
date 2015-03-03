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
 * SetStorageValue.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Stores the payload of the token passing through under the specified name in temporary storage.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
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
 * &nbsp;&nbsp;&nbsp;default: SetStorageValue
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
 * <pre>-cache &lt;java.lang.String&gt; (property: cache)
 * &nbsp;&nbsp;&nbsp;The name of the cache to store the value in; uses the regular storage if
 * &nbsp;&nbsp;&nbsp;left empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the stored value to retrieve.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetStorageValue
  extends AbstractTransformer
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 3086015634110488066L;

  /** the name of the LRU cache. */
  protected String m_Cache;

  /** the name of the value to store. */
  protected StorageName m_StorageName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Stores the payload of the token passing through under the specified "
      + "name in temporary storage.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cache", "cache",
	    "");

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName());
  }

  /**
   * Sets the name of the LRU cache to use, regular storage if left empty.
   *
   * @param value	the cache
   */
  public void setCache(String value) {
    m_Cache = value;
    reset();
  }

  /**
   * Returns the name of the LRU cache to use, regular storage if left empty.
   *
   * @return		the cache
   */
  public String getCache() {
    return m_Cache;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cacheTipText() {
    return "The name of the cache to store the value in; uses the regular storage if left empty.";
  }

  /**
   * Sets the name of the stored value.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the stored value.
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
    return "The name of the stored value to retrieve.";
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

    result = QuickInfoHelper.toString(this, "storageName", m_StorageName);
    value  = QuickInfoHelper.toString(this, "cache", (m_Cache.length() > 0 ? m_Cache : ""), " cache: ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
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
      if (canPerformSetUpCheck(fromSetUp, "storageName")) {
	if ((m_StorageName == null) || (m_StorageName.getValue().length() == 0))
	  result = "No name specified for storing value!";
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
    if (m_InputToken.getPayload() != null) {
      if (m_Cache.length() == 0)
	getStorageHandler().getStorage().put(m_StorageName, m_InputToken.getPayload());
      else
	getStorageHandler().getStorage().put(m_Cache, m_StorageName, m_InputToken.getPayload());
    }

    m_OutputToken = m_InputToken;

    return null;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }
}
