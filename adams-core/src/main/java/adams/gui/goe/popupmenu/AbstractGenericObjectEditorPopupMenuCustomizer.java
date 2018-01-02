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
 * AbstractGenericObjectEditorPopupMenuCustomizer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.core.logging.LoggingObject;
import adams.gui.goe.GenericObjectEditorPopupMenu;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import java.beans.PropertyEditor;

/**
 * Ancestor for classes that customize the GOE popup menu.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractGenericObjectEditorPopupMenuCustomizer
  extends LoggingObject
  implements GenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = 8656456412857517697L;

  /**
   * Customizes the GOE popup menu.
   *
   * @param menu	the menu to customize
   * @param editor	the current editor
   * @param comp	the GUI context
   */
  protected abstract boolean handles(GenericObjectEditorPopupMenu menu, PropertyEditor editor, JComponent comp);

  /**
   * Adds a separator if necessary.
   *
   * @param menu	the menu to add the separator to
   */
  protected void addSeparator(GenericObjectEditorPopupMenu menu) {
    if (menu.getComponentCount() == 0) {
      menu.addSeparator();
    }
    else {
      if (!(menu.getComponent() instanceof JSeparator))
	menu.addSeparator();
    }
  }

  /**
   * Customizes the GOE popup menu.
   *
   * @param menu	the menu to customize
   * @param editor	the current editor
   * @param comp	the GUI context
   */
  protected abstract void doCustomize(GenericObjectEditorPopupMenu menu, PropertyEditor editor, JComponent comp);

  /**
   * Customizes the GOE popup menu.
   *
   * @param menu	the menu to customize
   * @param editor	the current editor
   * @param comp	the GUI context
   */
  public void customize(GenericObjectEditorPopupMenu menu, PropertyEditor editor, JComponent comp) {
    if (handles(menu, editor, comp))
      doCustomize(menu, editor, comp);
  }
}
