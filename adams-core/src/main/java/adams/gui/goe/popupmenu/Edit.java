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
 * Edit.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.gui.core.ImageManager;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.goe.GenericObjectEditorPopupMenu;
import adams.gui.goe.PropertyPanel;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;

/**
 * Edits the item.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Edit
  extends AbstractGenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = -3815928335494719525L;

  /**
   * The name used for sorting.
   *
   * @return		the name
   */
  public String getName() {
    return "edit";
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
    return (comp instanceof PropertyPanel) || (comp instanceof GenericObjectEditorPanel);
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

    if (comp instanceof PropertyPanel) {
      item = new JMenuItem("Edit...", ImageManager.getIcon("properties.gif"));
      item.addActionListener((ActionEvent e) -> ((PropertyPanel) comp).showPropertyDialog());
      menu.insert(new JPopupMenu.Separator(), 0);
      menu.insert(item, 0);
    }
    else if (comp instanceof GenericObjectEditorPanel) {
      if (((GenericObjectEditorPanel) comp).isEditable())
	item = new JMenuItem("Edit...", ImageManager.getIcon("properties.gif"));
      else
	item = new JMenuItem("Show...", ImageManager.getIcon("properties.gif"));
      item.addActionListener((ActionEvent e) -> ((GenericObjectEditorPanel) comp).choose());
      menu.insert(new JPopupMenu.Separator(), 0);
      menu.insert(item, 0);
    }
  }
}
