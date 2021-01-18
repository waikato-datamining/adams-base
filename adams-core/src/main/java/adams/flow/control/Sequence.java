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
 * Sequence.java
 * Copyright (C) 2009-2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.InputConsumer;
import adams.flow.core.OptionalStopRestrictor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Encapsulates a sequence of flow items.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: Sequence
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseString&gt; [-annotation ...] (property: annotations)
 *         The annotations to attach to this actor.
 * </pre>
 *
 * <pre>-skip (property: skip)
 *         If set to true, transformation is skipped and the input token is just forwarded
 *          as it is.
 * </pre>
 *
 * <pre>-progress (property: showProgress)
 *         If set to true, progress information will be output to stdout ('.').
 * </pre>
 *
 * <pre>-actor &lt;adams.flow.core.Actor [options]&gt; [-actor ...] (property: actors)
 *         All the actors that define this sequence.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Sequence
  extends MutableConnectedControlActor
  implements InputConsumer, OptionalStopRestrictor {

  /** for serialization. */
  private static final long serialVersionUID = -9211041097478667239L;

  /** the key for storing the current token in the backup. */
  public final static String BACKUP_CURRENT = "current";

  /** the token that gets passed on to all sub-branches. */
  protected transient Token m_CurrentToken;

  /** whether to allow standalones or not. */
  protected boolean m_AllowStandalones;

  /** whether to allow a source or not. */
  protected boolean m_AllowSource;

  /** whether stops get restricted or not. */
  protected boolean m_RestrictingStops;

  /** whether a restricted stop occurred. */
  protected boolean m_RestrictedStop;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encapsulates a sequence of flow items.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RestrictingStops = getDefaultRestrictingStops();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String actorsTipText() {
    return "All the actors that define this sequence.";
  }

  /**
   * Returns the default for restricting stops.
   *
   * @return		true if restricted
   */
  protected boolean getDefaultRestrictingStops() {
    return false;
  }

  /**
   * Sets whether to restrict stops or not.
   *
   * @param value	true if to restrict
   */
  @Override
  public void setRestrictingStops(boolean value) {
    m_RestrictingStops = value;
  }

  /**
   * Returns whether stops are being restricted.
   *
   * @return		true if restricting stops
   */
  @Override
  public boolean isRestrictingStops() {
    return m_RestrictingStops;
  }

  /**
   * Returns whether the stop was a restricted one (that can be resumed).
   *
   * @return		true if restricted stop occurred
   */
  public boolean isRestrictedStop() {
    return m_RestrictedStop;
  }

  /**
   * Stops the (restricted) execution. No message set.
   */
  @Override
  public void restrictedStopExecution() {
    m_RestrictedStop = true;
    super.stopExecution();
  }

  /**
   * Stops the (restricted) execution.
   *
   * @param msg		the message to set as reason for stopping, can be null
   */
  @Override
  public void restrictedStopExecution(String msg) {
    m_RestrictedStop = true;
    super.stopExecution(msg);
  }

  /**
   * Sets whether standalones are allowed or not.
   *
   * @param value	true if standalones are allowed
   */
  public void setAllowStandalones(boolean value) {
    m_AllowStandalones = value;
  }

  /**
   * Returns whether standalones are allowed or not.
   *
   * @return		true if standalones are allowed
   */
  public boolean getAllowStandalones() {
    return m_AllowStandalones;
  }

  /**
   * Sets whether a source is allowed or not.
   *
   * @param value	true if a source is allowed
   */
  public void setAllowSource(boolean value) {
    m_AllowSource = value;
  }

  /**
   * Returns whether a source is allowed or not.
   *
   * @return		true if a source is allowed
   */
  public boolean getAllowSource() {
    return m_AllowSource;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo()
      .allowStandalones(m_AllowStandalones)
      .allowSource(m_AllowSource)
      .actorExecution(ActorExecution.SEQUENTIAL)
      .forwardsInput(true);
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

    if (m_CurrentToken != null)
      result.put(BACKUP_CURRENT, m_CurrentToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CURRENT)) {
      m_CurrentToken = (Token) state.get(BACKUP_CURRENT);
      input(m_CurrentToken);
      state.remove(BACKUP_CURRENT);
    }

    super.restoreState(state);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    Class[]	result;
    Actor 	first;

    result = new Class[]{Unknown.class};

    first = firstActive();
    if (first instanceof InputConsumer)
      result = ((InputConsumer) first).accepts();

    return result;
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    Actor	first;

    m_CurrentToken = token;

    first = firstActive();
    if (isLoggingEnabled())
      getLogger().info("first active actor: " + ((first == null) ? "null" : first.getFullName()));
    if (first instanceof InputConsumer) {
      ((InputConsumer) first).input(m_CurrentToken);
      if (isLoggingEnabled())
	getLogger().fine("input token: " + m_CurrentToken);
    }
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_CurrentToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_CurrentToken;
  }

  /**
   * Pre-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String preExecute() {
    if (m_RestrictedStop) {
      m_RestrictedStop = false;
      m_Stopped        = false;
    }
    return super.preExecute();
  }

  /**
   * Post-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String postExecute() {
    m_CurrentToken = null;
    return super.postExecute();
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    m_CurrentToken = null;

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    m_CurrentToken = null;

    super.cleanUp();
  }
}
