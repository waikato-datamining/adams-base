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
 * SendFile.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.basic;

import adams.core.Utils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.scripting.command.AbstractFlowAwareCommand;
import adams.scripting.engine.RemoteScriptingEngine;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Sends a file as binary blob.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SendFile
  extends AbstractFlowAwareCommand {

  private static final long serialVersionUID = -1657908444959620122L;

  /** the file to send. */
  protected PlaceholderFile m_File;

  /** the remote directory where to place the file. */
  protected PlaceholderDirectory m_RemoteDir;

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
      "file", "file",
      new PlaceholderFile());

    m_OptionManager.add(
      "remote-dir", "remoteDir",
      new PlaceholderDirectory());
  }

  /**
   * Sets the file to send.
   *
   * @param value	the file
   */
  public void setFile(PlaceholderFile value) {
    m_File = value;
    reset();
  }

  /**
   * Returns the file to send.
   *
   * @return		the file
   */
  public PlaceholderFile getFile() {
    return m_File;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String fileTipText() {
    return "The file to send.";
  }

  /**
   * Sets the remote directory to place the file in.
   *
   * @param value	the dir
   */
  public void setRemoteDir(PlaceholderDirectory value) {
    m_RemoteDir = value;
    reset();
  }

  /**
   * Returns the remote directory to place the file in.
   *
   * @return		the dir
   */
  public PlaceholderDirectory getRemoteDir() {
    return m_RemoteDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String remoteDirTipText() {
    return "The remote directory to place the file in.";
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
    m_Content = value;
  }

  /**
   * Hook method for preparing the request payload,
   */
  protected void prepareRequestPayload() {
    try {
      m_Content = FileUtils.readFileToByteArray(m_File.getAbsoluteFile());
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to read data from file: " + m_File, e);
    }
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    return m_Content;
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[]{m_Content};
  }

  /**
   * Handles the request.
   *
   * @param engine	the remote engine handling the request
   * @return		null if successful, otherwise error message
   */
  protected String doHandleRequest(RemoteScriptingEngine engine) {
    String	result;
    File 	file;

    result = null;

    file = new File(m_RemoteDir.getAbsolutePath() + File.separator + m_File.getName());
    getLogger().info("Writing data to: " + file);
    try {
      FileUtils.writeByteArrayToFile(file, m_Content, false);
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to write content to: " + file, e);
    }

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
}
