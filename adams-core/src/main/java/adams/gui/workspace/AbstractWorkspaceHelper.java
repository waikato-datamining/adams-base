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
 * AbstractWorkspaceHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.workspace;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractObjectReader;
import adams.data.io.output.AbstractObjectWriter;
import adams.gui.chooser.SerializationFileChooser;

import java.io.File;

/**
 * Helper class for loading/saving workspaces.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWorkspaceHelper<P extends AbstractWorkspacePanel, M extends AbstractWorkspaceManagerPanel<P>> {

  /**
   * Returns the object that represents the panel's serialized state.
   *
   * @param panel	the panel to serialize
   * @return		the object to serialize
   * @throws Exception	if serialization fails
   */
  protected abstract Object serialize(P panel) throws Exception;

  /**
   * Saves the panel session to the given file.
   *
   * @param manager 	the panel to save
   * @param file     	the file to save the workspace to
   * @return		null if successful, otherwise error message
   * @throws Exception 	if serialization/saving fails
   */
  public String write(M manager, File file, AbstractObjectWriter writer) throws Exception {
    int 		i;
    Object[][]		data;

    data = new Object[manager.getEntryPanel().count()][2];
    for (i = 0; i < manager.getEntryPanel().count(); i++) {
      data[i][0] = manager.getEntryPanel().getEntryName(i);
      data[i][1] = serialize((P) manager.getEntryPanel().getEntry(i));
    }

    return writer.write(new PlaceholderFile(file), data);
  }

  /**
   * Deserializes the data for the panel.
   *
   * @param panel	the panel to populate
   * @param data	the data to deserialize
   * @param errors	for storing errors
   * @throws Exception	if deserialization fails
   */
  protected abstract void deserialize(P panel, Object data, MessageCollection errors) throws Exception;

  /**
   * Reads the session and initializes the manager panel.
   *
   * @param file	the file to load the session from
   * @param reader	the reader to use for loading the serialized file
   * @param manager	the manager panel to initialize with the session
   * @param errors	for storing errors
   * @throws Exception 	if loading fails
   */
  public void read(File file, AbstractObjectReader reader, M manager, MessageCollection errors) throws Exception {
    int 		i;
    P 			panel;
    String 		name;
    Object[][] 		data;

    data = (Object[][]) reader.read(new PlaceholderFile(file));
    if (data == null) {
      errors.add("Failed to read data from: " + file);
      return;
    }

    manager.getEntryPanel().clear();
    for (i = 0; i < data.length; i++) {
      name  = (String) data[i][0];
      panel = manager.newWorkspace(false);
      deserialize(panel, data[i][1], errors);
      manager.getEntryPanel().addEntry(name, panel);
    }
  }

  /**
   * Copies a panel.
   *
   * @param manager	the manager panel to use
   * @param panel	the panel to copy
   * @param errors	for storing errors
   * @return		the copy
   * @throws Exception	if copying fails
   */
  public P copy(M manager, P panel, MessageCollection errors) throws Exception {
    P		result;
    Object	data;

    data = Utils.deepCopy(serialize(panel), true);
    if (data == null)
      throw new IllegalStateException("Failed to copy workspace!");
    result = manager.newWorkspace(false);
    deserialize(result, data, errors);

    return result;
  }

  /**
   * Creates a filechooser for loading/saving workspaces.
   *
   * @return the filechooser
   */
  public SerializationFileChooser newFileChooser() {
    return new SerializationFileChooser();
  }
}
