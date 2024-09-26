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
 * DefaultProcessor.java
 * Copyright (C) 2017-2024 University of Waikato, Hamilton, NZ
 */

package adams.scripting.processor;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.GzipUtils;
import adams.core.option.OptionUtils;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Processors for simple remote command format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultProcessor
  extends AbstractRemoteCommandProcessor {

  private static final long serialVersionUID = -7804545376269832263L;

  /** the width in characters for the base64 encoded payload. */
  public static final int PAYLOAD_WIDTH = 72;

  public static final String MESSAGE_CHARSET = "US-ASCII";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the simple format for processing remote commands.";
  }

  /**
   * Instantiates the command from the received data string.
   *
   * @param data	the data string to parse
   * @param errors	for collecting errors
   * @return		the instantiated command, null if failed to parse
   */
  @Override
  public RemoteCommand parse(String data, MessageCollection errors) {
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
    payload = Base64.getDecoder().decode((Utils.flatten(payloadLines, "").getBytes()));
    if (payload.length > 0) {
      payload = GzipUtils.decompress(payload, 1024);
      if (payload == null) {
        errors.add("Failed to decompress payload!");
        payload = new byte[0];
      }
    }

    cmd = header.getProperty(RemoteCommand.KEY_COMMAND, "");
    if (cmd.isEmpty()) {
      errors.add("No command present in content, failed to parse!");
      return null;
    }

    // instantiate command
    try {
      result = (RemoteCommand) OptionUtils.forCommandLine(RemoteCommand.class, cmd);
      result.parse(header);
      if (result.isRequest()) {
        result.setRequestPayload(payload);
      }
      else {
        if (result instanceof RemoteCommandWithResponse)
          ((RemoteCommandWithResponse) result).setResponsePayload(payload);
        else
          errors.add(
            "Command flagged as response but does not implement "
              + RemoteCommandWithResponse.class.getName() + ": " + cmd);
      }
    }
    catch (Exception e) {
      errors.add("Failed to instantiate commandline: " + cmd, e);
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
  @Override
  public String format(Properties header, byte[] payload) {
    StringBuilder	result;
    String		data;

    if (payload.length == 0)
      data = "";
    else
      data = Base64.getEncoder().encodeToString(payload);

    // command string
    result = new StringBuilder();
    result.append(header.toComment(true));
    result.append(Utils.flatten(Utils.breakUp(data, PAYLOAD_WIDTH), "\n"));

    return result.toString();
  }

  /**
   * Reads a remote command from a file.
   *
   * @param file	the file to read
   * @param errors	for collecting errors
   * @return		the remote command, null if failed to load
   */
  @Override
  public RemoteCommand read(File file, MessageCollection errors) {
    RemoteCommand	cmd;
    List<String>	lines;
    String		data;

    cmd   = null;
    data  = null;
    lines = FileUtils.loadFromFile(file, MESSAGE_CHARSET);
    if (lines == null)
      errors.add("Failed to read data from remote command file: " + file);
    else
      data = Utils.flatten(lines, "\n");
    if (data != null) {
      cmd = parse(data, errors);
      if (cmd == null)
	errors.add("Failed to parse remote command from data:\n" + data);
    }

    return cmd;
  }

  /**
   * Writes a remote command to a file.
   *
   * @param cmd		the command to write
   * @param file	the file to write to
   * @param errors	for collecting errors
   * @return		true if successful
   */
  @Override
  public boolean write(RemoteCommand cmd, File file, MessageCollection errors) {
    String	data;
    String	msg;

    data = null;
    if (cmd.isRequest()) {
      data = cmd.assembleRequest(this);
    }
    else {
      if (cmd instanceof RemoteCommandWithResponse)
	data = ((RemoteCommandWithResponse) cmd).assembleResponse(this);
      else
	errors.add("Remote command is not a response but flagged as such:\n" + cmd.toString());
    }

    if (errors.isEmpty()) {
      msg = FileUtils.writeToFileMsg(file.getAbsolutePath(), data, false, MESSAGE_CHARSET);
      if (msg != null)
	errors.add(msg);
    }

    return errors.isEmpty();
  }
}
