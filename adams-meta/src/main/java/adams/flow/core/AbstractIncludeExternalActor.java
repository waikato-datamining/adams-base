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
 * AbstractIncludeExternalActor.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.FlowFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for actors that get replaced with the externally stored actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9391 $
 */
public abstract class AbstractIncludeExternalActor
  extends AbstractActor {

  /** for serialization. */
  private static final long serialVersionUID = -7860206690560690212L;

  /** the file the external actor is stored in. */
  protected FlowFile m_ActorFile;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "file", "actorFile",
	    new FlowFile("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "actorFile", m_ActorFile);
  }

  /**
   * Sets the file containing the external actor.
   *
   * @param value	the actor file
   */
  public void setActorFile(FlowFile value) {
    m_ActorFile = value;
    reset();
  }

  /**
   * Returns the file containing the external actor.
   *
   * @return		the actor file
   */
  public FlowFile getActorFile() {
    return m_ActorFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorFileTipText() {
    return "The file containing the external actor.";
  }

  /**
   * Performs checks on the external actor.
   * 
   * @param actor	the actor to check
   * @return		null if OK, otherwise error message
   */
  protected abstract String checkExternalActor(AbstractActor actor);
  
  /**
   * Sets up the external actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUpExternalActor() {
    String		result;
    List<String>	errors;
    AbstractActor	externalActor;

    result = null;

    if (!m_ActorFile.isFile()) {
      result = "'" + m_ActorFile.getAbsolutePath() + "' does not point to a file!";
    }
    else {
      errors = new ArrayList<String>();
      if (isLoggingEnabled())
	getLogger().fine("Attempting to load actor file: " + m_ActorFile);
      externalActor = ActorUtils.read(m_ActorFile.getAbsolutePath(), errors);
      if (!errors.isEmpty()) {
	result = "Error loading external actor '" + m_ActorFile.getAbsolutePath() + "':\n" + Utils.flatten(errors, "\n");
      }
      else if (externalActor == null) {
	result = "Error loading external actor '" + m_ActorFile.getAbsolutePath() + "'!";
      }
      else {
	result = checkExternalActor(externalActor);
	if (result == null) {
	  if (externalActor.getName().equals(externalActor.getDefaultName()))
	    externalActor.setName(getName());
	  externalActor.setVariables(getVariables());
	  ((ActorHandler) getParent()).set(index(), externalActor);
	  result = externalActor.setUp();
	  if (getErrorHandler() != this)
	    ActorUtils.updateErrorHandler(externalActor, getErrorHandler(), isLoggingEnabled());
	  // make sure we've got the current state of the variables
	  if (result == null)
	    externalActor.getOptionManager().updateVariableValues(true);
	  setParent(null);
	  cleanUp();
	}
      }
      if (isLoggingEnabled())
	getLogger().fine("Actor file load/setUp result: " + result);
    }

    return result;
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
      result = setUpExternalActor();

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    return null;
  }
}
