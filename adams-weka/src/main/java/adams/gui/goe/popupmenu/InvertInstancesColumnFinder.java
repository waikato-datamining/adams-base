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
 * InvertInstancesColumnFinder.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.data.weka.columnfinder.ColumnFinder;
import adams.data.weka.columnfinder.Invert;
import adams.gui.goe.GenericObjectEditorPopupMenu;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;

/**
 * Encloses a Instances ColumnFinder in Invert.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InvertInstancesColumnFinder
  extends AbstractGenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = 1425725787002837224L;

  /**
   * The name used for sorting.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Invert column finder";
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
    return (editor.getValue() instanceof ColumnFinder);
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
      ColumnFinder finder = (ColumnFinder) editor.getValue();
      Invert inv = new Invert();
      inv.setColumnFinder(finder);
      editor.setValue(inv);
    });
    menu.add(menuitem);
  }
}
