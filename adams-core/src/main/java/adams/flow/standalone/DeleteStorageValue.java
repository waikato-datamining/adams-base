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
 * DeleteStorageValue.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;

/**
 <!-- globalinfo-start -->
 * Removes the specified value (or the ones that match the regular expression) from temporary storage.<br>
 * By supplying a cache name, the value can be removed from a LRU cache instead of the regular storage.
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
 * &nbsp;&nbsp;&nbsp;default: DeleteStorageValue
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
 * <pre>-type &lt;NAME|REGEXP&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;How to determine the storage item(s) to delete.
 * &nbsp;&nbsp;&nbsp;default: NAME
 * </pre>
 *
 * <pre>-cache &lt;java.lang.String&gt; (property: cache)
 * &nbsp;&nbsp;&nbsp;The name of the cache to remove the value from; uses the regular storage
 * &nbsp;&nbsp;&nbsp;if left empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the stored value to delete.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used for matching the storage items to delete.
 * &nbsp;&nbsp;&nbsp;default: storage
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DeleteStorageValue
  extends AbstractStandalone
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 3427074997423945878L;

  /**
   * Determines how to locate the variable.
   */
  public enum MatchingType {
    NAME,
    REGEXP,
  }

  /** how to determine variables to delete. */
  protected MatchingType m_Type;

  /** the name of the LRU cache. */
  protected String m_Cache;

  /** the name of the value to store. */
  protected StorageName m_StorageName;

  /** the regexp to match against variable names. */
  protected BaseRegExp m_RegExp;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Removes the specified value (or the ones that match the regular "
	+ "expression) from temporary storage.\n"
	+ "By supplying a cache name, the value can be removed from a LRU cache "
	+ "instead of the regular storage.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      MatchingType.NAME);

    m_OptionManager.add(
      "cache", "cache",
      "");

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(StorageName.DEFAULT));
  }

  /**
   * Sets how to determine storage items to delete.
   *
   * @param value	the matching type
   */
  public void setType(MatchingType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns how to determine storage items to delete.
   *
   * @return		the matching type
   */
  public MatchingType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "How to determine the storage item(s) to delete.";
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
    return "The name of the cache to remove the value from; uses the regular storage if left empty.";
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
    return "The name of the stored value to delete.";
  }

  /**
   * Sets the regular expression to match the storage item names against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the storage item names against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the storage items to delete.";
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

    switch (m_Type) {
      case NAME:
	result = QuickInfoHelper.toString(this, "storageName", m_StorageName);
	break;
      case REGEXP:
	result = QuickInfoHelper.toString(this, "regExp", m_RegExp);
	break;
      default:
	throw new IllegalStateException("Unhandled matching type: " + m_Type);
    }

    value = QuickInfoHelper.toString(this, "cache", (m_Cache.length() > 0 ? m_Cache : ""), " cache: ");
    if (value != null)
      result += value;

    return result;
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
    switch (m_Type) {
      case NAME:
	if (m_Cache.isEmpty())
	  getStorageHandler().getStorage().remove(m_StorageName);
	else
	  getStorageHandler().getStorage().remove(m_Cache, m_StorageName);
	break;

      case REGEXP:
	if (m_Cache.isEmpty())
	  getStorageHandler().getStorage().remove(m_RegExp);
	else
	  getStorageHandler().getStorage().remove(m_Cache, m_RegExp);
	break;

      default:
	throw new IllegalStateException("Unhandled matching type: " + m_Type);
    }

    return null;
  }
}
