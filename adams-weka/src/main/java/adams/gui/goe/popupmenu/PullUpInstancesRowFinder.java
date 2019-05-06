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
 * PullUpInstancesRowFinder.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.data.weka.rowfinder.AbstractFilteredRowFinder;
import adams.gui.goe.GenericObjectEditorPopupMenu;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;

/**
 * Pulls up the base Instances RowFinder from a filtered RowFinder.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PullUpInstancesRowFinder
  extends AbstractGenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = 1425725787002837224L;

  /**
   * The name used for sorting.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Pull up row finder";
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
    return (editor.getValue() instanceof AbstractFilteredRowFinder);
  }

  /**
   * Customizes the GOE popup menu.
   *
   * @param menu	the menu to customize
   * @param editor	the current editor
   * @param comp	the GUI context
   */
  @Override
  protected void doCustomize(GenericObjectEditorPopupMenu menu, final PropertyEditor editor, JComponent comp) {
    JMenuItem   menuitem;

    menuitem = new JMenuItem(getName());
    menuitem.addActionListener((ActionEvent e) -> {
      AbstractFilteredRowFinder finder = (AbstractFilteredRowFinder) editor.getValue();
      editor.setValue(finder.getRowFinder());
    });
    menu.add(menuitem);
  }
}
