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
 * AbstractContainerUpdater.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.flow.container.AbstractContainer;
import adams.flow.core.AbstractActor;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.Hashtable;

/**
 * Ancestor for control actors that update a specific value of a container
 * using the defined sub-actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractContainerUpdater
  extends SubProcess {

  /** for serialization. */
  private static final long serialVersionUID = 7140175689043000123L;

  /** the key for storing the output token in the backup. */
  public final static String BACKUP_OUTPUT = "output";

  /** the value to modify. */
  protected String m_ContainerValueName;

  /** the output. */
  protected Token m_OutputToken;

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_OutputToken = null;
  }

  /**
   * Sets the container value to update.
   *
   * @param value	the name of the value
   */
  protected void setContainerValueName(String value) {
    m_ContainerValueName = value;
    reset();
  }

  /**
   * Returns the container value to update.
   *
   * @return		the name of the value
   */
  protected String getContainerValueName() {
    return m_ContainerValueName;
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
    return new Class[]{AbstractContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
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
   * Tries to obtain the container value.
   *
   * @param cont      the container to obtain the value from
   * @return          the value, if available
   * @throws java.lang.IllegalStateException  if failed to obtain value
   */
  protected Object getContainerValue(AbstractContainer cont) {
    if (cont.hasValue(m_ContainerValueName))
      return cont.getValue(m_ContainerValueName);
    else
      throw new IllegalStateException("Container value not present: " + m_ContainerValueName);
  }

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    AbstractActor	first;
    Object		input;
    Object		output;
    AbstractContainer	cont;
    AbstractContainer	contNew;
    boolean		processed;

    result = null;

    first = firstActive();
    if (isLoggingEnabled())
      getLogger().info("first active actor: " + first.getFullName());

    cont      = null;
    processed = false;
    if ((first != null) && (first instanceof InputConsumer)) {
      cont  = (AbstractContainer) m_CurrentToken.getPayload();
      input = getContainerValue(cont);

      ((InputConsumer) first).input(new Token(input));
      if (isLoggingEnabled())
	getLogger().fine("input: " + input);

      try {
	result    = m_Director.execute();
	processed = true;
      }
      catch (Exception e) {
	result = handleException("Failed to execute director", e);
      }
    }

    if (processed) {
      if (m_OutputTokens.size() == 1) {
	try {
	  output  = m_OutputTokens.get(0).getPayload();
	  contNew = cont.getClone();
	  contNew.setValue(m_ContainerValueName, output);
	  m_OutputToken = new Token(contNew);
	}
	catch (Exception e) {
	  result = handleException("Failed to generate new container: ", e);
	  m_OutputToken = null;
	}
      }
      else {
	result = "Last active sub-actor did not produce exactly one output: " + m_OutputTokens.size();
      }
    }

    m_OutputTokens.clear();

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
