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
 * TreeOperations.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree;

import adams.core.CleanUpHandler;
import adams.core.Utils;
import adams.core.io.FlowFile;
import adams.core.option.NestedConsumer;
import adams.core.optiontransfer.AbstractOptionTransfer;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.condition.bool.Expression;
import adams.flow.control.Flow;
import adams.flow.core.AbstractCallableActor;
import adams.flow.core.AbstractDisplay;
import adams.flow.core.AbstractExternalActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.ActorWithConditionalEquivalent;
import adams.flow.core.ActorWithTimedEquivalent;
import adams.flow.core.CallableActorHandler;
import adams.flow.core.CallableActorReference;
import adams.flow.core.ExternalActorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.processor.AbstractActorProcessor;
import adams.flow.processor.GraphicalOutputProducingProcessor;
import adams.flow.processor.ModifyingProcessor;
import adams.flow.processor.RemoveDisabledActors;
import adams.flow.sink.CallableSink;
import adams.flow.sink.DisplayPanelManager;
import adams.flow.sink.DisplayPanelProvider;
import adams.flow.sink.ExternalSink;
import adams.flow.source.CallableSource;
import adams.flow.source.ExternalSource;
import adams.flow.standalone.AbstractMultiView;
import adams.flow.standalone.CallableActors;
import adams.flow.standalone.ExternalStandalone;
import adams.flow.transformer.CallableTransformer;
import adams.flow.transformer.ExternalTransformer;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ErrorMessagePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.dotnotationtree.AbstractItemFilter;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.FlowEditorDialog;
import adams.gui.flow.tree.postprocessor.AbstractEditPostProcessor;
import adams.gui.goe.FlowHelper;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.goe.classtree.ActorClassTreeFilter;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.io.Serializable;
import java.util.List;

