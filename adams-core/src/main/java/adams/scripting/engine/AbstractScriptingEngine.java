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
 * AbstractScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import adams.core.management.ProcessUtils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.core.Actor;
import adams.gui.application.ApplicationContext;
import adams.gui.scripting.ScriptingEngine;
import adams.multiprocess.CallableWithResult;
import adams.scripting.permissionhandler.AllowAll;
import adams.scripting.permissionhandler.PermissionHandler;
import adams.scripting.requesthandler.RequestHandler;
import adams.scripting.responsehandler.ResponseHandler;

/**
 * Ancestor of scripting engine for remote commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptingEngine
  extends AbstractOptionHandler
  implements RemoteScriptingEngine, StoppableWithFeedback {

  private static final long serialVersionUID = -3763240773922918567L;

  /** the application context. */
  protected ApplicationContext m_ApplicationContext;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the permission handler. */
  protected PermissionHandler m_PermissionHandler;

  /** the command handler. */
  protected RemoteCommandHandler m_CommandHandler;
  
  /** the request handler. */
  protected RequestHandler m_RequestHandler;

  /** the response handler to use. */
  protected ResponseHandler m_ResponseHandler;

  /** whether the engine is paused. */
  protected boolean m_Paused;

  /** whether the engine is stopped. */
  protected boolean m_Stopped;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "permission-handler", "permissionHandler",
      getDefaultPermissionHandler());

    m_OptionManager.add(
      "request-handler", "requestHandler",
      getDefaultRequestHandler());

    m_OptionManager.add(
      "response-handler", "responseHandler",
      getDefaultResponseHandler());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    setCommandHandler(getDefaultCommandHandler());
  }

  /**
   * Returns the default command handler.
   *
   * @return		the default
   */
  protected RemoteCommandHandler getDefaultCommandHandler() {
    return new DefaultRemoteCommandHandler();
  }

  /**
   * Sets the command handler to use.
   *
   * @param value	the command handler
   */
  public void setCommandHandler(RemoteCommandHandler value) {
    m_CommandHandler = value;
    m_CommandHandler.setOwner(this);
  }

  /**
   * Returns the command handler in use.
   *
   * @return		the command handler
   */
  public RemoteCommandHandler getCommandHandler() {
    return m_CommandHandler;
  }

  /**
   * Returns the default permission handler.
   *
   * @return		the default
   */
  protected PermissionHandler getDefaultPermissionHandler() {
    return new AllowAll();
  }

  /**
   * Sets the permission handler to use.
   *
   * @param value	the permission handler
   */
  public void setPermissionHandler(PermissionHandler value) {
    m_PermissionHandler = value;
    reset();
  }

  /**
   * Returns the permission handler in use.
   *
   * @return		the permission handler
   */
  public PermissionHandler getPermissionHandler() {
    return m_PermissionHandler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String permissionHandlerTipText() {
    return "The handler that determines what request can be executed.";
  }

  /**
   * Returns the default request handler.
   *
   * @return		the default
   */
  protected RequestHandler getDefaultRequestHandler() {
    return new adams.scripting.requesthandler.LoggingHandler();
  }

  /**
   * Sets the request handler to use.
   *
   * @param value	the request handler
   */
  public void setRequestHandler(RequestHandler value) {
    m_RequestHandler = value;
    m_RequestHandler.setOwner(this);
    reset();
  }

  /**
   * Returns the request handler in use.
   *
   * @return		the request handler
   */
  public RequestHandler getRequestHandler() {
    return m_RequestHandler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String requestHandlerTipText() {
    return "The request handler for acting on rejected, failed and successful requests.";
  }

  /**
   * Returns the default request handler.
   *
   * @return		the default
   */
  protected ResponseHandler getDefaultResponseHandler() {
    return new adams.scripting.responsehandler.LoggingHandler();
  }

  /**
   * Sets the response listener to use.
   *
   * @param value	the response listener
   */
  public void setResponseHandler(ResponseHandler value) {
    m_ResponseHandler = value;
    m_ResponseHandler.setOwner(this);
    reset();
  }

  /**
   * Returns the response listener in use.
   *
   * @return		the response listener
   */
  public ResponseHandler getResponseHandler() {
    return m_ResponseHandler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String responseHandlerTipText() {
    return "The handler for acting on successful and failed responses.";
  }

  /**
   * Sets the application context.
   *
   * @param value	the context
   */
  public void setApplicationContext(ApplicationContext value) {
    m_ApplicationContext = value;
  }

  /**
   * Returns the application context.
   *
   * @return		the context, null if none set
   */
  public ApplicationContext getApplicationContext() {
    return m_ApplicationContext;
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
   * Executes the job.
   *
   * @param job		the job to execute
   */
  public void executeJob(CallableWithResult<String> job) {
    String 	msg;

    try {
      msg = job.call();
    }
    catch (Exception e) {
      msg = Utils.handleException(this, "Failed to execute job!", e);
    }

    if (msg != null)
      getLogger().severe(msg);
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    m_Paused = true;
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    return m_Paused;
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    m_Paused = false;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
    m_Paused  = false;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Runs the engine from the commandline.
   * Retrieves the environment from option "-env classname".
   *
   *
   * @param options	the commandline options
   * @return		the instantiated frame, null in case of an error or
   * 			invocation of help
   */
  public static RemoteScriptingEngine runScriptingEngine(Class engine, String[] options) {
    String	env;
    Class	envCls;

    env = OptionUtils.getOption(options, "-env");
    if (env == null)
      env = Environment.class.getName();

    try {
      envCls = Class.forName(env);
      return runScriptingEngine(envCls, engine, options);
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Runs the engine from the commandline.
   *
   * @param env		the environment class to use
   * @param engine		the engine class
   * @param options	the commandline options
   * @return		the instantiated frame, null in case of an error or
   * 			invocation of help
   */
  public static RemoteScriptingEngine runScriptingEngine(Class env, Class engine, String[] options) {
    RemoteScriptingEngine	result;
    String			msg;

    Environment.setEnvironmentClass(env);
    LoggingHelper.useHandlerFromOptions(options);

    try {
      if (OptionUtils.helpRequested(options)) {
	System.out.println("Help requested...\n");
	result = forName(engine.getName(), new String[0]);
	System.out.print("\n" + OptionUtils.list(result));
	LoggingHelper.outputHandlerOption();
	ScriptingEngine.stopAllEngines();
	result = null;
      }
      else {
	result = forName(engine.getName(), options);
	if (result instanceof LoggingSupporter)
	  ((LoggingSupporter) result).getLogger().info("PID: " + ProcessUtils.getVirtualMachinePID());
	msg = result.execute();
	if (msg != null) {
	  if (result instanceof LoggingSupporter)
	    ((LoggingSupporter) result).getLogger().severe(msg);
	  else
	    System.err.println(msg);
	}
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the engine with the given options.
   *
   * @param classname	the classname of the engine to instantiate
   * @param options	the options for the engine
   * @return		the instantiated engine or null if an error occurred
   */
  public static RemoteScriptingEngine forName(String classname, String[] options) {
    RemoteScriptingEngine	result;

    try {
      result = (RemoteScriptingEngine) OptionUtils.forName(RemoteScriptingEngine.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the engine from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			engine to instantiate
   * @return		the instantiated engine
   * 			or null if an error occurred
   */
  public static RemoteScriptingEngine forCommandLine(String cmdline) {
    return (RemoteScriptingEngine) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
