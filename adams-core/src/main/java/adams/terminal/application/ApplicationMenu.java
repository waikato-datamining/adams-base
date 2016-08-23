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
 * ApplicationMenu.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.terminal.application;

import adams.core.ClassLister;
import adams.core.logging.LoggingObject;
import adams.terminal.menu.AbstractMenuItemDefinition;
import adams.terminal.menu.ProgramExit;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Generates the menu for the terminal application.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ApplicationMenu
  extends LoggingObject
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 7821913342345552227L;

  /** the parent application frame. */
  protected AbstractTerminalApplication m_Owner;

  /**
   * Initializes menu generator.
   *
   * @param owner	the owning application frame
   */
  public ApplicationMenu(AbstractTerminalApplication owner) {
    super();

    m_Owner = owner;
  }

  /**
   * Returns the owning frame.
   *
   * @return		the owner
   */
  public AbstractTerminalApplication getOwner() {
    return m_Owner;
  }

  /**
   * Generates the menu and returns it.
   *
   * @param context	the context to use
   * @return		the menu bar
   */
  public Panel getMenuBar(final WindowBasedTextGUI context) {
    Panel						result;
    Class[]						classes;
    AbstractMenuItemDefinition 				definition;
    Map<String,List<AbstractMenuItemDefinition>> 	menus;
    List<AbstractMenuItemDefinition>			menuitems;
    List<String>					categories;
    ActionListBox					actionsProgram;
    ActionListBox					actionsHelp;

    result = new Panel();
    result.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

    // collect menu items
    classes = ClassLister.getSingleton().getClasses(AbstractMenuItemDefinition.class);
    menus   = new HashMap<>();
    for (Class cls: classes) {
      try {
	definition = (AbstractMenuItemDefinition) cls.newInstance();
	definition.setOwner(getOwner());
	if (!menus.containsKey(definition.getCategory()))
	  menus.put(definition.getCategory(), new ArrayList<>());
	menus.get(definition.getCategory()).add(definition);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to create new instance of: " + cls.getName(), e);
      }
    }
    for (String key: menus.keySet())
      Collections.sort(menus.get(key));

    // build menu
    // first menu is "Program"
    menuitems      = menus.get(AbstractMenuItemDefinition.CATEGORY_PROGRAM);
    actionsProgram = new ActionListBox();
    for (AbstractMenuItemDefinition def: menuitems) {
      // Exit is always last
      if (def.getClass() == ProgramExit.class)
	continue;
      actionsProgram.addItem(def.getTitle(), def.getRunnable(context));
    }
    definition = new ProgramExit();
    definition.setOwner(getOwner());
    actionsProgram.addItem(definition.getTitle(), definition.getRunnable(context));
    result.addComponent(new Button(AbstractMenuItemDefinition.CATEGORY_PROGRAM, () -> {
      actionsProgram.addTo(result);
    }));

    // other categories
    categories = new ArrayList<>(menus.keySet());
    categories.remove(AbstractMenuItemDefinition.CATEGORY_PROGRAM);
    categories.remove(AbstractMenuItemDefinition.CATEGORY_HELP);
    for (String category : categories) {
      menuitems = menus.get(category);
      final ActionListBox actions = new ActionListBox();
      for (AbstractMenuItemDefinition def: menuitems)
	actions.addItem(def.getTitle(), def.getRunnable(context));
      result.addComponent(new Button(category, () -> {
	actions.addTo(result);
      }));
    }

    // last menu is "Help"
    menuitems   = menus.get(AbstractMenuItemDefinition.CATEGORY_HELP);
    actionsHelp = new ActionListBox();
    for (AbstractMenuItemDefinition def: menuitems)
      actionsHelp.addItem(def.getTitle(), def.getRunnable(context));
    result.addComponent(new Button(AbstractMenuItemDefinition.CATEGORY_HELP, () -> {
      actionsHelp.addTo(result);
    }));

    return result;
  }
}
