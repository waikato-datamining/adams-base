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
 * MakeContainer.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.flow.container.AbstractContainer;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

import java.util.Iterator;

/**
 <!-- globalinfo-start -->
 * Assembles a container with data obtained from either callable actors or storage items.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.SequencePlotterContainer: PlotName, X, Y, Content type, Error X, Error Y, MetaData
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
 * &nbsp;&nbsp;&nbsp;default: MakeContainer
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
 * <pre>-callable-actor &lt;adams.flow.core.CallableActorReference&gt; [-callable-actor ...] (property: callableActors)
 * &nbsp;&nbsp;&nbsp;The callable actors to obtain the data from.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; [-storage-name ...] (property: storageNames)
 * &nbsp;&nbsp;&nbsp;The storage items to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-use-storage &lt;boolean&gt; (property: useStorage)
 * &nbsp;&nbsp;&nbsp;Whether to use storage items or data from callable actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-value-name &lt;adams.core.base.BaseString&gt; [-value-name ...] (property: valueNames)
 * &nbsp;&nbsp;&nbsp;The names to use for storing the values in the container:PlotName, X, Y, 
 * &nbsp;&nbsp;&nbsp;Content type, Error X, Error Y, MetaData.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-container-class &lt;adams.flow.container.AbstractContainer&gt; (property: containerClass)
 * &nbsp;&nbsp;&nbsp;The container class to generate (full class name).
 * &nbsp;&nbsp;&nbsp;default: adams.flow.container.SequencePlotterContainer
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MakeContainer
  extends AbstractSimpleSource
  implements ProvenanceSupporter, StorageUser {

  /** for serialization. */
  private static final long serialVersionUID = -132045002653940359L;

  /** the callable actors to retrieve the data from. */
  protected CallableActorReference[] m_CallableActors;

  /** the storage names to retrieve the data from. */
  protected StorageName[] m_StorageNames;

  /** the names under which to store the data. */
  protected BaseString[] m_ValueNames;

  /** whether to use callable actors or storage. */
  protected boolean m_UseStorage;

  /** the container class to create. */
  protected AbstractContainer m_ContainerClass;

  /** the names of the items for the current container. */
  protected String m_ContainerValues;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Assembles a container with data obtained from either callable actors " 
        + "or storage items.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "callable-actor", "callableActors",
      new CallableActorReference[0]);

    m_OptionManager.add(
      "storage-name", "storageNames",
      new StorageName[0]);

    m_OptionManager.add(
      "use-storage", "useStorage",
      false);

    m_OptionManager.add(
      "value-name", "valueNames",
      new BaseString[0]);

    m_OptionManager.add(
      "container-class", "containerClass",
      new SequencePlotterContainer());
  }

  /**
   * Resets the members.
   */
  @Override
  protected void reset() {
    super.reset();

    m_OutputToken     = null;
    m_ContainerValues = null;
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "containerClass", m_ContainerClass.getClass());
  }

  /**
   * Sets the names of the callable actors to obtain the data from.
   *
   * @param value	the names
   */
  public void setCallableActors(CallableActorReference[] value) {
    m_CallableActors = value;
    m_ValueNames   = (BaseString[]) Utils.adjustArray(m_ValueNames, m_CallableActors.length, new BaseString("unknown"));
    reset();
  }

  /**
   * Returns the names of the callable actors to get the data form.
   *
   * @return 		the names
   */
  public CallableActorReference[] getCallableActors() {
    return m_CallableActors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String callableActorsTipText() {
    return "The callable actors to obtain the data from.";
  }

  /**
   * Sets the names of the storage items to use.
   *
   * @param value	the names
   */
  public void setStorageNames(StorageName[] value) {
    m_StorageNames = value;
    m_ValueNames   = (BaseString[]) Utils.adjustArray(m_ValueNames, m_StorageNames.length, new BaseString("unknown"));
    reset();
  }

  /**
   * Returns the names of the storage items to use.
   *
   * @return 		the names
   */
  public StorageName[] getStorageNames() {
    return m_StorageNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String storageNamesTipText() {
    return "The storage items to use.";
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
   * Sets the names of the values in the container.
   *
   * @param value	the names
   */
  public void setValueNames(BaseString[] value) {
    m_ValueNames     = value;
    m_CallableActors = (CallableActorReference[]) Utils.adjustArray(m_CallableActors, m_ValueNames.length, new CallableActorReference("unknown"));
    m_StorageNames   = (StorageName[]) Utils.adjustArray(m_StorageNames, m_ValueNames.length, new StorageName());
    reset();
  }

  /**
   * Returns the names to store the values under in the container.
   *
   * @return 		the names
   */
  public BaseString[] getValueNames() {
    return m_ValueNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String valueNamesTipText() {
    String	values;

    values = getContainerValues();
    if (values != null)
      values = values.replace(",", ", ");

    return
      "The names to use for storing the values in the container"
        + ((values == null) ? "" : ":" + values) + ".";
  }

  /**
   * Sets the class name of the container to create.
   *
   * @param value	the class name
   */
  public void setContainerClass(AbstractContainer value) {
    m_ContainerClass = value;
    reset();
  }

  /**
   * Returns the class name of the container to create.
   *
   * @return 		the class name
   */
  public AbstractContainer getContainerClass() {
    return m_ContainerClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String containerClassTipText() {
    return "The container class to generate (full class name).";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.SequencePlotterContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    AbstractContainer	cont;

    cont = m_ContainerClass.getClone();
    if (cont != null)
      return new Class[]{cont.getClass()};
    else
      return new Class[]{Object.class};
  }

  /**
   * Returns a comma-separated list of names that the container accepts.
   *
   * @return		the list of names, null if no container class name
   * 			or invalid classname specified
   */
  protected synchronized String getContainerValues() {
    Iterator<String>	names;

    if (m_ContainerValues == null) {
      m_ContainerValues = "";
      names             = m_ContainerClass.names();
      while (names.hasNext()) {
        if (m_ContainerValues.length() > 0)
          m_ContainerValues += ",";
        m_ContainerValues += names.next();
      }
    }

    return m_ContainerValues;
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		value;
    AbstractContainer	cont;
    int			i;

    result = null;

    cont = getContainerClass().getClone();
    if (m_UseStorage) {
      for (i = 0; i < m_StorageNames.length; i++) {
        value = getValue(m_StorageNames[i]);
        cont.setValue(m_ValueNames[i].getValue(), value);
      }
    }
    else {
      for (i = 0; i < m_CallableActors.length; i++) {
        value = getValue(m_CallableActors[i]);
        cont.setValue(m_ValueNames[i].getValue(), value);
      }
    }
    if (cont.isValid())
      m_OutputToken = new Token(cont);
    else
      result = "Container (" + cont.getClass().getName() + ") not valid!";

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, m_OutputToken.getPayload().getClass()));
  }
}
