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
 * SwapActor.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.core.optiontransfer.AbstractOptionTransfer;
import adams.flow.core.Actor;
import adams.gui.core.BaseMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuItemComparator;
import adams.gui.flow.tree.actorswap.AbstractActorSwapSuggestion;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditorPopupMenu;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows the swapping of an actor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SwapActor
  extends AbstractGenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = -3626038863078370162L;

  /**
   * The name used for sorting.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "swap actor";
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
    return (editor instanceof GenericObjectEditor) && (((GenericObjectEditor) editor).getClassType() == Actor.class);
  }

  /**
   * Performs the swap.
   *
   * @param editor	the property editor
   * @param source	the current actor
   * @param target	the actor to replace the current one with
   */
  protected void swapActor(PropertyEditor editor, Actor source, Actor target) {
    List<AbstractOptionTransfer>	transfers;

    transfers = AbstractOptionTransfer.getTransfers(source, target);
    for (AbstractOptionTransfer transfer: transfers)
      transfer.transfer(source, target);

    editor.setValue(target);
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
    BaseMenu 		submenu;
    JMenuItem 		menuitem;
    int			i;
    List<JMenuItem>	menuitems;
    List<Actor> 	suggestions;
    final Actor		current;

    current     = (Actor) editor.getValue();
    suggestions = AbstractActorSwapSuggestion.suggestAll(current);
    menuitems   = new ArrayList<>();
    for (i = 0; i < suggestions.size(); i++) {
      final Actor target = suggestions.get(i);
      if (current.getClass().getSimpleName().equals(target.getClass().getSimpleName()))
        menuitem = new JMenuItem(target.getClass().getSimpleName() + " (" + target.getClass().getPackage().getName() + ")");
      else
        menuitem = new JMenuItem(target.getClass().getSimpleName());
      menuitem.setIcon(GUIHelper.getIcon(target.getClass()));
      menuitems.add(menuitem);
      menuitem.addActionListener((ActionEvent e) -> swapActor(editor, current, target));
    }
    Collections.sort(menuitems, new MenuItemComparator());
    submenu = BaseMenu.createCascadingMenu(menuitems, -1, "More...");
    submenu.setText("Swap actor");
    submenu.setEnabled(true);
    submenu.setIcon(GUIHelper.getIcon("swap.gif"));
    menu.add(submenu);
  }
}
