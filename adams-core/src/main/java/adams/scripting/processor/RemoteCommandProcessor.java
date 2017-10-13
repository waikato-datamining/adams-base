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
 * RemoteCommandProcessor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.processor;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.scripting.command.RemoteCommand;

import java.io.File;

/**
 * Interface for command processors, classes that parse and format commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface RemoteCommandProcessor {

  /**
   * Instantiates the command from the received data string.
   *
   * @param data	the data string to parse
   * @param errors	for collecting errors
   * @return		the instantiated command, null if failed to parse
   */
  public RemoteCommand parse(String data, MessageCollection errors);

  /**
   * Turns the command properties and payload into a single string to send.
   *
   * @param header	the header data
   * @param payload	the payload
   * @return		the assembled string
   */
  public String format(Properties header, byte[] payload);

  /**
   * Reads a remote command from a file.
   *
   * @param file	the file to read
   * @param errors	for collecting errors
   * @return		the remote command, null if failed to load
   */
  public RemoteCommand read(File file, MessageCollection errors);

  /**
   * Writes a remote command to a file.
   *
   * @param cmd		the command to write
   * @param file	the file to write to
   * @param errors	for collecting errors
   * @return		true if successful
   */
  public boolean write(RemoteCommand cmd, File file, MessageCollection errors);
}
