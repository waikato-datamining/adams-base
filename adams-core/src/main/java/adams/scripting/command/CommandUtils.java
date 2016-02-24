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
 * CommandUtils.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.logging.LoggingObject;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import org.apache.commons.codec.binary.Base64;

import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Utility functions for remote commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CommandUtils {

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param cmd		the command to send
   * @param host	the host to send the command to
   * @param port	the host port
   * @param request	whether Request or Response
   * @return		null if successfully sent, otherwise error message
   */
  protected static String send(RemoteCommand cmd, String host, int port, boolean request) {
    String	result;
    String	data;
    Socket 	socket;

    result = null;
    if (request)
      data = cmd.assembleRequest();
    else
      data = ((RemoteCommandWithResponse) cmd).assembleResponse();
    try {
      socket = new Socket(host, port);
      socket.getOutputStream().write(data.getBytes(Charset.forName("US-ASCII")));
      socket.getOutputStream().flush();
      socket.close();
    }
    catch (Exception e) {
      result = Utils.handleException(
	cmd, "Failed to send " + (request ? "request" : "response") + " to " + host + ":" + port, e);
    }

    return result;
  }

  /**
   * Sends the command (request) to the specified sscripting engine.
   *
   * @param cmd		the command to send
   * @param host	the host to send the command to
   * @param port	the host port
   * @return		null if successfully sent, otherwise error message
   */
  public static String sendRequest(RemoteCommand cmd, String host, int port) {
    return send(cmd, host, port, true);
  }

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param host	the host to send the command to
   * @param port	the host port
   * @return		null if successfully sent, otherwise error message
   */
  public static String sendResponse(RemoteCommand cmd, String host, int port) {
    return send(cmd, host, port, false);
  }

  /**
   * Instantiates the command from the received data string.
   *
   * @param owner	the optional owner, for logging purposes
   * @param data	the data string to parse
   * @return		the instantiated command, null if failed to parse
   */
  public static RemoteCommand parse(LoggingObject owner, String data) {
    RemoteCommand	result;
    List<String> headerLines;
    Properties header;
    List<String> 	payloadLines;
    String[]		lines;
    boolean		start;
    byte[] 		payload;
    String		cmd;

    lines        = Utils.split(data, "\n");
    headerLines  = new ArrayList<>();
    payloadLines = new ArrayList<>();
    start        = true;
    for (String line: lines) {
      if (start && line.startsWith(Properties.COMMENT)) {
	headerLines.add(line);
      }
      else {
	payloadLines.add(line);
	start = false;
      }
    }

    header = Properties.fromComment(Utils.flatten(headerLines, "\n"));
    // compression needs to be handle by individual commands
    payload = Base64.decodeBase64((Utils.flatten(payloadLines, "").getBytes()));

    cmd = header.getProperty(RemoteCommand.KEY_COMMAND, "");
    if (cmd.isEmpty()) {
      if (owner != null)
	owner.getLogger().severe("No command present in content, failed to parse!");
      return null;
    }

    // instantiate command
    try {
      result = (RemoteCommand) OptionUtils.forCommandLine(RemoteCommand.class, cmd);
      result.parse(header);
      result.setPayload(payload);
    }
    catch (Exception e) {
      if (owner != null)
	owner.getLogger().log(Level.SEVERE, "Failed to instantiate commandline: " + cmd, e);
      result = null;
    }

    return result;
  }

  /**
   * For testing commands from the commandline. Parameters:
   * <host> <port> <request:true|false> <cmd> [<options>]
   *
   * @param args	the commandline arguments
   * @throws Exception	if instantiation of command fails
   */
  public static void main(String[] args) throws Exception {
    String 		host;
    int 		port;
    boolean		request;
    String 		cname;
    RemoteCommand 	cmd;
    String		msg;

    if (args.length < 4) {
      System.err.println("Usage: <host> <port> <request:true|false> <cmd> [<options>]");
      return;
    }

    Environment.setEnvironmentClass(Environment.class);

    host    = args[0];
    port    = Integer.parseInt(args[1]);
    request = Boolean.parseBoolean(args[2]);
    cname   = args[3];
    args[0] = "";
    args[1] = "";
    args[2] = "";
    args[3] = "";
    cmd = (RemoteCommand) OptionUtils.forName(RemoteCommand.class, cname, args);
    if (cmd == null) {
      System.err.println("Failed to instantiate command!");
      return;
    }
    msg = send(cmd, host, port, request);
    if (msg != null)
      System.err.println(msg);
  }
}
