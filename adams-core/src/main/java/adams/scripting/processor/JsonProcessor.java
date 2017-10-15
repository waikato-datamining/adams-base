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
 * JsonProcessor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.processor;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.GzipUtils;
import adams.core.io.PrettyPrintingSupporter;
import adams.core.option.OptionUtils;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.util.List;

/**
 * Processor for remote commands in JSON format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JsonProcessor
  extends AbstractRemoteCommandProcessor
  implements PrettyPrintingSupporter {

  private static final long serialVersionUID = -7804545376269832263L;

  /** the char set for the messages. */
  public final static String MESSAGE_CHARSET = "UTF-8";

  /** whether to use pretty printing. */
  protected boolean m_PrettyPrinting;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses JSON for processing remote commands.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "pretty-printing", "prettyPrinting",
      false);
  }

  /**
   * Sets whether to use pretty-printing or not.
   *
   * @param value	true if to use pretty-printing
   */
  public void setPrettyPrinting(boolean value) {
    m_PrettyPrinting = value;
    reset();
  }

  /**
   * Returns whether pretty-printing is used or not.
   *
   * @return		true if to use pretty-printing
   */
  public boolean getPrettyPrinting() {
    return m_PrettyPrinting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prettyPrintingTipText() {
    return "If enabled, the JSON message are pretty-printed rather than optimized for size.";
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
    JSONObject 		json;
    JSONObject		header;
    Properties		props;
    byte[] 		payload;
    String		cmd;

    try {
      json = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(data);
    }
    catch (Exception e) {
      errors.add("Failed to parse: " + data, e);
      return null;
    }

    payload = new byte[0];
    if (json.containsKey("payload"))
      payload = Base64.decodeBase64(json.getAsString("payload").getBytes());
    if (payload.length > 0) {
      payload = GzipUtils.decompress(payload, 1024);
      if (payload == null) {
        errors.add("Failed to decompress payload!");
        payload = new byte[0];
      }
    }

    if (!json.containsKey("header")) {
      errors.add("Header missing!");
      return null;
    }
    header = (JSONObject) json.get("header");
    cmd    = header.getAsString(RemoteCommand.KEY_COMMAND);
    if ((cmd == null) || cmd.isEmpty()) {
      errors.add("No command present in content, failed to parse!");
      return null;
    }
    props = new Properties();
    for (String key: header.keySet())
      props.setProperty(key, header.getAsString(key));

    // instantiate command
    try {
      result = (RemoteCommand) OptionUtils.forCommandLine(RemoteCommand.class, cmd);
      result.parse(props);
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
    JsonObject 		result;
    String		data;
    JsonObject		objHeader;
    Gson 		gson;

    if (payload.length == 0)
      data = "";
    else
      data = Base64.encodeBase64String(payload);

    objHeader  = new JsonObject();
    for (String key: header.keySetAll())
      objHeader.addProperty(key, header.getProperty(key));

    result = new JsonObject();
    result.add("header",  objHeader);
    result.addProperty("payload", data);

    if (m_PrettyPrinting) {
      gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(result);
    }
    else {
      return result.toString();
    }
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
