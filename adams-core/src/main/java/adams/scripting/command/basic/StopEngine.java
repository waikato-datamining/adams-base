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
 * StopEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.basic;

import adams.scripting.command.AbstractCommandWithResponse;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.requesthandler.RequestHandler;
import adams.scripting.responsehandler.ResponseHandler;

/**
 * Simply stops the scripting engine, either the one handling the request
 * or the response. Useful for terminating the receiving scripting engine
 * once the last command has been processed.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StopEngine
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /**
   * The type of engine to stop.
   */
  public enum EngineType {
    REQUEST,
    RESPONSE
  }

  /** the type. */
  protected EngineType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Simply stops the scripting engine, either the one handling the request "
	+ "or the response.\n"
	+ "Useful for terminating a scripting engine after the last "
	+ "command has been processed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      EngineType.RESPONSE);
  }

  /**
   * Sets the type of engine to stop.
   *
   * @param value	the type
   */
  public void setType(EngineType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of engine to stop.
   *
   * @return		the type
   */
  public EngineType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String typeTipText() {
    return "The type of engine to stop.";
  }

  /**
   * Sets the payload for the request.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
  }

  /**
   * Returns the payload of the request, if any.
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
  @Override
  public Object[] getRequestPayloadObjects() {
    return new Object[0];
  }

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @param handler	for handling the request
   */
  @Override
  public void handleRequest(RemoteScriptingEngine engine, RequestHandler handler) {
    if (m_Type == EngineType.REQUEST) {
      getLogger().info("Request/Stopping " + toCommandLine());
      engine.stopExecution();
    }
    else {
      super.handleRequest(engine, handler);
    }
  }

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    return new byte[0];
  }

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  @Override
  public Object[] getResponsePayloadObjects() {
    return new Object[0];
  }

  /**
   * Handles the response.
   *
   * @param engine	the remote engine handling the response
   * @param handler	for handling the response
   */
  @Override
  public void handleResponse(RemoteScriptingEngine engine, ResponseHandler handler) {
    if (m_Type == EngineType.RESPONSE) {
      getLogger().info("Response/Stopping " + toCommandLine());
      engine.stopExecution();
    }
    else {
      super.handleResponse(engine, handler);
    }
  }
}
