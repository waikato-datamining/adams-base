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
 * ChangeCallableActor.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.MessageCollection;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.PropertyPath;
import adams.core.option.UserMode;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.UserModeUtils;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.flow.tree.Node;
import adams.gui.goe.FlowHelper;
import adams.gui.goe.actorpathtree.ActorPathNode;
import adams.gui.goe.callableactorstree.CallableActorsTree;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Changes the callable actor that is being referenced.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeCallableActor
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  public static final String CALLABLE_NAME = "callableName";

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Change callable actor...";
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    UserMode	userMode;
    boolean	hasMethod;

    userMode  = UserModeUtils.getUserMode(m_State.tree);
    hasMethod = IntrospectionHelper.hasProperty(
      m_State.selNode.getActor().getClass(), CALLABLE_NAME, CallableActorReference.class, userMode);
    setEnabled(m_State.editable && m_State.isSingleSel && hasMethod);
  }

  /**
   * Checks whether the select path is a valid callable actor reference.
   *
   * @param path	the path to check
   * @return		true if valid
   */
  protected boolean isValid(TreePath path) {
    boolean		result;
    BaseTreeNode node;
    ActorPathNode callable;

    result = false;

    if (path != null) {
      node = (BaseTreeNode) path.getLastPathComponent();
      if (node instanceof ActorPathNode) {
	callable = (ActorPathNode) node;
	if (callable.hasClassname())
	  result = true;
      }
    }

    return result;
  }

  /**
   * Locates all the callable actors for the node.
   *
   * @return		the located callable actors (including CallableActors nodes)
   */
  protected List<String> findCallableActors() {
    List<String>	result;
    List<Node>		callables;
    int			i;
    Node		child;

    result = new ArrayList<>();

    callables = FlowHelper.findCallableActorsHandler(m_State.selNode);
    for (Node callable: callables) {
      result.add(callable.getFullName());
      for (i = 0; i < callable.getChildCount(); i++) {
	child = (Node) callable.getChildAt(i);
	if (child.getActor().getSkip())
	  continue;
	result.add(child.getFullName());
      }
    }

    return result;
  }

  /**
   * Prompts the user to select callable actor.
   *
   * @param refOld 	the old callable actor
   * @return 		the selected actor, null if none selected
   */
  protected CallableActorReference selectCallableActor(CallableActorReference refOld) {
    final ApprovalDialog	dialog;
    CallableActorsTree 		tree;
    JPanel 			panelTree;
    BaseTreeNode		node;
    ActorPathNode		callable;

    dialog = ApprovalDialog.getDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    dialog.setDiscardVisible(false);

    panelTree = new JPanel(new BorderLayout(0, 5));
    panelTree.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
    tree = new CallableActorsTree();
    tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
	isValid(e.getPath());
      }
    });
    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
	final TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
	if (MouseUtils.isDoubleClick(e)) {
	  if (isValid(selPath)) {
	    e.consume();
	    dialog.approveDialog();
	  }
	}
	if (!e.isConsumed())
	  super.mousePressed(e);
      }
    });
    panelTree.add(new BaseScrollPane(tree), BorderLayout.CENTER);
    panelTree.add(new JLabel("Select callable actor:"), BorderLayout.NORTH);
    panelTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    tree.setFlowTree(m_State.tree);
    tree.setItems(findCallableActors());
    tree.expandAll();
    tree.selectNodeByName(refOld.getValue());

    dialog.getContentPane().add(panelTree, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultTinyDialogDimension());
    dialog.setLocationRelativeTo(getParentDialog());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return null;

    if (tree.getSelectionPath() == null)
      return null;

    node = (BaseTreeNode) tree.getSelectionPath().getLastPathComponent();
    if (node instanceof ActorPathNode) {
      callable = (ActorPathNode) node;
      if (callable.hasClassname())
	return new CallableActorReference(callable.getLabel());
    }

    return null;
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Component			parent;
    Actor			actorOld;
    Actor 			actorNew;
    CallableActorReference 	refOld;
    CallableActorReference 	refNew;
    MessageCollection		errors;
    boolean			updated;

    parent = GUIHelper.getParentComponent(m_State.tree);

    // get old ref
    actorOld = m_State.selNode.getActor();
    errors   = new MessageCollection();
    refOld = (CallableActorReference) PropertyPath.getValue(actorOld, CALLABLE_NAME, errors);
    if (refOld == null)
      refOld = new CallableActorReference();

    // select new actor
    refNew = selectCallableActor(refOld);
    if (refNew == null)
      return;
    if (refNew.getValue().equals(refOld.getValue()))
      return;

    // update actor
    actorNew = actorOld.shallowCopy();
    updated  = PropertyPath.setValue(actorNew, CALLABLE_NAME, refNew, errors);
    if (!updated) {
      GUIHelper.showErrorMessage(parent, "Failed to update callable actor reference!");
      return;
    }
    addUndoPoint("Changed callable actor to: " + refNew);
    updateSelectedActor(actorNew);
  }
}
