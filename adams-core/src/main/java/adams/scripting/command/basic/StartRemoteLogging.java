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
 * StartRemoteLogging.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.basic;

import adams.core.base.BaseHostname;
import adams.core.logging.LoggingHelper;
import adams.core.logging.RemoteReceiveHandler;
import adams.core.logging.RemoteReceiveHandler.AbstractRemoteListenerRunnable;
import adams.core.logging.RemoteSendHandler;
import adams.core.logging.SimpleConsoleHandler;
import adams.scripting.command.AbstractCommandWithResponse;

import java.util.logging.Handler;

/**
 * Starts remote logging.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StartRemoteLogging
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the logging host (local machine). */
  protected BaseHostname m_LoggingHost;

  /** the timeout to use. */
  protected int m_TimeOut;

  /** whether to instal a listener. */
  protected boolean m_InstallListener;

  /** the handler to use. */
  protected Handler m_Handler;

  /** the maximum number of failed send attempts to tolerate. */
  protected int m_MaxFailures;

  /** the message (null is successful, otherwise error message). */
  protected String m_Message;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Starts up framework for receiving remote logging information.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "loggingHost", "loggingHost",
      new BaseHostname("127.0.0.1:" + AbstractRemoteListenerRunnable.DEFAULT_PORT));

    m_OptionManager.add(
      "time-out", "timeOut",
      AbstractRemoteListenerRunnable.DEFAULT_TIMEOUT, 1, null);

    m_OptionManager.add(
      "install-listener", "installListener",
      true);

    m_OptionManager.add(
      "handler", "handler",
      new SimpleConsoleHandler());

    m_OptionManager.add(
      "max-failures", "maxFailures",
      -1, -1, null);
  }

  /**
   * Sets the logging host, i.e., the host/port of the local machine to send the logging
   * information to.
   *
   * @param value	host/port
   */
  public void setLoggingHost(BaseHostname value) {
    m_LoggingHost = value;
    reset();
  }

  /**
   * Returns the logging host, i.e., the host/port of the local machine to send the logging
   * information to.
   *
   * @return		host/port
   */
  public BaseHostname getLoggingHost() {
    return m_LoggingHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggingHostTipText() {
    return "The hostname and port of the local machine the remote machine will send the logging information to.";
  }

  /**
   * Sets the timeout for accepting remote data.
   *
   * @param value	timeout in msec
   */
  public void setTimeOut(int value) {
    if (getOptionManager().isValid("timeOut", value)) {
      m_TimeOut = value;
      reset();
    }
  }

  /**
   * Returns the timeout for accepting remote data.
   *
   * @return		timeout in msec
   */
  public int getTimeOut() {
    return m_TimeOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timeOutTipText() {
    return "The time-out in msec for accepting data.";
  }

  /**
   * Sets whether to install a listener.
   *
   * @param value	true if to install listener
   */
  public void setInstallListener(boolean value) {
    m_InstallListener = value;
    reset();
  }

  /**
   * Returns whether to install a listener.
   *
   * @return		true if to install listener
   */
  public boolean getInstallListener() {
    return m_InstallListener;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String installListenerTipText() {
    return "If enabled, a listener gets installed using the specified handler.";
  }

  /**
   * Sets the handler to use.
   *
   * @param value	handler
   */
  public void setHandler(Handler value) {
    m_Handler = value;
    reset();
  }

  /**
   * Returns the handler to use.
   *
   * @return		handler
   */
  public Handler getHandler() {
    return m_Handler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String handlerTipText() {
    return "The logging handler to use.";
  }

  /**
   * Sets the maximum number of failures to accept on the sending side.
   *
   * @param value	maximum number, less than 1 for infinite
   */
  public void setMaxFailures(int value) {
    if (getOptionManager().isValid("maxFailures", value)) {
      m_MaxFailures = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of failures to accept on the sending side.
   *
   * @return		maximum number, less than 1 for infinite
   */
  public int getMaxFailures() {
    return m_MaxFailures;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxFailuresTipText() {
    return "The maximum number of failures to tolerate on the sending side (< 1 for infinite).";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Message = null;
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    return new byte[0];
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[0];
  }

  /**
   * Hook method before sending the command.
   */
  @Override
  public void beforeSendRequest() {
    RemoteReceiveHandler	handler;
    String			msg;

    super.beforeSendRequest();

    if (m_InstallListener) {
      handler = new RemoteReceiveHandler();
      handler.setHandler(m_Handler);
      handler.setPort(m_LoggingHost.portValue());
      handler.setTimeOut(m_TimeOut);
      msg = LoggingHelper.wrapDefaultHandler(handler);
      if (msg != null)
	getLogger().severe("beforeSendRequest/add: " + msg);
      else
	handler.startListening();
    }
  }

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
    if (value.length == 0)
      return;

    m_Message = new String(value);
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    if (m_Message == null)
      return new byte[0];
    else
      return m_Message.getBytes();
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    RemoteSendHandler 	handler;

    handler = new RemoteSendHandler();
    handler.setHostname(m_LoggingHost.hostnameValue());
    handler.setPort(m_LoggingHost.portValue());
    handler.setMaxFailures(m_MaxFailures);
    m_Message = LoggingHelper.addToDefaultHandler(handler, true);
  }

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  public Object[] getResponsePayloadObjects() {
    if (m_Message == null)
      return new Object[0];
    else
      return new Object[]{m_Message};
  }
}
