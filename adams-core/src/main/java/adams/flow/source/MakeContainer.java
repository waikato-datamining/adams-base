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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.util.Iterator;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.flow.container.AbstractContainer;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Assembles a container with data obtained from callable actors.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.SequencePlotterContainer: PlotName, X, Y, Content type, Error X, Error Y
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
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-global-actor &lt;adams.flow.core.CallableActorReference&gt; [-global-actor ...] (property: callableActors)
 * &nbsp;&nbsp;&nbsp;The callable actors to obtain the data from.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-value-name &lt;adams.core.base.BaseString&gt; [-value-name ...] (property: valueNames)
 * &nbsp;&nbsp;&nbsp;The names to use for storing the values in the container:PlotName, X, Y, 
 * &nbsp;&nbsp;&nbsp;Content type, Error X, Error Y.
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
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -132045002653940359L;

  /** the callable actors to retrieve the data from. */
  protected CallableActorReference[] m_CallableActors;

  /** the names under which to store the data. */
  protected BaseString[] m_ValueNames;

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
        "Assembles a container with data obtained from callable actors.";
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
   * Sets the names of the values in the container.
   *
   * @param value	the names
   */
  public void setValueNames(BaseString[] value) {
    m_ValueNames   = value;
    m_CallableActors = (CallableActorReference[]) Utils.adjustArray(m_CallableActors, m_ValueNames.length, new CallableActorReference("unknown"));
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
    return CallableActorHelper.getSetupFromSource(null, name, this);
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
    for (i = 0; i < m_CallableActors.length; i++) {
      value = getValue(m_CallableActors[i]);
      cont.setValue(m_ValueNames[i].getValue(), value);
    }
    if (cont.isValid())
      m_OutputToken = new Token(cont);

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
