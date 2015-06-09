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
 * UpdateProperties.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.PropertyHelper;
import adams.flow.core.Token;
import adams.flow.transformer.PassThrough;
import adams.gui.goe.PropertyPath;
import adams.gui.goe.PropertyPath.PropertyContainer;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Updates the properties of the sub-actor using the values associated with the specfiied variables. The input&#47;output of the actor are the same as the ones of the sub-actor, since this control actor merely functions as wrapper.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: SetProperties
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
 * <pre>-property &lt;adams.core.base.BaseString&gt; [-property ...] (property: properties)
 * &nbsp;&nbsp;&nbsp;The properties to update with the values associated with the specified values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-variable &lt;adams.core.VariableName&gt; [-variable ...] (property: variableNames)
 * &nbsp;&nbsp;&nbsp;The names of the variables to update the properties with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-sub-actor &lt;adams.flow.core.AbstractActor&gt; (property: subActor)
 * &nbsp;&nbsp;&nbsp;The transformer to update the properties for.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.PassThrough
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UpdateProperties
  extends AbstractControlActor
  implements InputConsumer, OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = -2286932196386647785L;

  /** the key for storing the input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the key for storing the output token in the backup. */
  public final static String BACKUP_OUTPUT = "output";

  /** the current input token. */
  protected transient Token m_InputToken;

  /** the current output token. */
  protected transient Token m_OutputToken;

  /** the property paths. */
  protected BaseString[] m_Properties;

  /** the variables to update the properties with. */
  protected VariableName[] m_VariableNames;

  /** the actor to update the properties foe. */
  protected AbstractActor m_SubActor;

  /** the property containers of the properties to update. */
  protected transient PropertyContainer[] m_Containers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Updates the properties of the sub-actor using the values associated with "
      + "the specfiied variables. The input/output of the actor are the same "
      + "as the ones of the sub-actor, since this control actor merely functions "
      + "as wrapper.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "property", "properties",
	    new BaseString[0]);

    m_OptionManager.add(
	    "variable", "variableNames",
	    new VariableName[0]);

    m_OptionManager.add(
	    "sub-actor", "subActor",
	    new PassThrough());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_InputToken  = null;
    m_OutputToken = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "properties", (m_Properties.length == 1 ? m_Properties[0] : m_Properties.length), "props: ");
    result += QuickInfoHelper.toString(this, "variableNames", (m_VariableNames.length == 1 ? m_VariableNames[0] : m_VariableNames.length), ", vars: ");

    return result;
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_InputToken != null)
      result.put(BACKUP_INPUT, m_InputToken);
    if (m_OutputToken != null)
      result.put(BACKUP_OUTPUT, m_OutputToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INPUT)) {
      m_InputToken = (Token) state.get(BACKUP_INPUT);
      ((InputConsumer) m_SubActor).input(m_InputToken);
      state.remove(BACKUP_INPUT);
    }

    if (state.containsKey(BACKUP_OUTPUT)) {
      m_OutputToken = (Token) state.get(BACKUP_OUTPUT);
      state.remove(BACKUP_OUTPUT);
    }

    super.restoreState(state);
  }

  /**
   * Sets the properties to update.
   *
   * @param value	the properties
   */
  public void setProperties(BaseString[] value) {
    m_Properties    = value;
    m_VariableNames = (VariableName[]) Utils.adjustArray(m_VariableNames, m_Properties.length, new VariableName("unknown"));
    reset();
  }

  /**
   * Returns the properties to update.
   *
   * @return		the properties
   */
  public BaseString[] getProperties() {
    return m_Properties;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propertiesTipText() {
    return "The properties to update with the values associated with the specified values.";
  }

  /**
   * Sets the variables to use.
   *
   * @param value	the variables
   */
  public void setVariableNames(VariableName[] value) {
    m_VariableNames = value;
    m_Properties    = (BaseString[]) Utils.adjustArray(m_Properties, m_VariableNames.length, new BaseString("subActor.property"));
    reset();
  }

  /**
   * Returns the variables to use.
   *
   * @return		the variables
   */
  public VariableName[] getVariableNames() {
    return m_VariableNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNamesTipText() {
    return "The names of the variables to update the properties with.";
  }

  /**
   * Checks the sub-actor before it is set via the setSubActor method.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkSubActor(AbstractActor actor) {
    if (!ActorUtils.isTransformer(actor))
      return "Not a transformer: " + actor;

    return null;
  }

  /**
   * Sets the sub actor.
   *
   * @param value	the actor
   */
  public void setSubActor(AbstractActor value) {
    String	msg;

    msg = checkSubActor(value);
    if (msg == null) {
      m_SubActor = value;
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Returns the sub-actor.
   *
   * @return		the actor
   */
  public AbstractActor getSubActor() {
    return m_SubActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String subActorTipText() {
    return "The transformer to update the properties for.";
  }

  /**
   * Returns the size of the group.
   *
   * @return		always 1
   */
  @Override
  public int size() {
    return 1;
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public AbstractActor get(int index) {
    if (index == 0)
      return m_SubActor;
    else
      throw new IllegalArgumentException("Illegal index: " + index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, AbstractActor actor) {
    if (index == 0)
      setSubActor(actor);
    else
      throw new IllegalArgumentException("Illegal index: " + index);
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    if (m_SubActor.getName().equals(actor))
      return 0;
    else
      return -1;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(false, ActorExecution.SEQUENTIAL, true);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return ((InputConsumer) m_SubActor).accepts();
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   * @see		#m_InputToken
   */
  public void input(Token token) {
    m_InputToken  = token;
    m_OutputToken = null;
    if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
      getFlowExecutionListeningSupporter().getFlowExecutionListener().preInput(m_SubActor, m_InputToken);
    ((InputConsumer) m_SubActor).input(m_InputToken);
    if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
      getFlowExecutionListeningSupporter().getFlowExecutionListener().postInput(m_SubActor);
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_InputToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_InputToken;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return ((OutputProducer) m_SubActor).generates();
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    Class	cls;
    int		i;

    result = super.setUp();

    if (result == null) {
      m_Containers = new PropertyContainer[m_Properties.length];
      for (i = 0; i < m_Properties.length; i++) {
	m_Containers[i] = PropertyPath.find(m_SubActor, m_Properties[i].getValue());
	if (m_Containers[i] == null) {
	  result = "Cannot find property '" + m_Properties[i] + "' in sub actor!";
	}
	else {
	  cls = m_Containers[i].getReadMethod().getReturnType();
	  if (cls.isArray())
	    result = "Property '" + m_Properties[i] + "' is an array!";
	}
	if (result != null)
	  break;
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
    int			i;
    Object		value;

    result = null;

    for (i = 0; i < m_Properties.length; i++) {
      try {
	value = PropertyHelper.convertValue(
          m_Containers[i],
          getVariables().get(m_VariableNames[i].getValue()));
	if (isLoggingEnabled())
	  getLogger().info("Updating #" + (i+1) + ": var=" + m_VariableNames[i] + ", value=" + getVariables().get(m_VariableNames[i].getValue()) + ", class=" + (value == null ? "null" : value.getClass().getName()));
	if (!PropertyPath.setValue(
	    m_SubActor,
	    m_Properties[i].stringValue(),
	    value)) {
	  throw new IllegalStateException(
	      "Property #" + (i+1) + " could not be updated: " + m_Properties[i].stringValue());
	}
      }
      catch (Exception e) {
	if (result == null)
	  result = "";
	else
	  result += "\n";
	result += handleException("Failed to set property '" + m_Properties[i] + "': ", e);
      }
    }

    if (result == null) {
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(m_SubActor);
      result = m_SubActor.execute();
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(m_SubActor);
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String execute() {
    String	result;

    result = super.execute();
    
    if (m_Skip)
      m_OutputToken = m_InputToken;
    
    return result;
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    if (m_SubActor instanceof ActorHandler)
      ((ActorHandler) m_SubActor).flushExecution();
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return ((OutputProducer) m_SubActor).hasPendingOutput() || (m_OutputToken != null);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;
    
    m_InputToken = null;

    if (m_OutputToken != null) {
      result = m_OutputToken;
    }
    else {
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preOutput(m_SubActor);
      result = ((OutputProducer) m_SubActor).output();
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postOutput(m_SubActor, result);
    }
    
    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_InputToken  = null;
    m_OutputToken = null;

    super.wrapUp();
  }
}
