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
 * ChooseClass.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditor.GOEPanel;
import adams.gui.goe.GenericObjectEditorPopupMenu;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;

/**
 * TODO: What class does.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ChooseClass
  extends AbstractGenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = 3433842531685529617L;

  /**
   * The name used for sorting.
   *
   * @return		the name
   */
  public String getName() {
    return "choose";
  }

  /**
   * Customizes the GOE popup menu.
   *
   * @param menu	the menu to customize
   * @param editor	the current editor
   * @param comp	the GUI context
   */
  @Override
  protected boolean handles(GenericObjectEditorPopupMenu menu, PropertyEditor editor, JComponent comp) {
    return (comp instanceof GOEPanel)
      && (editor instanceof GenericObjectEditor)
      && ((GenericObjectEditor) editor).getCanChangeClassInDialog();
  }

  /**
   * Customizes the GOE popup menu.
   *
   * @param menu	the menu to customize
   * @param editor	the current editor
   * @param comp	the GUI context
   */
  @Override
  protected void doCustomize(GenericObjectEditorPopupMenu menu, PropertyEditor editor, JComponent comp) {
    JMenuItem 	item;

    item = new JMenuItem("Choose...", GUIHelper.getIcon("tree.gif"));
    item.addActionListener((ActionEvent ae) -> ((GOEPanel) editor.getCustomEditor()).getChooseButton().doClick());
    menu.insert(new JPopupMenu.Separator(), 0);
    menu.insert(item, 0);
  }
}
