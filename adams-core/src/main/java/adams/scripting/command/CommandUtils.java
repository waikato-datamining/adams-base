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
import org.apache.commons.codec.binary.Base64;

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

  /** the width in characters for the base64 encoded payload. */
  public static final int PAYLOAD_WIDTH = 72;

  public static final String MESSAGE_CHARSET = "US-ASCII";

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
   * Turns the command properties and payload into a single string to send.
   *
   * @param header	the header data
   * @param payload	the payload
   * @return		the assembled string
   */
  public static String commandToString(Properties header, byte[] payload) {
    StringBuilder	result;
    String		data;

    if (payload.length == 0)
      data = "";
    else
      data = Base64.encodeBase64String(payload);

    // command string
    result = new StringBuilder();
    result.append(header.toComment());
    result.append(Utils.flatten(Utils.breakUp(data, PAYLOAD_WIDTH), "\n"));

    return result.toString();
  }
}
