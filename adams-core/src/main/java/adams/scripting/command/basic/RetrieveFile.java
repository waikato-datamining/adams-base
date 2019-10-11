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
 * RetrieveFile.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.basic;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.scripting.command.AbstractCommandWithResponse;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.processor.RemoteCommandProcessor;
import adams.scripting.responsehandler.ResponseHandler;

/**
 * Retrieves a file as binary blob.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RetrieveFile
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -1657908444959620122L;

  /** the file to retriev. */
  protected PlaceholderFile m_RemoteFile;

  /** the local file to save to. */
  protected PlaceholderFile m_LocalFile;

  /** the actual payload. */
  protected byte[] m_Content;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Loads and sends a file as binary blob.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "remote-file", "remoteFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "local-file", "localFile",
      new PlaceholderFile());
  }

  /**
   * Sets the remote file to send.
   *
   * @param value	the file
   */
  public void setRemoteFile(PlaceholderFile value) {
    m_RemoteFile = value;
    reset();
  }

  /**
   * Returns the remote file to send.
   *
   * @return		the file
   */
  public PlaceholderFile getRemoteFile() {
    return m_RemoteFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String remoteFileTipText() {
    return "The remote file to send.";
  }

  /**
   * Sets the local file to save the remote file to.
   *
   * @param value	the file
   */
  public void setLocalFile(PlaceholderFile value) {
    m_LocalFile = value;
    reset();
  }

  /**
   * Returns the local file to save the remote file to.
   *
   * @return		the file
   */
  public PlaceholderFile getLocalFile() {
    return m_LocalFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String localFileTipText() {
    return "The local file to save the remote file to.";
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
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @param processor 	the processor for formatting/parsing
   * @return		null if successful, otherwise error message
   */
  protected String doHandleRequest(RemoteScriptingEngine engine, RemoteCommandProcessor processor) {
    String	result;

    result = null;

    getLogger().info("Loading data from: " + m_RemoteFile);
    try {
      m_Content = FileUtils.loadFromBinaryFile(m_RemoteFile.getAbsoluteFile());
    }
    catch (Exception e) {
      result         = LoggingHelper.handleException(this, "Failed to read content from: " + m_RemoteFile, e);
      m_ErrorMessage = result;
    }

    if (result == null)
      return super.doHandleRequest(engine, processor);
    else
      return result;
  }

  /**
   * Returns a short description of the command.
   *
   * @return		the description
   */
  public String toString() {
    return getClass().getName() + ": " + m_Content.length + " bytes";
  }

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
    m_Content = value;
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    return m_Content;
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
    if (!FileUtils.writeToBinaryFile(m_LocalFile.getAbsolutePath(), m_Content))
      getLogger().severe("Failed to write remote content to local file: " + m_LocalFile);
    super.handleResponse(engine, handler);
  }
}