/**
 * Performs complex operations on the tree, like adding, removing, enclosing
 * actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TreeOperations
  implements Serializable, CleanUpHandler {

  private static final long serialVersionUID = -7841526414048795652L;

  /**
   * Enumeration for how to insert a node.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 12135 $
   */
  public enum InsertPosition {
    /** beneath the current path. */
    BENEATH,
    /** here at this position. */
    HERE,
    /** after this position. */
    AFTER;

  }
  /** the tree to operate on. */
  protected Tree m_Owner;

  /** the dialog for processing actors. */
  protected GenericObjectEditorDialog m_DialogProcessActors;

  /**
   * Initializes the object.
   *
   * @param owner	the tree to operate on
   */
  public TreeOperations(Tree owner) {
    super();
    m_Owner = owner;
  }

  /**
   * Returns the owning tree.
   *
   * @return		the tree
   */
  public Tree getOwner() {
    return m_Owner;
  }

  /**
   * Checks whether standalones can be placed beneath the parent actor.
   * If the actor isn't a standalone, this method returns true, of course.
   * In case the actor is a standalone and the parent doesn't allow standalones
   * to be placed, an error message pops up and informs the user.
   *
   * @param actor	the actor to place beneath the parent
   * @param parent	the parent to place the actor beneath
   * @return		true if actor can be placed, false if not
   */
  public boolean checkForStandalones(Actor actor, Node parent) {
    return checkForStandalones(new Actor[]{actor}, parent);
  }

  /**
   * Checks whether standalones can be placed beneath the parent actor.
   * If the actors contain no standalones, this method returns true, of course.
   * In case the actors contain a standalone and the parent doesn't allow standalones
   * to be placed, an error message pops up and informs the user.
   *
   * @param actors	the actors to place beneath the parent
   * @param parent	the parent to place the actor beneath
   * @return		true if actor can be placed, false if not
   */
  public boolean checkForStandalones(Actor[] actors, Node parent) {
    for (Actor actor: actors) {
      if (    ActorUtils.isStandalone(actor)
	  && (parent != null)
	  && (parent.getActor() instanceof ActorHandler)
	  && !((ActorHandler) parent.getActor()).getActorHandlerInfo().canContainStandalones()) {

	GUIHelper.showErrorMessage(
	    getOwner(), "Actor '" + parent.getFullName() + "' cannot contain standalones!");
	return false;
      }
    }

    return true;
  }

  /**
   * Configures a filter for the ClassTree.
   *
   * @param path	the path where to insert the actor
   * @param position	where to add the actor, if null "editing" an existing actor is assumed
   * @return		the configured filter
   */
  public AbstractItemFilter configureFilter(TreePath path, InsertPosition position) {
    ActorClassTreeFilter 	result;
    Actor			before;
    Actor			after;
    Actor			parent;
    Node			parentNode;
    Node			node;
    int				index;
    ActorHandlerInfo 		handlerInfo;

    result      = new ActorClassTreeFilter();
    after       = null;
    before      = null;
    parentNode  = null;
    handlerInfo = null;

    // edit/update current actor
    if (position == null) {
      node       = TreeHelper.pathToNode(path);
      parentNode = (Node) node.getParent();
      if (parentNode != null) {
	parent = parentNode.getActor();
	if (parent instanceof MutableActorHandler) {
	  handlerInfo = ((MutableActorHandler) parent).getActorHandlerInfo();
	  if (handlerInfo.getActorExecution() == ActorExecution.SEQUENTIAL) {
	    index  = parentNode.getIndex(node);
	    before = getOwner().getNearestActor(parentNode, index, false);
	    after  = getOwner().getNearestActor(parentNode, index, true);
	  }
	}
      }
    }
    // add beneath
    else if (position == InsertPosition.BENEATH) {
      parentNode = TreeHelper.pathToNode(path);
      before     = getOwner().getNearestActor(parentNode, parentNode.getChildCount(), false);
    }
    // add here
    else if (position == InsertPosition.HERE) {
      node       = TreeHelper.pathToNode(path);
      parentNode = (Node) node.getParent();
      index      = parentNode.getIndex(node);
      before     = getOwner().getNearestActor(parentNode, index, false);
      after      = node.getActor();
      if (after.getSkip())
	after = getOwner().getNearestActor(parentNode, index, true);
    }
    // add after
    else if (position == InsertPosition.AFTER) {
      node       = TreeHelper.pathToNode(path);
      parentNode = (Node) node.getParent();
      index      = parentNode.getIndex(node);
      after      = getOwner().getNearestActor(parentNode, index, true);
      before     = node.getActor();
      if (before.getSkip())
	before = getOwner().getNearestActor(parentNode, index, false);
    }

    if ((handlerInfo == null) && (parentNode != null)) {
      parent = parentNode.getActor();
      if (parent instanceof ActorHandler)
	handlerInfo = ((ActorHandler) parent).getActorHandlerInfo();
    }

    // check types
    if ((before != null) && !(before instanceof OutputProducer))
      before = null;
    if ((after != null) && !(after instanceof InputConsumer))
      after = null;

    // before/after only important for sequential execution
    if ((handlerInfo != null) && (handlerInfo.getActorExecution() != ActorExecution.SEQUENTIAL)) {
      before = null;
      after  = null;
    }

    // set constraints
    if (before != null)
      result.setAccepts(((OutputProducer) before).generates());
    else
      result.setAccepts(null);
    if (after != null)
      result.setGenerates(((InputConsumer) after).accepts());
    else
      result.setGenerates(null);

    // standalones?
    result.setStandalonesAllowed(false);
    if ((handlerInfo != null) && handlerInfo.canContainStandalones()) {
      // standalones can only be added at the start
      if ((before == null) || ActorUtils.isStandalone(before))
	result.setStandalonesAllowed(true);
    }

    // sources?
    result.setSourcesAllowed(false);
    if ((handlerInfo != null) && handlerInfo.canContainSource()) {
      // source can only be added at the start
      if ((before == null) || ActorUtils.isStandalone(before))
	result.setSourcesAllowed(true);
    }

    // restrictions?
    if ((handlerInfo != null) && handlerInfo.hasRestrictions())
      result.setRestrictions(handlerInfo.getRestrictions());

    return result;
  }

  /**
   * Brings up the GOE dialog for adding an actor if no actor supplied,
   * otherwise just adds the given actor at the position specified
   * by the path.
   *
   * @param path	the path to the actor to add the new actor sibling
   * @param actor	the actor to add, if null a GOE dialog is presented
   * @param position	where to insert the actor
   */
  public void addActor(TreePath path, Actor actor, InsertPosition position) {
    addActor(path, actor, position, false);
  }

  /**
   * Brings up the GOE dialog for adding an actor if no actor supplied,
   * otherwise just adds the given actor at the position specified
   * by the path.
   *
   * @param path	the path to the actor to add the new actor sibling
   * @param actor	the actor to add, if null a GOE dialog is presented
   * @param position	where to insert the actor
   * @param record	whether to record the addition
   */
  public void addActor(TreePath path, Actor actor, InsertPosition position, boolean record) {
    GenericObjectEditorDialog 	dialog;
    final Node			node;
    final Node			parent;
    int				index;
    Node[]			children;
    Actor[]			actors;
    String			txt;
    final List<String> exp;

    if (actor == null) {
      node = TreeHelper.pathToNode(path);
      if (position == InsertPosition.BENEATH)
	getOwner().updateCurrentEditing(node, null);
      else
	getOwner().updateCurrentEditing((Node) node.getParent(), null);
      dialog = GenericObjectEditorDialog.createDialog(getOwner());
      if (position == InsertPosition.HERE)
	dialog.setTitle("Add here...");
      else if (position == InsertPosition.AFTER)
	dialog.setTitle("Add after...");
      else if (position == InsertPosition.BENEATH)
	dialog.setTitle("Add beneath...");
      actors = suggestActors(path, position);
      dialog.getGOEEditor().setCanChangeClassInDialog(true);
      dialog.getGOEEditor().setClassType(Actor.class);
      dialog.getGOEEditor().setFilter(configureFilter(path, position));
      dialog.setProposedClasses(actors);
      if (actors != null)
	dialog.setCurrent(actors[0]);
      else
	dialog.setCurrent(null);
      dialog.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
      dialog.setVisible(true);
      getOwner().updateCurrentEditing(null, null);
      if (dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION)
        addActor(path, (Actor) dialog.getEditor().getValue(), position, record);
    }
    else {
      if (position == InsertPosition.BENEATH) {
	node = TreeHelper.pathToNode(path);

	// does actor handler allow standalones?
	if (actor instanceof ClipboardActorContainer)
	  actors = ((ClipboardActorContainer) actor).getActors();
	else
	  actors = new Actor[]{actor};
	if (actors.length < 1)
	  return;
	if (!checkForStandalones(actors, node))
	  return;

	if (actors.length == 1)
	  txt = "'" + actors[0].getName() + "'";
	else
	  txt = actors.length + " actors";
	getOwner().addUndoPoint("Adding " + txt + " to '" + node.getFullName() + "'");

	// add
	exp      = getOwner().getExpandedFullNames();
	children = TreeHelper.buildTree(node, actors, true);
	for (Node child: children)
	  getOwner().updateActorName(child);
	SwingUtilities.invokeLater(() -> {
	  getOwner().nodeStructureChanged(node);
	  getOwner().setExpandedFullNames(exp);
	  getOwner().expand(node);
	});
	SwingUtilities.invokeLater(() -> {
	  getOwner().setSelectedFullName(children[children.length - 1].getFullName());
	});

	// record
	if (getOwner().getRecordAdd() && (actors.length == 1)) {
	  ActorSuggestion.getSingleton().record(
	      children[0],
	      node,
	      position);
	}
      }
      else {
	node   = TreeHelper.pathToNode(path);
	parent = (Node) node.getParent();
	index  = node.getParent().getIndex(node);
	if (position == InsertPosition.AFTER)
	  index++;

	// does actor handler allow standalones?
	if (actor instanceof ClipboardActorContainer)
	  actors = ((ClipboardActorContainer) actor).getActors();
	else
	  actors = new Actor[]{actor};
	if (actors.length < 1)
	  return;
	if (!checkForStandalones(actors, parent))
	  return;

	if (actors.length == 1)
	  txt = "'" + actors[0].getName() + "'";
	else
	  txt = actors.length + " actors";
	if (position == InsertPosition.AFTER)
	  getOwner().addUndoPoint("Adding " + txt + " after " + ((Node) parent.getChildAt(index - 1)).getFullName() + "'");
	else
	  getOwner().addUndoPoint("Adding " + txt + " before " + ((Node) parent.getChildAt(index)).getFullName() + "'");

	// insert
	exp      = getOwner().getExpandedFullNames();
	children = TreeHelper.buildTree(node, actors, false);
	for (Node child: children) {
	  final int fIndex = index;
	  SwingUtilities.invokeLater(() -> {
	    parent.insert(child, fIndex);
	    getOwner().updateActorName(child);
	  });
	  index++;
	}
	SwingUtilities.invokeLater(() -> {
	  getOwner().nodeStructureChanged(parent);
	  getOwner().setExpandedFullNames(exp);
	});
	SwingUtilities.invokeLater(() -> {
	  getOwner().setSelectedFullName(children[children.length - 1].getFullName());
	});

	// record
	if (getOwner().getRecordAdd() && (actors.length == 1)) {
	  ActorSuggestion.getSingleton().record(
	      children[0],
	      parent,
	      position);
	}
      }

      SwingUtilities.invokeLater(() -> {
	getOwner().setModified(true);
	// notify listeners
	getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), node, Type.MODIFY));
      });
    }
  }

  /**
   * Brings up the GOE dialog for editing the selected actor.
   *
   * @param path	the path to the actor
   */
  public void editActor(TreePath path) {
    GenericObjectEditorDialog	dialog;
    Node 			currNode;
    Node			newNode;
    Node			parent;
    Actor			actor;
    Actor			actorOld;
    int				index;
    boolean			changed;
    ActorHandler		handler;
    ActorHandler		handlerOld;
    int				i;
    boolean			editable;
    final boolean[]		expanded;

    if (path == null)
      return;

    currNode = TreeHelper.pathToNode(path);
    getOwner().updateCurrentEditing((Node) currNode.getParent(), currNode);
    actorOld = currNode.getActor().shallowCopy();
    dialog   = GenericObjectEditorDialog.createDialog(getOwner());
    editable = getOwner().isEditable() && currNode.isEditable();
    if (editable)
      dialog.setTitle("Edit...");
    else
      dialog.setTitle("Show...");
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(Actor.class);
    dialog.setProposedClasses(null);
    dialog.setCurrent(currNode.getActor().shallowCopy());
    dialog.getGOEEditor().setReadOnly(!editable);
    dialog.getGOEEditor().setFilter(getOwner().getOperations().configureFilter(path, null));
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
    dialog.setVisible(true);
    getOwner().updateCurrentEditing(null, null);
    if (dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION) {
      actor = (Actor) dialog.getEditor().getValue();
      // make sure name is not empty
      if (actor.getName().length() == 0)
	actor.setName(actor.getDefaultName());
      if (actor.equals(actorOld)) {
	actorOld.destroy();
	return;
      }
      parent = (Node) currNode.getParent();

      // does parent allow singletons?
      if (!getOwner().getOperations().checkForStandalones(actor, parent))
	return;

      getOwner().addUndoPoint("Updating node '" + currNode.getFullName() + "'");

      // check whether actor class or actor structure (for ActorHandlers) has changed
      changed = (actor.getClass() != actorOld.getClass());
      if (!changed && (actor instanceof ActorHandler)) {
	handler    = (ActorHandler) actor;
	handlerOld = (ActorHandler) actorOld;
	changed    = (handler.size() != handlerOld.size());
	if (!changed) {
	  for (i = 0; i < handler.size(); i++) {
	    if (handler.get(i).getClass() != handlerOld.get(i).getClass()) {
	      changed = true;
	      break;
	    }
	  }
	}
      }

      if (changed) {
	expanded = null;
	if (parent == null) {
          getOwner().buildTree(actor);
	  currNode = (Node) getOwner().getModel().getRoot();
	}
	else {
	  newNode = TreeHelper.buildTree(null, actor, false);
	  newNode.setOwner(parent.getOwner());
	  index   = parent.getIndex(currNode);
	  parent.remove(index);
	  parent.insert(newNode, index);
	  currNode = newNode;
	}
      }
      else {
	currNode.setActor(actor);
	expanded = getOwner().getExpandedState();
      }
      getOwner().updateActorName(currNode);
      currNode.invalidateRendering();
      getOwner().redraw(currNode);
      getOwner().setModified(true);
      if (expanded != null)
	getOwner().setExpandedState(expanded);
      else
	getOwner().nodeStructureChanged(currNode);
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), currNode, Type.MODIFY));
      // update all occurrences, if necessary
      if (!getOwner().getIgnoreNameChanges())
	AbstractEditPostProcessor.apply(getOwner(), ((parent != null) ? parent.getActor() : null), actorOld, currNode.getActor());
      final Node fCurrNode = currNode;
      SwingUtilities.invokeLater(() -> {
	getOwner().locateAndDisplay(fCurrNode.getFullName());
	getOwner().refreshTabs();
      });
    }
  }

  /**
   * Renames an actor.
   *
   * @param path	the path to the actor
   */
  public void renameActor(TreePath path) {
    String		oldName;
    String 		newName;
    Node		node;
    Node		parent;
    Actor		actorOld;
    Actor		actorNew;
    List<TreePath> 	exp;

    node    = TreeHelper.pathToNode(path);
    oldName = node.getActor().getName();
    newName = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(getOwner()),
	"Please enter new name:", oldName);

    if (newName != null) {
      actorOld = node.getActor();

      // make sure name is not empty
      if (newName.length() == 0)
	newName = actorOld.getDefaultName();
      getOwner().addUndoPoint("Renaming actor " + actorOld.getName() + " to " + newName);
      exp = getOwner().getExpandedTreePaths();
      actorNew = actorOld.shallowCopy();
      actorNew.setName(newName);
      node.setActor(actorNew);
      getOwner().updateActorName(node);
      ((DefaultTreeModel) getOwner().getModel()).nodeChanged(node);
      getOwner().setModified(getOwner().isModified() || !oldName.equals(node.getActor().getName()));
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), node, Type.MODIFY));
      SwingUtilities.invokeLater(() -> getOwner().setExpandedTreePaths(exp));

      // update all occurrences, if necessary
      parent = (Node) node.getParent();
      if (!getOwner().getIgnoreNameChanges())
	AbstractEditPostProcessor.apply(getOwner(), ((parent != null) ? parent.getActor() : null), actorOld, actorNew);
      SwingUtilities.invokeLater(() -> {
	getOwner().locateAndDisplay(node.getFullName());
	getOwner().refreshTabs();
      });
    }
  }

  /**
   * Encloses the selected actors in the specified actor handler.
   *
   * @param paths	the (paths to the) actors to wrap in the control actor
   * @param handler	the handler to use
   */
  public void encloseActor(TreePath[] paths, ActorHandler handler) {
    Actor[]		currActor;
    Node		parent;
    Node 		currNode;
    Node		newNode;
    int			index;
    String		msg;
    MutableActorHandler mutable;
    int			i;
    String		newName;

    parent    = null;
    currActor = new Actor[paths.length];
    for (i = 0; i < paths.length; i++) {
      currNode     = TreeHelper.pathToNode(paths[i]);
      currActor[i] = currNode.getFullActor().shallowCopy();
      if (parent == null)
	parent = (Node) currNode.getParent();

      if (ActorUtils.isStandalone(currActor[i])) {
	if (!handler.getActorHandlerInfo().canContainStandalones()) {
	  GUIHelper.showErrorMessage(
	    getOwner(),
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
    newName = GUIHelper.showInputDialog(GUIHelper.getParentComponent(getOwner()), "Please enter name for enclosing actor (leave empty for default):", newName);
    if (newName == null)
      return;
    if (newName.isEmpty())
      newName = handler.getDefaultName();
    handler.setName(newName);

    if (paths.length == 1)
      getOwner().addUndoPoint("Enclosing node '" + TreeHelper.pathToActor(paths[0]).getFullName() + "' in " + handler.getClass().getName());
    else
      getOwner().addUndoPoint("Enclosing " + paths.length + " nodes in " + handler.getClass().getName());

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
      newNode = TreeHelper.buildTree(null, (Actor) handler, false);
      newNode.setOwner(parent.getOwner());
      for (i = 0; i < paths.length; i++) {
	currNode = TreeHelper.pathToNode(paths[i]);
	index    = parent.getIndex(currNode);
	parent.remove(index);
	if (i == 0)
	  parent.insert(newNode, index);
      }

      final Node current;
      if (paths.length == 1)
	current = newNode;
      else
	current = parent;
      SwingUtilities.invokeLater(() -> {
	getOwner().updateActorName(newNode);
	getOwner().setModified(true);
	getOwner().nodeStructureChanged(current);
	getOwner().expand(newNode);
      });
      SwingUtilities.invokeLater(() -> {
	getOwner().locateAndDisplay(newNode.getFullName());
	getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), current, Type.MODIFY));
	getOwner().redraw();
      });
    }
    catch (Exception e) {
      if (paths.length == 1)
	msg = "Failed to enclose actor '" + TreeHelper.pathToActor(paths[0]).getFullName() + "'";
      else
	msg = "Failed to enclose " + paths.length + " actors";
      msg += " in a " + handler.getClass().getSimpleName() + ": ";
      ConsolePanel.getSingleton().append(this, msg, e);
      GUIHelper.showErrorMessage(
	  getOwner(), msg + "\n" + e.getMessage());
    }
  }

  /**
   * Swaps the actor handler of the node with the new node, keeping the children
   * intact, as well as some basic options.
   *
   * @param path	the path to the actor to swap out
   * @param handler	the new handler to use
   */
  public void swapActor(TreePath path, ActorHandler handler) {
    final Node				node;
    ActorHandler			current;
    List<AbstractOptionTransfer>	transfers;

    node    = TreeHelper.pathToNode(path);
    current = (ActorHandler) TreeHelper.pathToActor(path);

    getOwner().addUndoPoint("Swapping node '" + current.getFullName() + "' with " + handler.getClass().getName());

    // transfer options
    transfers = AbstractOptionTransfer.getTransfers(current, handler);
    for (AbstractOptionTransfer transfer: transfers)
      transfer.transfer(current, handler);

    node.setActor(handler);
    node.invalidateRendering();
    SwingUtilities.invokeLater(() -> {
      getOwner().setModified(true);
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), node, Type.MODIFY));
      getOwner().redraw();
    });
  }

  /**
   * Encloses the specified actor in a DisplayPanelManager actor.
   *
   * @param path	the path of the actor to enclose
   */
  public void encloseInDisplayPanelManager(TreePath path) {
    Actor		currActor;
    Node		currNode;
    DisplayPanelManager manager;
    AbstractDisplay 	display;

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

    getOwner().addUndoPoint("Enclosing node '" + currNode.getActor().getFullName() + "' in " + manager.getClass().getName());

    SwingUtilities.invokeLater(() -> {
      List<String> exp = getOwner().getExpandedFullNames();
      currNode.setActor(manager);
      getOwner().setModified(true);
      getOwner().nodeStructureChanged((Node) currNode.getParent());
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), currNode, Type.MODIFY));
      getOwner().setExpandedFullNames(exp);
      getOwner().expand(currNode);
      getOwner().locateAndDisplay(currNode.getFullName());
    });
  }

  /**
   * Tries to figure what actors fit best in the tree at the given position.
   *
   * @param path	the path where to insert the actors
   * @param position	how the actors are to be inserted
   * @return		the actors
   */
  public Actor[] suggestActors(TreePath path, InsertPosition position) {
    Actor[]		result;
    Actor		parent;
    Node		parentNode;
    Node		node;
    int			pos;
    Actor[]		actors;
    int			i;
    Actor[]		suggestions;

    result = null;

    if (position == InsertPosition.BENEATH) {
      parentNode = TreeHelper.pathToNode(path);
      pos        = parentNode.getChildCount();
    }
    else {
      node       = TreeHelper.pathToNode(path);
      parentNode = (Node) node.getParent();
      pos        = parentNode.getIndex(node);
      if (position == InsertPosition.AFTER)
	pos++;
    }

    parent  = parentNode.getActor();
    actors  = new Actor[parentNode.getChildCount()];
    for (i = 0; i < actors.length; i++)
      actors[i] = ((Node) parentNode.getChildAt(i)).getActor();

    suggestions = ActorSuggestion.getSingleton().suggest(parent, pos, actors);
    if (suggestions.length > 0)
      result = suggestions;

    // default is "Filter"
    // TODO
    //if (result == null)
    //  result = ActorSuggestion.getSingleton().getDefaults();

    return result;
  }

  /**
   * Processes the specified actor with a user-specified actor processor
   * (prompts user with GOE dialog).
   * NB: The options of the specified actor will get processed.
   *
   * @param path	the path of the actor, if null the root actor is used
   * @return		true if actors processed
   */
  public boolean processActor(TreePath path) {
    return processActor(path, null);
  }

  /**
   * Processes the specified actor with the specified actor processor.
   * NB: The options of the specified actor will get processed.
   *
   * @param path	the path of the actor, if null the root actor is used
   * @param processor	the processor to use, null if to prompt user
   * @return		true if actors processed
   */
  public boolean processActor(TreePath path, AbstractActorProcessor processor) {
    ModifyingProcessor 			modifying;
    GraphicalOutputProducingProcessor 	graphical;
    BaseDialog 				dialog;
    final BaseDialog			fDialog;
    Node				node;
    Actor				flow;
    Actor				selected;
    final Node				newNode;
    final Node				parent;
    final int				index;
    final Component 			comp;
    ErrorMessagePanel 			errorPanel;
    final ErrorMessagePanel		fErrorPanel;
    final BaseTabbedPane 		tabbedPane;
    List<String>			exp;

    // selected actor or full flow?
    flow = getOwner().getActor();
    if (flow instanceof Flow)
      ((Flow) flow).setParentComponent(getOwner());
    if ((path != null) && (path.getPathCount() == 1))
      path = null;
    if (path == null) {
      selected = flow;
      node     = getOwner().getRootNode();
    }
    else {
      selected = ActorUtils.locate(TreeHelper.treePathToActorPath(path).getChildPath(), flow);
      node     = TreeHelper.pathToNode(path);
    }

    // prompt for processor?
    if (processor == null) {
      if (m_DialogProcessActors == null) {
	if (getOwner().getParentDialog() != null)
	  m_DialogProcessActors = new GenericObjectEditorDialog(getOwner().getParentDialog());
	else
	  m_DialogProcessActors = new GenericObjectEditorDialog(getOwner().getParentFrame());
	m_DialogProcessActors.setTitle("Process actors");
	m_DialogProcessActors.setModalityType(ModalityType.DOCUMENT_MODAL);
	m_DialogProcessActors.getGOEEditor().setCanChangeClassInDialog(true);
	m_DialogProcessActors.getGOEEditor().setClassType(AbstractActorProcessor.class);
	m_DialogProcessActors.setCurrent(new RemoveDisabledActors());
      }
      m_DialogProcessActors.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
      m_DialogProcessActors.setVisible(true);

      if (m_DialogProcessActors.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return false;

      processor = (AbstractActorProcessor) m_DialogProcessActors.getCurrent();
    }

    // process
    if (processor instanceof ModifyingProcessor)
      ((ModifyingProcessor) processor).setNoCopy(true);
    processor.process(selected);

    // modified?
    if (processor instanceof ModifyingProcessor) {
      modifying = (ModifyingProcessor) processor;
      if (modifying.isModified()) {
	getOwner().addUndoPoint("Processing actors with " + processor.toString());
	exp = getOwner().getExpandedFullNames();
	if (path == null) {
	  getOwner().buildTree(modifying.getModifiedActor());
	  newNode = node;
	}
	else {
	  newNode = TreeHelper.buildTree((Node) node.getParent(), modifying.getModifiedActor(), false);
	  parent  = (Node) node.getParent();
	  index   = parent.getIndex(node);
	  SwingUtilities.invokeLater(() -> {
	    parent.remove(index);
	    parent.insert(newNode, index);
	  });
	}
	SwingUtilities.invokeLater(() -> {
	  getOwner().setModified(true);
	  getOwner().nodeStructureChanged(newNode);
	  getOwner().setExpandedFullNames(exp);
	  getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), newNode, Type.MODIFY));
	});
      }
    }

    // any errors?
    if (processor.hasErrors()) {
      errorPanel = new ErrorMessagePanel();
      errorPanel.setErrorMessage(Utils.flatten(processor.getErrors(), "\n"));
    }
    else {
      errorPanel = null;
    }

    // graphical output?
    if (processor instanceof GraphicalOutputProducingProcessor) {
      graphical = (GraphicalOutputProducingProcessor) processor;
      if (graphical.hasGraphicalOutput()) {
	if (getOwner().getParentDialog() != null)
	  dialog = new BaseDialog(getOwner().getParentDialog());
	else
	  dialog = new BaseDialog(getOwner().getParentFrame());
	dialog.setTitle(graphical.getTitle());
	dialog.getContentPane().setLayout(new BorderLayout());
	comp = graphical.getGraphicalOutput();
	if (errorPanel == null) {
	  dialog.getContentPane().add(comp, BorderLayout.CENTER);
	  if (comp instanceof MenuBarProvider)
	    dialog.setJMenuBar(((MenuBarProvider) comp).getMenuBar());
	}
	else {
	  fDialog     = dialog;
	  fErrorPanel = errorPanel;
	  tabbedPane  = new BaseTabbedPane();
	  tabbedPane.addChangeListener((ChangeEvent e) -> {
	    if (tabbedPane.getSelectedIndex() == 0) {
	      if (comp instanceof MenuBarProvider)
		fDialog.setJMenuBar(((MenuBarProvider) comp).getMenuBar());
	      else
		fDialog.setJMenuBar(null);
	    }
	    else {
	      fDialog.setJMenuBar(fErrorPanel.getMenuBar());
	    }
	  });
	  dialog.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	  tabbedPane.addTab("Output", comp);
	  tabbedPane.addTab("Errors", errorPanel);
	}
	if (comp instanceof MenuBarProvider)
	  dialog.setJMenuBar(((MenuBarProvider) comp).getMenuBar());
	dialog.pack();
        dialog.setSize(Math.max(600, dialog.getWidth()), Math.max(400, dialog.getHeight()));
	dialog.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
	dialog.setVisible(true);
	errorPanel = null;
      }
    }

    // errors still to display?
    if (errorPanel != null) {
      if (getOwner().getParentDialog() != null)
	dialog = new BaseDialog(getOwner().getParentDialog());
      else
	dialog = new BaseDialog(getOwner().getParentFrame());
      dialog.setTitle(processor.getClass().getSimpleName());
      dialog.getContentPane().setLayout(new BorderLayout());
      dialog.getContentPane().add(errorPanel, BorderLayout.CENTER);
      dialog.setJMenuBar(errorPanel.getMenuBar());
      dialog.pack();
      dialog.setSize(Math.max(600, dialog.getWidth()), Math.max(400, dialog.getHeight()));
      dialog.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
      dialog.setVisible(true);
    }

    return true;
  }

  /**
   * Turns the selected actor into a callable actor using the specified
   * enclosing handler type.
   *
   * @param path	the (path to the) actor to turn into callable actor
   * @param handler	the callable actors handler
   */
  public void createCallableActor(TreePath path, Class handler) {
    Actor			currActor;
    Node 			currNode;
    Node			callableNode;
    Node			root;
    List<Node>			callable;
    List<Node>			multiview;
    CallableActorHandler 	callableActors;
    final Node			moved;
    AbstractCallableActor 	replacement;
    List<TreePath>		exp;
    int				index;

    currNode  = TreeHelper.pathToNode(path);
    currActor = currNode.getFullActor().shallowCopy();
    if (ActorUtils.isStandalone(currActor)) {
      GUIHelper.showErrorMessage(
        getOwner(),
        "Standalone actors cannot be turned into a callable actor!");
      return;
    }
    if (currActor instanceof AbstractCallableActor) {
      GUIHelper.showErrorMessage(
        getOwner(),
        "Actor points already to a callable actor!");
      return;
    }
    if ((currNode.getParent() != null) && (((Node) currNode.getParent()).getActor() instanceof CallableActors)) {
      GUIHelper.showErrorMessage(
        getOwner(),
        "Actor is already a callable actor!");
      return;
    }

    getOwner().addUndoPoint("Creating callable actor from '" + currNode.getActor().getFullName());

    callable  = FlowHelper.findCallableActorsHandler((Node) currNode.getParent(), new Class[]{handler});
    multiview = FlowHelper.findCallableActorsHandler((Node) currNode.getParent(), new Class[]{AbstractMultiView.class});

    // no handler instance available?
    if (callable.size() == 0) {
      root = (Node) currNode.getRoot();
      if (!((ActorHandler) root.getActor()).getActorHandlerInfo().canContainStandalones()) {
	GUIHelper.showErrorMessage(
	  getOwner(),
	  "Root actor '" + root.getActor().getName() + "' cannot contain standalones!");
	return;
      }
      try {
	callableActors = (CallableActorHandler) handler.newInstance();
      }
      catch (Exception e) {
	GUIHelper.showErrorMessage(
	  getOwner(),
	  "Failed to instantiate callable actor handler: " + handler.getName() + "\n"
	    + Utils.throwableToString(e));
	return;
      }
      callableNode = new Node(getOwner(), callableActors);
      index = 0;
      // CallableActors has to come after multiview actors
      if (handler == CallableActors.class) {
	if (multiview.size() > 0) {
	  for (Node node : multiview) {
	    if (node.getParent().getIndex(node) >= index)
	      index = node.getParent().getIndex(node) + 1;
	  }
	}
      }
      final int fIndex = index;
      SwingUtilities.invokeLater(() -> {
	root.insert(callableNode, fIndex);
	getOwner().updateActorName(callableNode);
      });
    }
    else {
      callableNode = callable.get(callable.size() - 1);
    }

    exp = getOwner().getExpandedTreePaths();

    // move actor
    moved = TreeHelper.buildTree(callableNode, currActor, true);
    getOwner().updateActorName(moved);

    // create replacement
    replacement = null;
    if (ActorUtils.isSource(currActor))
      replacement = new CallableSource();
    else if (ActorUtils.isTransformer(currActor))
      replacement = new CallableTransformer();
    else if (ActorUtils.isSink(currActor))
      replacement = new CallableSink();
    if (replacement != null) {
      final AbstractCallableActor fReplacement = replacement;
      SwingUtilities.invokeLater(() -> {
	fReplacement.setCallableName(new CallableActorReference(moved.getActor().getName()));
	currNode.setActor(fReplacement);
	currNode.removeAllChildren();
	getOwner().updateActorName(currNode);
      });
    }

    // update tree
    SwingUtilities.invokeLater(() -> {
      getOwner().setModified(true);
      getOwner().nodeStructureChanged(callableNode);
      getOwner().setExpandedTreePaths(exp);
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), callableNode, Type.MODIFY));
      getOwner().nodeStructureChanged((Node) currNode.getParent());
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), currNode, Type.MODIFY));
      getOwner().expand(callableNode);
    });
    SwingUtilities.invokeLater(() -> getOwner().redraw());
    SwingUtilities.invokeLater(() -> {
      getOwner().locateAndDisplay(currNode.getFullName());
    });
  }

  /**
   * Brings up a flow window for editing the selected external actor's flow.
   *
   * @param path	the path to the node
   */
  public void editFlow(TreePath path) {
    Node			node;
    ExternalActorHandler 	actor;

    node = TreeHelper.pathToNode(path);
    if (node == null)
      return;
    actor = (ExternalActorHandler) node.getActor();
    if (actor == null)
      return;
    if (getOwner() == null)
      return;
    if (getOwner().getEditor() == null)
      return;
    getOwner().getEditor().loadUnsafe(actor.getActorFile());

    // external flow might have changed, discard any inlined actors
    node.collapse();

    // notify listeners
    getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), node, Type.MODIFY));
  }

  /**
   * Opens a new FlowEditor window with the currently selected sub-flow.
   * Checks for callable actors and diplays confirmation dialog if so.
   *
   * @param paths	the (paths to the) actors to externalize
   */
  public void externalizeActor(TreePath[] paths) {
    Actor		handler;
    Actor[]		actors;
    Node		newNode;
    Node		currNode;
    Node		parent;
    int			index;
    int			i;
    int			retVal;

    if (paths.length == 0)
      return;
    if (paths.length == 1) {
      externalizeActor(paths[0]);
      return;
    }

    // externalize actors
    actors = new Actor[paths.length];
    parent = null;
    for (i = 0; i < paths.length; i++) {
      currNode  = TreeHelper.pathToNode(paths[i]);
      actors[i] = currNode.getFullActor().shallowCopy();
      if (parent == null)
	parent = (Node) currNode.getParent();
    }
    try {
      handler = ActorUtils.createExternalActor(actors);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  getOwner(), "Failed to externalize actor(s):\n" + Utils.throwableToString(e));
      return;
    }

    if (parent == null)
      return;

    // confirm when callable actors present
    if (ActorUtils.checkForCallableActorUser(handler)) {
      retVal = GUIHelper.showConfirmMessage(getOwner(), "Callable actor(s) referenced - continue?");
      if (retVal != ApprovalDialog.APPROVE_OPTION)
	return;
    }

    getOwner().addUndoPoint("Enclosing " + paths.length + " nodes in " + handler.getClass().getName());

    // update tree
    newNode = TreeHelper.buildTree(null, handler, false);
    newNode.setOwner(parent.getOwner());
    for (i = 0; i < paths.length; i++) {
      currNode = TreeHelper.pathToNode(paths[i]);
      index    = parent.getIndex(currNode);
      parent.remove(index);
      if (i == 0)
	parent.insert(newNode, index);
    }
    getOwner().updateActorName(newNode);
    getOwner().setModified(true);
    final Node fParent = parent;
    SwingUtilities.invokeLater(() -> {
      getOwner().nodeStructureChanged(fParent);
      getOwner().locateAndDisplay(fParent.getFullName());
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), fParent, Type.MODIFY));
    });

    externalizeActor(new TreePath(newNode.getPath()), false);
  }

  /**
   * Opens a new FlowEditor window with the currently selected sub-flow.
   * Checks for callable actors and diplays confirmation dialog if so.
   *
   * @param path	the (path to the) actor to externalize
   */
  public void externalizeActor(TreePath path) {
    externalizeActor(path, true);
  }

  /**
   * Opens a new FlowEditor window with the currently selected sub-flow.
   *
   * @param path		the (path to the) actor to externalize
   * @param checkCallActors	whether to check for callable actors
   */
  protected void externalizeActor(TreePath path, boolean checkCallActors) {
    Actor			currActor;
    Node 			currNode;
    AbstractExternalActor 	extActor;
    FlowEditorDialog		dialog;
    int				retVal;

    currNode  = TreeHelper.pathToNode(path);
    currActor = currNode.getFullActor().shallowCopy();

    // confirm when callable actors present
    if (checkCallActors && ActorUtils.checkForCallableActorUser(currActor)) {
      retVal = GUIHelper.showConfirmMessage(getOwner(), "Callable actor(s) referenced - continue?");
      if (retVal != ApprovalDialog.APPROVE_OPTION)
	return;
    }

    if (getOwner().getParentDialog() != null)
      dialog = new FlowEditorDialog(getOwner().getParentDialog());
    else
      dialog = new FlowEditorDialog(getOwner().getParentFrame());
    dialog.getFlowEditorPanel().newTab();
    dialog.getFlowEditorPanel().setCurrentFlow(currActor);
    dialog.getFlowEditorPanel().setModified(true);
    dialog.setVisible(true);
    if (dialog.getFlowEditorPanel().getCurrentFile() == null)
      return;

    getOwner().addUndoPoint("Externalizing node '" + currNode.getFullName() + "'");

    extActor = null;
    if (ActorUtils.isStandalone(currActor))
      extActor = new ExternalStandalone();
    else if (ActorUtils.isSource(currActor))
      extActor = new ExternalSource();
    else if (ActorUtils.isTransformer(currActor))
      extActor = new ExternalTransformer();
    else if (ActorUtils.isSink(currActor))
      extActor = new ExternalSink();
    extActor.setActorFile(new FlowFile(dialog.getFlowEditorPanel().getCurrentFile()));

    getOwner().setModified(true);
    currNode.setActor(extActor);
    currNode.removeAllChildren();
    getOwner().nodeStructureChanged(currNode);
    getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), currNode, Type.MODIFY));
  }

  /**
   * Turns the selected actor into its conditional equivalent.
   *
   * @param path	the (path to the) actor to turn into its conditional equivalent
   */
  public void makeConditional(TreePath path) {
    Actor			currActor;
    Node 			currNode;
    Node			parentNode;
    Class			condEquiv;
    Node			newNode;
    Actor			newActor;
    boolean			noEquiv;
    int				index;
    boolean			defaultName;
    boolean			expanded;
    GenericObjectEditorDialog	dialog;

    currNode   = TreeHelper.pathToNode(path);
    parentNode = (Node) currNode.getParent();
    expanded   = getOwner().isExpanded(path);
    currActor  = currNode.getFullActor().shallowCopy();
    noEquiv    = false;
    condEquiv  = null;

    if (!(currActor instanceof ActorWithConditionalEquivalent))
      noEquiv = true;

    if (!noEquiv) {
      condEquiv = ((ActorWithConditionalEquivalent) currActor).getConditionalEquivalent();
      if (condEquiv == null)
	noEquiv = true;
    }

    if (noEquiv) {
      GUIHelper.showErrorMessage(
	  getOwner(),
	  "Actor '" + currActor.getClass().getName() + "' does not have a conditional equivalent!");
      return;
    }

    // instantiate equivalent
    newNode  = null;
    newActor = null;
    try {
      newActor = (Actor) condEquiv.newInstance();
      // transfer some basic options
      newActor.setAnnotations(currActor.getAnnotations());
      newActor.setSkip(currActor.getSkip());
      newActor.setLoggingLevel(currActor.getLoggingLevel());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  getOwner(),
	  "Failed to instantiate conditional equivalent: " + condEquiv.getName());
      return;
    }

    // choose condition
    if (getOwner().getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getOwner().getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(getOwner().getParentFrame());
    dialog.setTitle("Conditions");
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(BooleanCondition.class);
    dialog.setCurrent(new Expression());
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    // create node
    ((BooleanConditionSupporter) newActor).setCondition((BooleanCondition) dialog.getCurrent());
    newNode = new Node(getOwner(), newActor);

    getOwner().addUndoPoint("Making conditional actor from '" + currNode.getActor().getFullName());

    // move children
    for (BaseTreeNode child: currNode.getChildren())
      newNode.add(child);

    // replace node
    defaultName = currActor.getName().equals(currActor.getDefaultName());
    index       = parentNode.getIndex(currNode);
    parentNode.insert(newNode, index);
    parentNode.remove(currNode);
    if (!defaultName) {
      newActor.setName(currActor.getName());
      newNode.setActor(newActor);
      getOwner().updateActorName(newNode);
    }
    if (expanded)
      getOwner().expand(newNode);

    // update tree
    getOwner().setModified(true);
    getOwner().nodeStructureChanged(parentNode);
    getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), parentNode, Type.MODIFY));
    getOwner().nodeStructureChanged(parentNode);
    getOwner().locateAndDisplay(newNode.getFullName());
    getOwner().redraw();
  }

  /**
   * Turns the selected actor into its timed equivalent.
   *
   * @param path	the (path to the) actor to turn into its timed equivalent
   */
  public void makeTimed(TreePath path) {
    Actor	currActor;
    Node 	currNode;
    Node	parentNode;
    Class	timedEquiv;
    Node	newNode;
    Actor	newActor;
    boolean	noEquiv;
    int		index;
    boolean	defaultName;
    boolean	expanded;

    currNode   = TreeHelper.pathToNode(path);
    parentNode = (Node) currNode.getParent();
    expanded   = getOwner().isExpanded(path);
    currActor  = currNode.getFullActor().shallowCopy();
    noEquiv    = false;
    timedEquiv = null;

    if (!(currActor instanceof ActorWithTimedEquivalent))
      noEquiv = true;

    if (!noEquiv) {
      timedEquiv = ((ActorWithTimedEquivalent) currActor).getTimedEquivalent();
      if (timedEquiv == null)
	noEquiv = true;
    }

    if (noEquiv) {
      GUIHelper.showErrorMessage(
	  getOwner(),
	  "Actor '" + currActor.getClass().getName() + "' does not have a timed equivalent!");
      return;
    }

    // instantiate equivalent
    newNode  = null;
    newActor = null;
    try {
      newActor = (Actor) timedEquiv.newInstance();
      // transfer some basic options
      newActor.setAnnotations(currActor.getAnnotations());
      newActor.setSkip(currActor.getSkip());
      newActor.setLoggingLevel(currActor.getLoggingLevel());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  getOwner(),
	  "Failed to instantiate timed equivalent: " + timedEquiv.getName());
      return;
    }

    // create node
    newNode = new Node(getOwner(), newActor);

    getOwner().addUndoPoint("Making timed actor from '" + currNode.getActor().getFullName());

    // move children
    for (BaseTreeNode child: currNode.getChildren())
      newNode.add(child);

    // replace node
    defaultName = currActor.getName().equals(currActor.getDefaultName());
    index       = parentNode.getIndex(currNode);
    parentNode.insert(newNode, index);
    parentNode.remove(currNode);
    if (!defaultName) {
      newActor.setName(currActor.getName());
      newNode.setActor(newActor);
      getOwner().updateActorName(newNode);
    }
    if (expanded)
      getOwner().expand(newNode);

    // update tree
    getOwner().setModified(true);
    getOwner().nodeStructureChanged(parentNode);
    getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), parentNode, Type.MODIFY));
    getOwner().nodeStructureChanged(parentNode);
    getOwner().locateAndDisplay(newNode.getFullName());
    getOwner().redraw();
  }

  /**
   * Returns the actor stored on the clipboard.
   *
   * @return		the actor or null if none available
   */
  public static Actor getActorFromClipboard() {
    Actor		result;
    NestedConsumer 	consumer;

    result = null;

    try {
      if (GUIHelper.canPasteStringFromClipboard()) {
	consumer = new NestedConsumer();
	consumer.setQuiet(true);
	result = (Actor) consumer.fromString(GUIHelper.pasteSetupFromClipboard());
	consumer.cleanUp();
      }
    }
    catch (Exception ex) {
      result = null;
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_DialogProcessActors != null) {
      m_DialogProcessActors.dispose();
      m_DialogProcessActors = null;
    }
  }
}
