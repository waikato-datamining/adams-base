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
 * CounterInit.java
 * Copyright (C) 2015-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.NamedCounter;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;

/**
 <!-- globalinfo-start -->
 * Creates an empty counter in internal storage under the specified name.
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
 * &nbsp;&nbsp;&nbsp;default: CounterInit
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
 * &nbsp;&nbsp;&nbsp;The name of the counter in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: counter
 * </pre>
 *
 * <pre>-initial-values &lt;adams.core.base.BaseString&gt; [-initial-values ...] (property: initialValues)
 * &nbsp;&nbsp;&nbsp;The values to initialize the counter with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-initial-count &lt;int&gt; (property: initialCount)
 * &nbsp;&nbsp;&nbsp;The count to use for the initial values.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CounterInit
    extends AbstractStandalone
    implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 4182914190162129217L;

  /** the name of the counter in the internal storage. */
  protected StorageName m_StorageName;

  /** the initial labels to use. */
  protected BaseString[] m_InitialValues;

  /** the initial counter value to use. */
  protected int m_InitialCount;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Creates an empty counter in internal storage under the specified name.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"storage-name", "storageName",
	new StorageName("counter"));

    m_OptionManager.add(
	"initial-values", "initialValues",
	new BaseString[0]);

    m_OptionManager.add(
	"initial-count", "initialCount",
	0);
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

    result = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "initialValues", (m_InitialValues.length == 0) ? "-none-" : m_InitialValues, ", initial values: ");
    result += QuickInfoHelper.toString(this, "initialCount", m_InitialCount, ", initial count: ");

    return result;
  }

  /**
   * Sets the name for the counter in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the counter in the internal storage.
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
    return "The name of the counter in the internal storage.";
  }

  /**
   * Sets the values to initialize the counter with.
   *
   * @param value	the values
   */
  public void setInitialValues(BaseString[] value) {
    m_InitialValues = value;
    reset();
  }

  /**
   * Returns the values to initialize the counter with.
   *
   * @return		the values
   */
  public BaseString[] getInitialValues() {
    return m_InitialValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initialValuesTipText() {
    return "The values to initialize the counter with.";
  }

  /**
   * Sets the initial count to use for the initial values.
   *
   * @param value	the count
   */
  public void setInitialCount(int value) {
    m_InitialCount = value;
    reset();
  }

  /**
   * Returns the initial count to use for the initial values.
   *
   * @return		the count
   */
  public int getInitialCount() {
    return m_InitialCount;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initialCountTipText() {
    return "The count to use for the initial values.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String 		result;
    NamedCounter	counter;

    result = getOptionManager().ensureVariableForPropertyExists("storageName");

    if (result == null) {
      counter = new NamedCounter();
      for (BaseString initialValue: m_InitialValues)
	counter.set(initialValue.getValue(), m_InitialCount);
      getStorageHandler().getStorage().put(m_StorageName, counter);
    }

    return result;
  }
}
