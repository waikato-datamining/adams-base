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
 * SMB.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingObject;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.protocol.commons.EnumWithValue;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.utils.SmbFiles;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
   * @param share	the share to copy to
   * @param remoteFile	the remote file
   * @return		null if successful, otherwise error message
   */
  public static String copyTo(LoggingObject owner, SMBSessionProvider provider, File localFile, String share, String remoteFile) {
    try (DiskShare diskShare = (DiskShare) provider.getSession().connectShare(share)) {
      return copyTo(owner, provider, localFile, diskShare, remoteFile);
    }
    catch (IOException e) {
      return LoggingHelper.handleException(owner, "Failed to copy '" + localFile + "' to file '" + remoteFile + "' on share '" + share + "'!", e);
    }
  }

  /**
   * Copies a local file to a remote server.
   *
   * @param owner	the owner that initiates the transfer, can be null
   * @param provider	the SMB authentication provider to use
   * @param localFile	the local file
   * @param share	the share to copy to
   * @param remoteFile	the remote file
   * @return		null if successful, otherwise error message
   */
  public static String copyTo(LoggingObject owner, SMBSessionProvider provider, File localFile, DiskShare share, String remoteFile) {
    try {
      SmbFiles.copy(localFile.getAbsoluteFile(), share, remoteFile, true);
      return null;
    }
    catch (IOException e) {
      return LoggingHelper.handleException(owner, "Failed to copy local '" + localFile + "' to remote file '" + remoteFile + "' on share '" + share.getSmbPath() + "'!", e);
    }
  }

  /**
   * Copies a remote file onto the local machine.
   *
   * @param owner	the owner that initiates the transfer
   * @param provider	the SMB session provider to use
   * @param share	the share to copy from
   * @param remoteFile	the remote file to copy
   * @param localFile	the local file
   * @return		null if successful, otherwise error message
   */
  public static String copyFrom(LoggingObject owner, SMBSessionProvider provider, String share, String remoteFile, File localFile) {
    try (DiskShare diskShare = (DiskShare) provider.getSession().connectShare(share)) {
      return copyFrom(owner, provider, diskShare, remoteFile, localFile);
    }
    catch (IOException e) {
      return LoggingHelper.handleException(owner, "Failed to copy remote '" + remoteFile + "' from share '" + share + "' to local '" + localFile + "'!", e);
    }
  }

  /**
   * Copies a remote file onto the local machine.
   *
   * @param owner	the owner that initiates the transfer
   * @param provider	the SMB session provider to use
   * @param share	the share to copy from
   * @param remoteFile	the remote file to copy
   * @param localFile	the local file
   * @return		null if successful, otherwise error message
   */
  public static String copyFrom(LoggingObject owner, SMBSessionProvider provider, DiskShare share, String remoteFile, File localFile) {
    InputStream 			is;
    FileOutputStream 			fos;
    BufferedOutputStream 		bos;
    Set<SMB2ShareAccess> 		s;
    com.hierynomus.smbj.share.File 	file;

    is  = null;
    fos = null;
    bos = null;
    try {
      s = new HashSet<>();
      s.add(SMB2ShareAccess.FILE_SHARE_READ);
      file = share.openFile(remoteFile, EnumSet.of(AccessMask.GENERIC_READ), null, s, null, null);
      is   = file.getInputStream();
      fos  = new FileOutputStream(localFile.getAbsoluteFile());
      bos  = new BufferedOutputStream(fos);
      IOUtils.copy(is, bos);
      return null;
    }
    catch (IOException e) {
      return LoggingHelper.handleException(owner, "Failed to copy remote '" + remoteFile + "' from share '" + share + "' to local '" + localFile + "'!", e);
    }
    finally {
      FileUtils.closeQuietly(is);
      FileUtils.closeQuietly(bos);
      FileUtils.closeQuietly(fos);
    }
  }

  /**
   * Checks whether the file object is a directory.
   *
   * @param file	the file object to check
   * @return		true if directory
   */
  public static boolean isDirectory(FileIdBothDirectoryInformation file) {
    return EnumWithValue.EnumUtils.isSet(file.getFileAttributes(), FileAttributes.FILE_ATTRIBUTE_DIRECTORY);
  }

  /**
   * Checks whether the file object is hidden.
   *
   * @param file	the file object to check
   * @return		true if hidden
   */
  public static boolean isHidden(FileIdBothDirectoryInformation file) {
    return EnumWithValue.EnumUtils.isSet(file.getFileAttributes(), FileAttributes.FILE_ATTRIBUTE_HIDDEN);
  }

  /**
   * Splits the path into its individual elements.
   *
   * @param path	the path to process
   * @return		the path elements
   */
  public static String[] splitPath(String path) {
    List<String>	result;

    result = new ArrayList<>(Arrays.asList(path.split("/")));
    Utils.removeEmptyLines(result, false);

    return result.toArray(new String[0]);
  }

  /**
   * Creates a new path (with leading /).
   *
   * @param elements	the path elements to join
   * @param isDir 	whether the path represents a directory and requires a trailing /
   * @return		the generated path
   */
  public static String joinPath(String[] elements, boolean isDir) {
    StringBuilder	result;

    result = new StringBuilder();

    for (String element: elements) {
      result.append("/");
      result.append(element);
    }

    if (isDir)
      result.append("/");

    if (result.length() == 0)
      result.append("/");

    return result.toString();
  }

  /**
   * Returns the parent path.
   *
   * @param path	the path to get the parent for
   * @return		the parent path
   */
  public static String getParent(String path) {
    return joinPath(getParent(splitPath(path)), true);
  }

  /**
   * Returns the parent path.
   *
   * @param elements	the path elements to get the parent for
   * @return		the parent path elements
   */
  public static String[] getParent(String[] elements) {
    List<String>	result;

    result = new ArrayList<>(Arrays.asList(elements));
    if (!result.isEmpty())
      result.remove(result.size() - 1);

    return result.toArray(new String[0]);
  }

  /**
   * Ensures that the path starts and ends with /.
   *
   * @param path	the path to process
   * @return		the (potentially) fixed path
   */
  public static String fixDir(String path) {
    String	result;

    result = path;

    if (!result.startsWith("/"))
      result = "/" + result;
    if (!result.endsWith("/"))
      result += "/";

    return result;
  }

  /**
   * Ensures that the path ends with /.
   *
   * @param path	the path to process
   * @return		the (potentially) fixed path
   */
  public static String fixSubDir(String path) {
    String	result;

    result = path;

    if (!result.endsWith("/"))
      result += "/";

    return result;
  }
}
