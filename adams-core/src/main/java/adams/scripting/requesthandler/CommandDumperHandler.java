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
 * CommandDumperHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.requesthandler;

import adams.core.MessageCollection;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.TempUtils;
import adams.scripting.command.CommandUtils;
import adams.scripting.command.RemoteCommand;

import java.io.File;

/**
 * Saves requests as command files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CommandDumperHandler
  extends AbstractRequestHandler {

  private static final long serialVersionUID = -575781398900766054L;

  /** the output directory for successful requests. */
  protected PlaceholderDirectory m_SuccessfulDir;

  /** the output directory for failed requests. */
  protected PlaceholderDirectory m_FailedDir;

  /** the output directory for rejected requests. */
  protected PlaceholderDirectory m_RejectedDir;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Saves the request as command files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "successful-dir", "successfulDir",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "failed-dir", "failedDir",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "rejected-dir", "rejectedDir",
      new PlaceholderDirectory());
  }

  /**
   * Sets the directory for successful commands.
   *
   * @param value	the dir
   */
  public void setSuccessfulDir(PlaceholderDirectory value) {
    m_SuccessfulDir = value;
    reset();
  }

  /**
   * Returns the directory for successful commands.
   *
   * @return		the dir
   */
  public PlaceholderDirectory getSuccessfulDir() {
    return m_SuccessfulDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String successfulDirTipText() {
    return "The directory to use for saving the successful commands in.";
  }

  /**
   * Sets the directory for failed commands.
   *
   * @param value	the dir
   */
  public void setFailedDir(PlaceholderDirectory value) {
    m_FailedDir = value;
    reset();
  }

  /**
   * Returns the directory for failed commands.
   *
   * @return		the dir
   */
  public PlaceholderDirectory getFailedDir() {
    return m_FailedDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String failedDirTipText() {
    return "The directory to use for saving the failed commands in.";
  }

  /**
   * Sets the directory for rejected commands.
   *
   * @param value	the dir
   */
  public void setRejectedDir(PlaceholderDirectory value) {
    m_RejectedDir = value;
    reset();
  }

  /**
   * Returns the directory for rejected commands.
   *
   * @return		the dir
   */
  public PlaceholderDirectory getRejectedDir() {
    return m_RejectedDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String rejectedDirTipText() {
    return "The directory to use for saving the rejected commands in.";
  }

  /**
   * Handles successfuly requests.
   *
   * @param cmd		the command with the request
   */
  @Override
  public void requestSuccessful(RemoteCommand cmd) {
    File 		tmpFile;
    MessageCollection	errors;

    if (!m_Enabled)
      return;

    tmpFile = TempUtils.createTempFile(m_SuccessfulDir, "successful-", ".rc");
    errors  = new MessageCollection();
    if (!CommandUtils.write(cmd, tmpFile, errors)) {
      if (errors.isEmpty())
	getLogger().severe("Failed to save successful command to: " + tmpFile);
      else
	getLogger().severe("Failed to save successful command to: " + tmpFile + "\n" + errors);
    }
  }

  /**
   * Handles failed requests.
   *
   * @param cmd		the command with the request
   * @param msg		the optional error message, can be null
   */
  @Override
  public void requestFailed(RemoteCommand cmd, String msg) {
    File 		tmpFile;
    MessageCollection	errors;

    if (!m_Enabled)
      return;

    tmpFile = TempUtils.createTempFile(m_FailedDir, "failed-", ".rc");
    errors  = new MessageCollection();
    if (!CommandUtils.write(cmd, tmpFile, errors)) {
      if (errors.isEmpty())
	getLogger().severe("Failed to save failed command to: " + tmpFile);
      else
	getLogger().severe("Failed to save failed command to: " + tmpFile + "\n" + errors);
    }
  }

  /**
   * Handles rejected requests.
   *
   * @param cmd		the command with the request
   * @param msg		the optional error message, can be null
   */
  @Override
  public void requestRejected(RemoteCommand cmd, String msg) {
    File 		tmpFile;
    MessageCollection	errors;

    if (!m_Enabled)
      return;

    tmpFile = TempUtils.createTempFile(m_RejectedDir, "rejected-", ".rc");
    errors  = new MessageCollection();
    if (!CommandUtils.write(cmd, tmpFile, errors)) {
      if (errors.isEmpty())
	getLogger().severe("Failed to save rejected command to: " + tmpFile);
      else
	getLogger().severe("Failed to save rejected  command to: " + tmpFile + "\n" + errors);
    }
  }
}
