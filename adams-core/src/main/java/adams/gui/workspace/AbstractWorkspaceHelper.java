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

import adams.core.ClassLocator;
import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Helper class for loading/saving workspaces.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWorkspaceHelper<P extends AbstractWorkspacePanel, M extends AbstractWorkspaceManagerPanel<P>, H extends SerializablePanelHandler<P>> {

  /**
   * the additional associations between panels and handlers.
   */
  protected HashMap<Class, H> m_AdditionalHandlers;

  /**
   * Initializes the helper.
   */
  protected AbstractWorkspaceHelper() {
    super();
    m_AdditionalHandlers = new HashMap<>();
  }

  /**
   * Registers an additional handler for a panel.
   *
   * @param panel the panel to register the handler for
   * @param handler       the handler to register
   */
  public void registerAdditionalHandler(Class panel, H handler) {
    if (!ClassLocator.isSubclass(AbstractWorkspacePanel.class, panel))
      throw new IllegalArgumentException(
	"Panel class '" + panel.getName() + "' does not implement '" + AbstractWorkspacePanel.class.getName() + "'!");
    m_AdditionalHandlers.put(panel, handler);
  }

  /**
   * Returns all available handlers. If a default handler is available, this should be listed last.
   *
   * @throws Exception if instantiation of handlers fails
   * @return the handlers
   */
  protected abstract H[] getHandlers() throws Exception;

  /**
   * Serializes the panel instance to the output stream.
   *
   * @param panel	the panel to serialize
   * @param name	the name of the panel
   * @param oos		the output stream
   * @throws Exception	if serialization fails
   */
  protected abstract void serialize(P panel, String name, ObjectOutputStream oos) throws Exception;

  /**
   * Saves the panel session to the given file.
   *
   * @param manager the panel to save
   * @param file     the file to save the workspace to
   * @throws Exception if saving fails
   */
  public void write(M manager, File file) throws Exception {
    ObjectOutputStream 	oos;
    FileOutputStream 	fos;
    int 		i;
    P 			panel;
    String 		name;

    fos = new FileOutputStream(file);
    oos = new ObjectOutputStream(new BufferedOutputStream(fos));

    oos.writeObject(manager.getEntryPanel().count());
    for (i = 0; i < manager.getEntryPanel().count(); i++) {
      name = manager.getEntryPanel().getEntryName(i);
      panel = (P) manager.getEntryPanel().getEntry(i);
      serialize(panel, name, oos);
    }

    FileUtils.closeQuietly(oos);
    FileUtils.closeQuietly(fos);
  }

  /**
   * Deserializes an panel instance from the input stream.
   *
   * @param ois		the input stream to read
   * @return		the name (= 0) and the panel instance (= 1)
   * @throws Exception	if deserialization fails
   */
  protected abstract Object[] deserialize(ObjectInputStream ois) throws Exception;

  /**
   * Reads the session and initializes the manager panel.
   *
   * @param file	the file to load the session from
   * @param manager	the manager panel to initialize with the session
   * @throws Exception 	if loading fails
   */
  public void read(File file, M manager) throws Exception {
    ObjectInputStream 	ois;
    FileInputStream 	fis;
    int 		i;
    P 			panel;
    String 		name;
    int 		panelCount;
    Object[] 		objs;

    fis = new FileInputStream(file);
    ois = new ObjectInputStream(new BufferedInputStream(fis));

    manager.getEntryPanel().clear();
    panelCount = (Integer) ois.readObject();
    for (i = 0; i < panelCount; i++) {
      objs = deserialize(ois);
      name   = (String) objs[0];
      panel = (P) objs[1];
      manager.addPanel(panel, name);
    }

    FileUtils.closeQuietly(ois);
    FileUtils.closeQuietly(fis);
  }

  /**
   * Copies a panel.
   *
   * @param panel	the panel to copy
   * @return		the copy
   * @throws Exception	if copying fails
   */
  public P copy(P panel) throws Exception {
    Object[] 			objs;
    ByteArrayOutputStream	bos;
    ObjectOutputStream		oos;
    byte[]			data;
    ByteArrayInputStream	bis;
    ObjectInputStream		ois;

    bos = new ByteArrayOutputStream();
    oos = new ObjectOutputStream(bos);
    serialize(panel, "dummy", oos);
    data = bos.toByteArray();

    bis = new ByteArrayInputStream(data);
    ois = new ObjectInputStream(bis);
    objs = deserialize(ois);

    return (P) objs[1];
  }

  /**
   * Creates a filechooser for loading/saving workspaces.
   *
   * @return the filechooser
   */
  public abstract BaseFileChooser newFileChooser();
}
