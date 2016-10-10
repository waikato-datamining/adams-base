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
 * InvestigatorWorkspaceHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator;

import adams.core.MessageCollection;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.workspace.AbstractSerializableWorkspaceManagerPanel;
import adams.gui.workspace.AbstractWorkspaceHelper;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Helper class for Weka Investigator workspaces.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorWorkspaceHelper
  extends AbstractWorkspaceHelper<InvestigatorPanel, AbstractSerializableWorkspaceManagerPanel<InvestigatorPanel, InvestigatorPanelHandler>, InvestigatorPanelHandler> {

  /**
   * Returns all available handlers. If a default handler is available, this should be listed last.
   *
   * @throws Exception if instantiation of handlers fails
   * @return the handlers
   */
  @Override
  protected InvestigatorPanelHandler[] getHandlers() throws Exception {
    return new InvestigatorPanelHandler[]{new InvestigatorPanelHandler()};
  }

  /**
   * Serializes the panel instance to the output stream.
   *
   * @param panel	the panel to serialize
   * @param name	the name of the panel
   * @param oos		the output stream
   * @throws Exception	if serialization fails
   */
  @Override
  protected void serialize(InvestigatorPanel panel, String name, ObjectOutputStream oos) throws Exception {
    InvestigatorPanelHandler	handler;

    handler = new InvestigatorPanelHandler();

    // name
    oos.writeUTF(name);

    // panel
    oos.writeObject(handler.serialize(panel));
  }

  /**
   * Deserializes an panel instance from the input stream.
   *
   * @param ois		the input stream to read
   * @param errors	for storing errors
   * @return		the name (= 0) and the panel instance (= 1)
   * @throws Exception	if deserialization fails
   */
  @Override
  protected Object[] deserialize(ObjectInputStream ois, MessageCollection errors) throws Exception {
    Object[]			result;
    InvestigatorPanelHandler	handler;

    result  = new Object[2];
    handler = new InvestigatorPanelHandler();

    // name
    result[0] = ois.readUTF();

    // panel
    result[1] = new InvestigatorPanel();
    handler.deserialize((InvestigatorPanel) result[1], ois.readObject(), errors);

    return result;
  }

  /**
   * Creates a filechooser for loading/saving workspaces.
   *
   * @return the filechooser
   */
  @Override
  public BaseFileChooser newFileChooser() {
    BaseFileChooser	result;
    ExtensionFileFilter	filter;

    result = new BaseFileChooser();
    filter = new ExtensionFileFilter("Weka Investigator workspace", "wiws");
    result.addChoosableFileFilter(filter);
    result.setFileFilter(filter);
    result.setAutoAppendExtension(true);

    return result;
  }
}
