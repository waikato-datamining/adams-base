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
 * SMB.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingObject;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Class for SMB actions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SMB {

  /**
   * Copies a local file to a remote server.
   *
   * @param owner	the owner that initiates the transfer, can be null
   * @param provider	the SMB authentication provider to use
   * @param localFile	the local file
   * @param remoteFile	the remote file
   * @return		null if successful, otherwise error message
   */
  public static String copyTo(LoggingObject owner, SMBAuthenticationProvider provider, File localFile, String remoteFile) {
    String			result;
    NtlmPasswordAuthentication	auth;
    SmbFileOutputStream		sos;
    FileInputStream		fis;
    BufferedInputStream		bis;
    byte[]			buffer;
    int				read;

    result = null;
    auth   = provider.getAuthentication();
    sos    = null;
    fis    = null;
    bis    = null;
    buffer = new byte[8192];
    try {
      sos = new SmbFileOutputStream(new SmbFile(remoteFile, auth));
      fis = new FileInputStream(localFile.getAbsolutePath());
      bis = new BufferedInputStream(fis);
      while ((read = bis.read(buffer)) != -1)
        sos.write(buffer, 0, read);
    }
    catch (Exception e) {
      result = Utils.handleException(
        owner, "Failed to upload file '" + localFile + "' to '" + remoteFile + "': ", e);
    }
    finally {
      FileUtils.closeQuietly(sos);
      FileUtils.closeQuietly(bis);
      FileUtils.closeQuietly(fis);
    }

    return result;
  }

  /**
   * Copies a remote file onto the local machine.
   *
   * @param owner	the owner that initiates the transfer
   * @param provider	the SSH session provider to use
   * @param remoteFile	the remote file to copy
   * @param localFile	the local file
   * @return		null if successful, otherwise error message
   */
  public static String copyFrom(LoggingObject owner, SMBAuthenticationProvider provider, String remoteFile, File localFile) {
    String			result;
    NtlmPasswordAuthentication	auth;
    byte[]			buffer;
    int				read;
    SmbFileInputStream		sis;
    FileOutputStream		fos;
    BufferedOutputStream	bos;

    result = null;
    auth   = provider.getAuthentication();
    sis    = null;
    fos    = null;
    bos    = null;
    buffer = new byte[8192];
    try {
      sis = new SmbFileInputStream(new SmbFile(remoteFile, auth));
      fos = new FileOutputStream(localFile.getAbsolutePath());
      bos = new BufferedOutputStream(fos);
      while ((read = sis.read(buffer)) != -1)
	bos.write(buffer, 0, read);
    }
    catch (Exception e) {
      result = Utils.handleException(
        owner,
        "Failed to download file '" + remoteFile + "' to '" + localFile + "': ", e);
    }
    finally {
      FileUtils.closeQuietly(sis);
      FileUtils.closeQuietly(bos);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }
}
