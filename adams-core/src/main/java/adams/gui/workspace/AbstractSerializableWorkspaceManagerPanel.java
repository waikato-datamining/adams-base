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
 * AbstractSerializableWorkspaceManagerPanel.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.workspace;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.data.io.input.AbstractObjectReader;
import adams.data.io.output.AbstractObjectWriter;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.SerializationFileChooser;
import adams.gui.core.GUIHelper;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Ancestor for manager panels that allow saving/loading of workspaces.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSerializableWorkspaceManagerPanel<P extends AbstractWorkspacePanel>
  extends AbstractWorkspaceManagerPanel<P> {

  /** for serialization. */
  private static final long serialVersionUID = -20320489406680254L;

  /** the button for copying a panel. */
  protected JButton m_ButtonCopy;

  /** the button for managing the workspaces. */
  protected JButton m_ButtonWorkspace;

  /** the workspace helper. */
  protected AbstractWorkspaceHelper<P, AbstractSerializableWorkspaceManagerPanel<P>> m_WorkspaceHelper;

  /** the file chooser for the workspaces. */
  protected SerializationFileChooser m_WorkspaceFileChooser;
  
  @Override
  protected void initialize() {
    super.initialize();

    m_WorkspaceHelper      = newWorkspaceHelper();
    m_WorkspaceFileChooser = m_WorkspaceHelper.newFileChooser();
  }
  
  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    int		height;

    super.initGUI();

    height = m_ButtonAdd.getHeight();

    // left buttons
    m_ButtonCopy = new JButton(GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.setSize(height, height);
    m_ButtonCopy.setToolTipText("Creates a copy of the current workspace");
    m_ButtonCopy.addActionListener((ActionEvent e) -> copyWorkspace());
    m_PanelButtons.add(m_ButtonCopy);

    m_ButtonWorkspace = new JButton(GUIHelper.getIcon("workspace.png"));
    m_ButtonWorkspace.setSize(height, height);
    m_ButtonWorkspace.setToolTipText("Loading/saving of workspaces");
    m_ButtonWorkspace.addActionListener((ActionEvent e) -> {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem menuitem;

      // load workspace
      menuitem = new JMenuItem("Open...");
      menuitem.addActionListener((ActionEvent ae) -> GUIHelper.doInBackground(() -> openWorkspace()));
      menu.add(menuitem);

      // save workspace
      menuitem = new JMenuItem("Save as...");
      menuitem.addActionListener((ActionEvent ae) -> GUIHelper.doInBackground(() -> saveWorkspace()));
      menu.add(menuitem);

      // show menu
      menu.show(m_ButtonWorkspace, 0, m_ButtonWorkspace.getHeight());
    });
    m_PanelButtons.add(m_ButtonWorkspace);
  }

  /**
   * Returns a new instance of the workspace helper to use.
   *
   * @return		the workspace helper
   */
  protected abstract AbstractWorkspaceHelper<P, AbstractSerializableWorkspaceManagerPanel<P>> newWorkspaceHelper();

  /**
   * Copies a workspace.
   */
  protected abstract void copyWorkspace();

  /**
   * Opens a workspace.
   */
  protected void openWorkspace() {
    int	 			retVal;
    File 			file;
    AbstractObjectReader	reader;
    MessageCollection		errors;
    
    retVal = m_WorkspaceFileChooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;
    
    file   = m_WorkspaceFileChooser.getSelectedFile();
    reader = m_WorkspaceFileChooser.getReader();
    errors = new MessageCollection();
    try {
      m_WorkspaceHelper.read(file, reader, this, errors);
      if (!errors.isEmpty())
	GUIHelper.showErrorMessage(
	  this,
	  "Failed to open workspace '" + file + "'!\n" + errors);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  this, 
	  "Failed to open workspace '" + file + "'!\n" + Utils.throwableToString(e));
    }
  }
  
  /**
   * Saves the current workspace.
   */
  public void saveWorkspace() {
    int		 		retVal;
    File 			file;
    AbstractObjectWriter	writer;
    
    retVal = m_WorkspaceFileChooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    file   = m_WorkspaceFileChooser.getSelectedFile();
    writer = m_WorkspaceFileChooser.getWriter();
    try {
      m_WorkspaceHelper.write(this, file, writer);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  this, 
	  "Failed to save workspace to '" + file + "'!\n" + Utils.throwableToString(e));
    }
  }

  /**
   * Updates the buttons.
   */
  protected void updateButtons() {
    super.updateButtons();
    m_ButtonCopy.setEnabled(m_History.getSelectedIndices().length == 1);
  }
}
