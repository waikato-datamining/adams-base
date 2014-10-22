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
 * EncloseActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.AbstractDisplay;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHandler;
import adams.flow.core.MutableActorHandler;
import adams.flow.sink.DisplayPanelManager;
import adams.flow.sink.DisplayPanelProvider;
import adams.gui.core.BaseMenu;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;

/**
 * For enclosing the actors in an actor handler.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class EncloseActor
  extends AbstractTreePopupMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Enclose";
  }

  /**
   * Encloses the currently selected actors in the specified actor handler.
   *
   * @param paths	the (paths to the) actors to wrap in the control actor
   * @param handler	the handler to use
   */
  protected void encloseActor(TreePath[] paths, ActorHandler handler) {
    AbstractActor[]	currActor;
    Node		parent;
    Node 		currNode;
    Node		newNode;
    int			index;
    String		msg;
    MutableActorHandler	mutable;
    int			i;
    String		newName;

    parent    = null;
    currActor = new AbstractActor[paths.length];
    for (i = 0; i < paths.length; i++) {
      currNode     = TreeHelper.pathToNode(paths[i]);
      currActor[i] = currNode.getFullActor().shallowCopy();
      if (parent == null)
	parent = (Node) currNode.getParent();

      if (ActorUtils.isStandalone(currActor[i])) {
	if (!handler.getActorHandlerInfo().canContainStandalones()) {
	  GUIHelper.showErrorMessage(
	      m_State.tree,
	      "You cannot enclose a standalone actor in a "
	      + handler.getClass().getSimpleName() + "!");
	  return;
	}
      }
    }

    // enter new name
    newName = handler.getName();
    if ((parent.getActor() instanceof CallableActorHandler) && (currActor.length == 1))
      newName = currActor[0].getName();
    newName = JOptionPane.showInputDialog(GUIHelper.getParentComponent(m_State.tree), "Please enter name for enclosing actor (leave empty for default):", newName);
    if (newName == null)
      return;
    if (newName.isEmpty())
      newName = handler.getDefaultName();
    handler.setName(newName);

    if (paths.length == 1)
      addUndoPoint("Enclosing node '" + TreeHelper.pathToActor(paths[0]).getFullName() + "' in " + handler.getClass().getName());
    else
      addUndoPoint("Enclosing " + paths.length + " nodes in " + handler.getClass().getName());

    try {
      if (handler instanceof MutableActorHandler) {
	mutable = (MutableActorHandler) handler;
	mutable.removeAll();
	for (i = 0; i < currActor.length; i++)
	  mutable.add(i, currActor[i]);
      }
      else {
	handler.set(0, currActor[0]);
      }
      newNode = m_State.tree.buildTree(null, (AbstractActor) handler, false);
      for (i = 0; i < paths.length; i++) {
	currNode = TreeHelper.pathToNode(paths[i]);
	index    = parent.getIndex(currNode);
	parent.remove(index);
	if (i == 0)
	  parent.insert(newNode, index);
      }
      m_State.tree.updateActorName(newNode);
      m_State.tree.setModified(true);
      if (paths.length == 1) {
	m_State.tree.nodeStructureChanged(newNode);
	m_State.tree.expand(newNode);
	m_State.tree.locateAndDisplay(newNode.getFullName());
	m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, newNode, Type.MODIFY));
      }
      else {
	m_State.tree.nodeStructureChanged(parent);
	m_State.tree.expand(parent);
	m_State.tree.locateAndDisplay(parent.getFullName());
	m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, parent, Type.MODIFY));
      }
      m_State.tree.redraw();
    }
    catch (Exception e) {
      if (paths.length == 1)
	msg = "Failed to enclose actor '" + TreeHelper.pathToActor(paths[0]).getFullName() + "'";
      else
	msg = "Failed to enclose " + paths.length + " actors";
      msg += " in a " + handler.getClass().getSimpleName() + ": ";
      ConsolePanel.getSingleton().append(OutputType.ERROR, msg + "\n" + Utils.throwableToString(e));
      GUIHelper.showErrorMessage(
	  m_State.tree, msg + "\n" + e.getMessage());
    }
  }

  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    BaseMenu	result;
    JMenuItem	menuitem;
    String[]	actors;
    int		i;
    
    result = new BaseMenu(getName());
    result.setEnabled(isEnabled());
    result.setIcon(getIcon());

    actors = ClassLister.getSingleton().getClassnames(ActorHandler.class);
    for (i = 0; i < actors.length; i++) {
      final ActorHandler actor = (ActorHandler) AbstractActor.forName(actors[i], new String[0]);
      if (!actor.getActorHandlerInfo().canEncloseActors())
	continue;
      if (actor instanceof Flow)
	continue;
      if ((m_State.selPaths != null) && (m_State.selPaths.length > 1) && (!(actor instanceof MutableActorHandler)))
	continue;
      menuitem = new JMenuItem(actor.getClass().getSimpleName());
      result.add(menuitem);
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  encloseActor(m_State.selPaths, actor);
	}
      });
    }
    result.sort();

    if (m_State.isSingleSel && (m_State.selNode.getActor() instanceof DisplayPanelProvider)) {
      result.addSeparator();
      menuitem = new JMenuItem(DisplayPanelManager.class.getSimpleName());
      result.add(menuitem);
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  encloseInDisplayPanelManager(m_State.selPaths[0]);
	}
      });
    }
    
    return result;
  }

  /**
   * Encloses the specified actor in a DisplayPanelManager actor.
   *
   * @param path	the path of the actor to enclose
   */
  protected void encloseInDisplayPanelManager(TreePath path) {
    AbstractActor	currActor;
    Node		currNode;
    DisplayPanelManager	manager;
    AbstractDisplay	display;
    List<TreePath>	exp;

    currNode  = TreeHelper.pathToNode(path);
    currActor = currNode.getFullActor().shallowCopy();
    manager   = new DisplayPanelManager();
    manager.setName(currActor.getName());
    manager.setPanelProvider((DisplayPanelProvider) currActor);
    if (currActor instanceof AbstractDisplay) {
      display = (AbstractDisplay) currActor;
      manager.setWidth(display.getWidth() + 100);
      manager.setHeight(display.getHeight());
      manager.setX(display.getX());
      manager.setY(display.getY());
    }

    addUndoPoint("Enclosing node '" + currNode.getActor().getFullName() + "' in " + manager.getClass().getName());

    exp = m_State.tree.getExpandedNodes();
    currNode.setActor(manager);
    m_State.tree.setModified(true);
    m_State.tree.nodeStructureChanged((Node) currNode.getParent());
    m_State.tree.notifyActorChangeListeners(new ActorChangeEvent(m_State.tree, currNode, Type.MODIFY));
    m_State.tree.setExpandedNodes(exp);
    m_State.tree.expand(currNode);
    m_State.tree.locateAndDisplay(currNode.getFullName());
    m_State.tree.redraw();
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && (m_State.parent != null) && (m_State.numSel > 0));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    // obsolete
  }
}
