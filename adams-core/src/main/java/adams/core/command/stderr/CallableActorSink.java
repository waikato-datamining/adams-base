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
 * CallableActorSink.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command.stderr;

import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.FlowContextHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;

/**
 * Forwards the data to the callable actor sink.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CallableActorSink
  extends AbstractStdErrProcessor
  implements CallableActorUser  {

  private static final long serialVersionUID = -2194306680981658479L;

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the data received from the command's stderr via its logger instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "callable", "callableName",
      new CallableActorReference(CallableActorReference.UNKNOWN));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Sets the name of the callable actor to use.
   *
   * @param value 	the callable name
   */
  public void setCallableName(CallableActorReference value) {
    m_CallableName = value;
    reset();
  }

  /**
   * Returns the name of the callable actor in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getCallableName() {
    return m_CallableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String callableNameTipText() {
    return "The name of the callable actor to use.";
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  public Actor getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Configures the handler.
   *
   * @param owner 	the owning command
   * @return 		null if successfully setup, otherwise error message
   */
  @Override
  public String setUp(FlowContextHandler owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      m_CallableActor = m_Helper.findCallableActorRecursive(m_Owner.getFlowContext(), getCallableName());
      if (m_CallableActor == null)
        result = "Failed to locate callable actor: " + m_CallableName;
      else if (!ActorUtils.isSink(m_CallableActor))
        result = "Callable actor is not a sink: " + m_CallableName;
    }

    return result;
  }

  /**
   * Processes the stderr output received when in async mode.
   *
   * @param output the output to process
   */
  @Override
  public void processAsync(String output) {
    String	msg;

    if (m_CallableActor != null) {
      synchronized (m_CallableActor) {
	((InputConsumer) m_CallableActor).input(new Token(output));
	msg = m_CallableActor.execute();
	if (msg != null)
	  getLogger().warning("Problem forwarding data: " + msg);
      }
    }
  }

  /**
   * Processes the stderr output received when in blocking mode.
   *
   * @param output the output to process
   */
  @Override
  public void processBlocking(String output) {
    processAsync(output);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_CallableActor = null;
    m_Helper        = null;
    super.cleanUp();
  }
}
