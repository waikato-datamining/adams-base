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
 * TreeOperations.java
 * Copyright (C) 2015-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree;

import adams.core.ByteFormat;
import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.NewInstance;
import adams.core.SizeOf;
import adams.core.Utils;
import adams.core.io.FlowFile;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.core.optiontransfer.AbstractOptionTransfer;
import adams.core.sizeof.ActorFilter;
import adams.data.io.input.DefaultFlowReader;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.condition.bool.Expression;
import adams.flow.control.ConditionalSequence;
import adams.flow.control.ConditionalSubProcess;
import adams.flow.control.ConditionalTrigger;
import adams.flow.control.Flow;
import adams.flow.control.SubProcess;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractCallableActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorPath;
import adams.flow.core.ActorReferenceHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.ActorWithConditionalEquivalent;
import adams.flow.core.ActorWithTimedEquivalent;
import adams.flow.core.CallableActorReference;
import adams.flow.core.ExternalActorFileHandler;
import adams.flow.core.ExternalActorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.processor.ActorProcessor;
import adams.flow.processor.ActorProcessorWithFlowPanelContext;
import adams.flow.processor.GraphicalOutputProducingProcessor;
import adams.flow.processor.ModifyingProcessor;
import adams.flow.processor.MultiProcessor;
import adams.flow.sink.CallableSink;
import adams.flow.sink.ExternalSink;
import adams.flow.source.CallableSource;
import adams.flow.source.EnterManyValues;
import adams.flow.source.EnterManyValues.OutputType;
import adams.flow.source.ExternalSource;
import adams.flow.source.valuedefinition.DefaultValueDefinition;
import adams.flow.standalone.AbstractMultiView;
import adams.flow.standalone.CallableActors;
import adams.flow.standalone.ExternalStandalone;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.CallableTransformer;
import adams.flow.transformer.ExternalTransformer;
import adams.flow.transformer.MapToVariables;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ErrorMessagePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SearchableBaseList;
import adams.gui.core.dotnotationtree.AbstractItemFilter;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.event.SearchEvent;
import adams.gui.flow.FlowEditorDialog;
import adams.gui.flow.tabhandler.GraphicalActorProcessorHandler;
import adams.gui.flow.tree.postprocessor.AbstractEditPostProcessor;
import adams.gui.flow.tree.record.add.AbstractRecordActorAdded;
import adams.gui.flow.tree.record.enclose.AbstractRecordActorEnclosed;
import adams.gui.goe.Favorites;
import adams.gui.goe.Favorites.Favorite;
import adams.gui.goe.FlowHelper;
import adams.gui.goe.GenericObjectEditorClassTreePopupMenu;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.goe.classtree.ActorClassTreeFilter;
import adams.gui.goe.classtree.ClassTree;
import adams.parser.ActorSuggestion.SuggestionData;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import sizeof.agent.Statistics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Performs complex operations on the tree, like adding, removing, enclosing
 * actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
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
    AFTER
  }

  /**
   * Enumeration for actor dialogs.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum ActorDialog {
    GOE,
    GOE_NO_TREE,
    GOE_FORCED,
    GOE_FORCED_NO_TREE,
    TREE,
    TREE_NO_GOE,
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
      if (parentNode != null) {
	index = parentNode.getIndex(node);
	before = getOwner().getNearestActor(parentNode, index, false);
	after = node.getActor();
	if (after.getSkip())
	  after = getOwner().getNearestActor(parentNode, index, true);
      }
    }
    // add after
    else if (position == InsertPosition.AFTER) {
      node       = TreeHelper.pathToNode(path);
      parentNode = (Node) node.getParent();
      if (parentNode != null) {
	index = parentNode.getIndex(node);
	after = getOwner().getNearestActor(parentNode, index, true);
	before = node.getActor();
	if (before.getSkip())
	  before = getOwner().getNearestActor(parentNode, index, false);
      }
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
    addActor(path, actor, position, record, ActorDialog.GOE);
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
   * @param dialogType	the dialog type to use
   */
  public void addActor(TreePath path, Actor actor, InsertPosition position, boolean record, ActorDialog dialogType) {
    GenericObjectEditorDialog 	dialog;
    final Node			node;
    final Node			parent;
    int				index;
    Node[]			children;
    Actor[]			actors;
    String			txt;
    final List<String> 		exp;
    final ClassTree 		tree;
    final GenericObjectEditorClassTreePopupMenu goePopup;
    Component 			comp;

    if ((actor == null) || (dialogType == ActorDialog.GOE_FORCED) || (dialogType == ActorDialog.GOE_FORCED_NO_TREE)) {
      node = TreeHelper.pathToNode(path);
      if (position == InsertPosition.BENEATH)
	getOwner().updateCurrentEditing(node, null);
      else
	getOwner().updateCurrentEditing((Node) node.getParent(), null);

      switch (dialogType) {
	case GOE:
	case GOE_NO_TREE:
	case GOE_FORCED:
	case GOE_FORCED_NO_TREE:
	  dialog = GenericObjectEditorDialog.createDialog(getOwner());
	  dialog.setUISettingsPrefix(Actor.class);
	  if (position == InsertPosition.HERE)
	    dialog.setTitle("Add here...");
	  else if (position == InsertPosition.AFTER)
	    dialog.setTitle("Add after...");
	  else if (position == InsertPosition.BENEATH)
	    dialog.setTitle("Add beneath...");
	  actors = suggestActors(path, position);
	  dialog.getGOEEditor().setCanChangeClassInDialog((dialogType == ActorDialog.GOE) || (dialogType == ActorDialog.GOE_FORCED));
	  dialog.getGOEEditor().setClassType(Actor.class);
	  dialog.getGOEEditor().setFilter(configureFilter(path, position));
	  dialog.setProposedClasses(actors);
	  if (actor != null) {
	    dialog.setCurrent(actor);
	  }
	  else {
	    if (actors != null)
	      dialog.setCurrent(actors[0]);
	    else
	      dialog.setCurrent(null);
	  }
	  if (!dialog.getUISettingsApplied())
	    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
	  dialog.setVisible(true);
	  getOwner().updateCurrentEditing(null, null);
	  if (dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION)
	    addActor(path, (Actor) dialog.getEditor().getValue(), position, record);
	  break;

	case TREE:
	case TREE_NO_GOE:
	  tree = new ClassTree();
	  tree.setFilter(configureFilter(path, position));
	  tree.setItems(new ArrayList<>(Arrays.asList(ClassLister.getSingleton().getClassnames(Actor.class))));
	  tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	  tree.expandAll();
	  goePopup = new GenericObjectEditorClassTreePopupMenu(tree);
	  switch (position) {
	    case BENEATH:
	      goePopup.setInfoText("Insert _actor beneath");
	      break;
	    case HERE:
	      goePopup.setInfoText("Insert _actor here");
	      break;
	    case AFTER:
	      goePopup.setInfoText("Insert _actor after");
	      break;
	    default:
	      ConsolePanel.getSingleton().append(
		LoggingLevel.WARNING, "Unhandled position for search tree info text: " + position);
	  }
	  goePopup.pack();
	  tree.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {
	      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		String classname = tree.getSelectedItem();
		if (classname == null)
		  return;
		goePopup.setVisible(false);
		addActor(
		  path,
		  (Actor) NewInstance.newInstance(classname),
		  position,
		  record,
		  (dialogType == ActorDialog.TREE ? ActorDialog.GOE_FORCED_NO_TREE : ActorDialog.GOE_NO_TREE));
	      }
	      else {
		super.keyPressed(e);
	      }
	    }
	  });
	  tree.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
	      if (MouseUtils.isDoubleClick(e)) {
		String classname = tree.getSelectedItem();
		if (classname == null)
		  return;
		goePopup.setVisible(false);
		addActor(
		  path,
		  (Actor) NewInstance.newInstance(classname),
		  position,
		  record,
		  (dialogType == ActorDialog.TREE ? ActorDialog.GOE_FORCED_NO_TREE : ActorDialog.GOE_NO_TREE));
	      }
	      else {
		super.mouseClicked(e);
	      }
	    }
	  });
	  comp = GUIHelper.getParentComponent(getOwner());
	  if (comp != null) {
	    goePopup.show(
	      comp,
	      (int) (comp.getWidth() - goePopup.getPreferredSize().getWidth()) / 2,
	      (int) (comp.getHeight() - goePopup.getPreferredSize().getHeight())/ 2);
	  }
	  else {
	    ConsolePanel.getSingleton().append(
	      LoggingLevel.WARNING, "Failed to obtain parent component of owner for showing actor search tree in Flow editor!");
	  }
	  break;

	default:
	  GUIHelper.showErrorMessage(
	    GUIHelper.getParentComponent(getOwner()),
	    "Unhandled actor dialog for adding actor: " + dialogType);
      }
    }
    else {
      // make sure name is not empty
      if (actor.getName().length() == 0)
	actor.setName(actor.getDefaultName());

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
	  getOwner().updateActorName(child, true);
	SwingUtilities.invokeLater(() -> {
	  getOwner().nodeStructureChanged(node);
	  getOwner().setExpandedFullNames(exp);
	  getOwner().expand(node);
	});
	SwingUtilities.invokeLater(() -> {
	  getOwner().setSelectedFullName(children[children.length - 1].getFullName());
	  getOwner().requestFocus();
	});

	// record
	if (actors.length == 1) {
	  AbstractRecordActorAdded.recordAll(
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
	    getOwner().updateActorName(child, true);
	  });
	  index++;
	}
	SwingUtilities.invokeLater(() -> {
	  getOwner().nodeStructureChanged(parent);
	  getOwner().setExpandedFullNames(exp);
	});
	SwingUtilities.invokeLater(() -> {
	  getOwner().setSelectedFullName(children[children.length - 1].getFullName());
	  getOwner().requestFocus();
	});

	// record
	if (actors.length == 1) {
	  AbstractRecordActorAdded.recordAll(
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
   * For pasting nodes.
   *
   * @param path	the path to the actor to add the new actor sibling
   * @param nodes	the nodes to paste
   * @param position	how to insert the nodes
   */
  public void pasteNodes(final TreePath path, final Node[] nodes, InsertPosition position) {
    Actor 		currentActor;
    final Node 		currentNode;
    Actor 		parentActor;
    Node 		parentNode;
    int			index;
    final List<String>	exp;

    currentActor = TreeHelper.pathToActor(path);
    if (currentActor == null) {
      GUIHelper.showErrorMessage(getOwner(), "Failed to determine actor from tree path!");
      return;
    }
    currentNode = TreeHelper.pathToNode(path);
    if (currentNode == null) {
      GUIHelper.showErrorMessage(getOwner(), "Failed to determine node from tree path!");
      return;
    }
    parentNode  = (Node) currentNode.getParent();
    parentActor = (parentNode != null) ? parentNode.getActor() : null;
    exp         = getOwner().getExpandedFullNames();

    switch (position) {
      case BENEATH:
        if (!(currentActor instanceof MutableActorHandler)) {
          GUIHelper.showErrorMessage(getOwner(), "Actor is not a " + Utils.classToString(MutableActorHandler.class) + ", cannot paste beneath!");
          return;
	}
	getOwner().addUndoPoint("Pasting node" + ((nodes.length > 1) ? "s" : "") + " beneath '" + currentNode.getActor().getName() + "'");
	for (Node node: nodes) {
          node.setOwner(getOwner());
          currentNode.add(node);
          getOwner().updateActorName(node, true);
	}
	getOwner().setModified(true);
	SwingUtilities.invokeLater(() -> {
	  getOwner().nodeStructureChanged(currentNode);
	  getOwner().setExpandedFullNames(exp);
	  getOwner().expand(currentNode);
	});
	SwingUtilities.invokeLater(() -> {
	  getOwner().setSelectedFullName(currentNode.getFullName());
	  getOwner().requestFocus();
	  getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), currentNode, Type.MODIFY));
	});
        break;

      case HERE:
        if (!(parentActor instanceof MutableActorHandler)) {
          GUIHelper.showErrorMessage(getOwner(), "Parent actor is not a " + Utils.classToString(MutableActorHandler.class) + ", cannot paste here!");
          return;
	}
	getOwner().addUndoPoint("Pasting node" + ((nodes.length > 1) ? "s" : "") + " before '" + currentNode.getActor().getName() + "'");
	index = parentNode.getIndex(currentNode);
	for (Node node: nodes) {
          node.setOwner(getOwner());
          parentNode.insert(node, index);
          getOwner().updateActorName(node, true);
          index++;
	}
	getOwner().setModified(true);
	SwingUtilities.invokeLater(() -> {
	  getOwner().nodeStructureChanged(parentNode);
	  getOwner().setExpandedFullNames(exp);
	  getOwner().expand(parentNode);
	});
	SwingUtilities.invokeLater(() -> {
	  getOwner().setSelectedFullName(nodes[nodes.length - 1].getFullName());
	  getOwner().requestFocus();
	  getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), parentNode, Type.MODIFY));
	});
        break;

      case AFTER:
        if (!(parentActor instanceof MutableActorHandler)) {
          GUIHelper.showErrorMessage(getOwner(), "Parent actor is not a " + Utils.classToString(MutableActorHandler.class) + ", cannot paste after!");
          return;
	}
	getOwner().addUndoPoint("Pasting node" + ((nodes.length > 1) ? "s" : "") + " after '" + currentNode.getActor().getName() + "'");
	index = parentNode.getIndex(currentNode) + 1;
	for (Node node: nodes) {
          node.setOwner(getOwner());
          parentNode.insert(node, index);
          getOwner().updateActorName(node, true);
          index++;
	}
	getOwner().setModified(true);
	SwingUtilities.invokeLater(() -> {
	  getOwner().nodeStructureChanged(parentNode);
	  getOwner().setExpandedFullNames(exp);
	  getOwner().expand(parentNode);
	});
	SwingUtilities.invokeLater(() -> {
	  getOwner().setSelectedFullName(nodes[nodes.length - 1].getFullName());
	  getOwner().requestFocus();
	  getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), parentNode, Type.MODIFY));
	});
        break;

      default:
        throw new IllegalStateException("Unhandled insert position: " + position);
    }
  }

  /**
   * Lets the user select an actor from a dialog of favorites.
   *
   * @param path	the path to the actor to add the new actor sibling
   * @param position	where to insert the actor
   * @param record	whether to record the addition
   */
  public void favoriteActor(TreePath path, InsertPosition position, boolean record) {
    List<Favorite>		favs;
    final SearchableBaseList	list;
    SearchPanel			search;
    final ApprovalDialog	dialog;
    JPanel			panel;
    Favorite			favorite;

    favs = Favorites.getSingleton().getFavorites(Actor.class);
    if (favs.size() == 0) {
      GUIHelper.showErrorMessage(m_Owner, "No favorite actors available!\nSee Maintenance -> Favorites management");
      return;
    }

    // TODO take context into account?

    list   = new SearchableBaseList(favs.toArray(new Favorite[favs.size()]));
    search = new SearchPanel(LayoutType.HORIZONTAL, false);
    search.addSearchListener((SearchEvent e) -> list.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    panel = new JPanel(new BorderLayout(5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panel.add(new BaseScrollPane(list), BorderLayout.CENTER);
    panel.add(search, BorderLayout.SOUTH);
    if (GUIHelper.getParentDialog(m_Owner) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(m_Owner), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(m_Owner), true);
    dialog.setTitle("Select favorite");
    dialog.setApproveCaption("Select");
    dialog.setApproveMnemonic(KeyEvent.VK_L);
    dialog.setCancelCaption("Cancel");
    dialog.setCancelMnemonic(KeyEvent.VK_C);
    dialog.setDiscardVisible(false);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(m_Owner);
    list.setSelectedIndex(0);
    list.requestFocusInWindow();
    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isDoubleClick(e) && (list.getSelectedIndices().length == 1)) {
          dialog.getApproveButton().doClick();
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;
    if (list.getSelectedIndices().length != 1)
      return;

    favorite = (Favorite) list.getSelectedValue();
    addActor(path, (Actor) favorite.getObject(), position, record, ActorDialog.GOE_FORCED);
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
    boolean[]			expanded;
    boolean			keepChildren;
    int				retVal;

    if (path == null)
      return;

    currNode = TreeHelper.pathToNode(path);
    getOwner().updateCurrentEditing((Node) currNode.getParent(), currNode);
    actorOld = currNode.getActor().shallowCopy();
    dialog   = GenericObjectEditorDialog.createDialog(getOwner());
    dialog.setUISettingsPrefix(Actor.class);
    editable = getOwner().isEditable() && !getOwner().isDebug() && currNode.isEditable();
    if ((getOwner().getRootActor() instanceof Flow) && ((Flow) getOwner().getRootActor()).getReadOnly()) {
      retVal   = GUIHelper.showConfirmMessage(getOwner().getParent(), "Flow is marked read-only - proceed with editing (Yes) or only viewing (No)?");
      editable = (retVal == ApprovalDialog.APPROVE_OPTION);
      if (retVal == ApprovalDialog.CANCEL_OPTION)
        return;
    }
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
    if (!dialog.getUISettingsApplied())
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
      keepChildren = false;
      changed = (actor.getClass() != actorOld.getClass());
      if (changed) {
        if ((actor instanceof MutableActorHandler) && (actorOld instanceof MutableActorHandler)) {
          keepChildren = true;
	}
      }
      else if (actor instanceof ActorHandler) {
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
	  if (keepChildren) {
	    currNode.setActor(actor);
	    expanded = getOwner().getExpandedState();
	  }
	  else {
	    getOwner().buildTree(actor);
	    currNode = (Node) getOwner().getModel().getRoot();
	  }
	}
	else {
	  if (keepChildren) {
	    currNode.setActor(actor);
	    expanded = getOwner().getExpandedState();
	  }
	  else {
	    newNode = TreeHelper.buildTree(null, actor, false);
	    newNode.setOwner(parent.getOwner());
	    index = parent.getIndex(currNode);
	    parent.remove(index);
	    parent.insert(newNode, index);
	    currNode = newNode;
	  }
	}
      }
      else {
	currNode.setActor(actor);
	expanded = getOwner().getExpandedState();
      }
      getOwner().updateActorName(currNode, false);
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
	getOwner().locateAndDisplay(fCurrNode.getFullName(), true);
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

    node    = TreeHelper.pathToNode(path);
    oldName = node.getActor().getName();
    newName = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(getOwner()),
	"Please enter new name:", oldName);

    renameActor(path, newName);
  }

  /**
   * Renames an actor.
   *
   * @param path	the path to the actor
   * @param newName 	the new name for the actor
   */
  public void renameActor(TreePath path, String newName) {
    String		oldName;
    Node		node;
    Node		parent;
    Actor		actorOld;
    Actor		actorNew;
    List<TreePath> 	exp;

    node    = TreeHelper.pathToNode(path);
    oldName = node.getActor().getName();

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
      getOwner().updateActorName(node, false);
      ((DefaultTreeModel) getOwner().getModel()).nodeChanged(node);
      getOwner().setModified(getOwner().isModified() || !oldName.equals(node.getActor().getName()));
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), node, Type.MODIFY));
      SwingUtilities.invokeLater(() -> getOwner().setExpandedTreePaths(exp));

      // update all occurrences, if necessary
      parent = (Node) node.getParent();
      if (!getOwner().getIgnoreNameChanges())
	AbstractEditPostProcessor.apply(getOwner(), ((parent != null) ? parent.getActor() : null), actorOld, actorNew);
      SwingUtilities.invokeLater(() -> {
	getOwner().locateAndDisplay(node.getFullName(), true);
	getOwner().refreshTabs();
      });
    }
  }

  /**
   * Cleans up the names of actors.
   *
   * @param paths	the paths to the actors
   */
  public void cleanUpActorName(TreePath[] paths) {
    Node			node;

    for (TreePath path: paths) {
      node = TreeHelper.pathToNode(path);
      renameActor(path, node.getActor().getDefaultName());
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
    boolean 		referenced;

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

    // do we need to propagate name changes (eg actor is below CallableActors)?
    referenced = (paths.length == 1) && (parent.getActor() instanceof ActorReferenceHandler);

    // enter new name
    newName = handler.getName();
    if ((parent.getActor() instanceof ActorReferenceHandler) && (currActor.length == 1))
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
      getOwner().updateActorName(newNode, false);
      if (referenced) {
	if (!getOwner().getIgnoreNameChanges())
	  AbstractEditPostProcessor.apply(getOwner(), parent.getActor(), TreeHelper.pathToNode(paths[0]).getActor(), newNode.getActor());
      }
      SwingUtilities.invokeLater(() -> {
	getOwner().setModified(true);
	getOwner().nodeStructureChanged(current);
	getOwner().expand(newNode);
      });
      SwingUtilities.invokeLater(() -> {
	getOwner().locateAndDisplay(newNode.getFullName(), true);
	getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), current, Type.MODIFY));
	getOwner().redraw();
      });

      // record enclosing
      AbstractRecordActorEnclosed.recordAll(
	  getOwner(),
	  paths,
	  handler);
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
   * Pulls up the actors of the selected mutable actor.
   *
   * @param path	the (paths to the) actors to wrap in the control actor
   */
  public void pullUpActors(TreePath path) {
    Node 	handlerNode;
    Node	parentNode;
    List<Node>	subNodes;
    int		index;
    int		i;

    handlerNode = TreeHelper.pathToNode(path);
    parentNode  = (Node) handlerNode.getParent();
    if (parentNode == null)
      return;

    index    = parentNode.getIndex(handlerNode);
    subNodes = new ArrayList<>();
    for (i = 0; i < handlerNode.getChildCount(); i++)
      subNodes.add((Node) handlerNode.getChildAt(i));
    if (subNodes.size() == 0)
      return;

    getOwner().addUndoPoint("Pulling up '" + handlerNode.getFullName() + "'");

    parentNode.remove(index);
    for (i = 0; i < subNodes.size(); i++) {
      parentNode.insert(subNodes.get(i), index + i);
      getOwner().updateActorName(subNodes.get(i), true);
    }

    SwingUtilities.invokeLater(() -> {
      getOwner().setModified(true);
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), parentNode, Type.MODIFY));
      getOwner().redraw();
    });
  }

  /**
   * Swaps the actor handler of the node with the new node, keeping the children
   * intact, as well as some basic options.
   *
   * @param sourcePath	the path to the actor to swap out
   * @param target	the new handler to use
   */
  public void swapActor(TreePath sourcePath, Actor target) {
    final Node				node;
    Actor 				source;
    List<AbstractOptionTransfer>	transfers;

    node   = TreeHelper.pathToNode(sourcePath);
    source = TreeHelper.pathToActor(sourcePath);

    getOwner().addUndoPoint("Swapping node '" + node.getFullName() + "' with " + target.getClass().getName());

    // transfer options
    transfers = AbstractOptionTransfer.getTransfers(source, target);
    for (AbstractOptionTransfer transfer: transfers)
      transfer.transfer(source, target);

    node.setActor(target);
    node.invalidateRendering();
    SwingUtilities.invokeLater(() -> {
      getOwner().setModified(true);
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), node, Type.MODIFY));
      getOwner().redraw();
    });
  }

  /**
   * Returns the context for making actor suggestions.
   *
   * @param path	the path where to insert the actors
   * @param position	how the actors are to be inserted
   * @return		the context
   */
  public SuggestionData configureSuggestionContext(TreePath path, InsertPosition position) {
    SuggestionData	result;
    Node		parentNode;
    Node		node;
    int			pos;
    int			i;

    result = new SuggestionData();

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

    result.parent     = parentNode.getActor();
    result.parentNode = parentNode;
    result.position   = pos;
    result.actors     = new Actor[parentNode.getChildCount()];
    result.actorNodes = new Node[parentNode.getChildCount()];
    for (i = 0; i < result.actors.length; i++) {
      result.actorNodes[i] = (Node) parentNode.getChildAt(i);
      result.actors[i]     = result.actorNodes[i].getActor();
    }

    // restrict actor types
    result.allowStandalones  = true;
    result.allowSources      = true;
    result.allowTransformers = true;
    result.allowSinks        = true;
    switch (position) {
      case HERE:
	result.allowSinks        = false;
	result.allowStandalones  = ActorUtils.isStandalone(result.actors[result.position]) || ActorUtils.isSource(result.actors[result.position]);
	result.allowSources      = !ActorUtils.isSource(result.actors[result.position]);
	result.allowTransformers = !ActorUtils.isSource(result.actors[result.position]) && !ActorUtils.isStandalone(result.actors[result.position]);;
        break;
      case AFTER:
	result.allowStandalones = ActorUtils.isStandalone(result.actors[result.position - 1]);
	result.allowSources     = ActorUtils.isStandalone(result.actors[result.position - 1]);
	result.allowSinks       = !ActorUtils.isStandalone(result.actors[result.position - 1]);
        break;
      case BENEATH:
        break;
    }

    return result;
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
    SuggestionData	context;
    Actor[]		suggestions;

    result = null;

    context     = configureSuggestionContext(path, position);
    suggestions = ActorSuggestion.getSingleton().suggest(context);
    if (suggestions.length > 0)
      result = suggestions;

    // default is "Filter"
    // TODO
    //if (result == null)
    //  result = ActorSuggestion.getSingleton().getDefaults();

    return result;
  }

  /**
   * Returns the context for making external actor suggestions.
   *
   * @param paths	the paths of the actors to externalize
   * @return		the context
   */
  public SuggestionData configureSuggestionContext(TreePath[] paths) {
    SuggestionData	result;
    Node		parentNode;
    Node		node;
    int			pos;
    int			i;

    result = new SuggestionData();

    node       = TreeHelper.pathToNode(paths[0]);
    parentNode = (Node) node.getParent();
    if (parentNode == null)
      return null;

    if (paths.length == 1)
      pos = 0;
    else
      pos = -1;

    result.parent     = parentNode.getActor();
    result.parentNode = parentNode;
    result.position   = pos;
    result.actors     = new Actor[paths.length];
    result.actorNodes = new Node[paths.length];
    for (i = 0; i < result.actors.length; i++) {
      result.actorNodes[i] = TreeHelper.pathToNode(paths[i]);
      result.actors[i]     = result.actorNodes[i].getActor();
    }

    // don't restrict actor types
    result.allowStandalones  = true;
    result.allowSources      = true;
    result.allowTransformers = true;
    result.allowSinks        = true;

    return result;
  }

  /**
   * Tries to figure what external actors fit best using the given selection of
   * actors.
   *
   * @param paths	the paths of the actors to externalize
   * @return		the actors
   */
  public Actor[] suggestExternalActors(TreePath[] paths) {
    Actor[]		result;
    SuggestionData	context;
    Actor[]		suggestions;

    result = null;

    context = configureSuggestionContext(paths);
    if (context == null)
      return null;
    suggestions = ExternalActorSuggestion.getSingleton().suggest(context);
    if (suggestions.length > 0)
      result = suggestions;

    return result;
  }

  /**
   * Returns the dialog for processing actors.
   *
   * @param title	the title for the dialog, null for default
   * @return		the dialog
   */
  public GenericObjectEditorDialog getProcessActorsDialog(String title) {
    if (m_DialogProcessActors == null) {
      if (getOwner().getParentDialog() != null)
        m_DialogProcessActors = new GenericObjectEditorDialog(getOwner().getParentDialog());
      else
        m_DialogProcessActors = new GenericObjectEditorDialog(getOwner().getParentFrame());
      m_DialogProcessActors.setUISettingsPrefix(ActorProcessor.class);
      m_DialogProcessActors.setModalityType(ModalityType.DOCUMENT_MODAL);
      m_DialogProcessActors.getGOEEditor().setCanChangeClassInDialog(true);
      m_DialogProcessActors.getGOEEditor().setClassType(ActorProcessor.class);
      m_DialogProcessActors.setCurrent(new MultiProcessor());
    }
    if (title == null)
      title = "Process actors";
    m_DialogProcessActors.setTitle(title);
    return m_DialogProcessActors;
  }

  /**
   * Processes the specified actor with the specified actor processor.
   * NB: The options of the specified actor will get processed.
   *
   * @param path	the path of the actor, if null the root actor is used
   * @param processor	the processor to use, null if to prompt user
   */
  public void processActor(TreePath path, ActorProcessor processor) {
    processActor(path, processor, null);
  }

  /**
   * Processes the specified actor with the specified actor processor.
   * NB: The options of the specified actor will get processed.
   *
   * @param path	the path of the actor, if null the root actor is used
   * @param processor	the processor to use, null if to prompt user
   * @param after 	runnable to execute after actors have been processed, null for none
   */
  public void processActor(TreePath path, ActorProcessor processor, final Runnable after) {
    Runnable				runnable;
    final ActorProcessor 		fProcessor;
    final TreePath			fPath;
    Node				node;
    Actor				flow;
    Actor				selected;
    GenericObjectEditorDialog		procDialog;

    // prompt for processor?
    if (processor == null) {
      procDialog = getProcessActorsDialog(null);
      procDialog.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
      procDialog.setVisible(true);

      if (procDialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return;

      processor = (ActorProcessor) procDialog.getCurrent();
    }

    if (processor instanceof ActorProcessorWithFlowPanelContext)
      ((ActorProcessorWithFlowPanelContext) processor).setContext(getOwner().getOwner());

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

    // process
    fProcessor = processor;
    fPath      = path;
    runnable = () -> {
      if (fProcessor instanceof ModifyingProcessor)
	((ModifyingProcessor) fProcessor).setNoCopy(true);
      fProcessor.process(selected);

      // modified?
      if (fProcessor instanceof ModifyingProcessor) {
	ModifyingProcessor modifying = (ModifyingProcessor) fProcessor;
	if (modifying.isModified()) {
	  getOwner().addUndoPoint("Processing actors with " + fProcessor.toString());
	  List<String> exp = getOwner().getExpandedFullNames();
	  final Node newNode;
	  if (fPath == null) {
	    getOwner().buildTree(modifying.getModifiedActor());
	    newNode = node;
	  }
	  else {
	    newNode = TreeHelper.buildTree((Node) node.getParent(), modifying.getModifiedActor(), false);
	    final Node parent = (Node) node.getParent();
	    final int index = parent.getIndex(node);
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
      ErrorMessagePanel errorPanel;
      if (fProcessor.hasErrors()) {
	errorPanel = new ErrorMessagePanel();
	errorPanel.setErrorMessage(Utils.flatten(fProcessor.getErrors(), "\n"));
      }
      else {
	errorPanel = null;
      }

      // graphical output?
      if (fProcessor instanceof GraphicalOutputProducingProcessor) {
	GraphicalOutputProducingProcessor graphical = (GraphicalOutputProducingProcessor) fProcessor;
	if (graphical.hasGraphicalOutput()) {
	  GraphicalActorProcessorHandler handler = getOwner().getOwner().getTabHandler(GraphicalActorProcessorHandler.class);
	  if (handler != null) {
	    handler.add(
	      graphical.getTitle(),
	      graphical.getGraphicalOutput(),
	      fProcessor.hasErrors() ? Utils.flatten(fProcessor.getErrors(), "\n") : null);
	    errorPanel = null;
	  }
	}
      }

      // errors still to display?
      if (errorPanel != null) {
	BaseDialog dialog;
	if (getOwner().getParentDialog() != null)
	  dialog = new BaseDialog(getOwner().getParentDialog());
	else
	  dialog = new BaseDialog(getOwner().getParentFrame());
	dialog.setTitle(fProcessor.getClass().getSimpleName());
	dialog.getContentPane().setLayout(new BorderLayout());
	dialog.getContentPane().add(errorPanel, BorderLayout.CENTER);
	dialog.setJMenuBar(errorPanel.getMenuBar());
	dialog.pack();
	dialog.setSize(Math.max(600, dialog.getWidth()), Math.max(400, dialog.getHeight()));
	dialog.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
	dialog.setVisible(true);
      }

      if (after != null)
	SwingUtilities.invokeLater(after);
    };
    getOwner().getOwner().startBackgroundTask(runnable, "Processing...", (after != null));
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
    ActorReferenceHandler callableActors;
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
	callableActors = (ActorReferenceHandler) handler.getDeclaredConstructor().newInstance();
      }
      catch (Exception e) {
	GUIHelper.showErrorMessage(
	  getOwner(),
	  "Failed to instantiate callable actor handler: " + handler.getName() + "\n"
	    + LoggingHelper.throwableToString(e));
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
	getOwner().updateActorName(callableNode, false);
      });
    }
    else {
      callableNode = callable.get(callable.size() - 1);
    }

    exp = getOwner().getExpandedTreePaths();

    // move actor
    moved = TreeHelper.buildTree(callableNode, currActor, true);
    getOwner().updateActorName(moved, false);

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
	getOwner().updateActorName(currNode, false);
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
      getOwner().locateAndDisplay(currNode.getFullName(), true);
    });
  }

  /**
   * Brings up a flow window for editing the selected external actor's flow.
   *
   * @param path	the path to the node
   */
  public void editFlow(TreePath path) {
    Node			node;
    ExternalActorFileHandler 	actor;

    node = TreeHelper.pathToNode(path);
    if (node == null)
      return;
    actor = (ExternalActorFileHandler) node.getActor();
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
   * @param suggestion	the suggested actor to replace with
   */
  public void externalizeActor(TreePath[] paths, Actor suggestion) {
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
      externalizeActor(paths[0], suggestion);
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
      handler = ActorUtils.createExternalActor(actors, null);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  getOwner(), "Failed to externalize actor(s):\n" + LoggingHelper.throwableToString(e));
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
    getOwner().updateActorName(newNode, false);
    getOwner().setModified(true);
    final Node fParent = parent;
    SwingUtilities.invokeLater(() -> {
      getOwner().nodeStructureChanged(fParent);
      getOwner().locateAndDisplay(fParent.getFullName(), true);
      getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), fParent, Type.MODIFY));
    });

    externalizeActor(new TreePath(newNode.getPath()), false, suggestion);
  }

  /**
   * Opens a new FlowEditor window with the currently selected sub-flow.
   * Checks for callable actors and diplays confirmation dialog if so.
   *
   * @param path	the (path to the) actor to externalize
   * @param suggestion	the suggested actor to replace with
   */
  public void externalizeActor(TreePath path, Actor suggestion) {
    externalizeActor(path, true, suggestion);
  }

  /**
   * Opens a new FlowEditor window with the currently selected sub-flow.
   *
   * @param path		the (path to the) actor to externalize
   * @param checkCallActors	whether to check for callable actors
   * @param suggestion		the suggested actor to replace with
   */
  protected void externalizeActor(TreePath path, boolean checkCallActors, Actor suggestion) {
    Actor			currActor;
    Node 			currNode;
    ExternalActorHandler 	extActor;
    FlowEditorDialog		dialog;
    int				retVal;

    if (suggestion == null) {
      GUIHelper.showErrorMessage(
        getOwner(),
	"No external actor suggested, cannot proceed in externalizing actor!");
      return;
    }

    if (!(suggestion instanceof ExternalActorHandler)) {
      GUIHelper.showErrorMessage(
        getOwner(),
	"Suggested external actor does not implement the following interface:\n"
	  + Utils.classToString(ExternalActorHandler.class));
      return;
    }

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

    extActor = (ExternalActorHandler) suggestion;
    if (ActorUtils.isStandalone(currActor) && !ActorUtils.isStandalone(suggestion))
      extActor = new ExternalStandalone();
    else if (ActorUtils.isSource(currActor) && !ActorUtils.isSource(suggestion))
      extActor = new ExternalSource();
    else if (ActorUtils.isTransformer(currActor) && !ActorUtils.isTransformer(suggestion))
      extActor = new ExternalTransformer();
    else if (ActorUtils.isSink(currActor) && !ActorUtils.isSink(suggestion))
      extActor = new ExternalSink();
    extActor.setActorFile(new FlowFile(dialog.getFlowEditorPanel().getCurrentFile()));

    getOwner().setModified(true);
    currNode.setActor((Actor) extActor);
    currNode.removeAllChildren();
    getOwner().nodeStructureChanged(currNode);
    getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), currNode, Type.MODIFY));
  }

  /**
   * Lets the user choose a boolean condition.
   *
   * @param defCond 	the default condition, can be null
   * @return		the boolean condition or null if dialog canceled
   */
  protected BooleanCondition chooseBooleanCondition(BooleanCondition defCond) {
    GenericObjectEditorDialog	dialog;

    if (defCond == null)
      defCond = new Expression();

    if (getOwner().getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getOwner().getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(getOwner().getParentFrame());
    dialog.setUISettingsPrefix(BooleanCondition.class);
    dialog.setTitle("Conditions");
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(BooleanCondition.class);
    dialog.setCurrent(defCond);
    if (!dialog.getUISettingsApplied())
      dialog.setLocationRelativeTo(GUIHelper.getParentComponent(getOwner()));
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return null;

    // create node
    return (BooleanCondition) dialog.getCurrent();
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
    ConditionalSubProcess	subprocess;
    ConditionalSequence 	sequence;
    ConditionalTrigger		trigger;
    BooleanCondition		cond;

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

    // no equivalent, but we can enclose it in a conditional subprocess
    if (noEquiv && (ActorUtils.isSource(currActor) || ActorUtils.isTransformer(currActor) || ActorUtils.isSink(currActor))) {
      // choose condition
      cond = chooseBooleanCondition(null);
      if (cond == null)
	return;

      if (ActorUtils.isSource(currActor)) {
	trigger = new ConditionalTrigger();
	trigger.setCondition(cond);
	encloseActor(new TreePath[]{path}, trigger);
      }

      if (ActorUtils.isTransformer(currActor)) {
	subprocess = new ConditionalSubProcess();
	subprocess.setCondition(cond);
	encloseActor(new TreePath[]{path}, subprocess);
      }

      if (ActorUtils.isSink(currActor)) {
	sequence = new ConditionalSequence();
	sequence.setCondition(cond);
	encloseActor(new TreePath[]{path}, sequence);
      }

      return;
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
      newActor = (Actor) condEquiv.getDeclaredConstructor().newInstance();
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
    cond = chooseBooleanCondition(null);
    if (cond == null)
      return;

    // create node
    ((BooleanConditionSupporter) newActor).setCondition(cond);
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
      getOwner().updateActorName(newNode, false);
    }
    if (expanded)
      getOwner().expand(newNode);

    // update tree
    getOwner().setModified(true);
    getOwner().nodeStructureChanged(parentNode);
    getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), parentNode, Type.MODIFY));
    getOwner().nodeStructureChanged(parentNode);
    getOwner().locateAndDisplay(newNode.getFullName(), true);
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
      newActor = (Actor) timedEquiv.getDeclaredConstructor().newInstance();
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
      getOwner().updateActorName(newNode, false);
    }
    if (expanded)
      getOwner().expand(newNode);

    // update tree
    getOwner().setModified(true);
    getOwner().nodeStructureChanged(parentNode);
    getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), parentNode, Type.MODIFY));
    getOwner().nodeStructureChanged(parentNode);
    getOwner().locateAndDisplay(newNode.getFullName(), true);
    getOwner().redraw();
  }

  /**
   * Turns the selected Setvariable standalines into a subflow with
   * EnterManyValues for prompting the user.
   *
   * @param paths	the (paths to the) actors to turn interactive
   */
  public void makeInteractive(TreePath[] paths) {
    Node[]	nodes;
    String	subflowName;
    Trigger 	subflow;
    String	defValue;
    Node	parent;
    Node	newNode;
    int		index;

    if (paths.length == 0)
      return;
    nodes  = TreeHelper.pathsToNodes(paths);
    parent = (Node) nodes[0].getParent();
    if (!(parent.getActor() instanceof MutableActorHandler))
      return;

    subflowName = GUIHelper.showInputDialog(getOwner(), "Please enter name of interactive sub-flow:", "prompt user");
    if (subflowName == null)
      return;

    // create interactive snippet
    subflow = new Trigger();
    subflow.setName(subflowName);
    {
      EnterManyValues many = new EnterManyValues();
      many.setStopFlowIfCanceled(true);
      many.setOutputType(OutputType.MAP);
      subflow.add(many);
      for (Node node: nodes) {
        if (!(node.getActor() instanceof SetVariable))
          return;
        SetVariable set = (SetVariable) node.getActor();
	DefaultValueDefinition value = new DefaultValueDefinition();
	value.setName(set.getVariableName().getValue());
	if (set.getName().startsWith(set.getDefaultName()))
	  value.setDisplay(set.getVariableName().getValue());
	else
	  value.setDisplay(set.getName());
	defValue = set.getVariableValue().stringValue();
	if (Utils.isInteger(defValue))
	  value.setType(PropertyType.INTEGER);
	else if (Utils.isLong(defValue))
	  value.setType(PropertyType.LONG);
	else if (Utils.isDouble(defValue))
	  value.setType(PropertyType.DOUBLE);
	else if (Utils.isBoolean(defValue))
	  value.setType(PropertyType.BOOLEAN);
	else
	  value.setType(PropertyType.STRING);
	value.setDefaultValue(defValue);
	many.addValue(value);
      }

      subflow.add(new MapToVariables());
    }

    // undo
    getOwner().addUndoPoint("Making interactive (" + paths.length + "actor" + (paths.length != 1 ? "s" : "") + ")");

    // insert subflow
    newNode = TreeHelper.buildTree(null, subflow, false);
    newNode.setOwner(nodes[0].getOwner());
    index = parent.getIndex(nodes[0]);
    parent.insert(newNode, index);

    // remove old variables
    for (Node node: nodes)
      parent.remove(node);

      SwingUtilities.invokeLater(() -> {
	getOwner().setModified(true);
	getOwner().nodeStructureChanged(parent);
	getOwner().expand(newNode);
      });
      SwingUtilities.invokeLater(() -> {
	getOwner().locateAndDisplay(newNode.getFullName(), true);
	getOwner().notifyActorChangeListeners(new ActorChangeEvent(getOwner(), newNode, Type.MODIFY));
	getOwner().redraw();
      });
  }

  /**
   * Displays the memory consumption of the selected actor, broken by class.
   *
   * @param path	the path of the actor
   * @param flow	the running flow
   */
  public void inspectMemoryDetails(TreePath path, Flow flow) {
    ActorPath 			actorPath;
    Actor			actor;
    Map<Class,Statistics>	stats;
    SpreadSheet			sheet;
    Row				row;
    Statistics			clsStats;
    SpreadSheetDialog 		dialog;
    int				i;
    String			from;
    String			to;

    if (!SizeOf.isSizeOfAgentAvailable()) {
      GUIHelper.showErrorMessage(m_Owner, "The SizeOf agent is not present, cannot inspect memory!");
      return;
    }

    actorPath = TreeHelper.treePathToActorPath(path);
    actor     = ActorUtils.locate(actorPath, flow, true, false);
    if (actor == null) {
      GUIHelper.showErrorMessage(m_Owner, "Failed to locate actor in flow:\n" + actorPath);
      return;
    }

    stats = SizeOf.sizeOfAgentPerClass(actor, new ActorFilter());
    sheet = new DefaultSpreadSheet();
    row   = sheet.getHeaderRow();
    row.addCell("CL").setContentAsString("Class");
    row.addCell("C").setContentAsString("Instances");
    row.addCell("T").setContentAsString("Size");
    for (Class cls: stats.keySet()) {
      clsStats = stats.get(cls);
      row      = sheet.addRow();
      row.addCell("CL").setContentAsString(cls.getName());
      row.addCell("C").setContent(clsStats.count);
      row.addCell("T").setContent(clsStats.total);
    }

    // sort desc by size
    sheet.sort(2, false);

    // add row with sums
    row = sheet.addRow();
    row.getCell(0).setContentAsString("Sum");
    for (i = 1; i <= 2; i++) {
      from = SpreadSheetUtils.getCellPosition(0, i);
      to   = SpreadSheetUtils.getCellPosition(sheet.getRowCount() - 2, i);
      row.addCell(i).setContent("=SUM(" + from +  ":" + to + ")");
    }

    if (getOwner().getParentDialog() != null)
      dialog = new SpreadSheetDialog(getOwner().getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new SpreadSheetDialog(getOwner().getParentFrame(), false);
    dialog.setTitle("Inspect memory: " + actorPath);
    dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
    dialog.setSpreadSheet(sheet);
    dialog.setShowSearch(true);
    dialog.setSize(GUIHelper.makeWider(GUIHelper.rotate(GUIHelper.getDefaultDialogDimension())));
    dialog.setLocationRelativeTo(getOwner());
    dialog.setVisible(true);
  }

  /**
   * Displays the memory consumption of the selected subtree (just the size).
   *
   * @param path	the path of the actor
   * @param flow	the running flow
   */
  public void inspectMemorySize(TreePath path, Flow flow) {
    ActorPath 			actorPath;
    Actor			actor;
    long			size;
    String			sizeStr;
    String			msg;

    if (!SizeOf.isSizeOfAgentAvailable()) {
      GUIHelper.showErrorMessage(m_Owner, "The SizeOf agent is not present, cannot inspect memory!");
      return;
    }

    actorPath = TreeHelper.treePathToActorPath(path);
    actor     = ActorUtils.locate(actorPath, flow, true, false);
    if (actor == null) {
      GUIHelper.showErrorMessage(m_Owner, "Failed to locate actor in flow:\n" + actorPath);
      return;
    }

    size    = SizeOf.sizeOfAgent(actor, new ActorFilter());
    sizeStr = ByteFormat.toBestFitBytes(size, 3);
    msg     = "Sub-tree size of " + TreeHelper.treePathToActorPath(path) + ": " + sizeStr;
    if (getOwner().getParentDialog() != null)
      GUIHelper.showInformationMessage(getOwner().getParentDialog(), msg);
    else
      GUIHelper.showInformationMessage(getOwner().getParentFrame(), msg);
  }

  /**
   * Returns the actor stored on the clipboard.
   *
   * @return		the actor or null if none available
   */
  public static Actor getActorFromClipboard() {
    Actor 		result;
    DefaultFlowReader 	freader;
    StringReader 	sreader;
    String 		pasted;
    Node[]		nodes;
    int			numLines;
    boolean		hasInput;
    boolean		hasOutput;

    result = null;

    if (hasNodesOnClipboard()) {
      nodes = getNodesFromClipboard();
      if (nodes.length == 1) {
        result = nodes[0].getFullActor();
      }
      else {
        hasInput  = (nodes[0].getActor() instanceof InputConsumer);
        hasOutput = (nodes[nodes.length - 1].getActor() instanceof OutputProducer);
        if (hasInput && hasOutput) {
	  SubProcess sub = new SubProcess();
	  for (Node node: nodes)
	    sub.add(node.getActor());
	  result = sub;
	}
	else if (hasInput) {
          Tee sub = new Tee();
	  for (Node node: nodes)
	    sub.add(node.getActor());
	  result = sub;
	}
	else {
          Trigger sub = new Trigger();
	  for (Node node: nodes)
	    sub.add(node.getActor());
	  result = sub;
	}
	result.setName("pasted from clipboard");
      }
    }
    else {
      pasted = null;
      numLines = 0;
      if (ClipboardHelper.canPasteStringFromClipboard()) {
	pasted = OptionUtils.pasteSetupFromClipboard();
	if (pasted != null)
	  numLines = Utils.split(pasted.trim(), "\n").length;
      }

      if (pasted != null) {
	// try commandline
	if (numLines == 1) {
	  try {
	    result = (Actor) OptionUtils.forAnyCommandLine(Actor.class, pasted);
	  }
	  catch (Exception e) {
	    result = null;
	  }
	}

	if (result == null) {
	  try {
	    sreader = new StringReader(pasted);
	    freader = new DefaultFlowReader();
	    freader.setQuiet(true);
	    result = freader.readActor(sreader);
	  }
	  catch (Exception ex) {
	    result = null;
	  }
	}
      }
    }

    return result;
  }

  /**
   * Checks whether any nodes in nested listed form are on the clipboard.
   *
   * @return		true if nodes available
   */
  public static boolean hasNodesOnClipboard() {
    return (getListsFromClipboard() != null);
  }

  /**
   * Returns the nested lists stored on the clipboard.
   *
   * @return		the nodes or null if none available
   */
  public static List[] getListsFromClipboard() {
    List[]	result;

    result = null;

    try {
      if (ClipboardHelper.canPasteFromClipboard(TransferableNestedList.Flavour))
        result = (List[]) ClipboardHelper.pasteFromClipboard(TransferableNestedList.Flavour);
    }
    catch (Exception ex) {
      result = null;
    }

    return result;
  }

  /**
   * Returns the nodes stored on the clipboard.
   * Note: use {@link #hasNodesOnClipboard()} if you just want a fast check
   * whether there is anything on the clipboard.
   *
   * @return		the nodes or null if none available
   * @see		#hasNodesOnClipboard()
   * @see		#getListsFromClipboard()
   */
  public static Node[] getNodesFromClipboard() {
    Node[]	result;
    List[]	lists;
    int		i;

    result = null;
    lists  = getListsFromClipboard();

    if (lists != null) {
      result = new Node[lists.length];
      for (i = 0; i < lists.length; i++)
        result[i] = TreeHelper.buildTree(lists[i]);
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
