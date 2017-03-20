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
import adams.core.logging.RemoteSendHandler;
import adams.scripting.command.AbstractCommandWithResponse;

/**
 * Starts remote logging.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StartRemoteLogging
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the local host. */
  protected BaseHostname m_LocalHost;

  /** the timeout to use. */
  protected int m_TimeOut;

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
      "local-host", "localHost",
      new BaseHostname("127.0.0.1:" + RemoteReceiveHandler.DEFAULT_PORT));

    m_OptionManager.add(
      "time-out", "timeOut",
      RemoteReceiveHandler.DEFAULT_TIMEOUT, 1, null);
  }

  /**
   * Sets the local host.
   *
   * @param value	host/port
   */
  public void setLocalHost(BaseHostname value) {
    m_LocalHost = value;
    reset();
  }

  /**
   * Returns the local host.
   *
   * @return		host/port
   */
  public BaseHostname getLocalHost() {
    return m_LocalHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String localHostTipText() {
    return "The hostname and port to use by the remote machine.";
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

    handler = new RemoteReceiveHandler();
    handler.setPort(m_LocalHost.portValue());
    handler.setTimeOut(m_TimeOut);
    msg = LoggingHelper.wrapDefaultHandler(handler);
    if (msg != null)
      getLogger().severe("beforeSendRequest/add: " + msg);
    else
      handler.startListening();
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
    handler.setHostname(m_LocalHost.hostnameValue());
    handler.setPort(m_LocalHost.portValue());
    m_Message = LoggingHelper.addToDefaultHandler(handler);
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
