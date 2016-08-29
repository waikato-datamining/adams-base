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
 * AbstractOutputPanelWithPopupMenu.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.DetachablePanel;
import adams.gui.core.GUIHelper;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Ancestor for output panels that can save the displayed output to a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOutputPanelWithPopupMenu<T extends BaseFileChooser>
  extends DetachablePanel {

  private static final long serialVersionUID = -2818808520522758309L;

  /** the filechooser. */
  protected T m_FileChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = createFileChooser();
  }

  /**
   * Creates the filechooser to use.
   *
   * @return		the filechooser
   */
  protected abstract T createFileChooser();

  /**
   * Saves the content to the specified file.
   *
   * @param file	the file to save to
   * @return		null if successful, otherwise error message
   */
  protected abstract String save(File file);

  /**
   * Pops up the filechooser.
   */
  protected void save() {
    int		retVal;
    String	msg;

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    msg = save(m_FileChooser.getSelectedFile());
    if (msg != null)
      GUIHelper.showErrorMessage(this, "Failed to save content to: " + m_FileChooser.getSelectedFile() + "\n" + msg);
  }

  @Override
  protected JPopupMenu createPopupMenu() {
    JPopupMenu		result;
    JMenuItem		menuitem;

    result = super.createPopupMenu();

    menuitem = new JMenuItem("Save...", GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener((ActionEvent e) -> save());
    result.add(menuitem);

    return result;
  }
}
