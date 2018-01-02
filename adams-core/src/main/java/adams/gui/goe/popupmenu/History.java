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
 * History.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.gui.event.HistorySelectionEvent;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.goe.GenericObjectEditorPopupMenu;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyEditor;

/**
 * Adds history support.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class History
  extends AbstractGenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = -3815928335494719525L;

  /**
   * The name used for sorting.
   *
   * @return		the name
   */
  public String getName() {
    return "history";
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
    return (comp instanceof GenericObjectEditorPanel);
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
    final GenericObjectEditorPanel	panel;

    panel = ((GenericObjectEditorPanel) comp);
    panel.getHistory().customizePopupMenu(
      menu,
      panel.getCurrent(),
      (HistorySelectionEvent e) -> {
	panel.setCurrent(e.getHistoryItem());
	panel.notifyChangeListeners(new ChangeEvent(panel));
      });
  }
}
