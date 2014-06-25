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
 * TryCatch.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import java.util.Hashtable;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.FixedNameActorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.InternalActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Safe-guards the execution of the 'try' sequence of actors. In case of an error, the 'catch' sequence is executed to generate output instead.<br/>
 * This works similar to the Java try-catch-block. Allowing the flow to recover from unexpected errors and, for instance, return default values.<br/>
 * If the 'try' block fails and the 'catch' block accepts input (doesn't have to be a transformer, it can be just a source, eg SequenceSource), then the same input token is presented to the 'catch' block. This allows you to react to errors better. E.g., if the input token is a filename, then you can create an error message made up of the recorded error and the filename and pass this on.<br/>
 * Note for developers: If actors use other actors internally, these need to be accessible. This can be achieved by simply  implementing the adams.flow.core.InternalActorHandler interface.
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
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TryCatch
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-try &lt;adams.flow.core.AbstractActor&gt; (property: try)
 * &nbsp;&nbsp;&nbsp;The 'try' branch which is attempted to be executed.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.SubProcess -name try
 * </pre>
 * 
 * <pre>-catch &lt;adams.flow.core.AbstractActor&gt; (property: catch)
 * &nbsp;&nbsp;&nbsp;The 'catch' branch which gets executed if the 'try' branch fails.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.control.SubProcess -name catch
 * </pre>
 * 
 * <pre>-store-error &lt;boolean&gt; (property: storeError)
 * &nbsp;&nbsp;&nbsp;If enabled, then any error gets stored in the specified variable 'errorVariable'
 * &nbsp;&nbsp;&nbsp;; does not modify the variable if there was no error.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-error-variable &lt;adams.core.VariableName&gt; (property: errorVariable)
 * &nbsp;&nbsp;&nbsp;The name of the variable to store the error messages in.
 * &nbsp;&nbsp;&nbsp;default: trycatch
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TryCatch
  extends AbstractControlActor
  implements InputConsumer, OutputProducer, FixedNameActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = -9029393233616734995L;

  /** the try branch. */
  protected AbstractActor m_Try;

  /** the catch branch. */
  protected AbstractActor m_Catch;

  /** the key for storing the input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the name for the try branch. */
  public final static String NAME_TRY = "try";

  /** the name for the catcj branch. */
  public final static String NAME_CATCH = "catch";

  /** the current input token. */
  protected transient Token m_InputToken;

  /** error message in try block. */
  protected String m_ErrorOccurred;

  /** whether to store any error message in a variable. */
  protected boolean m_StoreError;

  /** the variable to store the error in. */
  protected VariableName m_ErrorVariable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Safe-guards the execution of the 'try' sequence of actors. "
	+ "In case of an error, the 'catch' sequence is executed to generate "
	+ "output instead.\n"
	+ "This works similar to the Java try-catch-block. Allowing the flow "
	+ "to recover from unexpected errors and, for instance, return default "
	+ "values.\n"
	+ "If the 'try' block fails and the 'catch' block accepts input (doesn't "
	+ "have to be a transformer, it can be just a source, eg SequenceSource), "
	+ "then the same input token is presented to the 'catch' block. This allows "
	+ "you to react to errors better. E.g., if the input token is a filename, "
	+ "then you can create an error message made up of the recorded error "
	+ "and the filename and pass this on.\n"
	+ "Note for developers: If actors use other actors internally, these need to be accessible. "
	+ "This can be achieved by simply  implementing the " 
	+ InternalActorHandler.class.getName() + " interface.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Try   = getDefaultTry();
    m_Catch = getDefaultCatch();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_InputToken = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "try", "try",
	    getDefaultTry());

    m_OptionManager.add(
	    "catch", "catch",
	    getDefaultCatch());

    m_OptionManager.add(
	    "store-error", "storeError",
	    false);

    m_OptionManager.add(
	    "error-variable", "errorVariable",
	    new VariableName("trycatch"));
  }

  /**
   * Returns the default try branch.
   *
   * @return		the default branch
   */
  protected AbstractActor getDefaultTry() {
    Sequence	result;

    result = new SubProcess();
    result.setName(NAME_TRY);

    return result;
  }

  /**
   * Sets the try branch.
   *
   * @param value 	the try branch
   */
  public void setTry(AbstractActor value) {
    if (ActorUtils.isTransformer(value)) {
      m_Try = value;
      m_Try.setName(NAME_TRY);
      updateParent();
      reset();
    }
    else {
      getLogger().severe("'" + NAME_TRY + "' actor(s) must be a transformer, " + value.getClass().getName() + " is not!");
    }
  }

  /**
   * Returns the try branch.
   *
   * @return 		the try branch
   */
  public AbstractActor getTry() {
    return m_Try;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tryTipText() {
    return "The '" + NAME_TRY + "' branch which is attempted to be executed.";
  }

  /**
   * Returns the default catch branch.
   *
   * @return		the default branch
   */
  protected AbstractActor getDefaultCatch() {
    Sequence	result;

    result = new SubProcess();
    result.setName(NAME_CATCH);

    return result;
  }

  /**
   * Sets the catch branch.
   *
   * @param value 	the catch branch
   */
  public void setCatch(AbstractActor value) {
    if (ActorUtils.isSource(value) || ActorUtils.isTransformer(value)) {
      m_Catch = value;
      m_Catch.setName(NAME_CATCH);
      updateParent();
      reset();
    }
    else {
      getLogger().severe("'" + NAME_CATCH + "' actor(s) must be a source or transformer, " + value.getClass().getName() + " is not!");
    }
  }

  /**
   * Returns the try branch.
   *
   * @return 		the try branch
   */
  public AbstractActor getCatch() {
    return m_Catch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String catchTipText() {
    return
	"The '" + NAME_CATCH + "' branch which gets executed if the '"
	+ NAME_TRY + "' branch fails.";
  }

  /**
   * Sets whether to store any error in a variable.
   *
   * @param value 	true if to store error in variable
   */
  public void setStoreError(boolean value) {
    m_StoreError = value;
    reset();
  }

  /**
   * Returns whether to store any error in a variable.
   *
   * @return 		true if error gets stored in variable
   */
  public boolean getStoreError() {
    return m_StoreError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storeErrorTipText() {
    return
	"If enabled, then any error gets stored in the specified variable "
	+ "'errorVariable'; does not modify the variable if there was no error.";
  }

  /**
   * Sets the variable to store the error messages in.
   *
   * @param value 	the name
   */
  public void setErrorVariable(VariableName value) {
    m_ErrorVariable = value;
    reset();
  }

  /**
   * Returns the variable to store the error messages in.
   *
   * @return 		the name
   */
  public VariableName getErrorVariable() {
    return m_ErrorVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String errorVariableTipText() {
    return "The name of the variable to store the error messages in.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (QuickInfoHelper.hasVariable(this, "storeError") || m_StoreError)
      return QuickInfoHelper.toString(this, "errorVariable", m_ErrorVariable.paddedValue(), "error -> ");
    else
      return null;
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
      state.remove(BACKUP_INPUT);
    }

    super.restoreState(state);
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   * @see		#m_InputToken
   */
  @Override
  public void input(Token token) {
    m_InputToken = token;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(false, ActorExecution.UNDEFINED, true);
  }

  /**
   * Returns the size of the group.
   *
   * @return		the size
   */
  @Override
  public int size() {
    return 2;
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
      return m_Try;
    else if (index == 1)
      return m_Catch;
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
      setTry(actor);
    else if (index == 1)
      setCatch(actor);
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
    if (m_Try.getName().equals(actor))
      return 0;
    else if (m_Catch.getName().equals(actor))
      return 1;
    else
      return -1;
  }

  /**
   * Returns the name for the sub-actor at this position.
   *
   * @param index	the position of the sub-actor
   * @return		the name to use
   */
  @Override
  public String getFixedName(int index) {
    if (index == 0)
      return NAME_TRY;
    else if (index == 1)
      return NAME_CATCH;
    else
      throw new IllegalArgumentException("Invalid index: " + index);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return ((InputConsumer) m_Try).accepts();
  }

  /**
   * Handles the given error message with the flow that this actor belongs to,
   * if the flow has error logging turned on. Might stop the flow as well.
   *
   * @param source	the actor this error originated from
   * @param type	the type of error
   * @param msg		the error message to log
   * @return		always null
   */
  @Override
  public String handleError(AbstractActor source, String type, String msg) {
    m_ErrorOccurred = source.getFullName() + "/" + type + ": " + msg;
    return null;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null)
      ActorUtils.updateErrorHandler(this, this);

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
    String	msg;

    result          = null;
    m_ErrorOccurred = null;
    msg             = "Failed to execute '" + NAME_TRY + "' branch: ";

    try {
      // input
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preInput(m_Try, m_InputToken);
      ((InputConsumer) m_Try).input(m_InputToken);
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postInput(m_Try);
      // execute
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(m_Try);
      result = m_Try.execute();
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(m_Try);
      // error?
      if (result != null)
	m_ErrorOccurred = msg + result;
    }
    catch (Exception e) {
      m_ErrorOccurred = handleException(msg, e);
    }

    if (m_ErrorOccurred != null) {
      if (m_StoreError)
	getVariables().set(m_ErrorVariable.getValue(), m_ErrorOccurred);

      // input
      if (ActorUtils.isTransformer(m_Catch)) {
	if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  getFlowExecutionListeningSupporter().getFlowExecutionListener().preInput(m_Catch, m_InputToken);
	((InputConsumer) m_Catch).input(m_InputToken);
	if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	  getFlowExecutionListeningSupporter().getFlowExecutionListener().postInput(m_Catch);
      }
      // execute
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(m_Catch);
      result = m_Catch.execute();
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(m_Catch);
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return ((OutputProducer) m_Try).generates();  // TODO combine with catch?
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   * <p/>
   * The method is not allowed allowed to return "true" before the
   * actor has been executed. For actors that return an infinite
   * number of tokens, the m_Executed flag can be returned.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    if (m_ErrorOccurred != null)
      return ((OutputProducer) m_Catch).hasPendingOutput();
    else
      return ((OutputProducer) m_Try).hasPendingOutput();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    if (m_ErrorOccurred != null) {
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preOutput(m_Catch);
      result = ((OutputProducer) m_Catch).output();
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postOutput(m_Catch, result);
    }
    else {
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().preOutput(m_Try);
      result = ((OutputProducer) m_Try).output();
      if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
	getFlowExecutionListeningSupporter().getFlowExecutionListener().postOutput(m_Try, result);
    }

    return result;
  }
}
