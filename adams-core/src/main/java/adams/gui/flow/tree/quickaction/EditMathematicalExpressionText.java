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
 * EditMathematicalExpressionText.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.AdditionalInformationHandler;
import adams.core.MessageCollection;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.PropertyPath;
import adams.core.option.UserMode;
import adams.flow.core.Actor;
import adams.gui.core.AbstractTextEditorPanelWithSimpleSyntaxHighlighting;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.UserModeUtils;
import adams.gui.dialog.ApprovalDialog;
import adams.parser.MathematicalExpressionText;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Lets the user edit a MathematicalExpressionText.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class EditMathematicalExpressionText
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  public static final String EXPRESSION = "expression";

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Edit mathematical expression...";
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    UserMode userMode;
    boolean	hasMethod;

    userMode  = UserModeUtils.getUserMode(m_State.tree);
    hasMethod = IntrospectionHelper.hasProperty(
      m_State.selNode.getActor().getClass(), EXPRESSION, MathematicalExpressionText.class, userMode);
    setEnabled(m_State.editable && m_State.isSingleSel && hasMethod);
  }

  /**
   * Lets the user enter a new expression.
   *
   * @param expOld	the old expression
   * @return		the new expression
   */
  protected MathematicalExpressionText enterNewExpression(MathematicalExpressionText expOld) {
    MathematicalExpressionText				result;
    ApprovalDialog					dialog;
    AbstractTextEditorPanelWithSimpleSyntaxHighlighting	panel;
    JPanel						panelTabbedPane;
    BaseTabbedPane					tabbedPane;
    BaseTextArea					textHelp;
    JPanel						panelHelp;

    panel = expOld.getTextEditorPanel();
    panel.setContent(expOld.getValue());

    panelTabbedPane = new JPanel(new BorderLayout());
    panelTabbedPane.setPreferredSize(GUIHelper.getDefaultSmallDialogDimension());
    tabbedPane = new BaseTabbedPane(BaseTabbedPane.TOP);
    tabbedPane.addTab("Expression", panel);
    panelTabbedPane.add(tabbedPane, BorderLayout.CENTER);
    textHelp   = new BaseTextArea(((AdditionalInformationHandler) panel).getAdditionalInformation());
    textHelp.setLineWrap(true);
    textHelp.setWrapStyleWord(true);
    textHelp.setFont(Fonts.getMonospacedFont());
    textHelp.setEditable(false);
    panelHelp  = new JPanel(new BorderLayout());
    panelHelp.add(new BaseScrollPane(textHelp), BorderLayout.CENTER);
    panelHelp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    tabbedPane.addTab("Help", panelHelp);

    if (getParentDialog() != null)
      dialog = ApprovalDialog.getDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = ApprovalDialog.getDialog(getParentFrame(), true);
    dialog.setTitle("Enter mathematical expression");
    dialog.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultSmallDialogDimension());
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return expOld;

    result = new MathematicalExpressionText(panel.getContent());
    return result;
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
    MathematicalExpressionText expOld;
    MathematicalExpressionText exprNew;
    MessageCollection		errors;
    boolean			updated;

    parent = GUIHelper.getParentComponent(m_State.tree);

    // get old value
    actorOld = m_State.selNode.getActor();
    errors   = new MessageCollection();
    expOld = (MathematicalExpressionText) PropertyPath.getValue(actorOld, EXPRESSION, errors);
    if (expOld == null)
      expOld = new MathematicalExpressionText();

    // enter new expression
    exprNew = enterNewExpression(expOld);
    if (exprNew == null)
      return;
    if (exprNew.getValue().equals(expOld.getValue()))
      return;

    // update actor
    actorNew = actorOld.shallowCopy();
    updated  = PropertyPath.setValue(actorNew, EXPRESSION, exprNew, errors);
    if (!updated) {
      GUIHelper.showErrorMessage(parent, "Failed to update mathematical expression!");
      return;
    }
    addUndoPoint("Changed mathematical expression");
    updateSelectedActor(actorNew);
  }
}
