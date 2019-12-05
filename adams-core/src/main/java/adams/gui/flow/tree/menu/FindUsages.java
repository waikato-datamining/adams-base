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
 * FindUsages.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.core.Actor;
import adams.flow.core.ActorReferenceHandler;
import adams.flow.processor.ActorLocationsPanel;
import adams.flow.processor.ListActorReferenceUsage;
import adams.flow.processor.ListAllStorageNames;
import adams.flow.processor.ListAllVariables;
import adams.flow.processor.ListStorageUsage;
import adams.flow.processor.ListVariableUsage;
import adams.gui.action.AbstractPropertiesAction;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tabhandler.GraphicalActorProcessorHandler;
import adams.gui.flow.tree.Node;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.tree.TreeNode;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Finds usages of callable actors, variables and storage items.
 * 
 * @author fracpete
 */
public class FindUsages
  extends AbstractTreePopupSubMenuAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Find usages";
  }

  /**
   * Returns the actor reference, if applicable.
   *
   * @param actor	the current actor
   * @param parent	the parent of the actor, can be null
   * @return		the actor reference, null if not applicable
   */
  protected String getActorReference(Actor actor, Actor parent) {
    if (((parent != null) && (parent instanceof ActorReferenceHandler)))
      return actor.getName();
    else
      return null;
  }

  /**
   * Tries to locate the variable names in the actor's options.
   *
   * @param actor	the actor to search
   * @return		the variable names, empty if none found
   */
  protected List<String> findVariableNames(Actor actor) {
    List<String>	result;
    ListAllVariables	list;

    list = new ListAllVariables();
    list.process(actor);
    result = list.getVariables();

    if (result.size() > 1)
      Collections.sort(result);

    return result;
  }

  /**
   * Tries to locate the storage name in the actor's options.
   *
   * @param actor	the actor to search
   * @return		the storage name, empty if none found
   */
  protected List<String> findStorageNames(Actor actor) {
    List<String>	result;
    ListAllStorageNames	list;

    list = new ListAllStorageNames();
    list.process(actor);
    result = list.getStorageNames();

    if (result.size() > 1)
      Collections.sort(result);

    return result;
  }

  /**
   * Locates all the occurrences of this actor.
   *
   * @param actor	the actor to look for
   * @return		the actor paths
   */
  protected List<String> findActorLocations(Actor actor) {
    List<String>		result;
    Enumeration<TreeNode> 	nodes;
    Node 			node;

    result = new ArrayList<>();
    nodes  = m_State.tree.getRootNode().depthFirstEnumeration();
    while (nodes.hasMoreElements()) {
      node = (Node) nodes.nextElement();
      if (node.getActor().getClass().equals(actor.getClass())) {
        result.add(node.getFullName());
      }
    }

    return result;
  }

  /**
   * Ignored.
   *
   * @return		always null
   */
  @Override
  protected AbstractPropertiesAction[] getSubMenuActions() {
    return null;
  }

  /**
   * Creates a menu item for a actor reference.
   *
   * @param reference	the name of the referenced actor
   * @return		the menu item
   */
  protected JMenuItem createActorReferenceMenuItem(final String reference) {
    JMenuItem 	result;

    result = new JMenuItem(reference);
    result.addActionListener((ActionEvent e) -> {
      ListActorReferenceUsage processor = new ListActorReferenceUsage();
      processor.setName(reference);
      m_State.tree.getOwner().processActors(processor);
    });

    return result;
  }

  /**
   * Creates a menu item for a variable.
   *
   * @param variable	the name of the variable
   * @return		the menu item
   */
  protected JMenuItem createVariableMenuItem(final String variable) {
    JMenuItem 	result;

    result = new JMenuItem(variable);
    result.addActionListener((ActionEvent e) -> {
      ListVariableUsage processor = new ListVariableUsage();
      processor.setName(variable);
      m_State.tree.getOwner().processActors(processor);
    });

    return result;
  }

  /**
   * Creates a menu item for a storage item.
   *
   * @param storage	the name of the storage item
   * @return		the menu item
   */
  protected JMenuItem createStorageMenuItem(final String storage) {
    JMenuItem 	result;

    result = new JMenuItem(storage);
    result.addActionListener((ActionEvent e) -> {
      ListStorageUsage processor = new ListStorageUsage();
      processor.setName(storage);
      m_State.tree.getOwner().processActors(processor);
    });

    return result;
  }

  /**
   * Creates a menu item for a jumping to an actor location.
   *
   * @param locations	the locations of the actor
   * @return		the menu item
   */
  protected JMenuItem createActorLocationMenuItem(List<String> locations) {
    JMenuItem 				result;

    result  = new JMenuItem("Actor locations");
    result.addActionListener((ActionEvent e) -> {
      GraphicalActorProcessorHandler handler = m_State.tree.getOwner().getTabHandler(GraphicalActorProcessorHandler.class);
      if (handler != null) {
	ActorLocationsPanel panel = new ActorLocationsPanel(m_State.tree, locations);
	handler.add("Actor locations", panel, null);
      }
    });

    return result;
  }

  /**
   * Creates a new menu.
   */
  @Override
  public JMenu createMenu() {
    JMenu 		result;
    JMenu		submenu;
    Actor		parent;
    Actor 		actor;
    String 		reference;
    List<String>	vars;
    List<String>	items;
    List<String>	locations;
    int			count;

    if (m_State.selNode == null)
      return null;

    result = new JMenu(getName());
    if (getIcon() != null)
      result.setIcon(getIcon());
    else
      result.setIcon(GUIHelper.getEmptyIcon());
    actor  = m_State.selNode.getActor();
    parent = (m_State.parent != null) ? m_State.parent.getActor() : null;

    reference = getActorReference(actor, parent);
    vars      = findVariableNames(actor);
    items     = findStorageNames(actor);
    locations = findActorLocations(actor);

    // do we need a submenu?
    count = 0;
    if (reference != null)
      count++;
    if (vars.size() > 0)
      count++;
    if (items.size() > 0)
      count++;
    if (locations.size() > 0)
      count++;

    if (count > 1) {
      if (reference != null) {
	submenu = new JMenu("Actor reference");
	result.add(submenu);
	submenu.add(createActorReferenceMenuItem(reference));
      }
      if (locations.size() > 0)
	result.add(createActorLocationMenuItem(locations));
      if (items.size() > 0) {
	submenu = new JMenu("Storage item");
	result.add(submenu);
	for (String item: items)
	  submenu.add(createStorageMenuItem(item));
      }
      if (vars.size() > 0) {
	submenu = new JMenu("Variable");
	result.add(submenu);
	for (String var: vars)
	  submenu.add(createVariableMenuItem(var));
      }
    }
    else {
      if (reference != null) {
	result.add(createActorReferenceMenuItem(reference));
      }
      if (locations.size() > 0)
	result.add(createActorLocationMenuItem(locations));
      if (items.size() > 0) {
	for (String item: items)
	  result.add(createStorageMenuItem(item));
      }
      if (vars.size() > 0) {
	for (String var: vars)
	  result.add(createVariableMenuItem(var));
      }
    }

    result.setEnabled(result.getItemCount() > 0);

    return result;
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    boolean	enabled;
    Actor	parent;
    Actor 	actor;

    enabled = false;
    if (m_State.isSingleSel) {
      actor   = m_State.selNode.getActor();
      parent  = (m_State.parent != null) ? m_State.parent.getActor() : null;
      enabled = (getActorReference(actor, parent) != null)
	|| (findVariableNames(actor).size() > 0)
	|| (findStorageNames(actor).size() > 0);
    }

    setEnabled(enabled);
  }
}
