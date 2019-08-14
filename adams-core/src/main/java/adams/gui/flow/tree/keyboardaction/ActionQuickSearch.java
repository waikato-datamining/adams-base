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
 * ActionQuickSearch.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseShortcut;
import adams.gui.core.ClassQuickSearchPanel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.flow.menu.AbstractFlowEditorMenuItem;
import adams.gui.flow.menu.FlowEditorAction;
import adams.gui.flow.tree.StateContainer;
import adams.gui.flow.tree.menu.TreePopupAction;
import com.github.fracpete.javautils.enumerate.Enumerated;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Allows the user to search for actions in menu items for editor and tree popup.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActionQuickSearch
  extends AbstractKeyboardAction {

  private static final long serialVersionUID = 8171584749198453214L;

  /** all instantiated classes. */
  protected transient List<Object> m_AllActions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to search for actions in menu items for editor and tree popup.";
  }

  /**
   * Returns the default shortcut of the action.
   *
   * @return 		the default
   */
  @Override
  protected BaseShortcut getDefaultShortcut() {
    return new BaseShortcut("ctrl SPACE");
  }

  /**
   * Updates the state of the action.
   *
   * @param state	the state to use
   * @param obj		the action to update
   * @return		true if successfully updated
   */
  protected boolean updateAction(StateContainer state, Object obj) {
    if (ClassLocator.matches(TreePopupAction.class, obj.getClass())) {
      TreePopupAction action = (TreePopupAction) obj;
      action.update(state);
      return true;
    }

    if (ClassLocator.matches(FlowEditorAction.class, obj.getClass())) {
      FlowEditorAction action = (FlowEditorAction) obj;
      action.update(state.tree.getEditor());
      return true;
    }

    if (ClassLocator.matches(AbstractFlowEditorMenuItem.class, obj.getClass())) {
      AbstractFlowEditorMenuItem menuitem = (AbstractFlowEditorMenuItem) obj;
      menuitem.setOwner(state.tree.getOwner().getEditor());
      menuitem.updateAction();
      return true;
    }

    return false;
  }

  /**
   * Checks whether the action is enabled.
   *
   * @param obj		the action to check
   * @return		true if enabled
   */
  protected boolean isActionEnabled(Object obj) {
    if (ClassLocator.matches(TreePopupAction.class, obj.getClass())) {
      TreePopupAction action = (TreePopupAction) obj;
      return action.isEnabled();
    }

    if (ClassLocator.matches(FlowEditorAction.class, obj.getClass())) {
      FlowEditorAction action = (FlowEditorAction) obj;
      return action.isEnabled();
    }

    if (ClassLocator.matches(AbstractFlowEditorMenuItem.class, obj.getClass())) {
      AbstractFlowEditorMenuItem menuitem = (AbstractFlowEditorMenuItem) obj;
      return menuitem.getAction().isEnabled();
    }

    return false;
  }

  /**
   * Determines the action objects that can be used within this context.
   *
   * @return		the objects
   */
  protected Object[] determineActions(StateContainer state) {
    List<Class> 	allClasses;
    List<Object>	result;

    result = new ArrayList<>();

    // instantiate actions once
    if (m_AllActions == null) {
      allClasses = new ArrayList<>();
      allClasses.addAll(Arrays.asList(ClassLister.getSingleton().getClasses(TreePopupAction.class)));
      allClasses.addAll(Arrays.asList(ClassLister.getSingleton().getClasses(FlowEditorAction.class)));
      allClasses.addAll(Arrays.asList(ClassLister.getSingleton().getClasses(AbstractFlowEditorMenuItem.class)));

      m_AllActions = new ArrayList<>();
      for (Class cls: allClasses) {
	try {
	  m_AllActions.add(cls.newInstance());
	}
	catch (Exception e) {
	  ConsolePanel.getSingleton().append("Failed to instantiate action class " + Utils.classToString(cls), e);
	}
      }
    }

    // update state
    for (Object obj: m_AllActions)
      updateAction(state, obj);

    // collect only actions that are enabled
    for (Object obj: m_AllActions) {
      if (isActionEnabled(obj))
        result.add(obj);
    }

    Collections.sort(result, new Comparator<Object>() {
      @Override
      public int compare(Object o1, Object o2) {
	return o1.getClass().getName().compareTo(o2.getClass().getName());
      }
    });

    return result.toArray(new Object[0]);
  }

  /**
   * Performs the actual execution of the action.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doExecute(StateContainer state) {
    final BaseDialog		dialog;
    final ClassQuickSearchPanel	panel;
    final Object[]		actions;
    final Class[]		classes;

    if (state.tree.getParentDialog() != null)
      dialog = new BaseDialog(state.tree.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new BaseDialog(state.tree.getParentFrame(), true);

    actions = determineActions(state);
    classes = new Class[actions.length];
    for (Enumerated<Object> action: enumerate(actions))
      classes[action.index] = action.value.getClass();
    panel = new ClassQuickSearchPanel();
    panel.setClasses(classes);
    panel.addCancelListener((ChangeEvent e) -> dialog.setVisible(false));
    panel.addSelectionListener((ListSelectionEvent e) -> {
      int index = panel.getSelectedItemIndex();
      if (index == -1)
        return;

      Object obj = actions[index];
      if (ClassLocator.matches(TreePopupAction.class, obj.getClass())) {
	TreePopupAction action = (TreePopupAction) obj;
	dialog.setVisible(false);
	action.actionPerformed(null);
      }
      else if (ClassLocator.matches(FlowEditorAction.class, obj.getClass())) {
	FlowEditorAction action = (FlowEditorAction) obj;
	dialog.setVisible(false);
	action.actionPerformed(null);
      }
      else if (ClassLocator.matches(AbstractFlowEditorMenuItem.class, obj.getClass())) {
	AbstractFlowEditorMenuItem menuitem = (AbstractFlowEditorMenuItem) obj;
	AbstractBaseAction action = menuitem.getAction();
	dialog.setVisible(false);
	action.actionPerformed(null);
      }
      else {
	GUIHelper.showErrorMessage(
	  state.tree.getParent(),
	  "Unhandled quick action class: " + Utils.classToString(obj));
      }
    });

    dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Action quick search");
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultSmallDialogDimension());
    dialog.setLocationRelativeTo(state.tree.getParent());
    dialog.setVisible(true);
    return null;
  }
}
