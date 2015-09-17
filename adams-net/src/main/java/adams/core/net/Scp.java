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
 * Scp.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingObject;
import adams.flow.standalone.SSHConnection;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for SCP actions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Scp {

  /**
   * Copies a local file to a remote server.
   *
   * @param owner	the owner that initiates the transfer, can be null
   * @param conn	the SSH connection to use
   * @param localFile	the local file
   * @param remoteFile	the remote file
   * @return		null if successful, otherwise error message
   */
  public static String copyTo(LoggingObject owner, SSHConnection conn, File localFile, String remoteFile) {
    return copyTo(owner, conn, null, -1, localFile, remoteFile);
  }

  /**
   * Copies a local file to a remote server.
   *
   * @param owner	the owner that initiates the transfer, can be null
   * @param conn	the SSH connection to use
   * @param host	an alternative host, null if to use one from connection
   * @param port	an alternative port, ignored if host null
   * @param localFile	the local file
   * @param remoteFile	the remote file
   * @return		null if successful, otherwise error message
   */
  public static String copyTo(LoggingObject owner, SSHConnection conn, String host, int port, File localFile, String remoteFile) {
    String		result;
    Session		session;
    ChannelExec		channel;
    OutputStream 	out;
    InputStream 	in;
    byte[]		buffer;
    String		command;
    long 		filesize;
    FileInputStream	fis;
    int			len;

    result  = null;
    session = null;
    channel = null;
    try {
      if (host == null) {
	channel = (ChannelExec) conn.getSession().openChannel("exec");
      }
      else {
	session = conn.newSession(host, port);
	channel = (ChannelExec) session.openChannel("exec");
      }
      channel.setCommand("scp -p -t " + remoteFile);
      if ((owner != null) && owner.isLoggingEnabled())
	owner.getLogger().info("Uploading " + localFile + " to " + remoteFile);
      in     = channel.getInputStream();
      out    = channel.getOutputStream();

      channel.connect();

      if (SSHConnection.checkAck(in) != 0) {
	result = "Input stream check failed after opening channel!";
	return result;
      }

      // send "C0644 filesize filename", where filename should not include '/'
      filesize = localFile.length();
      command  = "C0644 " + filesize + " " + localFile.getName() + "\n";
      out.write(command.getBytes());
      out.flush();
      if (SSHConnection.checkAck(in) != 0)
	result = "Sending of filename failed!";

      // send a content of lfile
      fis    = new FileInputStream(localFile.getAbsoluteFile());
      buffer = new byte[1024];
      while (true) {
	len = fis.read(buffer, 0, buffer.length);
	if (len <= 0)
	  break;
	out.write(buffer, 0, len);
      }
      FileUtils.closeQuietly(fis);
      fis = null;

      // send '\0'
      buffer[0]=0;
      out.write(buffer, 0, 1);
      out.flush();

      if (SSHConnection.checkAck(in) != 0)
	result = "Left-over data in input stream!";
      FileUtils.closeQuietly(out);
    }
    catch (Exception e) {
      result = Utils.handleException(owner, "Failed to upload file '" + localFile + "' to '" + remoteFile + "': ", e);
    }
    finally {
      if (channel != null) {
	channel.disconnect();
      }
    }

    if (session != null) {
      if (session.isConnected()) {
        try {
          session.disconnect();
        }
        catch (Exception e) {
          Utils.handleException(owner, "Failed to disconnect from '" + host + "':", e);
        }
      }
    }

    return result;
  }

  /**
   * Copies a remote file onto the local machine.
   *
   * @param owner	the owner that initiates the transfer
   * @param conn	the SSH connection to use
   * @param remoteFile	the remote file to copy
   * @param localFile	the local file
   * @return		null if successful, otherwise error message
   */
  public static String copyFrom(LoggingObject owner, SSHConnection conn, String remoteFile, File localFile) {
    return copyFrom(owner, conn, null, -1, remoteFile, localFile);
  }

  /**
   * Copies a remote file onto the local machine.
   *
   * @param owner	the owner that initiates the transfer
   * @param conn	the SSH connection to use
   * @param host	an alternative host, null if to use one from connection
   * @param port	an alternative port, ignored if host null
   * @param remoteFile	the remote file to copy
   * @param localFile	the local file
   * @return		null if successful, otherwise error message
   */
  public static String copyFrom(LoggingObject owner, SSHConnection conn, String host, int port, String remoteFile, File localFile) {
    String		result;
    Session		session;
    ChannelExec		channel;
    OutputStream	out;
    InputStream		in;
    byte[]		buffer;
    int			c;
    long 		filesize;
    FileOutputStream	fos;
    int 		foo;
    int			i;

    result  = null;
    session = null;
    channel = null;
    fos     = null;
    try {
      if (host == null) {
	channel = (ChannelExec) conn.getSession().openChannel("exec");
      }
      else {
	session = conn.newSession(host, port);
	channel = (ChannelExec) session.openChannel("exec");
      }
      channel.setCommand("scp -f " + remoteFile);
      if ((owner != null) && owner.isLoggingEnabled())
	owner.getLogger().info("Downloading " + remoteFile);
      in     = channel.getInputStream();
      out    = channel.getOutputStream();
      buffer = new byte[1024];

      channel.connect();

      // send '\0'
      buffer[0] = 0;
      out.write(buffer, 0, 1);
      out.flush();

      while (true) {
	c = SSHConnection.checkAck(in);
        if (c != 'C')
	  break;

        // read '0644 '
        in.read(buffer, 0, 5);

        filesize = 0L;
        while(true){
          // error?
          if (in.read(buffer, 0, 1) < 0)
            break;
          if (buffer[0]== ' ')
            break;
          filesize = filesize * 10L + (long) (buffer[0] - '0');
        }

        for (i = 0; ; i++) {
          in.read(buffer, i, 1);
          if(buffer[i] == (byte) 0x0a)
            break;
        }

        // send '\0'
        buffer[0] = 0;
        out.write(buffer, 0, 1);
        out.flush();

        // read a content of lfile
        fos = new FileOutputStream(localFile.getAbsolutePath());
        while(true){
          if (buffer.length < filesize)
            foo = buffer.length;
	  else
	    foo = (int) filesize;
          foo = in.read(buffer, 0, foo);
          // error
          if (foo < 0)
            break;
          fos.write(buffer, 0, foo);
          filesize -= foo;
          if (filesize == 0L)
            break;
        }
        FileUtils.closeQuietly(fos);
        fos = null;

	if (SSHConnection.checkAck(in) != 0)
	  result = "Error occurred!";

        // send '\0'
        buffer[0] = 0;
        out.write(buffer, 0, 1);
        out.flush();
      }
    }
    catch (Exception e) {
      result = Utils.handleException(owner, "Failed to download file '" + remoteFile + "' to '" + localFile + "': ", e);
    }
    finally {
      FileUtils.closeQuietly(fos);
      if (channel != null) {
	channel.disconnect();
      }
    }

    if (session != null) {
      if (session.isConnected()) {
        try {
          session.disconnect();
        }
        catch (Exception e) {
          Utils.handleException(owner, "Failed to disconnect from '" + host + "':", e);
        }
      }
    }

    return result;
  }
}
