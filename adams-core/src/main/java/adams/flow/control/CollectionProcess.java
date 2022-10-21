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
 * CollectionProcess.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.base.BaseClassname;
import adams.flow.core.Actor;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

/**
 <!-- globalinfo-start -->
 * Applies all sub-actors to each of the collection elements.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: CollectionProcess
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
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stopping-timeout &lt;int&gt; (property: stoppingTimeout)
 * &nbsp;&nbsp;&nbsp;The timeout in milliseconds when waiting for actors to finish (&lt;= 0 for
 * &nbsp;&nbsp;&nbsp;infinity; see 'finishBeforeStopping').
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;All the actors that define this sequence.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-collection-class &lt;adams.core.base.BaseClassname&gt; (property: collectionClass)
 * &nbsp;&nbsp;&nbsp;The class to use for the collection; if none is specified, the class of
 * &nbsp;&nbsp;&nbsp;the first element is used.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-store-element-index &lt;boolean&gt; (property: storeElementIndex)
 * &nbsp;&nbsp;&nbsp;If enabled, the element index (1-based) of the current element being processed
 * &nbsp;&nbsp;&nbsp;gets stored in the specified variable.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-element-index-variable &lt;adams.core.VariableName&gt; (property: elementIndexVariable)
 * &nbsp;&nbsp;&nbsp;The variable to store the element index in.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CollectionProcess
  extends SubProcess {

  /** for serialization. */
  private static final long serialVersionUID = 5975989766824652946L;

  /** the key for storing the output token in the backup. */
  public final static String BACKUP_OUTPUT = "output";

  /** the class for the collection. */
  protected BaseClassname m_CollectionClass;

  /** whether to store the index of the element currently being processed in a variable. */
  protected boolean m_StoreElementIndex;

  /** the variable to store the element index in. */
  protected VariableName m_ElementIndexVariable;

  /** the output collection. */
  protected Token m_OutputToken;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies all sub-actors to each of the collection elements.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "collection-class", "collectionClass",
      new BaseClassname());

    m_OptionManager.add(
      "store-element-index", "storeElementIndex",
      false);

    m_OptionManager.add(
      "element-index-variable", "elementIndexVariable",
      new VariableName());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "collectionClass", (m_CollectionClass.length() != 0) ? m_CollectionClass : "-from 1st element-", "collection: ");
    
    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();

    if (m_StoreElementIndex)
      result += QuickInfoHelper.toString(this, "elementIndexVariable", m_ElementIndexVariable, ", index var: ");

    return result;
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_OutputToken = null;
  }

  /**
   * Sets the class for the collection.
   *
   * @param value	the classname, use empty string to use class of first
   * 			element
   */
  public void setCollectionClass(BaseClassname value) {
    m_CollectionClass = value;
    reset();
  }

  /**
   * Returns the class for the collection.
   *
   * @return		the classname, empty string if class of first element
   * 			is used
   */
  public BaseClassname getCollectionClass() {
    return m_CollectionClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String collectionClassTipText() {
    return
      "The class to use for the collection; if none is specified, the class of "
	+ "the first element is used.";
  }

  /**
   * Sets whether to store the element index in a variable.
   *
   * @param value	true if the element index should get stored in variable
   */
  public void setStoreElementIndex(boolean value) {
    m_StoreElementIndex = value;
    reset();
  }

  /**
   * Returns whether to store the element index in a variable.
   *
   * @return		true if the element index gets stored in variable
   */
  public boolean getStoreElementIndex() {
    return m_StoreElementIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storeElementIndexTipText() {
    return
      "If enabled, the element index (1-based) of the current element being "
	+ "processed gets stored in the specified variable.";
  }

  /**
   * Sets the variable name to store the current element index in.
   *
   * @param value	the variable name
   */
  public void setElementIndexVariable(VariableName value) {
    m_ElementIndexVariable = value;
    reset();
  }

  /**
   * Returns the variable name to store the current element index in.
   *
   * @return		the variable name
   */
  public VariableName getElementIndexVariable() {
    return m_ElementIndexVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String elementIndexVariableTipText() {
    return "The variable to store the element index in.";
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
    if (state.containsKey(BACKUP_OUTPUT)) {
      m_OutputToken = (Token) state.get(BACKUP_OUTPUT);
      state.remove(BACKUP_OUTPUT);
    }

    super.restoreState(state);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Collection.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    Class[]	result;
    Class[]	sub;
    int		i;

    if (m_CollectionClass.length() > 0) {
      try {
	result = new Class[]{m_CollectionClass.classValue()};
      }
      catch (Exception e) {
	// ignored
	result = new Class[0];
      }
    }
    else if (active() > 0) {
      result = new Class[]{Collection.class};
    }
    else {
      result = new Class[]{Unknown.class};
    }

    return result;
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  @Override
  public void input(Token token) {
    m_CurrentToken = token;
    m_OutputToken  = null;
  }

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Collection		collIn;
    Iterator		iter;
    Actor 		first;
    int			i;
    Token		input;
    Collection		collOut;

    result = null;

    first = firstActive();
    if (isLoggingEnabled())
      getLogger().info("first active actor: " + first.getFullName());

    collIn = new ArrayList();
    if ((first != null) && (first instanceof InputConsumer)) {
      collIn = m_CurrentToken.getPayload(Collection.class);
      iter   = collIn.iterator();
      i      = 0;
      while (iter.hasNext()) {
        if (m_StoreElementIndex) {
	  getVariables().set(m_ElementIndexVariable.getValue(), "" + (i + 1));
	  if (isLoggingEnabled())
	    getLogger().fine("element index variable '" + m_ElementIndexVariable + "' set to: " + (i+1));
	}
	input = new Token(iter.next());
	((InputConsumer) first).input(input);
	if (isLoggingEnabled())
	  getLogger().fine("input token #" + (i+1) + ": " + input);

	try {
	  result = m_Director.execute();
	}
	catch (Exception e) {
	  result = handleException("Failed to execute director", e);
	}

	if (result != null)
	  break;

	i++;
      }
    }

    if (!isStopped() && (result == null) && (getOutputTokens().size() > 0)) {
      try {
	if (m_CollectionClass.length() == 0)
	  collOut = collIn.getClass().getDeclaredConstructor().newInstance();
	else
	  collOut = (Collection) m_CollectionClass.classValue().getDeclaredConstructor().newInstance();
	for (i = 0; i < getOutputTokens().size(); i++)
	  collOut.add(getOutputTokens().get(i).getPayload());
	m_OutputToken = new Token(collOut);
      }
      catch (Exception e) {
	result = handleException("Failed to generate output collection: ", e);
	m_OutputToken = null;
      }
    }

    if (!isStopped())
      getOutputTokens().clear();

    return result;
  }

  /**
   * Post-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#m_Executed
   */
  @Override
  protected String postExecute() {
    String	result;

    result = super.postExecute();

    if (isStopped())
      m_OutputToken = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_OutputToken != null);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result        = m_OutputToken;
    m_OutputToken = null;

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_OutputToken = null;

    super.wrapUp();
  }
}
