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
 * AbstractQuickSearchActor.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.flow.core.Actor;
import adams.gui.core.BaseDialog;
import adams.gui.core.ClassQuickSearchPanel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.dotnotationtree.AbstractItemFilter;
import adams.gui.flow.tree.StateContainer;
import adams.gui.flow.tree.TreeOperations.ActorDialog;
import adams.gui.flow.tree.TreeOperations.InsertPosition;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;

/**
 * Ancestor for actions that add an actor via a quick search list.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractQuickSearchActor
  extends AbstractKeyboardAction {

  private static final long serialVersionUID = 9158512844896786075L;

  /** the panel to use for searching. */
  protected transient ClassQuickSearchPanel m_SearchPanel;

  /** the selection listener. */
  protected transient ListSelectionListener m_SelectionListener;

  /**
   * Checks whether the current state is suitable.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String check(StateContainer state) {
    String	result;

    result = super.check(state);

    if (result == null) {
      if (!state.editable)
	result = "Flow not editable";
      else if (!state.isSingleSel)
	result = "Not a single actor selected";
    }

    return result;
  }

  /**
   * Inserts the actor
   *
   * @param state	the current state
   * @param actor	the actor to insert
   * @param position	the position to insert the actor at
   */
  protected void addActor(StateContainer state, Actor actor, InsertPosition position) {
    if (actor == null)
      return;
    state.tree.getOperations().addActor(state.selPath, actor, position, true, ActorDialog.GOE_FORCED_NO_TREE);
  }

  /**
   * Allows the user to search for an actor to insert at the specified position.
   *
   * @param state	the current state
   * @param position	the insert position
   * @return		the chosen actor, null if cancelled
   */
  protected Actor search(final StateContainer state, final InsertPosition position) {
    Actor			result;
    AbstractItemFilter  	filter;
    final BaseDialog		dialog;
    Class[]			classes;

    result  = null;
    filter  = state.tree.getOperations().configureFilter(state.selPath, position);
    classes = ClassLister.getSingleton().getClasses(Actor.class);

    if (state.tree.getParentDialog() != null)
      dialog = new BaseDialog(state.tree.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new BaseDialog(state.tree.getParentFrame(), true);
    dialog.setTitle("Add actor (" + position.toString().toLowerCase() + ")");
    dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);

    if (m_SearchPanel == null)
      m_SearchPanel = new ClassQuickSearchPanel();
    if (m_SelectionListener != null)
      m_SearchPanel.removeSelectionListener(m_SelectionListener);
    m_SelectionListener = (ListSelectionEvent e) -> {
      Class cls = m_SearchPanel.getSelectedClass();
      dialog.setVisible(false);
      try {
	Actor actor = (Actor) cls.newInstance();
	addActor(state, actor, position);
      }
      catch (Exception ex) {
	ConsolePanel.getSingleton().append("Failed to instantiate actor: " + Utils.classToString(cls), ex);
	return;
      }
    };
    m_SearchPanel.setItemFilter(filter);
    m_SearchPanel.setClasses(classes);
    m_SearchPanel.addSelectionListener(m_SelectionListener);

    dialog.getContentPane().add(m_SearchPanel, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultSmallDialogDimension());
    dialog.setLocationRelativeTo(state.tree.getParent());
    dialog.setVisible(true);

    return result;
  }
}
