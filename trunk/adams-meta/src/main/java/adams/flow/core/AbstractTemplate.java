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
 * AbstractTemplate.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import java.util.Hashtable;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.flow.template.AbstractActorTemplate;

/**
 * Abstract ancestor for all actors that use a template to generate the
 * actual actor/sub-flow to be executed.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTemplate
  extends AbstractActor
  implements InternalActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7246162048306571873L;

  /** the key for storing the current actor in the backup. */
  public final static String BACKUP_ACTOR = "actor";

  /** the template. */
  protected AbstractActorTemplate m_Template;

  /** the generated actor. */
  protected AbstractActor m_Actor;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "template", "template",
	    getDefaultTemplate());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Actor = null;
  }

  /**
   * Returns the default template to use.
   *
   * @return		the template
   */
  protected abstract AbstractActorTemplate getDefaultTemplate();

  /**
   * Sets the name of the global actor to use.
   *
   * @param value 	the global name
   */
  public void setTemplate(AbstractActorTemplate value) {
    m_Template = value;
    reset();
  }

  /**
   * Returns the name of the global actor in use.
   *
   * @return 		the global name
   */
  public AbstractActorTemplate getTemplate() {
    return m_Template;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String templateTipText() {
    return "The template to use for generating the actual actor.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "template", m_Template);
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_ACTOR);
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

    if (m_Actor != null)
      result.put(BACKUP_ACTOR, m_Actor);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_ACTOR)) {
      m_Actor = (AbstractActor) state.get(BACKUP_ACTOR);
      state.remove(BACKUP_ACTOR);
    }

    super.restoreState(state);
  }

  /**
   * Returns the internal actor.
   *
   * @return		the actor, null if not available
   */
  @Override
  public Actor getInternalActor() {
    return m_Actor;
  }

  /**
   * Updates the Variables instance in use.
   * <p/>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(value);
    if (m_Actor != null)
      m_Actor.forceVariables(value);
  }

  /**
   * Initializes the template for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String setUpTemplate() {
    String	result;

    result = null;

    m_Actor = m_Template.generate();
    if (m_Actor == null) {
      result = "Couldn't generate actor from template '" + getTemplate() + "'!";
    }
    else {
      m_Actor.setParent(this);
      m_Actor.setHeadless(isHeadless());
      m_Actor.setVariables(getVariables());
      m_Actor.setStopFlowOnError(getStopFlowOnError());
      result = m_Actor.setUp();
      if (result == null) {
	if (getErrorHandler() != this)
	  ActorUtils.updateErrorHandler(m_Actor, getErrorHandler(), isLoggingEnabled());
      }
   }

    return result;
  }

  /**
   * Returns the actual actor that was generated from the template.
   *
   * @return		the actual actor, null if not available
   */
  public AbstractActor getActualActor() {
    return m_Actor;
  }

  /**
   * Returns whether the actor has finished.
   *
   * @return		true if finished
   */
  @Override
  public boolean isFinished() {
    if (m_Actor == null)
      return true;
    else
      return m_Actor.isFinished();
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    try {
      if (m_Actor != null)
	m_Actor.stopExecution();
    }
    catch (Exception e) {
      // ignored
    }

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_Actor != null)
      m_Actor.wrapUp();

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_Actor != null) {
      m_Actor.cleanUp();
      m_Actor = null;
    }
  }
}