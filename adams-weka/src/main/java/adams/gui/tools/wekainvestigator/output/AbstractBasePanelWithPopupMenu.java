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
 * AbstractBasePanelWithPopupMenu.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.PopupMenuCustomizer;
import adams.gui.core.PopupMenuProvider;

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
public abstract class AbstractBasePanelWithPopupMenu<T extends BaseFileChooser>
  extends BasePanel
  implements PopupMenuProvider {

  private static final long serialVersionUID = -2818808520522758309L;

  /** the filechooser. */
  protected T m_FileChooser;

  /** the optional popup menu customizer. */
  protected PopupMenuCustomizer<AbstractBasePanelWithPopupMenu<T>> m_PopupMenuCustomizer;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser         = createFileChooser();
    m_PopupMenuCustomizer = null;
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

  /**
   * Sets the popup customizer to use.
   *
   * @param value	the customizer, can be null
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer<AbstractBasePanelWithPopupMenu<T>> value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the current popup customizer in use.
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer<AbstractBasePanelWithPopupMenu<T>> getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Returns the popup menu.
   */
  @Override
  public JPopupMenu getPopupMenu() {
    JPopupMenu		result;
    JMenuItem		menuitem;

    result = new JPopupMenu();

    menuitem = new JMenuItem("Save...", GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener((ActionEvent e) -> save());
    result.add(menuitem);

    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(this, result);

    return result;
  }
}
