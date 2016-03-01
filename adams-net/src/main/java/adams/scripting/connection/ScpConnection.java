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
 * ScpConnection.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

import adams.core.MessageCollection;
import adams.core.io.FileUtils;
import adams.core.io.TempUtils;
import adams.core.net.Scp;
import adams.scripting.command.CommandUtils;
import adams.scripting.command.RemoteCommand;

import java.io.File;

/**
 * Copies the command as file to the remote host into the specified directory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScpConnection
  extends AbstractSSHConnection {

  private static final long serialVersionUID = 7165239073212488208L;

  /** the remote directory. */
  protected String m_RemoteDir;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Copies the command as file to the remote host into the specified directory.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "remote-dir", "remoteDir",
      "/tmp");
  }

  /**
   * Sets the remote directory to copy the command file to.
   *
   * @param value	the directory
   */
  public void setRemoteDir(String value) {
    m_RemoteDir = value;
    reset();
  }

  /**
   * Returns the remote directory to copy the command file to.
   *
   * @return		the directory
   */
  public String getRemoteDir() {
    return m_RemoteDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteDirTipText() {
    return "The remote directory to copy the command file to.";
  }

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param cmd		the command to send
   * @param request	whether Request or Response
   * @return		null if successfully sent, otherwise error message
   */
  @Override
  protected String doSend(RemoteCommand cmd, boolean request) {
    String		result;
    File 		tmpFile;
    MessageCollection	errors;

    result  = null;

    // save command
    tmpFile = TempUtils.createTempFile("remote", ".rc");
    errors  = new MessageCollection();
    if (!CommandUtils.write(cmd, tmpFile, errors))
      result = "Failed to write command to: " + tmpFile + "\n" + errors;

    // copy file
    if (result == null) {
      result = Scp.copyTo(this, this, m_Host, m_Port, tmpFile, m_RemoteDir);

      // remote tmp file
      if (!FileUtils.delete(tmpFile))
	getLogger().warning("Failed to delete tmp command file: " + tmpFile);
    }

    return result;
  }
}
