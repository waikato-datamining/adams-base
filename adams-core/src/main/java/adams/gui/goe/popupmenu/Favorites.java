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
 * Favorites.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.gui.goe.Favorites.FavoriteSelectionEvent;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.goe.GenericObjectEditorPopupMenu;

import javax.swing.JComponent;
import java.beans.PropertyEditor;

/**
 * Adds the favorites sub-menu.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Favorites
  extends AbstractGenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = -681726804679815767L;

  /**
   * The name used for sorting.
   *
   * @return		the name
   */
  public String getName() {
    return "favorites";
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
    boolean	result;

    result = (editor instanceof GenericObjectEditor);
    if (comp instanceof GenericObjectEditorPanel) {
      if (!((GenericObjectEditorPanel) comp).isEditable())
	result = false;
    }

    return result;
  }

  /**
   * Customizes the GOE popup menu.
   *
   * @param menu	the menu to customize
   * @param editor	the current editor
   * @param comp	the GUI context
   */
  @Override
  protected void doCustomize(final GenericObjectEditorPopupMenu menu, final PropertyEditor editor, final JComponent comp) {
    addSeparator(menu);
    adams.gui.goe.Favorites.getSingleton().customizePopupMenu(
      menu,
      ((GenericObjectEditor) editor).getClassType(),
      editor.getValue(),
      (FavoriteSelectionEvent fe) -> editor.setValue(fe.getFavorite().getObject()));
  }
}
