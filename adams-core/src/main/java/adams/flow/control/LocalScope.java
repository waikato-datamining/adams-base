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
 * LocalScope.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import java.util.HashSet;

import adams.core.Variables;
import adams.core.VariablesHandler;
import adams.flow.core.FlowVariables;

/**
 <!-- globalinfo-start -->
 * Executes the sub-actors whenever a token gets passed through, just like the adams.flow.control.Trigger actor, but also provides its own scope for variables and internal storage.
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
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: LocalScope
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-tee &lt;adams.flow.core.AbstractActor&gt; [-tee ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LocalScope
  extends Trigger 
  implements VariablesHandler, StorageHandler, ScopeHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8344934611549310497L;

  /** the storage for temporary data. */
  protected transient Storage m_LocalStorage;

  /** the variables manager. */
  protected FlowVariables m_LocalVariables;
  
  /** the global names. */
  protected HashSet<String> m_GlobalNames;
  
  /** whether the global name check is enforced. */
  protected boolean m_EnforceGlobalNameCheck;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Executes the sub-actors whenever a token gets passed through, just " 
        + "like the " + Trigger.class.getName() + " actor, but also provides "
        + "its own scope for variables and internal storage.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_GlobalNames            = new HashSet<String>();
    m_EnforceGlobalNameCheck = true;
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    m_GlobalNames.clear();
  }

  /**
   * Sets whether to enforce the global name check.
   * 
   * @param value	true if to enforce check
   */
  public void setEnforceGlobalNameCheck(boolean value) {
    m_EnforceGlobalNameCheck = value;
  }
  
  /**
   * Returns whether the check of global names is enforced.
   * 
   * @return		true if check enforced
   */
  public boolean getEnforceGlobalNameCheck() {
    return m_EnforceGlobalNameCheck;
  }

  /**
   * Checks whether a global name is already in use.
   * 
   * @param name	the name to check
   * @see		#getEnforceGlobalNameCheck()
   */
  public boolean isGlobalNameUsed(String name) {
    if (!getEnforceGlobalNameCheck())
      return false;
    else
      return m_GlobalNames.contains(name);
  }

  /**
   * Adds the global name to the list of used ones.
   * 
   * @param name	the name to add
   * @return		null if successfully added, otherwise error message
   * @see		#getEnforceGlobalNameCheck()
   */
  public String addGlobalName(String name) {
    if (!getEnforceGlobalNameCheck())
      return null;
    
    if (isGlobalNameUsed(name))
      return "Global name '" + name + "' is already used in this scope ('" + getFullName() + "')!";
    
    m_GlobalNames.add(name);
    return null;
  }
  
  /**
   * Returns the storage container.
   *
   * @return		the container
   */
  public synchronized Storage getStorage() {
    if (m_LocalStorage == null)
      m_LocalStorage = new Storage();

    return m_LocalStorage;
  }

  /**
   * Returns the Variables instance to use.
   *
   * @return		the local variables
   */
  public synchronized Variables getLocalVariables() {
    if (m_LocalVariables == null) {
      m_LocalVariables = new FlowVariables();
      m_LocalVariables.setFlow(this);
    }
    
    return m_LocalVariables;
  }

  /**
   * Returns the Variables instance to use.
   *
   * @return		the scope handler
   */
  @Override
  public synchronized Variables getVariables() {
    return getLocalVariables();
  }
  
  /**
   * Updates the Variables instance in use.
   *
   * @param value	ignored
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(getLocalVariables());
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_LocalVariables != null) {
      m_LocalVariables.cleanUp();
      m_LocalVariables = null;
    }

    m_GlobalNames.clear();

    super.cleanUp();
  }
}
