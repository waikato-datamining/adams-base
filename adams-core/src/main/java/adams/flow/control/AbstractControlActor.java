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
 * AbstractControlActor.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.Pausable;
import adams.core.Variables;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateListener;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.Compatibility;
import adams.flow.core.ControlActor;
import adams.flow.core.InputConsumer;
import adams.flow.core.PauseStateHandler;
import adams.flow.core.PauseStateManager;
import adams.flow.core.SubFlowWrapUp;

import java.util.HashSet;

/**
 * Ancestor for all actors that control sub-actors in some way.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractControlActor
  extends AbstractActor
  implements ControlActor, ActorHandler, Pausable, FlowPauseStateListener, SubFlowWrapUp {

  /** for serialization. */
  private static final long serialVersionUID = -7471817724012995179L;

  /** the compatibility class in use. */
  protected Compatibility m_Compatibility;

  /** the pause state manager. */
  protected PauseStateManager m_PauseStateManager;
  
  /** whether the sub-actors were set up. */
  protected boolean m_SetUpSubActors;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Compatibility = new Compatibility();
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_SetUpSubActors = false;
  }

  /**
   * Sets the parent of this actor, e.g., the group it belongs to.
   *
   * @param value	the new parent
   */
  @Override
  public void setParent(AbstractActor value) {
    super.setParent(value);
    if (value != null)
      updateParent();
  }
  
  /**
   * Updates the Variables instance in use.
   * <br><br>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  protected void forceVariables(Variables value) {
    int		i;
    
    super.forceVariables(value);
    
    for (i = 0; i < size(); i++)
      get(i).setVariables(value);
  }
  
  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  public abstract ActorHandlerInfo getActorHandlerInfo();

  /**
   * Returns the size of the group.
   *
   * @return		the size
   */
  @Override
  public abstract int size();

  /**
   * Returns the number of non-skipped actors.
   *
   * @return		the 'active' actors
   */
  public int active() {
    int		result;
    int		i;

    result = 0;
    for (i = 0; i < size(); i++) {
      if (!get(i).getSkip())
	result++;
    }

    return result;
  }

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  @Override
  public AbstractActor firstActive() {
    AbstractActor	result;
    int			i;

    result = null;
    for (i = 0; i < size(); i++) {
      if (!get(i).getSkip()) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  @Override
  public AbstractActor lastActive() {
    AbstractActor	result;
    int			i;

    result = null;
    for (i = size() - 1; i >= 0; i--) {
      if (!get(i).getSkip()) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns the first non-skipped InputConsumer.
   *
   * @return		the first 'active' InputConsumer, null if none available
   */
  public AbstractActor firstInputConsumer() {
    AbstractActor	result;
    int			i;

    result = null;
    for (i = 0; i < size(); i++) {
      if (!get(i).getSkip() && (get(i) instanceof InputConsumer)) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public abstract AbstractActor get(int index);

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public abstract void set(int index, AbstractActor actor);

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public abstract int indexOf(String actor);

  /**
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    int		i;

    for (i = 0; i < size(); i++) {
      get(i).setParent(null);
      get(i).setParent(this);
    }
  }

  /**
   * Checks whether the class' options can be inspected. By default, arrays
   * of actors (i.e., the control actor's sub-actors) won't be inspected, as
   * they do it themselves.
   *
   * @param cls		the class to check
   * @return		true if it can be inspected, false otherwise
   */
  @Override
  public boolean canInspectOptions(Class cls) {
    // we don't inspect sub-actors!
    if (cls == AbstractActor[].class)
      return false;
    else if (cls == AbstractActor.class)
      return false;
    else
      return super.canInspectOptions(cls);
  }

  /**
   * Performs the setUp of the sub-actors.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String setUpSubActors() {
    int			i;
    String		result;
    HashSet<String>	names;

    result = null;

    // check whether everything is correctly setup
    names = new HashSet<String>();
    for (i = 0; i < size(); i++) {
      // make sure that name is unique!
      ActorUtils.uniqueName(get(i), names);
      names.add(get(i).getName());

      // setup actor
      if (!get(i).getSkip()) {
	result = get(i).setUp();
	if (result != null) {
	  result = get(i).getFullName() + ": " + result;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    String		msg;

    result = super.setUp();

    if (result == null)
      result = setUpSubActors();

    if (getRoot() instanceof PauseStateHandler) {
      m_PauseStateManager = ((PauseStateHandler) getRoot()).getPauseStateManager();
      if (m_PauseStateManager != null)
	m_PauseStateManager.addListener(this);
    }
    else {
      m_PauseStateManager = null;
    }
    
    if (result == null) {
      // check connections
      msg = check();
      if (isLoggingEnabled())
	getLogger().info("check: " + ((msg == null) ? "OK" : msg));
      if (msg != null)
        result = "Check failed: " + msg;
    }

    if (result == null)
      m_SetUpSubActors = true;
    
    return result;
  }

  /**
   * Performs checks on the "sub-actors". Default implementation does nothing.
   *
   * @return		null
   */
  @Override
  public String check() {
    return null;
  }

  /**
   * Pre-execute hook.
   * <br><br>
   * Attempts to setUp actor again, if it was wrapped up before.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		SubFlowWrapUp#wrapUpSubFlow()
   */
  @Override
  protected String preExecute() {
    String	result;
    
    result = null;
    
    if (!m_SetUpSubActors) {
      if (isLoggingEnabled())
	getLogger().info("Setting up sub-flow again");
      
      result = setUpSubActors();

      if (isLoggingEnabled() && (result != null))
	getLogger().info("Setting up of subflow failed: " + result);
    }
    
    if (result == null)
      result = super.preExecute();
    
    return result;
  }

  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    int     i;

    for (i = 0; i < size(); i++) {
      if (get(i) instanceof ActorHandler)
        ((ActorHandler) get(i)).flushExecution();
    }
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    wrapUpSubFlow();
    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    int		i;

    for (i = 0; i < size(); i++) {
      if (!get(i).getSkip())
	get(i).cleanUp();
    }

    super.cleanUp();

    m_SetUpSubActors = false;
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Cleans up the options and calls the destroy() method of all sub-actors.
   */
  @Override
  public void destroy() {
    int		i;

    for (i = 0; i < size(); i++)
      get(i).destroy();

    super.destroy();

    m_SetUpSubActors = false;
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    if (m_PauseStateManager != null)
      m_PauseStateManager.pause(this);
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    if (m_PauseStateManager != null)
      return m_PauseStateManager.isPaused();
    else
      return false;
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    if (m_PauseStateManager != null)
      m_PauseStateManager.resume(this);
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_PauseStateManager != null) {
      if (m_PauseStateManager.isPaused())
	m_PauseStateManager.resume(this);
    }
    super.stopExecution();
  }

  /**
   * Gets called when the pause state of the flow changes.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param e		the event
   */
  @Override
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
  }

  /**
   * Wraps up the sub-actors, freeing up memory.
   */
  @Override
  public void wrapUpSubFlow() {
    int		i;
    
    if (isLoggingEnabled())
      getLogger().info("Wrapping up sub-flow");
    
    for (i = 0; i < size(); i++) {
      if (!get(i).getSkip())
	get(i).wrapUp();
    }

    m_SetUpSubActors = false;
  }
  
  /**
   * Checks whether the sub-flow has been wrapped up.
   * 
   * @return		true if sub-flow was wrapped up
   */
  @Override
  public boolean isSubFlowWrappedUp() {
    return !m_SetUpSubActors;
  }
}
