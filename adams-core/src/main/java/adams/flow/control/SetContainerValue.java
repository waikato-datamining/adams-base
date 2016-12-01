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
 * SetContainerValue.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.control;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.flow.container.AbstractContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.ControlActor;
import adams.flow.core.Token;
import adams.flow.transformer.AbstractTransformer;

/**
 <!-- globalinfo-start -->
 * Updates a single item in the container passing through, using either the data obtained from a callable actor or a storage item.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.AbstractContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.AbstractContainer: 
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
 * &nbsp;&nbsp;&nbsp;default: UpdateContainer
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
 * <pre>-callable-actor &lt;adams.flow.core.CallableActorReference&gt; (property: callableActor)
 * &nbsp;&nbsp;&nbsp;The callable actor to obtain the data from.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The storage item to use.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 * <pre>-use-storage &lt;boolean&gt; (property: useStorage)
 * &nbsp;&nbsp;&nbsp;Whether to use storage items or data from callable actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-value-name &lt;adams.core.base.BaseString&gt; (property: valueName)
 * &nbsp;&nbsp;&nbsp;The name to use for storing the value in the container.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetContainerValue
  extends AbstractTransformer
  implements ControlActor, StorageUser {

  private static final long serialVersionUID = 9035289662211034032L;

  /** the callable actor to retrieve the data from. */
  protected CallableActorReference m_CallableActor;

  /** the storage name to retrieve the data from. */
  protected StorageName m_StorageName;

  /** the name under which to store the data. */
  protected BaseString m_ValueName;

  /** whether to use callable actors or storage. */
  protected boolean m_UseStorage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Updates a single item in the container passing through, using either "
	+ "the data obtained from a callable actor or a storage item.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "callable-actor", "callableActor",
      new CallableActorReference());

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());

    m_OptionManager.add(
      "use-storage", "useStorage",
      false);

    m_OptionManager.add(
      "value-name", "valueName",
      new BaseString());
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    return !getSkip() && m_UseStorage;
  }

  /**
   * Sets the name of the callable actors to obtain the data from.
   *
   * @param value	the name
   */
  public void setCallableActor(CallableActorReference value) {
    m_CallableActor = value;
    reset();
  }

  /**
   * Returns the name of the callable actors to get the data form.
   *
   * @return 		the name
   */
  public CallableActorReference getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String callableActorTipText() {
    return "The callable actor to obtain the data from.";
  }

  /**
   * Sets the name of the storage item to use.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the storage item to use.
   *
   * @return 		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The storage item to use.";
  }

  /**
   * Sets whether to use storage items or data from callable actors.
   *
   * @param value	true if to use storage
   */
  public void setUseStorage(boolean value) {
    m_UseStorage = value;
    reset();
  }

  /**
   * Returns whether to use storage items or data from callable actors.
   *
   * @return 		true if to use storage
   */
  public boolean getUseStorage() {
    return m_UseStorage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String useStorageTipText() {
    return "Whether to use storage items or data from callable actors.";
  }

  /**
   * Sets the name of the value in the container.
   *
   * @param value	the name
   */
  public void setValueName(BaseString value) {
    m_ValueName = value;
    reset();
  }

  /**
   * Returns the name to store the value under in the container.
   *
   * @return 		the name
   */
  public BaseString getValueName() {
    return m_ValueName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String valueNameTipText() {
    return "The name to use for storing the value in the container.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "valueName", m_ValueName, "value: ");
  }

  /**
   * Returns the value obtained from the callable actor.
   *
   * @param name	the name of the callable actor to get the value from
   * @return		the obtained value
   */
  protected Object getValue(CallableActorReference name) {
    Object		result;
    MessageCollection errors;

    errors = new MessageCollection();

    result = CallableActorHelper.getSetupFromSource(null, name, this, errors);
    if (result == null) {
      if (!errors.isEmpty())
	getLogger().severe(errors.toString());
    }

    return result;
  }

  /**
   * Returns the value obtained from the callable actor.
   *
   * @param name	the name of the callable actor to get the value from
   * @return		the obtained value
   */
  protected Object getValue(StorageName name) {
    Object		result;

    result = getStorageHandler().getStorage().get(name);
    if (result == null)
      getLogger().severe("Failed to retrieve storage item for name: " + name);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Object.class};  // to avoid requiring Cast transformer
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    AbstractContainer	cont;
    Object		value;

    result = null;

    cont = (AbstractContainer) m_InputToken.getPayload();
    if (m_UseStorage) {
      value = getValue(m_StorageName);
      cont.setValue(m_ValueName.getValue(), value);
    }
    else {
      value = getValue(m_CallableActor);
      cont.setValue(m_ValueName.getValue(), value);
    }
    if (cont.isValid())
      m_OutputToken = new Token(cont);
    else
      result = "Container (" + cont.getClass().getName() + ") not valid!";

    return result;
  }
}
