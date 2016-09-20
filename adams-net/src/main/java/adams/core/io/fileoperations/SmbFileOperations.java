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
 * SmbFileOperations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.net.SMB;
import adams.core.net.SMBAuthenticationProvider;
import jcifs.smb.SmbFile;

/**
 * SMB / Windows share file operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SmbFileOperations
  extends AbstractRemoteFileOperations {

  private static final long serialVersionUID = -4668267794023495691L;

  /** the authentication provider to use. */
  protected SMBAuthenticationProvider m_Provider;

  /**
   * Sets the authentication provider to use.
   *
   * @param value	the provider
   */
  public void setProvider(SMBAuthenticationProvider value) {
    m_Provider = value;
  }

  /**
   * Returns the authentication provider in use.
   *
   * @return		the provider, null if none set
   */
  public SMBAuthenticationProvider getProvider() {
    return m_Provider;
  }

  /**
   * Checks whether the given operation is supported.
   *
   * @param op		the operation to check
   * @return		true if supported
   */
  public boolean isSupported(Operation op) {
    switch (op) {
      case COPY:
      case MOVE:
      case RENAME:
      case DELETE:
      case MKDIR:
	return true;
      default:
	throw new IllegalStateException("Unhandled operation: " + op);
    }
  }

  /**
   * Copies a file.
   *
   * @param source	the source file
   * @param target	the target file
   * @return		null if successful, otherwise error message
   */
  public String copy(String source, String target) {
    String		result;

    switch (m_Direction) {
      case LOCAL_TO_REMOTE:
        result = SMB.copyTo(this, m_Provider, new PlaceholderFile(source), target);
        break;

      case REMOTE_TO_LOCAL:
        result = SMB.copyFrom(this, m_Provider, source, new PlaceholderFile(target));
	break;

      default:
	throw new IllegalStateException("Unhandled direction: " + m_Direction);
    }

    return result;
  }

  /**
   * Moves a file.
   *
   * @param source	the source file
   * @param target	the target file
   * @return		null if successful, otherwise error message
   */
  public String move(String source, String target) {
    String	result;

    result = copy(source, target);

    if (result == null) {
      switch (m_Direction) {
	case LOCAL_TO_REMOTE:
	  if (!FileUtils.delete(source))
	    result = "Failed to delete: " + source;
	  break;

	case REMOTE_TO_LOCAL:
	  result = delete(source);
	  break;

	default:
	  throw new IllegalStateException("Unhandled direction: " + m_Direction);
      }
    }

    return result;
  }

  /**
   * Renames a remote file.
   *
   * @param source	the source file (old)
   * @param target	the target file (new)
   * @return		null if successful, otherwise error message
   */
  protected String renameRemote(String source, String target) {
    SmbFile	file;

    try {
      file = new SmbFile(source, m_Provider.getAuthentication());
      file.renameTo(new SmbFile(target, m_Provider.getAuthentication()));
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to rename file: " + source + " -> " + target, e);
    }

    return null;
  }

  /**
   * Deletes a remote file.
   *
   * @param file	the file to delete
   * @return		null if successful, otherwise error message
   */
  protected String deleteRemote(String file) {
    SmbFile 	smbfile;

    try {
      smbfile = new SmbFile(file, m_Provider.getAuthentication());
      smbfile.delete();
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to delete file: " + file, e);
    }

    return null;
  }

  /**
   * Creates the remote directory.
   *
   * @param dir		the directory to create
   * @return		null if successful, otherwise error message
   */
  protected String mkdirRemote(String dir) {
    SmbFile 	smbfile;

    try {
      smbfile = new SmbFile(dir, m_Provider.getAuthentication());
      smbfile.mkdirs();
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to create directory: " + dir, e);
    }

    return null;
  }
}
