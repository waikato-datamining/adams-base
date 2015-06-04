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
 * AbstractNamedSetup.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.NamedSetup;
import adams.core.QuickInfoHelper;

import java.util.HashSet;

/**
 * Ancestor of actors that obtain an actor through a named setup and execute it.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractNamedSetup
  extends AbstractActor {

  /** for serialization. */
  private static final long serialVersionUID = 1024129351334661368L;

  /** the named setup to use. */
  protected NamedSetup m_Setup;

  /** the named setup itself. */
  protected AbstractActor m_NamedSetupActor;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "setup", "setup",
	    new NamedSetup());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "setup", m_Setup.getName());
  }

  /**
   * Sets the named setup to use.
   *
   * @param value	the setup
   */
  public void setSetup(NamedSetup value) {
    m_Setup = value;
    reset();
  }

  /**
   * Returns the named setup to use.
   *
   * @return		the setup
   */
  public NamedSetup getSetup() {
    return m_Setup;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String setupTipText() {
    return "The named setup to use.";
  }

  /**
   * Returns the actor from the named setup.
   *
   * @return		the actor, can be null if not initialized yet or failed
   * 			to initialize
   */
  public AbstractActor getNamedSetupActor() {
    return m_NamedSetupActor;
  }

  /**
   * Sets up the actor referenced by the named setup.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String setUpNamedSetupActor() {
    String	result;

    result = null;

    m_NamedSetupActor = (AbstractActor) m_Setup.getSetup();
    if (m_NamedSetupActor == null) {
      result = "Error retrieving named setup '" + m_Setup.getName() + "'!";
    }
    else {
      m_NamedSetupActor.setParent(this);
      result = m_NamedSetupActor.setUp();
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
    String		result;
    HashSet<String>	variables;

    result = super.setUp();

    if (result == null) {
      // due to change in variable, we need to clean up external actor
      if (m_NamedSetupActor != null) {
	m_NamedSetupActor.wrapUp();
	m_NamedSetupActor.cleanUp();
	m_NamedSetupActor = null;
      }

      if (getOptionManager().getVariableForProperty("setup") == null) {
	result = setUpNamedSetupActor();
	if (result == null) {
	  variables = findVariables(m_NamedSetupActor);
	  m_DetectedVariables.addAll(variables);
	  if (m_DetectedVariables.size() > 0)
	    getVariables().addVariableChangeListener(this);
	}
      }
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();

    if (m_NamedSetupActor != null)
      m_NamedSetupActor.stopExecution();
  }

  /**
   * Gets called in the doExceute() method, after an optional
   * setUpNamedSetupActor() call (in case a variable is used for the actor file),
   * but before the named setup actor's execute() method is called.
   * <br><br>
   * Default implementation does nothing.
   *
   * @return		null if everything ok, otherwise error message
   * @see		#doExecute()
   * @see		#setUpNamedSetupActor()
   */
  protected String preExecuteNamedSetupActorHook() {
    return null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    HashSet<String>	variables;

    result = null;

    // not setup yet due to variable?
    if (m_NamedSetupActor == null) {
      result = setUpNamedSetupActor();
      if (result == null) {
	variables = findVariables(m_NamedSetupActor);
	m_DetectedVariables.addAll(variables);
	if (m_DetectedVariables.size() > 0)
	  getVariables().addVariableChangeListener(this);
      }
    }

    if (result == null)
      result = preExecuteNamedSetupActorHook();

    if (result == null)
      result = m_NamedSetupActor.execute();

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_NamedSetupActor != null)
      m_NamedSetupActor.wrapUp();

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void cleanUp() {
    if (m_NamedSetupActor != null) {
      m_NamedSetupActor.destroy();
      m_NamedSetupActor.setParent(null);
      m_NamedSetupActor = null;
    }

    super.cleanUp();
  }
}
