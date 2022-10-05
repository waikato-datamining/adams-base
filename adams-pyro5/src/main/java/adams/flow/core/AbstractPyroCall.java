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
 * AbstractPyroSource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.flow.standalone.PyroNameServer;
import net.razorvine.pyro.PyroProxy;

/**
 * Ancestor for Pyro5 method calls.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPyroCall
  extends AbstractOptionHandler
  implements PyroCall, QuickInfoSupporter {

  private static final long serialVersionUID = -5360445580341176447L;

  /** the name of the remote object. */
  protected String m_RemoteObjectName;

  /** the name of the method. */
  protected String m_MethodName;

  /** the flow context. */
  protected transient Actor m_FlowContext;

  /** the nameserver. */
  protected transient PyroNameServer m_NameServer;

  /** the remote object. */
  protected transient PyroProxy m_RemoteObject;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "remote-object-name", "remoteObjectName",
      getDefaultRemoteObjectName());

    m_OptionManager.add(
      "method-name", "methodName",
      getDefaultMethodName());
  }

  @Override
  protected void reset() {
    super.reset();

    m_NameServer   = null;
    m_RemoteObject = null;
  }

  /**
   * Returns the default remote object name.
   *
   * @return		the name
   */
  protected String getDefaultRemoteObjectName() {
    return "";
  }

  /**
   * Sets the name of the remote object to use.
   *
   * @param value 	the name
   */
  public void setRemoteObjectName(String value) {
    m_RemoteObjectName = value;
    reset();
  }

  /**
   * Returns the name of the remote object to use.
   *
   * @return 		the name
   */
  public String getRemoteObjectName() {
    return m_RemoteObjectName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteObjectNameTipText() {
    return "The name of the remote object to use.";
  }

  /**
   * Returns the default method name.
   *
   * @return		the name
   */
  protected String getDefaultMethodName() {
    return "";
  }

  /**
   * Sets the name of the method to call.
   *
   * @param value 	the name
   */
  public void setMethodName(String value) {
    m_MethodName = value;
    reset();
  }

  /**
   * Returns the name of the method to call.
   *
   * @return 		the name
   */
  public String getMethodName() {
    return m_MethodName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String methodNameTipText() {
    return "The name of the method to call.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "remoteObjectName", m_RemoteObjectName, "remote object: ");
    result += QuickInfoHelper.toString(this, "methodName", m_MethodName, ", method: ");

    return result;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Configures the call.
   *
   * @return		null if successful, otherwise error message
   */
  public String setUp() {
    if (m_FlowContext == null)
      return "No flow context set!";
    if (m_RemoteObjectName.trim().isEmpty())
      return "No remote object name provided!";
    if (m_MethodName.trim().isEmpty())
      return "No method name provided!";

    m_NameServer = (PyroNameServer) ActorUtils.findClosestType(m_FlowContext, PyroNameServer.class, true);
    if (m_NameServer == null)
      return "Failed to locate a " + Utils.classToString(PyroNameServer.class) + " actor!";

    return null;
  }

  /**
   * Before performing the actual call. Attempts to obtain the remote object
   * if no instance yet available.
   *
   * @return		null if successful, otherwise error message
   */
  protected String preExecute() {
    String	result;

    result = null;

    if (m_NameServer.getNameServer() == null)
      return "No name server instance available!";

    if (m_RemoteObject == null) {
      try {
	m_RemoteObject = new PyroProxy(m_NameServer.getNameServer().lookup(m_RemoteObjectName));
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to obtain remote object: " + m_RemoteObjectName, e);
      }
    }

    return result;
  }

  /**
   * Performs the actual call.
   *
   * @return		null if successful, otherwise error message
   */
  protected abstract String doExecute();

  /**
   * After performing the actual call.
   * <br>
   * Default implementation does nothing, returns null.
   *
   * @return		null if successful, otherwise error message
   */
  protected String postExecute() {
    return null;
  }

  /**
   * Performs the call.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String	result;

    result = preExecute();
    if (result == null)
      result = doExecute();
    if (result == null)
      result = postExecute();

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_NameServer  = null;
    m_FlowContext = null;
  }
}
