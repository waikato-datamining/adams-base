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
 * AbstractIncludeExternalActor.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.Utils;
import adams.core.Variables;
import adams.core.io.PlaceholderFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for actors that get replaced with the externally stored actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9391 $
 */
public abstract class AbstractIncludeExternalActor
  extends AbstractBaseExternalActor {

  /** for serialization. */
  private static final long serialVersionUID = -7860206690560690212L;

  /**
   * Performs checks on the external actor.
   * 
   * @param actor	the actor to check
   * @return		null if OK, otherwise error message
   */
  protected abstract String checkExternalActor(Actor actor);
  
  /**
   * Sets up the external actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUpExternalActor() {
    String		result;
    List<String>	errors;
    Actor		externalActor;
    PlaceholderFile	file;

    result = null;

    file = m_ActorFile;

    // programmatic variable maybe?
    if (file.toString().startsWith(Variables.START))
      file = new PlaceholderFile(getVariables().expand(file.toString()));

    if (!file.isFile()) {
      result = "'" + file.getAbsolutePath() + "' does not point to a file!";
    }
    else {
      errors = new ArrayList<>();
      if (isLoggingEnabled())
	getLogger().fine("Attempting to load actor file: " + file);
      externalActor = ActorUtils.read(file.getAbsolutePath(), errors);
      if (!errors.isEmpty()) {
	result = "Error loading external actor '" + file.getAbsolutePath() + "':\n" + Utils.flatten(errors, "\n");
      }
      else if (externalActor == null) {
	result = "Error loading external actor '" + file.getAbsolutePath() + "'!";
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
